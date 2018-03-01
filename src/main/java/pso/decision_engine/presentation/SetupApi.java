package pso.decision_engine.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pso.decision_engine.model.AppConfig;
import pso.decision_engine.model.ExcelParseResult;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.RuleSetInfo;
import pso.decision_engine.service.SetupApiService;

@RestController
@RequestMapping("/setup")
public class SetupApi {
	
	@Autowired
	AppConfig appConfig;
	
	@Autowired
	private SetupApiService setupService;
	
	@RequestMapping(value = "/upload_excel",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ExcelParseResult uploadExcel(HttpServletRequest req) throws Exception {
		return setupService.addExcelFile(req.getInputStream());	
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/form_upload_excel", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = "application/json;charset=UTF-8")
    public ExcelParseResult formUploadExcel(HttpServletRequest request) throws Exception {
		ExcelParseResult result=new ExcelParseResult();
		try {
			Collection<Part> parts = request.getParts();
			if (parts.isEmpty()) {
				result.setErrorMessage("Invalid Request.");
				return result;
			}
			Part part = parts.iterator().next();
			try (InputStream in = part.getInputStream()) {
				 return setupService.addExcelFile(in);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErrorMessage(e.getMessage());
			return result;
		}
	}
	
	
	@RequestMapping(value = "/download_excel/{restEndpoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/octetstream")
	public void downloadExcel(@PathVariable String restEndpoint,@PathVariable String ruleSetId, HttpServletResponse resp) throws IOException {
		if (!setupService.doesRuleSetExist(restEndpoint, ruleSetId)) {
			resp.sendError(404);;
			return;
		}
		if (!setupService.doesExcelFileExists(restEndpoint, ruleSetId)) {
			resp.sendError(404);;
			return;
		}
		resp.setHeader("pragma", "no-cache");
		resp.setHeader( "Cache-Control","no-cache" );
		resp.setHeader( "Cache-Control","no-store" );
		resp.setDateHeader( "Expires", 0 );
		resp.setContentType("application/octetstream");
		resp.setHeader("Content-Disposition", "attachment; filename="+restEndpoint+"_"+ruleSetId+".xlsx");
		setupService.downloadExcel(restEndpoint, ruleSetId, resp.getOutputStream());
	}
	
	@RequestMapping(value = "/setactive/{restEndpoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String setActiveRuleSet(@PathVariable String restEndpoint,@PathVariable String ruleSetId) {
		if (!setupService.doesRuleSetExist(restEndpoint, ruleSetId)) {
			return "RULESET NOT FOUND";
		} else {
			// todo: first run all unit tests before making a ruleset active
			try {
				setupService.setActiveRuleSet(restEndpoint, ruleSetId);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "OK";
		}
	}
	
	@RequestMapping(value = "/endpoints",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<String> getAllEndpoints() {
		return setupService.getAllEndpoints();
	}
	
	@RequestMapping(value = "/source/{restEndpoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public RuleSet getActiveRuleSet(@PathVariable String restEndpoint) {
		return setupService.getActiveRuleSetByEndpoint(restEndpoint, true, true);
	}
	
	@RequestMapping(value = "/source/{restEndpoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public RuleSet getRuleSet(@PathVariable String restEndpoint, @PathVariable String ruleSetId) {
		return setupService.getRuleSet(restEndpoint, ruleSetId, true, true);
	}

	@RequestMapping(value = "/versions/{restEndpoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<RuleSetInfo> getRuleSetVersions(@PathVariable String restEndpoint) {
		return setupService.getRuleSetVersionsForEndpoint(restEndpoint);
	}
	
	@RequestMapping(value = "/delete/inactive/{restEndpoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void deleteInactive(@PathVariable String restEndpoint) {
		setupService.deleteInactiveRuleSetsForEndpoint(restEndpoint);
	}
	
	@RequestMapping(value = "/delete/endpoint/{restEndpoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void deleteRestEndpoint(@PathVariable String restEndpoint) {
		setupService.deleteRuleSetsWithEndpoint(restEndpoint);
	}
	
	@RequestMapping(value = "/delete/ruleset/{restEndpoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void deleteRuleSet(@PathVariable String restEndpoint, @PathVariable String ruleSetId) {
		setupService.deleteRuleSet(restEndpoint, ruleSetId);
	}

}
