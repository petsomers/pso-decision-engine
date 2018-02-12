package pso.decision_engine.service;

import java.io.IOException;
import java.io.InputStream;

import pso.decision_engine.model.ExcelParseResult;

public interface SetupApiService {

	public ExcelParseResult addExcelFile(InputStream in) throws IOException;

}