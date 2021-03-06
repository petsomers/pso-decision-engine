import React from "react";
import { Button, Spinner, Picklist, PicklistItem, Input } from "react-lightning-design-system";
import axios from "axios";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faListAlt as fasListAlt, faTable as fasTable} from "@fortawesome/free-solid-svg-icons";
import {faFileExcel as farFileExcel} from "@fortawesome/free-regular-svg-icons";

export class FileUpload  extends React.Component {
	constructor(props) {
		super();
		this.state = {
			mode: null,
			selectedDataSetName: null,
			newDataSetName: null,
			selectedUploadFile: null,
			inProgress: false,
			errorMessage: "",
			message: "",
		}
	}

	setMode(mode) {
		this.setState({
			...this.state,
			errorMessage: "",
			mode: mode,
			selectedDataSetName: null,
			newDataSetName: null
		});
	}

	selectDataSet(dataSetName) {
		if (dataSetName==null)
			dataSetName=null
		this.setState({
			...this.state,
			selectedDataSetName: dataSetName,
			errorMessage: "",
			newDataSetName: null
		})
	}

	setNewDataSetName(dataSetName) {
		if (dataSetName==null)
			dataSetName=null
		this.setState({
			...this.state,
			selectedDataSetName: null,
			errorMessage: "",
			newDataSetName: dataSetName
		})
	}

	selectUploadFile(event) {
		this.setState({
			...this.state,
			errorMessage: "",
			selectedUploadFile:event.target.files[0]
		});
	}

	doUpload(event) {
		var dataSetName=this.state.selectedDataSetName==null?this.state.newDataSetName:this.state.selectedDataSetName;
		if (dataSetName==="") dataSetName=null;
		if (this.state.mode==="LIST" || this.state.mode==="LOOKUP") {
			if (dataSetName==null) {
				this.setState({
					...this.state,
					errorMessage:"Please select an exising or new data set.",
				});
				return;
			}
		}
		this.setState({
			...this.state,
			errorMessage:"",
			message: "",
			inProgress:true
		});
    var fd = new FormData();
    fd.append("upload_file", this.state.selectedUploadFile);
		if (this.state.mode==="RULESET") {
			this.uploadFile("setup/form_upload_excel", fd, (data) => {
				this.props.selectVersion(data.restEndpoint, data.ruleSetId);
				this.props.loadEndpoints();
			});
		} else if (this.state.mode==="LIST" || this.state.mode==="LOOKUP") {
			this.uploadFile("dataset/form_upload/"+this.state.mode+"/"+encodeURIComponent(dataSetName), fd, () => {
				this.props.loadDataSets();
				this.setState({
					...this.state,
					mode: null,
					selectedDataSetName: null,
					newDataSetName: null,
					message: "Done.",
					inProgress:false
				});
			});
		}
	}

	uploadFile(url, fd, onsuccess) {
		axios.post(url, fd, window.axiosConfig)
			.then(result => {
				console.log("File upload response", result)
				if (!result.data.ok) {
					this.setState({
						...this.state,
						errorMessage:result.data.errorMessage,
						message: "",
						inProgress:false
					});
				} else {
					if (!result.data.ok) {
						this.setState({
							...this.state,
							errorMessage:result.data.errorMessage,
							message: "",
							inProgress:false
						});
						return;
					}
					this.setState({
						...this.state,
						errorMessage:"",
						message: "",
						inProgress:false
					});
					onsuccess(result.data);
				}
			})
			.catch(error =>  {
				console.log("File upload error response: "+error.message,error)
				this.setState({...this.state, inProgress:false, errorMessage: error.message});
			 });
	}

	render() {
  	return (
		<div>
			{this.state.mode==null &&
			<div>
				<h2><b>What do you want to upload</b></h2>
				<br />
				<Button type="neutral" onClick={() => this.setMode("RULESET")} icon="new" iconAlign="left" label="1. Ruleset Excel File" />
				<br />
				<FontAwesomeIcon icon={farFileExcel}/> &nbsp;
				<i>The Excel contains the complete RuleSet definitions.</i>
				<br /><br />
				<Button type="neutral" onClick={() => this.setMode("LIST")} icon="new" iconAlign="left" label="2. Dataset LIST Text File" />
				<br />
				<FontAwesomeIcon icon={fasListAlt}/> &nbsp;
				<i>
					A Dataset List is a 1 column text file with unique values that can be used in a Rule Condition.<br />
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; No header row is required.
				</i>
				<br /><br />
				<Button type="neutral" onClick={() => this.setMode("LOOKUP")} icon="new" iconAlign="left" label="3. Dataset LOOKUP Text File" />
				<br />
				<FontAwesomeIcon icon={fasTable}/> &nbsp;
				<i>
					A Dataset Lookup is a multi column text file where the first column contains the key.<br />
				 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; A header row is required. The header names should match the exact parameters names as defined in the Rulesets.
				</i>
			</div>
			}
			{this.state.mode!=null &&
				<div style={{position: "absolute", right: "30px", top: "80px"}}>
		      <Button type="brand" onClick={() => this.setMode(null)} icon="left" iconAlign="left" label="Select Another File Type" />
			  </div>
			}
			{this.state.mode==="RULESET" &&
				<div>
					<h2><b>Ruleset Excel File Upload</b></h2>
					<br /><br />
					<FontAwesomeIcon icon={farFileExcel}/> &nbsp; <b>Upload Excel File</b><br /><br />
				</div>
			}
			{this.state.mode==="LIST" &&
				<div>
					<h2><b>Data Set Text File Upload</b></h2>
					<br /><br />
					<FontAwesomeIcon icon={fasListAlt}/> &nbsp; <b>Upload Text File</b><br /><br />
					<Picklist
					  label="Select LIST Data Set"
					  selectedText=""
						value={this.state.selectedDataSetName==null?"":this.state.selectedDataSetName}
					  onValueChange={(value) => this.selectDataSet(value)}
					  menuSize="small"
					  menuStyle={{maxHeight: "20rem", overflowY: "auto"}}
					>
							<PicklistItem key="" label="New Data Set" value="" />
						{this.props.dataSets.filter(ds => ds.type==="LIST").map((dataSet, index) => (
							<PicklistItem key={dataSet.name} label={dataSet.name} value={dataSet.name} />
						))}
					</Picklist>
					{!this.state.selectedDataSetName &&
						<div style={{display: "inline-block", width: "250px", paddingTop:"20px"}}>
							<Input
				        value={this.state.newDataSetName==null?"":this.state.newDataSetName}
				        label="New Data Set Name"
				        onChange={(event) => this.setNewDataSetName(event.target.value)}/>
						</div>
					}
					<br />
				</div>
			}
			{this.state.mode==="LOOKUP" &&
				<div>
					<h2><b>Lookup Text File Upload</b></h2>
					<br /><br />
					<FontAwesomeIcon icon={fasTable}/> &nbsp; <b>Upload Text File</b><br /><br />
					<Picklist
						label="Select LOOKUP Data Set"
						selectedText=""
						value={this.state.selectedDataSetName==null?"":this.state.selectedDataSetName}
						onValueChange={(value) => this.selectDataSet(value)}
						menuSize="small"
						menuStyle={{maxHeight: "20rem", overflowY: "auto"}}
					>
							<PicklistItem key="" label="New Data Set" value="" />
						{this.props.dataSets.filter(ds => ds.type==="LOOKUP").map((dataSet, index) => (
							<PicklistItem key={dataSet.name} label={dataSet.name} value={dataSet.name} />
						))}
					</Picklist>
					{!this.state.selectedDataSetName &&
						<div style={{display: "inline-block", width: "250px", paddingTop:"20px"}}>
							<Input
								value={this.state.newDataSetName==null?"":this.state.newDataSetName}
								label="New Data Set Name"
								onChange={(event) => this.setNewDataSetName(event.target.value)}/>
						</div>
					}
					<br />
				</div>
			}
			{this.state.mode &&
				<div style={{paddingTop: "20px"}}>
					File: <input type="file" id="fileinput" onChange={(event) => this.selectUploadFile(event)}/>
					<br /><br />
					{this.state.selectedUploadFile!=null &&
						<Button type="neutral" onClick={() => this.doUpload()} icon="upload" iconAlign="left" label="Upload File" />
					}
				</div>
			}
			{this.state.inProgress &&
				<Spinner />
			}
			<br /><br />
			{this.state.errorMessage!=="" &&
				<div style={{color: "red"}}>{this.state.errorMessage}</div>
			}
			{this.state.message!=="" &&
				<div style={{color: "green"}}>{this.state.message}</div>
			}
		</div>
	)}

}
