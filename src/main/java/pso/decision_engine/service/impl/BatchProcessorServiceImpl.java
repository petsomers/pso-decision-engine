package pso.decision_engine.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;
import pso.decision_engine.config.AppConfig;
import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.service.RuleSetProcessorService;
import pso.decision_engine.service.SetupApiService;

@Service
public class BatchProcessorServiceImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	@Autowired
	AppConfig appConfig;
	
	@Autowired
	private SetupApiService setupService;
	
	@Autowired
	private RuleSetProcessorService ruleSetProcessorService;
	
	final LinkedBlockingQueue<Path> filesToProcess=new LinkedBlockingQueue<>();
	
	@PostConstruct
	public void init() {
		if (!appConfig.isEnableBatchProcessing()) return;
		File f=new File(appConfig.getBatchInputDirectory());
		if (!f.exists() || !f.isDirectory()) {
			throw new RuntimeException("BatchProcessorService Error: this is not a directory: "+appConfig.getBatchInputDirectory());
		}
		Paths.get(appConfig.getBatchInputDirectory(),"in_progress").toFile().mkdirs();
		Paths.get(appConfig.getBatchInputDirectory(),"done").toFile().mkdirs();
		new Thread(() ->  watch()).start();
		new Thread(() ->  process()).start();
	}
	
	public void watch() {
		// watch directory and put on queue
		try {
			Thread.sleep(5000); // wait 10 seconds initially
			WatchService watchService = FileSystems.getDefault().newWatchService();
			final Path path = Paths.get(appConfig.getBatchInputDirectory());
			
			// first add existing files to the quey
			Files.list(path)
			.filter(Files::isRegularFile)
			.forEach(file -> {
				try {
					filesToProcess.put(file.toAbsolutePath());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			
			path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			WatchKey key;
	        while ((key = watchService.take()) != null) {
	            for (WatchEvent<?> event : key.pollEvents()) {
	                System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + "   "+event.context().getClass().getName());
	                if (event.kind()==StandardWatchEventKinds.ENTRY_CREATE) {
	                	filesToProcess.put(path.resolve((Path)event.context()));	
	                }
	            }
	            key.reset();
	        }
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Data
	private static class BatchFileSettings {
		// first line of file: resEndPoint<tab>inputParameters,<tab>outputParameters,
		private String[] inputParameters;
		private String[] outputParameters;
		private boolean headerOk;
		private RuleSet ruleSet;
		private String headerError;
	}
	
	public void process() {
		// watch queue, and process
		PROCESS_LOOP:while (true) {
			Path p=null;
			Path outputPath=null;
			int[] lineNumber= {0};
			try {
				p=filesToProcess.take();
				System.out.println("BATCH File: "+p);
				File file=p.toFile();
				if (!file.exists()) {
					System.out.println(" -> file does not exist anymore. Skipping.");
					continue;
				}
				logger.info("BATCH File: "+p);
				System.out.println("Moving file to in_progress.");
				Path newPath=Paths.get(p.getParent().toString(),"in_progress",p.getFileName().toString());
				
				String outputFileName=p.getFileName().toString();
				int pointLoc=outputFileName.lastIndexOf(".");
				if (pointLoc>1) {
					outputFileName=outputFileName.substring(0, pointLoc)+"_output"+outputFileName.substring(pointLoc);
				} else {
					outputFileName+="_output";
				}
				Files.move(p, newPath);
				
				System.out.println("Start Processing.");
				outputPath=Paths.get(p.getParent().toString(),"in_progress",outputFileName);
				long startnano=System.nanoTime();
				lineNumber[0]= 0;
				try (BufferedWriter out=Files.newBufferedWriter(outputPath)) {

					final BatchFileSettings bfs=processHeaderLine(newPath);
					if (!bfs.isHeaderOk()) {
						out.write("ERROR "+bfs.getHeaderError());
						logger.error("BATCH File: "+p+" error parsing header: "+bfs.getHeaderError());
						continue PROCESS_LOOP;
					}
					Files.lines(newPath, Charset.forName("UTF-8"))
					.skip(1) // skip header row
					.parallel() // multi-core
					.map(line -> processLine(bfs, line)) // process
					.forEachOrdered(result -> { // output
						try {
							bfs.getClass();
							lineNumber[0]++;
							out.write(result);
							out.write("\r\n");
						} catch (IOException e) {
							e.printStackTrace();
							throw new RuntimeException();
						}
					});
					out.flush();
				}
				long stopnano=System.nanoTime();
				double ms=(stopnano - startnano)/1000000d;
				logger.info("BATCH File: "+p+"DONE. "+lineNumber[0]+" lines in "+ms+"ms");
				System.out.println("BATCH File: "+p+" DONE IN "+ms+"ms");
				
			} catch (Exception e) {
				String error="ERROR on line "+lineNumber[0]+": "+e.getMessage();
				logger.error("BATCH File: "+p+" "+error,e);
				try {
					Files.write(outputPath, error.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
				} catch (IOException e1) {e1.printStackTrace();}
			}
		}
	}
	
	private BatchFileSettings processHeaderLine(Path p) throws IOException {
		BatchFileSettings r=new BatchFileSettings();
		String headerLine=null;
		try (BufferedReader bfr=Files.newBufferedReader(p)) {
			headerLine=bfr.readLine();
		}
		if (headerLine==null) {
			r.setHeaderError("Empty file.");
			return r;
		}
		String[] h=headerLine.split("\t", 3);
		if (h.length<3) {
			r.setHeaderError("Invalid header. Format: restEndpoint<tab>inputParameters,<tab>RESULT,outputParameters,");
			return r;
		}
		String restEndpoint=h[0];
		String inputParameters=h[1];
		String outputParameters=h[2];
		String ruleSetId=setupService.getActiveRuleSetId(restEndpoint);
		if (ruleSetId==null) {
			r.setHeaderError("Invalid header. Ruleset not found for restEndpoint "+restEndpoint);
			return r;
		}
		RuleSet ruleSet=setupService.getRuleSet(restEndpoint, ruleSetId, false, false);
		if (ruleSet==null) {
			r.setHeaderError("Invalid header. Ruleset not found for restEndpoint "+restEndpoint);
			return r;
		}
		
		
		r.setRuleSet(ruleSet);
		
		Function<String, String[]> toArray=
			(ip) -> Arrays.stream(ip.split(",",100))
			.map(s -> s.trim())
			.filter(s -> !s.isEmpty())
			.toArray(String[]::new);
		
		r.setInputParameters(toArray.apply(inputParameters));
		r.setOutputParameters(toArray.apply(outputParameters));
		
		for (String ih:r.getInputParameters()) {
			if (ruleSet.getInputParameters().get(ih)==null) {
				r.setHeaderError("Invalid header: this is not a valid input parameter: "+ih);
				return r;
			}
		}
		boolean hasResultHeader=false;
		for (String oh:r.getOutputParameters()) {
			if (ruleSet.getInputParameters().get(oh)==null) {
				if (oh.equals("RESULT")) {
					hasResultHeader=true;
				} else {
					r.setHeaderError("Invalid header: this is not a valid output parameter: "+oh);
					return r;
				}
			}
		}
		if(!hasResultHeader) {
			r.setHeaderError("Invalid header: the output parameter list should contain a RESULT parameter");
			return r;
		}
		r.setHeaderOk(true);
		return r;
	}
	
	public String processLine(BatchFileSettings bfs, String s) {
		String values[]=s.split(";", bfs.getInputParameters().length);
		
		HashMap<String, String> parameters=new HashMap<>();
		int i=0;
		for (String parameterName:bfs.getInputParameters()) {
			if (i<values.length) {
				parameters.put(parameterName, values[i++].trim());	
			}
		}
		DecisionResult result=ruleSetProcessorService.runRuleSetWithParameters(bfs.getRuleSet(), parameters);
		String[] output=new String[bfs.getOutputParameters().length];
		i=0;
		for (String parameterName:bfs.getOutputParameters()) {
			if ("RESULT".equals(parameterName)) {
				output[i++]=result.getDecision();
			} else {
				output[i++]=parameters.get(parameterName);
			}
		}
		return String.join("\t", output);
	}
	
	
}
