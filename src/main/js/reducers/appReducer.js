const appReducer = (state = {
}, action) => {
switch (action.type) {
  case "PREPARE_COUNTRY_DATA":
		state = {...state,
		};
		break;
  }
  return state;
};


export default appReducer;
