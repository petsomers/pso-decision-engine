package pso.decision_engine.persistence.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import pso.decision_engine.model.DataSetInfo;
import pso.decision_engine.model.enums.DataSetType;
import pso.decision_engine.persistence.DataSetDao;
import pso.decision_engine.service.IdService;
import reactor.core.publisher.Flux;

@Component
public class DataSetDaoImpl implements DataSetDao {

	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	private IdService idService;
	
	 @Autowired
    public void setJdbcTemplate (NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	
	@Override
	public String getOrCreateDataSetId(String dataSetName, DataSetType dataSetType) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetName", dataSetName)
		.addValue("dataSetType", dataSetType.toString());
		try {
			return jdbcTemplate.queryForObject(
				"select dataSetId from DataSet where name=:dataSetName and type=:dataSetType", 
				params, 
				String.class);
		} catch(EmptyResultDataAccessException emty) {
		}
		String dataSetId=idService.createShortUniqueId();
		
		params.addValue("dataSetId", dataSetId);
		jdbcTemplate.update("insert into DataSet (dataSetId, name, type) values (:dataSetId, :dataSetName, :dataSetType)", params) ;
		return dataSetId;
	}
	
	@Override
	public String createDataSetVersion(String dataSetId) {
		String dataSetVersionId=idService.createShortUniqueId();
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetId", dataSetId)
		.addValue("dataSetVersionId", dataSetVersionId)
		.addValue("uploadDate", Timestamp.valueOf(LocalDateTime.now()));
		jdbcTemplate.update("insert into DataSetVersion (dataSetVersionId, dataSetId, uploadDate) values (:dataSetVersionId, :dataSetId, :uploadDate)", params) ;
		return dataSetVersionId;
	}

	@Override
	public void setActiveDataSetVersion(String dataSetId, String dataSetVersionId) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetId", dataSetId)
		.addValue("dataSetVersionId", dataSetVersionId);
		int c=jdbcTemplate.update("update ActiveDataSetVersion set dataSetVersionId=:dataSetVersionId where dataSetId=:dataSetId", params) ;
		if (c==0)
			jdbcTemplate.update("insert into ActiveDataSetVersion (dataSetId, dataSetVersionId) values (:dataSetId, :dataSetVersionId)", params);
	}
	
	@Override
	public String getActiveDataSetVersionForDataSetName(String dataSetName) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetName", dataSetName);
		try {
			return jdbcTemplate.queryForObject(
				"SELECT a.dataSetVersionId from ActiveDataSetVersion as a "+ 
				"left join DataSet as d on a.dataSetId=d.dataSetId "+
				"where d.name=:dataSetName", 
				params, 
				String.class);
		} catch(EmptyResultDataAccessException emty) {
			return null;
		}
	}
	
	@Override
	public List<DataSetInfo> getDataSetNames() {
		MapSqlParameterSource params=new MapSqlParameterSource();
		return jdbcTemplate.query(
			"SELECT name, type from DataSet order by name", 
			params, 
			(ResultSet rs, int rowNumber) -> {
				DataSetType type="LOOKUP".equals(rs.getString(2))?DataSetType.LOOKUP:DataSetType.SET;
				return new DataSetInfo(rs.getString(1), type);
			});
	}

	@Override
	public void deleteDataSet(String dataSetName) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetName", dataSetName);
		jdbcTemplate.update("delete from DataSet where name=:dataSetName", params);
	}

	@Override
	public boolean isKeyInDataSet(String dataSetVersionId, String key) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetVersionId", dataSetVersionId)
		.addValue("key", key);
		return jdbcTemplate.queryForObject(
			"select count(*) from DataSetKeys where dataSetVersionId=:dataSetVersionId and key=:key",
			params, Integer.class)>0;
	}

	@Override
	public void uploadSet(String dataSetVersionId, Flux<String> in) {
		int[] keyId= {0};
		in.buffer(1000).subscribe( values -> {
			int[] i= {0};
			MapSqlParameterSource[] items=new MapSqlParameterSource[values.size()];
			values.forEach(value -> {
				items[i[0]++]=new MapSqlParameterSource()
					.addValue("dataSetVersionId", dataSetVersionId)
					.addValue("keyId", keyId[0]++)
					.addValue("key", value);
			});
			jdbcTemplate.batchUpdate("INSERT INTO DataSetKeys (dataSetVersionId, keyId, key) values (:dataSetVersionId, :keyId, :key)", items);
		});
	}

	@Override
	public void deleteInactiveDataSetVersions(String dataSetName) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetName", dataSetName);
		jdbcTemplate.update(
			"delete from DataSetVersion where dataSetVersionId not in ( "+
			"SELECT a.dataSetVersionId from ActiveDataSetVersion as a "+ 
			"left join DataSet as d on a.dataSetId=d.dataSetId "+
			"where d.name=:dataSetName)",
			params
		);
	}
	
	@Override
	public List<String> getKeyListFrom(String dataSetVersionId, String fromKey, int max) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetVersionId", dataSetVersionId)
		.addValue("fromKey", fromKey)
		.addValue("max", max);
		return jdbcTemplate.query(
			fromKey==null?
			"select key from DataSetKeys where dataSetVersionId=:dataSetVersionId order by key limit :max"
			:"select key from DataSetKeys where dataSetVersionId=:dataSetVersionId and key>:fromKey order by key limit :max", 
			params,
			(ResultSet rs, int rowNumber) -> rs.getString(1));
	}
}
