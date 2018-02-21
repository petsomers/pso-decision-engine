import React from "react";
import { Button } from "react-lightning-design-system";

export const RuleSetSelection = ({restEndpoints, versions, layout, openFileUpload}) => {
	const restEndpointsHeight=(layout.windowHeight-60)/3;
	const versionsHeight=layout.windowHeight-restEndpointsHeight-180;

	const globalListStyle={position:"fixed", top:(layout.windowHeight-100)+"px", left:"0px"};

	const uploadStyle={position:"fixed", top:(layout.windowHeight-50)+"px", left:"0px"};

	const restEndpointsStyle={
		position:"fixed",
		top:"60px",
		height:restEndpointsHeight+"px",
		left:"5px",
		width: "300px",
		backgroundColor: "#FFFFFF"
	};
	const versionsStyle={
		position:"fixed",
		top:(65+restEndpointsHeight)+"px",
		height:versionsHeight+"px",
		left:"5px",
		width: "300px",
		backgroundColor: "#FFFFFF"
	};
  return (
    		<div>
					<div style={restEndpointsStyle}>
						<b>Rest Endpoints</b>
						{restEndpoints.map((endpoint, index) => (
							<div key={endpoint} className='slds-table slds-table--bordered'>{endpoint}</div>
						))}
					</div>
					<div style={versionsStyle}>
						Versions
					</div>
					<div style={versionsStyle}>
						Versions
					</div>
					<div style={globalListStyle}>
						Global Lists (all endpoints and versions)
					</div>
					<div style={uploadStyle}>
						<Button type="neutral" onClick={() => openFileUpload()} icon="new" iconAlign="left" label="Upload Excel" />
					</div>
    		</div>
   );
}
