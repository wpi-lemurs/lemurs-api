import React, { useContext, useEffect, useState } from 'react';
import { TokenContext } from '../components/token/TokenContext';

export default function Dashboard() {
    const { token } = useContext(TokenContext);

    // 1. Add a new state to track if we are verified
    // 'verifying', 'success', or 'failed'
    const [authStatus, setAuthStatus] = useState('verifying');

    // 2. Add an effect to check our auth status on page load
    useEffect(() => {
        // This only runs if the React token exists
        if (token !== "") {
            fetch('/api/validate')
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
    }, [token]); // Re-run if the token changes

    // 3. Update the render logic based on authStatus

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