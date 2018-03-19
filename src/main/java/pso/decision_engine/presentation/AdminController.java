package pso.decision_engine.presentation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import pso.decision_engine.service.JwtService;

@Controller
public class AdminController {
	
	@Autowired
	private JwtService jwtService;

	@RequestMapping(value = "/admin")
	public String index(HttpServletRequest req, HttpServletResponse resp, Model model) {
		String jwt=jwtService.generateJwt("admin");
		model.addAttribute("jwt", jwt);
		return "app";
	}
}
