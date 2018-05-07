package pso.decision_engine.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import pso.decision_engine.utils.JsonSerializerUtils.JsonLocalDateTimeDeSerializer;
import pso.decision_engine.utils.JsonSerializerUtils.JsonLocalDateTimeSerializer;

@Data
@JsonIgnoreProperties(value = { "rowLabels" })
public class RuleSet {

	private String id;
	private String name;
	private String restEndpoint;
	private String createdBy;
	private String version;
	private String remark;

	@JsonSerialize(using=JsonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JsonLocalDateTimeDeSerializer.class)
	private LocalDateTime uploadDate;
	private Hashtable<String, InputParameterInfo> inputParameters=new Hashtable<>();
	private List<Rule> rules=new ArrayList<>();
	
	private Hashtable<String, Integer> rowLabels=new Hashtable<>();
	
	// List are always strings. (also for INTEGER values)
	private HashMap<String, Set<String>> lists=new HashMap<>();
	
	private List<UnitTest> unitTests;
	
}
