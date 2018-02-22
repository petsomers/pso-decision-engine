import React from "react";

export const InputParameters = ({inputParameters}) => {
  const tableRowStyle={borderBottom: 'solid',borderBottomWidth: '1px',borderBottomColor:'#CCCCCC'}
  return (
  <table>
  <tr>
    <td width="200"><b>Parameter</b></td>
    <td width="90"><b>Type</b></td>
    <td width="200"><b>Default Value</b></td>
    <td>&nbsp;</td>
  </tr>
  {Object.keys(inputParameters).map((parameterName, index) => (
    <tr style={tableRowStyle}>
      <td>{parameterName}</td>
      <td>{inputParameters[parameterName].type}</td>
      <td>{inputParameters[parameterName].defaultValue}</td>
      <td>&nbsp;</td>
    </tr>
  ))}
  </table>
  );
};
