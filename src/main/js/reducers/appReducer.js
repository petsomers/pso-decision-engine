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
  ruleSetDetails: null
}, action) => {
  if (action.type.endsWith("_FULFILLED")) {
    if (action.payload && action.payload.data && action.payload.data.error) {
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
