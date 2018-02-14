package pso.decision_engine.model;

import java.util.HashMap;

import lombok.Data;

@Data
public class UnitTest {
	private String name;
	private HashMap<String, String> parameters;
	private String expectedResult;
}
