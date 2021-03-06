import React from "react";
import { Button } from "react-lightning-design-system";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faDotCircle as farDotCircle} from "@fortawesome/free-regular-svg-icons";

export const RuleSetVersionSelection = ({layout, selectedEndpoint, versions, selectVersion, downloadExcel, deleteVersion}) => {
	const cardStyle = {
		padding: "10px",
		whiteSpace: "nowrap"
	};
	const liveCardStyle = {
		padding: "10px",
		whiteSpace: "nowrap",
		backgroundColor: '#AAFFAA'
	};
  return (
  		<div>
					<FontAwesomeIcon icon={farDotCircle}/> &nbsp;
					<b>Rest Endpoint:</b> {selectedEndpoint}<br />
				{versions.map((version, index) => (
					<div key={version.id} style={version.active?liveCardStyle:cardStyle} className='slds-table slds-table--bordered'>
						{version.active &&
							<b>Live Version: </b>
						}
						<a onClick={() => selectVersion(selectedEndpoint, version.id)}>
							{version.uploadDate}
						</a><br />
						Name: {version.name}<br />
						Created by: {version.createdBy}<br />
						Version: {version.version}<br />
						Remark: {version.remark}<br />
						<Button type="neutral" onClick={() => downloadExcel(version.restEndpoint, version.id)} icon="download" iconAlign="left" label="Download Excel" />
					</div>
				))}
  		</div>
   );
}
