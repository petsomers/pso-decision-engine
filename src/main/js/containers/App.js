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
		this.props.loadDataSets();
	}

	downloadExcel(restEndpoint, ruleSetId) {
		window.location.href="setup/download_excel/"+restEndpoint+"/"+ruleSetId;
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
						dataSets={this.props.dataSets}
						layout={this.props.layout}
						openFileUpload={() => this.props.openFileUpload()}
						selectedEndpoint={this.props.selectedEndpoint}
						selectedDataSet={this.props.selectedDataSet}
						selectEndpoint={(endpoint) => this.props.selectEndpoint(endpoint)}
						/>
					</div>
					<div style={mainScreen}>
					{this.props.mainScreen=="upload" &&
						<FileUpload
							selectVersion={(endpoint, versionId) => this.props.selectVersion(endpoint, versionId)}
							loadEndpoints={() => this.props.loadEndpoints()}
							dataSets={this.props.dataSets}
						/>
					}
					{this.props.mainScreen=="ruleSetVersionSelection" &&
						<RuleSetVersionSelection
							layout={this.props.layout}
							selectedEndpoint={this.props.selectedEndpoint}
							versions={this.props.versions}
							selectVersion={(endpoint, versionId) => this.props.selectVersion(endpoint, versionId)}
							downloadExcel={(restEndpoint, ruleSetId) => this.downloadExcel(restEndpoint, ruleSetId)}
							/>
					}
					{this.props.mainScreen=="ruleSetDetails" &&
						<RuleSetDetails
							ruleSetDetails={this.props.ruleSetDetails}
							downloadExcel={() => this.downloadExcel(this.props.ruleSetDetails.restEndpoint, this.props.ruleSetDetails.id)}
							runUnitTests={(endpoint, versionId) => this.props.runUnitTests(endpoint, versionId)}
							unitTestsResult={this.props.unitTestsResult}
							setRunNowParameterValue={(name, value) => this.props.setRunNowParameterValue(name, value)}
							runNowData={this.props.runNowData}
							runNow={(endpoint, versionId, parameters) => this.props.runNow(endpoint, versionId, parameters)}
							runNowClearResult={() => this.props.runNowClearResult()}
							setActive={(endpoint, versionId) => this.props.setActive(endpoint, versionId)}
							deleteRuleSet={(endpoint, versionId) => this.props.deleteRuleSet(endpoint, versionId)}
						/>
					}
					{this.props.inProgress &&
						<Spinner />
					}
					{(this.props.infoMessage && this.props.infoMessage!="") &&
						<div style={{position: "fixed", zIndex:"100", right:"50px", bottom:"50px", width:"50%" }}>
							<Notification
								type="toast"
								level="success"
								alertTexture
								onClose={()=>this.props.doClearMessages()}>
								{this.props.infoMessage}
						 </Notification>
						</div>
					}
					{(this.props.errorMessage && this.props.errorMessage!="") &&
  				<div style={{position: "fixed", zIndex:"100", right:"50px", bottom:"50px", width:"50%" }}>
    				<Notification
    				  type="alert"
    				  level="error"
    				  alertTexture
    				  onClose={()=>this.props.doClearMessages()}>
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
		inProgress: state.appReducer.inProgress,
		restEndpoints: state.appReducer.restEndpoints,
		dataSets: state.appReducer.dataSets,
		versions: state.appReducer.versions,
		selectedEndpoint: state.appReducer.selectedEndpoint,
		selectedDataSet:  state.appReducer.selectedDataSet,
		ruleSetDetails: state.appReducer.ruleSetDetails,
		unitTestsResult: state.appReducer.unitTestsResult,
		runNowData: state.appReducer.runNowData,
		layout: state.appReducer.layout,
		mainScreen: state.appReducer.mainScreen,
		errorMessage: state.appReducer.errorMessage,
		infoMessage: state.appReducer.infoMessage
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
		doClearMessages() {
			dispatch({type: "CLEAR_MESSAGES"});
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
		loadDataSets: () => {
			console.log("LOAD LISTS.....");
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
  			type: "GET_DATASETS",
  			payload: axios.get("setup/dataset/datasets")
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
			console.log("LOAD RULESET for "+endpoint+"/"+versionId);
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
				type: "RUN_NOW",
				payload: axios.get("json/run/"+endpoint+"/"+versionId+"?trace=Y&"+parameterStr)
			});
		},
		runNowClearResult: () => {
			dispatch({type: "RUN_NOW_CLEAR_RESULT"});
		},
		setActive: (endpoint, versionId) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "SET_ACTIVE",
				payload: axios.get("setup/setactive/"+endpoint+"/"+versionId)
			});
		},
		deleteInactive: (endpoint) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "DELETE_INACTIVE",
				payload: axios.get("setup/delete/inactive/"+endpoint)
			});
		},
		deleteEndpoint: (endpoint) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "DELETE_ENDPOINT",
				payload: axios.get("setup/delete/endpoint/"+endpoint)
			});
		},
		deleteRuleSet: (endpoint, versionId) => {
			dispatch({type: "SET_INPROGRESS"});
			dispatch({
				type: "DELETE_RULESET",
				payload: axios.get("setup/delete/ruleset/"+endpoint+"/"+versionId)
			});
		}
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
