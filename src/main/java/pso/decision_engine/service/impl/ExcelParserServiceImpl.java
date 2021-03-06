package pso.decision_engine.service.impl;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import pso.decision_engine.model.ExcelParserException;
import pso.decision_engine.model.InputParameterInfo;
import pso.decision_engine.model.Rule;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.UnitTest;
import pso.decision_engine.model.enums.Comparator;
import pso.decision_engine.model.enums.ParameterType;
import pso.decision_engine.service.ExcelParserService;
import pso.decision_engine.utils.ComparatorHelper;

@Service
public class ExcelParserServiceImpl implements ExcelParserService {
	
	@Override
	public RuleSet parseExcel(String id, File f) throws ExcelParserException, Exception {
		RuleSet rs=new RuleSet();
		rs.setId(id);
		rs.setUploadDate(LocalDateTime.now());
		try (XSSFWorkbook wb = new XSSFWorkbook(f)) {
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
				} else if (sheetName.startsWith("UNIT TESTS")) {
					parseUnitTestsSheet(sheet, rs);
				}
			}
		}
		
		return rs;
	}
	
	private void parseInfoSheet(Sheet sheet, RuleSet rs) throws ExcelParserException {
		int firstRowNum=sheet.getFirstRowNum();
		int lastRowNum=sheet.getLastRowNum();
		ArrayList<String> parameterNames=new ArrayList<>();
		ArrayList<ParameterType> parameterTypes=new ArrayList<>();
		ArrayList<String> defaultValues=new ArrayList<>();
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
					rs.setRestEndpoint(getCellValueNoNull(row.getCell(++c)));
					validateRestEndPoint(rs.getRestEndpoint());
					c=lastCellNum;
				} else if ("Created by".equalsIgnoreCase(cv)) {
					rs.setCreatedBy(getCellValueNoNull(row.getCell(++c)));
					c=lastCellNum;
				} else if ("Version".equalsIgnoreCase(cv)) {
					rs.setVersion(getCellValueNoNull(row.getCell(++c)));
					c=lastCellNum;
				} else if ("Remark".equalsIgnoreCase(cv)) {
					rs.setRemark(getCellValueNoNull(row.getCell(++c)));
					c=lastCellNum;
				} else if ("Parameter Names".equalsIgnoreCase(cv)) {
					for (c=c+1;c<lastCellNum;c++) {
						String parameterName=getCellValueNoNull(row.getCell(c));
						if (parameterName.isEmpty()) {
							c=lastCellNum;
						} else {
							parameterNames.add(parameterName);	
						}
					}
				} else if ("Parameter Types".equalsIgnoreCase(cv)) {
					for (c=c+1;c<lastCellNum;c++) {
						String parameterType=getCellValueNoNull(row.getCell(c));
						if (parameterType.isEmpty()) {
							c=lastCellNum;
						} else {
							ParameterType pt=ComparatorHelper.stringToParameterType(parameterType);
							if (pt==null) {
								throw new ExcelParserException("Invalid type value: "+parameterType);
							}
							parameterTypes.add(pt);	
						}
					}
				} else if ("Default Values".equalsIgnoreCase(cv)) {
					for (int j=c+1;j<c+1+parameterNames.size();j++) {
						defaultValues.add(getCellValueNoNull(row.getCell(j)));
					}
					c=lastCellNum;
				}
			}
		}
		if (parameterNames.size()<parameterTypes.size()) {
			throw new ExcelParserException("More parameter names than type definitions.");
		}
		for (int i=0;i<parameterNames.size();i++) {
			ParameterType pt=parameterTypes.get(i);
			String defaultValue=i<defaultValues.size()?defaultValues.get(i):null;
			InputParameterInfo ipi=new InputParameterInfo();
			ipi.setType(pt);
			ipi.setDefaultValue(defaultValue);
			ipi.setSeqNr(i+1);
			rs.getInputParameters().put(parameterNames.get(i), ipi);	
		}
	}
	
	private void parseRuleSheet(Sheet sheet, RuleSet rs) throws ExcelParserException {
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
			
			Comparator comparator=ComparatorHelper.shortStringToComparator(getCellValueNoNull(row.getCell(2)));
			rule.setComparator(comparator);
			
			if (rule.getParameterName().isEmpty() && Comparator.ALWAYS!=comparator) continue;
			
			rs.getRules().add(rule);
			
			if (!rule.getLabel().isEmpty()) {
				if (rs.getRowLabels().containsKey(rule.getLabel())) {
					throw new ExcelParserException("Duplicate Label: "+rule.getLabel()+" on sheet '"+sheetName+"' and row "+(r+1));
				}
				rs.getRowLabels().put(rule.getLabel(), rs.getRules().size()-1);
			}
			
			String value1=getCellValueNoNull(row.getCell(3));
			String value2="";
			if (value1.indexOf(';')>0) {
				String [] values=value1.split(";");
				value1=values[0];
				value2=values[1];
			}
			rule.setValue1(value1);
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
			if (!listName.isEmpty())
				rs.getLists().put(listName, new HashSet<String>());
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
	
	private void parseUnitTestsSheet(Sheet sheet, RuleSet rs)  throws ExcelParserException {
		String sheetName=sheet.getSheetName();
		int firstRowNum=sheet.getFirstRowNum();
		int lastRowNum=sheet.getLastRowNum();
		if (firstRowNum!=0 && lastRowNum < 1) {
			return;
		}
		Row row=sheet.getRow(0);
		int cellCount=0;
		ArrayList<String> parameterNames=new ArrayList<>();
		boolean expectedResultOk=false;
		for (int col=1;col<=row.getLastCellNum();col++) {
			String parameterName=getCellValueNoNull(row.getCell(col));
			if ("Expected Result".equalsIgnoreCase(parameterName)) {
				expectedResultOk=true;
				break;
			} else {
				parameterNames.add(parameterName);
			}
		}
		if (!expectedResultOk) {
			throw new ExcelParserException("Sheet "+sheetName+": column 'Expected Result' not found.");
		}
		ArrayList<UnitTest> unitTests=new ArrayList<>();
		for (int r=1;r<=lastRowNum;r++) {
			row=sheet.getRow(r);
			if (row==null) continue;
			String testName=getCellValueNoNull(row.getCell(0));
			if (testName.isEmpty()) continue;
			UnitTest unitTest=new UnitTest();
			unitTest.setName(testName);
			HashMap<String, String> parameters=new HashMap<>();
			for (int col=1;col<parameterNames.size()+1;col++) {
				String parameterValue=getCellValueNoNull(row.getCell(col));
				if (!parameterValue.isEmpty()) {
					parameters.put(parameterNames.get(col-1), parameterValue);	
				}
			}
			unitTest.setParameters(parameters);
			unitTest.setExpectedResult(getCellValueNoNull(row.getCell(parameterNames.size()+1)));
			unitTests.add(unitTest);
		}
		rs.setUnitTests(unitTests);
	}
	
	
	private static String getCellValueNoNull(Cell cell) {
		String cv=new DataFormatter().formatCellValue(cell).toString();
		return cv==null?cv="":cv.trim();
	}
	
	private void validateRestEndPoint(String rep) throws ExcelParserException {
		if (rep==null || rep.isEmpty()) {
			throw new ExcelParserException("Rest Endpoint is empty.");
		}
		for (int i=0;i<rep.length();i++) {
			char c=rep.charAt(i);
			if (!((c>='a' && c<='z') 
				|| (c>='A' && c<='Z')
				|| (c>='0' && c<='9')
				|| c=='_' || c=='-')) {
				throw new ExcelParserException("Invalid Rest Endpoint: only use letter, numbers, _ and - characters.");	
			}
		}
		
	}
	
}
