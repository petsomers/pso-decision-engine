package pso.decision_engine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import pso.decision_engine.presentation.SecurityFilter;
import pso.decision_engine.service.JwtService;

@Configuration
public class SpringConfig {
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private JwtService jwtService;

	@Bean
	public FilterRegistrationBean securityFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new SecurityFilter(jwtService));
		registration.addUrlPatterns("/*");
		registration.setName("securityFilter");
		registration.addInitParameter("adminUser", appConfig.getAdminUserId());
		registration.addInitParameter("adminPassword", appConfig.getAdminPassword());
		registration.addInitParameter("processorUserId", appConfig.getProcessorUserId());
		registration.addInitParameter("processorPassword", appConfig.getProcessorPassword());
		registration.addInitParameter("disableSecurity", appConfig.isDisableSecurity()?"Y":"N");
		registration.setOrder(1);
		return registration;
	}

	@Bean
	public FreeMarkerConfigurer freeMarkerConfigurer() {
		FreeMarkerConfigurer cfg = new FreeMarkerConfigurer();
		cfg.setTemplateLoaderPaths("classpath:/static/engine-interface/");
		cfg.setDefaultEncoding("UTF-8");
		return cfg;
	}

}
