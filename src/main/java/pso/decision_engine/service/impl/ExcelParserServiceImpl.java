package pso.decision_engine.service.impl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import pso.decision_engine.model.InputParameter;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.model.enums.ParameterType;

@Service
public class ExcelParserServiceImpl {
	
	public RuleSet parseExcel(InputStream is) throws Exception {
		RuleSet rs=new RuleSet();
		rs.setUploadDate(LocalDateTime.now());
		try (XSSFWorkbook wb = new XSSFWorkbook(is)) {
			Sheet sheet=wb.getSheetAt(0);
			parseInfoSheet(sheet, rs);		
		}
		
		return rs;
	}
	
	private void parseInfoSheet(Sheet sheet, RuleSet rs) {
		int firstRowNum=sheet.getFirstRowNum();
		int lastRowNum=sheet.getLastRowNum();
		ArrayList<String> parameterNames=new ArrayList<>();
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
					rs.setVersion(getCellValueNoNull(row.getCell(c)));
					c=lastCellNum;
				} else if ("Rule Set Name".equalsIgnoreCase(cv)) {
					rs.setName(getCellValueNoNull(row.getCell(c)));
					c=lastCellNum;
				} else if ("Rest Endpoint".equalsIgnoreCase(cv)) {
					rs.setRestEndPoint(getCellValueNoNull(row.getCell(c)));
					c=lastCellNum;
				} else if ("Created by".equalsIgnoreCase(cv)) {
					rs.setCreatedBy(getCellValueNoNull(row.getCell(c)));
					c=lastCellNum;
				} else if ("Input fields".equalsIgnoreCase(cv)) {
					rs.setVersion(getCellValueNoNull(row.getCell(c)));
					int i=0;
					for (c=c+1;c<lastCellNum;c++) {
						parameterNames.add(getCellValueNoNull(row.getCell(c)));
						i++;
					}
				} else if ("Input Types".equalsIgnoreCase(cv)) {
					int i=0;
					for (c=c+1;c<lastCellNum;c++) {
						i++;
						ParameterType tp=toParameterType(getCellValueNoNull(row.getCell(c)));
						String parameterName=parameterNames.get(i);
						if (!parameterName.isEmpty() && tp!=null) {
							rs.getInputParameters().put(parameterName, tp);
						}
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
			case "NUMERIC": return ParameterType.NUMERIC;
			default: return null;
		}
	}
}
