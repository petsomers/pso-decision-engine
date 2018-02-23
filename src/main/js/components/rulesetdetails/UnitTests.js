import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn } from "react-lightning-design-system";

export class UnitTests  extends React.Component {
	constructor(props) {
		super();
		this.state = {
		}
	}

  render() {
    return (
    <div>
      {this.props.unitTests.map((unitTest, index1) => (
      <div key={"ut"+index1} style={{paddingBottom: "30px"}}>
        <i className="fas fa-flag-checkered"></i>
        {unitTest.name}
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
        </div>
      </div>
      ))}
    </div>
    );
  }

};
