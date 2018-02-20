import React from "react";

export const RuleSetSelection = ({restEndPoints, versions, layout}) => {
	const restEndPointsHeight=(layout.windowHeight-65)/2;
	const versionsHeight=layout.windowHeight-restEndPointsHeight-90;
	const uploadStyle={position:"fixed", top:"60px", left:"0px"};
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
					Upload Excel
					</div>
					<div style={restEndPointsStyle}>
					Rest Endpoints
					</div>
					<div style={versionsStyle}>
					Rest Endpoints
					</div>
    		</div>
   );
}
