import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn, Button } from "react-lightning-design-system";

export const UnitTestTrace = ({runData}) => {
  const borderedTextStyle={border: "1px", borderStyle: "solid", borderColor: "#CCCAC9", backgroundColor: "#EEEEEE"};
  return (
    <div style={{paddingLeft: "45px"}}>
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
              {t.rule.parameterName} ( <span style={borderedTextStyle}>{t.parameterValue}</span> )
              &nbsp; {t.rule.comparator} &nbsp;
              {t.rule.value1}
              {t.rule.value2!="" &&
                <span> ; {t.rule.value2}</span>
              }
            </td>
          </tr>
          <tr>
            {t.result==t.rule.positiveResult?(
              <td style={{border: "2px", borderStyle: "solid", borderColor: "#dddbda", width:"50%", backgroundColor: "#EEEEEE"}}>
                {t.rule.positiveResult}
              </td>
            ) : (
              <td style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%"}}>
                {t.rule.positiveResult}
              </td>
            )}
            {t.result==t.rule.negativeResult?(
              <td style={{border: "2px", borderStyle: "solid", borderColor: "#dddbda", width:"50%", backgroundColor: "#EEEEEE"}}>
                {t.rule.negativeResult}
              </td>
            ) : (
              <td style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%"}}>
                {t.rule.negativeResult}
              </td>
            )}
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
