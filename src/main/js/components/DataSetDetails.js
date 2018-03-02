import React from "react";
import { Button } from "react-lightning-design-system";

export const DataSetDetails = ({selectedDataSetInfo}) => {
	return (
  	<div>
				{selectedDataSetInfo.type=="LOOKUP" ? (
					<i className="fas fa-th-list"></i>
				):(
					<i className="fas fa-bars"></i>
				)
				}
				{selectedDataSetInfo.name}
  		</div>
   );
}
