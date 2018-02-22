import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn } from "react-lightning-design-system";

export const InputParameters = ({inputParameters}) => {
  const tableRowStyle={borderBottom: 'solid',borderBottomWidth: '1px',borderBottomColor:'#CCCCCC'}
  return (
  <div>
    <Table bordered>
      <TableHeader>
        <TableRow>
          <TableHeaderColumn width={"200"}><b>Parameter</b></TableHeaderColumn>
          <TableHeaderColumn width={"90"}><b>Type</b></TableHeaderColumn>
          <TableHeaderColumn width={"200"}><b>Default Value</b></TableHeaderColumn>
          <TableHeaderColumn>&nbsp;</TableHeaderColumn>
        </TableRow>
      </TableHeader>
      <TableBody>
        {Object.keys(inputParameters).map((parameterName, index) => (
          <TableRow key={"key"+index}>
            <TableRowColumn>{parameterName}</TableRowColumn>
            <TableRowColumn>{inputParameters[parameterName].type}</TableRowColumn>
            <TableRowColumn>{inputParameters[parameterName].defaultValue}</TableRowColumn>
            <TableRowColumn>&nbsp;</TableRowColumn>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  </div>
  );
};
