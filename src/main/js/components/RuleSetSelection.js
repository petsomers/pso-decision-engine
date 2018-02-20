import React from "react";

export const RuleSetSelection = ({restEndPoints, versions, layout}) => {
	const restEndPointsHeight=layout.windowHeight-65/2;
	const uploadStyle={position:"fixed", top:"60px", left:"0px"};
	const restEndPointsStyle={position:"fixed", top:"80px", height:restEndPointsHeight+"px", left:"0px", width: "300px", backgroundColor: "#CCCCCC"};
    return (
    		<div>
					<div style={uploadStyle}>
					Upload Excel
					</div>
					<div style={restEndPointsStyle}>
					endpoints
					</div>
					Rest Endpoints<br /><br />
					Versions
    		</div>
   );
}
