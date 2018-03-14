package pso.decision_engine.service.impl;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pso.decision_engine.config.AppConfig;

@Service
public class BatchProcessorServiceImpl {

	@Autowired
	AppConfig appConfig;
	
	final LinkedBlockingQueue<String> filesToProcess=new LinkedBlockingQueue<>();
	
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
			Path path = Paths.get(appConfig.getBatchInputDirectory());
			
			// first add existing files to the quey
			Files.list(path)
			.filter(Files::isRegularFile)
			.forEach(file -> {
				try {
					filesToProcess.put(file.toAbsolutePath().toString());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			
			path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			WatchKey key;
	        while ((key = watchService.take()) != null) {
	            for (WatchEvent<?> event : key.pollEvents()) {
	                System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + "   "+event.context().getClass().getName());
	                filesToProcess.put(event.context().toString());
	            }
	            key.reset();
	        }
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void process() {
		// watch queue, and process
		while (true) {
			try {
				String p=filesToProcess.take();
				System.out.println("PROCESSING "+p);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
