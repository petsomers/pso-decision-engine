package pso.decision_engine;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pso.decision_engine.model.ExcelParseResult;
import pso.decision_engine.service.impl.SetupApiServiceImpl;

@RestController
public class SetupApi {
	
	@Autowired
	private SetupApiServiceImpl setupService;

	@RequestMapping(value = "/test",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ExcelParseResult test() throws Exception {
		try (FileInputStream fi=new FileInputStream("C:\\SonyWorkspace\\documents\\projects\\aep\\decision engine\\Decision Engine Input - version 3.xlsx")) {
		//try (FileInputStream fi=new FileInputStream("C:\\temp\\Decision Engine Input - version 3 - BIG.xlsx")) {
			return setupService.addExcelFile(fi);
		}
    }
	
}
