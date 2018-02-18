package pso.decision_engine.model;

import java.util.List;

import lombok.Data;

@Data
public class UnitTestRunnerResult {
	private boolean allTestsPassed;
	private String errorMessage;
	private List<UnitTestResult> unitTestResults;
}
