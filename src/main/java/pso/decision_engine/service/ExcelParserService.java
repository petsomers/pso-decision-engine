package pso.decision_engine.service;

import java.io.File;

import pso.decision_engine.model.ExcelParserException;
import pso.decision_engine.model.RuleSet;

public interface ExcelParserService {

	public RuleSet parseExcel(String id, File f) throws ExcelParserException, Exception;

}