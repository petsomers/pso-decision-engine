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

public class SecurityFilter implements Filter {

	
	private String adminUserAuthHeader;
	private String processorAuthHeader;

	
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
		if (request.getSession(false)!=null && request.getSession(false).getAttribute("adminUserId")!=null) {
			chain.doFilter(request, response);
			return;
		}
		String header = request.getHeader("Authorization");
		if (header!=null && header.equals(adminUserAuthHeader)) {
			chain.doFilter(request, response);
			return;
		}
		if (request.getServletPath()!=null && request.getServletPath().startsWith("/processor")) {
			if (header!=null && header.equals(processorAuthHeader)) {
				chain.doFilter(request, response);
				return;
			}
		}
		response.setHeader("WWW-Authenticate", "Basic realm=\"Decision Engine\"");
		response.setStatus(401);
	}

	@Override
	public void destroy() {
			
	}

	
}
