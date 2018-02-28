package pso.decision_engine.service.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import pso.decision_engine.model.AppConfig;
import pso.decision_engine.model.DataSetUploadResult;
import pso.decision_engine.model.ListParseResult;
import pso.decision_engine.model.enums.DataSetType;
import pso.decision_engine.persistence.DataSetDao;
import pso.decision_engine.service.DataSetService;
import pso.decision_engine.service.IdService;

@Service
public class DataSetServiceImpl implements DataSetService {

	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private IdService idService;
	
	@Autowired	
	private DataSetDao dataSetDao;
	
	@Override
	public DataSetUploadResult uploadSet(String dataSetName, InputStream in) throws IOException {
		DataSetUploadResult result=new DataSetUploadResult();
		String id=idService.createShortUniqueId();
		Path rawOutputFile=Paths.get(appConfig.getDataDirectory()+"/lists", listName, id+"_raw.txt");
		rawOutputFile.toFile().getParentFile().mkdirs();
		try (OutputStream out=Files.newOutputStream(rawOutputFile)) {
			StreamUtils.copy(in, out);
		}
		Path tempOutFile=Paths.get(appConfig.getDataDirectory()+"/lists", listName, id+"_temp.txt");
		
		try(BufferedWriter writer = Files.newBufferedWriter(tempOutFile, Charset.forName("UTF-8"))) {
			try (Stream<String> stream = Files.lines(rawOutputFile)) {
				stream
				.map(line -> line.trim())
				.filter(line -> !line.isEmpty())
				.distinct()
				.sorted()
				.forEach(line -> {
					try {
						writer.write(line);
						writer.write('\r');
					} catch (IOException e) {
						throw new RuntimeException("Error during list processing: "+e.getMessage(),e);
					}
				});
				writer.flush();
			} catch (Exception e) {
				result.setOk(false);
				result.setErrorMessage(e.getMessage());
				e.printStackTrace();
				return result;
			}
		}

		Path outputFile=Paths.get(appConfig.getDataDirectory()+"/lists", listName, id+".txt");
		Files.move(tempOutFile, outputFile);
		setActiveList(listName, id);

		int dbListId=dataSetDao.getOrCreateListId(listName);
		dataSetDao.uploadList(dbListId, Files.readAllLines(outputFile));

		result.setOk(true);
		result.setListId(id);
		result.setListName(listName);
		return result;
	}
	
	private void setActiveList(String listName, String id) throws IOException {
		Path activeIndicatorFile=Paths.get(appConfig.getDataDirectory()+"/lists", listName, "active.txt");
		Files.deleteIfExists(activeIndicatorFile);
		Files.write(activeIndicatorFile, id.getBytes("UTF-8"));
	}

	@Override
	public List<String> getListNames() {
		return dataSetDao.getListNames();
	}

	@Override
	public void deleteList(String listName) {
		// TODO: delete fiels
		dataSetDao.deleteList(listName);
	}

	@Override
	public boolean isInList(String listName, String value) {
		return dataSetDao.isInList(listName, value);
	}

	@Override
	public List<String> getDataSetNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteDataSet(String dataSetName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isKeyInDataSet(String listName, String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getOrCreateDataSetId(String dataSetName, DataSetType dataSetType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createDataSetVersion(String dataSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActiveDataSetVersion(String dataSetId, String dataSetVersionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getActiveDataSetVersionForDataSetName(String dataSetName) {
		// TODO Auto-generated method stub
		return null;
	}

}
