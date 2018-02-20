import {createStore, combineReducers, applyMiddleware} from "redux";
import logger from 'redux-logger'
import promiseMiddleware from 'redux-promise-middleware';
import thunk from 'redux-thunk';

import appReducer from "./reducers/appReducer";

export default createStore(
    combineReducers({
		    appReducer
    }),
    {},
    applyMiddleware(promiseMiddleware(), thunk, logger)
);
