package pso.decision_engine.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import pso.decision_engine.model.RuleSet;

public interface RuleSetDao {

	public void saveRuleSet(RuleSet ruleSet);

	public boolean doesRuleSetExist(String restEndPoint, String ruleSetId);
	
	public void setActiveRuleSet(String restEndPoint, String ruleSetId);

	public RuleSet getRuleSet(String ruleSetId);

	public RuleSet getActiveRuleSet(String restEndPoint);

	public String getActiveRuleSetId(String restEndPoint);

	public HashMap<String, HashSet<String>> getRuleSetLists(String ruleSetId);

	public List<String> getAllEndPoints();

}
