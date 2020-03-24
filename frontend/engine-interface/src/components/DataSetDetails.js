import React from "react";
import { Button } from "react-lightning-design-system";
import { Table , TableRow, TableBody, TableRowColumn } from "react-lightning-design-system";
import { Modal, ModalHeader, ModalFooter, ModalContent } from "react-lightning-design-system";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTable as fasTable, faListAlt as fasListAlt, faSpinner as fasSpinner} from "@fortawesome/free-solid-svg-icons";

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
				{selectedDataSetInfo.type==="LOOKUP" ? (
					<span>
						<FontAwesomeIcon icon={fasTable}/>
						&nbsp; <b>Lookup Table</b>
					</span>
				):(
					<span>
						<FontAwesomeIcon icon={fasListAlt}/>
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
			{selectedDataSetInfo.type==="LOOKUP" && dataSetData.headers.length>0 &&
				<div>
				{dataSetData.rows && dataSetData.rows.length>0 &&
				<Table bordered>
					<TableBody>
						<TableRow>
						{dataSetData.headers.map((headerName, indexh) => (
							<TableRowColumn key={"column"+indexh}><b>{headerName}</b></TableRowColumn>
						))}
						</TableRow>
					{dataSetData.rows.map((row, index) => (
						<TableRow key={"key"+index}>
							{row.map((colvalue, index2) => (
							<TableRowColumn key={"val_"+index+"_"+index2}>{colvalue}</TableRowColumn>
							))}
						</TableRow>
					))}
					</TableBody>
				</Table>
				}
				{selectedDataSetInfo.type==="LOOKUP" && dataSetData.headers.length>0 && dataSetData.rows>0 &&
					<div><i>No data.</i></div>
				}
				{dataSetData.hasMore && !dataSetData.loading &&
					<div style={{padding: "20px"}}>
						<Button type="neutral" onClick={() => this.props.loadMoreDataSetData(selectedDataSetInfo, dataSetData.rows[dataSetData.rows.length-1][0])} icon="more" iconAlign="left" label="Load More" />
					</div>
				}
				</div>
			}
			{selectedDataSetInfo.type==="LIST" &&
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
					<FontAwesomeIcon icon={fasSpinner} spin={true}/>
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
