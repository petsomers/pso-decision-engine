package pso.decision_engine.presentation;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

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
	
	@RequestMapping(value = "/upload_set/{dataSetName}",method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public String uploadSet(HttpServletRequest req, @PathVariable String dataSetName) throws Exception {
		try {
			DataSetUploadResult result=dataSetService.uploadSet(dataSetName, req.getInputStream());
			if (result.isOk()) return "OK";
			return "ERROR: "+result.getErrorMessage();
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR: "+e.getMessage();
		}
	}
	

	@RequestMapping(method = RequestMethod.POST, path = "/form_upload_set/{dataSetName}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = "application/json;charset=UTF-8")
    public DataSetUploadResult formUploadSet(HttpServletRequest req, @PathVariable String dataSetName) throws Exception {
		DataSetUploadResult result=new DataSetUploadResult();
		try {
			Collection<Part> parts = req.getParts();
			if (parts.isEmpty()) {
				result.setErrorMessage("Invalid Request.");
				return result;
	        }
			Part part = parts.iterator().next();
			try (InputStream in = part.getInputStream()) {
				return dataSetService.uploadSet(dataSetName, in);
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
	
	@RequestMapping(value = "/download/{dataSetName}",method = RequestMethod.GET, produces = "application/octetstream")
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
		resp.setHeader("Content-Disposition", "attachment; filename="+ dataSetName+".txt");
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
}
