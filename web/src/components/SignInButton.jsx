import React from "react";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";
import { Button } from "react-bootstrap";

export const SignInButton = () => {
    const { instance } = useMsal();

    // Use loginRedirect to avoid COOP window.closed issues with popups
    const handleLogin = () => {
        instance.loginRedirect(loginRequest).catch(e => {
            console.log(e);
        });
        // Note: loginRedirect does not return a response here; token handling should be done after redirect
    }
    return (
        <Button onClick={handleLogin}>Sign in</Button>
    )
}
