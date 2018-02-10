package pso.decision_engine.model;

import java.util.List;

import lombok.Data;

@Data
public class UnitTest {
	private String name;
	private List<InputParameter> parameters;
	private String expectedResult;
}
