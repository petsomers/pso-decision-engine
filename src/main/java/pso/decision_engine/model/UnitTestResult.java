package pso.decision_engine.model;

import lombok.Data;

@Data
public class UnitTestResult {
	private String name;
	private boolean passed;
	private String result;
	private String expectedResult;
	private DecisionTrace run;
}
