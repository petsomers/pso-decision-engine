package pso.decision_engine.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Data;

@Data
public class DecisionTrace {
	private LocalDateTime requestTimestamp;
	private LocalDateTime responseTimestamp;
	private long durationInMilliSeconds;
	private HashMap<String, String> inputParameters;
	private String restEndPoint;
	private String ruleId;
	private ArrayList<DecisionTraceElement> trace=new ArrayList<>();
	private boolean error;
	private ArrayList<String> messages=new ArrayList<>();
	private String result;
}
