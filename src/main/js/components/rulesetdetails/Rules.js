import React from "react";

export const Rules = ({rules}) => {
  const sheetNumberStyle={display: "inline-block", width: "30px", verticalAlign: "text-top", textAlign: "right"}
  const ruleItemStyle={display: "inline-block", verticalAlign: "text-top", paddingLeft:"5px"}

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
            <div>
              <div style={sheetNumberStyle}>
                {rule.rowNumber}
              </div>
              <div style={ruleItemStyle}>
                {rule.label!="" &&
                  <div>
                  <i className="fas fa-tags"></i> &nbsp; {rule.label}
                  </div>
                }
                <i className="fas fa-balance-scale"></i> &nbsp;&nbsp;<i>{rule.parameterName}</i> <b>{rule.comparator}</b> {rule.value1}
                {rule.value2!="" &&
                  <span> ; {rule.value2}</span>
                }
                <br />
                <i className="fas fa-check"></i> &nbsp;&nbsp;&nbsp;
                {rule.positiveResult.startsWith("goto ") &&
                  <i className="fas fa-forward"></i>
                }
                {rule.positiveResult}<br />

                <i className="fas fa-times"></i> &nbsp;&nbsp;&nbsp;&nbsp;
                {rule.negativeResult.startsWith("goto ") &&
                  <i className="fas fa-forward"></i>
                }
                {rule.negativeResult}<br />

                {rule.remark!="" &&
                  <span>
                    <i className="fas fa-info"></i> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {rule.remark}<br />
                  </span>
                }
                <br />
              </div>
            </div>
          ))}
          </div>
        ))}
    </div>
  );
};
