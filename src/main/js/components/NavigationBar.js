import React from "react";

export const NavigationBar = ({message, showLogout=true}) => {

	const logoStyle={position:"fixed", top:"1px", left:"0px", zIndex:"4", backgroundColor: "#FFFFFF"}
    return (
    		<div style={logoStyle}>
					<img src="logo-small-white.png" />
    		</div>
   );
}
