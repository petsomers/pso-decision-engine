import React from "react";
import { Button } from "react-lightning-design-system";

export const DataSetDetails = ({selectedDataSetInfo, dataSetData}) => {
	return (
  	<div>
				{selectedDataSetInfo.type=="LOOKUP" ? (
					<i className="fas fa-th-list"></i>
				):(
					<i className="fas fa-bars"></i>
				)
				}
				{selectedDataSetInfo.name}
				<br /><br />
				{dataSetData && dataSetData.rows &&
					<div>
					{dataSetData.rows.map((row, index) => (
						<div key={index}>{row}</div>
					))}
					</div>
				}
  		</div>
   );
}
