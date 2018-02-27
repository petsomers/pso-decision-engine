package pso.decision_engine.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import pso.decision_engine.model.AppConfig;
import pso.decision_engine.model.ListParseResult;
import pso.decision_engine.service.DataSetService;
import pso.decision_engine.service.IdService;

@Service
public class DataSetServiceImpl implements DataSetService {

	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private IdService idService;
	
	@Override
	public ListParseResult uploadList(String listName, InputStream in) throws IOException {
		String id=idService.createShortUniqueId();
		Path tempOutputFile=Paths.get(appConfig.getDataDirectory()+"/lists", listName, id+"_raw.txt");
		tempOutputFile.toFile().getParentFile().mkdirs();
		try (OutputStream out=Files.newOutputStream(tempOutputFile)) {
			StreamUtils.copy(in, out);
		}
		Path outputFile=Paths.get(appConfig.getDataDirectory()+"/lists", listName, id+".txt");
		
		try (PrintWriter out=new PrintWriter(new BufferedWriter(new FileWriter(outputFile.toFile())))) {
			try (Stream<String> stream = Files.lines(tempOutputFile)) {
				stream
				.map(line -> line.trim())
				.distinct()
				.sorted()
				.forEach(line -> out.println(line));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setActiveList(listName, id);
		ListParseResult result=new ListParseResult();
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

}
