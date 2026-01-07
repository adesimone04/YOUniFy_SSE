import React, { useState, useEffect } from 'react';
import {
    Mail, Lock, Eye, EyeOff, AlertCircle, LogIn
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';


// --- IMPORT STILI ---
import styles from '../LandingPage/LandingPage.module.css';

// --- COMPONENTI DI SUPPORTO ---

const ErrorMessage = ({ message }) => (
    <AnimatePresence>
        {message && (
            <motion.div
                initial={{ opacity: 0, y: -5, height: 0 }}
                animate={{ opacity: 1, y: 0, height: 'auto' }}
                exit={{ opacity: 0, y: -5, height: 0 }}
                className="overflow-hidden" // RIMOSSO: absolute -bottom-5 left-0 w-full
            >
                <div className="flex items-center gap-1 text-red-500 text-[11px] ml-1 mt-1.5 font-medium">
                    <AlertCircle size={12} /> <span>{message}</span>
                </div>
            </motion.div>
        )}
    </AnimatePresence>
);

const InputGroup = ({ icon, placeholder, type, name, onChange, errorMessage, disabled, className = "" }) => {
    const [showPassword, setShowPassword] = useState(false);
    const inputType = type === 'password' ? (showPassword ? 'text' : 'password') : type;

    // Cambiato mb-5 in mb-3 per bilanciare lo spazio quando esce l'errore
    return (
        <div className={`mb-3 relative ${className}`}>
            <div className={`relative group ${disabled ? 'opacity-50 cursor-not-allowed' : ''}`}>
                <div className={`absolute left-3 top-1/2 -translate-y-1/2 transition-colors ${errorMessage ? 'text-red-500' : 'text-slate-400 group-focus-within:text-younify-blue'}`}>
                    {icon}
                </div>
                <input
                    type={inputType}
                    name={name}
                    placeholder={placeholder}
                    onChange={onChange}
                    disabled={disabled}
                    className={`w-full bg-slate-900/80 border rounded-xl py-3 pl-10 ${type === 'password' ? 'pr-12' : 'pr-4'} text-white placeholder-slate-500 focus:outline-none focus:ring-1 transition-all text-sm 
                    ${errorMessage
                        ? 'border-red-500 focus:border-red-500 focus:ring-red-500'
                        : 'border-slate-700 focus:border-younify-blue focus:ring-younify-blue'
                    }`}
                />

                {type === 'password' && !disabled && (
                    <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-white transition-colors focus:outline-none"
                    >
                        {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                )}
            </div>
            {/* L'errore è qui sotto, relativo, e farà scorrere il contenuto */}
            <ErrorMessage message={errorMessage} />
        </div>
    );
};

const LoginPage = () => {
    const [loginData, setLoginData] = useState({ email: '', password: '' });
    const [errors, setErrors] = useState({ email: false, password: false });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setLoginData(prev => ({ ...prev, [name]: value }));
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: false }));
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const newErrors = {};
        if (!loginData.email) newErrors.email = "Inserisci l'email";
        if (!loginData.password) newErrors.password = "Inserisci la password";

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }
        console.log("Login:", loginData);
        alert("Login simulato! Invio al backend...");
    };

    return (
        <div className="h-dvh md:min-h-screen w-full bg-slate-950 flex flex-col font-sans overflow-hidden relative">

            {/* --- BACKGROUND LAYER --- */}
            <div className="fixed inset-0 z-0 pointer-events-none">
                <div className={`${styles.blob} ${styles.blue}`}></div>
                <div className={`${styles.blob} ${styles.indigo}`}></div>
                <div className={`${styles.blob} ${styles.rose}`}></div>
                <div className={`${styles.blob} ${styles.teal}`}></div>
                <div className={`${styles.blob} ${styles.purple}`}></div>
                <div className={`${styles.blob} ${styles.cyan}`}></div>
            </div>

            {/* Container principale centrato */}
            <div className="flex-1 flex items-center justify-center px-0 md:px-4 relative z-10 py-0 md:py-6">

                {/* --- CARD UNIFICATA (Stile AuthPage) ---
                    Questa applica lo sfondo scuro e il blur sia a mobile che desktop
                */}
                <div className="relative bg-slate-900/60 border border-white/10 w-full max-w-[1050px] h-full md:h-[600px] rounded-none md:rounded-[30px] shadow-2xl flex flex-col md:flex-row overflow-hidden backdrop-blur-md">

                    {/* =======================================
                        1. LAYOUT DESKTOP
                       ======================================= */}

                    {/* LATO SINISTRO (Testo Introduttivo) */}
                    <div className="hidden md:flex w-1/2 p-12 flex-col justify-center relative z-20">
                        <div className="mb-6">
                            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/5 border border-white/10 mb-6">
                                <span className="text-[20px] font-bold">
                                    <span className="text-white">YOUni</span>
                                    <span className="text-younify-blue">Fy</span>
                                </span>
                            </div>
                            <h1 className="text-5xl font-black text-white font-inter tracking-tight leading-[1.1] mb-6">
                                La tua vita universitaria<br/>
                                <span className="text-transparent bg-clip-text bg-gradient-to-r from-younify-blue to-purple-400">
                                    in una tasca.
                                </span>
                            </h1>
                            <p className="text-slate-400 text-base leading-relaxed max-w-md">
                                <span className="block mb-0">Accedi a YOUniFy.</span>
                                Scopri e promuovi eventi, crea nuove connessioni e cogli le migliori opportunità del mondo universitario.</p>
                        </div>

                        <div className="mt-auto">
                            <p className="text-slate-500 text-sm [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">
                                Non hai ancora un account?{' '}
                                <a href="/register" className="text-white font-bold hover:underline decoration-younify-blue underline-offset-4 [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">
                                    Registrati qui
                                </a>
                            </p>
                        </div>
                    </div>

                    {/* LATO DESTRO (Mockup Telefono Desktop) */}
                    <div className="hidden md:flex w-1/2 h-full flex-col justify-center items-center relative z-30">
                        <div className="
                            w-[320px] h-[580px]
                            bg-black
                            border-[3px] border-slate-800
                            rounded-[44px] shadow-2xl
                            flex flex-col overflow-hidden relative
                            absolute
                            md:bottom-[-50px]
                            right-0
                            rotate-[-2deg] hover:rotate-0
                            transition-transform duration-500 ease-out
                        ">
                            <div className="flex-1 bg-slate-950/80 md:bg-slate-950 p-6 flex flex-col justify-center relative">
                                {/* Dynamic Island Desktop */}
                                <div className="absolute top-3 left-1/2 -translate-x-1/2 w-[96px] h-[26px] bg-black rounded-full z-50 justify-between items-center px-3 shadow-sm flex">
                                    <div className="w-2 h-2 rounded-full bg-[#1a1a1a]"></div>
                                    <div className="w-4 h-4 rounded-full bg-[#1a1a1a]"></div>
                                </div>
                                {/* Decorazione sfumata */}
                                <div className="absolute top-0 left-0 w-full h-40 bg-gradient-to-b from-younify-blue/20 to-transparent pointer-events-none opacity-50 z-0"></div>

                                <div className="relative z-10 mt-6 md:mt-0">
                                    <div className="text-center mb-8">
                                        <div className="w-12 h-12 bg-gradient-to-tr from-younify-blue to-purple-600 rounded-xl mx-auto mb-4 flex items-center justify-center shadow-lg shadow-purple-500/20">
                                            <LogIn className="text-white w-6 h-6" />
                                        </div>
                                        <h2 className="text-2xl font-bold text-white font-inter [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">Bentornato</h2>
                                        <p className="text-slate-400 text-xs mt-1">Inserisci le credenziali per entrare</p>
                                    </div>

                                    <form onSubmit={handleSubmit}>
                                        <InputGroup icon={<Mail size={18} />} placeholder="Email" type="email" name="email" onChange={handleChange} errorMessage={errors.email} />
                                        <InputGroup icon={<Lock size={18} />} placeholder="Password" type="password" name="password" onChange={handleChange} errorMessage={errors.password} />
                                        <button type="submit" className="w-full mt-6 py-3 rounded-xl bg-younify-blue text-white font-bold text-sm shadow-lg shadow-younify-blue/25 hover:shadow-younify-blue/40 transition-all hover:scale-[1.02] active:scale-95">
                                            Accedi
                                        </button>
                                    </form>
                                </div>
                                {/* Home Bar Desktop */}
                                <div className="absolute bottom-2 left-1/2 -translate-x-1/2 w-[35%] h-[4px] bg-slate-100/30 rounded-full block z-50"></div>
                            </div>
                        </div>
                    </div>


                    {/* =======================================
                        2. LAYOUT MOBILE (PULITO - SENZA MOCKUP)
                       ======================================= */}
                    <div className="md:hidden w-full h-full relative z-20 overflow-y-auto px-6 py-8 scrollbar-hide">

                        {/* Wrapper centrato verticalmente */}
                        <div className="min-h-full flex flex-col justify-center">

                            <div className="text-center mb-10">
                                <div className="w-16 h-16 bg-gradient-to-tr from-younify-blue to-purple-600 rounded-2xl mx-auto mb-6 flex items-center justify-center shadow-lg shadow-purple-500/20">
                                    <LogIn className="text-white w-8 h-8" />
                                </div>
                                <h2 className="text-3xl font-black text-white font-inter tracking-tight [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">Bentornato</h2>
                                <p className="text-slate-400 text-sm mt-2">Inserisci le credenziali per entrare</p>
                            </div>

                            <form onSubmit={handleSubmit}>
                                <InputGroup icon={<Mail size={20} />} placeholder="Email" type="email" name="email" onChange={handleChange} errorMessage={errors.email} />
                                <InputGroup icon={<Lock size={20} />} placeholder="Password" type="password" name="password" onChange={handleChange} errorMessage={errors.password} />

                                <button type="submit" className="w-full mt-6 py-3.5 rounded-xl bg-younify-blue text-white font-bold text-sm shadow-lg shadow-younify-blue/25 active:scale-95 transition-transform">
                                    Accedi
                                </button>
                            </form>

                            <div className="mt-12 text-center">
                                <p className="text-slate-500 text-xs">
                                    Non hai un account? <br/>
                                    <a href="/register" className="text-younify-blue font-bold hover:underline ml-1">Registrati qui</a>
                                </p>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default LoginPage;