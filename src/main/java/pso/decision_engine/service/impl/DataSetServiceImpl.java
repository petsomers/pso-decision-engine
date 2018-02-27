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
import pso.decision_engine.model.ListParseResult;
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
	public ListParseResult uploadList(String listName, InputStream in) throws IOException {
		ListParseResult result=new ListParseResult();
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

}
