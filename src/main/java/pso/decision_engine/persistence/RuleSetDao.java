package pso.decision_engine.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import pso.decision_engine.model.InputParameterInfo;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.RuleSetInfo;
import pso.decision_engine.model.UnitTest;

public interface RuleSetDao {

	public void saveRuleSet(RuleSet ruleSet);

	public boolean doesRuleSetExist(String restEndpoint, String ruleSetId);
	
	public void setActiveRuleSet(String restEndpoint, String ruleSetId);

	public RuleSet getRuleSet(String ruleSetId);

	public String getActiveRuleSetId(String restEndpoint);

	/**
	 * 
	 * @param ruleSetId
	 * @param loadAll: don't use the maxInMemoryListSize into account
	 * @return
	 */
	public HashMap<String, HashSet<String>> getRuleSetLists(String ruleSetId, boolean loadAll);

	public List<String> getAllEndpoints();
	
	public boolean isInList(String ruleSetId, String listName, String value);

	public List<RuleSetInfo> getRuleSetVersionsForEndpoint(String restEndpoint);

	public Hashtable<String, InputParameterInfo> getRuleSetInputParameters(String ruleSetId);
	
	public List<UnitTest> getRuleSetUnitTests(String ruleSetId);

	public void deleteRuleSet(String restEndpoint, String ruleSetId);
	
	public void deleteRuleSetsWithEndpoint(String restEndpoint);

	public void deleteRuleSetsWithEndpointSkipId(String restEndpoint, String activeId);

	public void saveRuleSetSource(String ruleSetId, int contentLength, InputStream inputStream);

	public void streamRuleSetSource(String ruleSetId, OutputStream outputStream);

}
