package pso.decision_engine.persistence.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteSet;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import pso.decision_engine.model.InputParameterInfo;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.RuleSetInfo;
import pso.decision_engine.model.UnitTest;
import pso.decision_engine.model.ignitedto.RuleSetDto;
import pso.decision_engine.persistence.RuleSetDao;

//@Repository("igniteRuleSetDao")
public class IgniteRuleSetDaoImpl implements RuleSetDao {
	
	@Autowired
	private Ignite ignite;
	
	private IgniteCache<String, RuleSetDto> ruleSetCache;
	IgniteCache<String, String> activeEndPointsCache;
	
	private CollectionConfiguration setCfg = 
		new CollectionConfiguration()
		.setCollocated(true);
		//.setBackups(1);

	@PostConstruct
	public void createCaches() {
		ignite.cache("ruleSets").close();
		ignite.cache("activeEndPoints").close();
		
		CacheConfiguration<String, RuleSetDto> ruleSetCacheConfig = new CacheConfiguration<>("ruleSets2");
        ruleSetCacheConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ruleSetCacheConfig.setIndexedTypes(String.class, RuleSetDto.class);
        ruleSetCacheConfig.setSqlSchema("PUBLIC");
        ruleSetCache=ignite.getOrCreateCache(ruleSetCacheConfig);
        
        CacheConfiguration<String, String> activeEndPointsCacheConfig = new CacheConfiguration<>("activeEndPoints2");
        activeEndPointsCacheConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        activeEndPointsCacheConfig.setIndexedTypes(String.class, String.class);
        activeEndPointsCacheConfig.setSqlSchema("PUBLIC");
        activeEndPointsCache=ignite.getOrCreateCache(activeEndPointsCacheConfig);
	}

	@Override
	public void saveRuleSet(RuleSet ruleSet) {
		RuleSetDto ruleSetDto=new RuleSetDto(ruleSet);
		ruleSetCache.put(ruleSet.getId(), ruleSetDto);
		
		ruleSet.getLists().forEach((name, items) -> {
			IgniteSet<String> set = ignite.set(ruleSet.getId()+"-"+name, setCfg);
			items.forEach(item -> set.add(item));
		});
	}

	@Override
	public boolean doesRuleSetExist(String restEndpoint, String ruleSetId) {
		RuleSetDto r=ruleSetCache.get(ruleSetId);
		if (r==null) return false;
		return restEndpoint.equals(r.getRestEndpoint());
	}

	@Override
	public void setActiveRuleSet(String restEndpoint, String ruleSetId) {
		activeEndPointsCache.put(restEndpoint, ruleSetId);
	}

	@Override
	public RuleSet getRuleSet(String ruleSetId) {
		RuleSetDto rsdto=ruleSetCache.get(ruleSetId);
		if (rsdto==null) return null;

		RuleSet rs=new RuleSet();
		rs.setId(rsdto.getId());
		rs.setName(rsdto.getName());
		rs.setCreatedBy(rsdto.getCreatedBy());
		rs.setRemark(rsdto.getRemark());
		rs.setRestEndpoint(rsdto.getRestEndpoint());
		rs.setInputParameters(rsdto.getInputParameters());
		rs.setRowLabels(rsdto.getRowLabels());
		rs.setRules(rsdto.getRules());
		rs.setUnitTests(rsdto.getUnitTests());
		rs.setUploadDate(rsdto.getUploadDate());
		rs.setVersion(rsdto.getVersion());
		return rs;
	}

	@Override
	public String getActiveRuleSetId(String restEndpoint) {
		return activeEndPointsCache.get(restEndpoint);
	}

	@Override
	public HashMap<String, Set<String>> getRuleSetLists(String ruleSetId, boolean loadAll) {
		HashMap<String, Set<String>> result=new HashMap<>();
		if (!loadAll) {
			// all or nothing
			return result;
		}
		RuleSetDto rsdto=ruleSetCache.get(ruleSetId);
		if (rsdto==null) return result;
		rsdto.getLists().forEach((listName) -> {
			IgniteSet<String> set=ignite.set(ruleSetId+"-"+listName, setCfg);
			Set<String> listItems=set;
			result.put(listName, listItems);
		});
		return result;
	}

	@Override
	public List<String> getAllEndpoints() {
		final ArrayList<String> result=new ArrayList<>();
		SqlFieldsQuery sql = new SqlFieldsQuery("select distinct restEndpoint from ruleSetdto");
		try (QueryCursor<List<?>> cursor = ruleSetCache.query(sql)) {
			for (List<?> row : cursor) {
				result.add((String)row.get(0));
			}
		}
		return result;
	}

	@Override
	public boolean isInList(String ruleSetId, String listName, String value) {
		IgniteSet<String> set=ignite.set(ruleSetId+"-"+listName, setCfg);
		if (set==null)  {
			return false;
		}
		if (set.size()==0) {
			set.close(); // removes this set
			return false;
		}
		return set.contains(value);
	}

	@Override
	public List<RuleSetInfo> getRuleSetVersionsForEndpoint(String restEndpoint) {
		final ArrayList<RuleSetInfo> result=new ArrayList<>();
		final String ativeId=getActiveRuleSetId(restEndpoint);
		SqlFieldsQuery sql = new SqlFieldsQuery("select id,  restEndpoint, name, createdBy, version, remark, uploadDate  from ruleSetdto");
		try (QueryCursor<List<?>> cursor = ruleSetCache.query(sql)) {
			for (List<?> row : cursor) {
				RuleSetInfo rsi=new RuleSetInfo();
				rsi.setId((String)row.get(0));
				rsi.setRestEndpoint((String)row.get(1));
				rsi.setName((String)row.get(2));
				rsi.setCreatedBy((String)row.get(3));
				rsi.setVersion((String)row.get(4));
				rsi.setRemark((String)row.get(5));
				rsi.setUploadDate((LocalDateTime) row.get(5));
				rsi.setActive(rsi.getId().equals(ativeId));
				result.add(rsi);
			}
		}
		return result;
	}

	@Override
	public Hashtable<String, InputParameterInfo> getRuleSetInputParameters(String ruleSetId) {
		// not needed when using Ignite
		return null;
	}

	@Override
	public List<UnitTest> getRuleSetUnitTests(String ruleSetId) {
		// not needed when using Ignite
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
