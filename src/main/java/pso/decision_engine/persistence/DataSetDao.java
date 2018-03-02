package pso.decision_engine.persistence;

import java.util.List;
import java.util.stream.Stream;

import pso.decision_engine.model.DataSetInfo;
import pso.decision_engine.model.enums.DataSetType;
import reactor.core.publisher.Flux;

public interface DataSetDao {

	public String getOrCreateDataSetId(String dataSetName, DataSetType dataSetType);

	public String createDataSetVersion(String dataSetId);

	public void setActiveDataSetVersion(String dataSetId, String dataSetVersionId);
	
	public String getActiveDataSetVersionForDataSetName(String dataSetName);

	public List<DataSetInfo> getDataSetNames();

	public void deleteDataSet(String dataSetName);

	public boolean isKeyInDataSet(String dataSetVersionId, String key);

	//public void uploadSet(String dataSetVersionId, List<String> values);
	
	public void uploadSet(String dataSetVersionId, Flux<String> in);
	
	public void deleteInactiveDataSetVersions(String dataSetName);

	public List<String> getKeyListFrom(String dataSetVersionId, String fromKey, int max);

}
