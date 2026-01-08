import React, { useState, useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import { ShieldCheck, Timer, RotateCcw, Lock } from 'lucide-react';
import { useLocation } from 'react-router-dom';
import styles from '../LandingPage/LandingPage.module.css';

const TwoFactorAuth = () => {
    // --- RECUPERO DATI DAL LOGIN ---
    const location = useLocation();
    const userName = location.state?.name || "Utente";
    const userEmail = location.state?.email || "tua email";

    const [otp, setOtp] = useState(['', '', '', '', '', '']);
    const [timeLeft, setTimeLeft] = useState(300);
    const [isTimerExpired, setIsTimerExpired] = useState(false);

    const inputRefs = useRef([]);

    useEffect(() => {
        if (timeLeft <= 0) {
            setIsTimerExpired(true);
            return;
        }
        const timerId = setInterval(() => {
            setTimeLeft((prevTime) => prevTime - 1);
        }, 1000);
        return () => clearInterval(timerId);
    }, [timeLeft]);

    const formatTime = (seconds) => {
        const m = Math.floor(seconds / 60);
        const s = seconds % 60;
        return `${m}:${s < 10 ? '0' : ''}${s}`;
    };

    const handleChange = (index, value) => {
        if (isNaN(value)) return;
        const newOtp = [...otp];
        newOtp[index] = value.substring(value.length - 1);
        setOtp(newOtp);
        if (value && index < 5 && inputRefs.current[index + 1]) {
            inputRefs.current[index + 1].focus();
        }
    };

    const handleKeyDown = (index, e) => {
        if (e.key === 'Backspace' && !otp[index] && index > 0 && inputRefs.current[index - 1]) {
            inputRefs.current[index - 1].focus();
        }
    };

    const handlePaste = (e) => {
        e.preventDefault();
        const data = e.clipboardData.getData('text').slice(0, 6).split('');
        if (data.length === 6 && data.every(char => !isNaN(char))) {
            setOtp(data);
            inputRefs.current[5].focus();
        }
    };

    const handleVerify = (e) => {
        e.preventDefault();
        const code = otp.join('');
        console.log(`Verifica codice: ${code} per ${userEmail}`);
        alert(`Codice inserito: ${code}`);
    };

    const handleResend = () => {
        setOtp(['', '', '', '', '', '']);
        setTimeLeft(300);
        setIsTimerExpired(false);
        if(inputRefs.current[0]) inputRefs.current[0].focus();
    };

    return (
        // CAMBIAMENTO 1: h-screen e overflow-hidden bloccano lo scroll
        <div className="relative h-screen w-full bg-slate-950 flex flex-col items-center justify-center font-sans overflow-hidden">

            <div className="fixed inset-0 z-0 pointer-events-none">
                <div className={`${styles.blob} ${styles.blue}`}></div>
                <div className={`${styles.blob} ${styles.indigo}`}></div>
                <div className={`${styles.blob} ${styles.purple}`}></div>
                <div className={`${styles.blob} ${styles.teal}`}></div>
            </div>

            <section className="relative z-10 w-full px-4 flex items-center justify-center h-full">

                <motion.div
                    initial={{ opacity: 0, scale: 0.9, y: 30 }}
                    animate={{ opacity: 1, scale: 1, y: 0 }}
                    transition={{ duration: 0.8, type: "spring", bounce: 0.4 }}
                    className="w-full max-w-lg"
                >
                    {/* CAMBIAMENTO 2: Padding ridotto su mobile (p-6) vs desktop (md:p-12) */}
                    <div className="bg-slate-900/40 backdrop-blur-xl border border-white/10 rounded-[2rem] p-6 md:p-12 shadow-2xl shadow-younify-blue/10 relative overflow-hidden flex flex-col justify-center">

                        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-3/4 h-[1px] bg-gradient-to-r from-transparent via-younify-blue to-transparent opacity-50"></div>

                        {/* --- HEADER COMPATTO --- */}
                        {/* Margini ridotti: mb-4 su mobile, mb-10 su desktop */}
                        <div className="flex flex-col items-center text-center mb-6 md:mb-10">
                            {/* Icona più piccola su mobile (w-14) */}
                            <div className="w-14 h-14 md:w-20 md:h-20 bg-gradient-to-br from-younify-blue/20 to-indigo-500/20 rounded-2xl flex items-center justify-center text-younify-blue mb-4 md:mb-6 border border-white/5 shadow-[0_0_30px_rgba(29,78,216,0.2)]">
                                <ShieldCheck className="w-8 h-8 md:w-10 md:h-10" />
                            </div>

                            {/* Testi più piccoli su mobile */}
                            <h2 className="text-2xl md:text-4xl font-black text-white font-inter tracking-tight mb-2">
                                Ciao {userName}
                            </h2>

                            <p className="text-slate-400 text-xs md:text-lg leading-relaxed max-w-[250px] md:max-w-none">
                                abbiamo inviato il codice all <span className="text-white font-medium block md:inline">{userEmail}</span>
                            </p>
                        </div>

                        {/* Timer più piccolo */}
                        <div className={`flex items-center justify-center gap-2 mb-6 p-2 md:p-3 rounded-xl border transition-all duration-300 ${
                            isTimerExpired
                                ? 'bg-rose-500/10 border-rose-500/20 text-rose-400'
                                : 'bg-white/5 border-white/10 text-slate-300'
                        }`}>
                            {isTimerExpired ? <Lock size={16} /> : <Timer size={16} />}
                            <span className="font-mono text-lg md:text-xl font-bold tracking-widest">
                                {isTimerExpired ? "Scaduto" : formatTime(timeLeft)}
                            </span>
                        </div>

                        {/* Form compatto */}
                        <form onSubmit={handleVerify} className="flex flex-col gap-5 md:gap-8">

                            <div className="flex justify-between gap-1 md:gap-3" onPaste={handlePaste}>
                                {otp.map((digit, index) => (
                                    <input
                                        key={index}
                                        ref={el => inputRefs.current[index] = el}
                                        type="tel" /* Tasterino numerico su mobile */
                                        maxLength={1}
                                        disabled={isTimerExpired}
                                        value={digit}
                                        onChange={(e) => handleChange(index, e.target.value)}
                                        onKeyDown={(e) => handleKeyDown(index, e)}
                                        // INPUT PICCOLI: h-10 su mobile, h-16 su desktop. Text-xl su mobile.
                                        className={`w-full h-10 md:h-16 bg-slate-950/50 border rounded-lg md:rounded-xl text-center text-xl md:text-3xl text-white font-bold transition-all placeholder-transparent p-0
                                            ${isTimerExpired
                                            ? 'border-white/5 opacity-50 cursor-not-allowed text-slate-500'
                                            : 'border-white/10 focus:border-younify-blue focus:ring-2 md:focus:ring-4 focus:ring-younify-blue/20 focus:outline-none hover:border-white/20'
                                        }`}
                                    />
                                ))}
                            </div>

                            <button
                                type="submit"
                                disabled={isTimerExpired || otp.some(d => d === '')}
                                className={`w-full py-3 md:py-4 rounded-xl font-bold text-white text-base md:text-lg flex items-center justify-center gap-2 transition-all transform
                                    ${isTimerExpired || otp.some(d => d === '')
                                    ? 'bg-slate-800 cursor-not-allowed opacity-50 grayscale'
                                    : 'bg-younify-blue hover:bg-blue-600 shadow-[0_4px_20px_rgba(29,78,216,0.4)] active:scale-95'
                                }`}
                            >
                                {isTimerExpired ? 'Codice Scaduto' : 'Verifica'}
                            </button>
                        </form>

                        {/* Footer compatto */}
                        <div className="mt-4 md:mt-8 pt-4 md:pt-6 border-t border-white/5 text-center">
                            <button
                                onClick={handleResend}
                                disabled={!isTimerExpired}
                                className={`flex items-center justify-center gap-2 mx-auto text-xs md:text-sm font-semibold transition-colors
                                    ${isTimerExpired
                                    ? 'text-younify-blue hover:text-white cursor-pointer px-3 py-1.5 md:px-4 md:py-2 bg-younify-blue/10 hover:bg-younify-blue rounded-lg border border-younify-blue/20'
                                    : 'text-slate-500 cursor-not-allowed'
                                }`}
                            >
                                <RotateCcw size={14} />
                                {isTimerExpired ? "Invia nuovo codice" : "Attendi scadenza per rinviare"}
                            </button>
                        </div>

                    </div>
                </motion.div>
            </section>
        </div>
    );
};

export default TwoFactorAuth;