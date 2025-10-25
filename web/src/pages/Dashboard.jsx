import React, { useContext, useEffect, useState } from 'react';
import { TokenContext } from '../components/token/TokenContext';

export default function Dashboard() {
    const { token } = useContext(TokenContext);
    const [authStatus, setAuthStatus] = useState('verifying');

    const validateUrl = `${process.env.REACT_APP_LEMURS_API_HOST}/api/validate`;
    const refreshUrl = `${process.env.REACT_APP_LEMURS_API_HOST}/auth/refresh`;

    useEffect(() => {
        if (token !== "") {
            fetch(validateUrl, {
                method: 'GET',
                credentials: 'include'
            })
            .then(validateRes => {
                if (validateRes.ok) {
                    return 'success';
                }
                if (validateRes.status === 401) {
                    return fetch(refreshUrl, {
                        method: 'POST',
                        credentials: 'include'
                    })
                    .then(refreshRes => {
                        if (refreshRes.ok) {
                            return 'success';
                        } else {
                            return 'failed';
                        }
                    });
                }
                return 'failed';
            })
            .then(status => {
                setAuthStatus(status);
            })
            .catch(err => {
                console.error("Auth check/refresh failed:", err);
                setAuthStatus('failed');
            });
        }
    }, [token, validateUrl, refreshUrl]);

    if (token === "") {
        return <h5 className="card-title">Please sign in to view the dashboard.</h5>;
    }

    if (authStatus === 'verifying') {
        return <h5 className="card-title">Verifying session...</h5>;
    }

    if (authStatus === 'failed') {
        return <h5 className="card-title">Your session has expired. Please sign out and sign back in to continue.</h5>;
    }

    return (
        <iframe
            src="/dashboard/"
            style={{ width: '100%', height: '100vh', border: 'none' }}
            title="Dashboard"
        />
    );
}