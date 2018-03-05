package pso.decision_engine.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pso.decision_engine.model.DataSetInfo;
import pso.decision_engine.model.DataSetUploadResult;
import pso.decision_engine.model.ScrollItems;
import pso.decision_engine.model.enums.DataSetType;
import reactor.core.publisher.Flux;

public interface DataSetService {
	
	public List<DataSetInfo> getDataSetNames();

	public void deleteDataSet(String dataSetName);

	public boolean isKeyInDataSet(String dataSetName, String key);

	/**
	 * upload the data in a new dataset version
	 * when successful => this dataset will be made active
	 * 
	 * @param dataSetName
	 * @param in
	 * @return dataSetVersionId
	 * @throws IOException
	 */
	public DataSetUploadResult uploadDataSet(String dataSetName, DataSetType dataSetType, InputStream in) throws IOException;
	
	public ScrollItems<String> getKeysFromActiveDataSet(String dataSetName, String fromKey, int max);
	
	public Flux<String> streamKeysFromActiveDataSet(String dataSetName);

	public List<String> getParameterNamesForActiveDataSet(String dataSetName);
	
	public ScrollItems<String[]> getRowsForActiveLookupDataSet(String dataSetName, String fromKey, int max);

}
