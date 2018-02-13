package pso.decision_engine.persistence.impl;

import java.sql.Timestamp;
import java.sql.Types;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Component;

import pso.decision_engine.model.RuleSet;

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
		MapSqlParameterSource parameters=new MapSqlParameterSource();
		parameters.addValue("ruleSetId", ruleSet.getId());
		parameters.addValue("restEndPoint", ruleSet.getRestEndPoint());
		parameters.addValue("createdBy", ruleSet.getCreatedBy());
		parameters.addValue("version", ruleSet.getVersion());
		parameters.addValue("uploadDate", Timestamp.valueOf(ruleSet.getUploadDate()));
		
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
		
	}
	
	public void setActiveRuleSet(String restEndPoint, String ruleSetId) {
		
	}
	
	public RuleSet getRuleSet(String ruleSetId) {
		return null;
	}
	
	public RuleSet getActiveRuleSet(String restEndPoint) {
		return null;
	}

}
