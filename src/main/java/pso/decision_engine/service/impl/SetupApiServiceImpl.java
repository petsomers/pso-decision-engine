package pso.decision_engine.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import pso.decision_engine.model.AppConfig;
import pso.decision_engine.model.ExcelParseResult;
import pso.decision_engine.model.ExcelParserException;
import pso.decision_engine.model.RuleSet;
import pso.decision_engine.persistence.RuleSetDao;
import pso.decision_engine.service.ExcelParserService;
import pso.decision_engine.service.SetupApiService;

@Service
public class SetupApiServiceImpl implements SetupApiService {

	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private RuleSetDao ruleSetDao;
	
	@Autowired
	private ExcelParserService excelParserService;
	
	ObjectMapper mapper=new ObjectMapper();
	{
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	/* (non-Javadoc)
	 * @see pso.decision_engine.service.impl.SetupApiService#addExcelFile(java.io.InputStream)
	 */
	@Override
	public ExcelParseResult addExcelFile(InputStream in) throws IOException {
		String id=createShortUniqueId();
		Path outputFile=Paths.get(appConfig.getDataDirectory(), "temp", id+".xlsx");
		outputFile.toFile().getParentFile().mkdirs();
		try (OutputStream out=Files.newOutputStream(outputFile)) {
			StreamUtils.copy(in, out);
		}
		ExcelParseResult result=new ExcelParseResult();
		result.setRuleSetId(id);
		RuleSet rs=null;
		try {
			rs=excelParserService.parseExcel(id, outputFile.toFile());
			result.setOk(true);
			result.setRestEndPoint(rs.getRestEndPoint());
		} catch (ExcelParserException epe) {
			result.setErrorMessage(epe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setErrorMessage("General Error");
		}
		if (result.isOk()) {
			Path moveToFile=Paths.get(appConfig.getDataDirectory(), result.getRestEndPoint(), id+".xlsx");
			moveToFile.toFile().getParentFile().mkdirs();
			Files.move(outputFile, moveToFile);
			Path jsonFile=Paths.get(appConfig.getDataDirectory(), result.getRestEndPoint(), id+".json");
			mapper.writeValue(jsonFile.toFile(), rs);
			ruleSetDao.saveRuleSet(rs);
		} else {
			Path moveToFile=Paths.get(appConfig.getDataDirectory(), "error", id+".xlsx");
			moveToFile.toFile().getParentFile().mkdirs();
			Files.move(outputFile, moveToFile);
		}
		
		return result;
	}
	
	private DateTimeFormatter ldtformatter = DateTimeFormatter.ofPattern("yyMMddHHmmssms");
	private String createShortUniqueId() {
		return LocalDateTime.now().format(ldtformatter)+Math.round(Math.random()*100d);
	}
	
	@Override
	public String saveRuleSet(RuleSet ruleSet) {
		String id=createShortUniqueId();
		ruleSet.setId(id);
		ruleSetDao.saveRuleSet(ruleSet);
		return id;
	}
	@Override
	public RuleSet getActiveRuleSet(String restEndPoint) {
		String ruleSetId=ruleSetDao.getActiveRuleSetId(restEndPoint);
		if (ruleSetId==null) return null;
		return ruleSetDao.getRuleSet(ruleSetId);
	}
	
	@Override
	public void setActiveRuleSet(String restEndPoint, String ruleSetId) {
		ruleSetDao.setActiveRuleSet(restEndPoint, ruleSetId);
	}

	@Override
	public boolean doesRuleSetExist(String restEndPoint, String ruleSetId) {
		return ruleSetDao.doesRuleSetExist(restEndPoint, ruleSetId);
	}

	@Override
	public List<String> getAllEndPoints() {
		return ruleSetDao.getAllEndPoints();
	}
	
}
