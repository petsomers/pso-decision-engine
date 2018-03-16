package pso.decision_engine.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

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
		if (!appConfig.isEnableBatcProcessing()) return;
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
		private	String endPoint;
		private String ruleSetId;
		private String[] inputParameters;
		private String[] outputParameters;
	}
	
	public void process() {
		// watch queue, and process
		while (true) {
			Path p=null;
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
				Path outputPath=Paths.get(p.getParent().toString(),"in_progress",outputFileName);
				long startnano=System.nanoTime();
				int[] count= {0};
				try (BufferedWriter out=Files.newBufferedWriter(outputPath)) {
					Files.lines(newPath, Charset.forName("UTF-8"))
					.parallel()
					.map(line -> processLine(line))
					.forEachOrdered(result -> {
						try {
							count[0]++;
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
				logger.info("BATCH File: "+p+"DONE. "+count[0]+" lines in "+ms+"ms");
				System.out.println("BATCH File: "+p+" DONE IN "+ms+"ms");
				
			} catch (Exception e) {
				logger.error("BATCH File: "+p+" ERROR: "+e.getMessage(),e);
			}
		}
	}
	
	public String processLine(String s) {
		StringBuilder r=new StringBuilder();
		int i=s.indexOf('?');
		if (i<=0) {
			return "No endPoint.";
		}
		String endPoint=s.substring(0, i);
		String ruleSetId=setupService.getActiveRuleSetId(endPoint);
		RuleSet ruleSet=setupService.getRuleSet(endPoint, ruleSetId, false, false);
		String parametersStr=s.substring(i+1);
		HashMap<String, String> parameters=new HashMap<>();
		for (String parameterSection:parametersStr.split("&")) {
			parameterSection=parameterSection.trim();
			if (parameterSection.isEmpty()) continue;
			int e=parameterSection.indexOf('=');
			if (e<0) {
				parameters.put(parameterSection, "");
			} else {
				parameters.put(parameterSection.substring(0, e),parameterSection.substring(e+1));
			}
			
		}
		r.append(s);
		r.append(" => ");
		
		DecisionResult result=ruleSetProcessorService.runRuleSetWithParameters(ruleSet, parameters);
		r.append(result.getDecision());
		return r.toString();
	}
	
	
}
