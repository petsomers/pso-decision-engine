import React from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {
  faTags as fasTags,
  faBalanceScale as fasBalanceScale,
  faCheck as fasCheck,
  faTimes as fasTimes,
  faExclamationTriangle as fasExclamationTriangle
} from "@fortawesome/free-solid-svg-icons";
import {faFileExcel as farFileExcel} from "@fortawesome/free-regular-svg-icons";

export const UnitTestTrace = ({runData}) => {
  const borderedTextStyle={border: "1px", borderStyle: "solid", borderColor: "#CCCAC9", backgroundColor: "#EEEEEE"};
  const tdResultMatchStyle={border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%", backgroundColor: "#EEEEEE"};
  const tdResultNoMatchStyle={border: "1px", borderStyle: "solid", borderColor: "#dddbda", width:"50%"};
  return (
    <div>
      {runData.durationInMilliSeconds>0 &&
        <span><b>Duration:</b> {runData.durationInMilliSeconds} ms<br /></span>
      }
      {runData.trace && runData.trace.length>0 &&
        <div>
        {runData.trace.map((t, tracenr) => (
          <div key={"tr"+tracenr} style={{paddingBottom:"10px"}}>
          <table style={{width: "90%"}}>
          <tbody>
          <tr style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda"}}>
            <td>
              <FontAwesomeIcon icon={farFileExcel}/> &nbsp;  {t.rule.sheetName}: {t.rule.rowNumber}
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
                <FontAwesomeIcon icon={fasTags}/> &nbsp; {t.rule.label}
              </td>
            </tr>
          }
          <tr style={{border: "1px", borderStyle: "solid", borderColor: "#dddbda"}}>
            <td colSpan="2">
              <FontAwesomeIcon icon={fasBalanceScale}/> &nbsp;
              {t.rule.parameterName} ( <span style={borderedTextStyle}>{t.parameterValue}</span> )
              &nbsp; {t.rule.comparator} &nbsp;
              {t.rule.value1}
              {t.rule.value2!="" &&
                <span> ; {t.rule.value2}</span>
              }
            </td>
          </tr>
          {t.info && t.info.length>0 &&
            <tr>
              <td colSpan="2">
                {t.info.map((info, infoIndex) => (
                  <div style={{paddingLeft: "40px"}} key={"tr"+tracenr+"info"+infoIndex}><i>{info}</i></div>
                ))}
              </td>
            </tr>
          }
          <tr>
            {t.result==t.rule.positiveResult?(
              <td style={tdResultMatchStyle}>
                <FontAwesomeIcon icon={fasCheck}/>  &nbsp; {t.rule.positiveResult} &nbsp;
              </td>
            ) : (
              <td style={tdResultNoMatchStyle}>
                <FontAwesomeIcon icon={fasCheck}/>  &nbsp; {t.rule.positiveResult} &nbsp;
              </td>
            )}
            {t.result==t.rule.negativeResult?(
              <td style={tdResultMatchStyle}>
                <FontAwesomeIcon icon={fasTimes}/> &nbsp; {t.rule.negativeResult} &nbsp;
              </td>
            ) : (
              <td style={tdResultNoMatchStyle}>
                <FontAwesomeIcon icon={fasTimes}/>  &nbsp; {t.rule.negativeResult} &nbsp;
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
        <font color="red"><FontAwesomeIcon icon={fasExclamationTriangle}/> &nbsp; Error</font><br />
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
