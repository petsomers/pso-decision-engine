package pso.decision_engine.presentation;

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
import pso.decision_engine.service.RuleSetProcessorService;
import pso.decision_engine.service.SetupApiService;

@RestController
public class ProcessorApi {

	@Autowired
	private SetupApiService setupService;
	
	@Autowired
	private RuleSetProcessorService ruleSetProcessorService;
	
	@RequestMapping(value = "/run/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public DecisionResult run(HttpServletRequest request, @PathVariable String restEndPoint) throws Exception {
		RuleSet ruleSet=setupService.getActiveRuleSetByEndPoint(restEndPoint, false, false);
		if (ruleSet==null) {
			DecisionResult result=new DecisionResult();
			result.setError(true);
			result.setErrorMessage("RuleSet not found.");
		}
		final HashMap<String, String> parameters=new HashMap<>();
		for (String parameter: Collections.list(request.getParameterNames())) {
			parameters.put(parameter, request.getParameter(parameter).trim());
		}
		return ruleSetProcessorService.runRuleSetWithParameters(ruleSet, parameters);
    }
	
	@RequestMapping(value = "/run/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public DecisionResult run(
    		HttpServletRequest request, 
    		@PathVariable String restEndPoint,
    		@PathVariable String ruleSetId) throws Exception {
		RuleSet ruleSet=setupService.getRuleSet(restEndPoint, ruleSetId, false, false);
		if (ruleSet==null) {
			DecisionResult result=new DecisionResult();
			result.setError(true);
			result.setErrorMessage("RuleSet not found.");
		}
		final HashMap<String, String> parameters=new HashMap<>();
		for (String parameter: Collections.list(request.getParameterNames())) {
			parameters.put(parameter, request.getParameter(parameter).trim());
		}
		return ruleSetProcessorService.runRuleSetWithParameters(ruleSet, parameters);
    }
}
