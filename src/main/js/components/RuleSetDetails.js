import React from "react";
import { Button, Spinner, Tabs, Tab } from "react-lightning-design-system";
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
		const tableRowStyle={borderBottom: 'solid',borderBottomWidth: '1px',borderBottomColor:'#CCCCCC'}
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
			</Tabs>
			<div style={{backgroundColor: "white"}}>
				{(activeTab=="inputParameters") &&
					<table>
					<tr>
						<td width="200"><b>Parameter</b></td>
						<td width="90"><b>Type</b></td>
						<td width="200"><b>Default Value</b></td>
						<td>&nbsp;</td>
					</tr>
					{Object.keys(d.inputParameters).map((parameterName, index) => (
						<tr style={tableRowStyle}>
							<td>{parameterName}</td>
							<td>{d.inputParameters[parameterName].type}</td>
							<td>{d.inputParameters[parameterName].defaultValue}</td>
							<td>&nbsp;</td>
						</tr>
					))}
					</table>
				}
			</div>
		</div>
 )}

}
