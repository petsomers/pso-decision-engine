import React from "react";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn, Button } from "react-lightning-design-system";

export const UnitTestTrace = ({runData}) => {
  const borderedTextStyle={border: "1px", borderStyle: "solid", borderColor: "#CCCAC9", backgroundColor: "#EEEEEE"};
  return (
    <div>
      {runData.durationInMilliSeconds>0 &&
        <span><b>Duration:</b> {runData.durationInMilliSeconds} ms<br /></span>
      }
      {runData.trace && runData.trace.length>0 &&
        <div>
        {runData.trace.map((t, tracenr) => (
          <div key={"tr"+tracenr} style={{paddingBottom:"10px"}}>
          <table style={{width: "70%"}}>
          <tbody>
          <tr style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda"}}>
            <td>
              <i className="far fa-file-excel"></i> &nbsp;  {t.rule.sheetName}: {t.rule.rowNumber}
            </td>
            <td align="right">
              &nbsp;
              {t.result &&
                <span>RESULT: {t.result}</span>
              }
            </td>
          </tr>
          {t.rule.label && t.rule.label!="" &&
            <tr style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda"}}>
              <td colSpan="2">
                <i className="fas fa-tags"></i> &nbsp; {t.rule.label}
              </td>
            </tr>
          }
          <tr style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda"}}>
            <td colSpan="2">
              <i className="fas fa-balance-scale"></i> &nbsp;
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
              <td style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%", backgroundColor: "#EEEEEE"}}>
                <i className="fas fa-check"></i>  &nbsp; {t.rule.positiveResult} &nbsp;
              </td>
            ) : (
              <td style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%"}}>
                <i className="fas fa-check"></i>  &nbsp; {t.rule.positiveResult} &nbsp;
              </td>
            )}
            {t.result==t.rule.negativeResult?(
              <td style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%", backgroundColor: "#EEEEEE"}}>
                <i className="fas fa-times"></i> &nbsp; {t.rule.negativeResult} &nbsp;
              </td>
            ) : (
              <td style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%"}}>
                <i className="fas fa-times"></i>  &nbsp; {t.rule.negativeResult} &nbsp;
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
        { runData.messages.map((message, index) => (
          <div key={"message_"+index}>{message}<br /></div>
        ))};
        </div>
      }
    </div>
  );
};
