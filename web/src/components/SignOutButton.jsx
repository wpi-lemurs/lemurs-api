import React from "react";
import { Button } from "react-bootstrap";
import { useMsal } from "@azure/msal-react"; // <-- 1. Import useMsal

/**
 * Renders a sign-out button
 */
export const SignOutButton = ({ setToken }) => {
  const { instance } = useMsal(); // <-- 2. Get the MSAL instance

  const handleLogout = () => {
    fetch(`${process.env.REACT_APP_LEMURS_API_HOST}/auth/logout`, {
      method: "POST",
    })
      .catch((err) => {
        console.error("Backend logout failed:", err);
      })
      .finally(() => {
        setToken("");
        // 3. Call MSAL logout to clear the Microsoft session
        instance.logoutRedirect().catch((e) => {
          console.error("MSAL logout error:", e);
        });
      });
  };

  return (
    <Button variant="secondary" className="ml-auto" onClick={handleLogout}>
      Sign Out
    </Button>
  );
};