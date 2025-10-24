/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

import React, { useContext } from 'react';
import Navbar from 'react-bootstrap/Navbar';
import { useMsal } from "@azure/msal-react";
import { SignInButton } from './SignInButton';
import { SignOutButton } from './SignOutButton';
import { TokenContext } from './token/TokenContext';
import { NotificationContainer } from 'react-notifications';
import 'react-notifications/lib/notifications.css';

/**
 * Renders the navbar component with a sign-in or sign-out button depending on whether or not a user is authenticated
 * @param props
 */
export const PageLayout = (props) => {
    const { token } = useContext(TokenContext);
    const { accounts } = useMsal();

    return (
        <>
            <Navbar bg="primary" variant="dark" className="navbarStyle">
                <a className="navbar-brand" href="/">
                    LEMURS
                </a>
                <div className="collapse navbar-collapse justify-content-end">
                    {token !== "" &&
                        <>
                            <a className="nav-link text-white" href="admin" style={{marginRight: "10px"}}>Admin Panel</a>
                            <a className="nav-link text-white" href="dashboard" style={{marginRight: "10px"}}>Dashboard</a>
                        </>
                    }

                    {(token === "") ? (
                        <SignInButton setToken={props.setToken}/>
                    ) : (
                        <>
                            {accounts.length > 0 && (
                                <span style={{ color: "white", marginRight: "15px", alignSelf: "center" }}>
                                    Welcome, {accounts[0].name}
                                </span>
                            )}
                            <SignOutButton setToken={props.setToken} />
                        </>
                    )}

                </div>
            </Navbar>
            <div className="content">
                <   NotificationContainer />
                {props.children}
            </div>
        </>
    );
};