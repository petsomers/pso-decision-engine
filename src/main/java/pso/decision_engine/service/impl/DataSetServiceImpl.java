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
import pso.decision_engine.model.DataSetName;
import pso.decision_engine.model.DataSetUploadResult;
import pso.decision_engine.model.enums.DataSetType;
import pso.decision_engine.persistence.DataSetDao;
import pso.decision_engine.service.DataSetService;

@Service
public class DataSetServiceImpl implements DataSetService {

	@Autowired
	private AppConfig appConfig;
	
	@Autowired	
	private DataSetDao dataSetDao;
	
	@Override
	public DataSetUploadResult uploadSet(String dataSetName, InputStream in) throws IOException {
		DataSetUploadResult result=new DataSetUploadResult();
		String dataSetId=dataSetDao.getOrCreateDataSetId(dataSetName, DataSetType.SET);
		String versionId=dataSetDao.createDataSetVersion(dataSetId);
		Path rawOutputFile=Paths.get(appConfig.getDataDirectory()+"/datasets/sets/", dataSetName, versionId+"_raw.txt");
		rawOutputFile.toFile().getParentFile().mkdirs();
		try (OutputStream out=Files.newOutputStream(rawOutputFile)) {
			StreamUtils.copy(in, out);
		}
		Path tempOutFile=Paths.get(appConfig.getDataDirectory()+"/datasets/sets/", dataSetName, versionId+"_temp.txt");
		
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

		Path outputFile=Paths.get(appConfig.getDataDirectory()+"/datasets/sets/", dataSetName, versionId+".txt");
		Files.move(tempOutFile, outputFile);
		setActiveDataSet(dataSetName, versionId);

		dataSetDao.uploadSet(versionId, Files.readAllLines(outputFile));
		dataSetDao.setActiveDataSetVersion(dataSetId, versionId);
		result.setOk(true);
		result.setDataSetVersionId(versionId);
		result.setDataSetName(dataSetName);
		return result;
	}
	
	private void setActiveDataSet(String dataSetName, String id) throws IOException {
		Path activeIndicatorFile=Paths.get(appConfig.getDataDirectory()+"/datasets/sets/", dataSetName, "active.txt");
		Files.deleteIfExists(activeIndicatorFile);
		Files.write(activeIndicatorFile, id.getBytes("UTF-8"));
	}

	
	@Override
	public List<DataSetName> getDataSetNames() {
		return dataSetDao.getDataSetNames();
	}

	@Override
	public void deleteDataSet(String dataSetName) {
		dataSetDao.deleteDataSet(dataSetName);
	}

	@Override
	public boolean isKeyInDataSet(String dataSetName, String key) {
		String versionId=dataSetDao.getActiveDataSetVersionForDataSetName(dataSetName);
		return dataSetDao.isKeyInDataSet(versionId, key); // better: use dataSetName directly
	}

}
