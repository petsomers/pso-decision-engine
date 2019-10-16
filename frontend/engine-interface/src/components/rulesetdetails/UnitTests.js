import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn, Button } from "react-lightning-design-system";
import { UnitTestTrace } from "./UnitTestTrace"
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faCheck as fasCheck, faFrown as fasFrown, faBug as fasBug, faChild as fasChild} from "@fortawesome/free-solid-svg-icons";

export const UnitTests = ({unitTests, runUnitTests, unitTestsResult, selectUnitTest, selectedUnitTest}) => {
	let utres=unitTestsResult!=null?unitTestsResult.unitTestResults:null;
	const runButtonPaneStyle={position: "absolute", right: "15px", top: "135px"};
	console.log("selectedUnitTest:"+selectedUnitTest);
	console.log("unitTestsResult: " +unitTestsResult);
	if (selectedUnitTest==-1) {
		return (
    <div>
			<div style={runButtonPaneStyle}>
				{(unitTestsResult==null || !unitTestsResult.allTestsPassed) &&
				<Button type="brand" onClick={() => runUnitTests()} icon="right" iconAlign="left" label="Run All" />
				}
			</div>
			{unitTestsResult!=null && unitTestsResult.allTestsPassed &&
				<span>
				<b><font color="green" size="+1"><FontAwesomeIcon icon={fasCheck}/>> All Tests Passed</font></b>
				</span>
			}
			{unitTestsResult!=null && !unitTestsResult.allTestsPassed &&
				<span>
				<br />
				<b><font color="red" size="+1"><FontAwesomeIcon icon={fasFrown}/>>FAILED</font></b>
				</span>
			}
			{unitTestsResult!=null && unitTestsResult.errorMessage &&
				<div style={{padding: "50px"}}><font color="red">{unitTestsResult.errorMessage}</font></div>
			}
			<div style={{paddingRight:"105px"}}>
			<Table bordered fixedLayout>
				<TableHeader>
					<TableRow>
						<TableHeaderColumn><b>Test Name</b></TableHeaderColumn>
						<TableHeaderColumn><b>Expected Result</b></TableHeaderColumn>
						<TableHeaderColumn><b>Result</b></TableHeaderColumn>
						<TableHeaderColumn><b>Passed</b></TableHeaderColumn>
					</TableRow>
				</TableHeader>
				<TableBody>
					{unitTests.map((unitTest, utnr) => (
						<TableRow key={"unittestkey"+utnr+"_row"}>
							<TableRowColumn>{unitTest.name}</TableRowColumn>
							<TableRowColumn>{unitTest.expectedResult}</TableRowColumn>
							<TableRowColumn>
								{utres!=null && utres[utnr] &&
									<span>{utres[utnr].result}</span>
								}
							</TableRowColumn>
							<TableRowColumn>
								{utres!=null && utres[utnr].passed &&
										<span>
												&nbsp;&nbsp;&nbsp;
												<b><font color="green"><FontAwesomeIcon icon={fasCheck}/>&nbsp;PASSED</font></b>
												&nbsp;&nbsp;&nbsp;&nbsp;
												<a onClick={() => selectUnitTest(utnr)}><FontAwesomeIcon icon={fasBug}/></a>
										</span>
								}
								{utres!=null && !utres[utnr].passed &&
										<span>
											&nbsp;&nbsp;&nbsp;<b><font color="red"><FontAwesomeIcon icon={fasFrown}/>&nbsp;FAILED</font></b>
											&nbsp;&nbsp;&nbsp;
											<a onClick={() => selectUnitTest(utnr)}><FontAwesomeIcon icon={fasBug}/></a>
										</span>
								}
							</TableRowColumn>
						</TableRow>
					))}
				</TableBody>
			</Table>
			</div>
    </div>
  )
	}
	if (selectedUnitTest>=0) {
		let unitTest=unitTests[selectedUnitTest];
		let unitTestResult=utres[selectedUnitTest];
		return (
		<div>
			<div style={runButtonPaneStyle}>
				<Button type="brand" onClick={() => selectUnitTest(-1)} icon="left" iconAlign="left" label="Back" />
			</div>

			Unit Test: <b>{unitTest.name}</b>
			{unitTestResult.passed &&
					<span>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b><font color="green"><FontAwesomeIcon icon={fasCheck}/>&nbsp;PASSED</font></b>
					</span>
			}
			{!unitTestResult.passed &&
					<span>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b><font color="red"><FontAwesomeIcon icon={fasFrown}/>&nbsp;FAILED</font></b>
					</span>
			}
			<div style={{paddingLeft: "40px", width: "70%"}}>
				<Table bordered fixedLayout>
					<TableHeader>
						<TableRow>
							<TableHeaderColumn><b>Parameter</b></TableHeaderColumn>
							<TableHeaderColumn><b>Value</b></TableHeaderColumn>
						</TableRow>
					</TableHeader>
					<TableBody>
						{Object.keys(unitTest.parameters).map((parameterName, index2) => (
							<TableRow key={"key_parameter_"+index2}>
								<TableRowColumn>{parameterName}</TableRowColumn>
								<TableRowColumn>{unitTest.parameters[parameterName]}</TableRowColumn>
							</TableRow>
						))}
					</TableBody>
				</Table>
				<div style={{paddingTop:"10px"}}>
					<FontAwesomeIcon icon={fasChild}/> <b>Expected Result: <font color="green">{unitTest.expectedResult}</font></b>
					{unitTestResult.passed &&
							<span>
								&nbsp;<b><font color="green"><FontAwesomeIcon icon={fasCheck}/>&nbsp;</font></b>
							</span>
					}
				</div>
			</div>
			<div style={{paddingLeft: "55px"}}>
				<UnitTestTrace runData={unitTestResult.run} />
			</div>
		</div>
	)
	}
};
