import React from "react";
import { Button } from "react-lightning-design-system";

export const DataSetDetails = ({selectedDataSetInfo, dataSetData, loadMoreDataSetData}) => {
	return (
  	<div>
			<div style={{display: "inline-block", width: "350px"}}>
				{selectedDataSetInfo.type=="LOOKUP" ? (
					<i className="fas fa-th-list"></i>
				):(
					<i className="fas fa-bars"></i>
				)
				}
				&nbsp;&nbsp;
				<b>{selectedDataSetInfo.name}</b>
			</div>
			<div style={{display: "inline-block", paddingLeft: "25px"}}>
				<Button type="neutral" onClick={() => alert("Download")} icon="new" iconAlign="left" label="Download" />
				&nbsp;
				<Button type="neutral" onClick={() => alert("Download")} icon="new" iconAlign="left" label="Delete" />
			</div>

			{dataSetData &&
				<div>
				{dataSetData.rows &&
					<div>
					{dataSetData.rows.map((row, index) => (
						<div key={index}>{row}</div>
					))}
					</div>
				}
				{dataSetData.hasMore && !dataSetData.loading &&
					<div>
						<Button type="neutral" onClick={() => loadMoreDataSetData(selectedDataSetInfo, dataSetData.rows[dataSetData.rows.length-1])} icon="new" iconAlign="left" label="Load More" />
					</div>
				}
				{dataSetData.loading &&
					<div>
						<i className="fas fa-spinner fa-spin"></i>
					</div>
				}
				</div>
			}
  	</div>
   );
}
