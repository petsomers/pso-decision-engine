import React from "react";
import { Button, Spinner } from "react-lightning-design-system";
import axios from "axios"

export class FileUpload  extends React.Component {
	constructor(props) {
		super();
		this.state = {
			selectedUploadFile: null,
			inProgress: false,
			errorMessage: "",
			message: "",
		}
	}

	selectUploadFile(event) {
		this.setState({
			...this.state,
			selectedUploadFile:event.target.files[0]
		});
	}

	doUpload(event) {
			this.setState({
				...this.state,
				errorMessage:"",
				message: "",
				inProgress:true
			});
			var xhr = new XMLHttpRequest();
		    var fd = new FormData();
		    fd.append("upload_file", this.state.selectedUploadFile);

		    axios.post('setup/form_upload_excel', fd)
	        .then(result => {
	        	console.log("File upload response", result)
						if (!result.data.ok) {
							this.setState({
			    			...this.state,
			    			errorMessage:result.data.errorMessage,
			    			message: "?",
			    			inProgress:false
		    			});
						} else {
							this.setState({
								...this.state,
								errorMessage:"",
								message: "Rest Endpoint: "+result.data.restEndpoint,
								inProgress:false
							});
							this.props.setSelectedVersion(result.data.restEndpoint, result.data.ruleSetId);
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
			<h1>Excel File Upload</h1>
			<i className="fa fa-file-excel-o" aria-hidden="true"></i> <b>Upload Excel File</b><br /><br />
			File: <input type="file" id="fileinput" onChange={(event) => this.selectUploadFile(event)}/>
			<br />
			{this.state.selectedUploadFile!=null &&
				<Button type="neutral" onClick={() => this.doUpload()} icon="new" iconAlign="left" label="Upload File" />
			}
			{this.state.inProgress &&
				<Spinner />
			}
			<br /><br />
			{this.state.errorMessage!="" &&
				<div style={{color: "red"}}>{this.state.errorMessage}</div>
			}
			{this.state.message!="" &&
				<div style={{color: "green"}}>{this.state.message}</div>
			}

		</div>
   )
 }
}
