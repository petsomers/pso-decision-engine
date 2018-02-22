import React from "react";
import { Button } from "react-lightning-design-system";

export const RuleSetSelection = ({restEndpoints, versions, layout, openFileUpload, selectEndpoint}) => {
	const restEndpointsHeight=(layout.windowHeight-120)/3-5;
	const versionsHeight=restEndpointsHeight;
	const globalListsHeight=restEndpointsHeight;

	const restEndpointTitlePosition=60;
	const versionTitlePosition=87+restEndpointsHeight;
	const globalListTitlePosition=27+versionTitlePosition+versionsHeight;

	const uploadStyle={
		position:"fixed", top:(layout.windowHeight-50)+"px", left:"0px"
	};

	const restEndpointsTitleStyle={
		position:"fixed", top:(restEndpointTitlePosition)+"px",
	}
	const versionTitleStyle={
		position:"fixed", top:(versionTitlePosition)+"px",
	}
	const globalListTitleStyle={
		position:"fixed", top:(globalListTitlePosition)+"px",
	}
	const restEndpointsStyle={
		position:"fixed", top:(restEndpointTitlePosition+20)+"px",
		height:restEndpointsHeight+"px",
		left:"5px",	width: "300px",
		overflowX: "hidden",overflowY: "scroll"
	};
	const versionsStyle={
		position:"fixed",
		top:(versionTitlePosition+20)+"px",
		height:versionsHeight+"px",
		left:"5px",	width: "300px",
		overflowX: "hidden",overflowY: "scroll"
	};
	const globalListStyle={
		position:"fixed",
		top:(globalListTitlePosition+20)+"px",
		height:globalListsHeight+"px",
		left:"5px",	width: "300px",
		overflowX: "hidden",overflowY: "scroll"
	};

	const cardStyle= {
		padding: "5px",
		whiteSpace: "nowrap"
	}
  return (
  		<div>
				<div style={restEndpointsTitleStyle}><b>Rest Endpoints</b></div>
				<div style={restEndpointsStyle} className='slds-table slds-table--bordered'>
					{restEndpoints.map((endpoint, index) => (
						<div key={endpoint} style={cardStyle} className='slds-table slds-table--bordered'>
							<a onClick={() => selectEndpoint(endpoint)}>
								{endpoint}
							</a>
						</div>
					))}
				</div>
				<div style={versionTitleStyle}><b>Versions</b></div>
				<div style={versionsStyle}>
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
				<div style={globalListTitleStyle}><b>Global Lists</b></div>
				<div style={globalListStyle}>
					Global Lists (for all endpoints and versions)
				</div>
				<div style={uploadStyle}>
					<Button type="neutral" onClick={() => openFileUpload()} icon="new" iconAlign="left" label="Upload Excel" />
				</div>
  		</div>
   );
}
