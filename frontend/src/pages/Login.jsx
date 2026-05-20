import React, { useState } from 'react';
import { authApi } from '../services/api'; // <--- Usiamo Axios del tuo compagno

export default function Login({ onLoginSuccess }) {
    // true = Schermata Login, false = Schermata Registrazione
    const [isLoginMode, setIsLoginMode] = useState(true);

    // Stati per i campi del form
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [fullName, setFullName] = useState('');
    const [email, setEmail] = useState('');

    // Stati per la gestione dell'interfaccia
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            let response;

            if (isLoginMode) {
                // 1. FLUSSO LOGIN: invia solo username e password a /api/auth/login
                response = await authApi.login({ username, password });
            } else {
                // 2. FLUSSO REGISTRAZIONE: invia tutti i dati a /api/auth/register
                response = await authApi.register({ username, password, fullName, email });
            }

            const userLogged = response.data;

            if (userLogged) {
                console.log("Autenticazione riuscita:", userLogged);

                // Salviamo l'utente nel browser per usarlo nelle altre schermate
                localStorage.setItem('user', JSON.stringify(userLogged));

                // Se il backend restituisce un token JWT, lo salviamo per gli interceptor di api.js
                if (userLogged.token) {
                    localStorage.setItem('token', userLogged.token);
                }

                // Avvisiamo App.jsx che l'utente è dentro!
                onLoginSuccess(userLogged);
            } else {
                setError(isLoginMode ? 'Account non trovato o credenziali errate.' : 'Impossibile registrare l\'utente.');
            }

        } catch (err) {
            console.error("Errore di autenticazione:", err);
            // Se il server risponde con un errore (es. 400 Bad Request o 401 Unauthorized)
            if (err.response && err.response.data) {
                setError(isLoginMode ? 'Username o password errati.' : 'Dati di registrazione non validi (es. username già preso).');
            } else {
                setError('Impossibile raggiungere il server backend.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={styles.title}>DisSpeak</h2>
                <p style={styles.subtitle}>
                    {isLoginMode ? 'Bentornato! Accedi per chattare' : 'Crea un account in pochi secondi'}
                </p>

                {error && <div style={styles.error}>{error}</div>}

                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Username</label>
                        <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required style={styles.input} />
                    </div>

                    {/* Mostra Nome Completo ed Email SOLO in modalità Registrazione */}
                    {!isLoginMode && (
                        <>
                            <div style={styles.inputGroup}>
                                <label style={styles.label}>Nome Completo</label>
                                <input type="text" value={fullName} onChange={(e) => setFullName(e.target.value)} required style={styles.input} />
                            </div>

                            <div style={styles.inputGroup}>
                                <label style={styles.label}>Email</label>
                                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required style={styles.input} />
                            </div>
                        </>
                    )}

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Password</label>
                        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required style={styles.input} />
                    </div>

                    <button type="submit" disabled={loading} style={styles.button}>
                        {loading ? 'Attendi...' : (isLoginMode ? 'Accedi' : 'Registrati')}
                    </button>
                </form>

                <p style={styles.toggleText}>
                    {isLoginMode ? "Nuovo su DisSpeak? " : "Hai già un account? "}
                    <span
                        style={styles.toggleLink}
                        onClick={() => {
                            setIsLoginMode(!isLoginMode);
                            setError(''); // Svuota gli errori nel cambio schermata
                        }}
                    >
                        {isLoginMode ? 'Registrati qui' : 'Accedi qui'}
                    </span>
                </p>
            </div>
        </div>
    );
}

// Design scuro personalizzato stile Discord
const styles = {
    container: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', backgroundColor: '#2f3136' },
    card: { backgroundColor: '#36393f', padding: '40px', borderRadius: '8px', boxShadow: '0 4px 10px rgba(0,0,0,0.3)', width: '400px', color: '#fff', fontFamily: 'sans-serif' },
    title: { textAlign: 'center', margin: '0 0 5px 0', fontSize: '28px', fontWeight: 'bold' },
    subtitle: { textAlign: 'center', color: '#b9bbbe', marginBottom: '20px', fontSize: '14px' },
    form: { display: 'flex', flexDirection: 'column' },
    inputGroup: { display: 'flex', flexDirection: 'column', marginBottom: '15px' },
    label: { fontSize: '12px', color: '#b9bbbe', textTransform: 'uppercase', fontWeight: 'bold', marginBottom: '5px' },
    input: { padding: '10px', borderRadius: '3px', border: '1px solid #202225', backgroundColor: '#202225', color: '#fff', fontSize: '16px', outline: 'none' },
    button: { padding: '12px', borderRadius: '3px', border: 'none', backgroundColor: '#5865F2', color: '#fff', fontWeight: 'bold', fontSize: '16px', cursor: 'pointer', marginTop: '10px' },
    error: { backgroundColor: '#f04747', padding: '10px', borderRadius: '4px', color: '#fff', marginBottom: '15px', textAlign: 'center', fontSize: '14px' },
    toggleText: { textAlign: 'center', color: '#b9bbbe', marginTop: '20px', fontSize: '14px' },
    toggleLink: { color: '#00aff4', cursor: 'pointer', textDecoration: 'underline' }
};