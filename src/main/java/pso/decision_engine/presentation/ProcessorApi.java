package pso.decision_engine.presentation;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.DecisionTrace;
import pso.decision_engine.model.DecisionTraceElement;
import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.UnitTestRunnerResult;
import pso.decision_engine.service.RuleSetProcessorService;
import pso.decision_engine.service.SetupApiService;
import pso.decision_engine.utils.ComparatorHelper;

@RestController
public class ProcessorApi {

	@Autowired
	private SetupApiService setupService;
	
	@Autowired
	private RuleSetProcessorService ruleSetProcessorService;
	
	@RequestMapping(value = "/json/run/{restEndPoint}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public DecisionResult runJson(HttpServletRequest request, @PathVariable String restEndPoint) throws Exception {
		String ruleSetId=setupService.getActiveRuleSetId(restEndPoint);
		return runJson(request, restEndPoint, ruleSetId);
    }
	
	@RequestMapping(value = "/json/run/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public DecisionResult runJson(
    		HttpServletRequest request, 
    		@PathVariable String restEndPoint,
    		@PathVariable String ruleSetId) throws Exception {
		RuleSet ruleSet=setupService.getRuleSet(restEndPoint, ruleSetId, false, false);
		if (ruleSet==null) {
			DecisionResult result=new DecisionResult();
			result.setError(true);
			result.setErrorMessage("RuleSet not found.");
		}
		final HashMap<String, String> parameters=new HashMap<>();
		for (String parameter: Collections.list(request.getParameterNames())) {
			if (!"trace".equals(parameter))
				parameters.put(parameter, request.getParameter(parameter).trim());
		}
		return ruleSetProcessorService.runRuleSetWithParameters(ruleSet, parameters);
    }
	
	
	static private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@RequestMapping(value = "/run/{restEndPoint}",method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public void runPlainText(HttpServletRequest request, PrintWriter pw, @PathVariable String restEndPoint) throws Exception {
		DecisionResult r=runJson(request, restEndPoint);
		boolean trace=request.getParameter("trace")!=null && !"N".equalsIgnoreCase(request.getParameter("trace"));
		if (r.isError()) {
			pw.print("ERROR: "+r.getErrorMessage());
		} else {
			pw.print(r.getDecision());
		}
		if (trace) {
			pw.println();pw.println();
			pw.println("Trace:");
			DecisionTrace t=r.getTrace();
			if (t==null) {
				pw.println("No trace information available.");
				return;
			}
			pw.println("Rest Endpoint: "+t.getRestEndPoint());
			pw.println("Ruleset Id: "+t.getRuleSetId());
			pw.println("Input Paramteters: "+t.getInputParameters());
			pw.println("Request Timestamp: "+dateTimeFormatter.format(t.getRequestTimestamp()));
			pw.println("Duration in ms: "+t.getDurationInMilliSeconds());
			for (DecisionTraceElement te:t.getTrace()) {
				pw.println();
				Rule rule=te.getRule();
				pw.print("Sheet "+rule.getSheetName()+", row: "+rule.getRowNumber());
				if (rule.getLabel()!=null && !rule.getLabel().isEmpty()) {
					pw.println(", label: "+rule.getLabel());
				} else {
					pw.println();
				}
				if (rule.getRemark()!=null && !rule.getRemark().isEmpty()) {
					pw.println("Remark: "+rule.getRemark());
				}
				pw.print("Condition: "+rule.getParameterName()+" ("+t.getInputParameters().get(rule.getParameterName()) +") "+ComparatorHelper.comparatorToShortString(rule.getComparator())+" "+rule.getValue1());
				if (rule.getValue2()!=null && !rule.getValue2().isEmpty()) {
					pw.println(";"+rule.getValue2());	
				} else {
					pw.println();
				} 
				pw.println("Positive Result: "+rule.getPositiveResult());
				pw.println("Negative Result: "+rule.getNegativeResult());
				pw.println("Result: "+te.getResult());
			}
			
			
			
		}
		
	}
	
	@RequestMapping(value = "/run_unittests/{restEndPoint}/{ruleSetId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public UnitTestRunnerResult runUnitTests(
    		HttpServletRequest request, 
    		@PathVariable String restEndPoint,
    		@PathVariable String ruleSetId) throws Exception {
		RuleSet ruleSet=setupService.getRuleSet(restEndPoint, ruleSetId, false, true);
		if (ruleSet==null) {
			UnitTestRunnerResult result=new UnitTestRunnerResult();
			result.setErrorMessage("RuleSet not found.");
		}
		return ruleSetProcessorService.runUnitTests(ruleSet);
    }
	
}
