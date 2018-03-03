package pso.decision_engine.model;

import lombok.Data;
import pso.decision_engine.model.enums.DataSetType;

@Data
public class DataSetInfo {
	final String id;
	final String name;
	final DataSetType type;
}
