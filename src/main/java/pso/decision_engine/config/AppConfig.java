package pso.decision_engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties
@Data
public class AppConfig {
	private String dataDirectory;
	private int maxInMemoryListSize;
	
	private boolean createTables;
	
	private String adminUserId;
	private String adminPassword;
	
	private String processorUserId;
	private String processorPassword;

}