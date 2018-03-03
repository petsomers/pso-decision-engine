package pso.decision_engine.model.enums;

public enum DataSetType {
	LIST, LOOKUP;
	
	static public DataSetType fromString(String value) {
		if (value==null) return null;
		switch(value.toUpperCase()) {
			case "LIST": return LIST;
			case "LOOKUP": return LOOKUP;
			default: return null;
		}
	}
	
}
