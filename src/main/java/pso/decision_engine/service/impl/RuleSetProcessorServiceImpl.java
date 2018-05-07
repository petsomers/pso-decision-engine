package pso.decision_engine.service.impl;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pso.decision_engine.model.DataSetLookupResult;
import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.DecisionTrace;
import pso.decision_engine.model.DecisionTraceElement;
import pso.decision_engine.model.InputParameterInfo;
import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.UnitTestResult;
import pso.decision_engine.model.UnitTestRunnerResult;
import pso.decision_engine.model.enums.Comparator;
import pso.decision_engine.model.enums.ParameterType;
import pso.decision_engine.service.DataSetService;
import pso.decision_engine.service.RuleSetProcessorService;
import pso.decision_engine.service.SetupApiService;

@Service
public class RuleSetProcessorServiceImpl implements RuleSetProcessorService {

	@Autowired
	private SetupApiService setupApiService;
	
	@Autowired
	private DataSetService dataSetService;
	
	@Override
	public DecisionResult runRuleSetWithParameters(RuleSet rs, HashMap<String, String> parameters) {
		long startnano=System.nanoTime();
		DecisionResult result=new DecisionResult();
		DecisionTrace trace=new DecisionTrace();
		result.setRun(trace);
		trace.setRequestTimestamp(LocalDateTime.now());
		trace.setInputParameters(parameters);
		trace.setRuleSetId(rs.getId());
		trace.setRestEndPoint(rs.getRestEndpoint());

		final HashMap<String, Object> typedParameters=toTypedParameters(rs, parameters, trace);
		if (typedParameters==null) {
			result.setError(true);
			result.setErrorMessage(trace.getMessages().get(trace.getMessages().size()-1));
			return result;
		}
		
		HashSet<Integer> executedRules=new HashSet<>();
		Integer ruleNumber=rs.getRowLabels().get("START");
		if (ruleNumber==null) ruleNumber=0; 
		final int ruleCount=rs.getRules().size();
		while (ruleNumber<ruleCount) {
			Rule r=rs.getRules().get(ruleNumber);
			DecisionTraceElement dte=new DecisionTraceElement();
			trace.getTrace().add(dte);
			dte.setRule(r);
			if (executedRules.contains(ruleNumber)) {
				trace.getMessages().add("ERROR: rule has already been executed.");
				trace.setError(true);
				result.setError(true);
				result.setErrorMessage("Rule has already been executed.");
				return result;
			}
			executedRules.add(ruleNumber);
			Boolean ruleResult=evaluateRule(rs, r, typedParameters, dte);
			if (ruleResult==null) {
				String error="Error Evaluating Rule (sheet: "+r.getSheetName()+", row: "+r.getRowNumber()+")";
				trace.getMessages().add("ERROR: "+error);
				trace.setError(true);
				result.setError(true);
				result.setErrorMessage(error);
				return result;
			}
			dte.setResult(ruleResult?"POSITIVE":"NEGATIVE");
			String action=ruleResult?r.getPositiveResult():r.getNegativeResult();
			if (action==null || action.isEmpty()) {
				dte.setResult("");
				ruleNumber++;
				continue;
			}
			dte.setResult(action);
			if (action.startsWith("goto ") && action.length()>5) {
				String label=action.substring(5);
				Integer toRuleNumber=rs.getRowLabels().get(label);
				if (toRuleNumber==null) {
					trace.getMessages().add("ERROR: Label not found: "+label);
					result.setError(true);
					result.setErrorMessage("Label not found: "+label);
					return result;
				}
				ruleNumber=toRuleNumber;
				continue;
			}
			if (action.startsWith("return ")) action=action.substring(7);
			result.setDecision(action);
			break;
		}
		trace.setResult(result.getDecision());
		trace.setResponseTimestamp(LocalDateTime.now());
		long stopnano=System.nanoTime();
		trace.setDurationInMilliSeconds((stopnano - startnano)/1000000d);
		return result;
	}
	
	private List<String> convertValuesFromLookupAndReplaceInRuleSet(final RuleSet rs, final HashMap<String, Object> runParameters, final HashMap<String, String> parameters) {
		final List<String> info=new ArrayList<>();
		
		parameters.forEach((parameterName, parameterValue) -> {
			InputParameterInfo pinfo=rs.getInputParameters().get(parameterName);
			if (pinfo==null) {
				info.add("Parameter '"+parameterName+"' is not defined in the Rule Set -> skipping.");
			} else {
				switch(pinfo.getType()) {
					case TEXT: 
						runParameters.put(parameterName, parameterValue); 
						//info.add("Using dataset value '"+parameterValue+"' for "+parameterName);
						break;
					case INTEGER: 
						try {
							int intValue=Integer.parseInt(parameterValue);
							runParameters.put(parameterName, intValue);
						} catch (NumberFormatException nfe) {
							info.add("Dataset value '"+parameterValue+"' for "+parameterName+" is an invalid INTEGER value  -> skipping.");
						}
						break;
					case DECIMAL: 
						try {
							double doubleValue=Integer.parseInt(parameterValue);
							runParameters.put(parameterName, doubleValue);
						} catch (NumberFormatException nfe) {
							info.add("Dataset value '"+parameterValue+"' for "+parameterName+" is an invalid DECIMAL value  -> skipping.");
						}
						break;
				}
			}
		});

		return info;
	}
	
	private HashMap<String, Object> toTypedParameters(RuleSet rs, HashMap<String, String> parameters, DecisionTrace trace) {
		final HashMap<String, Object> typedParameters=new HashMap<>();
		for (String parameterName:rs.getInputParameters().keySet()) {
			InputParameterInfo pInfo=rs.getInputParameters().get(parameterName);
			String value=parameters.get(parameterName);
			if (value==null || value.trim().isEmpty()) {
				value=pInfo.getDefaultValue();
			}
			if (value!=null && !value.trim().isEmpty()) {
				switch(pInfo.getType()) {
					case TEXT: typedParameters.put(parameterName, value); break;
					case INTEGER: {
						int intValue=0;
						try {
							intValue=Integer.parseInt(value);
						} catch (NumberFormatException nfe) {
							trace.getMessages().add("Invalid INTEGER value for parameter "+parameterName+": "+value);
							trace.setError(true);
							return null;
						}
						typedParameters.put(parameterName, intValue);
					}; break;
					case DECIMAL: {
						double doubleValue=0d;
						try {
							doubleValue=Double.parseDouble(value);
						} catch (NumberFormatException nfe) {
							trace.getMessages().add("Invalid DECIMAL value for parameter "+parameterName+": "+value);
							trace.setError(true);
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
	
	private DecimalFormat df=new DecimalFormat("#.###");
	private Boolean evaluateRule(RuleSet rs, Rule r, HashMap<String, Object> parameters, DecisionTraceElement dte) {
		Comparator c=r.getComparator();
		if (Comparator.ALWAYS==c) {
			return true;
		}
		InputParameterInfo inputParameterInfo=rs.getInputParameters().get(r.getParameterName());
		if (inputParameterInfo==null) {
			dte.setParameterValue("ERROR: NOT FOUND");
			return null;
		}
		ParameterType type=inputParameterInfo.getType();
		if (type==null) {
			dte.setParameterValue("ERROR: NOT FOUND");
			return null;
		}
		Object parameterValue=parameters.get(r.getParameterName());
		dte.setParameterValue(String.valueOf(parameterValue));
		Boolean evalResult=null;
		if (c==Comparator.IN_LIST || c==Comparator.NOT_IN_LIST) {
			String strparameterValue=getStringValue(parameterValue, type);
			boolean inList=setupApiService.isInList(rs, r.getValue1(), strparameterValue);
			return c==Comparator.IN_LIST?inList:!inList;
		}
		if (c==Comparator.LOOKUP) {
			String strparameterValue=getStringValue(parameterValue, type);
			DataSetLookupResult dslr=dataSetService.lookup(r.getValue1(), strparameterValue);
			if (dslr.isKeyFound()) {
				dte.getInfo().add("Using DataSet values "+dslr.getValues());
				dte.getInfo().addAll(convertValuesFromLookupAndReplaceInRuleSet(rs, parameters, dslr.getValues()));
			} else if (!dslr.isDataSetFound()) {
				dte.getInfo().add("DataSet not found in "+r.getValue1());
			} else if (!dslr.isKeyFound()) {
				dte.getInfo().add("Data not found for key: "+strparameterValue);
			}
			return dslr.isKeyFound();
		}
		switch(type) {
			case TEXT: evalResult=compare(rs, (String)parameterValue, c, r.getValue1(), r.getValue2());break;
			case INTEGER: 
				Integer ivalue1=null;
				Integer ivalue2=null;
				try {
					ivalue1=Integer.parseInt(r.getValue1());
					ivalue2=Integer.parseInt(r.getValue2());
				} catch (NumberFormatException nfe) {};
				evalResult=compare(rs, (Integer)parameterValue, c, ivalue1, ivalue2);break;
			case DECIMAL: 
				Double dvalue1=null;
				Double dvalue2=null;
				try {
					dvalue1=Double.parseDouble(r.getValue1());
					dvalue2=Double.parseDouble(r.getValue2());
				} catch (NumberFormatException nfe) {};
				evalResult=compare(rs, (Double)parameterValue, c, dvalue1, dvalue2);break;
		}
		return evalResult;
	}
	
	private String getStringValue(Object value, ParameterType type) {
		String strparameterValue=
				type==ParameterType.TEXT?(String)value:
					type==ParameterType.INTEGER?
						String.valueOf(value):
							df.format((Double)value);
		return strparameterValue;
	}
	
	private Boolean compare(RuleSet rs, String parameterValue, Comparator comparator, String value1, String value2) {
		if (parameterValue==null) parameterValue="";
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
		default: return null;
		}
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

	@Override
	public UnitTestRunnerResult runUnitTests(RuleSet rs) {
		final UnitTestRunnerResult result=new UnitTestRunnerResult();
		result.setAllTestsPassed(true);
		result.setUnitTestResults(new ArrayList<>());
		rs.getUnitTests().forEach(unitTest -> {
			DecisionResult dr=runRuleSetWithParameters(rs, unitTest.getParameters());
			UnitTestResult utr=new UnitTestResult();
			utr.setName(unitTest.getName());
			utr.setExpectedResult(unitTest.getExpectedResult());
			utr.setRun(dr.getRun());
			utr.setResult(dr.getDecision());
			if (dr.isError()) {
				result.setAllTestsPassed(false);
				utr.setPassed(false);
				result.setErrorMessage("Error(s) occurred.");
			} else if (!unitTest.getExpectedResult().equals(dr.getDecision())) {
				result.setAllTestsPassed(false);
				utr.setPassed(false);
			} else {
				utr.setPassed(true);
			}
			result.getUnitTestResults().add(utr);
		});
		return result;
	}
}
