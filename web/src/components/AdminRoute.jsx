import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import useToken from './token/useToken';

const ALLOW_LOCAL_ADMIN = true; // set to false to disable local override
const isLocalhost = () => typeof window !== 'undefined' && window.location.hostname === 'localhost';

// Wrap protected admin content: <AdminRoute><AdminPanel /></AdminRoute>
const AdminRoute = ({ children }) => {
  const { token } = useToken();
  const [isAdmin, setIsAdmin] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

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
        })
        .finally(() => {
            setIsLoading(false);
        });
    } else {
        setIsAdmin(false);
        setIsLoading(false);
    }
  }, [token]);

  if (isLoading) {
      return <div>Loading...</div>; // Or a spinner component
  }

  return isAdmin ? children : <Navigate to="/" replace />;
};

export default AdminRoute;
