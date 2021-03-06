import axios from "axios";

const mapDispatchToProps = (dispatch) => {
	const axiosConfig = window.axiosConfig;
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
  			payload: axios.get("setup/endpoints", axiosConfig)
			});
		},
		loadDataSets: () => {
			console.log("LOAD LISTS.....");
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "GET_DATASETS",
  			payload: axios.get("dataset/all", axiosConfig)
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
  			payload: axios.get("setup/versions/"+endpoint, axiosConfig)
			});
		},
		selectVersion: (endpoint, versionId) => {
			console.log("LOAD RULESET for "+endpoint+"/"+versionId);
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "GET_RULESET",
  			payload: axios.get("setup/source/"+encodeURIComponent(endpoint)+"/"+encodeURIComponent(versionId), axiosConfig)
			});
		},
		runUnitTests: (endpoint, versionId) => {
			console.log("RUNNING UNIT TESTS for "+endpoint+"/"+versionId);
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "RUN_UNIT_TESTS",
  			payload: axios.get("processor/run_unittests/"+encodeURIComponent(endpoint)+"/"+encodeURIComponent(versionId), axiosConfig)
			});
		},
		selectUnitTest: (unitTestNumber) => {
			dispatch({
  			type: "SELECT_UNIT_TEST",
  			payload: unitTestNumber
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
			Object.keys(parameters).forEach((parameterName, index) => {
				parameterStr+= "&"+parameterName+'='+encodeURIComponent(parameters[parameterName]);
			});
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "RUN_NOW",
				payload: axios.get("processor/json/run/"+endpoint+"/"+versionId+"?trace=Y&"+parameterStr, axiosConfig)
			});
		},
		runNowClearResult: () => {
			dispatch({type: "RUN_NOW_CLEAR_RESULT"});
		},
		setActive: (endpoint, versionId) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "SET_ACTIVE",
				payload: axios.get("setup/setactive/"+encodeURIComponent(endpoint)+"/"+encodeURIComponent(versionId), axiosConfig)
			});
		},
		deleteInactive: (endpoint) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "DELETE_INACTIVE",
				payload: axios.get("setup/delete/inactive/"+encodeURIComponent(endpoint), axiosConfig)
			});
		},
		deleteEndpoint: (endpoint) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "DELETE_ENDPOINT",
				payload: axios.get("setup/delete/endpoint/"+encodeURIComponent(endpoint), axiosConfig)
			});
		},
		deleteRuleSet: (endpoint, versionId) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "DELETE_RULESET",
				payload: axios.get("setup/delete/ruleset/"+encodeURIComponent(endpoint)+"/"+encodeURIComponent(versionId), axiosConfig)
			});
		},
		selectDataSet: (dataSetInfo) => {
			dispatch({type: "SELECT_DATA_SET", payload: dataSetInfo});
			if (dataSetInfo.type==="LOOKUP") {
				dispatch({
					type: "LOAD_DATA_SET_DATA_HEADERS",
					payload: axios.get("dataset/headers/LOOKUP/"+encodeURIComponent(dataSetInfo.name), axiosConfig)
				});
				dispatch({
					type: "LOAD_DATA_SET_DATA",
					payload: axios.get("dataset/data/LOOKUP/"+encodeURIComponent(dataSetInfo.name), axiosConfig)
				});
			} else {
				// type = LIST
				dispatch({
					type: "LOAD_DATA_SET_KEYS",
					payload: axios.get("dataset/keys/"+encodeURIComponent(dataSetInfo.name), axiosConfig)
				});
			}
		},
    loadMoreDataSetData: (dataSetInfo, fromKey) => {
			if (dataSetInfo.type==="LOOKUP") {
	      dispatch({
					type: "LOAD_MORE_DATA_SET_DATA",
					payload: axios.get("dataset/data/LOOKUP/"+encodeURIComponent(dataSetInfo.name)+"?fromKey="+encodeURIComponent(fromKey), axiosConfig)
				});
			} else {
				dispatch({
					type: "LOAD_MORE_DATA_SET_KEYS",
					payload: axios.get("dataset/keys/"+encodeURIComponent(dataSetInfo.name)+"?fromKey="+encodeURIComponent(fromKey), axiosConfig)
				});
			}
    },
		deleteDataSet: (dataSetInfo) => {
			dispatch({
				type: "DELETE_DATA_SET",
				payload: axios.get("dataset/delete/"+encodeURIComponent(dataSetInfo.name), axiosConfig)
			});
		}
	}
};

export default mapDispatchToProps;
