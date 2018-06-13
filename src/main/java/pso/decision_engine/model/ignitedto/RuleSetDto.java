package pso.decision_engine.model.ignitedto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

//import org.apache.ignite.cache.query.annotations.QuerySqlField;

import lombok.Data;
import pso.decision_engine.model.InputParameterInfo;
import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.UnitTest;

@Data
public class RuleSetDto implements Serializable {

	private static final long serialVersionUID = 1L;

	//@QuerySqlField(index = true)
	private String id;
	
	//@QuerySqlField(index = true)
	private String restEndpoint;
	
	//@QuerySqlField
	private String name;
	
	//@QuerySqlField
	private String createdBy;
	
	//@QuerySqlField
	private String version;
	
	//@QuerySqlField
	private String remark;

	//@QuerySqlField
	private LocalDateTime uploadDate;
	
	private Hashtable<String, InputParameterInfo> inputParameters=new Hashtable<>();
	private List<Rule> rules=new ArrayList<>();
	
	private Hashtable<String, Integer> rowLabels=new Hashtable<>();
	
	private List<UnitTest> unitTests;
	
	private Set<String> lists=new HashSet<>();
	
	public RuleSetDto() {}
	
	public RuleSetDto(RuleSet rs) {
		this.name=rs.getName();
		this.id=rs.getId();
		this.restEndpoint=rs.getRestEndpoint();
		this.createdBy=rs.getCreatedBy();
		this.version=rs.getVersion();
		this.remark=rs.getRemark();
		
		this.uploadDate=rs.getUploadDate();
		this.inputParameters=rs.getInputParameters();
		this.rules=rs.getRules();
		this.rowLabels=rs.getRowLabels();
		this.unitTests=rs.getUnitTests();
		if (rs.getLists()!=null) {
			rs.getLists().keySet().forEach(listName -> this.lists.add(listName));
		}
	}
}