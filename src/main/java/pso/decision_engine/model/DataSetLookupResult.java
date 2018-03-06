package pso.decision_engine.model;

import java.util.HashMap;

import lombok.Data;

@Data
public class DataSetLookupResult {
	private boolean dataSetFound;
	private boolean keyFound;
	private HashMap<String, String> values;
}
