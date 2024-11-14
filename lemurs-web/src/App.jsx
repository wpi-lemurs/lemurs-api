import React, { useState, useMemo, useContext } from 'react';

import { PageLayout } from './components/PageLayout';
import { loginRequest } from './authConfig';

import { AuthenticatedTemplate, UnauthenticatedTemplate, useMsal } from '@azure/msal-react';
import './App.css';
import Button from 'react-bootstrap/Button';
import { TokenContext } from './components/token/TokenContext';
import useToken from './components/token/useToken';

/**
 * Renders information about the signed-in user or a button to retrieve data about the user
 */

const ProfileContent = () => {
    const { instance, accounts } = useMsal();

    function RequestProfileData() {
        // Silently acquires an access token which is then attached to a request for MS Graph data
        instance
            .acquireTokenSilent({
                ...loginRequest,
                account: accounts[0],
            })
            .then((response) => {
                // response.accessToken
            });
    }

    return (
        <>
        </>
    );
};

/**
 * If a user is authenticated the ProfileContent component above is rendered. Otherwise a message indicating a user is not authenticated is rendered.
 */
const MainContent = () => {
    const {token} = useContext(TokenContext)

    return (
        <div className="App">
            {(token === "") ? (
                <h5 className="card-title">Please sign in to use the LEMURS web interface.</h5>
            ) : (
                <h5> Not Ready Yet... </h5>
            ) }
        </div>
    );
};

export default function App() {
     const {token, setToken} = useToken()
     const tokenProvider = useMemo(() => ({token: token, setToken: setToken}), [token])

    return (
        <TokenContext.Provider value={tokenProvider}>
            <PageLayout setToken={setToken}> 
                <MainContent />
            </PageLayout>
        </TokenContext.Provider>
    );
}
