package pso.decision_engine.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import pso.decision_engine.model.ExcelParseResult;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.RuleSetInfo;

public interface SetupApiService {

	public ExcelParseResult addExcelFile(InputStream in) throws IOException;
	
	boolean doesExcelFileExists(String restEndpoint, String ruleSetId);
	
	public void downloadExcel(String restEndpoint, String ruleSetId, OutputStream out)  throws IOException;

	/**
	 * 
	 * @param ruleSet
	 * @return ruleSetId
	 */
	public String saveRuleSet(RuleSet ruleSet);
	
	public boolean doesRuleSetExist(String restEndpoint, String ruleSetId);
	
	public List<String> getAllEndpoints();
	
	public void setActiveRuleSet(String restEndpoint, String ruleSetId);
	
	public String getActiveRuleSetId(String restEndpoint);
	
	public RuleSet getActiveRuleSetByEndpoint(String restEndpoint, boolean loadAllLists, boolean loadUnitTests);

	public RuleSet getRuleSet(String restEndpoint, String ruleSetId, boolean loadAllLists, boolean loadUnitTests);

	public List<RuleSetInfo> getRuleSetVersionsForEndpoint(String restEndpoint);

	public boolean isInList(RuleSet ruleSet, String listName, String value);
	
	public void deleteRuleSet(String restEndpoint, String ruleSetId);
	
	public void deleteInactiveRuleSetsForEndpoint(String restEndpoint);

	public void deleteRuleSetsWithEndpoint(String restEndpoint);
}