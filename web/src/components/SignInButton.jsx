import React, { useEffect } from "react";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";
import { Button } from "react-bootstrap";

export const SignInButton = ({ setToken }) => {
  const { instance, accounts } = useMsal();

  // Start an interactive login using redirect (no popup) to avoid popup stuck behavior.
  const handleLogin = () => {
    instance.loginRedirect(loginRequest).catch((e) => {
      console.error("loginRedirect error:", e);
    });
  };

  // When accounts become available (i.e. user returned from redirect or is already signed in),
  // try to silently acquire an access token and perform the backend exchange (loginUser).
  useEffect(() => {
    const exchangeToken = async () => {
      if (!accounts || accounts.length === 0) return;

      try {
        const response = await instance.acquireTokenSilent({
          ...loginRequest,
          account: accounts[0],
        });

        // Keep existing behavior: send the accessToken to backend and set the returned token
        const token = await loginUser(response.accessToken);
        setToken(`Bearer ${token.accessToken}`);
      } catch (err) {
        // Silent acquisition can fail (interaction required). Fall back to interactive redirect
        // rather than a popup to avoid the popup stuck issue.
        console.warn("acquireTokenSilent failed, falling back to redirect:", err);
        try {
          await instance.acquireTokenRedirect({
            ...loginRequest,
            account: accounts[0],
          });
        } catch (redirectErr) {
          console.error("acquireTokenRedirect error:", redirectErr);
        }
      }
    };

    exchangeToken();
  }, [accounts, instance, setToken]);

  return <Button onClick={handleLogin}>Sign in</Button>;
};

async function loginUser(accessToken) {
  return fetch(`${process.env.REACT_APP_LEMURS_API_HOST}/auth/login`, {
    method: "POST",
    headers: new Headers({ "content-type": "application/json" }),
    body: JSON.stringify({ accessToken: accessToken }),
  }).then(async (response) => {
    if (!response.ok) {
      throw response.status;
    }

    return response.json();
  });
}