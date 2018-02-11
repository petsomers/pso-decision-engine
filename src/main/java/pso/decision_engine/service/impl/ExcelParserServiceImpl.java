package pso.decision_engine.service.impl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.enums.Comparator;
import pso.decision_engine.model.enums.ParameterType;

@Service
public class ExcelParserServiceImpl {
	
	public RuleSet parseExcel(InputStream is) throws Exception {
		RuleSet rs=new RuleSet();
		rs.setUploadDate(LocalDateTime.now());
		try (XSSFWorkbook wb = new XSSFWorkbook(is)) {
			for (int sheetNumber=0;sheetNumber<wb.getNumberOfSheets();sheetNumber++) {
				Sheet sheet=wb.getSheetAt(sheetNumber);
				String sheetName=sheet.getSheetName().toUpperCase();
				boolean infoLoaded=false;
				if ("INFO".equalsIgnoreCase(sheetName) && !infoLoaded) {
					parseInfoSheet(sheet, rs);
					infoLoaded=true;
				} else if (sheetName.startsWith("RULES")) {
					parseRuleSheet(sheet, rs);
				} else if (sheetName.startsWith("LISTS")) {
					parseListsSheet(sheet, rs);
				}
			}
		}
		
		return rs;
	}
	
	private void parseInfoSheet(Sheet sheet, RuleSet rs) {
		int firstRowNum=sheet.getFirstRowNum();
		int lastRowNum=sheet.getLastRowNum();
		ArrayList<String> parameterNames=new ArrayList<>();
		ArrayList<ParameterType> parameterTypes=new ArrayList<>();
		for (int r=firstRowNum;r<=lastRowNum;r++) {
			Row row=sheet.getRow(r);
			if (row==null) continue;
			short firstCellNum=row.getFirstCellNum();
			short lastCellNum=row.getLastCellNum();
			for (int c=firstCellNum;c<lastCellNum;c++) {
				Cell cell=row.getCell(c);
				String cv=getCellValueNoNull(cell);
				cv=cv==null?cv="":cv.trim();
				if ("Excel Format Version".equalsIgnoreCase(cv)) {
					rs.setVersion(getCellValueNoNull(row.getCell(++c)));
					c=lastCellNum;
				} else if ("Rule Set Name".equalsIgnoreCase(cv)) {
					rs.setName(getCellValueNoNull(row.getCell(++c)));
					c=lastCellNum;
				} else if ("Rest Endpoint".equalsIgnoreCase(cv)) {
					rs.setRestEndPoint(getCellValueNoNull(row.getCell(++c)));
					c=lastCellNum;
				} else if ("Created by".equalsIgnoreCase(cv)) {
					rs.setCreatedBy(getCellValueNoNull(row.getCell(++c)));
					c=lastCellNum;
				} else if ("Parameter Names".equalsIgnoreCase(cv)) {
					for (c=c+1;c<lastCellNum;c++) {
						parameterNames.add(getCellValueNoNull(row.getCell(c)));
					}
				} else if ("Parameter Types".equalsIgnoreCase(cv)) {
					for (c=c+1;c<lastCellNum;c++) {
						parameterTypes.add(toParameterType(getCellValueNoNull(row.getCell(c))));
					}
				}
			}
		}
		if (parameterNames.size()>0 && parameterNames.size()<=parameterTypes.size()) {
			for (int i=0;i<parameterNames.size();i++) {
				ParameterType pt=parameterTypes.get(i);
				if (pt!=null) {
					rs.getInputParameters().put(parameterNames.get(i), pt);	
				}
			}
		}
	}
	
	private void parseRuleSheet(Sheet sheet, RuleSet rs) {
		String sheetName=sheet.getSheetName();
		int firstRowNum=sheet.getFirstRowNum();
		int lastRowNum=sheet.getLastRowNum();
		if (firstRowNum!=0 && lastRowNum < 3) {
			return;
		}
		for (int r=2;r<=lastRowNum;r++) {
			Row row=sheet.getRow(r);
			if (row==null) continue;
			Rule rule=new Rule();
			rule.setRowNumber(r+1);
			rule.setSheetName(sheetName);
			rule.setLabel(getCellValueNoNull(row.getCell(0)));
			rule.setParameterName(getCellValueNoNull(row.getCell(1)));
			if (rule.getParameterName().isEmpty()) continue;
			rs.getRules().add(rule);
			
			Comparator comparator=toComparator(getCellValueNoNull(row.getCell(2)));
			rule.setComparator(comparator);
			
			String value1=getCellValueNoNull(row.getCell(3));
			String value2="";
			if (value1.indexOf(';')>0) {
				String [] values=value1.split(";");
				value1=values[0];
				value2=values[1];
			}
			rule.setValue1(value1); // todo: convert to correct type STRING/LONG/DOUBLE
			rule.setValue2(value2);
			rule.setPositiveResult(getCellValueNoNull(row.getCell(4)));
			rule.setNegativeResult(getCellValueNoNull(row.getCell(5)));
			rule.setRemark(getCellValueNoNull(row.getCell(6)));
		}
	}
	
	
	private void parseListsSheet(Sheet sheet, RuleSet rs) {
		String sheetName=sheet.getSheetName();
		int firstRowNum=sheet.getFirstRowNum();
		int lastRowNum=sheet.getLastRowNum();
		if (firstRowNum!=0 && lastRowNum < 1) {
			return;
		}
		ArrayList<String> listNames=new ArrayList<>();
		Row row=sheet.getRow(0);
		int cellCount=0;
		for (int col=0;col<=row.getLastCellNum();col++) {
			String listName=getCellValueNoNull(row.getCell(col));
			listNames.add(listName);
			if (!listName.isEmpty()) {
				cellCount=listNames.size();
			}
		}
		for (String listName:listNames) {
			rs.getLists().put(listName, new HashSet<Object>());
		}
		for (int rowNumber=1; rowNumber<=lastRowNum;rowNumber++) {
			row=sheet.getRow(rowNumber);
			for (int cellNumber=0;cellNumber<cellCount;cellNumber++) {
				String value=getCellValueNoNull(row.getCell(cellNumber));
				if (!value.isEmpty()) {
					String listName=listNames.get(cellNumber);
					if (!listName.isEmpty()) {
						rs.getLists().get(listName).add(value);
					}
				}
			}
		}
		
	}
	
	private static String getCellValueNoNull(Cell cell) {
		String cv=new DataFormatter().formatCellValue(cell).toString();
		return cv==null?cv="":cv.trim();
	}
	
	private ParameterType toParameterType(String s) {
		switch (s.toUpperCase()) {
			case "TEXT": return ParameterType.TEXT;
			case "DECIMAL": return ParameterType.DECIMAL;
			case "INTEGER": return ParameterType.INTEGER;
			default: return null;
		}
	}
	
	private Comparator toComparator(String s) {
		switch(s.toUpperCase()) {
			case "=": return Comparator.EQUAL_TO;
			case "<": return Comparator.SMALLER_THAN;
			case ">": return Comparator.GREATER_THAN;
			case "<=": return Comparator.SMALLER_OR_EQUAL_TO;
			case ">=": return Comparator.GREATER_OR_EQUAL_TO;
			case "BETWEEN": return Comparator.BETWEEN;
			case "IN LIST": return Comparator.IN_LIST;
			case "STARTS WITH": return Comparator.STARTS_WITH;
			case "CONTAINS": return Comparator.CONTAINS;
			case "ENDS WITH": return Comparator.ENDS_WITH;
			default: return null;
			
		}
	}
}
