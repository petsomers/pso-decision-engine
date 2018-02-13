package pso.decision_engine.persistence.impl;

import pso.decision_engine.model.RuleSet;

public interface RuleSetDao {

	public void saveRuleSet(RuleSet ruleSet);

	public void setActiveRuleSet(String restEndPoint, String ruleSetId);

	public RuleSet getRuleSet(String ruleSetId);

	public RuleSet getActiveRuleSet(String restEndPoint);

	public String getActiveRuleSetId(String restEndPoint);

}
