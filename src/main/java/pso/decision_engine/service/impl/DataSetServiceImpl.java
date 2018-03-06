package pso.decision_engine.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import pso.decision_engine.model.AppConfig;
import pso.decision_engine.model.DataSetInfo;
import pso.decision_engine.model.DataSetLookupResult;
import pso.decision_engine.model.DataSetUploadResult;
import pso.decision_engine.model.ParameterValuesRow;
import pso.decision_engine.model.ScrollItems;
import pso.decision_engine.model.enums.DataSetType;
import pso.decision_engine.persistence.DataSetDao;
import pso.decision_engine.service.DataSetService;
import pso.decision_engine.utils.bigfilesort.BigFileSort;
import pso.decision_engine.utils.bigfilesort.BigFileSortCommand;
import pso.decision_engine.utils.bigfilesort.BigFileSortResult;
import reactor.core.publisher.Flux;

@Service
public class DataSetServiceImpl implements DataSetService {

	@Autowired
	private AppConfig appConfig;
	
	@Autowired	
	private DataSetDao dataSetDao;
	
	@Override
	public DataSetUploadResult uploadDataSet(String dataSetName, DataSetType dataSetType, InputStream in) throws IOException {
		DataSetUploadResult result=new DataSetUploadResult();
		DataSetInfo dataSetInfo=dataSetDao.getDataSetInfo(dataSetName);
		if (dataSetInfo==null) {
			String id=dataSetDao.createDataSet(dataSetName, dataSetType);
			dataSetInfo=new DataSetInfo(id, dataSetName, dataSetType);
		} else if (dataSetInfo.getType()!=dataSetType) {
			result.setErrorMessage("The data set already exists for another type.");
			return result;
		}
		
		String versionId=dataSetDao.createDataSetVersion(dataSetInfo.getId());
		Path rawOutputFile=Paths.get(appConfig.getDataDirectory()+"/datasets/"+dataSetType.toString()+"/", dataSetName, versionId+"_raw.txt");
		rawOutputFile.toFile().getParentFile().mkdirs();
		try (OutputStream out=Files.newOutputStream(rawOutputFile)) {
			StreamUtils.copy(in, out);
		}
	
		BigFileSortCommand c=new BigFileSortCommand();
		c.setInputFile(rawOutputFile);
		c.setOutputFileName(versionId+"_temp");
		c.setOutputFileExtension("txt");
		c.setExtractHeaderLine(dataSetType==DataSetType.LOOKUP);
		c.setKeepFirstTabUnique(dataSetType==DataSetType.LOOKUP);
		c.setRemoveTabs(dataSetType==DataSetType.LIST);
		c.setRemoveEmptyLines(true);
		BigFileSortResult bfsr=BigFileSort.sortAndRemoveDuplicates(c);
		Path tempOutFile=bfsr.getOutputFile();
		
/*		
		Path tempOutFile=Paths.get(appConfig.getDataDirectory()+"/datasets/sets/", dataSetName, versionId+"_temp.txt");
		
		try(BufferedWriter writer = Files.newBufferedWriter(tempOutFile, Charset.forName("UTF-8"))) {
			try (Stream<String> stream = Files.lines(rawOutputFile)) {
				stream
				.map(line -> line.trim())
				.map(line -> {
					// remove tabs, a set only has 1 column
					int tabpos=line.indexOf('\t');
					if (tabpos>=0) {
						return line.substring(0, tabpos).trim();
					}
					return line;
				})
				.filter(line -> !line.isEmpty())
				.distinct()
				.sorted()
				.forEach(line -> {
					try {
						writer.write(line);
						writer.write("\r\n");
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
*/
		Path outputFile=Paths.get(appConfig.getDataDirectory()+"/datasets/"+dataSetType.toString()+"/", dataSetName, versionId+".txt");
		
		Files.move(tempOutFile, outputFile);
		setActiveDataSet(dataSetName, dataSetType, versionId);
		if (dataSetType==DataSetType.LOOKUP && bfsr.getHeaderLine()==null) {
			result.setOk(false);
			result.setErrorMessage("Empty file.");
			return result;
		}
		if (bfsr.getHeaderLine()!=null) {
			writeHeaderLine(dataSetName, dataSetType, versionId, bfsr.getHeaderLine());
			List<String> parameterNames=Arrays.asList(bfsr.getHeaderLine().split("\t"));
			dataSetDao.saveDataSetParameterNames(versionId, parameterNames);
			int[] keyId= {0};
			dataSetDao.uploadDataSetKeysAndValues(versionId, 
				Flux
				.fromStream(Files.lines(outputFile))
				.map(line -> {
					String[]lineItems=line.split("\t");
					if (lineItems.length==0)
						return null;
					String key=lineItems[0];
					ArrayList<String> values=new ArrayList<>(lineItems.length-1);
					for (int i=1;i<lineItems.length;i++) {
						values.add(lineItems[i]);
					}
					ParameterValuesRow row=new ParameterValuesRow();
					row.setKeyId(keyId[0]++);
					row.setKey(key);
					row.setValues(values);
					return row;
				})
			);
			
		} else {
			dataSetDao.uploadDataSetKeys(versionId, Flux.fromStream(Files.lines(outputFile)));	
		}
		
		
		dataSetDao.setActiveDataSetVersion(dataSetInfo.getId(), versionId);
		dataSetDao.deleteInactiveDataSetVersions(dataSetName);
		result.setOk(true);
		result.setDataSetVersionId(versionId);
		result.setDataSetName(dataSetName);
		return result;
	}
	
	private void setActiveDataSet(String dataSetName, DataSetType dataSetType, String versionId) throws IOException {
		Path activeIndicatorFile=Paths.get(appConfig.getDataDirectory()+"/datasets/"+dataSetType.toString()+"/", dataSetName, "active.txt");
		Files.deleteIfExists(activeIndicatorFile);
		Files.write(activeIndicatorFile, versionId.getBytes("UTF-8"));
	}
	
	private void writeHeaderLine(String dataSetName, DataSetType dataSetType, String versionId, String headerLine) throws IOException {
		Path activeIndicatorFile=Paths.get(appConfig.getDataDirectory()+"/datasets/"+dataSetType.toString()+"/", dataSetName, versionId+"_header.txt");
		Files.write(activeIndicatorFile, headerLine.getBytes("UTF-8"));
	}

	
	@Override
	public List<DataSetInfo> getDataSetNames() {
		return dataSetDao.getDataSetNames();
	}

	@Override
	public void deleteDataSet(String dataSetName) {
		dataSetDao.deleteDataSet(dataSetName);
	}

	@Override
	public boolean isKeyInDataSet(String dataSetName, String key) {
		String versionId=dataSetDao.getActiveDataSetVersionForDataSetName(dataSetName);
		if (versionId==null) return false;
		return dataSetDao.isKeyInDataSet(versionId, key);
	}

	@Override
	public ScrollItems<String> getKeysFromActiveDataSet(String dataSetName, String fromKey, int max) {
		ScrollItems<String> result=new ScrollItems<>();
		String versionId=dataSetDao.getActiveDataSetVersionForDataSetName(dataSetName);
		if (versionId==null) {
			result.setItems(new ArrayList<>());
			result.setHasMore(false);
			return result;
		}
		result.setItems(dataSetDao.getKeyListFrom(versionId, fromKey, max+1));
		if (result.getItems().size()>max) {
			result.setHasMore(true);
			result.getItems().remove(result.getItems().size()-1);
		}
		return result;
	}

	@Override
	public Flux<String> streamKeysFromActiveDataSet(String dataSetName) {
		String dataSetVersionId=dataSetDao.getActiveDataSetVersionForDataSetName(dataSetName);
		if (dataSetVersionId==null) return null;
		return dataSetDao.streamDataSetKeys(dataSetVersionId);
	}
	
	@Override
	public List<String> getParameterNamesForActiveDataSet(String dataSetName)  {
		String versionId=dataSetDao.getActiveDataSetVersionForDataSetName(dataSetName);
		if (versionId==null) {
			return new ArrayList<>();
		}
		return dataSetDao.getDataSetParameterNames(versionId);
	}
	
	@Override
	public ScrollItems<String[]> getRowsForActiveLookupDataSet(String dataSetName, String fromKey, int max) {
		ScrollItems<String[]> result=new ScrollItems<>();
		String versionId=dataSetDao.getActiveDataSetVersionForDataSetName(dataSetName);
		if (versionId==null) {
			result.setItems(new ArrayList<>());
			result.setHasMore(false);
			return result;
		}
		int columnCount=dataSetDao.getDataSetParameterNames(versionId).size();
		if (columnCount==0) {
			result.setItems(new ArrayList<>());
			result.setHasMore(false);
			return result;
		}
		List<String[]> rows=dataSetDao.getDataSetValues(versionId, columnCount, fromKey, max+1);
		
		result.setItems(rows);
		if (result.getItems().size()>max) {
			result.setHasMore(true);
			result.getItems().remove(result.getItems().size()-1);
		}
		return result;
	}
	
	@Override
	public Flux<String[]> streamRowsFromActiveLookupDataSet(String dataSetName) {
		String dataSetVersionId=dataSetDao.getActiveDataSetVersionForDataSetName(dataSetName);
		if (dataSetVersionId==null) return null;
		int columnCount=dataSetDao.getDataSetParameterNames(dataSetVersionId).size();
		if (columnCount==0) {
			return null;
		}
		return dataSetDao.streamDataSetRows(dataSetVersionId, columnCount);
	}
	
	@Override
	public DataSetLookupResult lookup(String dataSetName, String key) {
		String dataSetVersionId=dataSetDao.getActiveDataSetVersionForDataSetName(dataSetName);
		DataSetLookupResult result=new DataSetLookupResult();
		if (dataSetVersionId==null) return result;
		result.setDataSetFound(true);
		result.setValues(dataSetDao.getDataSetLookupRow(dataSetVersionId, key));
		result.setKeyFound(result.getValues().size()>0);
		return result;
	}

}
