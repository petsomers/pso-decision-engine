package pso.decision_engine.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.RuleSetInfo;
import pso.decision_engine.model.UnitTest;
import pso.decision_engine.model.enums.ParameterType;

public interface RuleSetDao {

	public void saveRuleSet(RuleSet ruleSet);

	public boolean doesRuleSetExist(String restEndPoint, String ruleSetId);
	
	public void setActiveRuleSet(String restEndPoint, String ruleSetId);

	public RuleSet getRuleSet(String ruleSetId);

	public String getActiveRuleSetId(String restEndPoint);

	/**
	 * 
	 * @param ruleSetId
	 * @param loadAll: don't use the maxInMemoryListSize into account
	 * @return
	 */
	public HashMap<String, HashSet<String>> getRuleSetLists(String ruleSetId, boolean loadAll);

	public List<String> getAllEndPoints();
	
	public boolean isInList(String ruleSetId, String listName, String value);

	public List<RuleSetInfo> getRuleSetVersionsForEndPoint(String restEndPoint);

	public Hashtable<String, ParameterType> getRuleSetInputParameters(String ruleSetId);
	
	public List<UnitTest> getRuleSetUnitTests(String ruleSetId);

}
