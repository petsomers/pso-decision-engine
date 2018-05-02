package pso.decision_engine.persistence.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import pso.decision_engine.config.AppConfig;
import pso.decision_engine.model.InputParameterInfo;
import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.RuleSetInfo;
import pso.decision_engine.model.UnitTest;
import pso.decision_engine.persistence.RuleSetDao;
import pso.decision_engine.utils.JsonSerializerUtils.JsonLocalDateTimeDeSerializer;
import pso.decision_engine.utils.JsonSerializerUtils.JsonLocalDateTimeSerializer;
import reactor.core.publisher.Flux;


// WIP
public class RuleSetDaoObjectStoreImpl implements RuleSetDao {
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	private AppConfig appConfig;

    @Autowired
    public void setJdbcTemplate (NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    ObjectMapper mapper=new ObjectMapper();
	{
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	@Data
	static class RuleSetDto {
		
		private String name;
		private String id;
		private String restEndpoint;
		private String createdBy;
		private String version;
		private String remark;

		@JsonSerialize(using=JsonLocalDateTimeSerializer.class)
		@JsonDeserialize(using = JsonLocalDateTimeDeSerializer.class)
		private LocalDateTime uploadDate;
		private Hashtable<String, InputParameterInfo> inputParameters=new Hashtable<>();
		private List<Rule> rules=new ArrayList<>();
		
		private Hashtable<String, Integer> rowLabels=new Hashtable<>();
		
		private List<UnitTest> unitTests;
		
		private HashMap<String, Integer> lists=new HashMap<>();
		
		public RuleSetDto() {}
		
		public RuleSetDto(RuleSet rs) {
			this.name=rs.getName();
			this.id=rs.getId();
			this.restEndpoint=rs.getRestEndpoint();
			this.createdBy=rs.getCreatedBy();
			this.version=rs.getVersion();
			this.remark=rs.getRemark();
			
			this.uploadDate=rs.getUploadDate();
			this.inputParameters=rs.getInputParameters();
			this.rules=rs.getRules();
			this.rowLabels=rs.getRowLabels();
			this.unitTests=rs.getUnitTests();
			if (rs.getLists()!=null && rs.getLists().size()>0) {
				int listId=0;
				for (String list:rs.getLists().keySet()) {
					this.lists.put(list, listId++);
				}
			}
		}
	}
	
	@Override
	public void saveRuleSet(RuleSet ruleSet) {
		RuleSetDto rsdto=new RuleSetDto(ruleSet);
		String json;
		try {
			json = mapper.writeValueAsString(rsdto);
		} catch (JsonProcessingException e) {throw new RuntimeException("Error storing ruleset "+ruleSet, e);}
		jdbcTemplate.update(
			"INSERT INTO JsonRuleSet (ruleSetId, jsonvalue) values (:ruleSetId, :jsonvalue::JSON)", 
			new MapSqlParameterSource()
			.addValue("ruleSetId", rsdto.getId())
			.addValue("jsonvalue", json));
		jdbcTemplate.update(
			"INSERT INTO JsonRuleSetEndPoint (ruleSetId, restEndpoint) values (:ruleSetId, :restEndpoint)", 
			new MapSqlParameterSource()
			.addValue("ruleSetId", rsdto.getId())
			.addValue("restEndpoint", rsdto.getRestEndpoint()));
		
		for (String listName:ruleSet.getLists().keySet()) {
			int listId=rsdto.getLists().get(listName);
			HashSet<String> values=ruleSet.getLists().get(listName);
			Flux.fromIterable(values).buffer(1000).subscribe( valueList -> {
				MapSqlParameterSource[] batch=new MapSqlParameterSource[valueList.size()];
				int[] j= {0};
				valueList.forEach(value -> {
					batch[j[0]++]=
						new MapSqlParameterSource()
						.addValue("ruleSetId", ruleSet.getId())
						.addValue("listId", listId)
						.addValue("listValue", value);
				});
				jdbcTemplate.batchUpdate(
					"INSERT INTO JsonRuleSetListValues (ruleSetId, listId, listValue) "+
					"values (:ruleSetId, :listId, :listValue)", batch);
			});
		}
				
	}

	@Override
	public boolean doesRuleSetExist(String restEndpoint, String ruleSetId) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("restEndpoint", restEndpoint)
		.addValue("ruleSetId", ruleSetId);
		return jdbcTemplate.queryForObject("select count(*) from JsonRuleSetEndPoint where ruleSetId=:ruleSetId and restEndpoint=:restEndpoint", params, Integer.class)>0;
	}

	@Override
	public void setActiveRuleSet(String restEndpoint, String ruleSetId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RuleSet getRuleSet(String ruleSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getActiveRuleSetId(String restEndpoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashSet<String>> getRuleSetLists(String ruleSetId, boolean loadAll) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllEndpoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInList(String ruleSetId, String listName, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<RuleSetInfo> getRuleSetVersionsForEndpoint(String restEndpoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hashtable<String, InputParameterInfo> getRuleSetInputParameters(String ruleSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UnitTest> getRuleSetUnitTests(String ruleSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRuleSet(String restEndpoint, String ruleSetId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteRuleSetsWithEndpoint(String restEndpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteRuleSetsWithEndpointSkipId(String restEndpoint, String activeId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveRuleSetSource(String ruleSetId, int contentLength, InputStream inputStream) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void streamRuleSetSource(String ruleSetId, OutputStream outputStream) {
		// TODO Auto-generated method stub
		
	}

}
