package pso.decision_engine.model;

import lombok.Data;

@Data
public class DecisionTraceElement {
	private Rule rule;
	private String result;
}
