package pso.decision_engine.model;

import lombok.Data;
import pso.decision_engine.model.enums.Comparator;

@Data
public class Rule {
	private String sheetName;
	private int rowNumber;
	private String label;
	private String parameterName;
	private Comparator comparator;
	private Object value1;
	private Object value2;
	private String positiveResult;
	private String negativeResult;
	private String remark;
}