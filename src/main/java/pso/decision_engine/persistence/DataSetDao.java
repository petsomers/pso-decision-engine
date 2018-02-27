package pso.decision_engine.persistence;

import java.util.List;

public interface DataSetDao {

	public int getOrCreateListId(String listName);

	public void uploadList(int listId, List<String> values);

	public List<String> getListNames();

	public void deleteList(String listName);
	
	public boolean isInList(String listName, String value);
}
