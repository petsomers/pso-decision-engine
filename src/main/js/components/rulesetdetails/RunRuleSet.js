import React from "react";
import { Input, Button } from "react-lightning-design-system";

export const RunRuleSet = ({inputParameters, runNowData, setRunNowParameterValue}) => {
    return (
<div>
  <div style={{position: "absolute", right: "left: 280px", top: "135px"}}>
		<Button type="brand" onClick={() => run()} icon="right" iconAlign="left" label="Run" />
	</div>
  <div style={{display: "inline-block", width: 200}}>
    {Object.keys(inputParameters).map((parameterName, index) => (
      <Input
        key={index}
        value={runNowData.parameterValues[parameterName]}
        label={parameterName}
        onChange={(event) => setRunNowParameterValue(parameterName, event.target.value)}/>
      ))}
  </div>
  <div style={{display: "inline-block"}}>
    RUN RESULT
  </div>
</div>
    )
};
