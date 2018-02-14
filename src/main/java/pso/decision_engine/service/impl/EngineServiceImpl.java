package pso.decision_engine.service.impl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import pso.decision_engine.model.AppConfig;
import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.service.EngineService;

public class EngineServiceImpl implements EngineService {
	
	@Autowired
	private AppConfig appConfig;
	
	/* (non-Javadoc)
	 * @see pso.decision_engine.service.impl.EngineService#loadRuleSet(java.lang.String)
	 */
	@Override
	public RuleSet loadRuleSet(String ruleSetId) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see pso.decision_engine.service.impl.EngineService#getActiveRuleSet(java.lang.String)
	 */
	@Override
	public String getActiveRuleSet(String restEndPoint) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see pso.decision_engine.service.impl.EngineService#switchActiveRuleSet(java.lang.String)
	 */
	@Override
	public void switchActiveRuleSet(String ruleSetId) {
		
	}
}
