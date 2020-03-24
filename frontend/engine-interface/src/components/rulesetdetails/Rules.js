import React from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {
  faTags as fasTags,
  faBalanceScale as fasBalanceScale,
  faCheck as fasCheck,
  faTimes as fasTimes,
  faLevelDownAlt as fasLevelDownAlt,
  faInfo as fasInfo
} from "@fortawesome/free-solid-svg-icons";
import {faFileExcel as farFileExcel} from "@fortawesome/free-regular-svg-icons";

export const Rules = ({rules}) => {
  const sheetNumberStyle={display: "inline-block", width: "30px", verticalAlign: "text-top", textAlign: "right"}
  const ruleItemStyle={display: "inline-block", verticalAlign: "text-top", paddingLeft:"5px"}
  const conditionStyle={width:"660px", border:"solid", borderWidth:"1px", borderColor:"#CCCCCC", paddingLeft: "5px", borderBottom:"none"}
  const remarkStyle={width:"660px", border:"solid", borderWidth:"1px", borderColor:"#CCCCCC", paddingLeft: "5px", borderTop:"none"}
  const resultStyleGreen={display: "inline-block", color: "green", width:"330px", border:"solid", borderWidth:"1px", borderColor:"#CCCCCC", paddingLeft: "5px"}
  const resultStyleRed={display: "inline-block", color: "red", width:"330px", border:"solid", borderWidth:"1px", borderColor:"#CCCCCC", paddingLeft: "5px"}
  let sheets=[];
  let currentRuleSheet="XXXXXXXXXXXXXXXXXXXXX";
  let currentRuleSheetRuleList=[];
  rules.forEach((rule, index) => {
    if (currentRuleSheet!==rule.sheetName) {
      currentRuleSheet=rule.sheetName;
      currentRuleSheetRuleList=[];
      sheets.push({sheetName: rule.sheetName, rules:currentRuleSheetRuleList});
    }
    currentRuleSheetRuleList.push(rule);
  });
  return (
    <div>
      {sheets.map((sheet, index1) => (
        <div key={"sheet"+index1}>
          <p><FontAwesomeIcon icon={farFileExcel}/> &nbsp;&nbsp; <b>Excel Sheet:</b> {sheet.sheetName}</p>
          {sheet.rules.map((rule, index2) => (
            <div key={index1+"_"+index2} style={{paddingBottom: "20px"}}>
              <div style={sheetNumberStyle}>
                {rule.rowNumber}
              </div>
              <div style={ruleItemStyle}>
                {rule.label!=="" &&
                  <div style={conditionStyle}>
                    <FontAwesomeIcon icon={fasTags}/> &nbsp; {rule.label}
                  </div>
                }
                <div style={conditionStyle}>
                  <FontAwesomeIcon icon={fasBalanceScale}/> &nbsp;&nbsp;<i>{rule.parameterName}</i> <b>{rule.comparator}</b> {rule.value1}
                  {rule.value2!=="" &&
                    <span> ; {rule.value2}</span>
                  }
                </div>
                <div style={resultStyleGreen}>
                  <FontAwesomeIcon icon={fasCheck}/> &nbsp;&nbsp;&nbsp;
                  {rule.positiveResult==="" && <FontAwesomeIcon icon={fasLevelDownAlt}/>}
                  {rule.positiveResult}
                </div>
                <div style={resultStyleRed}>
                  <FontAwesomeIcon icon={fasTimes}/> &nbsp;&nbsp;&nbsp;&nbsp;
                  {rule.negativeResult==="" && <FontAwesomeIcon icon={fasLevelDownAlt}/>}
                  {rule.negativeResult}<br />
                  </div>
                  {rule.remark!=="" &&
                    <div style={remarkStyle}>
                      <FontAwesomeIcon icon={fasInfo}/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {rule.remark}<br />
                    </div>
                  }
              </div>
            </div>
          ))}
          </div>
        ))}
    </div>
  );
};
