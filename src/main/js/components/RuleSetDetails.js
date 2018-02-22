import React from "react";
import { Button, Spinner, Tabs, Tab } from "react-lightning-design-system";
import { InputParameters } from "./rulesetdetails/InputParameters"
import { Rules } from "./rulesetdetails/Rules"
import { Lists } from "./rulesetdetails/Lists"
import { UnitTests } from "./rulesetdetails/UnitTests"

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

	render() {
		let d=this.props.ruleSetDetails;
		let activeTab=this.state.activeTab;

		return (
		<div>
			<div style={{display: "inline-block", width: "350px"}}>
				<b>Ruleset Name:</b> {d.name}<br />
				<b>Id:</b> {d.id}<br />
				<b>Rest Endpoint:</b> {d.restEndpoint}<br />
				<b>Created By:</b> {d.createdBy}<br />
				<b>Version:</b> {d.version}<br />
			</div>
			<div style={{display: "inline-block"}}>
				<b>Upload Date:</b> {d.uploadDate}<br />
				<b># of Input Parameters:</b> {Object.keys(d.inputParameters).length}<br />
				<b># of Rules:</b> {d.rules.length}<br />
				<b># of Lists:</b> {Object.keys(d.lists).length}<br />
				<b># of Unit Tests:</b> {d.unitTests.length}<br />
			</div>
			<div>
				<b>Remark:</b> {d.remark}<br />
			</div>
			<Tabs type="default" defaultActiveKey={activeTab}  onSelect={(tabName) => this.setActiveTab(tabName)}>
				<Tab eventKey="inputParameters" title="Input Parameters" />
				<Tab eventKey="rules" title="Rules" />
				<Tab eventKey="lists" title="Lists" />
				<Tab eventKey="unitTests" title="Unit Tests" />
				<Tab eventKey="runNow" title="Run" />
				<Tab eventKey="statistics" title="Usage Statistics" />
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
			</div>
		</div>
	)
	}
}
