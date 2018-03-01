import React from "react";
import { Button } from "react-lightning-design-system";

export const RuleSetSelection = ({restEndpoints, dataSets, layout, openFileUpload, selectEndpoint, selectedEndpoint, selectedDataSet}) => {
	const restEndpointsHeight=(layout.windowHeight-160)/2;
	const globalListsHeight=restEndpointsHeight;

	const restEndpointTitlePosition=60;
	const globalListTitlePosition=restEndpointTitlePosition+restEndpointsHeight+25;

	const uploadStyle={
		position:"fixed", top:(layout.windowHeight-45)+"px", left:"0px"
	};

	const restEndpointsTitleStyle={
		position:"fixed", top:(restEndpointTitlePosition)+"px",
	}

	const globalListTitleStyle={
		position:"fixed", top:(globalListTitlePosition)+"px",
	}
	const restEndpointsStyle={
		position:"fixed", top:(restEndpointTitlePosition+20)+"px",
		height:restEndpointsHeight+"px",
		left:"5px",	width: layout.leftPaneWidth+"px",
		overflowX: "hidden",overflowY: "scroll",
		borderBottom: 'solid',borderBottomWidth: '1px',borderBottomColor:'#CCCCCC'
	};
	const globalListStyle={
		position:"fixed",
		top:(globalListTitlePosition+20)+"px",
		height:globalListsHeight+"px",
		left:"5px",	width: layout.leftPaneWidth+"px",
		overflowX: "hidden",overflowY: "scroll",
		borderBottom: 'solid',borderBottomWidth: '1px',borderBottomColor:'#CCCCCC'
	};

	const endPointCardStyle= {padding: "5px", whiteSpace: "nowrap", backgroundColor: "#FFFFFF"}
	const endPointCardStyleSelected= {padding: "5px", whiteSpace: "nowrap", backgroundColor: "#EEEEFF"}
  return (
  		<div>
				<div style={restEndpointsTitleStyle}><b>Rest Endpoints</b></div>
				<div style={restEndpointsStyle} className='slds-table slds-table--bordered'>
					{restEndpoints.map((endpoint, index) => (
						<div key={endpoint} style={(selectedEndpoint==endpoint)?endPointCardStyleSelected:endPointCardStyle} className='slds-table slds-table--bordered'>
							<i className="fas fa-genderless"></i> &nbsp;
							<a onClick={() => selectEndpoint(endpoint)}>
								{endpoint}
							</a>
						</div>
					))}
				</div>
				<div style={globalListTitleStyle}><b>Datasets (Sets and Lookup Tables) Lists</b></div>
				<div style={globalListStyle}>
					{dataSets.map((dataSet, index) => (
					<div key={dataSet.name} style={(selectedDataSet==dataSet.name)?endPointCardStyleSelected:endPointCardStyle} className='slds-table slds-table--bordered'>
						<a onClick={() => selectEndpoint(dataSet.name)}>
							{dataSet.type=="LOOKUP" ? (
									<i className="fas fa-th-list"></i>
								):(
									<i className="fas fa-bars"></i>
								)
							}
							&nbsp; {dataSet.name}
						</a>
					</div>
					))}
				</div>
				<div style={uploadStyle}>
					<Button type="neutral" onClick={() => openFileUpload()} icon="new" iconAlign="left" label="Upload File" />
				</div>
  		</div>
   );
}
