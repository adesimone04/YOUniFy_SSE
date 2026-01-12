import React, { useState, useEffect, useRef } from 'react';
import { toast } from 'react-toastify';
import { motion } from 'framer-motion';
import { ShieldCheck, Timer, RotateCcw, Lock } from 'lucide-react';
import { useLocation, useNavigate } from 'react-router-dom';
import PageTransition from '../../components/ui/PageTransition/PageTransition'
import styles from '../LandingPage/LandingPage.module.css';

import authService from '../../services/authService';
import { handleAuthResponse } from '../../utils/authHandler';

const TwoFactorAuth = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const userEmail = location.state?.email;
    const userName = location.state?.name;

    // --- 1. UTILS ---

    const getDurationFromServerDate = (serverDateString) => {
        if (!serverDateString) return 300;
        const now = new Date();
        const end = new Date(serverDateString);
        if (isNaN(end.getTime())) return 300;

        const nowSec = now.getMinutes() * 60 + now.getSeconds();
        const endSec = end.getMinutes() * 60 + end.getSeconds();
        let diff = endSec - nowSec;

        if (diff < -3000) diff += 3600;
        if (diff < 0 || diff > 300) return 300;
        return diff;
    };

    const calculateInitialDeadline = () => {
        const saved = localStorage.getItem('2fa_deadline');
        if (saved) {
            const timestamp = parseInt(saved, 10);
            if (timestamp > Date.now()) return timestamp;
        }
        const serverExpiry = location.state?.expiryDate;
        const duration = getDurationFromServerDate(serverExpiry);
        const newDeadline = Date.now() + (duration * 1000);
        localStorage.setItem('2fa_deadline', newDeadline.toString());
        return newDeadline;
    };

    // --- 2. STATI ---

    const initialDeadline = useRef(calculateInitialDeadline()).current;
    const [deadline, setDeadline] = useState(initialDeadline);

    const [accountId, setAccountId] = useState(() => {
        return localStorage.getItem('2fa_accountId') || location.state?.accountId;
    });

    const [timeLeft, setTimeLeft] = useState(() => {
        const now = Date.now();
        const delta = Math.floor((initialDeadline - now) / 1000);
        return delta > 0 ? delta : 0;
    });

    // NOTA: Ho rimosso attemptsCount. Ci fidiamo del Backend ora.

    const [otp, setOtp] = useState(['', '', '', '', '', '']);
    const [isTimerExpired, setIsTimerExpired] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const inputRefs = useRef([]);

    // --- 3. TIMER ---
    useEffect(() => {
        if (timeLeft === 0) setIsTimerExpired(true);

        const intervalId = setInterval(() => {
            const now = Date.now();
            const delta = Math.floor((deadline - now) / 1000);

            if (delta <= 0) {
                setTimeLeft(0);
                setIsTimerExpired(true);
                clearInterval(intervalId);
            } else {
                setTimeLeft(delta);
                setIsTimerExpired(false);
            }
        }, 1000);

        return () => clearInterval(intervalId);
    }, [deadline]);

    useEffect(() => {
        if (accountId) localStorage.setItem('2fa_accountId', accountId);
    }, [accountId]);

    useEffect(() => {
        if (!userEmail && !accountId) navigate('/login');
    }, [userEmail, accountId, navigate]);

    const formatTime = (seconds) => {
        const m = Math.floor(seconds / 60);
        const s = seconds % 60;
        return `${m}:${s < 10 ? '0' : ''}${s}`;
    };

    // --- INPUTS ---
    const handleChange = (index, value) => {
        if (isNaN(value)) return;
        const newOtp = [...otp];
        newOtp[index] = value.substring(value.length - 1);
        setOtp(newOtp);
        if (value && index < 5 && inputRefs.current[index + 1]) inputRefs.current[index + 1].focus();
    };

    const handleKeyDown = (index, e) => {
        if (e.key === 'Backspace' && !otp[index] && index > 0) inputRefs.current[index - 1].focus();
    };

    const handlePaste = (e) => {
        e.preventDefault();
        const data = e.clipboardData.getData('text').slice(0, 6).split('');
        if (data.length === 6 && data.every(char => !isNaN(char))) {
            setOtp(data);
            inputRefs.current[5].focus();
        }
    };

    // --- 4. RESEND ---
    const triggerResend = async (automatic = false) => {
        setIsLoading(true);
        try {
            const response = await authService.resend2FACode(accountId);
            const responseData = response.data;
            const newData = responseData.data || responseData;

            if (!automatic) toast.success("Nuovo codice inviato!");

            if (newData.accountID || newData.accountId || newData.id) {
                setAccountId(newData.accountID || newData.accountId || newData.id);
            }

            const duration = getDurationFromServerDate(newData.expiryDate);
            const newDeadline = Date.now() + (duration * 1000);

            setDeadline(newDeadline);
            localStorage.setItem('2fa_deadline', newDeadline.toString());

            // RESET
            setTimeLeft(duration);
            setIsTimerExpired(false);
            setOtp(['', '', '', '', '', '']);

            setTimeout(() => {
                if (inputRefs.current[0]) inputRefs.current[0].focus();
            }, 100);

        } catch (error) {
            console.error("Errore Resend:", error);
            toast.error("Errore nell'invio del nuovo codice.");
        } finally {
            setIsLoading(false);
        }
    };

    // --- 5. VERIFICA ---
    const handleVerify = async (e) => {
        e.preventDefault();
        const code = otp.join('');
        if (code.length < 6) return;

        setIsLoading(true);

        try {
            const response = await authService.verify2FA(accountId, code);

            localStorage.removeItem('2fa_deadline');
            localStorage.removeItem('2fa_accountId');
            handleAuthResponse(response.data, navigate);

        } catch (error) {
            const errorData = error.response?.data;
            if (errorData) {
                const internalCode = handleAuthResponse(errorData, navigate);

                // --- LOGICA BASATA SOLO SUL BACKEND ---
                // Se il backend dice che hai finito i tentativi, blocchiamo.
                if (['MAX_ATTEMPTS_REACHED', 'MAX_ATTEMPTS', '2FA_CODE_EXPIRED', '2FA_EXPIRED'].includes(internalCode)) {

                    toast.warning("Tentativi esauriti. Richiedi un nuovo codice.");

                    // Kill Timer & Enable Button
                    setDeadline(Date.now());
                    localStorage.removeItem('2fa_deadline');
                    setTimeLeft(0);
                    setIsTimerExpired(true);
                    setOtp(['', '', '', '', '', '']);
                }
                else if (internalCode === 'INVALID_2FA_CODE' || internalCode === 'TRY_2FA_AGAIN') {
                    // Semplice errore codice
                    toast.error("Codice non valido. Riprova.");
                    setOtp(['', '', '', '', '', '']);
                    inputRefs.current[0].focus();
                }
            }
        } finally {
            setIsLoading(false);
        }
    };

    const areInputsDisabled = isTimerExpired || isLoading;

    return (
        <PageTransition className="relative h-screen w-full bg-slate-950 flex flex-col items-center justify-center font-sans overflow-hidden">
            <div className="fixed inset-0 z-0 pointer-events-none">
                <div className={`${styles.blob} ${styles.blue}`}></div>
                <div className={`${styles.blob} ${styles.indigo}`}></div>
                <div className={`${styles.blob} ${styles.magenta}`}></div>
                <div className={`${styles.blob} ${styles.crimson}`}></div>
                <div className={`${styles.blob} ${styles.purple}`}></div>
                <div className={`${styles.blob} ${styles.amber}`}></div>
            </div>

            <section className="relative z-10 w-full px-4 flex items-center justify-center h-full">
                <motion.div
                    initial={{ opacity: 0, scale: 0.9, y: 30 }}
                    animate={{ opacity: 1, scale: 1, y: 0 }}
                    transition={{ duration: 0.8, type: "spring", bounce: 0.4 }}
                    className="w-full max-w-lg"
                >
                    <div className="bg-slate-900/40 backdrop-blur-xl border border-white/10 rounded-[2rem] p-6 md:p-12 shadow-2xl relative overflow-hidden flex flex-col justify-center">
                        <div className="flex flex-col items-center text-center mb-6 md:mb-10">
                            <div className="w-14 h-14 md:w-20 md:h-20 rounded-2xl flex items-center justify-center mb-4 border border-white/5 bg-gradient-to-br from-younify-blue/20 to-indigo-500/20 text-younify-blue">
                                <ShieldCheck className="w-8 h-8 md:w-10 md:h-10" />
                            </div>
                            <h2 className="text-xl md:text-3xl font-black text-white font-inter tracking-tight mb-2">
                                {userName ? `Ciao ${userName}` : "Verifica Identità"}
                            </h2>
                            <p className="text-slate-400 text-xs md:text-lg">
                                Inserisci il codice inviato a <br/><span className="text-white font-medium">{userEmail || "la tua email"}</span>
                            </p>
                        </div>

                        <div className={`flex items-center justify-center gap-2 mb-6 p-2 md:p-3 rounded-xl border transition-all duration-300 ${isTimerExpired ? 'bg-rose-500/10 border-rose-500/20 text-rose-400' : 'bg-white/5 border-white/10 text-slate-300'}`}>
                            {isTimerExpired ? <Lock size={16} /> : <Timer size={16} />}
                            <span className="font-mono text-lg md:text-xl font-bold tracking-widest">
                                {isTimerExpired ? "Scaduto" : formatTime(timeLeft)}
                            </span>
                        </div>

                        <form onSubmit={handleVerify} className="flex flex-col gap-5 md:gap-8">
                            <div className="flex justify-between gap-1 md:gap-3" onPaste={!areInputsDisabled ? handlePaste : undefined}>
                                {otp.map((digit, index) => (
                                    <input
                                        key={index}
                                        ref={el => inputRefs.current[index] = el}
                                        type="tel"
                                        maxLength={1}
                                        disabled={areInputsDisabled}
                                        value={digit}
                                        onChange={(e) => handleChange(index, e.target.value)}
                                        onKeyDown={(e) => handleKeyDown(index, e)}
                                        className={`w-full h-10 md:h-16 bg-slate-950/50 border rounded-lg md:rounded-xl text-center text-xl md:text-3xl text-white font-bold transition-all p-0 ${areInputsDisabled ? 'border-white/5 opacity-30 cursor-not-allowed text-slate-500' : 'border-white/10 focus:border-younify-blue focus:ring-2 md:focus:ring-4 focus:ring-younify-blue/20 focus:outline-none'}`}
                                    />
                                ))}
                            </div>

                            <button type="submit" disabled={areInputsDisabled || otp.some(d => d === '')} className={`w-full py-3 md:py-4 rounded-xl font-bold text-white text-base md:text-lg flex items-center justify-center gap-2 transition-all transform ${areInputsDisabled || otp.some(d => d === '') ? 'bg-slate-800 cursor-not-allowed opacity-50' : 'bg-younify-blue hover:bg-blue-600 shadow-lg active:scale-95'}`}>
                                {isLoading ? "Attendi..." : "Verifica"}
                            </button>
                        </form>

                        <div className="mt-4 md:mt-8 pt-4 md:pt-6 border-t border-white/5 text-center">
                            <button onClick={() => triggerResend(false)} disabled={!isTimerExpired || isLoading} className={`flex items-center justify-center gap-2 mx-auto text-xs md:text-sm font-semibold transition-all w-full md:w-auto ${isTimerExpired ? 'text-white cursor-pointer px-4 py-3 bg-younify-blue rounded-xl shadow-lg hover:bg-blue-600 animate-pulse' : 'text-slate-500 cursor-not-allowed'}`}>
                                <RotateCcw size={16} />
                                {isTimerExpired ? "Invia nuovo codice" : `Attendi scadenza (${formatTime(timeLeft)})`}
                            </button>
                        </div>
                    </div>
                </motion.div>
            </section>
        </PageTransition>
    );
};

export default TwoFactorAuth;