import axios from "axios"

const mapDispatchToProps = (dispatch) => {
	return {
		onWindowResize: (width, height) => {
			dispatch({
				type: "WINDOW_RESIZE",
				payload: {width, height}
			});
		},
		doClearMessages() {
			dispatch({type: "CLEAR_MESSAGES"});
		},
		openFileUpload: (width, height) => {
			dispatch({
				type: "GOTO_UPLOAD",
				payload: {width, height}
			});
		},
		setSelectedVersion: (endpoint, version) => {
			dispatch({
				type: "SET_SELECTED_VERSION",
				payload: {endpoint,version}
			});
		},
		loadEndpoints: () => {
			console.log("LOAD ENDPOINTS.....");
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "GET_ENDPOINTS",
  			payload: axios.get("setup/endpoints")
			});
		},
		loadDataSets: () => {
			console.log("LOAD LISTS.....");
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "GET_DATASETS",
  			payload: axios.get("dataset/all")
			});
		},
		selectEndpoint: (endpoint) => {
			console.log("LOAD VERSIONS for "+endpoint);
			dispatch({
				type: "SET_SELECTED_ENDPOINT",
				payload: endpoint
			});
			//dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "GET_VERSIONS",
  			payload: axios.get("setup/versions/"+endpoint)
			});
		},
		selectVersion: (endpoint, versionId) => {
			console.log("LOAD RULESET for "+endpoint+"/"+versionId);
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "GET_RULESET",
  			payload: axios.get("setup/source/"+encodeURIComponent(endpoint)+"/"+encodeURIComponent(versionId))
			});
		},
		runUnitTests: (endpoint, versionId) => {
			console.log("RUNNING UNIT TESTS for "+endpoint+"/"+versionId);
			//dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "RUN_UNIT_TESTS",
  			payload: axios.get("run_unittests/"+encodeURIComponent(endpoint)+"/"+encodeURIComponent(versionId))
			});
		},
		setRunNowParameterValue: (name, value) => {
			dispatch({
  			type: "SET_RUNNOW_PARAMETER_VALUE",
  			payload: {parameterName: name, parameterValue: value}
			});
		},
		runNow: (endpoint, versionId, parameters) => {
			var parameterStr="";
			Object.keys(parameters).map((parameterName, index) => {
				parameterStr+= "&"+parameterName+'='+encodeURIComponent(parameters[parameterName]);
			});
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "RUN_NOW",
				payload: axios.get("json/run/"+endpoint+"/"+versionId+"?trace=Y&"+parameterStr)
			});
		},
		runNowClearResult: () => {
			dispatch({type: "RUN_NOW_CLEAR_RESULT"});
		},
		setActive: (endpoint, versionId) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "SET_ACTIVE",
				payload: axios.get("setup/setactive/"+encodeURIComponent(endpoint)+"/"+encodeURIComponent(versionId))
			});
		},
		deleteInactive: (endpoint) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "DELETE_INACTIVE",
				payload: axios.get("setup/delete/inactive/"+encodeURIComponent(endpoint))
			});
		},
		deleteEndpoint: (endpoint) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "DELETE_ENDPOINT",
				payload: axios.get("setup/delete/endpoint/"+encodeURIComponent(endpoint))
			});
		},
		deleteRuleSet: (endpoint, versionId) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "DELETE_RULESET",
				payload: axios.get("setup/delete/ruleset/"+encodeURIComponent(endpoint)+"/"+encodeURIComponent(versionId))
			});
		},
		selectDataSet: (dataSetInfo) => {
			dispatch({type: "SELECT_DATA_SET", payload: dataSetInfo});
      dispatch({
				type: "LOAD_DATA_SET_DATA",
				payload: axios.get("dataset/keys/"+encodeURIComponent(dataSetInfo.name))
			});
		},
    loadMoreDataSetData: (dataSetInfo, fromKey) => {
      dispatch({
				type: "LOAD_MORE_DATA_SET_DATA",
				payload: axios.get("dataset/keys/"+encodeURIComponent(dataSetInfo.name)+"?fromKey="+encodeURIComponent(fromKey))
			});
    }
	}
};

export default mapDispatchToProps;
