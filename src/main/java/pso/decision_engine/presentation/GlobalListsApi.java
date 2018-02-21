package pso.decision_engine.presentation;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/global_list")
public class GlobalListsApi {

	@RequestMapping(value = "/lists",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<String> getAllGlobalListNames() {
		return new ArrayList<>();
	}
	
	@RequestMapping(value = "/list/{listName}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<String> getGlobalList(@PathVariable String listName) {
		return new ArrayList<>();
	}
	
	// upload stream data list
	// separated by line feeds (\r\n)
	// returns true for success
	@RequestMapping(value = "/list/{listName}",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public boolean uploadGlobalListData(HttpServletRequest req,@PathVariable String listName) {
		// step 1: store list in temp data
		// step 2: replace existing list data with this data
		
		// => allows external job to replicate a list from a database and upload that into the decision engine
		// as an alternative for the SQL data
		
		return true;
	}
}
