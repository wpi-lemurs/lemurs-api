import React, { useContext } from 'react';
import { TokenContext } from '../components/token/TokenContext';

export default function Dashboard() {
    const {token} = useContext(TokenContext)
    return (
        <div>
            {/*<h2 className="card-title">Dashboard</h2>*/}
            {/*<br/>*/}
            {(token === "") ? (
                <h5 className="card-title">Please sign in to view the dashboard.</h5>
            ) : (
                <iframe
                    src="/dashboard/"
                    style={{ width: '100%', height: '80vh', border: 'none' }}
                    title="Dashboard"
                />
            )}
        </div>
    );
}
