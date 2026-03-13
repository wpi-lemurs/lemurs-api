import React, { useContext, useState, useEffect } from 'react';
import { Navbar, Nav } from 'react-bootstrap';
import { useMsal } from "@azure/msal-react";
import { SignInButton } from './SignInButton';
import { SignOutButton } from './SignOutButton';
import { TokenContext } from './token/TokenContext';
import { NotificationContainer } from 'react-notifications';
import 'react-notifications/lib/notifications.css';
import { NavLink } from 'react-router-dom';

export const PageLayout = (props) => {
    const { token } = useContext(TokenContext);
    const { accounts } = useMsal();
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        if (token) {
            fetch(`${process.env.REACT_APP_LEMURS_API_HOST}/validate`, {
                method: 'GET',
                headers: {
                    'Authorization': token
                }
            })
            .then(res => {
                if (res.ok) {
                    setIsAdmin(true);
                } else {
                    setIsAdmin(false);
                }
            })
            .catch(() => {
                setIsAdmin(false);
            });
        } else {
            setIsAdmin(false);
        }
    }, [token]);

    // Helper function to extract first name from a full name string
    function extractFirstName(fullName) {
        if (!fullName) return "";

        // Handle "lastName, firstName" format
        if (fullName.includes(",")) {
            const parts = fullName.split(",");
            return parts[1].trim();
        }

        // Handle "firstName lastName" format
        const parts = fullName.trim().split(" ");
        return parts[0]; // first part assumed to be first name
    }

    let firstName = "";

    if (accounts.length > 0) {
        const account = accounts[0];
        const claims = account.idTokenClaims || {};

        // Determine first name
        firstName =
            claims.given_name ||
            extractFirstName(claims.name) ||
            extractFirstName(account.name) ||
            "";
    }

    return (
        <>
            <Navbar bg="primary" variant="dark" className="navbarStyle" expand="lg">
                <a className="navbar-brand" href="/" style={{marginRight: "20px"}}>
                    LEMURS
                </a>

                <Nav className="me-auto">
                    {token && <Nav.Link as={NavLink} to="/dashboard">Dashboard</Nav.Link>}
                    {isAdmin && <Nav.Link as={NavLink} to="/admin">Admin</Nav.Link>}
                </Nav>

                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav" className="justify-content-end">
                    <Nav>
                        {(token === "") ? (
                            <SignInButton setToken={props.setToken}/>
                        ) : (
                            <>
                                {firstName && (
                                    <Navbar.Text style={{ color: "white", marginRight: "15px" }}>
                                        Welcome, {firstName}
                                    </Navbar.Text>
                                )}
                                <SignOutButton setToken={props.setToken} />
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
            <div className="content">
                <   NotificationContainer />
                {props.children}
            </div>
        </>
    );
};