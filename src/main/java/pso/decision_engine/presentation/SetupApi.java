package pso.decision_engine.presentation;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jodd.servlet.ServletUtil;
import jodd.servlet.upload.MultipartRequestWrapper;
import jodd.upload.FileUpload;
import jodd.upload.impl.DiskFileUploadFactory;
import jodd.upload.impl.MemoryFileUploadFactory;
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
	
	@RequestMapping(value = "/form_upload_excel",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ExcelParseResult formUploadExcel(HttpServletRequest req) throws Exception {
		ExcelParseResult result=new ExcelParseResult();
		try {
			boolean multipartRequest = ServletUtil.isMultipartRequest(req);
			if (!multipartRequest) {
				result.setErrorMessage("Invalid Request.");
				return result;
			}
			
			
			DiskFileUploadFactory dfuf=new DiskFileUploadFactory(
				Paths.get(appConfig.getDataDirectory(), "temp").toString(),
				10*1024*1024);
			MultipartRequestWrapper mrw = new MultipartRequestWrapper(
				req, 
				dfuf);
			String fileParameterName=null;
			if (mrw.getFileParameterNames().hasMoreElements()) {
				fileParameterName=mrw.getFileParameterNames().nextElement();
			}
			if (fileParameterName==null) {
				result.setErrorMessage("No file in request.");
				return result;
			}
			
			FileUpload fileUpload=mrw.getFile(fileParameterName);
			if (fileUpload.isFileTooBig()) {
				result.setErrorMessage("The file is too big. (max 1MB)");
				return result;
			}
			if (!fileUpload.isValid()) {
				result.setErrorMessage("The file is invalid.");
				return result;
			}
			try (InputStream fi =fileUpload.getFileInputStream()) {
				return setupService.addExcelFile(fi);
			}
		} catch (Exception e) {
			result.setErrorMessage(e.getMessage());
			return result;
		}
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

	}
	
	@RequestMapping(value = "/delete/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void delete(@PathVariable String restEndPoint) {
		
	}
	
}
