package pso.decision_engine.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import pso.decision_engine.utils.JsonSerializerUtils.JsonLocalDateTimeSerializer;

@Data
public class DecisionTrace {
	@JsonSerialize(using=JsonLocalDateTimeSerializer.class)
	private LocalDateTime requestTimestamp;
	@JsonSerialize(using=JsonLocalDateTimeSerializer.class)
	private LocalDateTime responseTimestamp;
	
	private double durationInMilliSeconds;
	private HashMap<String, String> inputParameters;
	private String restEndPoint;
	private String ruleSetId;
	private ArrayList<DecisionTraceElement> trace=new ArrayList<>();
	private boolean error;
	private ArrayList<String> messages=new ArrayList<>();
	private String result;
}
