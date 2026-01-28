import React from 'react';
import { BrowserRouter, Routes, Route, useLocation, Navigate, Outlet } from 'react-router-dom';
import { AnimatePresence } from 'framer-motion';

import { ToastContainer, cssTransition } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import AuthPage from './pages/Auth/AuthPage';
import LoginPage from './pages/Auth/LoginPage';
import LandingPage from './pages/LandingPage/LandingPage';
import TwoFactorAuth from './pages/TwoFactorAuth/TwoFactorAuth';
import EventPage from './pages/Event/EventsPage';
import MatchingPage from './pages/Matching/MatchingPage';

// --- IMPORTANTE: Importa gli stili dei blob qui ---
import styles from './pages/LandingPage/LandingPage.module.css';

// ----------------------------------------------------------------------
// 1. COMPONENTE SFONDO INTELLIGENTE
// ----------------------------------------------------------------------
const GlobalBackground = () => {
    const location = useLocation();

    // LISTA NERA: Aggiungi qui le rotte che NON devono avere i blob globali.
    // Esempio: const hiddenPaths = ['/admin', '/dashboard-nera', '/settings'];
    const hiddenPaths = [];

    // Se la pagina attuale è nella lista, non renderizza nulla (sfondo trasparente/assente)
    // permettendo alla pagina specifica di definire il proprio sfondo.
    if (hiddenPaths.includes(location.pathname)) {
        return null;
    }

    return (
        <div className="fixed inset-0 z-0 pointer-events-none overflow-hidden bg-slate-950">
            <div className={`${styles.blob} ${styles.blue}`}></div>
            <div className={`${styles.blob} ${styles.indigo}`}></div>
            <div className={`${styles.blob} ${styles.magenta}`}></div>
            <div className={`${styles.blob} ${styles.crimson}`}></div>
            <div className={`${styles.blob} ${styles.purple}`}></div>
            <div className={`${styles.blob} ${styles.amber}`}></div>
        </div>
    );
};

// ----------------------------------------------------------------------
// 2. ROTTE E PROTEZIONE
// ----------------------------------------------------------------------
const PublicRoute = () => {

    const isLogged = localStorage.getItem('user_is_logged_in') === 'true';
    if (isLogged) { return <Navigate to="/" replace />; }

    return <Outlet />;
};

const ProtectedRoute = () => {
    const isLogged = localStorage.getItem('user_is_logged_in') === 'true';
    if (!isLogged) { return <Navigate to="/login" replace />; }
    return <Outlet />;

}

const AnimatedRoutes = () => {
    const location = useLocation();

    return (
        <AnimatePresence mode="wait">
            <Routes location={location} key={location.pathname}>

                <Route path="/" element={<LandingPage />} />

                <Route element={<PublicRoute />}>
                    <Route path="/auth" element={<AuthPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/2fa" element={<TwoFactorAuth />} />
                </Route>
                <Route element={<ProtectedRoute />}>
                    <Route path="/events" element={<EventPage />} />
                    <Route path="/match" element={<MatchingPage />} />
                </Route>
            </Routes>
        </AnimatePresence>
    );
};

const SoftFade = cssTransition({
    enter: "soft-enter", // Deve corrispondere al nome della classe CSS in index.css
    exit: "soft-exit"
});

function App() {
    return (
        <BrowserRouter>
            {/* Inseriamo qui il componente sfondo che reagisce al cambio pagina */}
            <GlobalBackground />

            <ToastContainer position="top-center" theme="dark" transition={SoftFade} autoClose={5000} limit={3} />

            {/* Le pagine verranno renderizzate sopra lo sfondo */}
            <AnimatedRoutes />
        </BrowserRouter>
    );
}

export default App;