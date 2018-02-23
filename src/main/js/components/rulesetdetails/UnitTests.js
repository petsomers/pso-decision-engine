import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn, Button } from "react-lightning-design-system";

export class UnitTests  extends React.Component {
	constructor(props) {
		super();
		this.state = {
		}
	}

  render() {
    return (
    <div>
			<div style={{position: "absolute", right: "30px", top: "135px"}}>
				<Button type="brand" onClick={() => this.doUpload()} icon="right" iconAlign="left" label="Run All Tests" />
			</div>
      {this.props.unitTests.map((unitTest, index1) => (
      <div key={"ut"+index1} style={{paddingBottom: "30px"}}>
        <i className="fas fa-flag-checkered"></i>
				&nbsp;&nbsp;<b>{unitTest.name}</b>
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
                <TableRow key={"key"+index1+"_"+index2}>
                  <TableRowColumn>{parameterName}</TableRowColumn>
                  <TableRowColumn>{unitTest.parameters[parameterName]}</TableRowColumn>
                </TableRow>
              ))}
            </TableBody>
          </Table>
					<div style={{paddingTop:"10px"}}>
						<i class="fas fa-child"></i> <b>Expected Result: <font color="green">{unitTest.expectedResult}</font></b>
					</div>
        </div>
      </div>
      ))}
    </div>
    );
  }

};
