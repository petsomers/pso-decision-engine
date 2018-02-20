import React from "react";
import {connect} from "react-redux";
import { Route } from 'react-router-dom';


class App extends React.Component {

	constructor(props) {
		super();
		this.state={
		}
	}

	render() {
        return (
			<div>
				Hello
			</div>
	);
  }
}

const mapStateToProps = (state) => {
  return {
  };
};

const mapDispatchToProps = (dispatch) => {
	return {
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
