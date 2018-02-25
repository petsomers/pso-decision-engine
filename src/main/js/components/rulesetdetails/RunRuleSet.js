import React from "react";
import { Input, Button } from "react-lightning-design-system";

export const RunRuleSet = ({inputParameters, runNowData, setRunNowParameterValue, runNow}) => {
    return (
<div>
  <div style={{position: "absolute", right: "left: 280px", top: "135px"}}>
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
  {runNowData.result &&
    <div style={{display: "inline-block"}}>
      {runNowData.result.errorMessage}
    </div>
  }
</div>
    )
};
