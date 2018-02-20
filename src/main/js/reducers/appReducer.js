const appReducer = (state = {
  layout: {
    windowHeight: 0,
    windowWidth: 0
  },
  restEndPoints: [],
  versions: []
}, action) => {
switch (action.type) {
  case "PREPARE_COUNTRY_DATA":
		state = {...state,
		};
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
