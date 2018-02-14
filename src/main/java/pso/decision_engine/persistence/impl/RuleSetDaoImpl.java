package pso.decision_engine.persistence.impl;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.persistence.RuleSetDao;
import pso.decision_engine.utils.ComparatorHelper;

@Component
public class RuleSetDaoImpl implements RuleSetDao {
	
	private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate (NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static String createTables[]=
	{
		"CREATE TABLE IF NOT EXISTS RuleSet ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"restEndPoint VARCHAR(50) NOT NULL, "+
		"createdBy VARCHAR(50), "+
		"version VARCHAR(20), "+
		"uploadDate TIMESTAMP NOT NULL, "+
		"PRIMARY KEY (ruleSetId))",
		
		"CREATE TABLE IF NOT EXISTS ActiveRuleSet ("+
		"restEndPoint VARCHAR(50) NOT NULL, "+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"PRIMARY KEY (restEndPoint))",
		
		"CREATE TABLE IF NOT EXISTS RuleSetParameters ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"parameterName VARCHAR(40) NOT NULL, "+
		"parameterType VARCHAR(10) NOT NULL, "+
		"PRIMARY KEY (ruleSetId, parameterName))",
		
		"CREATE TABLE IF NOT EXISTS Rule ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"ruleNumber INTEGER NOT NULL, "+
		"sheetName VARCHAR(100), "+
		"rowNumber INTEGER NOT NULL, "+
		"rowLabel VARCHAR(100), "+
		"parameterName VARCHAR(40), "+
		"comparator VARCHAR(15), "+
		"value1 VARCHAR(100), "+
		"value2 VARCHAR(100), "+
		"positiveResult VARCHAR(200), "+
		"negativeResult VARCHAR(200), "+
		"remark VARCHAR(500), "+
		"PRIMARY KEY (ruleSetId, ruleNumber))",
		
		"CREATE TABLE IF NOT EXISTS RuleSetList ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"listId INTEGER NOT NULL, "+
		"listName VARCHAR(100), "+
		"PRIMARY KEY (ruleSetId, listId))",
		
		"CREATE TABLE IF NOT EXISTS RuleSetListValues ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"listId INTEGER NOT NULL, "+
		"listValue VARCHAR(100) NOT NULL, "+
		"PRIMARY KEY (ruleSetId, listId, listValue))"
	};
    
	@PostConstruct
	public void prepareDatabase() {
		System.out.println("CREATE DB");
		MapSqlParameterSource params=new MapSqlParameterSource();
		for (String sql:createTables) {
			jdbcTemplate.update(sql, params);
		}
	}
	
	@Override
	public void saveRuleSet(RuleSet ruleSet) {
		MapSqlParameterSource parameters=new MapSqlParameterSource()
		.addValue("ruleSetId", ruleSet.getId())
		.addValue("restEndPoint", ruleSet.getRestEndPoint())
		.addValue("createdBy", ruleSet.getCreatedBy())
		.addValue("version", ruleSet.getVersion())
		.addValue("uploadDate", Timestamp.valueOf(ruleSet.getUploadDate()));
		
		jdbcTemplate.update(
			"INSERT INTO RuleSet (ruleSetId, restEndPoint, createdBy, version, uploadDate) "+
			"values (:ruleSetId, :restEndPoint, :createdBy, :version, :uploadDate)", parameters);
		
		MapSqlParameterSource[] inputParameters=new MapSqlParameterSource[ruleSet.getInputParameters().size()];
		int i=0;
		for (String parameterName:ruleSet.getInputParameters().keySet()) {
			inputParameters[i++]=
				new MapSqlParameterSource()
				.addValue("ruleSetId", ruleSet.getId())
				.addValue("parameterName", parameterName)
				.addValue("parameterType", ruleSet.getInputParameters().get(parameterName).toString());
		};
		jdbcTemplate.batchUpdate(
			"INSERT INTO RuleSetParameters (ruleSetId, parameterName, parameterType) "+
			"values (:ruleSetId, :parameterName, :parameterType)", inputParameters);
		inputParameters=null;
		
		MapSqlParameterSource[] rules=new MapSqlParameterSource[ruleSet.getRules().size()];
		i=0;
		for (Rule rule:ruleSet.getRules()) {
			rules[i]=
				new MapSqlParameterSource()
				.addValue("ruleSetId", ruleSet.getId())
				.addValue("ruleNumber", i++)
				.addValue("sheetName", rule.getSheetName())
				.addValue("rowNumber", rule.getRowNumber())
				.addValue("rowLabel", rule.getLabel())
				.addValue("parameterName", rule.getParameterName())
				.addValue("comparator", ComparatorHelper.comparatorToShortString(rule.getComparator()))
				.addValue("value1", valueToString(rule.getValue1()))
				.addValue("value2", valueToString(rule.getValue1()))
				.addValue("positiveResult", rule.getPositiveResult())
				.addValue("negativeResult", rule.getNegativeResult())
				.addValue("remark", rule.getRemark());
		}
		jdbcTemplate.batchUpdate(
			"INSERT INTO Rule ( "+
			"ruleSetId, ruleNumber, sheetName, rowNumber, rowLabel, parameterName, "+ 
			"comparator, value1, value2, positiveResult, negativeResult, remark) values "+
			"(:ruleSetId, :ruleNumber, :sheetName, :rowNumber, :rowLabel, :parameterName, "+ 
			":comparator, :value1, :value2, :positiveResult, :negativeResult, :remark)",rules);
		rules=null;
		
		i=0;
		for (String listName:ruleSet.getLists().keySet()) {
			parameters=new MapSqlParameterSource()
					.addValue("ruleSetId", ruleSet.getId())
					.addValue("listId", i)
					.addValue("listName", listName);
			jdbcTemplate.update(
				"INSERT INTO RuleSetList (ruleSetId, listId, listName) "+
				"values (:ruleSetId, :listId, :listName)", parameters);
			HashSet<String> values=ruleSet.getLists().get(listName);
			saveList(ruleSet.getId(), i, values);
			i++;
		}
	}
	
	private void saveList(String ruleSetId, int listId, HashSet<String> values) {
		MapSqlParameterSource[] listValues=new MapSqlParameterSource[values.size()];
		int j=0;
		for (String value:values) {
			listValues[j++]=
				new MapSqlParameterSource()
				.addValue("ruleSetId", ruleSetId)
				.addValue("listId", listId)
				.addValue("listValue", value);
		};
		jdbcTemplate.batchUpdate(
			"INSERT INTO RuleSetListValues (ruleSetId, listId, listValue) "+
			"values (:ruleSetId, :listId, :listValue)", listValues);
		listValues=null;
	}
	
	@Override
	public void setActiveRuleSet(String restEndPoint, String ruleSetId) {
		
	}
	
	@Override
	public RuleSet getRuleSet(String ruleSetId) {
		return null;
	}
	
	@Override
	public RuleSet getActiveRuleSet(String restEndPoint) {
		return null;
	}
	
	@Override
	public String getActiveRuleSetId(String restEndPoint) {
		return null;
	}
	
	@Override
	public HashMap<String, HashSet<String>> getRuleSetLists(String ruleSetId) {
		return null;
	}
	
	private DecimalFormat df=new DecimalFormat("#.###");
	private String valueToString(Object o) {
		if (o==null) return null;
		if (o instanceof String) return ((String) o).trim();
		if (o instanceof Double) return df.format((Double) o);
		return null;
	}

}
