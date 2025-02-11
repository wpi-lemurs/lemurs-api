/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

import React, { useContext } from 'react';
import Navbar from 'react-bootstrap/Navbar';

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
    const { token } = useContext(TokenContext)    

    return (
        <>
            <Navbar bg="primary" variant="dark" className="navbarStyle">
                <a className="navbar-brand" href="/">
                    LEMURS
                </a>
                <div className="collapse navbar-collapse justify-content-end">
                    {(token === "") ? <SignInButton setToken={props.setToken}/> : <SignOutButton setToken={props.setToken} />}
                </div>
            </Navbar>
            <div className="content">
            <   NotificationContainer />
                {props.children}
            </div>
        </>
    );
};
