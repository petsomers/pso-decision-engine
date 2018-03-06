package pso.decision_engine.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DecisionTraceElement {
	private Rule rule;
	private String parameterValue;
	private String result;
	private List<String> info=new ArrayList<>();
}
