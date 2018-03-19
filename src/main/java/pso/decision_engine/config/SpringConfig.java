package pso.decision_engine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
		registration.setOrder(1);
		return registration;
	}
}
