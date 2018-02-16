package pso.decision_engine.model;

import lombok.Data;

@Data
public class DecisionResult {

	private boolean error;
	private String errorMessage;
	private DecisionTrace trace;
	
	private String decision;
}
