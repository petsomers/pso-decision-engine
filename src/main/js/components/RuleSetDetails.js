import React from "react";
import { Button, Spinner, Tabs, Tab } from "react-lightning-design-system";
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
			activeTab: "inputParameters"
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
				<div>
					<b>Remark:</b> {d.remark}<br />
				</div>
			}
			<Tabs type="default" defaultActiveKey={activeTab}  onSelect={(tabName) => this.setActiveTab(tabName)}>
				<Tab eventKey="inputParameters" title="Input Parameters" />
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
						runUnitTests={() => this.runUnitTests()} />
 				}
				{(activeTab=="runNow") &&
					<RunRuleSet
						inputParameters={d.inputParameters}
						runNowData={this.props.runNowData}
						setRunNowParameterValue={(name, value) => this.props.setRunNowParameterValue(name, value)}
						runNow={() => this.runNow()}
					/>
				}
			</div>
		</div>
	)
	}
}
