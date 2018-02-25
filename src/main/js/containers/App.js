import React from "react";
import {connect} from "react-redux";
import { Route } from 'react-router-dom';
import ResizeAware from 'react-resize-aware';
import { Spinner, Notification } from "react-lightning-design-system";
import { NavigationBar } from '../components/NavigationBar'
import { RuleSetSelection } from '../components/RuleSetSelection'
import { FileUpload } from '../components/FileUpload'
import { RuleSetVersionSelection } from '../components/RuleSetVersionSelection'
import { RuleSetDetails} from '../components/RuleSetDetails'
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
		const mainScreen={marginLeft:(this.props.layout.leftPaneWidth+15)+"px", marginTop:"70px"};
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
					{this.props.mainScreen=="ruleSetDetails" &&
						<RuleSetDetails
							ruleSetDetails={this.props.ruleSetDetails}
							runUnitTests={(endpoint, versionId) => this.props.runUnitTests(endpoint, versionId)}
							unitTestsResult={this.props.unitTestsResult}
							setRunNowParameterValue={(name, value) => this.props.setRunNowParameterValue(name, value)}
							runNowData={this.props.runNowData}
							runNow={(endpoint, versionId, parameters) => this.props.runNow(endpoint, versionId, parameters)}
						/>
					}
					{this.props.inprogress &&
						<Spinner />
					}
					{(this.props.errorMessage!=null && this.props.errorMessage!="") &&
  				<div style={{position: "fixed", zIndex:"100", right:"50px", bottom:"50px", width:"50%" }}>
    				<Notification
    				  type="alert"
    				  level="error"
    				  alertTexture
    				  onClose={()=>this.props.doClearErrorMessage()}>
    					{this.props.errorMessage}
    				 </Notification>
  				 </div>
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
		ruleSetDetails: state.appReducer.ruleSetDetails,
		unitTestsResult: state.appReducer.unitTestsResult,
		runNowData: state.appReducer.runNowData,
		layout: state.appReducer.layout,
		mainScreen: state.appReducer.mainScreen,
		errorMessage: state.appReducer.errorMessage
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
		doClearErrorMessage() {
			dispatch({type: "CLEAR_ERROR_MESSAGE"});
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
			dispatch({type: "SET_INPROGRESS"});
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
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "GET_VERSIONS",
  			payload: axios.get("setup/versions/"+endpoint)
			});
		},
		selectVersion: (endpoint, versionId) => {
			console.log("LOAD VERSIONS for "+endpoint+"/"+versionId);
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "GET_RULESET",
  			payload: axios.get("setup/source/"+endpoint+"/"+versionId)
			});
		},
		runUnitTests: (endpoint, versionId) => {
			console.log("RUNNING UNIT TESTS for "+endpoint+"/"+versionId);
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "RUN_UNIT_TESTS",
  			payload: axios.get("run_unittests/"+endpoint+"/"+versionId)
			});
		},
		setRunNowParameterValue: (name, value) => {
			dispatch({
  			type: "SET_RUNNOW_PARAMETER_VALUE",
  			payload: {parameterName: name, parameterValue: value}
			});
		},
		runNow: (endpoint, versionId, parameters) => {
			var parameterStr="";
			Object.keys(parameters).map((parameterName, index) => {
				parameterStr+= "&"+parameterName+'='+encodeURIComponent(parameters[parameterName]);
			});
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "run_now",
				payload: axios.get("json/run/"+endpoint+"/"+versionId+"?trace=Y&"+parameterStr)
			});
		}
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
