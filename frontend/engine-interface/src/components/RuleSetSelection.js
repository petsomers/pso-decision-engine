import React from "react";
import { Button } from "react-lightning-design-system";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faTable as fasTable, faListAlt as fasListAlt} from "@fortawesome/free-solid-svg-icons";
import {faDotCircle as farDotCircle} from "@fortawesome/free-regular-svg-icons";

export const RuleSetSelection = ({restEndpoints, dataSets, layout, openFileUpload, selectEndpoint, selectedEndpoint, selectedDataSetInfo, selectDataSet}) => {
	const showHeader = window.showHeader;
	const restEndpointsHeight=showHeader?(layout.windowHeight-160)/2:(layout.windowHeight-110)/2;
	const globalListsHeight=restEndpointsHeight;

	const restEndpointTitlePosition=showHeader?60:10;
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
						<div key={endpoint} style={(selectedEndpoint===endpoint)?endPointCardStyleSelected:endPointCardStyle} className='slds-table slds-table--bordered'>
							<FontAwesomeIcon icon={farDotCircle}/> &nbsp;
							<a onClick={() => selectEndpoint(endpoint)}>
								{endpoint}
							</a>
						</div>
					))}
				</div>
				<div style={globalListTitleStyle}><b>Datasets (Lists and Lookup Tables) Lists</b></div>
				<div style={globalListStyle}>
					{dataSets.map((dataSet, index) => (
					<div key={dataSet.name} style={(selectedDataSetInfo && selectedDataSetInfo.name===dataSet.name)?endPointCardStyleSelected:endPointCardStyle} className='slds-table slds-table--bordered'>
						<a onClick={() => selectDataSet(dataSet)}>
							{dataSet.type==="LOOKUP" ? (
								<FontAwesomeIcon icon={fasTable}/>
								):(
								<FontAwesomeIcon icon={fasListAlt}/>
								)
							}
							&nbsp; {dataSet.name}
						</a>
					</div>
					))}
				</div>
				<div style={uploadStyle}>
					<Button type="neutral" onClick={() => openFileUpload()} icon="upload" iconAlign="left" label="Upload File" />
				</div>
  		</div>
   );
}
