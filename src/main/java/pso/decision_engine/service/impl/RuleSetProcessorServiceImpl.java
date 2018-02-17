package pso.decision_engine.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.DecisionTrace;
import pso.decision_engine.model.DecisionTraceElement;
import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.enums.Comparator;
import pso.decision_engine.model.enums.ParameterType;
import pso.decision_engine.service.RuleSetProcessorService;
import pso.decision_engine.service.SetupApiService;

@Service
public class RuleSetProcessorServiceImpl implements RuleSetProcessorService {

	//private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.ms");
	
	@Autowired
	private SetupApiService setupApiService;
	
	@Override
	public DecisionResult runRuleSetWithParameters(RuleSet rs, HashMap<String, String> parameters) {
		DecisionResult result=new DecisionResult();
		DecisionTrace trace=new DecisionTrace();
		result.setTrace(trace);
		trace.setRequestTimestamp(LocalDateTime.now());
		trace.setInputParameters(parameters);
		trace.setRuleId(rs.getId());
		trace.setRestEndPoint(rs.getRestEndPoint());

		final HashMap<String, Object> typedParameters=toTypedParameters(rs, parameters, trace);
		if (typedParameters==null) {
			result.setError(true);
			return result;
		}
		
		Integer ruleNumber=rs.getRowLabels().get("START");
		if (ruleNumber==null) ruleNumber=0; 
		final int ruleCount=rs.getRules().size();
		while (ruleNumber<ruleCount) {
			Rule r=rs.getRules().get(ruleNumber);
			Boolean ruleResult=evaluateRule(rs, r, typedParameters);
			DecisionTraceElement dte=new DecisionTraceElement();
			trace.getTrace().add(dte);
			dte.setRule(r);
			dte.setResult(ruleResult);
			if (ruleResult==null) {
				trace.getMessages().add("ERROR: evaluating rule conditions");
				result.setError(true);
				return result;
			}
			
			String action=ruleResult?r.getPositiveResult():r.getNegativeResult();
			if (action==null || action.isEmpty()) {
				ruleNumber++;
				continue;
			}
			if (action.startsWith("goto ") && action.length()>5) {
				String label=action.substring(5);
				Integer toRuleNumber=rs.getRowLabels().get("START");
				if (toRuleNumber==null) {
					trace.getMessages().add("ERROR: Label not found: "+label);
					result.setError(true);
					return result;
				}
				ruleNumber=toRuleNumber;
				continue;
			}
			result.setDecision(action);
			break;
		}
		trace.setResponseTimestamp(LocalDateTime.now());
		trace.setDurationInMilliSeconds(trace.getResponseTimestamp().toEpochSecond(ZoneOffset.UTC) 
				- trace.getRequestTimestamp().toEpochSecond(ZoneOffset.UTC));
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
	
	private Boolean evaluateRule(RuleSet rs, Rule r, HashMap<String, Object> parameters) {
		ParameterType type=rs.getInputParameters().get(r.getParameterName());
		Object parameterValue=parameters.get(r.getParameterName());
		Boolean evalResult=null;
		if (r.getComparator()==Comparator.IN_LIST) {
			return setupApiService.isInList(rs, String.valueOf(r.getValue1()), String.valueOf(parameterValue));
		}
		if (r.getComparator()==Comparator.NOT_IN_LIST) {
			return !setupApiService.isInList(rs, String.valueOf(r.getValue1()), String.valueOf(parameterValue));
		}
		switch(type) {
			case TEXT: evalResult=compare(rs, (String)parameterValue, r.getComparator(), r.getValue1(), r.getValue2());break;
			case INTEGER: 
				Integer ivalue1=null;
				Integer ivalue2=null;
				try {
					ivalue1=Integer.parseInt(r.getValue1());
					ivalue2=Integer.parseInt(r.getValue2());
				} catch (NumberFormatException nfe) {};
				evalResult=compare(rs, (Integer)parameterValue, r.getComparator(), ivalue1, ivalue2);break;
			case DECIMAL: 
				Double dvalue1=null;
				Double dvalue2=null;
				try {
					dvalue1=Double.parseDouble(r.getValue1());
					dvalue2=Double.parseDouble(r.getValue2());
				} catch (NumberFormatException nfe) {};
				evalResult=compare(rs, (Double)parameterValue, r.getComparator(), dvalue1, dvalue2);break;
		}
		return evalResult;
	}
	
	private Boolean compare(RuleSet rs, String parameterValue, Comparator comparator, String value1, String value2) {
		switch (comparator) {
		case EQUAL_TO: return parameterValue.equals(value1);
		case CONTAINS: return value1.contains(parameterValue);
		case ENDS_WITH: return value1.endsWith(parameterValue);
		case STARTS_WITH: return value1.startsWith(parameterValue);
		case NOT_EQUAL_TO: return !parameterValue.equals(value1);
		case BETWEEN: return parameterValue.compareToIgnoreCase(value1)>=0 && parameterValue.compareToIgnoreCase(value2)<=0; 
		case GREATER_OR_EQUAL_TO: return parameterValue.compareToIgnoreCase(value1)>=0;
		case GREATER_THAN: return parameterValue.compareToIgnoreCase(value1)>0;
		case SMALLER_OR_EQUAL_TO: return parameterValue.compareToIgnoreCase(value1)<=0;
		case SMALLER_THAN: return parameterValue.compareToIgnoreCase(value1)<0;
		}
		return null;
	}
	
	private Boolean compare(RuleSet rs, int parameterValue, Comparator comparator, Integer value1, Integer value2) {
		if (value1==null) return null;
		switch (comparator) {
		case EQUAL_TO: return parameterValue==value1;
		case NOT_EQUAL_TO: return parameterValue!=value1;
		case GREATER_OR_EQUAL_TO: return parameterValue>=value1;
		case GREATER_THAN: return parameterValue>value1;
		case SMALLER_OR_EQUAL_TO: return parameterValue<=value1;
		case SMALLER_THAN: return parameterValue<value1;
		
		case BETWEEN: 
			if (value2==null) return null;
			return parameterValue>=value1 && parameterValue<=value2;
			
		case CONTAINS:
		case ENDS_WITH:
		case STARTS_WITH:
		default: return null;
		}
	}
	
	private Boolean compare(RuleSet rs, double parameterValue, Comparator comparator, Double value1, Double value2) {
		if (value1==null) return null;
		switch (comparator) {
		case EQUAL_TO: return parameterValue==value1;
		case NOT_EQUAL_TO: return parameterValue!=value1;
		case GREATER_OR_EQUAL_TO: return parameterValue>=value1;
		case GREATER_THAN: return parameterValue>value1;
		case SMALLER_OR_EQUAL_TO: return parameterValue<=value1;
		case SMALLER_THAN: return parameterValue<value1;
		
		case BETWEEN: 
			if (value2==null) return null;
			return parameterValue>=value1 && parameterValue<=value2;
			
		case CONTAINS:
		case ENDS_WITH:
		case STARTS_WITH:
		default: return null;
		}
	}
}
