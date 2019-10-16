import React from "react";

export const NavigationBar = ({message, showLogout = true}) => {

    const logoStyle = {
        position: "fixed", top: "0px", left: "0px", zIndex: "10",
        backgroundColor: "#FFFFFF", width: "100%",
        paddingLeft: "5px", paddingTop: "2px"
    };

    return (
        <div style={logoStyle}>
            {window.showHeader && (
                <img src={`${process.env.PUBLIC_URL}/logo-small-white.png`}/>
            )}
        </div>
    );
};
