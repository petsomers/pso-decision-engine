package pso.decision_engine.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pso.decision_engine.model.ListParseResult;

public interface DataSetService {

	/*
	 * List separated by line feeds
	 */
	public ListParseResult uploadList(String listName, InputStream in) throws IOException;
	
	public List<String> getListNames();

	public void deleteList(String listName);
	
	public boolean isInList(String listName, String value);
	
}
