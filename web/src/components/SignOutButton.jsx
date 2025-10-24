import React from "react";
import { Button } from "react-bootstrap";

/**
 * Renders a sign-out button
 */
export const SignOutButton = ({setToken}) => {

    const handleLogout = () => {
        fetch('/auth/logout', {
            method: 'POST'
        })
        .finally(() => {
            setToken("");
        });
    }

    return (
        <Button variant="secondary" className="ml-auto" onClick={handleLogout}>
            Sign Out
        </Button>
    )
}