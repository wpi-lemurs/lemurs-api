import React from 'react';
import { Navigate } from 'react-router-dom';
import useToken from './token/useToken';

const ALLOW_LOCAL_ADMIN = true; // set to false to disable local override
const isLocalhost = () => typeof window !== 'undefined' && window.location.hostname === 'localhost';

// Wrap protected admin content: <AdminRoute><AdminPanel /></AdminRoute>
const AdminRoute = ({ children }) => {
  const { token } = useToken();

  const isAdmin = () => {
    if (ALLOW_LOCAL_ADMIN && isLocalhost()) return true;
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.roles && payload.roles.includes('admin');
    } catch (e) {
      return false;
    }
  };

  return isAdmin() ? children : <Navigate to="/" replace />;
};

export default AdminRoute;
