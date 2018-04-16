package pso.decision_engine.presentation;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pso.decision_engine.service.JwtService;

@Controller
public class AdminController {
	
	@Autowired
	private JwtService jwtService;

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String index(HttpServletRequest req, HttpServletResponse resp, Model model) {
		String jwt=jwtService.generateJwt("admin");
		model.addAttribute("jwt", jwtService.getJwtPayload(jwt));
		if ("YES".equals(req.getAttribute("fromUM"))) {
			model.addAttribute("showHeader", "false");
		} else {
			model.addAttribute("showHeader", "true");
		}
		Cookie tokenCookie=new Cookie("token", jwt);
		tokenCookie.setHttpOnly(true);
		resp.addCookie(tokenCookie);
		return "app";
	}
	
	@RequestMapping(value = "/admin", method = RequestMethod.POST)
	public String startWithJwt(HttpServletRequest req, HttpServletResponse resp, Model model, @RequestParam(name="jwt") String jwt) {
		
		return index(req, resp, model);
	}
}
