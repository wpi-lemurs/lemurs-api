import React, { useContext } from 'react';
import { Navbar, Nav } from 'react-bootstrap';
import { useMsal } from "@azure/msal-react";
import { SignInButton } from './SignInButton';
import { SignOutButton } from './SignOutButton';
import { TokenContext } from './token/TokenContext';
import { NotificationContainer } from 'react-notifications';
import 'react-notifications/lib/notifications.css';

export const PageLayout = (props) => {
    const { token } = useContext(TokenContext);
    const { accounts } = useMsal();

    let firstName = "";

    if (accounts.length > 0) {
        const account = accounts[0];
        const claims = account.idTokenClaims || {};

        // Safely handle name retrieval
        firstName =
            claims.given_name ||
            account.name ||
            "";
    }

    return (
        <>
            <Navbar bg="primary" variant="dark" className="navbarStyle" expand="lg">
                <a className="navbar-brand" href="/" style={{marginRight: "20px"}}>
                    LEMURS
                </a>

                <Nav className="me-auto">
                    {token !== "" &&
                        <>
                            <a className="nav-link text-white" href="admin">Admin Panel</a>
                            <a className="nav-link text-white" href="dashboard">Dashboard</a>
                        </>
                    }
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