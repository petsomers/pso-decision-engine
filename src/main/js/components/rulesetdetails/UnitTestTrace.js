import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn, Button } from "react-lightning-design-system";

export const UnitTestTrace = ({runData}) => {
  return (
    <div style={{borderLeft: "solid", borderLeftWidth: "1px", borderLeftColor: "#CCCCCC", padding: "15px"}}>
      <b>Unit Test Duration:</b> {runData.durationInMilliSeconds} ms<br />
      {runData.trace && runData.trace.length>0 &&
        <div>
        {runData.trace.map((t, tracenr) => (
          <div key={"tr"+tracenr} style={{paddingBottom:"10px"}}>
          <table style={{width: "60%"}}>
          <tbody>
          <tr style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda"}}>
            <td colSpan="2">{t.rule.sheetName}: {t.rule.rowNumber} - {t.rule.label}</td>
          </tr>
          <tr style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda"}}>
            <td colSpan="2">
              {t.rule.parameterName} ({t.parameterValue})
              {t.rule.comparator}
              {t.rule.value1}
              {t.rule.value2!="" &&
                <span> ; {t.rule.value2}</span>
              }
            </td>
          </tr>
          <tr>
            <td style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%"}}>{t.rule.positiveResult}</td>
            <td style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%"}}>{t.rule.negativeResult}</td>
          </tr>
          </tbody>
          </table>
          </div>
        ))}
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
