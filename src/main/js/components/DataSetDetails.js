import React from "react";
import { Button } from "react-lightning-design-system";
import { Table , TableHeader, TableRow, TableHeaderColumn, TableBody, TableRowColumn } from "react-lightning-design-system";
import { Modal, ModalHeader, ModalFooter, ModalContent } from "react-lightning-design-system";

export class  DataSetDetails extends React.Component {
	constructor(props) {
		super();
		this.state = {
			deleteConfirmAction: false
		}
	}

	deleteRequest() {
		this.setState({
			...this.state,
			deleteConfirmAction: true
		});
	}

	confirmDelete() {
		this.setState({
			...this.state,
			deleteConfirmAction: false
		});
		this.props.deleteDataSet(this.props.selectedDataSetInfo)
	}

	cancelDelete() {
		this.setState({
			...this.state,
			deleteConfirmAction: false
		});
	}

	render() {
		var selectedDataSetInfo=this.props.selectedDataSetInfo;
		var dataSetData=this.props.dataSetData;
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
				<Button type="neutral" onClick={() => this.props.downloadDataSet(selectedDataSetInfo)} icon="download" iconAlign="left" label="Download" />
				&nbsp;
				<Button type="neutral" onClick={() => this.deleteRequest()} icon="delete" iconAlign="left" label="Delete" />
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
						<Button type="neutral" onClick={() => this.props.loadMoreDataSetData(selectedDataSetInfo, dataSetData.rows[dataSetData.rows.length-1][0])} icon="more" iconAlign="left" label="Load More" />
					</div>
				}
				</div>
			}
			{selectedDataSetInfo.type=="LIST" &&
				<div>
				{dataSetData &&
					<div>
					<Table bordered>
					{dataSetData.rows &&
						<TableBody>
							{dataSetData.rows.map((row, index) => (
								<TableRow key={"key"+index}>
									<TableRowColumn>{row}</TableRowColumn>
								</TableRow>
							))}
						</TableBody>
						}
					</Table>
					</div>
				}

				{dataSetData.hasMore && !dataSetData.loading &&
					<div style={{padding: "20px"}}>
						<Button type="neutral" onClick={() => this.props.loadMoreDataSetData(selectedDataSetInfo, dataSetData.rows[dataSetData.rows.length-1])} icon="more" iconAlign="left" label="Load More" />
					</div>
				}
				</div>
			}
			{dataSetData.loading &&
				<div style={{padding: "20px"}}>
					<i className="fas fa-spinner fa-spin"></i>
				</div>
			}
			{this.state.deleteConfirmAction &&
				<Modal opened>
					<ModalHeader title="Delete Dataset" />
					<ModalContent>
						<div className="slds-p-around--small">
							Are you sure to delete this Dataset?
						</div>
					</ModalContent>
					<ModalFooter directional={null}>
						<Button type="neutral" label="Cancel" onClick={() => this.cancelDelete()}/>
						<Button type="brand" label="OK" onClick={() => this.confirmDelete()}/>
					</ModalFooter>
				</Modal>
			}
  	</div>
   );
 	}
}
