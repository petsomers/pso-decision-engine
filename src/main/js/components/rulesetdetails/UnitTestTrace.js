import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn, Button } from "react-lightning-design-system";

export const UnitTestTrace = ({runData}) => {

  return (
    <div style={{borderLeft: "solid", borderLeftWidth: "1px", borderLeftColor: "#CCCCCC", padding: "15px"}}>
      <b>Unit Test Duration:</b> {runData.durationInMilliSeconds}<br />
      {runData.trace && runData.trace.length>0 &&
        <Table bordered fixedLayout>
          <TableHeader>
            <TableRow>
              <TableHeaderColumn><b>Position</b></TableHeaderColumn>
              <TableHeaderColumn><b>Label</b></TableHeaderColumn>
              <TableHeaderColumn><b>Condition</b></TableHeaderColumn>
              <TableHeaderColumn><b>Positve Result</b></TableHeaderColumn>
              <TableHeaderColumn><b>Negative Result</b></TableHeaderColumn>
            </TableRow>
          </TableHeader>
          <TableBody>
            {runData.trace.map((t, tracenr) => (
              <TableRow key={tracenr}>
                <TableRowColumn>{t.rule.sheetName}: {t.rule.rowNumber}</TableRowColumn>
                <TableRowColumn>{t.rule.label}</TableRowColumn>
                <TableRowColumn>
                  {t.rule.parameterName} ({t.parameterValue})
                  {t.rule.comparator}
                  {t.rule.value1}
                  {t.rule.value2!="" &&
                    <span> ; {t.rule.value2}</span>
                  }
                </TableRowColumn>
                <TableRowColumn>{t.rule.positiveResult}</TableRowColumn>
                <TableRowColumn>{t.rule.negativeResult}</TableRowColumn>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      }
      {runData.error &&
        <div>
        <font color="red">Error</font><br />
        </div>
      }
      {runData.messages && runData.messages.length>0 &&
        <div>
        <u>Info:</u>
        runData.messages.map((message, index) => {
          <div>{message}<br /></div>
        });
        </div>
      }
    </div>
  );
};
