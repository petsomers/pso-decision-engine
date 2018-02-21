import React from "react";
import { Button } from "react-lightning-design-system";

export const RuleSetSelection = ({restEndPoints, versions, layout, openFileUpload}) => {
	const restEndPointsHeight=(layout.windowHeight-60)/3;
	const versionsHeight=layout.windowHeight-restEndPointsHeight-120;

	const uploadStyle={position:"fixed", top:(layout.windowHeight-50)+"px", left:"0px"};

	const restEndPointsStyle={
		position:"fixed",
		top:"60px",
		height:restEndPointsHeight+"px",
		left:"0px",
		width: "300px",
		backgroundColor: "#CCCCCC"
	};
	const versionsStyle={
		position:"fixed",
		top:(65+restEndPointsHeight)+"px",
		height:versionsHeight+"px",
		left:"0px",
		width: "300px",
		backgroundColor: "#CCCCCC"
	};
  return (
    		<div>
					<div style={restEndPointsStyle}>
						Rest Endpoints
					</div>
					<div style={versionsStyle}>
						Version
					</div>
					<div style={uploadStyle}>
						<Button type="neutral" onClick={() => openFileUpload()} icon="new" iconAlign="left" label="Upload Excel" />
					</div>
    		</div>
   );
}
