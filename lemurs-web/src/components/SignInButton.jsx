import React, { useContext } from "react";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";
import DropdownButton from "react-bootstrap/DropdownButton";
import Dropdown from "react-bootstrap/Dropdown";

/**
 * Renders a drop down button with child buttons for logging in with a popup or redirect
 */
export const SignInButton = ({setToken}) => {
    const { instance } = useMsal();

    const handleLogin = (loginType) => {
        if (loginType === "popup") {
            instance.loginPopup(loginRequest).catch(e => {
                console.log(e);
            }).then(async (response) => {
                const token = await loginUser(response.accessToken)
                setToken(`${token.tokenType} ${token.accessToken}`);
            });
        } else if (loginType === "redirect") {
            instance.loginRedirect(loginRequest).catch(e => {
                console.log(e);
            }).then(async (response) => {
                const token = await loginUser(response.accessToken)
                setToken(`${token.tokenType} ${token.accessToken}`);
            });
        }
    }
    return (
        <DropdownButton variant="secondary" className="ml-auto" drop="start" title="Sign In">
            <Dropdown.Item as="button" onClick={() => handleLogin("popup")}>Sign in using Popup</Dropdown.Item>
            <Dropdown.Item as="button" onClick={() => handleLogin("redirect")}>Sign in using Redirect</Dropdown.Item>
        </DropdownButton>
    )
}


async function loginUser(accessToken) {

    return fetch(
      `${process.env.REACT_APP_LEMURS_SERVER_HOST}/auth/login`,
      {
        method: "POST",
        headers: new Headers({ "content-type": "application/json" }),
        body: JSON.stringify({ code: accessToken }),
      }
    ).then((response) => {
      if (!response.ok) {
        throw response.status;
      }
      
      return response.json();
    });
}