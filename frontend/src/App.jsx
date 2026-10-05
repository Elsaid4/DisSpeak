import React, { useState, useEffect } from 'react';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';

export default function App() {
    const [user, setUser] = useState(null);

    // Al caricamento iniziale dell'app, controlla se l'utente era già loggato
    useEffect(() => {
        const savedUser = localStorage.getItem('user');
        if (savedUser) {
            try {
                setUser(JSON.parse(savedUser));
            } catch (e) {
                localStorage.removeItem('user');
            }
        }
    }, []);

    const handleLoginSuccess = (loggedInUser) => {
        setUser(loggedInUser);
    };

    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        setUser(null);
    };

    // Se l'utente NON è loggato, mostra la pagina di Login
    if (!user) {
        return <Login onLoginSuccess={handleLoginSuccess} />;
    }

    // Se l'utente è autenticato, mostra la Dashboard
    return <Dashboard user={user} onLogout={handleLogout} />;
}