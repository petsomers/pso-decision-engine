import React from "react";
import { Button } from "react-lightning-design-system";

export const RuleSetVersionSelection = ({layout, selectedEndpoint, versions, selectVersion}) => {
	const cardStyle= {
		padding: "5px",
		whiteSpace: "nowrap"
	}
  return (
  		<div>
				Rest Endpoint: {selectedEndpoint}<br />

				{versions.map((version, index) => (
					<div key={version.id} style={cardStyle} className='slds-table slds-table--bordered'>
						{version.active &&
							<b>Live Version: </b>
						}
						<a onClick={() => selectVersion(selectedEndpoint, version.id)}>
							{version.uploadDate}
						</a><br />
						Name: {version.name}<br />
						Created by: {version.createdBy}<br />
						Version: {version.version}<br />
						Remark: {version.remark}
					</div>
				))}
  		</div>
   );
}
