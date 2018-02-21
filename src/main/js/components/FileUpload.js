import React from "react";
import { Button } from "react-lightning-design-system";

export const FileUpload = ({fileUploadOk}) => {

  return (
    		<div>
					Select File
					<Button type="neutral" onClick={() => openFileUpload()} icon="new" iconAlign="left" label="Upload Excel" />
    		</div>
   );
}
