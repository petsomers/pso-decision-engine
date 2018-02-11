package pso.decision_engine.model;

import lombok.Data;
import pso.decision_engine.model.enums.ParameterType;

@Data
public class InputParameterValue {
	private ParameterType type;
	/**
	 * String, Long or Double
	 */
	private Object value; 
}
