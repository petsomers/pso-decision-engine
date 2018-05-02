package pso.decision_engine.persistence.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import pso.decision_engine.config.AppConfig;

@Component
public class CreateTables {
	
	@Autowired
	private AppConfig appConfig;

	private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate (NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String[] createPostgresCreateStatements() {
    	return 	new String[] {
    		"CREATE TABLE IF NOT EXISTS RuleSet ("+
			"ruleSetId VARCHAR(20) NOT NULL, "+
			"restEndpoint VARCHAR(50) NOT NULL, "+
			"name VARCHAR(150), "+
			"createdBy VARCHAR(50), "+
			"version VARCHAR(20), "+
			"remark VARCHAR(500), "+
			"uploadDate TIMESTAMP NOT NULL, "+
			"PRIMARY KEY (ruleSetId))",
			
			"CREATE INDEX IF NOT EXISTS ruleset_restEndpointIndex "+
			"on RuleSet (restEndpoint)",
			
			"CREATE TABLE IF NOT EXISTS ActiveRuleSet ("+
			"restEndpoint VARCHAR(50) NOT NULL, "+
			"ruleSetId VARCHAR(20) NOT NULL, "+
			"PRIMARY KEY (restEndpoint), "+
			"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
			
			"CREATE UNIQUE INDEX IF NOT EXISTS activeRuleSet_ruleSetIdIndex "+
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
			
			//"CREATE CACHED TABLE IF NOT EXISTS RuleSetLists ("+
			"CREATE TABLE IF NOT EXISTS RuleSetLists ("+
			"ruleSetId VARCHAR(20) NOT NULL, "+
			"listId INTEGER NOT NULL, "+
			"listName VARCHAR(100), "+
			"PRIMARY KEY (ruleSetId, listId), "+
			"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
			
			"CREATE UNIQUE INDEX IF NOT EXISTS list_nameIndex "+
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
			
			"CREATE TABLE IF NOT EXISTS RuleSetSource ("+
			"ruleSetId VARCHAR(20) NOT NULL, "+
			"size INTEGER NOT NULL, "+
			"data bytea NOT NULL, "+
			"PRIMARY KEY (ruleSetId), "+
			"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
			
			"CREATE TABLE IF NOT EXISTS DataSet ("+
			"dataSetId VARCHAR(20) NOT NULL, "+
			"name VARCHAR(100), "+
			"type VARCHAR(10), "+ // SET or LOOKUP
			"PRIMARY KEY (dataSetId))",
			
			"CREATE UNIQUE INDEX IF NOT EXISTS dataSet_nameIndex "+
			"on DataSet (name)",
			
			"CREATE TABLE IF NOT EXISTS DataSetVersion ("+
			"dataSetVersionId VARCHAR(20) NOT NULL, "+
			"dataSetId VARCHAR(20) NOT NULL, "+
			"uploadDate TIMESTAMP NOT NULL, "+
			"PRIMARY KEY (dataSetVersionId), "+
			"FOREIGN KEY (dataSetId) REFERENCES DataSet (dataSetId) ON DELETE CASCADE)",
			
			"CREATE TABLE IF NOT EXISTS ActiveDataSetVersion ("+
			"dataSetId VARCHAR(20) NOT NULL, "+
			"dataSetVersionId VARCHAR(20) NOT NULL, "+
			"PRIMARY KEY (dataSetId), "+
			"FOREIGN KEY (dataSetId) REFERENCES DataSet (dataSetId) ON DELETE CASCADE)",
			
			//"CREATE CACHED TABLE IF NOT EXISTS DataSetKeys ("+
			"CREATE TABLE IF NOT EXISTS DataSetKeys ("+
			"dataSetVersionId VARCHAR(20) NOT NULL, "+
			"keyId INTEGER NOT NULL, "+
			"key VARCHAR(100), "+
			"PRIMARY KEY (dataSetVersionId, keyId), "+
			"FOREIGN KEY (dataSetVersionId) REFERENCES DataSetVersion (dataSetVersionId) ON DELETE CASCADE)",
			
			"CREATE INDEX IF NOT EXISTS DataSetKeyNameIndex "+
			"on DataSetKeys (dataSetVersionId, key)",
			
			"CREATE TABLE IF NOT EXISTS DataSetValueNames ("+
			"dataSetVersionId VARCHAR(20) NOT NULL, "+
			"valueId INTEGER NOT NULL, "+
			"name VARCHAR(100), "+
			"PRIMARY KEY (dataSetVersionId, valueId), "+
			"FOREIGN KEY (dataSetVersionId) REFERENCES DataSetVersion (dataSetVersionId) ON DELETE CASCADE)",
			
			//"CREATE CACHED TABLE IF NOT EXISTS DataSetValues ("+
			"CREATE TABLE IF NOT EXISTS DataSetValues ("+
			"dataSetVersionId VARCHAR(20) NOT NULL, "+
			"keyId INTEGER NOT NULL, "+
			"valueId INTEGER NOT NULL, "+
			"value VARCHAR(100), "+
			"PRIMARY KEY (dataSetVersionId, keyId, valueId), "+
			"FOREIGN KEY (dataSetVersionId) REFERENCES DataSetVersion (dataSetVersionId) ON DELETE CASCADE)",
			
			
			/*
			// experimenting with using key/value instead of relational data
			"CREATE TABLE IF NOT EXISTS JsonRuleSet ("+
			"ruleSetId VARCHAR(20) NOT NULL, "+
			"jsonvalue json NOT NULL,"+
			"PRIMARY KEY (ruleSetId), "+
			"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
			
			"CREATE TABLE IF NOT EXISTS JsonRuleSetListValues ("+
			"ruleSetId VARCHAR(20) NOT NULL, "+
			"listId INTEGER, "+
			"value VARCHAR(100), "+
			"PRIMARY KEY (ruleSetId, listId, value), "+
			"FOREIGN KEY (ruleSetId) REFERENCES JsonRuleSet (ruleSetId) ON DELETE CASCADE)",
			
			"CREATE TABLE IF NOT EXISTS JsonRuleSetEndPoint ("+
			"ruleSetId VARCHAR(20) NOT NULL, "+
			"restEndpoint VARCHAR(50) NOT NULL, "+
			"PRIMARY KEY (ruleSetId), "+
			"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
			
			"CREATE TABLE IF NOT EXISTS JsonActiveRuleSet ("+
			"restEndpoint VARCHAR(50) NOT NULL, "+
			"ruleSetId VARCHAR(20) NOT NULL, "+
			"PRIMARY KEY (restEndpoint), "+
			"FOREIGN KEY (ruleSetId) REFERENCES RuleSet (ruleSetId) ON DELETE CASCADE)",
					
			/*
			"CREATE TABLE IF NOT EXISTS JsonDataSetValues ("+
			"dataSetVersionId VARCHAR(20) NOT NULL, "+
			"keyId INTEGER NOT NULL, "+
			"jsonvalue json NOT NULL, "+
			"PRIMARY KEY (dataSetVersionId, keyId), "+
			"FOREIGN KEY (dataSetVersionId) REFERENCES DataSetVersion (dataSetVersionId) ON DELETE CASCADE)"
			*/
    	};
    }
    
    private String[] createIgniteCreateStatements() {
    	return 	new String[] {
		"CREATE TABLE IF NOT EXISTS RuleSet ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"restEndpoint VARCHAR(50) NOT NULL, "+
		"name VARCHAR(150), "+
		"createdBy VARCHAR(50), "+
		"version VARCHAR(20), "+
		"remark VARCHAR(500), "+
		"uploadDate TIMESTAMP NOT NULL, "+
		"PRIMARY KEY (ruleSetId)) WITH \"template=replicated\"",
		
		"CREATE INDEX IF NOT EXISTS ruleset_restEndpointIndex "+
		"on RuleSet (restEndpoint)",
		
		"CREATE TABLE IF NOT EXISTS ActiveRuleSet ("+
		"restEndpoint VARCHAR(50) NOT NULL, "+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"PRIMARY KEY (restEndpoint)) WITH \"template=replicated\"",
		
		"CREATE INDEX IF NOT EXISTS activeRuleSet_ruleSetIdIndex "+
		"on ActiveRuleSet (ruleSetId)",
		
		"CREATE TABLE IF NOT EXISTS RuleSetParameters ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"parameterName VARCHAR(40) NOT NULL, "+
		"parameterType VARCHAR(10) NOT NULL, "+
		"defaultValue VARCHAR(100), "+
		"PRIMARY KEY (ruleSetId, parameterName)) "+
		"WITH \"template=replicated\"",
		
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
		"PRIMARY KEY (ruleSetId, ruleNumber)) "+
		"WITH \"template=replicated\"",
		
		//"CREATE CACHED TABLE IF NOT EXISTS RuleSetLists ("+
		"CREATE TABLE IF NOT EXISTS RuleSetLists ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"listId INTEGER NOT NULL, "+
		"listName VARCHAR(100), "+
		"PRIMARY KEY (ruleSetId, listId)) "+
		"WITH \"template=replicated\"",
		
		"CREATE INDEX IF NOT EXISTS list_nameIndex "+
		"on RuleSetLists (ruleSetId, listName)",
		
		"CREATE TABLE IF NOT EXISTS RuleSetListValues ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"listId INTEGER NOT NULL, "+
		"listValue VARCHAR(100) NOT NULL, "+
		"nonPk INT, "+ 
		"PRIMARY KEY (ruleSetId, listId, listValue)) "+
		"WITH \"template=replicated\"",
		
		"CREATE TABLE IF NOT EXISTS RuleSetUnitTests ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"unitTestId INTEGER NOT NULL, "+
		"unitTestName VARCHAR(100) NOT NULL, "+
		"expectedResult  VARCHAR(200) NOT NULL, "+
		"PRIMARY KEY (ruleSetId, unitTestId)) WITH \"template=replicated\"",
		
		"CREATE TABLE IF NOT EXISTS RuleSetUnitTestParameters ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"unitTestId INTEGER NOT NULL, "+
		"parameterName VARCHAR(40) NOT NULL, "+
		"parameterValue VARCHAR(100), "+
		"PRIMARY KEY (ruleSetId, unitTestId, parameterName)) "+
		"WITH \"template=replicated\"",
		
		"CREATE TABLE IF NOT EXISTS RuleSetSource ("+
		"ruleSetId VARCHAR(20) NOT NULL, "+
		"size INTEGER NOT NULL, "+
		"data bytea NOT NULL, "+
		"PRIMARY KEY (ruleSetId)) "+
		"WITH \"template=replicated\"",
		
		"CREATE TABLE IF NOT EXISTS DataSet ("+
		"dataSetId VARCHAR(20) NOT NULL, "+
		"name VARCHAR(100), "+
		"type VARCHAR(10), "+ // SET or LOOKUP
		"PRIMARY KEY (dataSetId)) "+
		"WITH \"template=replicated\"",
		
		"CREATE INDEX IF NOT EXISTS dataSet_nameIndex "+
		"on DataSet (name)",
		
		"CREATE TABLE IF NOT EXISTS DataSetVersion ("+
		"dataSetVersionId VARCHAR(20) NOT NULL, "+
		"dataSetId VARCHAR(20) NOT NULL, "+
		"uploadDate TIMESTAMP NOT NULL, "+
		"PRIMARY KEY (dataSetVersionId)) "+
		"WITH \"template=replicated\"",
		
		"CREATE TABLE IF NOT EXISTS ActiveDataSetVersion ("+
		"dataSetId VARCHAR(20) NOT NULL, "+
		"dataSetVersionId VARCHAR(20) NOT NULL, "+
		"PRIMARY KEY (dataSetId)) "+
		"WITH \"template=replicated\"",
		
		//"CREATE CACHED TABLE IF NOT EXISTS DataSetKeys ("+
		"CREATE TABLE IF NOT EXISTS DataSetKeys ("+
		"dataSetVersionId VARCHAR(20) NOT NULL, "+
		"keyId INTEGER NOT NULL, "+
		"key VARCHAR(100), "+
		"PRIMARY KEY (dataSetVersionId, keyId)) "+
		"WITH \"template=replicated\"",
		
		"CREATE INDEX IF NOT EXISTS DataSetKeyNameIndex "+
		"on DataSetKeys (dataSetVersionId, key)",
		
		"CREATE TABLE IF NOT EXISTS DataSetValueNames ("+
		"dataSetVersionId VARCHAR(20) NOT NULL, "+
		"valueId INTEGER NOT NULL, "+
		"name VARCHAR(100), "+
		"PRIMARY KEY (dataSetVersionId, valueId)) "+
		"WITH \"template=replicated\"",
		
		//"CREATE CACHED TABLE IF NOT EXISTS DataSetValues ("+
		"CREATE TABLE IF NOT EXISTS DataSetValues ("+
		"dataSetVersionId VARCHAR(20) NOT NULL, "+
		"keyId INTEGER NOT NULL, "+
		"valueId INTEGER NOT NULL, "+
		"value VARCHAR(100), "+
		"PRIMARY KEY (dataSetVersionId, keyId, valueId)) "+
		"WITH \"template=replicated\"",
    	};
    }
    
	@PostConstruct
	public void prepareDatabase() {
		if (!appConfig.isCreateTables()) return;
		System.out.println("CREATE DB");
		MapSqlParameterSource params=new MapSqlParameterSource();
		boolean ignite="IGNITE".equals(appConfig.getDatabaseEngine());
		String[] createStatements=ignite?createIgniteCreateStatements():createPostgresCreateStatements();
		for (String sql:createStatements) {
			jdbcTemplate.update(sql, params);
		}
	}

}
