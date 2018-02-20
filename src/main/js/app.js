import React from "react";

import { HashRouter,  Route } from 'react-router-dom';
import {Provider} from "react-redux";

import App from "./containers/App";
import store from "./store";

render(
    <Provider store={store}>
    	<HashRouter>
    		<Route path='/' component={App} />
      </HashRouter>
    </Provider>,
    window.document.getElementById('react')
);
