const appReducer = (state = {
  layout: {
    windowHeight: 0,
    windowWidth: 0
  },
  mainScreen: "welcome",
  restEndpoints: [],
  versions: [],
  selectedEndpoint: "",
  selectedVersion: ""
}, action) => {
switch (action.type) {
  case "GOTO_UPLOAD":
		state = {...state,
      mainScreen: "upload",
		};
		break;
  case "SET_SELECTED_ENDPOINT":
    state = {
      ...state, selectedEndpoint: action.payload
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
      selectedEndpoint:selectedEndpoint,
      restEndpoints:endpoints
    }
  break;
  case "GET_VERSIONS_FULFILLED":
    state = {
      ...state,
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
