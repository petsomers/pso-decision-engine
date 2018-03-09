package pso.decision_engine.presentation;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.UnitTestRunnerResult;
import pso.decision_engine.presentation.utils.JsonToTextOutput;
import pso.decision_engine.service.RuleSetProcessorService;
import pso.decision_engine.service.SetupApiService;

@RestController
@RequestMapping("/processor")
public class ProcessorApi {

	@Autowired
	private SetupApiService setupService;
	
	@Autowired
	private RuleSetProcessorService ruleSetProcessorService;
	
	@RequestMapping(value = "/json/run/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public DecisionResult runJson(HttpServletRequest request, @PathVariable String restEndPoint) throws Exception {
		String ruleSetId=setupService.getActiveRuleSetId(restEndPoint);
		return runJson(request, restEndPoint, ruleSetId);
    }
	
	@RequestMapping(value = "/json/run/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public DecisionResult runJson(
    		HttpServletRequest request, 
    		@PathVariable String restEndPoint,
    		@PathVariable String ruleSetId) throws Exception {
		RuleSet ruleSet=setupService.getRuleSet(restEndPoint, ruleSetId, false, false);
		if (ruleSet==null) {
			DecisionResult result=new DecisionResult();
			result.setError(true);
			result.setErrorMessage("RuleSet not found.");
			return result;
		}
		boolean trace=request.getParameter("trace")!=null && !"N".equalsIgnoreCase(request.getParameter("trace"));
		final HashMap<String, String> parameters=new HashMap<>();
		for (String parameter: Collections.list(request.getParameterNames())) {
			if (!"trace".equals(parameter))
				parameters.put(parameter, request.getParameter(parameter).trim());
		}
		DecisionResult r=ruleSetProcessorService.runRuleSetWithParameters(ruleSet, parameters);
		if (!trace) {
			r.setRun(null);
		}
		return r;
    }
	
	
	@RequestMapping(value = "/run/{restEndPoint}",method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public void runPlainText(HttpServletRequest request, 
    		PrintWriter pw, 
    		@PathVariable String restEndPoint) throws Exception {
		DecisionResult r=runJson(request, restEndPoint);
		boolean trace=request.getParameter("trace")!=null && !"N".equalsIgnoreCase(request.getParameter("trace"));
		if (r.isError()) {
			pw.print("ERROR: "+r.getErrorMessage());
		} else {
			pw.print(r.getDecision());
		}
		if (trace) {
			pw.println();pw.println();
			JsonToTextOutput.renderDecisionResult(pw, r);
		}
	}
	
	@RequestMapping(value = "/run/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public void runPlainText(HttpServletRequest request, 
    		PrintWriter pw, 
    		@PathVariable String restEndPoint, 
    		@PathVariable String ruleSetId) throws Exception {
		DecisionResult r=runJson(request, restEndPoint, ruleSetId);
		boolean trace=request.getParameter("trace")!=null && !"N".equalsIgnoreCase(request.getParameter("trace"));
		if (r.isError()) {
			pw.print("ERROR: "+r.getErrorMessage());
		} else {
			pw.print(r.getDecision());
		}
		if (trace) {
			pw.println();pw.println();
			JsonToTextOutput.renderDecisionResult(pw, r);
		}
	}
	
	@RequestMapping(value = "/run_unittests/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public UnitTestRunnerResult runUnitTests(
    		HttpServletRequest request, 
    		@PathVariable String restEndPoint,
    		@PathVariable String ruleSetId) throws Exception {
		RuleSet ruleSet=setupService.getRuleSet(restEndPoint, ruleSetId, false, true);
		if (ruleSet==null) {
			UnitTestRunnerResult result=new UnitTestRunnerResult();
			result.setErrorMessage("RuleSet not found.");
			return result;
		}
		return ruleSetProcessorService.runUnitTests(ruleSet);
    }
	
}
