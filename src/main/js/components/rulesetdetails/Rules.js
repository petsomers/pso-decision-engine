import React from "react";

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
  rules.map((rule, index) => {
    if (currentRuleSheet!=rule.sheetName) {
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
          <p><i className="far fa-file-excel"></i> &nbsp;&nbsp; <b>Excel Sheet:</b> {sheet.sheetName}</p>
          {sheet.rules.map((rule, index2) => (
            <div style={{paddingBottom: "20px"}}>
              <div style={sheetNumberStyle}>
                {rule.rowNumber}
              </div>
              <div style={ruleItemStyle}>
                {rule.label!="" &&
                  <div style={conditionStyle}>
                  <i className="fas fa-tags"></i> &nbsp; {rule.label}
                  </div>
                }
                <div style={conditionStyle}>
                  <i className="fas fa-balance-scale"></i> &nbsp;&nbsp;<i>{rule.parameterName}</i> <b>{rule.comparator}</b> {rule.value1}
                  {rule.value2!="" &&
                    <span> ; {rule.value2}</span>
                  }
                </div>
                <div style={resultStyleGreen}>
                  <i className="fas fa-check"></i> &nbsp;&nbsp;&nbsp;
                  {rule.positiveResult=="" &&
                    <i class="fas fa-level-down-alt"></i>
                  }
                  {rule.positiveResult}
                </div>
                <div style={resultStyleRed}>
                  <i className="fas fa-times"></i> &nbsp;&nbsp;&nbsp;&nbsp;
                  {rule.negativeResult=="" &&
                    <i class="fas fa-level-down-alt"></i>
                  }
                  {rule.negativeResult}<br />
                  </div>
                  {rule.remark!="" &&
                    <div style={remarkStyle}>
                      <i className="fas fa-info"></i> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {rule.remark}<br />
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
