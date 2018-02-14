package pso.decision_engine.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.enums.Comparator;
import pso.decision_engine.model.enums.ParameterType;

public class RuleSetProcessorServiceImpl {

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.ms");
	
	public DecisionResult runRuleSetWithParameters(RuleSet rs, HashMap<String, String> parameters) {
		DecisionResult result=new DecisionResult();
		final ArrayList<String> trace=new ArrayList<>();
		result.setTrace(trace);
		trace.add(rs.getName()+"(id: "+rs.getId()+")");
		trace.add("Parameters: "+parameters);
		trace.add("start "+ dateTimeFormatter.format(LocalDateTime.now()));
		final HashMap<String, Object> typedParameters=toTypedParameters(rs, parameters);
		
		Integer ruleNumber=rs.getRowLabels().get("START");
		if (ruleNumber==null) ruleNumber=0; 
		final int ruleCount=rs.getRules().size();
		while (ruleNumber<ruleCount) {
			Rule r=rs.getRules().get(ruleNumber);
			String ruleResult=evaluateRule(rs, r, typedParameters);
		}
		
		return result;
	}
	
	private HashMap<String, Object> toTypedParameters(RuleSet rs, HashMap<String, String> parameters, final ArrayList<String> trace) {
		final HashMap<String, Object> typedParameters=new HashMap<>();
		for (String parameterName:parameters.keySet()) {
			ParameterType type=rs.getInputParameters().get(parameterName);
			if (type==null) {
				trace.add("No type information for parameter "+parameterName);
				continue;
			}
			
			
		}
		
		return typedParameters;
	}
	
	private String evaluateRule(RuleSet rs, Rule r, HashMap<String, Object> parameters) {
		
		
		
		return null;
	}
	
	private boolean compare(String parameterValue, Comparator comparator, String value1, String value2) {
		return false;
	}
	private boolean compare(int parameterValue, Comparator comparator, int value1, int value2) {
		return false;
	}
	private boolean compare(double parameterValue, Comparator comparator, double value1, double value2) {
		return false;
	}
}