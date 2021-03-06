package pso.decision_engine.persistence;

import java.util.HashMap;
import java.util.List;

import pso.decision_engine.model.DataSetInfo;
import pso.decision_engine.model.ParameterValuesRow;
import pso.decision_engine.model.enums.DataSetType;
import reactor.core.publisher.Flux;

public interface DataSetDao {

	public DataSetInfo getDataSetInfo(String dataSetName);
	
	public String createDataSet(String dataSetName, DataSetType dataSetType);
	
	public String createDataSetVersion(String dataSetId);

	public void setActiveDataSetVersion(String dataSetId, String dataSetVersionId);
	
	public String getActiveDataSetVersionForDataSetName(String dataSetName);

	public List<DataSetInfo> getDataSetNames();

	public void deleteDataSet(String dataSetName);

	public boolean isKeyInDataSet(String dataSetVersionId, String key);

	
	public void uploadDataSetKeys(String dataSetVersionId, Flux<String> in);
	
	
	public void deleteInactiveDataSetVersions(String dataSetName);

	public List<String> getKeyListFrom(String dataSetVersionId, String fromKey, int max);

	public Flux<String> streamDataSetKeys(String dataSetVersionId);

	public void saveDataSetParameterNames(String dataSetVersionId, List<String> parameterNames);
	
	public List<String> getDataSetParameterNames(String dataSetVersionId);
	
	// public void uploadDataSetKeysAndValues(String dataSetVersionId, int valuesPerRow, Flux<String> in);
	
	public void uploadDataSetKeysAndValues(String dataSetVersionId, Flux<ParameterValuesRow> in);

	public List<String[]> getDataSetValues(String versionId, int valuesPerRow, String fromKey, int max);

	public Flux<String[]> streamDataSetRows(String dataSetVersionId, int valuesPerRow);

	public HashMap<String, String> getDataSetLookupRow(String dataSetVersionId, String key);

}
