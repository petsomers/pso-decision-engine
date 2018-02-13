package pso.decision_engine.persistence.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RuleSetDaoImpl {
	
	private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate (NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static String createTables[]=
	{
		"CREATE TABLE IF NOT EXISTS RuleSet ("+
		"ruleSetId varchar(20) NOT NULL, "+
		"restEndPoint varchar(20) NOT NULL, "+
		"createdBy varchar(50), "+
		"version varchar(20), "+
		"uploadDate TIMESTAMP NOT NULL, "+
		"PRIMARY KEY (ruleSetId))",

		"CREATE TABLE IF NOT EXISTS RuleSetParameters ("+
		"ruleSetId varchar(20) NOT NULL, "+
		"parameterName varchar(40) NOT NULL, "+
		"parameterType varchar(10) NOT NULL, "+
		"PRIMARY KEY (ruleSetId, parameterName))",
		
		"CREATE TABLE IF NOT EXISTS Rule ("+
		"ruleSetId varchar(20) NOT NULL, "+
		"ruleNumber INTEGER NOT NULL, "+
		"sheetName VARCHAR(100), "+
		"rowNumber INTEGER NOT NULL, "+
		"rowLabel VARCHAR(100), "+
		"parameterName varchar(40), "+
		"comparator varchar(15), "+
		"value1 varchar(100), "+
		"value2 varchar(100), "+
		"positiveResult varchar(200), "+
		"negativeResult varchar(200), "+
		"remark varchar(500), "+
		"PRIMARY KEY (ruleSetId, ruleNumber))"
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
