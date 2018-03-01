package pso.decision_engine.persistence.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pso.decision_engine.model.AppConfig;
import pso.decision_engine.model.InputParameterInfo;
import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.RuleSetInfo;
import pso.decision_engine.model.UnitTest;
import pso.decision_engine.persistence.RuleSetDao;
import pso.decision_engine.utils.ComparatorHelper;

@Component
public class RuleSetDaoImpl implements RuleSetDao {

	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	private AppConfig appConfig;

    @Autowired
    public void setJdbcTemplate (NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveRuleSet(RuleSet ruleSet) {
    	// avoid @Transactional => don't lock any tables!
    	try {
    		doSaveRuleSet(ruleSet);
    	} catch (Exception e) {
    		try {
    			deleteRuleSet(ruleSet.getRestEndpoint(), ruleSet.getId());
    		} catch (Exception ex) {}
    		throw new RuntimeException(e);
    	}
    }
    
	private void doSaveRuleSet(RuleSet ruleSet) {
		MapSqlParameterSource parameters=new MapSqlParameterSource()
		.addValue("ruleSetId", ruleSet.getId())
		.addValue("restEndpoint", ruleSet.getRestEndpoint())
		.addValue("name", ruleSet.getName())
		.addValue("createdBy", ruleSet.getCreatedBy())
		.addValue("version", ruleSet.getVersion())
		.addValue("remark", ruleSet.getRemark())
		.addValue("uploadDate", Timestamp.valueOf(ruleSet.getUploadDate()));
		
		jdbcTemplate.update(
			"INSERT INTO RuleSet (ruleSetId, restEndpoint, name, createdBy, version, remark, uploadDate) "+
			"values (:ruleSetId, :restEndpoint, :name, :createdBy, :version, :remark, :uploadDate)", parameters);
		
		MapSqlParameterSource[] inputParameters=new MapSqlParameterSource[ruleSet.getInputParameters().size()];
		int i=0;
		for (String parameterName:ruleSet.getInputParameters().keySet()) {
			InputParameterInfo ipi=ruleSet.getInputParameters().get(parameterName);
			inputParameters[i++]=
				new MapSqlParameterSource()
				.addValue("ruleSetId", ruleSet.getId())
				.addValue("parameterName", parameterName)
				.addValue("parameterType", ipi.getType().toString())
				.addValue("defaultValue", ipi.getDefaultValue());
		};
		jdbcTemplate.batchUpdate(
			"INSERT INTO RuleSetParameters (ruleSetId, parameterName, parameterType, defaultValue) "+
			"values (:ruleSetId, :parameterName, :parameterType, :defaultValue)", inputParameters);
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
				.addValue("value2", valueToString(rule.getValue2()))
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
				"INSERT INTO RuleSetLists (ruleSetId, listId, listName) "+
				"values (:ruleSetId, :listId, :listName)", parameters);
			HashSet<String> values=ruleSet.getLists().get(listName);
			saveList(ruleSet.getId(), i, values);
			i++;
		}
		
		MapSqlParameterSource[] unitTests=new MapSqlParameterSource[ruleSet.getUnitTests().size()];
		ArrayList<MapSqlParameterSource> unitTestParameters=new ArrayList<>();
		i=0;
		for (UnitTest unitTest:ruleSet.getUnitTests()) {
			unitTests[i]=
				new MapSqlParameterSource()
				.addValue("ruleSetId", ruleSet.getId())
				.addValue("unitTestId", i)
				.addValue("unitTestName", unitTest.getName())
				.addValue("expectedResult", unitTest.getExpectedResult());
			for (String parameterName:unitTest.getParameters().keySet()) {
				String value=unitTest.getParameters().get(parameterName);
				if (value!=null && !value.isEmpty()) {
					unitTestParameters.add(new MapSqlParameterSource()
					.addValue("ruleSetId", ruleSet.getId())
					.addValue("unitTestId", i)
					.addValue("parameterName", parameterName)
					.addValue("parameterValue", value));
				}
			}
			i++;
		}
		jdbcTemplate.batchUpdate(
			"INSERT INTO RuleSetUnitTests (ruleSetId, unitTestId, unitTestName, expectedResult) "+
			"values (:ruleSetId, :unitTestId, :unitTestName, :expectedResult)", unitTests);
		unitTests=null;
		jdbcTemplate.batchUpdate(
			"INSERT INTO RuleSetUnitTestParameters (ruleSetId, unitTestId, parameterName, parameterValue) "+
			"values (:ruleSetId, :unitTestId, :parameterName, :parameterValue)", unitTestParameters.toArray(new MapSqlParameterSource[0]));
		
		unitTests=null;
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
	public void setActiveRuleSet(String restEndpoint, String ruleSetId) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("restEndpoint", restEndpoint)
		.addValue("ruleSetId", ruleSetId);
		int i=jdbcTemplate.update("update ActiveRuleSet set ruleSetId=:ruleSetId where restEndpoint=:restEndpoint", params);
		if (i==0) {
			jdbcTemplate.update("insert into ActiveRuleSet (restEndpoint, ruleSetId) values (:restEndpoint, :ruleSetId)", params);
		}
	}
	
	@Override
	public RuleSet getRuleSet(String ruleSetId) {
		RuleSet ruleSet=null;
		try {
			ruleSet=jdbcTemplate.queryForObject(
				"select ruleSetId, restEndpoint, name, createdBy, version, remark, uploadDate from RuleSet where ruleSetId=:ruleSetId",
				new MapSqlParameterSource().addValue("ruleSetId", ruleSetId), 
				ruleSetRowMapper);
		} catch (EmptyResultDataAccessException eda) {
			return null;
		}
		ruleSet.setRules(getRules(ruleSet.getId()));
		return ruleSet;
	}
	
	private List<Rule> getRules(String ruleSetId) {
		return jdbcTemplate.query(
			"select ruleNumber, sheetName, rowNumber, rowLabel, parameterName, "+
			"comparator, value1, value2, positiveResult, negativeResult, remark "+
			"from Rule where ruleSetId=:ruleSetId order by ruleNumber", 
			new MapSqlParameterSource().addValue("ruleSetId", ruleSetId),
			(ResultSet rs, int RowNumber) -> {
				Rule rule=new Rule();
				rule.setSheetName(rs.getString("sheetName"));
				rule.setRowNumber(rs.getInt("rowNumber"));
				rule.setLabel(rs.getString("rowLabel"));
				rule.setParameterName(rs.getString("parameterName"));
				rule.setComparator(ComparatorHelper.shortStringToComparator(rs.getString("comparator")));
				rule.setValue1(rs.getString("value1"));
				rule.setValue2(rs.getString("value2"));
				rule.setPositiveResult(rs.getString("positiveResult"));
				rule.setNegativeResult(rs.getString("negativeResult"));
				rule.setRemark(rs.getString("remark"));
				return rule;
			});
	}
	
	@Override
	public String getActiveRuleSetId(String restEndpoint) {
		try {
			return jdbcTemplate.queryForObject(
				"select ruleSetId from ActiveRuleSet where restEndpoint=:restEndpoint", 
				new MapSqlParameterSource().addValue("restEndpoint", restEndpoint), String.class);
		} catch (EmptyResultDataAccessException eda) {
			return null;
		}
	}
	
	@Override
	public Hashtable<String, InputParameterInfo> getRuleSetInputParameters(String ruleSetId) {
		final Hashtable<String, InputParameterInfo> result=new Hashtable<>();
		jdbcTemplate.query(
			"select parameterName, parameterType, defaultValue from RuleSetParameters where ruleSetId=:ruleSetId", 
			new MapSqlParameterSource().addValue("ruleSetId", ruleSetId),
			(ResultSet rs) -> {
				InputParameterInfo ipi=new InputParameterInfo();
				ipi.setType(ComparatorHelper.stringToParameterType(rs.getString("parameterType")));
				ipi.setDefaultValue(rs.getString("defaultValue"));
				result.put(rs.getString(1), ipi);
			});
		return result;
	}
	
	@Override
	public HashMap<String, HashSet<String>> getRuleSetLists(String ruleSetId, boolean loadAll) {
		final HashMap<String, HashSet<String>> result=new HashMap<>();
		jdbcTemplate.query(
			loadAll?
			"select listName, listValue from RuleSetLists as rsl "+
			"left join RuleSetListValues as rslv "+
			"on rslv.ruleSetId=rsl.ruleSetId "+
			"and rslv.listId=rsl.listId "+
			"where rsl.ruleSetId=:ruleSetId "+
			"order by listName, listValue"
			:
			"select listName, listValue from RuleSetLists as rsl "+
			"left join RuleSetListValues as rslv "+
			"on rslv.ruleSetId=rsl.ruleSetId "+
			"and rslv.listId=rsl.listId "+
			"where rsl.ruleSetId=:ruleSetId "+
			"and ("+
			  "select count(*) from RuleSetListValues as rslv2 "+
			  "where rslv2.ruleSetId=rsl.ruleSetId and rslv2.listId=rsl.listId "+
			") < :maxInMemoryListSize "+
			"order by listName, listValue",
			new MapSqlParameterSource().addValue("ruleSetId", ruleSetId).addValue("maxInMemoryListSize", appConfig.getMaxInMemoryListSize()), 
			(ResultSet rs) -> {
				String listName=rs.getString("listName");
				HashSet<String> values=result.get(listName);
				if (values==null) {
					values=new HashSet<>();
					result.put(listName, values);
				}
				values.add(rs.getString("listValue"));
			});
		return result;
	}
	
	@Override
	public List<UnitTest> getRuleSetUnitTests(String ruleSetId) {
		final int[] currentUnitTestId=new int[] {-1};
		final ArrayList<UnitTest> result=new ArrayList<>();
		jdbcTemplate.query(
			"select ut.unitTestId, unitTestName, expectedResult, parameterName, parameterValue from RuleSetUnitTests as ut "+
			"left join RuleSetUnitTestParameters as utp on ut.ruleSetId=utp.ruleSetId and ut.unitTestId=utp.unitTestId "+
			"where ut.ruleSetId=:ruleSetId order by ut.unitTestId",
			new MapSqlParameterSource().addValue("ruleSetId", ruleSetId),
			(ResultSet rs) -> {
				int unitTestId=rs.getInt("unitTestId");
				if (currentUnitTestId[0]!=unitTestId) {
					currentUnitTestId[0]=unitTestId;
					UnitTest unitTest=new UnitTest();
					unitTest.setName(rs.getString("unitTestName"));
					unitTest.setExpectedResult(rs.getString("expectedResult"));
					unitTest.setParameters(new HashMap<>());
					result.add(unitTest);
				}
				result.get(result.size()-1).getParameters().put(rs.getString("parameterName"), rs.getString("parameterValue"));
			});
		return result;
	}
	
	private DecimalFormat df=new DecimalFormat("#.###");
	private String valueToString(Object o) {
		if (o==null) return null;
		if (o instanceof String) return ((String) o).trim();
		if (o instanceof Double) return df.format((Double) o);
		return null;
	}

	@Override
	public boolean doesRuleSetExist(String restEndpoint, String ruleSetId) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("restEndpoint", restEndpoint)
		.addValue("ruleSetId", ruleSetId);
		return jdbcTemplate.queryForObject("select count(*) from RuleSet where ruleSetId=:ruleSetId and restEndpoint=:restEndpoint", params, Integer.class)>0;
	}

	@Override
	public List<String> getAllEndpoints() {
		return jdbcTemplate.query("select distinct restEndpoint from RuleSet", new MapSqlParameterSource(), (ResultSet rs, int rowNumber) -> rs.getString(1));
	}
	
	@Override
	public List<RuleSetInfo> getRuleSetVersionsForEndpoint(String restEndpoint) {
		return jdbcTemplate.query(
			"select rs.ruleSetId, rs.restEndpoint, name, createdBy, version, remark, uploadDate, ars.ruleSetId as active from RuleSet as rs "+
			"left join ActiveRuleSet as ars on ars.ruleSetId=rs.ruleSetId "+
			"where rs.restEndpoint=:restEndpoint order by ruleSetId desc",
			new MapSqlParameterSource().addValue("restEndpoint", restEndpoint),
			ruleSetInfoRowMapper);
	}

	@Override
	public boolean isInList(String ruleSetId, String listName, String value) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("ruleSetId", ruleSetId)
		.addValue("listName", listName)
		.addValue("listValue", value);
		return jdbcTemplate.queryForObject(
			"select count(*) from RuleSetLists as rsl "+
			"left join RuleSetListValues as rslv on rsl.ruleSetId=rslv.ruleSetId and rsl.listId=rslv.listId "+
			"where rsl.ruleSetId=:ruleSetId and rsl.listName=:listName and rslv.listValue=:listValue", 
			params, Integer.class)>0;
	}
	
	
	private RowMapper<RuleSet> ruleSetRowMapper=(ResultSet rs, int rowNumber) -> {
		RuleSet ruleSet=new RuleSet();
		ruleSet.setId(rs.getString("ruleSetId"));
		ruleSet.setName(rs.getString("name"));
		ruleSet.setRestEndpoint(rs.getString("restEndpoint"));
		ruleSet.setCreatedBy(rs.getString("createdBy"));
		ruleSet.setVersion(rs.getString("version"));
		ruleSet.setRemark(rs.getString("remark"));
		ruleSet.setUploadDate(rs.getTimestamp("uploadDate").toLocalDateTime());
		return ruleSet;
	};
	
	private RowMapper<RuleSetInfo> ruleSetInfoRowMapper=(ResultSet rs, int rowNumber) -> {
		RuleSetInfo ruleSet=new RuleSetInfo();
		ruleSet.setId(rs.getString("ruleSetId"));
		ruleSet.setName(rs.getString("name"));
		ruleSet.setRestEndpoint(rs.getString("restEndpoint"));
		ruleSet.setCreatedBy(rs.getString("createdBy"));
		ruleSet.setVersion(rs.getString("version"));
		ruleSet.setRemark(rs.getString("remark"));
		ruleSet.setUploadDate(rs.getTimestamp("uploadDate").toLocalDateTime());
		ruleSet.setActive(rs.getString("active")!=null);
		return ruleSet;
	};

	@Override
	public void deleteRuleSet(String restEndpoint, String ruleSetId) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("restEndpoint", restEndpoint)
		.addValue("ruleSetId", ruleSetId);
		jdbcTemplate.update("DELETE FROM RuleSet WHERE restEndpoint=:restEndpoint and ruleSetId=:ruleSetId" , params);
	}

	@Override
	public void deleteRuleSetsWithEndpoint(String restEndpoint) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("restEndpoint", restEndpoint);
		jdbcTemplate.update("DELETE FROM RuleSet WHERE restEndpoint=:restEndpoint" , params);
	}

	@Override
	public void deleteRuleSetsWithEndpointSkipId(String restEndpoint, String activeId) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("restEndpoint", restEndpoint)
		.addValue("activeId", activeId);
		jdbcTemplate.update("DELETE FROM RuleSet WHERE restEndpoint=:restEndpoint and ruleSetId<>:activeId" , params);
	}

}
