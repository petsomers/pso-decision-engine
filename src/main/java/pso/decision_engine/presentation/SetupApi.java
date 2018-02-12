package pso.decision_engine.presentation;

import java.io.FileInputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pso.decision_engine.model.ExcelParseResult;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.service.SetupApiService;

@RestController
public class SetupApi {
	
	@Autowired
	private SetupApiService setupService;

	@RequestMapping(value = "/test",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ExcelParseResult test() throws Exception {
		try (FileInputStream fi=new FileInputStream("C:\\SonyWorkspace\\documents\\projects\\aep\\decision engine\\Decision Engine Input - version 3.xlsx")) {
		//try (FileInputStream fi=new FileInputStream("C:\\temp\\Decision Engine Input - version 3 - BIG.xlsx")) {
			return setupService.addExcelFile(fi);
		}
    }
	
	@RequestMapping(value = "/upload_excel",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ExcelParseResult uploadExcel(HttpServletRequest req) throws Exception {
		return setupService.addExcelFile(req.getInputStream());
	}
	
	@RequestMapping(value = "/source/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public RuleSet getActiveRuleSet(String restEndPoint) {
		return null;
	}
	
	@RequestMapping(value = "/source/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public RuleSet getActiveRuleSet(String restEndPoint, String ruleSetId) {
		return null;
	}
	
	public static class RuleSetVersions {
		List<String> ruleSetIds;
		String activeRuleSetId;
	}
	@RequestMapping(value = "/versions/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public RuleSetVersions getRuleSetVersions(String restEndPoint) {
		return null;
	}
	
	@RequestMapping(value = "/delete_inactive/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void deleteInactiveVersion(String restEndPoint, String ruleSetId) {

	}
	
	@RequestMapping(value = "/delete/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void delete(String restEndPoint) {
		
	}
	
}
