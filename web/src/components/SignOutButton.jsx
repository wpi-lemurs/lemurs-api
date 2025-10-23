import React from "react";
import { Button } from "react-bootstrap";

/**
 * Renders a sign-out button
 */
export const SignOutButton = ({setToken}) => {
    return (
        <Button variant="secondary" className="ml-auto" onClick={() => setToken("")}>
            Sign Out
        </Button>
    )
}