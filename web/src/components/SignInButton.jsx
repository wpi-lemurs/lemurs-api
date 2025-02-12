import React from "react";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";
import { Button } from "react-bootstrap";

export const SignInButton = ({setToken}) => {
    const { instance } = useMsal();

    const handleLogin = () => {
        instance.loginPopup(loginRequest).catch(e => {
            console.log(e);
        }).then(async (response) => {
            const token = await loginUser(response.accessToken)
            setToken(`Bearer ${token.accessToken}`);
        });
    }
    return (
        <Button onClick={handleLogin}>Sign in</Button>
    )
}

async function loginUser(accessToken) {

    return fetch(
      `${process.env.REACT_APP_LEMURS_API_HOST}/auth/login`,
      {
        method: "POST",
        headers: new Headers({ "content-type": "application/json" }),
        body: JSON.stringify({ accessToken: accessToken }),
      }
    ).then(async (response) => {
      if (!response.ok) {
        throw response.status;
      }
      
      return response.json();
    });
}