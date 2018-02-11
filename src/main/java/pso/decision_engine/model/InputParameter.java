package pso.decision_engine.model;

import lombok.Data;
import pso.decision_engine.model.enums.ParameterType;

@Data
public class InputParameter {
	final private ParameterType type;
	final private String name; 
}
