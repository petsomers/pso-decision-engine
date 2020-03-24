package pso.decision_engine.model;

import java.io.Serializable;

import lombok.Data;
import pso.decision_engine.model.enums.ParameterType;

@Data
public class InputParameterInfo  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ParameterType type;
	private String defaultValue;
	private int seqNr;
}
