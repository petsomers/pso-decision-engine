import React from "react";
import {connect} from "react-redux";
import { Route } from 'react-router-dom';
import ResizeAware from 'react-resize-aware';
import { NavigationBar } from '../components/NavigationBar'
import { RuleSetSelection } from '../components/RuleSetSelection'
import { FileUpload }from '../components/FileUpload'
import axios from "axios"

class App extends React.Component {

	constructor(props) {
		super();
		this.state={
		}
	}

	componentDidMount() {
		this.props.loadEndpoints();
	}

	handleResize = ({ width, height }) => this.props.onWindowResize(width, height);

	render() {
		const mainScreen={position:"fixed", top:"55px", left:"310px"};
    return (
			<ResizeAware
    		style={{ position: 'absolute',top:"0px", left:"0px", height:"100%", width:"100%" }}
				onResize={this.handleResize}>
				<div>
					<NavigationBar />
					<RuleSetSelection
						restEndpoints={this.props.restEndpoints}
						versions={this.props.versions}
						layout={this.props.layout}
						openFileUpload={() => this.props.openFileUpload()}
						/>
							Hallo
					</div>
					<div style={mainScreen}>
					{this.props.mainScreen=="upload" &&
						<FileUpload
							setSelectedVersion={(endpoint, version) => this.props.setSelectedVersion(endpoint, version)}
						/>
					}
					</div>
				</ResizeAware>
	);
  }
}

const mapStateToProps = (state) => {
  return {
		layout: state.appReducer.layout,
		restEndpoints: state.appReducer.restEndpoints,
		versions: state.appReducer.versions,
		layout: state.appReducer.layout,
		mainScreen: state.appReducer.mainScreen
  };
};

const mapDispatchToProps = (dispatch) => {
	return {
		onWindowResize: (width, height) => {
			dispatch({
				type: "WINDOW_RESIZE",
				payload: {width, height}
			});
		},
		openFileUpload: (width, height) => {
			dispatch({
				type: "GOTO_UPLOAD",
				payload: {width, height}
			});
		},
		setSelectedEndpoint: (endpoint) => {
			dispatch({
				type: "SET_SELECTED_endpoint",
				payload: endpoint
			});
		},
		setSelectedVersion: (endpoint, version) => {
			dispatch({
				type: "SET_SELECTED_VERSION",
				payload: {endpoint,version}
			});
		},
		loadEndpoints: () => {
			console.log("LOAD ENDPOINTS.....");
			dispatch({
  			type: "GET_ENDPOINTS",
  			payload: axios.get("setup/endpoints")
			});
		}
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
