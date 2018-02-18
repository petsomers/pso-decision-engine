package pso.decision_engine.service;

import java.util.HashMap;

import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.UnitTestRunnerResult;

public interface RuleSetProcessorService {

	public DecisionResult runRuleSetWithParameters(RuleSet rs, HashMap<String, String> parameters);
	
	public UnitTestRunnerResult runUnitTests(RuleSet rs);

}
