package pso.decision_engine.model;

import lombok.Data;

@Data
public class ExcelParseResult {
	private boolean ok;
	private String errorMessage;
	private String ruleSetId;
	private String restEndPoint;
}
