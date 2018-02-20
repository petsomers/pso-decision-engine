import React from "react";

export const NavigationBar = ({message, showLogout=true}) => {

	const logoStyle={position:"fixed", top:"0px", left:"05px", zIndex:"4", backgroundColor: "#000000"}
	const logoutStyle={position:"fixed", top:"0px", right:"5px", zIndex:"4", fontSize:"75%"}
    return (
    		<div style={logoStyle}>
					<img src="logo-small.png" />
					Decision Engine
    		</div>
   );
}
