import React from "react";
import { Input } from "react-lightning-design-system";

export const RunRuleSet = ({inputParameters, runNowData, setRunNowParameterValue}) => {
    return (
      <div>
        {Object.keys(inputParameters).map((parameterName, index) => (
            <Input
              key={index}
              value={runNowData.parameterValues[parameterName]}
              label={parameterName}
              onChange={(event) => setRunNowParameterValue(parameterName, event.target.value)}/>
        ))}
      </div>
    )
};
