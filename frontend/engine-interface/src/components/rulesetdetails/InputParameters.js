import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn } from "react-lightning-design-system";

export const InputParameters = ({inputParameters}) => {
  const parameterList=[...Object.keys(inputParameters)];
  parameterList.sort((p1, p2) => inputParameters[p1].seqNr < inputParameters[p2].seqNr ? -1:1);

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
        {parameterList.map((parameterName, index) => (
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
