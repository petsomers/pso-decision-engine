package pso.decision_engine.model;

import java.util.List;

import lombok.Data;

@Data
public class ScrollItems<T> {
	private List<T> items;
	private boolean hasMore;
}
