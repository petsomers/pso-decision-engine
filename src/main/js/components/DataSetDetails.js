import React from "react";
import { Button } from "react-lightning-design-system";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn } from "react-lightning-design-system";

export const DataSetDetails = ({selectedDataSetInfo, dataSetData, loadMoreDataSetData, downloadDataSet}) => {
	return (
  	<div>
			<div style={{display: "inline-block", width: "350px"}}>
				{selectedDataSetInfo.type=="LOOKUP" ? (
					<span>
						<i className="fas fa-table"></i>
						&nbsp; <b>Lookup Table</b>
					</span>
				):(
					<span>
						<i className="fas fa-list-alt"></i>
						&nbsp; <b>List</b>
					</span>
				)
				}
				&nbsp;&nbsp;
				<b>{selectedDataSetInfo.name}</b>
			</div>
			<div style={{display: "inline-block", paddingLeft: "25px"}}>
				<Button type="neutral" onClick={() => downloadDataSet(selectedDataSetInfo)} icon="download" iconAlign="left" label="Download" />
				&nbsp;
				<Button type="neutral" onClick={() => alert("Download")} icon="delete" iconAlign="left" label="Delete" />
			</div>
			{selectedDataSetInfo.type=="LOOKUP" && dataSetData.headers.length>0 &&
				<div>
				<Table bordered>
					<TableHeader>
						<TableRow>
							{dataSetData.headers.map((headerName, indexh) => (
								<TableHeaderColumn key={"column"+indexh}><b>{headerName}</b></TableHeaderColumn>
							))}
						</TableRow>
					</TableHeader>
					{dataSetData.rows && dataSetData.rows.length>0 &&
					<TableBody>
						{dataSetData.rows.map((row, index) => (
							<TableRow key={"key"+index}>
								{row.map((colvalue, index2) => (
								<TableRowColumn key={"val_"+index+"_"+index2}>{colvalue}</TableRowColumn>
							))}
							</TableRow>
						))}
					</TableBody>
					}
				</Table>
				{dataSetData.hasMore && !dataSetData.loading &&
					<div style={{padding: "20px"}}>
						<Button type="neutral" onClick={() => loadMoreDataSetData(selectedDataSetInfo, dataSetData.rows[dataSetData.rows.length-1][0])} icon="more" iconAlign="left" label="Load More" />
					</div>
				}
				</div>
			}
			{selectedDataSetInfo.type=="LIST" &&
				<div>
				{dataSetData &&
					<div>
					{dataSetData.rows &&
						<div>
							{dataSetData.rows.map((row, index) => (
								<div key={index}>{row}</div>
							))}
							</div>
						}
					</div>
					}

					{dataSetData.hasMore && !dataSetData.loading &&
						<div style={{padding: "20px"}}>
							<Button type="neutral" onClick={() => loadMoreDataSetData(selectedDataSetInfo, dataSetData.rows[dataSetData.rows.length-1])} icon="more" iconAlign="left" label="Load More" />
						</div>
					}
				</div>
				}
				{dataSetData.loading &&
					<div style={{padding: "20px"}}>
						<i className="fas fa-spinner fa-spin"></i>
					</div>
				}

  	</div>
   );
}
