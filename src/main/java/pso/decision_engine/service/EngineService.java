package pso.decision_engine.service;

import java.util.HashMap;

import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.RuleSet;

public interface EngineService {

	public RuleSet loadRuleSet(String ruleSetId);

	/**
	 * 
	 * @param restEndPoint
	 * @return ruleSetId or null
	 */
	public String getActiveRuleSet(String restEndPoint);

	/**
	 * Makes this idea to be the active (default) ruleSetId for the restEndPoint
	 * The restEndPoint can be deducted from the ruleSetId
	 * @param ruleSetId
	 */
	public void switchActiveRuleSet(String ruleSetId);

}