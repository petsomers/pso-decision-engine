package pso.decision_engine.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import pso.decision_engine.utils.JsonSerializerUtils.JsonLocalDateTimeDeSerializer;
import pso.decision_engine.utils.JsonSerializerUtils.JsonLocalDateTimeSerializer;

@Data
public class RuleSetSearchResultItem {
	private String ruleSetid;
	private String name;
	private String id;
	private String restEndPoint;
	private String createdBy;
	private String version;
	private String remark;

	@JsonSerialize(using=JsonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JsonLocalDateTimeDeSerializer.class)
	private LocalDateTime uploadDate;
	
	private boolean activeRuleSet;
}
