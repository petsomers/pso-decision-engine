package pso.decision_engine.model;

import lombok.Data;

@Data
public class DataSetUploadResult {
	private boolean ok;
	private String errorMessage;
	private String dataSetName;
	private String dataSetVersionId;
}
