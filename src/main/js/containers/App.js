import React from "react";
import {connect} from "react-redux";
import { Route } from 'react-router-dom';
import ResizeAware from 'react-resize-aware';
import { Spinner, Notification } from "react-lightning-design-system";
import mapDispatchToProps from "../actions/appActions"
import { NavigationBar } from '../components/NavigationBar'
import { RuleSetSelection } from '../components/RuleSetSelection'
import { FileUpload } from '../components/FileUpload'
import { RuleSetVersionSelection } from '../components/RuleSetVersionSelection'
import { RuleSetDetails} from '../components/RuleSetDetails'
import { DataSetDetails } from '../components/DataSetDetails'
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
		window.location.href="setup/download_excel/"+encodeURIComponent(restEndpoint)+"/"+encodeURIComponent(ruleSetId);
	}

	downloadDataSet(dataSetInfo) {
		window.location.href="dataset/download/"+dataSetInfo.type+"/"+encodeURIComponent(dataSetInfo.name);
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
						selectedDataSetInfo={this.props.selectedDataSetInfo}
						selectEndpoint={(endpoint) => this.props.selectEndpoint(endpoint)}
						selectDataSet={(dataSetInfo) => this.props.selectDataSet(dataSetInfo)}
						/>
					</div>
					<div style={mainScreen}>
					{this.props.mainScreen=="upload" &&
						<FileUpload
							selectVersion={(endpoint, versionId) => this.props.selectVersion(endpoint, versionId)}
							loadEndpoints={() => this.props.loadEndpoints()}
							dataSets={this.props.dataSets}
							loadDataSets={() => this.props.loadDataSets()}
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
					{this.props.mainScreen=="dataSetDetails" &&
						<DataSetDetails
							selectedDataSetInfo={this.props.selectedDataSetInfo}
							dataSetData={this.props.dataSetData}
							loadMoreDataSetData={(dataSetInfo, fromKey) => this.props.loadMoreDataSetData(dataSetInfo, fromKey)}
							downloadDataSet={(dataSetInfo) => this.downloadDataSet(dataSetInfo)}
							deleteDataSet={(dataSetInfo) => this.props.deleteDataSet(dataSetInfo)}
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
		selectedDataSetInfo:  state.appReducer.selectedDataSetInfo,
		ruleSetDetails: state.appReducer.ruleSetDetails,
		unitTestsResult: state.appReducer.unitTestsResult,
		runNowData: state.appReducer.runNowData,
		layout: state.appReducer.layout,
		mainScreen: state.appReducer.mainScreen,
		errorMessage: state.appReducer.errorMessage,
		infoMessage: state.appReducer.infoMessage,
		dataSetData: state.appReducer.dataSetData,
		loadMoreDataSetData: state.appReducer.loadMoreDataSetData,
		deleteDataSet: state.appReducer.deleteDataSet
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
