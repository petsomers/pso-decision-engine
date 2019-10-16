import React from "react";
import { Button, Spinner, Tabs, Tab, Modal, ModalHeader, ModalFooter, ModalContent } from "react-lightning-design-system";
import { InputParameters } from "./rulesetdetails/InputParameters"
import { Rules } from "./rulesetdetails/Rules"
import { Lists } from "./rulesetdetails/Lists"
import { UnitTests } from "./rulesetdetails/UnitTests"
import { RunRuleSet }  from "./rulesetdetails/RunRuleSet"
import axios from "axios"

export class  RuleSetDetails extends React.Component {
	constructor(props) {
		super();
		this.state = {
			activeTab: "inputParameters",
			confirmAction: null
		}
	}

	setActiveTab(tab) {
		this.setState({
			...this.state,
			activeTab:tab
		});
	}

	runUnitTests() {
		let d=this.props.ruleSetDetails;
		this.props.runUnitTests(d.restEndpoint, d.id);
	}

	runNow() {
		let d=this.props.ruleSetDetails;
		this.props.runNow(d.restEndpoint, d.id, this.props.runNowData.parameterValues);
	}

	setActiveRequest() {
		this.setState({
			...this.state,
			confirmAction: {
				title: "Deploy Ruleset",
				info: "Are you sure you want to promote this rule set version to be the active one?",
				action: "SET_ACTIVE"
			}
		});
	}

	deleteRequest() {
		this.setState({
			...this.state,
			confirmAction: {
				title: "Delete Ruleset",
				info: "Are you sure you want to delete this rule set?",
				action: "DELETE"
			}
		});
	}

	cancelAction() {
		this.setState({
			...this.state,
			confirmAction: null
		});
	}

	confirmAction() {
		if (!this.state.confirmAction) return;
		this.cancelAction();
		let d=this.props.ruleSetDetails;
		if (this.state.confirmAction.action=="SET_ACTIVE") {
			this.props.setActive(d.restEndpoint, d.id);
		} else if (this.state.confirmAction.action=="DELETE") {
			this.props.deleteRuleSet(d.restEndpoint, d.id);
		}
	}


	render() {
		let d=this.props.ruleSetDetails;
		let activeTab=this.state.activeTab;

		return (
		<div>
			<div style={{display: "inline-block", width: "350px"}}>
				<b>Ruleset Name:</b> {d.name}<br />
				{(activeTab=="inputParameters") &&
				<div>
					<b>Id:</b> {d.id}<br />
					<b>Rest Endpoint:</b> {d.restEndpoint}<br />
					<b>Created By:</b> {d.createdBy}<br />
					<b>Version:</b> {d.version}<br />
				</div>
				}
			</div>
			<div style={{display: "inline-block"}}>
				<b>Upload Date:</b> {d.uploadDate}<br />
				{(activeTab=="inputParameters") &&
				<div>
					<b># of Input Parameters:</b> {Object.keys(d.inputParameters).length}<br />
					<b># of Rules:</b> {d.rules.length}<br />
					<b># of Lists:</b> {Object.keys(d.lists).length}<br />
					<b># of Unit Tests:</b> {d.unitTests.length}<br />
				</div>
				}
			</div>
			{(activeTab=="inputParameters") &&
				<div style={{display: "inline-block", paddingLeft: "25px"}}>
					<Button type="neutral" onClick={() => this.props.downloadExcel()} icon="download" iconAlign="left" label="Download Excel" />
					<br />
					<Button type="neutral" onClick={() => this.setActiveRequest()} icon="connected_apps" iconAlign="left" label="Deploy" />
					<br />
					<Button type="neutral" onClick={() => this.deleteRequest()} icon="delete" iconAlign="left" label="Delete" />
				</div>
			}
			{(activeTab=="inputParameters") &&
				<div>
					<b>Remark:</b> {d.remark}<br />
				</div>
			}
			<Tabs type="default" defaultActiveKey={activeTab}  onSelect={(tabName) => this.setActiveTab(tabName)}>
				<Tab eventKey="inputParameters" title="Input Parameters"/>
				<Tab eventKey="rules" title="Rules" />
				<Tab eventKey="lists" title="Lists" />
				<Tab eventKey="unitTests" title="Unit Tests" />
				<Tab eventKey="runNow" title="Run" />
			</Tabs>
			<div style={{backgroundColor: "white"}}>
				{(activeTab=="inputParameters") &&
					<InputParameters inputParameters={d.inputParameters} />
				}
				{(activeTab=="rules") &&
					<Rules rules={d.rules} />
 				}
				{(activeTab=="lists") &&
					<Lists lists={d.lists} />
 				}
				{(activeTab=="unitTests") &&
					<UnitTests
						unitTests={d.unitTests}
						unitTestsResult={this.props.unitTestsResult}
						runUnitTests={() => this.runUnitTests()}
						selectUnitTest={(unitTestNumber) => this.props.selectUnitTest(unitTestNumber)}
						selectedUnitTest={this.props.selectedUnitTest}
						/>
 				}
				{(activeTab=="runNow") &&
					<RunRuleSet
						inputParameters={d.inputParameters}
						runNowData={this.props.runNowData}
						setRunNowParameterValue={(name, value) => this.props.setRunNowParameterValue(name, value)}
						runNow={() => this.runNow()}
						runNowClearResult={() => this.props.runNowClearResult()}
					/>
				}
				{this.state.confirmAction &&
					<Modal opened>
						<ModalHeader title={this.state.confirmAction.title} />
						<ModalContent>
							<div className="slds-p-around--small">
								{this.state.confirmAction.info}
							</div>
						</ModalContent>
						<ModalFooter directional={null}>
							<Button type="neutral" label="Cancel" onClick={() => this.cancelAction()}/>
							<Button type="brand" label="OK" onClick={() => this.confirmAction()}/>
						</ModalFooter>
					</Modal>
				}
			</div>
		</div>
	)
	}
}
