import React from "react";
import { Button } from "react-lightning-design-system";

export const RuleSetSelection = ({restEndpoints, versions, layout, openFileUpload, selectEndpoint}) => {
	const restEndpointsHeight=(layout.windowHeight-60)/4;

	const versionsHeight=restEndpointsHeight*2;

	const globalListStyle={
		position:"fixed", top:(restEndpointsHeight+versionsHeight+70)+"px", left:"0px",
		overflowX: "hidden",overflowY: "scroll"
	};

	const uploadStyle={
		position:"fixed", top:(layout.windowHeight-50)+"px", left:"0px"
	};

	const restEndpointsStyle={
		position:"fixed", top:"60px",
		height:restEndpointsHeight+"px",
		left:"5px",	width: "300px",
		overflowX: "hidden",overflowY: "scroll"
	};
	const versionsStyle={
		position:"fixed",
		top:(65+restEndpointsHeight)+"px",
		height:versionsHeight+"px",
		left:"5px",	width: "300px",
		overflowX: "hidden",overflowY: "scroll"
	};
	const cardStyle= {
		padding: "5px",
		whiteSpace: "nowrap"
	}
  return (
  		<div>
				<div style={restEndpointsStyle} className='slds-table slds-table--bordered'>
					<b>Rest Endpoints</b>
					{restEndpoints.map((endpoint, index) => (
						<div key={endpoint} style={cardStyle} className='slds-table slds-table--bordered'>
							<a onClick={() => selectEndpoint(endpoint)}>
								{endpoint}
							</a>
						</div>
					))}
				</div>
				<div style={versionsStyle}>
					<b>Versions</b>
					{versions.map((version, index) => (
						<div key={version.id} className='slds-table slds-table--bordered' style={cardStyle}>
							Uploaded: <a onClick={() => selectVersion(version.id)}>{version.uploadDate}</a>
							<br />
							{version.active &&
								<span><b>Active</b><br /></span>
							}
							Version: {version.version}<br />
						</div>
					))}
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
