package pso.decision_engine.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import lombok.Data;
import pso.decision_engine.model.enums.ParameterType;

@Data
public class RuleSet {

	private String name;
	private String restEndPoint;
	private String createdBy;
	private String version;
	private LocalDateTime uploadDate;
	private Hashtable<String, ParameterType> inputParameters=new Hashtable<>();
	private List<Rule> rules=new ArrayList<>();
	private Hashtable<String, Integer> rowLabels;
	
	private HashMap<String, HashSet<Object>> lists=new HashMap<>();
	
	
}
