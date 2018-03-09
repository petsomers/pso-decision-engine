package pso.decision_engine;

import java.util.Properties;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DecisionEngineApplication extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(DecisionEngineApplication.class)
		.sources(DecisionEngineApplication.class)
		.properties(getProperties())
		.run();
		// SpringApplication.run(DecisionEngineApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder
		.sources(DecisionEngineApplication.class)
		.properties(getProperties());
    }

	static Properties getProperties() {
		Properties props = new Properties();
		props.put("spring.config.location",	"file:appconfig/pso-decision-engine/,classpath:appconfig/pso-decision-engine/");
		return props;
	}
}
