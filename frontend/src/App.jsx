import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'; //Import del gestore delle rotte

//Import delle pagine
import LandingPage from './pages/LandingPage/LandingPage';
import AuthPage from './pages/Auth/AuthPage';
import LoginPage from './pages/Auth/LoginPage';
// Import della nuova pagina 2FA
import TwoFactorAuth from './pages/TwoFactorAuth/TwoFactorAuth';

function App() {
    return (
        <div className="App">
            <Router>
                <Routes>
                    <Route path="/" element={<LandingPage />} /> {/*Se l'indirizzo è solo "/", mostra LandingPage */}
                    <Route path="/auth" element={<AuthPage />} /> {/*Se l'indirizzo è "/auth", mostra AuthPage */}
                    <Route path="/login" element={<LoginPage />} /> {/*Se l'indirizzo è "/login", mostra LoginPage */}

                    {/* Nuova rotta per la 2FA */}
                    <Route path="/2fa" element={<TwoFactorAuth />} />
                </Routes>
            </Router>
        </div>
    );
}

export default App;