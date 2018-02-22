import React from "react";
import {connect} from "react-redux";
import { Route } from 'react-router-dom';
import ResizeAware from 'react-resize-aware';
import { NavigationBar } from '../components/NavigationBar'
import { RuleSetSelection } from '../components/RuleSetSelection'
import { FileUpload } from '../components/FileUpload'
import { RuleSetVersionSelection } from '../components/RuleSetVersionSelection'
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
		const mainScreen={marginLeft:(this.props.layout.leftPaneWidth+15)+"px", marginTop:"60px"};
    return (
			<ResizeAware
    		style={{ position: 'absolute',top:"0px", left:"0px", height:"100%", width:"100%" }}
				onResize={this.handleResize}>
					<div>
					<NavigationBar />
					<RuleSetSelection
						restEndpoints={this.props.restEndpoints}
						layout={this.props.layout}
						openFileUpload={() => this.props.openFileUpload()}
						selectEndpoint={(endpoint) => this.props.selectEndpoint(endpoint)}
						/>
					</div>
					<div style={mainScreen}>
					{this.props.mainScreen=="upload" &&
						<FileUpload
							setSelectedVersion={(endpoint, version) => this.props.setSelectedVersion(endpoint, version)}
						/>
					}
					{this.props.mainScreen=="ruleSetVersionSelection" &&
						<RuleSetVersionSelection
							layout={this.props.layout}
							selectedEndpoint={this.props.selectedEndpoint}
							versions={this.props.versions}
							selectVersion={(endpoint, versionId) => this.props.selectVersion(endpoint, versionId)}
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
		selectedEndpoint: state.appReducer.selectedEndpoint,
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
		},
		selectEndpoint: (endpoint) => {
			console.log("LOAD VERSIONS for "+endpoint);
			dispatch({
				type: "SET_SELECTED_ENDPOINT",
				payload: endpoint
			});
			dispatch({
  			type: "GET_VERSIONS",
  			payload: axios.get("setup/versions/"+endpoint)
			});
		},
		selectVersion: (endpoint, versionId) => {
			console.log("LOAD VERSIONS for "+endpoint+"/"+versionId);
		}
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
