package pso.decision_engine.persistence.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CreateTables {

	private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate (NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static String createTables[]=
	{
		"CREATE TABLE IF NOT EXISTS RuleSet ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"restEndpoint VARCHAR(50) NOT NULL, "+
		"name VARCHAR(150), "+
		"createdBy VARCHAR(50), "+
		"version VARCHAR(20), "+
		"remark VARCHAR(500), "+
		"uploadDate TIMESTAMP NOT NULL, "+
		"PRIMARY KEY (ruleSetId))",
		
		"CREATE INDEX IF NOT EXISTS ruleset_restEndpoint "+
		"on RuleSet (restEndpoint)",
		
		"CREATE TABLE IF NOT EXISTS ActiveRuleSet ("+
		"restEndpoint VARCHAR(50) NOT NULL, "+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"PRIMARY KEY (restEndpoint), "+
		"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
		
		"CREATE UNIQUE INDEX IF NOT EXISTS activeRuleSet_ruleSetId "+
		"on ActiveRuleSet (ruleSetId)",
		
		"CREATE TABLE IF NOT EXISTS RuleSetParameters ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"parameterName VARCHAR(40) NOT NULL, "+
		"parameterType VARCHAR(10) NOT NULL, "+
		"defaultValue VARCHAR(100), "+
		"PRIMARY KEY (ruleSetId, parameterName), "+
		"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
		
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
		"PRIMARY KEY (ruleSetId, ruleNumber), "+
		"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
		
		"CREATE TABLE IF NOT EXISTS RuleSetLists ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"listId INTEGER NOT NULL, "+
		"listName VARCHAR(100), "+
		"PRIMARY KEY (ruleSetId, listId), "+
		"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
		
		"CREATE UNIQUE INDEX IF NOT EXISTS list_name "+
		"on RuleSetLists (ruleSetId, listName)",
		
		"CREATE TABLE IF NOT EXISTS RuleSetListValues ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"listId INTEGER NOT NULL, "+
		"listValue VARCHAR(100) NOT NULL, "+
		"PRIMARY KEY (ruleSetId, listId, listValue), "+
		"FOREIGN KEY (ruleSetId, listId) REFERENCES RuleSetLists (ruleSetId, listId) ON DELETE CASCADE)",
		
		"CREATE TABLE IF NOT EXISTS RuleSetUnitTests ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"unitTestId INTEGER NOT NULL, "+
		"unitTestName VARCHAR(100) NOT NULL, "+
		"expectedResult  VARCHAR(200) NOT NULL, "+
		"PRIMARY KEY (ruleSetId, unitTestId), "+
		"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
		
		"CREATE TABLE IF NOT EXISTS RuleSetUnitTestParameters ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"unitTestId INTEGER NOT NULL, "+
		"parameterName VARCHAR(40) NOT NULL, "+
		"parameterValue VARCHAR(100), "+
		"PRIMARY KEY (ruleSetId, unitTestId, parameterName), "+
		"FOREIGN KEY (ruleSetId, unitTestId) REFERENCES RuleSetUnitTests (ruleSetId, unitTestId) ON DELETE CASCADE)",
	};
    
	@PostConstruct
	public void prepareDatabase() {
		System.out.println("CREATE DB");
		MapSqlParameterSource params=new MapSqlParameterSource();
		for (String sql:createTables) {
			jdbcTemplate.update(sql, params);
		}
	}

}
