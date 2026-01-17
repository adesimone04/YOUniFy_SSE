import React from 'react';
import { BrowserRouter, Routes, Route, useLocation, Navigate, Outlet } from 'react-router-dom';
import { AnimatePresence } from 'framer-motion';

// Gestione Notifiche (Toastify)
import { ToastContainer, cssTransition } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Import Pagine Auth & Landing
import AuthPage from './pages/Auth/AuthPage';
import LoginPage from './pages/Auth/LoginPage';
import LandingPage from './pages/LandingPage/LandingPage';
import TwoFactorAuth from './pages/TwoFactorAuth/TwoFactorAuth';

// Import Pagine App (Private/Interne)
import ThreadPage from './pages/Thread/ThreadPage';
import ThreadDetail from './pages/Thread/ThreadDetail';
import EventsPage from './pages/Event/EventsPage';

// --- COMPONENTI DI UTILITÀ ---

// 1. Placeholder per le pagine "Coming Soon"
const PlaceholderPage = ({ title }) => (
    <div className="min-h-screen bg-slate-950 text-white flex items-center justify-center">
        <h1 className="text-3xl font-bold">{title} Coming Soon</h1>
    </div>
);

// 2. Logica Rotta Pubblica:
// Se l'utente è già loggato, non deve vedere Login/Auth, ma viene reindirizzato all'App (es. /threads)
const PublicRoute = () => {
    const isLogged = localStorage.getItem('user_is_logged_in') === 'true';
    // Se è loggato, lo mandiamo ai threads invece che alla Landing Page
    if (isLogged) { return <Navigate to="/threads" replace />; }

    return <Outlet />;
};

// 3. Transizione custom per i Toast
const SoftFade = cssTransition({
    enter: "soft-enter",
    exit: "soft-exit"
});

// --- GESTIONE ROTTE CON ANIMAZIONI ---
const AnimatedRoutes = () => {
    const location = useLocation();

    return (
        <AnimatePresence mode="wait">
            <Routes location={location} key={location.pathname}>

                {/* --- HOME PAGE (Landing) --- */}
                <Route path="/" element={<LandingPage />} />

                {/* --- ROTTE PUBBLICHE (Accessibili solo se NON sei loggato) --- */}
                <Route element={<PublicRoute />}>
                    <Route path="/auth" element={<AuthPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/2fa" element={<TwoFactorAuth />} />
                </Route>

                {/* --- ROTTE APP (Interne) --- */}
                {/* Qui vanno tutte le pagine dell'applicazione vera e propria */}

                {/* Sezione Threads */}
                <Route path="/threads" element={<ThreadPage />} />
                <Route path="/threads/:id" element={<ThreadDetail />} />

                {/* Sezioni Placeholder */}
                <Route path="/events" element={<EventsPage />} />
                <Route path="/match" element={<PlaceholderPage title="Match" />} />
                <Route path="/profile" element={<PlaceholderPage title="Profile" />} />
                <Route path="/notifications" element={<PlaceholderPage title="Notifiche" />} />

            </Routes>
        </AnimatePresence>
    );
};

// --- APP PRINCIPALE ---
function App() {
    return (
        <BrowserRouter>
            {/* Contenitore Notifiche */}
            <ToastContainer
                position="top-center"
                theme="dark"
                transition={SoftFade}
                autoClose={5000}
                limit={3}
            />

            {/* Routing Animato */}
            <AnimatedRoutes />
        </BrowserRouter>
    );
}

export default App;