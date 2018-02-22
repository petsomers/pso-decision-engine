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
		const sheetNumberStyle={display: "inline-block", width: "30px", verticalAlign: "text-top", textAlign: "right"}
		const ruleItemStyle={display: "inline-block", verticalAlign: "text-top", paddingLeft:"5px"}

		let sheets=[];
		if (activeTab=="rules") {
			let currentRuleSheet="XXXXXXXXXXXXXXXXXXXXX";
			let currentRuleSheetRuleList=[];
			d.rules.map((rule, index) => {
				if (currentRuleSheet!=rule.sheetName) {
					currentRuleSheet=rule.sheetName;
					currentRuleSheetRuleList=[];
					sheets.push({sheetName: rule.sheetName, rules:currentRuleSheetRuleList});
				}
				currentRuleSheetRuleList.push(rule);
			});
		}
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
				{(activeTab=="rules") &&
					<div>
						{sheets.map((sheet, index1) => (
							<div key={"sheet"+index1}>
								<p><i class="far fa-file-excel"></i> &nbsp;&nbsp; <b>Excel Sheet:</b> {sheet.sheetName}</p>
								{sheet.rules.map((rule, index2) => (
									<div>
										<div style={sheetNumberStyle}>
											{rule.rowNumber}
										</div>
										<div style={ruleItemStyle}>
											{rule.label!="" &&
												<div>
												<i class="fas fa-tags"></i> &nbsp; {rule.label}
												</div>
											}
											<i class="fas fa-balance-scale"></i> &nbsp;&nbsp;<i>{rule.parameterName}</i> <b>{rule.comparator}</b> {rule.value1}
											{rule.value2!="" &&
												<span> ; {rule.value2}</span>
											}
											<br />
											<i class="fas fa-check"></i> &nbsp;&nbsp;&nbsp;
											{rule.positiveResult.startsWith("goto ") &&
												<i class="fas fa-forward"></i>
											}
											{rule.positiveResult}<br />

											<i class="fas fa-times"></i> &nbsp;&nbsp;&nbsp;
											{rule.negativeResult.startsWith("goto ") &&
												<i class="fas fa-forward"></i>
											}
											{rule.negativeResult}<br />

											{rule.remark!="" &&
												<span>
													<i class="fas fa-info"></i> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {rule.remark}<br />
												</span>
											}
											<br />
										</div>
									</div>
								))}
								</div>
							))}
					</div>
				}
			</div>
		</div>
 )}

}
