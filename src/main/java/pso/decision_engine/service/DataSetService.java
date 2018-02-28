package pso.decision_engine.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pso.decision_engine.model.DataSetUploadResult;
import pso.decision_engine.model.enums.DataSetType;

public interface DataSetService {
	
	public String getOrCreateDataSetId(String dataSetName, DataSetType dataSetType);

	public String createDataSetVersion(String dataSetId);

	public void setActiveDataSetVersion(String dataSetId, String dataSetVersionId);
	
	public String getActiveDataSetVersionForDataSetName(String dataSetName);

	public List<String> getDataSetNames();

	public void deleteDataSet(String dataSetName);

	public boolean isKeyInDataSet(String dataSetVersionId, String key);

	/**
	 * upload the data in a new dataset version
	 * when successful => this dataset will be made active
	 * 
	 * @param dataSetName
	 * @param in
	 * @return dataSetVersionId
	 * @throws IOException
	 */
	public DataSetUploadResult uploadSet(String dataSetName, InputStream in) throws IOException;
	
}
