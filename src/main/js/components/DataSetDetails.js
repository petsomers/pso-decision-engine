import React from "react";
import { Button } from "react-lightning-design-system";

export const DataSetDetails = ({selectedDataSetInfo, dataSetData, loadMoreDataSetData}) => {
	return (
  	<div>
				{selectedDataSetInfo.type=="LOOKUP" ? (
					<i className="fas fa-th-list"></i>
				):(
					<i className="fas fa-bars"></i>
				)
				}
				&nbsp;&nbsp;
				{selectedDataSetInfo.name}
				<br /><br />
				{dataSetData &&
					<div>
					{dataSetData.rows &&
						<div>
						{dataSetData.rows.map((row, index) => (
							<div key={index}>{row}</div>
						))}
						</div>
					}
					{dataSetData.hasMore &&
						<div>
							<Button type="neutral" onClick={() => loadMoreDataSetData(selectedDataSetInfo, dataSetData.rows[dataSetData.rows.length-1])} icon="new" iconAlign="left" label="Load More" />
						</div>
					}
					</div>
				}
  		</div>
   );
}
