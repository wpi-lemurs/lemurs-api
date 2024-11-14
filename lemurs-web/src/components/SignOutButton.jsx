import React from "react";
import { useMsal } from "@azure/msal-react";
import DropdownButton from "react-bootstrap/DropdownButton";
import Dropdown from "react-bootstrap/Dropdown";
import useToken from "./token/useToken";

/**
 * Renders a sign-out button
 */
export const SignOutButton = ({setToken}) => {
    const { instance } = useMsal();

    const handleLogout = (logoutType) => {
        setToken("")

        if (logoutType === "popup") {
            instance.logoutPopup({
                postLogoutRedirectUri: "/",
                mainWindowRedirectUri: "/"
            }).then(() => {
                setToken("")
            });
        } else if (logoutType === "redirect") {
            instance.logoutRedirect({
                postLogoutRedirectUri: "/",
            }).then(() => {
                setToken("")
            });
        }
    }

    
    return (
        <DropdownButton variant="secondary" className="ml-auto" drop="start" title="Sign Out">
            <Dropdown.Item as="button" onClick={() => handleLogout("popup")}>Sign out using Popup</Dropdown.Item>
            <Dropdown.Item as="button" onClick={() => handleLogout("redirect")}>Sign out using Redirect</Dropdown.Item>
        </DropdownButton>
    )
}
