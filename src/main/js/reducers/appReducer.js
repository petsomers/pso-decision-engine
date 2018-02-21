const appReducer = (state = {
  layout: {
    windowHeight: 0,
    windowWidth: 0
  },
  mainScreen: "welcome",
  restEndPoints: [],
  versions: [],
  selectedEndPoint: "",
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
      ...state, selectedEndPoint: action.payload
    }
  break;
  case "SET_SELECTED_VERSION":
    state = {
      ...state,
      selectedEndPoint: action.payload.endpoint,
      selectedVersion: action.payload.version
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
