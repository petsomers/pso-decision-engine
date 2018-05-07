package pso.decision_engine.model;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Data;

@Data
public class UnitTest  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private HashMap<String, String> parameters;
	private String expectedResult;
}
