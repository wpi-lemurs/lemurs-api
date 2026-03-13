import React, { useMemo } from 'react';

import { PageLayout } from './components/PageLayout';

import './App.css';
import { TokenContext } from './components/token/TokenContext';
import useToken from './components/token/useToken';
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import AdminPanel from './pages/AdminPanel';
import HomePage from './pages/HomePage';
import Dashboard from './pages/Dashboard';
import AdminRoute from './components/AdminRoute';

export default function App() {
    const {token, setToken} = useToken()
    const tokenProvider = useMemo(() => ({token: token, setToken: setToken}), [token, setToken])

    return (
        <TokenContext.Provider value={tokenProvider}>
            <BrowserRouter basename="/web">
                <PageLayout setToken={setToken}>
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route
                          path="/admin"
                          element={
                            <AdminRoute>
                              <AdminPanel />
                            </AdminRoute>
                          }
                        />
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </PageLayout>
            </BrowserRouter>
        </TokenContext.Provider>
    );
}