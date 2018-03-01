package pso.decision_engine.model;

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
}