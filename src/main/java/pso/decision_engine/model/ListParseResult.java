package pso.decision_engine.model;

import lombok.Data;

@Data
public class ListParseResult {
	private boolean ok;
	private String errorMessage;
	private String listName;
	private String listId;
}
