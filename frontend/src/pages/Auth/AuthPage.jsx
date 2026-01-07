import React, { useState, useEffect, useRef, useMemo } from 'react';
import {
    User, ArrowRight, ArrowLeft, Mail, Lock, IdCard,
    Building2, Hash, CheckCircle, GraduationCap,
    Calendar, MapPin, School, FileCheck, ChevronDown, AlertCircle, Eye, EyeOff, Instagram, Receipt, CreditCard
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

//IMPORT STILI
import styles from '../LandingPage/LandingPage.module.css';

// --- IMPORT DATI REALI ---
import rawCitiesData from '../../data/comuni.json';

// --- IMPORT DATA ---
import { ALLOWED_EMAIL_DOMAINS } from '../../data/emailDomains';
import { UNIVERSITY_DATA } from '../../data/universities';
import { CDL_DATA } from '../../data/cdl';

const AuthPage = () => {
    const [isHostMode, setIsHostMode] = useState(false);
    const [isAssociation, setIsAssociation] = useState(false);

    // --- LOGICA DI TRASFORMAZIONE DATI ---
    const italianPlaces = useMemo(() => {
        const places = {};
        if (!rawCitiesData || !Array.isArray(rawCitiesData)) return {};
        try {
            rawCitiesData.forEach(city => {
                const nomeProvincia = city.provincia?.nome || city.provincia;
                const nomeComune = city.nome;
                if (nomeProvincia && nomeComune) {
                    if (!places[nomeProvincia]) places[nomeProvincia] = [];
                    places[nomeProvincia].push(nomeComune);
                }
            });
            Object.keys(places).forEach(prov => places[prov].sort());
        } catch (error) { console.error("Errore comuni.json:", error); }
        return places;
    }, []);

    // --- STATO STUDENTE ---
    const [studentData, setStudentData] = useState({
        firstName: '',
        lastName: '',
        dateOfBirth: new Date().toISOString().split('T')[0],
        email: '',
        password: '',
        confirmPassword: '',
        cdl: '',
        university: '',
        contactsInfo: ''
    });
    // Ora errors contiene stringhe (messaggi) o null
    const [errors, setErrors] = useState({});

    // --- STATO HOST ---
    const [hostData, setHostData] = useState({
        hostName: '', piva: '', pec: '', password: '', confirmPassword: '',
        city: '', province: '', uniList: []
    });
    // Ora hostErrors contiene stringhe (messaggi) o null
    const [hostErrors, setHostErrors] = useState({});

    const overlayVariants = {
        student: { x: "100%" },
        host: { x: "0%"}
    };

    // --- LOGICA VALIDAZIONE ---
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!.]).{8,}$/;
    const pivaRegex = /^(?:0[1-9]|[1-7]\d)\d{9}$/;
    const associationCfRegex = /^[A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]$/i;
    const pecRegex = /^[a-zA-Z0-9._%+-]+@.*(pec|legal|cert|sicurezza|posta).*\.[a-zA-Z]{2,}$/;
    const validateEmail = (email) => ALLOWED_EMAIL_DOMAINS.some(domain => email.endsWith(domain));

    // ==========================================
    // HANDLERS STUDENTE
    // ==========================================
    const handleStudentChange = (e) => {
        const { name, value } = e.target;

        // 1. Aggiorna lo stato
        setStudentData(prev => ({ ...prev, [name]: value }));

        // 2. Validazione Realtime
        let errorMsg = null;

        if (name === 'email') {
            if (value && !validateEmail(value)) errorMsg = "Email universitaria non valida";
        }

        if (name === 'password') {
            if (value && !passwordRegex.test(value)) errorMsg = "Min. 8 car: Maiusc, minusc, num e simbolo";
        }

        if (name === 'confirmPassword') {
            if (value && value !== studentData.password) errorMsg = "Le password non coincidono";
        }

        // Se il campo non è vuoto, mostra l'errore (o null se corretto).
        // Se è vuoto, rimuovi l'errore per non "urlare" subito.
        setErrors(prev => ({ ...prev, [name]: value ? errorMsg : null }));
    };

    const handleStudentSubmit = (e) => {
        e.preventDefault();
        const newErrors = {};

        if (!studentData.firstName) newErrors.firstName = "Nome richiesto";
        if (!studentData.lastName) newErrors.lastName = "Cognome richiesto";
        if (!studentData.dateOfBirth) newErrors.dateOfBirth = "Data di nascita richiesta";

        if (!studentData.email) newErrors.email = "Email richiesta";
        else if (!validateEmail(studentData.email)) newErrors.email = "Email universitaria non valida";

        if (!studentData.password) newErrors.password = "Password richiesta";
        else if (!passwordRegex.test(studentData.password)) newErrors.password = "Min. 8 caratteri: Maiusc, minusc, num e simbolo";

        if (!studentData.confirmPassword) newErrors.confirmPassword = "Conferma password";
        else if (studentData.password !== studentData.confirmPassword) newErrors.confirmPassword = "Le password non coincidono";

        if (!studentData.cdl) newErrors.cdl = "Seleziona un corso";
        if (!studentData.university) newErrors.university = "Seleziona università";

        setErrors(newErrors);

        if (Object.keys(newErrors).length > 0) return; // Blocca se ci sono errori

        alert("Registrazione Studente Riuscita!");
    };

    // ==========================================
    // HANDLERS HOST
    // ==========================================
    const handleHostChange = (e) => {
        const { name, value } = e.target;

        // Limite caratteri fisico
        const charLimit = (name === 'piva' && isAssociation) ? 16 : 11;
        if (name === 'piva' && value.length > charLimit) return;

        // 1. Aggiorna stato
        setHostData(prev => ({ ...prev, [name]: value }));

        // 2. Validazione Realtime
        let errorMsg = null;

        if (name === 'piva') {
            const currentRegex = isAssociation ? associationCfRegex : pivaRegex;
            // Controlla solo se abbiamo raggiunto la lunghezza minima per evitare errori mentre si scrive
            if (value.length === charLimit && !currentRegex.test(value)) {
                errorMsg = isAssociation ? "Codice Fiscale non valido" : "Partita IVA non valida";
            }
        }

        if (name === 'pec') {
            if (value && !pecRegex.test(value)) errorMsg = "Indirizzo PEC non valido";
        }

        if (name === 'password') {
            if (value && !passwordRegex.test(value)) errorMsg = "Min. 8 car: Maiusc, minusc, num e simbolo";
        }

        if (name === 'confirmPassword') {
            if (value && value !== hostData.password) errorMsg = "Le password non coincidono";
        }

        setHostErrors(prev => ({ ...prev, [name]: value ? errorMsg : null }));
    };

    const handleHostSubmit = (e) => {
        e.preventDefault();
        const newErrors = {};

        if (!hostData.hostName) newErrors.hostName = "Nome attività richiesto";

        if (!hostData.piva) {
            newErrors.piva = isAssociation ? "Codice Fiscale richiesto" : "Partita IVA richiesta";
        } else {
            const currentRegex = isAssociation ? associationCfRegex : pivaRegex;
            if (!currentRegex.test(hostData.piva)) {
                newErrors.piva = isAssociation ? "Codice Fiscale non valido" : "Partita IVA non valida";
            }
        }

        if (!hostData.pec) newErrors.pec = "PEC richiesta";
        else if (!pecRegex.test(hostData.pec)) newErrors.pec = "PEC non valida";

        if (!hostData.password) newErrors.password = "Password richiesta";
        else if (!passwordRegex.test(hostData.password)) newErrors.password = "Min. 8 caratteri: Maiusc, minusc, num e simbolo";

        if (!hostData.confirmPassword) newErrors.confirmPassword = "Conferma password";
        else if (hostData.password !== hostData.confirmPassword) newErrors.confirmPassword = "Le password non coincidono";

        if (!hostData.province) newErrors.province = "Seleziona provincia";
        if (!hostData.city) newErrors.city = "Seleziona città";

        setHostErrors(newErrors);

        if (Object.keys(newErrors).length > 0) return; // Blocca

        alert("Registrazione Host Riuscita!");
    };

    // ==========================================
    // HANDLERS SELECT (DROPDOWN)
    // ==========================================
    const handleSingleSelect = (field, value, isHost = false) => {
        if (isHost) {
            if (field === 'province') {
                setHostData(prev => ({ ...prev, province: value, city: '' }));
                setHostErrors(prev => ({ ...prev, province: null, city: null }));
            } else {
                setHostData(prev => ({ ...prev, [field]: value }));
                setHostErrors(prev => ({ ...prev, [field]: null }));
            }
        } else {
            setStudentData(prev => ({ ...prev, [field]: value }));
            setErrors(prev => ({ ...prev, [field]: null }));
        }
    };

    const handleHostMultiSelect = (val) => {
        setHostData(prev => {
            const currentList = prev.uniList;
            if (currentList.includes(val)) return { ...prev, uniList: currentList.filter(item => item !== val) };
            else return currentList.length < 5 ? { ...prev, uniList: [...currentList, val] } : prev;
        });
    };

    const isPasswordValid = studentData.password.length > 0;
    const isHostPasswordValid = hostData.password.length > 0;

    // Variabili per animazione mobile
    const mobileFormVariants = {
        initial: (isHost) => ({ opacity: 0, x: isHost ? 50 : -50, pointerEvents: 'none' }),
        animate: (isHost) => ({
            opacity: isHost === isHostMode ? 1 : 0,
            x: isHost === isHostMode ? 0 : (isHost ? 50 : -50),
            pointerEvents: isHost === isHostMode ? 'auto' : 'none',
            transition: { type: "spring", stiffness: 300, damping: 30 }
        })
    };

    const inputTransition = {
        initial: { opacity: 0, y: 10 },
        animate: { opacity: 1, y: 0 },
        exit: { opacity: 0, y: -10 },
        transition: { duration: 0.2 }
    };

    // Reset P.IVA Error on Checkbox toggle
    useEffect(() => {
        if (hostData.piva.length > 0) {
            // Opzionale: rivalida al volo se vuoi
        }
    }, [isAssociation]);

    return (
        <div className="h-dvh w-full bg-slate-950 flex flex-col font-sans overflow-hidden relative">
            {/* BACKGROUND */}
            <div className="fixed inset-0 z-0 pointer-events-none">
                <div className={`${styles.blob} ${styles.blue}`}></div>
                <div className={`${styles.blob} ${styles.indigo}`}></div>
                <div className={`${styles.blob} ${styles.rose}`}></div>
                <div className={`${styles.blob} ${styles.teal}`}></div>
                <div className={`${styles.blob} ${styles.purple}`}></div>
                <div className={`${styles.blob} ${styles.cyan}`}></div>
            </div>

            <div className="flex-1 flex items-center justify-center px-0 md:px-4 relative z-10 py-0 md:py-6">
                <div className="relative bg-slate-900/60 border border-white/10 w-full max-w-[1100px] h-full md:h-[750px] rounded-none md:rounded-[30px] shadow-2xl flex flex-col md:flex-row overflow-hidden backdrop-blur-md">

                    {/* --- LAYOUT MOBILE --- */}
                    <div className="md:hidden flex flex-col w-full h-full relative z-20 overflow-y-auto px-4 pb-4 scrollbar-hide">

                        {/* 1. TOGGLE - STICKY IN ALTO */}
                        {/* Ho aggiunto mt-6 per staccarlo dal bordo superiore dato che ho tolto il padding globale */}
                        <div className="flex w-full bg-slate-950/80 p-1 rounded-xl border border-white/10 mt-6 mb-2 backdrop-blur-md sticky top-0 z-50 shadow-lg shrink-0">
                            <button onClick={() => setIsHostMode(false)} className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all duration-300 ${!isHostMode ? 'bg-younify-blue text-white shadow-md' : 'text-slate-400 hover:text-white'}`}>Studente</button>
                            <button onClick={() => setIsHostMode(true)} className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all duration-300 ${isHostMode ? 'bg-purple-600 text-white shadow-md' : 'text-slate-400 hover:text-white'}`}>Host</button>
                        </div>

                        {/* 2. BLOCCO CENTRALE (TITOLO + FORM) - Centrato Verticalmente */}
                        <div className="flex-1 flex flex-col justify-center w-full min-h-min py-4">

                            {/* Intestazione Dinamica */}
                            <AnimatePresence mode='wait'>
                                <motion.div
                                    key={isHostMode ? "host-header" : "student-header"}
                                    // --- MODIFICA QUI: SLIDE LATERALE FLUIDO ---
                                    // Se diventa Host, entra da destra (x: 30). Se diventa Studente, entra da sinistra (x: -30).
                                    initial={{ opacity: 0, x: isHostMode ? 30 : -30 }}
                                    // Si posiziona al centro
                                    animate={{ opacity: 1, x: 0 }}
                                    // Se era Host, esce a sinistra (x: -30). Se era Studente, esce a destra (x: 30).
                                    exit={{ opacity: 0, x: isHostMode ? -30 : 30 }}
                                    // Usiamo easeInOut per una partenza e un arrivo morbidi
                                    transition={{ duration: 0.3, ease: "easeInOut" }}
                                    // -------------------------------------------
                                    className="text-center mb-6 shrink-0 overflow-hidden" // Aggiunto overflow-hidden per sicurezza
                                >
                                    <h2 className="text-xl font-black text-white font-inter [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">{isHostMode ? "Registrati come Host Partner" : "Registrati come studente"}</h2>
                                    <p className="text-slate-400 text-xs">{isHostMode ? "Registra la tua attività" : "Compila i dati del tuo profilo"}</p>
                                </motion.div>
                            </AnimatePresence>

                            {/* Contenitore Form */}
                            <div className="relative w-full">
                                <AnimatePresence mode='wait' initial={false}>
                                    {!isHostMode && (
                                        <motion.div key="student-mobile" initial={{ opacity: 0, x: -50 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 50 }} transition={{ duration: 0.3, ease: "easeInOut" }}>
                                            <form className="flex flex-col gap-3" onSubmit={handleStudentSubmit}>
                                                <div className="flex gap-2 w-full">
                                                    <InputGroup className="w-1/2 !mb-0" icon={<User size={15} />} placeholder="Nome" type="text" name="firstName" maxLength={30} onChange={handleStudentChange} errorMessage={errors.firstName} />
                                                    <InputGroup className="w-1/2 !mb-0" icon={<User size={15} />} placeholder="Cognome" type="text" name="lastName" maxLength={30} onChange={handleStudentChange} errorMessage={errors.lastName} />
                                                </div>
                                                <InputGroup className="!mb-0" icon={<Calendar size={15} />} placeholder="Data di nascita" type="date" name="dateOfBirth" value={studentData.dateOfBirth} onChange={handleStudentChange} errorMessage={errors.dateOfBirth} />
                                                <InputGroup className="!mb-0" icon={<Mail size={15} />} placeholder="Email universitaria" type="email" name="email" maxLength={40} onChange={handleStudentChange} errorMessage={errors.email} />
                                                <div className="flex gap-2 w-full">
                                                    <div className="w-1/2"><InputGroup className="!mb-0" icon={<Lock size={15} />} placeholder="Password" type="password" name="password" maxLength={100} onChange={handleStudentChange} errorMessage={errors.password} /></div>
                                                    <div className="w-1/2"><InputGroup className="!mb-0" icon={<CheckCircle size={15} />} placeholder="Conferma" type="password" name="confirmPassword" maxLength={100} onChange={handleStudentChange} errorMessage={errors.confirmPassword} disabled={!isPasswordValid} /></div>
                                                </div>
                                                <SearchableDropdown label="Corso di Laurea" icon={<GraduationCap size={15} />} options={CDL_DATA} selected={studentData.cdl} onSelect={(val) => handleSingleSelect('cdl', val)} isGrouped={true} isMulti={false} errorMessage={errors.cdl} />
                                                <SearchableDropdown label="Università" icon={<School size={15} />} options={UNIVERSITY_DATA} selected={studentData.university} onSelect={(val) => handleSingleSelect('university', val)} isGrouped={true} isMulti={false} errorMessage={errors.university} />
                                                <div className="w-full bg-slate-950/50 border border-slate-700 rounded-xl flex items-center focus-within:border-younify-blue focus-within:ring-1 focus-within:ring-younify-blue transition-all group overflow-hidden py-0.5 mb-2">
                                                    <div className="pl-3 text-slate-400 group-focus-within:text-younify-blue transition-colors flex-shrink-0"><Instagram size={15} /></div>
                                                    <span className="text-slate-500 text-xs font-medium select-none ml-2 flex-shrink-0">instagram.com/</span>
                                                    <input type="text" placeholder="username" value={studentData.contactsInfo.replace('instagram.com/', '')} onChange={(e) => setStudentData({...studentData, contactsInfo: 'instagram.com/' + e.target.value})} maxLength={50} className="flex-1 bg-transparent border-none text-white placeholder-slate-600 focus:outline-none focus:ring-0 text-sm py-2 px-1" />
                                                </div>
                                                <button type="submit" className="w-full mt-2 py-3 rounded-xl bg-younify-blue text-white font-bold text-sm shadow-lg shadow-younify-blue/20">Registrati</button>
                                            </form>
                                        </motion.div>
                                    )}

                                    {isHostMode && (
                                        <motion.div key="host-mobile" initial={{ opacity: 0, x: 50 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -50 }} transition={{ duration: 0.3, ease: "easeInOut" }}>
                                            <form className="flex flex-col gap-3" onSubmit={handleHostSubmit}>
                                                <InputGroup className="!mb-0" icon={<Building2 size={15} />} placeholder="Nome Attività" type="text" name="hostName" maxLength={30} onChange={handleHostChange} errorMessage={hostErrors.hostName} />

                                                {/* CHECKBOX ASSOCIAZIONE - MOBILE */}
                                                <div className="flex items-center justify-start gap-2 px-1 mb-1 w-full pl-2">
                                                    <input
                                                        type="checkbox"
                                                        id="isAssocMobile"
                                                        checked={isAssociation}
                                                        onChange={(e) => setIsAssociation(e.target.checked)}
                                                        className="appearance-none w-4 h-4 rounded border border-slate-600 bg-slate-950 checked:bg-purple-600 checked:border-purple-600 focus:ring-0 focus:ring-offset-0 cursor-pointer relative
                                                        after:content-[''] after:hidden checked:after:block after:absolute after:top-[2px] after:left-[5px] after:w-[5px] after:h-[9px] after:border-r-2 after:border-b-2 after:border-white after:rotate-45"
                                                    />
                                                    <label htmlFor="isAssocMobile" className="text-slate-400 text-[11px] cursor-pointer select-none mt-0.5 hover:text-white transition-colors">
                                                        Sono un'associazione
                                                    </label>
                                                </div>

                                                {/* LOGICA CONDIZIONALE MOBILE */}
                                                <motion.div layout className="w-full relative mb-1">
                                                    <AnimatePresence mode='wait'>
                                                        {isAssociation ? (
                                                            <motion.div key="mobile-assoc" {...inputTransition} className="w-full">
                                                                <InputGroup
                                                                    className="!mb-0"
                                                                    icon={<CreditCard size={15} />}
                                                                    placeholder="Codice Fiscale Ente"
                                                                    type="text"
                                                                    name="piva"
                                                                    value={hostData.piva}
                                                                    onChange={handleHostChange}
                                                                    maxLength={16}
                                                                    errorMessage={hostErrors.piva}
                                                                />
                                                            </motion.div>
                                                        ) : (
                                                            <motion.div key="mobile-piva" {...inputTransition} className="w-full">
                                                                <InputGroup
                                                                    className="!mb-0"
                                                                    icon={<Receipt size={15} />}
                                                                    placeholder="Partita IVA"
                                                                    type="text"
                                                                    name="piva"
                                                                    value={hostData.piva}
                                                                    onChange={handleHostChange}
                                                                    maxLength={11}
                                                                    errorMessage={hostErrors.piva}
                                                                />
                                                            </motion.div>
                                                        )}
                                                    </AnimatePresence>
                                                </motion.div>

                                                <InputGroup className="!mb-0" icon={<FileCheck size={15} />} placeholder="PEC" type="email" name="pec" maxLength={40} onChange={handleHostChange} errorMessage={hostErrors.pec} />
                                                <div className="flex gap-2 w-full">
                                                    <div className="w-1/2"><InputGroup className="!mb-0" icon={<Lock size={15} />} placeholder="Password" type="password" name="password" maxLength={100} onChange={handleHostChange} errorMessage={hostErrors.password} /></div>
                                                    <div className="w-1/2"><InputGroup className="!mb-0" icon={<CheckCircle size={15} />} placeholder="Conferma" type="password" name="confirmPassword" maxLength={100} onChange={handleHostChange} errorMessage={hostErrors.confirmPassword} disabled={!isHostPasswordValid} /></div>
                                                </div>
                                                <div className="flex gap-2 w-full">
                                                    <div className="w-1/3"><SearchableDropdown label="Provincia" icon={<MapPin size={15} />} options={Object.keys(italianPlaces).sort()} selected={hostData.province} onSelect={(val) => handleSingleSelect('province', val, true)} isGrouped={false} errorMessage={hostErrors.province} /></div>
                                                    <div className="w-2/3"><SearchableDropdown label="Città" icon={<MapPin size={15} />} options={hostData.province ? (italianPlaces[hostData.province] || []) : []} selected={hostData.city} onSelect={(val) => handleSingleSelect('city', val, true)} isGrouped={false} disabled={!hostData.province} errorMessage={hostErrors.city} /></div>
                                                </div>
                                                <SearchableDropdown label="Università (massimo 5)" icon={<School size={15} />} options={UNIVERSITY_DATA} selected={hostData.uniList} onSelect={handleHostMultiSelect} isGrouped={true} isMulti={true} limit={5} />
                                                <button type="submit" className="w-full mt-2 py-3 rounded-xl bg-purple-600 text-white font-bold text-sm shadow-lg shadow-purple-600/20">Registrati</button>
                                            </form>
                                        </motion.div>
                                    )}
                                </AnimatePresence>
                            </div>
                        </div>

                        {/* 3. LINK LOGIN - IN BASSO */}
                        <AnimatePresence mode='wait'>
                            {/* Aggiunto shrink-0 per evitare che venga schiacciato e mb-4 per staccarlo dal bordo */}
                            <motion.div key={isHostMode ? "host-login-link" : "student-login-link"} initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} transition={{ duration: 0.2 }} className="mt-4 text-center shrink-0 mb-6">
                                <p className="text-slate-500 text-xs">Hai già un account? <br/> <a href="#" className={`font-bold hover:underline ${isHostMode ? 'text-purple-400' : 'text-younify-blue'}`}>Accedi qui</a></p>
                            </motion.div>
                        </AnimatePresence>

                    </div>

                    {/* ---- DESKTOP LAYOUT ---- */}

                    {/* DESKTOP STUDENT FORM */}
                    <div className={`hidden md:flex w-1/2 h-full flex-col justify-center items-center p-5 transition-all duration-700 ${isHostMode ? 'opacity-0 pointer-events-none' : 'opacity-100 delay-300'}`}>
                        <div className="flex flex-col items-center w-full max-w-sm h-full px-1">
                            <div className="text-center mb-3 mt-7 flex-shrink-0">
                                <h2 className="text-2xl font-black text-white font-inter [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">Registrati come studente</h2>
                                <p className="text-slate-400 text-xs">Compila i dati del tuo profilo</p>
                            </div>
                            <form className="w-full flex flex-col gap-2 flex-grow justify-center" onSubmit={handleStudentSubmit}>
                                <div className="flex gap-2 w-full">
                                    <InputGroup className="w-1/2" icon={<User size={15} />} placeholder="Nome" type="text" name="firstName" maxLength={30} onChange={handleStudentChange} errorMessage={errors.firstName} />
                                    <InputGroup className="w-1/2" icon={<User size={15} />} placeholder="Cognome" type="text" name="lastName" maxLength={30} onChange={handleStudentChange} errorMessage={errors.lastName} />
                                </div>
                                <InputGroup icon={<Calendar size={15} />} placeholder="Data di nascita" type="date" name="dateOfBirth" onChange={handleStudentChange} errorMessage={errors.dateOfBirth} />
                                <InputGroup icon={<Mail size={15} />} placeholder="Email universitaria" type="email" name="email" maxLength={40} onChange={handleStudentChange} errorMessage={errors.email} />
                                <div className="flex gap-2 w-full">
                                    <div className="w-1/2">
                                        <InputGroup icon={<Lock size={15} />} placeholder="Password" type="password" name="password" maxLength={100} onChange={handleStudentChange} errorMessage={errors.password} />
                                    </div>
                                    <div className="w-1/2">
                                        <InputGroup icon={<CheckCircle size={15} />} placeholder="Conferma" type="password" name="confirmPassword" maxLength={100} onChange={handleStudentChange} errorMessage={errors.confirmPassword} disabled={!isPasswordValid} />
                                    </div>
                                </div>
                                <SearchableDropdown label="Corso di Laurea" icon={<GraduationCap size={15} />} options={CDL_DATA} selected={studentData.cdl} onSelect={(val) => handleSingleSelect('cdl', val)} isGrouped={true} isMulti={false} errorMessage={errors.cdl} />
                                <SearchableDropdown label="Università" icon={<School size={15} />} options={UNIVERSITY_DATA} selected={studentData.university} onSelect={(val) => handleSingleSelect('university', val)} isGrouped={true} isMulti={false} errorMessage={errors.university} />
                                <div className="w-full bg-slate-950/50 border border-slate-700 rounded-xl flex items-center focus-within:border-younify-blue focus-within:ring-1 focus-within:ring-younify-blue transition-all group overflow-hidden py-0.5 mb-2">
                                    <div className="pl-3 text-slate-400 group-focus-within:text-younify-blue transition-colors flex-shrink-0"><Instagram size={15} /></div>
                                    <span className="text-slate-500 text-xs font-medium select-none ml-2 flex-shrink-0">instagram.com/</span>
                                    <input type="text" name="contactsInfo" maxLength={50} placeholder="username" value={studentData.contactsInfo.replace('instagram.com/', '')} onChange={(e) => setStudentData({...studentData, contactsInfo: 'instagram.com/' + e.target.value})} className="flex-1 bg-transparent border-none text-white placeholder-slate-600 focus:outline-none focus:ring-0 text-sm py-2 px-1" />
                                </div>
                                <button type="submit" className="mt-3 py-2.5 px-6 rounded-xl bg-younify-blue text-white font-bold text-sm shadow-lg hover:shadow-younify-blue/50 transition-all hover:scale-[1.02] active:scale-95 flex-shrink-0">Registrati</button>
                            </form>
                            <p className="mt-3 mb-2 text-slate-500 text-[12px] text-center flex-shrink-0 [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">Hai già un account? <a href="#" className="text-younify-blue hover:underline">Accedi qui</a></p>
                        </div>
                    </div>

                    {/* DESKTOP HOST FORM */}
                    <div className={`hidden md:flex w-1/2 h-full flex-col justify-center items-center p-5 transition-all duration-700 ${!isHostMode ? 'opacity-0 pointer-events-none' : 'opacity-100 delay-300'}`}>
                        <div className="flex flex-col items-center w-full max-w-sm h-full">
                            <div className="text-center mb-3 mt-7 flex-shrink-0">
                                <h2 className="text-2xl font-black text-white font-inter [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">Registrati come Host Partner</h2>
                                <p className="text-slate-400 text-xs">Registra la tua attività commerciale</p>
                            </div>
                            <form className="w-full flex flex-col gap-2 flex-grow justify-center pb-2" onSubmit={handleHostSubmit}>

                                <InputGroup icon={<Building2 size={15} />} placeholder="Nome Attività / Host" type="text" name="hostName" maxLength={30} onChange={handleHostChange} errorMessage={hostErrors.hostName} />

                                {/* CHECKBOX ASSOCIAZIONE - MODIFICATA: Allineata a SX e Scura */}
                                <div className="flex items-center justify-start gap-2 px-1 mb-2 w-full pl-2">
                                    <input
                                        type="checkbox"
                                        id="isAssocDesktop"
                                        checked={isAssociation}
                                        onChange={(e) => setIsAssociation(e.target.checked)}
                                        // Aggiunto appearance-none e stili custom per farla scura
                                        className="appearance-none w-4 h-4 rounded border border-slate-600 bg-slate-950 checked:bg-purple-600 checked:border-purple-600 focus:ring-0 focus:ring-offset-0 cursor-pointer relative
                                        after:content-[''] after:hidden checked:after:block after:absolute after:top-[2px] after:left-[5px] after:w-[5px] after:h-[9px] after:border-r-2 after:border-b-2 after:border-white after:rotate-45"
                                    />
                                    <label htmlFor="isAssocDesktop" className="text-slate-400 text-[11px] cursor-pointer select-none mt-0.5 hover:text-white transition-colors">
                                        Sono un'associazione
                                    </label>
                                </div>

                                {/* LOGICA CONDIZIONALE */}
                                <div className="w-full overflow-hidden min-h-[48px]"> {/* min-h aiuta a stabilizzare l'altezza durante il cambio */}
                                    <AnimatePresence mode='wait'>
                                        {!isAssociation ? (
                                            /* CASO 1: PARTITA IVA (Con prefisso IT) */
                                            <motion.div key="desktop-piva" {...inputTransition}>
                                                <div className={`w-full bg-slate-950/50 border rounded-xl flex items-center transition-all group overflow-hidden 
                    ${hostErrors.piva ? 'border-red-500 text-red-500' : 'border-slate-700 focus-within:border-younify-blue focus-within:ring-1 focus-within:ring-younify-blue'}`}>
                                                    <div className={`pl-3 flex-shrink-0 transition-colors ${hostErrors.piva ? 'text-red-500' : 'text-slate-400 group-focus-within:text-younify-blue'}`}>
                                                        <Receipt size={15} />
                                                    </div>
                                                    <span className={`text-xs font-bold select-none ml-2 flex-shrink-0 ${hostErrors.piva ? 'text-red-500' : 'text-slate-500'}`}>IT</span>
                                                    <input
                                                        type="text"
                                                        name="piva"
                                                        placeholder="Partita IVA"
                                                        value={hostData.piva}
                                                        onChange={handleHostChange}
                                                        maxLength={11}
                                                        className="flex-1 bg-transparent border-none text-white placeholder-slate-600 focus:outline-none focus:ring-0 text-sm py-2 px-1"
                                                    />
                                                </div>
                                                <ErrorMessage message={hostErrors.piva} />
                                            </motion.div>
                                        ) : (
                                            /* CASO 2: CODICE FISCALE ASSOCIAZIONE */
                                            <motion.div key="desktop-assoc" {...inputTransition}>
                                                <InputGroup
                                                    icon={<CreditCard size={15} />}
                                                    placeholder="Codice Fiscale dell'Associazione"
                                                    type="text"
                                                    name="piva"
                                                    value={hostData.piva}
                                                    onChange={handleHostChange}
                                                    maxLength={16}
                                                    errorMessage={hostErrors.piva}
                                                />
                                            </motion.div>
                                        )}
                                    </AnimatePresence>
                                </div>

                                <InputGroup icon={<FileCheck size={15} />} placeholder="Indirizzo PEC" type="email" name="pec" maxLength={40} onChange={handleHostChange} errorMessage={hostErrors.pec} />

                                <div className="flex gap-2 w-full">
                                    <div className="w-1/2">
                                        <InputGroup icon={<Lock size={15} />} placeholder="Password" type="password" name="password" maxLength={100} onChange={handleHostChange} errorMessage={hostErrors.password} />
                                    </div>
                                    <div className="w-1/2">
                                        <InputGroup icon={<CheckCircle size={15} />} placeholder="Conferma" type="password" name="confirmPassword" maxLength={100} onChange={handleHostChange} errorMessage={hostErrors.confirmPassword} disabled={!isHostPasswordValid} />
                                    </div>
                                </div>

                                <div className="flex gap-2 w-full">
                                    <div className="w-1/3"><SearchableDropdown label="Provincia" icon={<MapPin size={15} />} options={Object.keys(italianPlaces).sort()} selected={hostData.province} onSelect={(val) => handleSingleSelect('province', val, true)} isGrouped={false} isMulti={false} errorMessage={hostErrors.province} /></div>
                                    <div className="w-2/3"><SearchableDropdown label={hostData.province ? "Seleziona Città" : "Scegli prima la provincia"} icon={<MapPin size={15} />} options={hostData.province ? (italianPlaces[hostData.province] || []) : []} selected={hostData.city} onSelect={(val) => handleSingleSelect('city', val, true)} isGrouped={false} isMulti={false} disabled={!hostData.province} errorMessage={hostErrors.city} /></div>
                                </div>

                                <SearchableDropdown label="Università (massimo 5)" icon={<School size={15} />} options={UNIVERSITY_DATA} selected={hostData.uniList} onSelect={handleHostMultiSelect} isGrouped={true} isMulti={true} limit={5} />

                                <button type="submit" className="mt-3 py-2.5 px-6 rounded-xl bg-purple-600 text-white font-bold text-sm shadow-lg hover:shadow-purple-600/50 transition-all hover:scale-[1.02] active:scale-95 flex-shrink-0">Registrati</button>
                            </form>
                            <p className="mt-2 mb-2 text-slate-500 text-[12px] text-center flex-shrink-0 [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">Hai già un account? <a href="#" className="text-purple-400 hover:underline">Accedi qui</a></p>
                        </div>
                    </div>

                    {/* OVERLAY DESKTOP */}
                    <motion.div
                        className="hidden md:flex absolute top-0 left-0 w-1/2 h-full z-40 flex-col justify-center items-center text-center p-8 overflow-hidden text-white bg-gradient-to-br from-younify-blue/40 border border-white/50 shadow-[0_8px_32px_0_rgba(31,38,135,0.37)] rounded-[20px]"
                        variants={overlayVariants}
                        initial="student"
                        animate={isHostMode ? "host" : "student"}
                        transition={{ type: "spring", stiffness: 200, damping: 25 }}
                    >
                        <div className="absolute inset-0 bg-gradient-to-b from-white/20 via-white/5 to-transparent pointer-events-none" />
                        <motion.div className="absolute inset-0 flex flex-col justify-center items-center p-8 z-30" initial={{ opacity: 0, pointerEvents: "none" }} animate={{ opacity: isHostMode ? 1 : 0, pointerEvents: isHostMode ? "auto" : "none" }} transition={{ duration: 0.4 }}>
                            <h2 className="text-3xl font-black font-inter mb-3 drop-shadow-lg">Sei uno Studente?</h2>
                            <p className="text-sm text-white/90 mb-6 font-medium px-6 drop-shadow-md">Unisciti alla community per trovare amici, eventi e opportunità.</p>
                            <button onClick={() => setIsHostMode(false)} className="group px-6 py-2.5 border border-white/50 bg-white/10 hover:bg-white text-white hover:text-purple-600 rounded-xl font-bold text-sm uppercase tracking-wider transition-all flex items-center gap-2 backdrop-blur-md shadow-lg hover:shadow-xl cursor-pointer">
                                <ArrowLeft size={16} className="group-hover:-translate-x-1 transition-transform" /> Registrati come Studente
                            </button>
                        </motion.div>
                        <motion.div className="absolute inset-0 flex flex-col justify-center items-center p-8 z-30" initial={{ opacity: 1, pointerEvents: "auto" }} animate={{ opacity: !isHostMode ? 1 : 0, pointerEvents: !isHostMode ? "auto" : "none" }} transition={{ duration: 0.4 }}>
                            <h2 className="text-3xl font-black font-inter mb-3 drop-shadow-lg">Sei un Host?</h2>
                            <p className="text-sm text-white/90 mb-6 font-medium px-6 drop-shadow-md">Hai un'attività e vuoi promuovere eventi per gli universitari?</p>
                            <button onClick={() => setIsHostMode(true)} className="group px-6 py-2.5 border border-white/50 bg-white/10 hover:bg-white text-white hover:text-younify-blue rounded-xl font-bold text-sm uppercase tracking-wider transition-all flex items-center gap-2 backdrop-blur-md shadow-lg hover:shadow-xl cursor-pointer">
                                Registrati come Host <ArrowRight size={16} className="group-hover:translate-x-1 transition-transform" />
                            </button>
                        </motion.div>
                    </motion.div>

                </div>
            </div>
        </div>
    );
};

// --- HELPER COMPONENTS ---

const ErrorMessage = ({ message }) => (
    <AnimatePresence>
        {message && (
            <motion.div initial={{ opacity: 0, y: -5, height: 0 }} animate={{ opacity: 1, y: 0, height: 'auto' }} exit={{ opacity: 0, y: -5, height: 0 }} className="overflow-hidden">
                <div className="flex items-center gap-1 text-red-500 text-[9px] ml-1 mt-1">
                    <AlertCircle size={10} /> <span>{message}</span>
                </div>
            </motion.div>
        )}
    </AnimatePresence>
);

const InputGroup = ({ icon, placeholder, type, name, onChange, errorMessage, disabled, className = "", value, maxLength }) => {
    const [showPassword, setShowPassword] = useState(false);
    const inputType = type === 'password' ? (showPassword ? 'text' : 'password') : type;

    return (
        <div className={`mb-1 md:mb-2 ${className}`}>
            <div className={`relative group ${disabled ? 'opacity-50 cursor-not-allowed' : ''}`}>
                <div className={`absolute left-3 top-1/2 -translate-y-1/2 transition-colors ${errorMessage ? 'text-red-500' : 'text-slate-400 group-focus-within:text-younify-blue'}`}>{icon}</div>
                <input
                    type={inputType}
                    name={name}
                    placeholder={placeholder}
                    onChange={onChange}
                    disabled={disabled}
                    value={value}
                    maxLength={maxLength}
                    className={`w-full h-full bg-slate-950/50 border rounded-xl pl-8 ${type === 'password' ? 'pr-10' : 'pr-3'} py-2 text-white placeholder-slate-500 focus:outline-none focus:ring-1 transition-all text-sm flex-1 appearance-none ${type === 'date' ? 'text-slate-400' : ''} 
                    ${errorMessage
                        ? 'border-red-500 focus:border-red-500 focus:ring-red-500'
                        : disabled ? 'border-slate-800 cursor-not-allowed' : 'border-slate-700 focus:border-younify-blue focus:ring-younify-blue'}`}
                />
                {type === 'password' && !disabled && (
                    <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-white transition-colors focus:outline-none">
                        {showPassword ? <EyeOff size={14} /> : <Eye size={14} />}
                    </button>
                )}
            </div>
            <ErrorMessage message={errorMessage} />
        </div>
    );
};

const SearchableDropdown = ({ label, icon, options, selected, onSelect, isGrouped, isMulti = false, limit = 5, disabled = false, errorMessage }) => {
    const [isOpen, setIsOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const dropdownRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (event) => { if (dropdownRef.current && !dropdownRef.current.contains(event.target)) setIsOpen(false); };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleSelectOption = (opt) => {
        if (disabled) return;
        onSelect(opt);
        if (!isMulti) setIsOpen(false);
    };

    const isSelected = (opt) => isMulti ? selected.includes(opt) : selected === opt;
    const getDisplayLabel = () => {
        if (!isMulti) return selected || label;
        if (selected.length === 0) return label;
        return selected.join(', ');
    };

    const renderOptions = () => {
        if (!isGrouped) {
            const filtered = options.filter(opt => opt.toLowerCase().includes(searchTerm.toLowerCase()));
            if (filtered.length === 0) return <div className="p-2 text-xs text-slate-500 text-center">Nessun risultato</div>;
            return filtered.map((opt, idx) => (
                <div key={idx} onClick={() => handleSelectOption(opt)} className={`p-2 text-xs cursor-pointer hover:bg-white/10 flex justify-between items-center ${isSelected(opt) ? 'text-younify-blue font-bold' : 'text-slate-300'}`}>
                    {opt} {isSelected(opt) && <CheckCircle size={12} />}
                </div>
            ));
        } else {
            return Object.keys(options).map((category, idx) => {
                const categoryOptions = options[category].filter(opt => opt.toLowerCase().includes(searchTerm.toLowerCase()));
                if (categoryOptions.length === 0) return null;
                return (
                    <div key={idx} className="border-b border-white/5 last:border-0">
                        <div className="px-2 py-1 text-[10px] font-bold text-slate-500 bg-slate-950/80 sticky top-0">{category}</div>
                        {categoryOptions.map((opt, i) => (
                            <div key={i} onClick={() => handleSelectOption(opt)} className={`p-2 pl-4 text-xs cursor-pointer hover:bg-white/10 flex justify-between items-center ${isSelected(opt) ? 'text-younify-blue font-bold' : 'text-slate-300'}`}>
                                {opt} {isSelected(opt) && <CheckCircle size={12} />}
                            </div>
                        ))}
                    </div>
                );
            });
        }
    };

    return (
        <div className={`relative w-full ${disabled ? 'opacity-50 pointer-events-none' : ''} mb-1 md:mb-2`} ref={dropdownRef}>
            <div onClick={() => !disabled && setIsOpen(!isOpen)}
                 className={`w-full bg-slate-950/50 border rounded-xl py-1.5 md:py-2 pl-9 pr-8 text-left text-sm cursor-pointer transition-all flex items-center min-h-[38px] 
                 ${errorMessage ? 'border-red-500 text-red-500' : 'border-slate-700 hover:border-younify-blue ' + (selected && (isMulti ? selected.length > 0 : selected !== '') ? 'text-white' : 'text-slate-500')}`}>
                <div className={`absolute left-3 ${errorMessage ? 'text-red-500' : 'text-slate-400'}`}>{icon}</div>
                <div className="truncate text-xs">{getDisplayLabel()}</div>
                <div className={`absolute right-3 ${errorMessage ? 'text-red-500' : 'text-slate-400'}`}><ChevronDown size={14} className={`transition-transform ${isOpen ? 'rotate-180' : ''}`} /></div>
            </div>
            <ErrorMessage message={errorMessage} />
            {isOpen && (
                <div className="relative md:absolute md:top-full left-0 w-full mt-1 bg-slate-900 border border-slate-700 rounded-xl shadow-xl z-50 max-h-48 overflow-hidden flex flex-col">
                    <div className="p-2 border-b border-slate-700">
                        <input type="text" placeholder="Cerca..." className="w-full bg-slate-950 rounded-lg p-1.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-younify-blue" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} autoFocus />
                    </div>
                    <div className="overflow-y-auto flex-1 scrollbar-thin scrollbar-thumb-slate-700 scrollbar-track-transparent">
                        {renderOptions()}
                    </div>
                    {isMulti && <div className="p-1.5 text-center text-[9px] text-slate-500 border-t border-slate-700 bg-slate-950">Selezionati: {selected.length}/{limit}</div>}
                </div>
            )}
        </div>
    );
};

export default AuthPage;