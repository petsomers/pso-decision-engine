import {applyMiddleware, combineReducers, createStore} from "redux";
import logger from 'redux-logger'
import promiseMiddleware from 'redux-promise-middleware';

import appReducer from "./reducers/appReducer";

export default createStore(
    combineReducers({
            appReducer
    }),
    {},
    applyMiddleware(promiseMiddleware(), logger)
);