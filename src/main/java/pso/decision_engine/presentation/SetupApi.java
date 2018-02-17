package pso.decision_engine.presentation;

import java.io.FileInputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pso.decision_engine.model.ExcelParseResult;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.RuleSetInfo;
import pso.decision_engine.service.SetupApiService;

@RestController
@RequestMapping("/setup")
public class SetupApi {
	
	@Autowired
	private SetupApiService setupService;

	@RequestMapping(value = "/test",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ExcelParseResult test() throws Exception {
		try (FileInputStream fi=new FileInputStream("C:\\SonyWorkspace\\documents\\projects\\aep\\decision engine\\Decision Engine Input - version 4.xlsx")) {
		//try (FileInputStream fi=new FileInputStream("C:\\temp\\Decision Engine Input - version 3 - BIG.xlsx")) {
			return setupService.addExcelFile(fi);
		}
    }
	
	@RequestMapping(value = "/upload_excel",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ExcelParseResult uploadExcel(HttpServletRequest req) throws Exception {
		return setupService.addExcelFile(req.getInputStream());	
	}
	
	@RequestMapping(value = "/setactive/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String setActiveRuleSet(@PathVariable String restEndPoint,@PathVariable String ruleSetId) {
		if (!setupService.doesRuleSetExist(restEndPoint, ruleSetId)) {
			return "RULESET NOT FOUND";
		} else {
			setupService.setActiveRuleSet(restEndPoint, ruleSetId);
			return "OK";
		}
	}
	
	@RequestMapping(value = "/endpoints",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<String> getAllEndPoints() {
		return setupService.getAllEndPoints();
	}
	
	@RequestMapping(value = "/source/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public RuleSet getActiveRuleSet(@PathVariable String restEndPoint) {
		return setupService.getActiveRuleSetByEndPoint(restEndPoint, true);
	}
	
	@RequestMapping(value = "/source/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public RuleSet getRuleSet(@PathVariable String restEndPoint, @PathVariable String ruleSetId) {
		return setupService.getRuleSet(restEndPoint, ruleSetId, true);
	}

	@RequestMapping(value = "/versions/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<RuleSetInfo> getRuleSetVersions(@PathVariable String restEndPoint) {
		return setupService.getRuleSetVersionsForEndPoint(restEndPoint);
	}
	
	@RequestMapping(value = "/delete_inactive/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void deleteInactiveVersion(@PathVariable String restEndPoint, @PathVariable String ruleSetId) {

	}
	
	@RequestMapping(value = "/delete/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void delete(@PathVariable String restEndPoint) {
		
	}
	
}
