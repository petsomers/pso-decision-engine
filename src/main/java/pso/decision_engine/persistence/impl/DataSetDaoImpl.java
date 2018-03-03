package pso.decision_engine.persistence.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import pso.decision_engine.model.DataSetInfo;
import pso.decision_engine.model.ParameterValuesRow;
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
	public DataSetInfo getDataSetInfo(String dataSetName) {
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetName", dataSetName);
		try {
			return jdbcTemplate.queryForObject(
				"SELECT dataSetId, name, type from DataSet where name=:dataSetName limit 1", 
				params, 
				dataSetInfoRowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	 
	@Override
	public String createDataSet(String dataSetName, DataSetType dataSetType) {
		String dataSetId=idService.createShortUniqueId();
		MapSqlParameterSource params=new MapSqlParameterSource()
		.addValue("dataSetId", dataSetId)
		.addValue("dataSetName", dataSetName)
		.addValue("dataSetType", dataSetType.toString());
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
				"where d.name=:dataSetName limit 1", 
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
			"SELECT dataSetId, name, type from DataSet order by name", 
			params, 
			dataSetInfoRowMapper);
	}
	
	private RowMapper<DataSetInfo> dataSetInfoRowMapper=(ResultSet rs, int rowNumber) -> {
		DataSetType type=DataSetType.fromString(rs.getString(3));
		return new DataSetInfo(rs.getString(1), rs.getString(2), type);
	};

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
	public void uploadDataSetKeys(String dataSetVersionId, Flux<String> in) {
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
			"  SELECT a.dataSetVersionId from ActiveDataSetVersion as a "+ 
			"  left join DataSet as d on a.dataSetId=d.dataSetId "+
			"  where d.name=:dataSetName) "+
			"and dataSetId in (select d2.dataSetId from DataSet as d2 where d2.name=:dataSetName)",
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
			"select key from DataSetKeys where dataSetVersionId=:dataSetVersionId order by keyId limit :max"
			:"select key from DataSetKeys where dataSetVersionId=:dataSetVersionId and key>:fromKey order by keyId limit :max", 
			params,
			(ResultSet rs, int rowNumber) -> rs.getString(1));
	}

	@Override
	public Flux<String> streamKeysFromActiveDataSet(String dataSetVersionId) {
		return Flux.<String>create(emitter -> {
			MapSqlParameterSource params=new MapSqlParameterSource()
			.addValue("dataSetVersionId", dataSetVersionId);
			jdbcTemplate.query(
				"select key from DataSetKeys where dataSetVersionId=:dataSetVersionId order by keyId",
				params,
				(ResultSet rs) -> {emitter.next(rs.getString(1));});
			emitter.complete();
		});
	}
	
	@Override
	public void saveDataSetParameterNames(String dataSetVersionId, List<String> parameterNames) {
		MapSqlParameterSource[] items=new MapSqlParameterSource[parameterNames.size()];
		int[] parameterId= {0};
		parameterNames.forEach(name -> {
			items[parameterId[0]]=new MapSqlParameterSource()
				.addValue("dataSetVersionId", dataSetVersionId)
				.addValue("valueId", parameterId[0]++)
				.addValue("name", name);
		});
		jdbcTemplate.batchUpdate("INSERT INTO DataSetValueNames (dataSetVersionId, valueId, name) values (:dataSetVersionId, :valueId, :name)", items);
	}

	@Override
	public void uploadDataSetValues(String dataSetVersionId, Flux<ParameterValuesRow> in) {
		in.buffer(100).subscribe( rows -> {
			ArrayList<MapSqlParameterSource> dbparams=new ArrayList<>();
			rows.forEach(row -> {
				int[] valueId= {1}; // start from 1.
				row.getValues().forEach(value -> {
					dbparams.add(new MapSqlParameterSource()
					.addValue("dataSetVersionId", dataSetVersionId)
					.addValue("keyId", row.getKeyId())
					.addValue("valueId", valueId[0]++)
					.addValue("value", value));
				});
		
			});
			jdbcTemplate.batchUpdate("INSERT INTO DataSetValues (dataSetVersionId, keyId, valueId, value) values (:dataSetVersionId, :keyId, :valueId, :value)", dbparams.toArray(new MapSqlParameterSource[0]));
		});
		
	}
}
