package pso.decision_engine.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import pso.decision_engine.model.enums.ParameterType;
import pso.decision_engine.utils.JsonSerializerUtils.JsonLocalDateTimeDeSerializer;
import pso.decision_engine.utils.JsonSerializerUtils.JsonLocalDateTimeSerializer;

@Data
public class RuleSet {

	private String name;
	private String id;
	private String restEndPoint;
	private String createdBy;
	private String version;

	@JsonSerialize(using=JsonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JsonLocalDateTimeDeSerializer.class)
	private LocalDateTime uploadDate;
	private Hashtable<String, ParameterType> inputParameters=new Hashtable<>();
	private List<Rule> rules=new ArrayList<>();
	private Hashtable<String, Integer> rowLabels=new Hashtable<>();
	
	// List are always strings. (also for INTEGER values)
	private HashMap<String, HashSet<String>> lists=new HashMap<>();
	
	private List<UnitTest> unitTests;
	
	private String parseError;
}
