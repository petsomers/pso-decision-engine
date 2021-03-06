package pso.decision_engine.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import pso.decision_engine.config.AppConfig;
import pso.decision_engine.model.*;
import pso.decision_engine.persistence.RuleSetDao;
import pso.decision_engine.service.DataSetService;
import pso.decision_engine.service.ExcelParserService;
import pso.decision_engine.service.IdService;
import pso.decision_engine.service.SetupApiService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class SetupApiServiceImpl implements SetupApiService {

	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private IdService idService;
	
	@Autowired
	private RuleSetDao ruleSetDao;
	
	@Autowired
	private DataSetService dataSetService;
	
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
		String id=idService.createShortUniqueId();
		Path outputFile=Paths.get(appConfig.getTempDataDirectory(), "temp", id+".xlsx");
		outputFile.toFile().getParentFile().mkdirs();
		try (OutputStream out=Files.newOutputStream(outputFile)) {
			StreamUtils.copy(in, out);
		}
		ExcelParseResult result=new ExcelParseResult();
		result.setRuleSetId(id);
		RuleSet rs=null;
		Path excelFileLocation=null;
		try {
			rs=excelParserService.parseExcel(id, outputFile.toFile());
			result.setOk(true);
			result.setRestEndpoint(rs.getRestEndpoint());
		} catch (ExcelParserException epe) {
			result.setErrorMessage(epe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setErrorMessage(e.getMessage());
		}
		if (result.isOk()) {
			Path moveToFile = Paths.get(appConfig.getResultDataDirectory(), result.getRestEndpoint(), id + ".xlsx");
			moveToFile.toFile().getParentFile().mkdirs();
			Files.move(outputFile, moveToFile);
			excelFileLocation = moveToFile;
			Path jsonFile = Paths.get(appConfig.getResultDataDirectory(), result.getRestEndpoint(), id + ".json");
			mapper.writeValue(jsonFile.toFile(), rs);
			ruleSetDao.saveRuleSet(rs);
		} else {
			Path moveToFile = Paths.get(appConfig.getResultDataDirectory(), "error", id + ".xlsx");
			moveToFile.toFile().getParentFile().mkdirs();
			Files.move(outputFile, moveToFile);
			excelFileLocation = moveToFile;
		}
		if (excelFileLocation!=null) {
			File f=excelFileLocation.toFile();
			if (f.exists()) {
				try (FileInputStream inputStream=new FileInputStream(f)) {
					ruleSetDao.saveRuleSetSource(result.getRuleSetId(), (int)f.length(), inputStream);
				}
			}
		}
		return result;
	}
	
	@Override
	public void downloadExcel(String restEndpoint, String ruleSetId, OutputStream out) throws IOException {
		ruleSetDao.streamRuleSetSource(ruleSetId, out);
	}
	
	@Override
	public String saveRuleSet(RuleSet ruleSet) {
		String id=idService.createShortUniqueId();
		ruleSet.setId(id);
		ruleSetDao.saveRuleSet(ruleSet);
		return id;
	}
	
	@Override
	public String getActiveRuleSetId(String restEndpoint) {
		return ruleSetDao.getActiveRuleSetId(restEndpoint);
	}
 	
	
	public RuleSet getActiveRuleSetByEndpoint(String restEndpoint, boolean loadAllLists, boolean loadUnitTests) {
		String ruleSetId=ruleSetDao.getActiveRuleSetId(restEndpoint);
		if (ruleSetId==null) return null;
		return getRuleSet(restEndpoint, ruleSetId, loadAllLists, loadUnitTests);
	}
	
	@Override
	public RuleSet getRuleSet(String restEndpoint, String ruleSetId, boolean loadAllLists, boolean loadUnitTests) {
		if (ruleSetId==null || ruleSetId.isEmpty()) return null;
		RuleSet rs=ruleSetDao.getRuleSet(ruleSetId);
		if (rs==null) return null;
		if (!restEndpoint.equals(rs.getRestEndpoint())) return null;
		rs.setLists(ruleSetDao.getRuleSetLists(rs.getId(), loadAllLists));
		
		if (rs.getInputParameters()==null || rs.getInputParameters().size()==0) {		
			rs.setInputParameters(ruleSetDao.getRuleSetInputParameters(rs.getId()));
		}
		
		int i=0;
		for (Rule r:rs.getRules()) {
			if (r.getLabel()!=null && !r.getLabel().isEmpty()) {
				rs.getRowLabels().put(r.getLabel(), i);
			}
			i++;
		}
		if (loadUnitTests && (rs.getUnitTests()==null || rs.getUnitTests().size()==0)) {
			rs.setUnitTests(ruleSetDao.getRuleSetUnitTests(rs.getId()));
		}
		return rs;
	}
	
	@Override
	public void setActiveRuleSet(String restEndpoint, String ruleSetId) throws IOException {
		ruleSetDao.setActiveRuleSet(restEndpoint, ruleSetId);
		Path activeIndicatorFile=Paths.get(appConfig.getResultDataDirectory(), restEndpoint, "active.txt");
		Files.deleteIfExists(activeIndicatorFile);
		Files.write(activeIndicatorFile, ruleSetId.getBytes(UTF_8));
	}
	
	@Override
	public boolean doesRuleSetExist(String restEndpoint, String ruleSetId) {
		return ruleSetDao.doesRuleSetExist(restEndpoint, ruleSetId);
	}

	@Override
	public List<String> getAllEndpoints() {
		return ruleSetDao.getAllEndpoints();
	}
	
	@Override
	public List<RuleSetInfo> getRuleSetVersionsForEndpoint(String restEndpoint) {
		return ruleSetDao.getRuleSetVersionsForEndpoint(restEndpoint);
	}
	
	@Override
	public boolean isInList(RuleSet ruleSet, String listName, String value) {
		Set<String> memoryList=ruleSet.getLists().get(listName);
		if (memoryList!=null) {
			return memoryList.contains(value);
		}
		boolean fromLocalList=ruleSetDao.isInList(ruleSet.getId(), listName, value);
		if (fromLocalList) return true;
		return dataSetService.isKeyInDataSet(listName, value);
	}

	@Override
	public void deleteRuleSet(String restEndpoint, String ruleSetId) {
		ruleSetDao.deleteRuleSet(restEndpoint, ruleSetId);
		Path excelfile=Paths.get(appConfig.getTempDataDirectory(), restEndpoint, ruleSetId+".xlsx");
		Path jsonfile=Paths.get(appConfig.getTempDataDirectory(), restEndpoint, ruleSetId+".json");
		try {
			Files.deleteIfExists(excelfile);
			Files.deleteIfExists(jsonfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteRuleSetsWithEndpoint(String restEndpoint) {
		// 1. REMOVE FROM DB
		ruleSetDao.deleteRuleSetsWithEndpoint(restEndpoint);
		
		// 2. REMOVE ALL FILES from the directory
		Path dir=Paths.get(appConfig.getTempDataDirectory(), restEndpoint);
		try {
			Files.list(dir)
			.filter(Files::isRegularFile)
			.filter(file -> file.endsWith(".xlsx") || file.endsWith(".json"))
			.forEach(file -> {
				try {
					Files.deleteIfExists(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void deleteInactiveRuleSetsForEndpoint(String restEndpoint) {
		String activeId=getActiveRuleSetId(restEndpoint);
		// 1. REMOVE FROM DB
		ruleSetDao.deleteRuleSetsWithEndpointSkipId(restEndpoint, activeId);
		
		// 2. REMOVE ALL FILES from the directory
		Path tempDir = Paths.get(appConfig.getTempDataDirectory(), restEndpoint);
		Path resultDir = Paths.get(appConfig.getResultDataDirectory(), restEndpoint);
		try {
			Stream.concat(Files.list(tempDir), Files.list(resultDir))
					.filter(Files::isRegularFile)
					.filter(file -> file.endsWith(".xlsx") || file.endsWith(".json"))
					.filter(file -> !file.startsWith(activeId))
					.forEach(file -> {
						try {
							Files.deleteIfExists(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
