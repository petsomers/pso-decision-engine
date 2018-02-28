package pso.decision_engine.persistence;

import java.util.List;

import pso.decision_engine.model.enums.DataSetType;

public interface DataSetDao {

	public String getOrCreateDataSetId(String dataSetName, DataSetType dataSetType);

	public String createDataSetVersion(String dataSetId);

	public void setActiveDataSetVersion(String dataSetId, String dataSetVersionId);
	
	public String getActiveDataSetVersionForDataSetName(String dataSetName);

	public List<String> getDataSetNames();

	public void deleteDataSet(String dataSetName);

	public boolean isKeyInDataSet(String dataSetVersionId, String key);

	public void uploadSet(String dataSetVersionId, List<String> values);

}
