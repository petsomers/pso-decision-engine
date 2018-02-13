package pso.decision_engine.utils;

import org.springframework.stereotype.Component;

import pso.decision_engine.model.enums.Comparator;

public class ComparatorHelper {
	
	static public Comparator shortStringToComparator(String s) {
		switch(s.toUpperCase()) {
			case "=": return Comparator.EQUAL_TO;
			case "<": return Comparator.SMALLER_THAN;
			case ">": return Comparator.GREATER_THAN;
			case "<=": return Comparator.SMALLER_OR_EQUAL_TO;
			case ">=": return Comparator.GREATER_OR_EQUAL_TO;
			case "BETWEEN": return Comparator.BETWEEN;
			case "IN LIST": return Comparator.IN_LIST;
			case "STARTS WITH": return Comparator.STARTS_WITH;
			case "CONTAINS": return Comparator.CONTAINS;
			case "ENDS WITH": return Comparator.ENDS_WITH;
			default: return null;
			
		}
	}
	
	static public String comparatorToShortString(Comparator c) {
		if (c==null) return null;
		switch(c) {
			case EQUAL_TO: return "=";
			case SMALLER_THAN: return "<";
			case GREATER_THAN: return ">";
			case SMALLER_OR_EQUAL_TO: return "<=";
			case GREATER_OR_EQUAL_TO: return ">=";
			case BETWEEN:return "BETWEEN";
			case IN_LIST: return "IN LIST";
			case STARTS_WITH: return "START WITH";
			case CONTAINS: return "CONTAINS";
			case ENDS_WITH: return "ENDS WITH";
			default: return null;
		}
	}
}
