package pso.decision_engine.presentation;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.ExcelParseResult;
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
    public DecisionResult run(String restEndPoint) throws Exception {
		RuleSet ruleSet=setupService.getActiveRuleSet(restEndPoint);
		
		return null;
		//return ruleSetProcessorService.runRuleSetWithParameters(rs, parameters);
    }
}
