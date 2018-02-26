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
	
	boolean doesExcelFileExists(String restEndPoint, String ruleSetId);
	
	public void downloadExcel(String restEndPoint, String ruleSetId, OutputStream out)  throws IOException;

	/**
	 * 
	 * @param ruleSet
	 * @return ruleSetId
	 */
	public String saveRuleSet(RuleSet ruleSet);
	
	public boolean doesRuleSetExist(String restEndPoint, String ruleSetId);
	
	public List<String> getAllEndPoints();
	
	public void setActiveRuleSet(String restEndPoint, String ruleSetId);
	
	public String getActiveRuleSetId(String restEndPoint);
	
	public RuleSet getActiveRuleSetByEndPoint(String restEndPoint, boolean loadAllLists, boolean loadUnitTests);

	public RuleSet getRuleSet(String restEndPoint, String ruleSetId, boolean loadAllLists, boolean loadUnitTests);

	public List<RuleSetInfo> getRuleSetVersionsForEndPoint(String restEndPoint);

	public boolean isInList(RuleSet ruleSet, String listName, String value);
}