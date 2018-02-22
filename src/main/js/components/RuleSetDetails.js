import React from "react";
import { Button, Spinner } from "react-lightning-design-system";
import axios from "axios"

export const RuleSetDetails = ({loadRuleSet, clearSelectedVersion, layout, selectedEndpoint, selectedVersion, versions, ruleSetDetails}) => {
  	return (
		{selectedVersion=='' &&
			

		}
		{selectedVersion!='' &&

		}
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
