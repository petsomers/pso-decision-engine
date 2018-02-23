import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn, Button } from "react-lightning-design-system";

export const UnitTestTrace = ({runData}) => {
  return (
    <div style={{borderLeft: "solid", borderLeftWidth: "1px", borderLeftColor: "#CCCCCC", padding: "15px"}}>
      <b>Unit Test Duration:</b> {runData.durationInMilliSeconds} ms<br />
      {runData.trace && runData.trace.length>0 &&
        <div>
        <table>
        <tr className="unitTests">
          <td><b>POSITION</b></td>
          <td><b>LABEL</b></td>
          <td><b>CONDITION</b></td>
          <td><b>POSITIVE RESULT</b></td>
          <td><b>NEGATIVE RESULT</b></td>
        </tr>
        {runData.trace.map((t, tracenr) => (
          <tr key={"tr"+tracenr}>
            <td>{t.rule.sheetName}: {t.rule.rowNumber}</td>
            <td>{t.rule.label}</td>
            <td>
              {t.rule.parameterName} ({t.parameterValue})
              {t.rule.comparator}
              {t.rule.value1}
              {t.rule.value2!="" &&
                <span> ; {t.rule.value2}</span>
              }
            </td>
            <td>{t.rule.positiveResult}</td>
            <td>{t.rule.negativeResult}</td>
          </tr>
        ))}
        </table>
        </div>
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
