import React from "react";
import {HashRouter, Route} from 'react-router-dom';
import {Provider} from "react-redux";

import MainPage from "./containers/MainPage";
import store from "./store";

import './App.css';

function App() {
    return (
        <Provider store={store}>
            <HashRouter>
                <Route path='/' component={MainPage}/>
            </HashRouter>
        </Provider>
    );
}

export default App;