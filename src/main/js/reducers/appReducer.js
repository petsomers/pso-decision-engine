const appReducer = (state = {
  layout: {
    windowHeight: 0,
    windowWidth: 0,
    leftPaneWidth: 300
  },
  errorMessage: "",
  infoMessage: "",
  mainScreen: "welcome",
  restEndpoints: [],
  versions: [],
  inProgress: false,
  selectedEndpoint: "",
  selectedDataSetInfo: null,
  selectedVersion: "",
  ruleSetDetails: null,
  unitTestsResult: null,
  runNowData: {
    parameterValues: {},
    result: null
  },
  dataSets: []
}, action) => {
  if (action.type.endsWith("_FULFILLED")) {
    if (action.payload && action.payload.data && action.payload.data.error && !action.payload.data.run) {
      state={...state, inProgress: false, errorMessage: action.payload.data.errorMessage};
      return state;
    }
  }
  if (action.type.endsWith("_REJECTED")) {
    console.error("Server call failed:", action);
    let errorMessage="Server call failed.";
    if (action.payload && action.payload && action.payload.message) {
      errorMessage=action.payload.message;
    }
    state={...state, inProgress: false, errorMessage: errorMessage};
    return state;
  }
  switch (action.type) {
  case "CLEAR_MESSAGES":
    state= {...state, errorMessage:null, infoMessage:null}
  break;
  case "SET_INPROGRESS":
    state= {...state, inProgress: true}
  break;
  case "GOTO_UPLOAD":
		state = {...state,
      mainScreen: "upload",
		};
		break;
  case "SET_SELECTED_ENDPOINT":
    state = {
      ...state,
      selectedEndpoint: action.payload,
      mainScreen: "ruleSetVersionSelection"

    }
  break;
  case "SET_SELECTED_VERSION":
    state = {
      ...state,
      selectedEndpoint: action.payload.endpoint,
      selectedVersion: action.payload.version
    }
  break;
  case "GET_ENDPOINTS_FULFILLED":
    let selectedEndpoint=state.selectedEndpoint;
    let endpoints=action.payload.data;
    state = {
      ...state,
      inProgress: false,
      selectedEndpoint:selectedEndpoint,
      restEndpoints:endpoints
    }
  break;
  case "GET_DATASETS_FULFILLED":
    state = {
      ...state,
      inProgress: false,
      dataSets:action.payload.data
    }
  break;
  case "GET_VERSIONS_FULFILLED":
    state = {
      ...state,
      inProgress: false,
      versions:action.payload.data
    }
  break;
  case "GET_RULESET_FULFILLED":
    let ruleSetResp=action.payload.data
    state = {
      ...state,
      inProgress: false,
      selectedEndpoint:ruleSetResp.restEndpoint,
      selectedVersion:ruleSetResp.id,
      ruleSetDetails:ruleSetResp,
      unitTestsResult: null,
      runNowData: {
        parameterValues: {},
        result: null
      },
      mainScreen: "ruleSetDetails"
    }
  break;
  case "RUN_UNIT_TESTS_FULFILLED":
    let unitTestsResult=action.payload.data
    state = {
      ...state,
      inProgress: false,
      unitTestsResult: unitTestsResult,
    }
  break;
  case "SET_RUNNOW_PARAMETER_VALUE":
    state = {
      ...state,
      runNowData: {
        parameterValues: {
          ...state.runNowData.parameterValues,
          [action.payload.parameterName]:action.payload.parameterValue
        },
        result: null
      }
    }
  break;
  case "RUN_NOW_FULFILLED":
    state = {
      ...state,
      inProgress: false,
      runNowData: {
        ...state.runNowData,
        result: action.payload.data
      }
    }
  break;
  case "RUN_NOW_CLEAR_RESULT":
    state = {
      ...state,
      runNowData: {
          ...state.runNowData,
          result: null
      }
    }
  break;
  case "SET_ACTIVE_FULFILLED":
    state = {
      ...state,
      inProgress: false,
      infoMessage: "This ruleset has been promoted."
    }
  break;
  case "DELETE_RULESET_FULFILLED":
    // remove version from versionlist
    var pos=-1;
    state.versions.map((version, index) => {
      if (version.id==state.selectedVersion) {
        pos=index;
      }
    })
    if (pos>-1) {
      state.versions.splice(pos,1);
    }
    state = {
      ...state,
      inProgress: false,
      infoMessage: "The ruleset has been deleted.",
      mainScreen: "ruleSetVersionSelection",
      selectedVersion: ""
    }
  break;
  case "SELECT_DATA_SET":
    state = {
      ...state,
      selectedDataSetInfo: action.payload,
      mainScreen: "dataSetDetails"
    }
  break;
  case "WINDOW_RESIZE":
    if (action.payload.height!=state.layout.windowHeight
      || action.payload.width!=state.layout.windowWidth) {
      	state = {
    			...state,
          layout: {
            windowHeight: action.payload.height,
            windowWidth: action.payload.width
          }
        }
    	}
    break;
  }
  return state;
};


export default appReducer;
