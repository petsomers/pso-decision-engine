package pso.decision_engine.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pso.decision_engine.model.DataSetInfo;
import pso.decision_engine.model.DataSetUploadResult;
import pso.decision_engine.model.ScrollItems;
import pso.decision_engine.model.enums.DataSetType;
import pso.decision_engine.service.DataSetService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/dataset")
public class DataSetApi {

	@Autowired
	private DataSetService dataSetService;
	
	@RequestMapping(value = "/all",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<DataSetInfo> getDataSets() {
		return dataSetService.getDataSetNames();
	}
	
	@RequestMapping(value = "/upload/{dataSetType}/{dataSetName}",method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public String uploadSet(HttpServletRequest req, 
    		@PathVariable String dataSetType, 
    		@PathVariable String dataSetName) throws Exception {
		try {
			DataSetType dataSetTypeE=DataSetType.fromString(dataSetType);
			if (dataSetTypeE==null) {
				return "ERROR: invalid dataSetType";
			}
			DataSetUploadResult result=dataSetService.uploadDataSet(dataSetName, dataSetTypeE, req.getInputStream());
			if (result.isOk()) return "OK";
			return "ERROR: "+result.getErrorMessage();
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR: "+e.getMessage();
		}
	}
	

	@RequestMapping(method = RequestMethod.POST, path = "/form_upload/{dataSetType}/{dataSetName}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = "application/json;charset=UTF-8")
    public DataSetUploadResult formUploadSet(HttpServletRequest req, 
    		@PathVariable String dataSetType, 
    		@PathVariable String dataSetName) throws Exception {
		DataSetUploadResult result=new DataSetUploadResult();
		DataSetType dataSetTypeE=DataSetType.fromString(dataSetType);
		if (dataSetTypeE==null) {
			result.setErrorMessage("ERROR: invalid dataSetType");
			return result;
		}
		try {
			Collection<Part> parts = req.getParts();
			if (parts.isEmpty()) {
				result.setErrorMessage("Invalid Request.");
				return result;
	        }
			Part part = parts.iterator().next();
			try (InputStream in = part.getInputStream()) {
				return dataSetService.uploadDataSet(dataSetName, dataSetTypeE, in);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErrorMessage(e.getMessage());
			return result;
		}
	}
	
	@RequestMapping(value = "/keys/{dataSetName}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public ScrollItems<String> keys(@PathVariable String dataSetName, @RequestParam(required=false) String fromKey) {
		if (fromKey!=null && fromKey.trim().length()==0) fromKey=null;
		return dataSetService.getKeysFromActiveDataSet(dataSetName, fromKey, 70);
	}
	
	@RequestMapping(value = "/download/LIST/{dataSetName}",method = RequestMethod.GET, produces = "application/octetstream")
	public void downloadDataSet(@PathVariable String dataSetName, HttpServletResponse resp) throws IOException {
		Flux<String> f=dataSetService.streamKeysFromActiveDataSet(dataSetName);
		if (f==null) {
			resp.sendError(404);
			return;
		}
		resp.setHeader("pragma", "no-cache");
		resp.setHeader( "Cache-Control","no-cache" );
		resp.setHeader( "Cache-Control","no-store" );
		resp.setDateHeader( "Expires", 0 );
		resp.setContentType("application/octetstream");
		resp.setHeader("Content-Disposition", "attachment; filename=\""+ dataSetName+".txt\"");
		OutputStreamWriter ow=new OutputStreamWriter(resp.getOutputStream(), "UTF-8");
		f.buffer(100).subscribe( values -> {
			StringBuilder batch=new StringBuilder();
			values.forEach(value -> {
				batch.append(value);
				batch.append("\r\n");
			});
			try {
				ow.write(batch.toString());
			} catch (IOException e) { // user cancelled the download => stop db query
				throw new RuntimeException(e);
			}
		});
		ow.flush();
	}
	
	@RequestMapping(value = "/download/LOOKUP/{dataSetName}",method = RequestMethod.GET, produces = "application/octetstream")
	public void downloadLookupDataSet(@PathVariable String dataSetName, HttpServletResponse resp) throws IOException {
		Flux<String[]> f=dataSetService.streamRowsFromActiveLookupDataSet(dataSetName);
		if (f==null) {
			resp.sendError(404);
			return;
		}
		List<String> columnHeaders=dataSetService.getParameterNamesForActiveDataSet(dataSetName);
		resp.setHeader("pragma", "no-cache");
		resp.setHeader( "Cache-Control","no-cache" );
		resp.setHeader( "Cache-Control","no-store" );
		resp.setDateHeader( "Expires", 0 );
		resp.setContentType("application/octetstream");
		resp.setHeader("Content-Disposition", "attachment; filename=\""+ dataSetName+".txt\"");
		
		// write header row
		OutputStreamWriter ow=new OutputStreamWriter(resp.getOutputStream(), "UTF-8");
		ow.write(String.join("\t", columnHeaders));
		ow.write("\r\n");

		f.buffer(100).subscribe( rows -> {
			StringBuilder batch=new StringBuilder();
			rows.forEach(row -> {
				batch.append(String.join("\t", row));
				batch.append("\r\n");
			});
			try {
				ow.write(batch.toString());
			} catch (IOException e) { // user cancelled the download => stop db query
				throw new RuntimeException(e);
			}
		});
		ow.flush();
	}
	
	@RequestMapping(value = "/headers/LOOKUP/{dataSetName}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<String> headers(@PathVariable String dataSetName) {
		return dataSetService.getParameterNamesForActiveDataSet(dataSetName);
	}
	
	@RequestMapping(value = "/data/LOOKUP/{dataSetName}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public ScrollItems<String[]> dataForLookupDataSet(@PathVariable String dataSetName, @RequestParam(required=false) String fromKey) {
		if (fromKey!=null && fromKey.trim().length()==0) fromKey=null;
		return dataSetService.getRowsForActiveLookupDataSet(dataSetName, fromKey, 70);
	}
	
	
	@RequestMapping(value = "/delete/{dataSetName}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void delete(@PathVariable String dataSetName) {
		dataSetService.deleteDataSet(dataSetName);
	}


}
