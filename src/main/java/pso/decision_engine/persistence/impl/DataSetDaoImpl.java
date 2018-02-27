package pso.decision_engine.persistence.impl;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pso.decision_engine.persistence.DataSetDao;

@Component
public class DataSetDaoImpl implements DataSetDao {

	private NamedParameterJdbcTemplate jdbcTemplate;
	
	 @Autowired
    public void setJdbcTemplate (NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	
	@Override
	public int getOrCreateListId(String listName) {
		// outside transaction! // atomic!
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("listName", listName);
		try {
			return jdbcTemplate.queryForObject(
				"select listId from lists where listName=:listName", 
				params, 
				Integer.class);
		} catch(EmptyResultDataAccessException emty) {
		}
		Integer listId=jdbcTemplate.queryForObject(
				"select max(listId) from Lists", 
				params, Integer.class);
		if (listId==null) listId=1;
		
		params.addValue("listId", listId);
		jdbcTemplate.update("insert into Lists (listId, listName) values (:listId, :listName)", params) ;
		return listId;
	}
	
	@Override
	@Transactional
	public void uploadList(int listId, List<String> values) {
		// maybe use reactive stream in batchs of x, instead of 1 shot?
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("listId", listId);
		jdbcTemplate.update("DELETE FROM ListValues where listId=:listId", params);
		
		MapSqlParameterSource[] items=new MapSqlParameterSource[values.size()];
		int[] i= {0};
		values.forEach(value -> {
			items[i[0]++]=new MapSqlParameterSource()
				.addValue("listId", listId)
				.addValue("value", value);
		});
		jdbcTemplate.batchUpdate("INSERT INTO ListValues (listId, value) values (:listId, :value)", items);
	}
	
	@Override
	public List<String> getListNames() {
		MapSqlParameterSource params=new MapSqlParameterSource();
		return jdbcTemplate.query(
			"SELECT distinct listName from Lists order by listName", 
			params, 
			(ResultSet rs, int rowNumber) -> rs.getString(1));
	}
	
	@Override
	public void deleteList(String listName) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("listName", listName);
		jdbcTemplate.update("delete from Lists where listName=:listName", params);
	}

	@Override
	public boolean isInList(String listName, String value) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("listName", listName)
		.addValue("listValue", value);
		return jdbcTemplate.queryForObject(
			"select count(*) from Lists as l "+
			"left join ListValues as lv on l.listId=lv.listId "+
			"where l.listName=:listName and lv.listValue=:listValue", 
			params, Integer.class)>0;
	}
}
