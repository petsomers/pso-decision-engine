package pso.decision_engine.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.DecisionTrace;
import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.enums.Comparator;
import pso.decision_engine.model.enums.ParameterType;

public class RuleSetProcessorServiceImpl {

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.ms");
	
	public DecisionResult runRuleSetWithParameters(RuleSet rs, HashMap<String, String> parameters) {
		DecisionResult result=new DecisionResult();
		DecisionTrace trace=new DecisionTrace();
		result.setTrace(trace);
		trace.setRequestTimestamp(LocalDateTime.now());
		trace.setInputParameters(parameters);
		trace.setRuleId(rs.getId());
		trace.setRestEndPoint(rs.getRestEndPoint());

		final HashMap<String, Object> typedParameters=toTypedParameters(rs, parameters, trace);
		if (typedParameters==null) return result;
		
		Integer ruleNumber=rs.getRowLabels().get("START");
		if (ruleNumber==null) ruleNumber=0; 
		final int ruleCount=rs.getRules().size();
		while (ruleNumber<ruleCount) {
			Rule r=rs.getRules().get(ruleNumber);
			String ruleResult=evaluateRule(rs, r, typedParameters);

		}
		trace.setResponseTimestamp(LocalDateTime.now());
		return result;
	}
	
	private HashMap<String, Object> toTypedParameters(RuleSet rs, HashMap<String, String> parameters, DecisionTrace trace) {
		final HashMap<String, Object> typedParameters=new HashMap<>();
		for (String parameterName:parameters.keySet()) {
			ParameterType type=rs.getInputParameters().get(parameterName);
			if (type==null) {
				trace.getMessages().add("INFO: No type information for parameter "+parameterName);
				continue;
			}
			String value=parameters.get(parameterName).trim();
			if (!value.isEmpty()) {
				switch(type) {
					case TEXT: typedParameters.put(parameterName, value); break;
					case INTEGER: {
						int intValue=0;
						try {
							intValue=Integer.parseInt(value);
						} catch (NumberFormatException nfe) {
							trace.getMessages().add("ERROR: Invalid INTEGER value for parameter "+parameterName+": "+value);
							return null;
						}
						typedParameters.put(parameterName, intValue);
					}; break;
					case DECIMAL: {
						double doubleValue=0d;
						try {
							doubleValue=Double.parseDouble(value);
						} catch (NumberFormatException nfe) {
							trace.getMessages().add("ERROR: Invalid DECIMAL value for parameter "+parameterName+": "+value);
							return null;
						}
						typedParameters.put(parameterName, doubleValue);
						break;
					}
				}
				
			}
		}
		return typedParameters;
	}
	
	private String evaluateRule(RuleSet rs, Rule r, HashMap<String, Object> parameters) {
		ParameterType type=rs.getInputParameters().get(r.getParameterName());
		Object parameterValue=parameters.get(r.getParameterName());
		boolean evalResult=false;
		switch(type) {
			case TEXT: evalResult=compare((String)parameterValue, r.getComparator(), r.getValue1(), r.getValue2());break;
			case INTEGER: evalResult=compare((Integer)parameterValue, r.getComparator(), r.getValue1(), r.getValue2());break;
			case DECIMAL: evalResult=compare((Double)parameterValue, r.getComparator(), r.getValue1(), r.getValue2());break;
		}
		return evalResult?r.getPositiveResult():r.getNegativeResult();
	}
	
	private boolean compare(String parameterValue, Comparator comparator, String value1, String value2) {
		return false;
	}
	private boolean compare(int parameterValue, Comparator comparator, String value1, String value2) {
		return false;
	}
	private boolean compare(double parameterValue, Comparator comparator, String value1, String value2) {
		return false;
	}
}
