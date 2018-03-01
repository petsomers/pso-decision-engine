package pso.decision_engine.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pso.decision_engine.model.DataSetName;
import pso.decision_engine.model.DataSetUploadResult;

public interface DataSetService {
	
	public List<DataSetName> getDataSetNames();

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
