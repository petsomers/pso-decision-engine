package pso.decision_engine.model;

import java.util.List;

import lombok.Data;

@Data
public class ParameterValuesRow {
	private int keyId;
	private List<String> values;
}
