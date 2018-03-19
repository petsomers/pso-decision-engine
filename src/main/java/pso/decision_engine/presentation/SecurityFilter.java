package pso.decision_engine.presentation;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import pso.decision_engine.service.JwtService;

public class SecurityFilter implements Filter {


	private final JwtService jwtService;
	
	private String adminUserAuthHeader;
	private String processorAuthHeader;

	
	public SecurityFilter(JwtService jwtService) {
		this.jwtService=jwtService;
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		adminUserAuthHeader=
			"Basic "+
			Base64.getEncoder().encodeToString((filterConfig.getInitParameter("adminUser")+":"+filterConfig.getInitParameter("adminPassword")).getBytes());
		
		processorAuthHeader=
			"Basic "+
			Base64.getEncoder().encodeToString((filterConfig.getInitParameter("processorUserId")+":"+filterConfig.getInitParameter("processorPassword")).getBytes());
		
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		// processor API: this will support both a session as basic authentication
		// other API's: only session with XSRF token in header will be allowed
		HttpServletRequest request = (HttpServletRequest) servletRequest;
	    HttpServletResponse response = (HttpServletResponse) servletResponse;
	    String header = request.getHeader("Authorization");
	    if (request.getServletPath()!=null && (request.getServletPath().startsWith("/processor") || request.getServletPath().startsWith("/dataset/upload"))) {
			if (header!=null && header.equals(processorAuthHeader)) {
				chain.doFilter(request, response);
				return;
			}
		}
	    if (request.getServletPath()!=null && request.getServletPath().startsWith("/admin")) {
			if (header!=null && header.equals(adminUserAuthHeader)) {
				chain.doFilter(request, response);
				return;
			} else {
				response.setHeader("WWW-Authenticate", "Basic realm=\"Decision Engine\"");
				response.setStatus(401);
				return;
			}
		}
	    
	    if (request.getServletPath()!=null && 
	    	(request.getServletPath().startsWith("/processor") 
	    	|| request.getServletPath().startsWith("/dataset")
	    	|| request.getServletPath().startsWith("/setup"))) {
	    	String jwt=request.getHeader("X-jwt");
		    if (jwt==null || jwtService.verifyJwt(jwt)==null) {
		    	response.sendError(HttpServletResponse.SC_FORBIDDEN);
		    	return;
		    }
	    }
	    
	    chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
			
	}

	
}
