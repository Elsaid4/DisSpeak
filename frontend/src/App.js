import React, { useState, useEffect } from 'react';
import Login from './pages/Login';

export default function App() {
  const [user, setUser] = useState(null);

  // Al caricamento iniziale dell'app, controlla se l'utente era già loggato
  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
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

  // Se l'utente NON è loggato, blocca la navigazione e mostra la Login/Registration
  if (!user) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  // Se l'utente si è autenticato con successo, mostra la Chat/Dashboard principale
  return (
      <div style={{ backgroundColor: '#36393f', height: '100vh', color: 'white', padding: '20px', fontFamily: 'sans-serif' }}>
        <h1>DisSpeak Dashboard</h1>
        <div style={{ padding: '15px', backgroundColor: '#202225', borderRadius: '5px', display: 'inline-block' }}>
          <p style={{ margin: 0 }}>🟢 Collegato come: <strong>{user.fullName}</strong> (@{user.username})</p>
        </div>

        <div style={{ marginTop: '30px', padding: '20px', backgroundColor: '#2f3136', borderRadius: '8px' }}>
          <h3>🚀 Autenticazione Completa!</h3>
          <p>Ora l'istanza Axios ha memorizzato l'utente. Il database PostgreSQL ha registrato la sessione.</p>
        </div>

        <button onClick={handleLogout} style={{ marginTop: '20px', padding: '10px 20px', backgroundColor: '#f04747', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
          Disconnetti (Logout)
        </button>
      </div>
  );
}