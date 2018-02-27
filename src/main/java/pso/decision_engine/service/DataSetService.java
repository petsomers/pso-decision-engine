package pso.decision_engine.service;

import java.io.IOException;
import java.io.InputStream;

import pso.decision_engine.model.ListParseResult;

public interface DataSetService {

	/*
	 * List separated by line feeds
	 */
	public ListParseResult uploadList(String listName, InputStream in) throws IOException;
	
}
