import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn, Button } from "react-lightning-design-system";
import { UnitTestTrace } from "./UnitTestTrace"

export const UnitTests = ({unitTests, runUnitTests, unitTestsResult}) => {
	let utres=unitTestsResult!=null?unitTestsResult.unitTestResults:null;
	return (
    <div>
			<div style={{position: "absolute", right: "30px", top: "135px"}}>
				{(unitTestsResult==null || !unitTestsResult.allTestsPassed) &&
				<Button type="brand" onClick={() => runUnitTests()} icon="right" iconAlign="left" label="Run All Tests" />
				}
				{unitTestsResult!=null && unitTestsResult.allTestsPassed &&
					<span>
					<b><font color="green" size="+1"><i class="fas fa-check"></i> All Tests Passed</font></b>
					</span>
				}
				{unitTestsResult!=null && !unitTestsResult.allTestsPassed &&
					<span>
					<br />
					<b><font color="red" size="+1"><i class="fas fa-frown"></i>FAILED</font></b>
					</span>
				}
			</div>
      {unitTests.map((unitTest, utnr) => (
      <div key={"ut"+utnr} style={{paddingBottom: "30px"}}>
        <i className="fas fa-flag-checkered"></i>
				&nbsp;&nbsp;<b>{unitTest.name}</b>
				{utres!=null && utres[utnr].passed &&
						<span>
							&nbsp;&nbsp;&nbsp;<b><font color="green"><i class="fas fa-check"></i>PASSED</font></b>
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
                <TableRow key={"key"+utnr+"_"+index2}>
                  <TableRowColumn>{parameterName}</TableRowColumn>
                  <TableRowColumn>{unitTest.parameters[parameterName]}</TableRowColumn>
                </TableRow>
              ))}
            </TableBody>
          </Table>
					<div style={{paddingTop:"10px"}}>
						<i className="fas fa-child"></i> <b>Expected Result: <font color="green">{unitTest.expectedResult}</font></b>
					</div>
					{utres!=null && utres[utnr] &&
						<UnitTestTrace runData={utres[utnr].run} />
					}
        </div>
      </div>
      ))}
    </div>
  )
};
