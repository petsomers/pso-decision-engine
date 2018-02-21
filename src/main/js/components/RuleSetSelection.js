import React from "react";
import { Button } from "react-lightning-design-system";

export const RuleSetSelection = ({restEndPoints, versions, layout, openFileUpload}) => {
	const restEndPointsHeight=(layout.windowHeight-65)/2;
	const versionsHeight=layout.windowHeight-restEndPointsHeight-90;

	const uploadStyle={position:"fixed", top:"55px", left:"0px"};

	const restEndPointsStyle={
		position:"fixed",
		top:"80px",
		height:restEndPointsHeight+"px",
		left:"0px",
		width: "300px",
		backgroundColor: "#CCCCCC"
	};
	const versionsStyle={
		position:"fixed",
		top:(85+restEndPointsHeight)+"px",
		height:versionsHeight+"px",
		left:"0px",
		width: "300px",
		backgroundColor: "#CCCCCC"
	};
  return (
    		<div>
					<div style={uploadStyle}>
						<Button type="neutral" onClick={() => openFileUpload()} icon="new" iconAlign="left" label="Upload Excel" />
					</div>
					<div style={restEndPointsStyle}>
						Rest Endpoints
					</div>
					<div style={versionsStyle}>
						Version
					</div>
    		</div>
   );
}
