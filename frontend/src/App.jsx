import React from 'react';
import { BrowserRouter, Routes, Route, useLocation, Navigate, Outlet } from 'react-router-dom';
import { AnimatePresence } from 'framer-motion';

import { ToastContainer, cssTransition } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import AuthPage from './pages/Auth/AuthPage';
import LoginPage from './pages/Auth/LoginPage';
import LandingPage from './pages/LandingPage/LandingPage';
import TwoFactorAuth from './pages/TwoFactorAuth/TwoFactorAuth';

const PublicRoute = () => {

    const isLogged = localStorage.getItem('user_is_logged_in') === 'true';
    if (isLogged) { return <Navigate to="/" replace />; }

    return <Outlet />;
};


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
            <ToastContainer position="top-center" theme="dark" transition={SoftFade} autoClose={5000} limit={3} />
            <AnimatedRoutes />
        </BrowserRouter>
    );
}

export default App;