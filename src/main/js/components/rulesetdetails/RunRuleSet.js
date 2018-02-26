import React from "react";
import { Input, Button } from "react-lightning-design-system";
import { UnitTestTrace } from "./UnitTestTrace"

export const RunRuleSet = ({inputParameters, runNowData, setRunNowParameterValue, runNow}) => {
    return (
<div>
  <div style={{position: "absolute", right: "30px", top: "135px"}}>
    <Button type="brand" onClick={() => runNow()} icon="right" iconAlign="left" label="Run" />
  </div>
  <div style={{display: "inline-block", float: "left", width: "200px"}}>
    {Object.keys(inputParameters).map((parameterName, index) => (
      <Input
        key={index}
        value={runNowData.parameterValues[parameterName]}
        label={parameterName}
        onChange={(event) => setRunNowParameterValue(parameterName, event.target.value)}/>
      ))}
  </div>
  <div style={{display: "inline-block", paddingLeft: "10px"}}>
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
