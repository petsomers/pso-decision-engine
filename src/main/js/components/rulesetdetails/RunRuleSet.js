import React from "react";
import { Input, Button } from "react-lightning-design-system";
import { UnitTestTrace } from "./UnitTestTrace"

export const RunRuleSet = ({inputParameters, runNowData, setRunNowParameterValue, runNow, runNowClearResult}) => {
    return (
<div>
  <div style={{position: "absolute", right: "30px", top: "135px"}}>
    {runNowData.result? (
      <Button type="brand" onClick={() => runNowClearResult()} icon="left" iconAlign="left" label="Back" />
    ):(
      <Button type="brand" onClick={() => runNow()} icon="right" iconAlign="left" label="Run" />
    )
    }
  </div>
  {!runNowData.result? (
  <div style={{display: "inline-block", float: "left", width: "200px"}}>
    <u><b>Input Parameters:</b></u><br />
    {Object.keys(inputParameters).map((parameterName, index) => (
      <Input
        key={index}
        value={runNowData.parameterValues[parameterName]}
        label={parameterName}
        onChange={(event) => setRunNowParameterValue(parameterName, event.target.value)}/>
      ))}
  </div>
  ) : (
    <div style={{display: "inline-block", float: "left", width: "200px"}}>
      <u><b>Input Parameters:</b></u><br />
      <br />
      {Object.keys(runNowData.result.run.inputParameters).length==0 &&
        <i>None</i>
      }
      {Object.keys(runNowData.result.run.inputParameters).map((parameterName, index) => (
        <div key={index}>
          <b>{parameterName}</b><br />
          {runNowData.result.run.inputParameters[parameterName]}
          <br />
        </div>
        ))}
      </div>
  )}
  <div style={{display: "inline-block", paddingLeft: "10px", width:"60%"}}>
    {runNowData.result &&
      <div>
        {runNowData.result.errorMessage}
        <UnitTestTrace runData={runNowData.result.run} />
      </div>
    }
  </div>
</div>
    )
};
