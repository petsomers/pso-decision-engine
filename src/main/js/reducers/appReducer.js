const appReducer = (state = {
  layout: {
    windowHeight: 0,
    windowWidth: 0,
    leftPaneWidth: 300
  },
  errorMessage: "",
  mainScreen: "welcome",
  restEndpoints: [],
  versions: [],
  inprogress: false,
  selectedEndpoint: "",
  selectedVersion: "",
  ruleSetDetails: null,
  unitTestsResult: null,
  runNowData: {
    parameterValues: {},
    result: null
  }
}, action) => {
  if (action.type.endsWith("_FULFILLED")) {
    if (action.payload && action.payload.data && action.payload.data.error && !action.payload.data.run) {
      state={...state, inprogress: false, errorMessage: action.payload.data.errorMessage};
      return state;
    }
  }
  if (action.type.endsWith("_REJECTED")) {
    console.error("Server call failed:", action);
    let errorMessage="Server call failed.";
    if (action.payload && action.payload && action.payload.message) {
      errorMessage=action.payload.message;
    }
    state={...state, inprogress: false, errorMessage: errorMessage};
    return state;
  }
  switch (action.type) {
  case "CLEAR_ERROR_MESSAGE":
    state= {...state, errorMessage:null}
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
    if (selectedEndpoint=="" && endpoints.length>0) {
      selectedEndpoint=endpoints[0];
    }
    state = {
      ...state,
      inprogress: false,
      selectedEndpoint:selectedEndpoint,
      restEndpoints:endpoints
    }
  break;
  case "GET_VERSIONS_FULFILLED":
    state = {
      ...state,
      inprogress: false,
      versions:action.payload.data
    }
  break;
  case "GET_RULESET_FULFILLED":
    let ruleSetResp=action.payload.data
    state = {
      ...state,
      inprogress: false,
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
      inprogress: false,
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
