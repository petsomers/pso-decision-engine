package pso.decision_engine.model;

import lombok.Data;
import pso.decision_engine.model.enums.ParameterType;

@Data
public class InputParameterInfo {
	private ParameterType type;
	private String defaultValue;
}
