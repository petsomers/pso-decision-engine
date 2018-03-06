package pso.decision_engine.presentation.utils;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import pso.decision_engine.model.DecisionResult;
import pso.decision_engine.model.DecisionTrace;
import pso.decision_engine.model.DecisionTraceElement;
import pso.decision_engine.model.Rule;
import pso.decision_engine.utils.ComparatorHelper;

public class JsonToTextOutput {

	static private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	static public void renderDecisionResult(PrintWriter pw, DecisionResult r) {
		pw.println("Trace:");
		DecisionTrace t=r.getRun();
		if (t==null) {
			pw.println("No trace information available.");
			return;
		}
		pw.println("Rest Endpoint: "+t.getRestEndPoint());
		pw.println("Ruleset Id: "+t.getRuleSetId());
		pw.println("Input Parameters: "+t.getInputParameters());
		pw.println("Request Timestamp: "+dateTimeFormatter.format(t.getRequestTimestamp()));
		pw.println("Duration in ms: "+t.getDurationInMilliSeconds());
		pw.println();
		pw.println("Rules:");
		for (DecisionTraceElement te:t.getTrace()) {
			pw.println();
			Rule rule=te.getRule();
			pw.print("Sheet: "+rule.getSheetName()+", row: "+rule.getRowNumber());
			if (rule.getLabel()!=null && !rule.getLabel().isEmpty()) {
				pw.println(", label: "+rule.getLabel());
			} else {
				pw.println();
			}
			if (rule.getRemark()!=null && !rule.getRemark().isEmpty()) {
				pw.println("Remark: "+rule.getRemark());
			}
			pw.print("Condition: "+rule.getParameterName()+" ("+te.getParameterValue() +") "+ComparatorHelper.comparatorToShortString(rule.getComparator())+" "+rule.getValue1());
			if (rule.getValue2()!=null && !rule.getValue2().isEmpty()) {
				pw.println(";"+rule.getValue2());	
			} else {
				pw.println();
			} 
			pw.println("Positive Result: "+rule.getPositiveResult());
			pw.println("Negative Result: "+rule.getNegativeResult());
			pw.println("Result: "+te.getResult());
			if (te.getInfo()!=null && te.getInfo().size()>0) {
				pw.println("Info: ");
				te.getInfo().forEach((s) -> pw.println(s));
			}
		}
		
	}
}
