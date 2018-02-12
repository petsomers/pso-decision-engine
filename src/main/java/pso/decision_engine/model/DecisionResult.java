package pso.decision_engine.model;

import java.util.List;

import lombok.Data;

@Data
public class DecisionResult {

	private boolean error;
	private String errorMessage;
	private List<String> trace;
	
	private String decision;
}
