package pso.decision_engine.presentation;

import java.io.FileInputStream;
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

	@RequestMapping(value = "/test",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ExcelParseResult test() throws Exception {
		try (FileInputStream fi=new FileInputStream("C:\\SonyWorkspace\\documents\\projects\\aep\\decision engine\\Decision Engine Input - version 4.xlsx")) {
		//try (FileInputStream fi=new FileInputStream("C:\\temp\\Decision Engine Input - version 3 - BIG.xlsx")) {
			return setupService.addExcelFile(fi);
		}
    }
	
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
	
	
	@RequestMapping(value = "/download_excel/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/octetstream")
	public void downloadExcel(@PathVariable String restEndPoint,@PathVariable String ruleSetId, HttpServletResponse resp) throws IOException {
		if (!setupService.doesRuleSetExist(restEndPoint, ruleSetId)) {
			resp.sendError(404);;
			return;
		}
		if (!setupService.doesExcelFileExists(restEndPoint, ruleSetId)) {
			resp.sendError(404);;
			return;
		}
		resp.setHeader("pragma", "no-cache");
		resp.setHeader( "Cache-Control","no-cache" );
		resp.setHeader( "Cache-Control","no-store" );
		resp.setDateHeader( "Expires", 0 );
		resp.setContentType("application/octetstream");
		resp.setHeader("Content-Disposition", "attachment; filename="+restEndPoint+"_"+ruleSetId+".xlsx");
		setupService.downloadExcel(restEndPoint, ruleSetId, resp.getOutputStream());
	}
	
	@RequestMapping(value = "/setactive/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String setActiveRuleSet(@PathVariable String restEndPoint,@PathVariable String ruleSetId) {
		if (!setupService.doesRuleSetExist(restEndPoint, ruleSetId)) {
			return "RULESET NOT FOUND";
		} else {
			// todo: first run all unit tests before making a ruleset active
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
		return setupService.getActiveRuleSetByEndPoint(restEndPoint, true, true);
	}
	
	@RequestMapping(value = "/source/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public RuleSet getRuleSet(@PathVariable String restEndPoint, @PathVariable String ruleSetId) {
		return setupService.getRuleSet(restEndPoint, ruleSetId, true, true);
	}

	@RequestMapping(value = "/versions/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<RuleSetInfo> getRuleSetVersions(@PathVariable String restEndPoint) {
		return setupService.getRuleSetVersionsForEndPoint(restEndPoint);
	}
	
	@RequestMapping(value = "/delete_inactive/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void deleteInactiveVersion(@PathVariable String restEndPoint, @PathVariable String ruleSetId) {
		setupService.deleteRuleSet(restEndPoint, ruleSetId);
	}
	
	@RequestMapping(value = "/delete/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void delete(@PathVariable String restEndPoint) {
		
	}
	
}
