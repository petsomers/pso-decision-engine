package pso.decision_engine;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pso.decision_engine.model.RuleSet;
import pso.decision_engine.service.impl.ExcelParserServiceImpl;

@SpringBootApplication
@RestController
public class DecisionEngineApplication {
	
	@Autowired
	ExcelParserServiceImpl excelParserService;

	public static void main(String[] args) {
		SpringApplication.run(DecisionEngineApplication.class, args);
	}
	
	@RequestMapping(value = "/test",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    RuleSet test() throws Exception {
		try (FileInputStream fi=new FileInputStream("C:\\SonyWorkspace\\documents\\projects\\aep\\decision engine\\Decision Engine Input - version 2.xlsx")) {
			return excelParserService.parseExcel(fi);
		}
    }
}
