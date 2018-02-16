package pso.decision_engine.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pso.decision_engine.model.ExcelParseResult;
import pso.decision_engine.model.RuleSet;

public interface SetupApiService {

	public ExcelParseResult addExcelFile(InputStream in) throws IOException;
	
	public RuleSet loadRuleSet(String ruleSetId);

		/**
	 * 
	 * @param ruleSet
	 * @return ruleSetId
	 */
	public String saveRuleSet(RuleSet ruleSet);
	
	public boolean doesRuleSetExist(String restEndPoint, String ruleSetId);
	
	public List<String> getAllEndPoints();
	
	public void setActiveRuleSet(String restEndPoint, String ruleSetId);
	
	/**
	 * 
	 * @param restEndPoint
	 * @return ruleSetId or null
	 */
	public RuleSet getActiveRuleSet(String restEndPoint);

	/**
	 * Makes this id to be the active (default) ruleSetId for the restEndPoint
	 * The restEndPoint can be deducted from the ruleSetId
	 * @param ruleSetId
	 */
	public void switchActiveRuleSet(String ruleSetId);

}