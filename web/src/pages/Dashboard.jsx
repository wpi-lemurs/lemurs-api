import React, { useContext, useEffect, useState } from 'react';
import { TokenContext } from '../components/token/TokenContext';

export default function Dashboard() {
    const { token } = useContext(TokenContext);
    const [authStatus, setAuthStatus] = useState('verifying');

    useEffect(() => {
        if (token !== "") {
            fetch(`${process.env.REACT_APP_LEMURS_API_HOST}/api/validate`, {
                method: 'GET',
                credentials: 'include'
            })
                .then(res => {
                    if (res.ok) {
                        setAuthStatus('success');
                    } else {
                        setAuthStatus('failed');
                    }
                })
                .catch(err => {
                    console.error("Auth check failed:", err);
                    setAuthStatus('failed');
                });
        }
    }, [token]);

    // Case 1: User is definitely logged out (no React token)
    if (token === "") {
        return <h5 className="card-title">Please sign in to view the dashboard.</h5>;
    }

    // Case 2: We have a token, but we are checking it...
    if (authStatus === 'verifying') {
        return <h5 className="card-title">Verifying session...</h5>;
    }

    // Case 3: We have a token, but the check failed (cookie expired)
    if (authStatus === 'failed') {
        return <h5 className="card-title">Your session has expired. Please sign out and sign back in to continue.</h5>;
    }

    // Case 4: We have a token AND the check succeeded
    return (
        <iframe
            src="/dashboard/"
            style={{ width: '100%', height: '100vh', border: 'none' }}
            title="Dashboard"
        />
    );
}