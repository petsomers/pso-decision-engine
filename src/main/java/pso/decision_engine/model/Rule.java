package pso.decision_engine.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import pso.decision_engine.model.enums.Comparator;
import pso.decision_engine.utils.JsonSerializerUtils.JsonComparatorDeSerializer;
import pso.decision_engine.utils.JsonSerializerUtils.JsonComparatorSerializer;

@Data
public class Rule {
	private String sheetName;
	private int rowNumber;
	private String label;
	private String parameterName;
	
	@JsonSerialize(using=JsonComparatorSerializer.class)
	@JsonDeserialize(using = JsonComparatorDeSerializer.class)
	private Comparator comparator;
	private String value1;
	private String value2;
	private String positiveResult;
	private String negativeResult;
	private String remark;
}