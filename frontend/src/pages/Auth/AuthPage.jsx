import React, { useState, useEffect, useRef, useMemo } from 'react';
import {
    User, ArrowRight, ArrowLeft, Mail, Lock,
    Building2, Hash, CheckCircle, GraduationCap,
    Calendar, MapPin, School, FileCheck, ChevronDown, AlertCircle, Eye, EyeOff, Instagram, Receipt
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

//IMPORT STILI
// (Navbar rimossa come richiesto)
import styles from '../LandingPage/LandingPage.module.css';

// --- IMPORT DATI REALI ---
import rawCitiesData from '../../data/comuni.json';

// --- DATI COSTANTI ---
const ALLOWED_EMAIL_DOMAINS = [
    "@studenti.unisannio.it", "@studenti.unibg.it", "@studio.unibo.it", "@student.unibz.it", "@unibz.it",
    "@studenti.unibs.it", "@studenti.unica.it", "@studenti.unicam.it", "@studenti.unimol.it", "@studenti.unicas.it",
    "@liuc.it", "@studenti.liuc.it", "@studium.unict.it", "@studenti.unicz.it", "@studenti.unich.it",
    "@studenti.uninsubria.it", "@studenti.unical.it", "@unikorestudent.it", "@edu.unife.it", "@edu.unifi.it",
    "@unifg.it", "@studenti.unifg.it", "@studenti.unige.it", "@student.univaq.it", "@gssi.it",
    "@studenti.unisalento.it", "@studenti.unimc.it", "@studenti.unime.it", "@studenti.unimi.it", "@campus.unimib.it",
    "@mail.polimi.it", "@studbocconi.it", "@icatt.it", "@studenti.iulm.it", "@studenti.unisr.it",
    "@st.hunimed.eu", "@studenti.unimore.it", "@studenti.unina.it", "@studenti.unicampania.it", "@studenti.unior.it",
    "@studenti.uniparthenope.it", "@studenti.unisob.na.it", "@studenti.unipd.it", "@community.unipa.it",
    "@studenti.unipr.it", "@universitadipavia.it", "@iusspavia.it", "@studenti.unipg.it", "@unistrapg.it",
    "@studenti.unipi.it", "@sns.it", "@santannapisa.it", "@studenti.unibas.it", "@studenti.unirc.it",
    "@unistrada.it", "@studenti.uniroma1.it", "@students.uniroma2.eu", "@stud.uniroma3.it", "@studenti.uniroma4.it",
    "@studenti.luiss.it", "@lumsastud.it", "@unier.it", "@alumni.unicampus.it", "@unilink.it", "@unint.eu",
    "@studenti.unisa.it", "@studenti.uniss.it", "@student.unisi.it", "@unistrasi.it", "@studenti.unite.it",
    "@studenti.unito.it", "@edu.unito.it", "@studenti.polito.it", "@studenti.unitn.it", "@stud.units.it",
    "@sissa.it", "@spes.uniud.it", "@campus.uniurb.it", "@stud.unive.it", "@stud.iuav.it", "@studenti.univr.it",
    "@studenti.unitus.it"
];

const UNIVERSITY_DATA = {
    "Ancona": ["Università Politecnica delle Marche"],
    "Aosta": ["Università della Valle d'Aosta"],
    "Bari": ["Università degli Studi di Bari Aldo Moro", "Politecnico di Bari", "LUM Giuseppe Degennaro"],
    "Benevento": ["Università degli Studi del Sannio"],
    "Bergamo": ["Università degli Studi di Bergamo"],
    "Bologna": ["Alma Mater Studiorum - Università di Bologna"],
    "Bolzano": ["Libera Università di Bolzano"],
    "Brescia": ["Università degli Studi di Brescia"],
    "Cagliari": ["Università degli Studi di Cagliari"],
    "Camerino": ["Università di Camerino"],
    "Campobasso": ["Università degli Studi del Molise"],
    "Cassino": ["Università degli Studi di Cassino e del Lazio Meridionale"],
    "Castellanza (VA)": ["LIUC - Università Cattaneo"],
    "Catania": ["Università degli Studi di Catania"],
    "Catanzaro": ["Università degli Studi Magna Græcia"],
    "Chieti-Pescara": ["Università degli Studi G. d'Annunzio"],
    "Como-Varese": ["Università degli Studi dell'Insubria"],
    "Cosenza (Rende)": ["Università della Calabria"],
    "Enna": ["Università Kore di Enna"],
    "Ferrara": ["Università degli Studi di Ferrara"],
    "Firenze": ["Università degli Studi di Firenze"],
    "Foggia": ["Università di Foggia"],
    "Genova": ["Università degli Studi di Genova"],
    "L'Aquila": ["Università degli Studi dell'Aquila", "Gran Sasso Science Institute"],
    "Lecce": ["Università del Salento"],
    "Macerata": ["Università di Macerata"],
    "Messina": ["Università degli Studi di Messina"],
    "Milano": ["Università degli Studi di Milano (Statale)", "Università degli Studi di Milano-Bicocca", "Politecnico di Milano", "Università Commerciale Luigi Bocconi", "Università Cattolica del Sacro Cuore", "IULM", "Università Vita-Salute San Raffaele", "Humanitas University"],
    "Modena e Reggio Emilia": ["Università degli Studi di Modena e Reggio Emilia"],
    "Napoli": ["Università degli Studi di Napoli Federico II", "Università degli Studi della Campania Luigi Vanvitelli", "Università degli Studi di Napoli L'Orientale", "Università degli Studi di Napoli Parthenope", "Università degli Studi Suor Orsola Benincasa"],
    "Padova": ["Università degli Studi di Padova"],
    "Palermo": ["Università degli Studi di Palermo"],
    "Parma": ["Università di Parma"],
    "Pavia": ["Università degli Studi di Pavia", "Scuola Universitaria Superiore IUSS"],
    "Perugia": ["Università degli Studi di Perugia", "Università per Stranieri di Perugia"],
    "Pisa": ["Università di Pisa", "Scuola Normale Superiore", "Scuola Superiore Sant'Anna"],
    "Potenza": ["Università degli Studi della Basilicata"],
    "Reggio Calabria": ["Università Mediterranea di Reggio Calabria", "Università per Stranieri Dante Alighieri"],
    "Roma": ["Sapienza Università di Roma", "Università degli Studi di Roma Tor Vergata", "Università degli Studi Roma Tre", "Università degli Studi di Roma Foro Italico", "LUISS Guido Carli", "LUMSA", "Università Europea di Roma", "Università Campus Bio-Medico", "Link Campus University", "UNINT"],
    "Salerno (Fisciano)": ["Università degli Studi di Salerno"],
    "Sassari": ["Università degli Studi di Sassari"],
    "Siena": ["Università di Siena", "Università per Stranieri di Siena"],
    "Teramo": ["Università degli Studi di Teramo"],
    "Torino": ["Università degli Studi di Torino", "Politecnico di Torino"],
    "Trento": ["Università degli Studi di Trento"],
    "Trieste": ["Università degli Studi di Trieste", "SISSA"],
    "Udine": ["Università degli Studi di Udine"],
    "Urbino": ["Università degli Studi di Urbino Carlo Bo"],
    "Venezia": ["Università Ca' Foscari Venezia", "Università Iuav di Venezia"],
    "Verona": ["Università di Verona"],
    "Viterbo": ["Università degli Studi della Tuscia"]
};

const CDL_DATA = {
    "Lauree Triennali (L)": [
        "L-1: Beni culturali", "L-2: Biotecnologie", "L-3: Discipline delle arti figurative della musica dello spettacolo e della moda",
        "L-4: Disegno industriale", "L-5: Filosofia", "L-6: Geografia", "L-7: Ingegneria civile e ambientale",
        "L-8: Ingegneria dell'informazione", "L-9: Ingegneria industriale", "L-10: Lettere", "L-11: Lingue e culture moderne",
        "L-12: Mediazione linguistica", "L-13: Scienze biologiche", "L-14: Scienze dei servizi giuridici", "L-15: Scienze del turismo",
        "L-16: Scienze dell'amministrazione e dell'organizzazione", "L-17: Scienze dell'architettura",
        "L-18: Scienze dell'economia e della gestione aziendale", "L-19: Scienze dell'educazione e della formazione",
        "L-20: Scienze della comunicazione", "L-21: Scienze della pianificazione territoriale urbanistica paesaggistica e ambientale",
        "L-22: Scienze delle attività motorie e sportive", "L-23: Scienze e tecniche dell'edilizia", "L-24: Scienze e tecniche psicologiche",
        "L-25: Scienze e tecnologie agrarie e forestali", "L-26: Scienze e tecnologie agro-alimentari", "L-27: Scienze e tecnologie chimiche",
        "L-28: Scienze e tecnologie della navigazione", "L-29: Scienze e tecnologie farmaceutiche", "L-30: Scienze e tecnologie fisiche",
        "L-31: Scienze e tecnologie informatiche", "L-32: Scienze e tecnologie per l'ambiente e la natura", "L-33: Scienze economiche",
        "L-34: Scienze geologiche", "L-35: Scienze matematiche", "L-36: Scienze politiche e delle relazioni internazionali",
        "L-37: Scienze sociali per la cooperazione lo sviluppo e la pace", "L-38: Scienze zootecniche e tecnologie delle produzioni animali",
        "L-39: Servizio sociale", "L-40: Sociologia", "L-41: Statistica", "L-42: Storia",
        "L-43: Tecnologie per la conservazione e il restauro dei beni culturali", "L/SNT1: Professioni sanitarie infermieristiche e professione sanitaria ostetrica",
        "L/SNT2: Professioni sanitarie della riabilitazione", "L/SNT3: Professioni sanitarie tecniche",
        "L/SNT4: Professioni sanitarie della prevenzione", "L-P01: Professioni tecniche per l'edilizia e il territorio",
        "L-P02: Professioni tecniche agrarie alimentari e forestali", "L-P03: Professioni tecniche industriali e dell'informazione"
    ],
    "Lauree Magistrali (LM)": [
        "LM-1: Antropologia culturale ed etnologia", "LM-2: Archeologia", "LM-3: Architettura del paesaggio",
        "LM-5: Archivistica e biblioteconomia", "LM-6: Biologia", "LM-7: Biotecnologie agrarie", "LM-8: Biotecnologie industriali",
        "LM-9: Biotecnologie mediche veterinarie e farmaceutiche", "LM-10: Conservazione dei beni architettonici e ambientali",
        "LM-11: Conservazione e restauro dei beni culturali", "LM-12: Design", "LM-14: Filologia moderna",
        "LM-15: Filologia letterature e storia dell'antichità", "LM-16: Finanza", "LM-17: Fisica", "LM-18: Informatica",
        "LM-19: Informazione e sistemi editoriali", "LM-20: Ingegneria aerospaziale e astronautica", "LM-21: Ingegneria biomedica",
        "LM-22: Ingegneria chimica", "LM-23: Ingegneria civile", "LM-24: Ingegneria dei sistemi edilizi", "LM-25: Ingegneria dell'automazione",
        "LM-26: Ingegneria della sicurezza", "LM-27: Ingegneria delle telecomunicazioni", "LM-28: Ingegneria elettrica",
        "LM-29: Ingegneria elettronica", "LM-30: Ingegneria energetica e nucleare", "LM-31: Ingegneria gestionale",
        "LM-32: Ingegneria informatica", "LM-33: Ingegneria meccanica", "LM-34: Ingegneria navale",
        "LM-35: Ingegneria per l'ambiente e il territorio", "LM-36: Lingue e letterature dell'Africa e dell'Asia",
        "LM-37: Lingue e letterature moderne europee e americane", "LM-38: Lingue moderne per la comunicazione e la cooperazione",
        "LM-39: Linguistica", "LM-40: Matematica", "LM-43: Metodologie informatiche per le discipline umanistiche",
        "LM-44: Modellistica matematico-fisica per l'ingegneria", "LM-45: Musicologia e beni musicali",
        "LM-47: Organizzazione e gestione dei servizi per lo sport e le attività motorie", "LM-48: Pianificazione territoriale urbanistica e ambientale",
        "LM-49: Progettazione e gestione dei sistemi turistici", "LM-50: Programmazione e gestione dei servizi educativi",
        "LM-51: Psicologia", "LM-52: Relazioni internazionali", "LM-53: Scienza e ingegneria dei materiali", "LM-54: Scienze chimiche",
        "LM-55: Scienze cognitive", "LM-56: Scienze dell'economia", "LM-57: Scienze dell'educazione degli adulti e della formazione continua",
        "LM-58: Scienze dell'universo", "LM-59: Scienze della comunicazione pubblica d'impresa e pubblicità", "LM-60: Scienze della natura",
        "LM-61: Scienze della nutrizione umana", "LM-62: Scienze della politica", "LM-63: Scienze delle pubbliche amministrazioni",
        "LM-64: Scienze delle religioni", "LM-65: Scienze dello spettacolo e produzione multimediale", "LM-66: Sicurezza informatica",
        "LM-67: Scienze e tecniche delle attività motorie preventive e adattate", "LM-68: Scienze e tecniche dello sport",
        "LM-69: Scienze e tecnologie agrarie", "LM-70: Scienze e tecnologie alimentari", "LM-71: Scienze e tecnologie della chimica industriale",
        "LM-72: Scienze e tecnologie della navigazione", "LM-73: Scienze e tecnologie forestali ed ambientali",
        "LM-74: Scienze e tecnologie geologiche", "LM-75: Scienze e tecnologie per l'ambiente e il territorio",
        "LM-76: Scienze economiche per l'ambiente e la cultura", "LM-77: Scienze economico-aziendali", "LM-78: Scienze filosofiche",
        "LM-79: Scienze geofisiche", "LM-80: Scienze geografiche", "LM-81: Scienze per la cooperazione allo sviluppo",
        "LM-82: Scienze statistiche", "LM-83: Scienze statistiche attuariali e finanziarie", "LM-84: Scienze storiche",
        "LM-85: Scienze pedagogiche", "LM-86: Scienze zootecniche e tecnologie animali", "LM-87: Servizio sociale e politiche sociali",
        "LM-88: Sociologia e ricerca sociale", "LM-89: Storia dell'arte", "LM-90: Studi europei",
        "LM-91: Tecniche e metodi per la società dell'informazione", "LM-92: Teorie della comunicazione",
        "LM-93: Teorie e metodologie dell'e-learning e della media education", "LM-94: Traduzione specialistica e interpretariato",
        "LM/SNT1: Scienze infermieristiche e ostetriche", "LM/SNT2: Scienze riabilitative delle professioni sanitarie",
        "LM/SNT3: Scienze delle professioni sanitarie tecniche", "LM/SNT4: Scienze delle professioni sanitarie della prevenzione"
    ],
    "Lauree Magistrali a Ciclo Unico": [
        "LM-4: Architettura e ingegneria edile-architettura", "LM-13: Farmacia e farmacia industriale", "LM-41: Medicina e chirurgia",
        "LM-42: Medicina veterinaria", "LM-46: Odontoiatria e protesi dentaria", "LM-85 bis: Scienze della formazione primaria",
        "LMG-01: Giurisprudenza"
    ]
};

const AuthPage = () => {
    const [isHostMode, setIsHostMode] = useState(false);

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
        firstName: '', lastName: '', dateOfBirth: '', email: '',
        password: '', confirmPassword: '', cdl: '', university: '', contactsInfo: ''
    });
    const [errors, setErrors] = useState({ email: false, password: false, passwordMatch: false });

    // --- STATO HOST ---
    const [hostData, setHostData] = useState({
        hostName: '', piva: '', pec: '', password: '', confirmPassword: '',
        city: '', province: '', uniList: []
    });
    const [hostErrors, setHostErrors] = useState({ piva: false, pec: false, password: false, passwordMatch: false });

    const overlayVariants = {
        student: { x: "100%" },
        host: { x: "0%"}
    };

    // --- LOGICA VALIDAZIONE ---
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!.]).{8,}$/;
    const pivaRegex = /^(?:0[1-9]|[1-7]\d)\d{9}$/;
    const pecRegex = /^[a-zA-Z0-9._%+-]+@.*(pec|legal|cert|sicurezza|posta).*\.[a-zA-Z]{2,}$/;
    const validateEmail = (email) => ALLOWED_EMAIL_DOMAINS.some(domain => email.endsWith(domain));

    // Handler Studente
    const handleStudentChange = (e) => {
        const { name, value } = e.target;
        setStudentData(prev => ({ ...prev, [name]: value }));
        if (name === 'email') setErrors(prev => ({ ...prev, email: value.length > 0 && !validateEmail(value) }));
        if (name === 'password') setErrors(prev => ({ ...prev, password: value.length > 0 && !passwordRegex.test(value), passwordMatch: studentData.confirmPassword.length > 0 && value !== studentData.confirmPassword }));
        if (name === 'confirmPassword') setErrors(prev => ({ ...prev, passwordMatch: value.length > 0 && value !== studentData.password }));
    };

    // Handler Host
    const handleHostChange = (e) => {
        const { name, value } = e.target;
        setHostData(prev => ({ ...prev, [name]: value }));
        if (name === 'piva') setHostErrors(prev => ({ ...prev, piva: value.length > 0 && !pivaRegex.test(value) }));
        if (name === 'pec') setHostErrors(prev => ({ ...prev, pec: value.length > 0 && !pecRegex.test(value) }));
        if (name === 'password') setHostErrors(prev => ({ ...prev, password: value.length > 0 && !passwordRegex.test(value), passwordMatch: hostData.confirmPassword.length > 0 && value !== hostData.confirmPassword }));
        if (name === 'confirmPassword') setHostErrors(prev => ({ ...prev, passwordMatch: value.length > 0 && value !== hostData.password }));
    };

    const handleSingleSelect = (field, value, isHost = false) => {
        if (isHost) {
            if (field === 'province') setHostData(prev => ({ ...prev, province: value, city: '' }));
            else setHostData(prev => ({ ...prev, [field]: value }));
        } else {
            setStudentData(prev => ({ ...prev, [field]: value }));
        }
    };

    const handleHostMultiSelect = (val) => {
        setHostData(prev => {
            const currentList = prev.uniList;
            if (currentList.includes(val)) return { ...prev, uniList: currentList.filter(item => item !== val) };
            else return currentList.length < 5 ? { ...prev, uniList: [...currentList, val] } : prev;
        });
    };

    const isPasswordValid = studentData.password.length > 0 && passwordRegex.test(studentData.password);
    const isHostPasswordValid = hostData.password.length > 0 && passwordRegex.test(hostData.password);

    // Submit Handles
    const handleStudentSubmit = (e) => {
        e.preventDefault();
        if (errors.email || errors.password || errors.passwordMatch || !studentData.email || !studentData.password) return alert("Compila i campi correttamente.");
        alert("Registrazione Studente simulata!");
    };

    const handleHostSubmit = (e) => {
        e.preventDefault();
        const isPivaValid = pivaRegex.test(hostData.piva);
        const isPecValid = pecRegex.test(hostData.pec);
        if (!isPivaValid || !isPecValid || hostErrors.password || hostErrors.passwordMatch || !hostData.hostName || !hostData.province || !hostData.city || hostData.uniList.length === 0) return alert("Compila i campi Host correttamente.");
        alert("Registrazione Host simulata!");
    };

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

    return (
        // Use h-dvh for mobile browsers to avoid address bar issues
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

            {/* Removed padding on mobile (py-0), kept on desktop (md:py-6) */}
            <div className="flex-1 flex items-center justify-center px-0 md:px-4 relative z-10 py-0 md:py-6">
                <div className="relative bg-slate-900/60 border border-white/10 w-full max-w-[950px] h-full md:h-[600px] rounded-none md:rounded-[20px] shadow-2xl flex flex-col md:flex-row overflow-hidden backdrop-blur-md">

                    {/* --- LAYOUT MOBILE (Mockup Telefono con Toggle Studente/Host) --- */}
                    <div className="md:hidden flex flex-col justify-center items-center w-full h-full relative z-20 py-4">

                        {/* WRAPPER RELATIVO PER TASTI LATERALI */}
                        <div className="relative">

                            {/* TASTI LATERALI (Decorativi) */}
                            <div className="absolute top-24 -left-[6px] w-[5px] h-6 bg-slate-800 rounded-l-sm shadow-sm"></div>
                            <div className="absolute top-36 -left-[6px] w-[5px] h-12 bg-slate-800 rounded-l-sm shadow-sm"></div>
                            <div className="absolute top-52 -left-[6px] w-[5px] h-12 bg-slate-800 rounded-l-sm shadow-sm"></div>
                            <div className="absolute top-40 -right-[6px] w-[5px] h-16 bg-slate-800 rounded-r-sm shadow-sm"></div>

                            {/* === CORPO TELEFONO === */}
                            <div className="
                                w-[90vw] max-w-[400px]
                                h-[85vh] min-h-[650px]
                                bg-black
                                border-[12px] border-slate-900
                                rounded-[55px]
                                shadow-2xl
                                flex flex-col overflow-hidden relative
                            ">

                                {/* SCHERMO */}
                                <div className="flex-1 bg-slate-950 flex flex-col relative rounded-[44px] overflow-hidden">

                                    {/* Dynamic Island */}
                                    <div className="absolute top-3 left-1/2 -translate-x-1/2 w-[96px] h-[26px] bg-black rounded-full z-50 justify-between items-center px-3 shadow-sm flex shrink-0">
                                        <div className="w-2 h-2 rounded-full bg-[#1a1a1a]"></div>
                                        <div className="w-4 h-4 rounded-full bg-[#1a1a1a]"></div>
                                    </div>

                                    {/* Sfondo sfumato */}
                                    <div className="absolute top-0 left-0 w-full h-40 bg-gradient-to-b from-younify-blue/20 to-transparent pointer-events-none opacity-50 z-0"></div>

                                    {/* CONTENUTO SCORREVOLE*/}
                                    <div className="flex-1 overflow-y-auto scrollbar-hide pt-14 pb-8 px-6 relative z-10">

                                        {/* --- TOGGLE SWITCH --- */}
                                        <div className="flex w-full bg-slate-900/80 p-1 rounded-xl border border-white/10 mb-6 backdrop-blur-md sticky top-0 z-40 shadow-lg">
                                            <button
                                                onClick={() => setIsHostMode(false)}
                                                className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all duration-300 ${!isHostMode ? 'bg-younify-blue text-white shadow-md' : 'text-slate-400 hover:text-white'}`}
                                            >
                                                Studente
                                            </button>
                                            <button
                                                onClick={() => setIsHostMode(true)}
                                                className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all duration-300 ${isHostMode ? 'bg-purple-600 text-white shadow-md' : 'text-slate-400 hover:text-white'}`}
                                            >
                                                Host
                                            </button>
                                        </div>

                                        {/* HEADER DINAMICO */}
                                        <AnimatePresence mode='wait'>
                                            <motion.div
                                                key={isHostMode ? "host-header" : "student-header"}
                                                initial={{ opacity: 0, x: isHostMode ? 20 : -20 }}
                                                animate={{ opacity: 1, x: 0 }}
                                                exit={{ opacity: 0, x: isHostMode ? -20 : 20 }}
                                                transition={{ duration: 0.2 }}
                                                className="text-center mb-6"
                                            >
                                                <h2 className="text-lg font-black text-white font-inter [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">
                                                    {isHostMode ? "Registrati come Host Partner" : "Registrati come studente"}
                                                </h2>
                                                <p className="text-slate-400 text-xs">
                                                    {isHostMode ? "Registra la tua attività" : "Compila i dati del tuo profilo"}
                                                </p>
                                            </motion.div>
                                        </AnimatePresence>

                                        {/* --- AREA FORM ---
                                            FIX: Rimosso "overflow-hidden" da qui.
                                            Ora i dropdown possono uscire dai bordi del form e tu puoi scrollare per vederli.
                                        */}
                                        <div className="relative w-full">
                                            <AnimatePresence mode='wait' initial={false}>

                                                {/* FORM STUDENTE */}
                                                {!isHostMode && (
                                                    <motion.div
                                                        key="student-mobile"
                                                        initial={{ opacity: 0, x: -50 }}
                                                        animate={{ opacity: 1, x: 0 }}
                                                        exit={{ opacity: 0, x: 50 }}
                                                        transition={{ duration: 0.3, ease: "easeInOut" }}
                                                    >
                                                        <form className="flex flex-col gap-3" onSubmit={handleStudentSubmit}>
                                                            <div className="flex gap-2 w-full">
                                                                <InputGroup className="w-1/2 !mb-0" icon={<User size={15} />} placeholder="Nome" type="text" name="firstName" onChange={handleStudentChange} />
                                                                <InputGroup className="w-1/2 !mb-0" icon={<User size={15} />} placeholder="Cognome" type="text" name="lastName" onChange={handleStudentChange} />
                                                            </div>

                                                            <InputGroup className="!mb-0" icon={<Calendar size={15} />} placeholder="Data di nascita" type="date" name="dateOfBirth" onChange={handleStudentChange} />

                                                            <InputGroup className="!mb-0" icon={<Mail size={15} />} placeholder="Email universitaria" type="email" name="email" onChange={handleStudentChange} errorMessage={errors.email ? "Email non valida" : null} />

                                                            <div className="flex gap-2 w-full">
                                                                <div className="w-1/2"><InputGroup className="!mb-0" icon={<Lock size={15} />} placeholder="Password" type="password" name="password" onChange={handleStudentChange} errorMessage={errors.password ? "Min. 8 caratteri: includere maiuscola, minuscola, numero e simbolo (@#$%^&+=!.)" : null} /></div>
                                                                <div className="w-1/2"><InputGroup className="!mb-0" icon={<CheckCircle size={15} />} placeholder="Conferma" type="password" name="confirmPassword" onChange={handleStudentChange} errorMessage={errors.passwordMatch ? "Non coincidono" : null} disabled={!isPasswordValid} /></div>
                                                            </div>

                                                            <SearchableDropdown label="Corso di Laurea" icon={<GraduationCap size={15} />} options={CDL_DATA} selected={studentData.cdl} onSelect={(val) => handleSingleSelect('cdl', val)} isGrouped={true} isMulti={false} />
                                                            <SearchableDropdown label="Università" icon={<School size={15} />} options={UNIVERSITY_DATA} selected={studentData.university} onSelect={(val) => handleSingleSelect('university', val)} isGrouped={true} isMulti={false} />

                                                            <div className="w-full bg-slate-950/50 border border-slate-700 rounded-xl flex items-center focus-within:border-younify-blue focus-within:ring-1 focus-within:ring-younify-blue transition-all group overflow-hidden py-0.5 mb-2">
                                                                <div className="pl-3 text-slate-400 group-focus-within:text-younify-blue transition-colors flex-shrink-0">
                                                                    <Instagram size={15} />
                                                                </div>
                                                                <span className="text-slate-500 text-xs font-medium select-none ml-2 flex-shrink-0">instagram.com/</span>
                                                                <input
                                                                    type="text"
                                                                    placeholder="username"
                                                                    value={studentData.contactsInfo.replace('instagram.com/', '')}
                                                                    onChange={(e) => setStudentData({...studentData, contactsInfo: 'instagram.com/' + e.target.value})}
                                                                    className="flex-1 bg-transparent border-none text-white placeholder-slate-600 focus:outline-none focus:ring-0 text-sm py-2 px-1"
                                                                />
                                                            </div>

                                                            <button type="submit" className="w-full mt-2 py-3 rounded-xl bg-younify-blue text-white font-bold text-sm shadow-lg shadow-younify-blue/20">
                                                                Registrati
                                                            </button>
                                                        </form>
                                                    </motion.div>
                                                )}

                                                {/* FORM HOST */}
                                                {isHostMode && (
                                                    <motion.div
                                                        key="host-mobile"
                                                        initial={{ opacity: 0, x: 50 }}
                                                        animate={{ opacity: 1, x: 0 }}
                                                        exit={{ opacity: 0, x: -50 }}
                                                        transition={{ duration: 0.3, ease: "easeInOut" }}
                                                    >
                                                        <form className="flex flex-col gap-3" onSubmit={handleHostSubmit}>
                                                            <InputGroup className="!mb-0" icon={<Building2 size={15} />} placeholder="Nome Attività" type="text" name="hostName" onChange={handleHostChange} />

                                                            {/* P.IVA */}
                                                            <div className="mb-1 w-full">
                                                                <div className={`w-full bg-slate-950/50 border rounded-xl flex items-center transition-all group overflow-hidden py-0.5 
                                                                    ${hostErrors.piva ? 'border-red-500 text-red-500' : 'border-slate-700 focus-within:border-younify-blue focus-within:ring-1 focus-within:ring-younify-blue'}`}>
                                                                    <div className={`pl-3 flex-shrink-0 transition-colors ${hostErrors.piva ? 'text-red-500' : 'text-slate-400 group-focus-within:text-younify-blue'}`}>
                                                                        <Receipt size={15} />
                                                                    </div>
                                                                    <span className={`text-xs font-bold select-none ml-2 flex-shrink-0 ${hostErrors.piva ? 'text-red-500' : 'text-slate-500'}`}>IT</span>
                                                                    <input
                                                                        type="text"
                                                                        name="piva"
                                                                        placeholder="Partita IVA (11 cifre)"
                                                                        value={hostData.piva}
                                                                        onChange={handleHostChange}
                                                                        maxLength={11}
                                                                        className="flex-1 bg-transparent border-none text-white placeholder-slate-600 focus:outline-none focus:ring-0 text-sm py-2 px-1"
                                                                    />
                                                                </div>
                                                                <ErrorMessage message={hostErrors.piva ? "P.IVA non valida" : null} />
                                                            </div>

                                                            <InputGroup className="!mb-0" icon={<FileCheck size={15} />} placeholder="PEC" type="email" name="pec" onChange={handleHostChange} />

                                                            <div className="flex gap-2 w-full">
                                                                <div className="w-1/2">
                                                                    <InputGroup
                                                                        className="!mb-0"
                                                                        icon={<Lock size={15} />}
                                                                        placeholder="Password"
                                                                        type="password"
                                                                        name="password"
                                                                        onChange={handleHostChange}
                                                                        errorMessage={hostErrors.password ? "Min. 8 caratteri: includere maiuscola, minuscola, numero e simbolo (@#$%^&+=!.)" : null}
                                                                    />
                                                                </div>
                                                                <div className="w-1/2">
                                                                    <InputGroup
                                                                        className="!mb-0"
                                                                        icon={<CheckCircle size={15} />}
                                                                        placeholder="Conferma"
                                                                        type="password"
                                                                        name="confirmPassword"
                                                                        onChange={handleHostChange}
                                                                        errorMessage={hostErrors.passwordMatch ? "Non coincidono" : null}
                                                                        disabled={!isHostPasswordValid}
                                                                    />
                                                                </div>
                                                            </div>

                                                            <div className="flex gap-2 w-full">
                                                                <div className="w-1/3"><SearchableDropdown label="Provincia" icon={<MapPin size={15} />} options={Object.keys(italianPlaces).sort()} selected={hostData.province} onSelect={(val) => handleSingleSelect('province', val, true)} isGrouped={false} /></div>
                                                                <div className="w-2/3"><SearchableDropdown label="Città" icon={<MapPin size={15} />} options={hostData.province ? (italianPlaces[hostData.province] || []) : []} selected={hostData.city} onSelect={(val) => handleSingleSelect('city', val, true)} isGrouped={false} disabled={!hostData.province} /></div>
                                                            </div>

                                                            {/* DROPDOWN PROBLEMATICA - Ora ha spazio per aprirsi grazie al pb-40 del contenitore e overflow visibile */}
                                                            <SearchableDropdown label="Università (massimo 5)" icon={<School size={15} />} options={UNIVERSITY_DATA} selected={hostData.uniList} onSelect={handleHostMultiSelect} isGrouped={true} isMulti={true} limit={5} />

                                                            <button type="submit" className="w-full mt-2 py-3 rounded-xl bg-purple-600 text-white font-bold text-sm shadow-lg shadow-purple-600/20">
                                                                Registrati
                                                            </button>
                                                        </form>
                                                    </motion.div>
                                                )}
                                            </AnimatePresence>
                                        </div>

                                        {/* Link Login */}
                                        <AnimatePresence mode='wait'>
                                            <motion.div
                                                key={isHostMode ? "host-login-link" : "student-login-link"}
                                                initial={{ opacity: 0 }}
                                                animate={{ opacity: 1 }}
                                                exit={{ opacity: 0 }}
                                                transition={{ duration: 0.2 }}
                                                className="mt-8 mb-4 text-center pb-4"
                                            >
                                                <p className="text-slate-500 text-xs">
                                                    Hai già un account? <br/>
                                                    <a href="#" className={`font-bold hover:underline ${isHostMode ? 'text-purple-400' : 'text-younify-blue'}`}>Accedi qui</a>
                                                </p>
                                            </motion.div>
                                        </AnimatePresence>

                                    </div>

                                    {/* Home Bar Mobile */}
                                    <div className="absolute bottom-1 left-1/2 -translate-x-1/2 w-[35%] h-[4px] bg-slate-100/30 rounded-full block z-50"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    {/* ---- DESKTOP LAYOUT (Hidden on mobile) ---- */}

                    {/* DESKTOP STUDENT FORM */}
                    <div className={`hidden md:flex w-1/2 h-full flex-col justify-center items-center p-5 transition-all duration-700 ${isHostMode ? 'opacity-0 pointer-events-none' : 'opacity-100 delay-300'}`}>
                        <div className="flex flex-col items-center w-full max-w-sm h-full px-1 overflow-y-auto scrollbar-hide">
                            <div className="text-center mb-3 mt-3 flex-shrink-0">
                                <h2 className="text-2xl font-black text-white font-inter">Registrati come studente</h2>
                                <p className="text-slate-400 text-xs">Compila i dati del tuo profilo</p>
                            </div>
                            <form className="w-full flex flex-col gap-2 flex-grow justify-center" onSubmit={handleStudentSubmit}>
                                <div className="flex gap-2 w-full">
                                    <InputGroup className="w-1/2" icon={<User size={15} />} placeholder="Nome" type="text" name="firstName" onChange={handleStudentChange} />
                                    <InputGroup className="w-1/2" icon={<User size={15} />} placeholder="Cognome" type="text" name="lastName" onChange={handleStudentChange} />
                                </div>
                                <InputGroup icon={<Calendar size={15} />} placeholder="Data di nascita" type="date" name="dateOfBirth" onChange={handleStudentChange} />
                                <InputGroup icon={<Mail size={15} />} placeholder="Email universitaria" type="email" name="email" onChange={handleStudentChange} errorMessage={errors.email ? "Email non valida" : null} />
                                <div className="flex gap-2 w-full">
                                    <div className="w-1/2">
                                        <InputGroup icon={<Lock size={15} />} placeholder="Password" type="password" name="password" onChange={handleStudentChange} errorMessage={errors.password ? "\"Min. 8 caratteri: includere maiuscola, minuscola, numero e simbolo (@#$%^&+=!.)\"" : null} />
                                    </div>
                                    <div className="w-1/2">
                                        <InputGroup icon={<CheckCircle size={15} />} placeholder="Conferma" type="password" name="confirmPassword" onChange={handleStudentChange} errorMessage={errors.passwordMatch ? "Non corrispondono" : null} disabled={!isPasswordValid} />
                                    </div>
                                </div>
                                <SearchableDropdown label="Corso di Laurea" icon={<GraduationCap size={15} />} options={CDL_DATA} selected={studentData.cdl} onSelect={(val) => handleSingleSelect('cdl', val)} isGrouped={true} isMulti={false} />
                                <SearchableDropdown label="Università" icon={<School size={15} />} options={UNIVERSITY_DATA} selected={studentData.university} onSelect={(val) => handleSingleSelect('university', val)} isGrouped={true} isMulti={false} />
                                <div className="w-full bg-slate-950/50 border border-slate-700 rounded-xl flex items-center focus-within:border-younify-blue focus-within:ring-1 focus-within:ring-younify-blue transition-all group overflow-hidden py-0.5 mb-2">
                                    <div className="pl-3 text-slate-400 group-focus-within:text-younify-blue transition-colors flex-shrink-0"><Instagram size={15} /></div>
                                    <span className="text-slate-500 text-xs font-medium select-none ml-2 flex-shrink-0">instagram.com/</span>
                                    <input type="text" name="contactsInfo" placeholder="username" value={studentData.contactsInfo.replace('instagram.com/', '')} onChange={(e) => setStudentData({...studentData, contactsInfo: 'instagram.com/' + e.target.value})} className="flex-1 bg-transparent border-none text-white placeholder-slate-600 focus:outline-none focus:ring-0 text-sm py-2 px-1" />
                                </div>
                                <button type="submit" className="mt-3 py-2.5 px-6 rounded-xl bg-younify-blue text-white font-bold text-sm shadow-lg hover:shadow-younify-blue/50 transition-all hover:scale-[1.02] active:scale-95 flex-shrink-0">Registrati</button>
                            </form>
                            <p className="mt-3 mb-2 text-slate-500 text-[12px] text-center flex-shrink-0 [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">Hai già un account? <a href="#" className="text-younify-blue hover:underline">Accedi qui</a></p>
                        </div>
                    </div>

                    {/* DESKTOP HOST FORM */}
                    <div className={`hidden md:flex w-1/2 h-full flex-col justify-center items-center p-5 transition-all duration-700 ${!isHostMode ? 'opacity-0 pointer-events-none' : 'opacity-100 delay-300'}`}>
                        <div className="flex flex-col items-center w-full max-w-sm h-full overflow-y-auto px-1 scrollbar-hide">
                            <div className="text-center mb-3 mt-3 flex-shrink-0">
                                <h2 className="text-2xl font-black text-white font-inter">Registrati come Host Partner</h2>
                                <p className="text-slate-400 text-xs">Registra la tua attività commerciale</p>
                            </div>
                            <form className="w-full flex flex-col gap-2 flex-grow justify-center pb-2" onSubmit={handleHostSubmit}>
                                <InputGroup icon={<Building2 size={15} />} placeholder="Nome Attività / Host" type="text" name="hostName" onChange={handleHostChange} />

                                {/* P.IVA CUSTOM DESKTOP */}
                                <div className="mb-2">
                                    <div className={`w-full bg-slate-950/50 border rounded-xl flex items-center transition-all group overflow-hidden py-0.5 
                                        ${hostErrors.piva ? 'border-red-500 text-red-500' : 'border-slate-700 focus-within:border-younify-blue focus-within:ring-1 focus-within:ring-younify-blue'}`}>
                                        <div className={`pl-3 flex-shrink-0 transition-colors ${hostErrors.piva ? 'text-red-500' : 'text-slate-400 group-focus-within:text-younify-blue'}`}><Receipt size={15} /></div>
                                        <span className={`text-xs font-bold select-none ml-2 flex-shrink-0 ${hostErrors.piva ? 'text-red-500' : 'text-slate-500'}`}>IT</span>
                                        <input type="text" name="piva" placeholder="Partita IVA (11 cifre)" value={hostData.piva} onChange={handleHostChange} maxLength={11} className="flex-1 bg-transparent border-none text-white placeholder-slate-600 focus:outline-none focus:ring-0 text-sm py-2 px-1" />
                                    </div>
                                    <ErrorMessage message={hostErrors.piva ? "P.IVA non valida (11 cifre)" : null} />
                                </div>

                                <InputGroup icon={<FileCheck size={15} />} placeholder="Indirizzo PEC" type="email" name="pec" onChange={handleHostChange} errorMessage={hostErrors.pec ? "Email PEC non valida" : null} />

                                <div className="flex gap-2 w-full">
                                    <div className="w-1/2">
                                        <InputGroup icon={<Lock size={15} />} placeholder="Password" type="password" name="password" onChange={handleHostChange} errorMessage={hostErrors.password ? "Min. 8 caratteri: Maiuscola, minuscola, numero e simbolo (@#$%^&+=!.)" : null} />
                                    </div>
                                    <div className="w-1/2">
                                        <InputGroup icon={<CheckCircle size={15} />} placeholder="Conferma" type="password" name="confirmPassword" onChange={handleHostChange} errorMessage={hostErrors.passwordMatch ? "Non corrispondono" : null} disabled={!isHostPasswordValid} />
                                    </div>
                                </div>

                                <div className="flex gap-2 w-full">
                                    <div className="w-1/3"><SearchableDropdown label="Provincia" icon={<MapPin size={15} />} options={Object.keys(italianPlaces).sort()} selected={hostData.province} onSelect={(val) => handleSingleSelect('province', val, true)} isGrouped={false} isMulti={false} /></div>
                                    <div className="w-2/3"><SearchableDropdown label={hostData.province ? "Seleziona Città" : "Scegli prima la provincia"} icon={<MapPin size={15} />} options={hostData.province ? (italianPlaces[hostData.province] || []) : []} selected={hostData.city} onSelect={(val) => handleSingleSelect('city', val, true)} isGrouped={false} isMulti={false} disabled={!hostData.province} /></div>
                                </div>

                                <SearchableDropdown label="Università (puoi selezionarne massimo 5)" icon={<School size={15} />} options={UNIVERSITY_DATA} selected={hostData.uniList} onSelect={handleHostMultiSelect} isGrouped={true} isMulti={true} limit={5} />

                                <button type="submit" className="mt-3 py-2.5 px-6 rounded-xl bg-purple-600 text-white font-bold text-sm shadow-lg hover:shadow-purple-600/50 transition-all hover:scale-[1.02] active:scale-95 flex-shrink-0">Registrati</button>
                            </form>
                            <p className="mt-2 mb-2 text-slate-500 text-[12px] text-center flex-shrink-0 [text-shadow:_0_2px_8px_rgba(0,0,0,0.6)]">Hai già un account? <a href="#" className="text-purple-400 hover:underline">Accedi qui</a></p>
                        </div>
                    </div>

                    {/* DESKTOP OVERLAY (Hidden on mobile) */}
                    {/* =========================================================
                        PANNELLO SCORREVOLE (OVERLAY) - DESKTOP
                       ========================================================= */}
                    <motion.div
                        className="hidden md:flex absolute top-0 left-0 w-1/2 h-full z-40 flex-col justify-center items-center text-center p-8 overflow-hidden text-white
                                   bg-gradient-to-br from-younify-blue/40 border border-white/50
                                   shadow-[0_8px_32px_0_rgba(31,38,135,0.37)] rounded-[20px]"
                        variants={overlayVariants}
                        initial="student"
                        animate={isHostMode ? "host" : "student"}
                        transition={{ type: "spring", stiffness: 200, damping: 25 }}
                    >
                        <div className="absolute inset-0 bg-gradient-to-b from-white/20 via-white/5 to-transparent pointer-events-none" />

                        {/* CONTENUTO 1: Visibile quando sei HOST (ti chiede: Sei Studente?) */}
                        <motion.div
                            className="absolute inset-0 flex flex-col justify-center items-center p-8 z-30"
                            // FIX: Aggiunto 'initial' per evitare il flash al caricamento
                            initial={{ opacity: 0, pointerEvents: "none" }}
                            animate={{ opacity: isHostMode ? 1 : 0, pointerEvents: isHostMode ? "auto" : "none" }}
                            transition={{ duration: 0.4 }}
                        >
                            <h2 className="text-3xl font-black font-inter mb-3 drop-shadow-lg">Sei uno Studente?</h2>
                            <p className="text-sm text-white/90 mb-6 font-medium px-6 drop-shadow-md">Unisciti alla community per trovare amici, eventi e opportunità.</p>
                            <button onClick={() => setIsHostMode(false)} className="group px-6 py-2.5 border border-white/50 bg-white/10 hover:bg-white text-white hover:text-purple-600 rounded-xl font-bold text-sm uppercase tracking-wider transition-all flex items-center gap-2 backdrop-blur-md shadow-lg hover:shadow-xl cursor-pointer">
                                <ArrowLeft size={16} className="group-hover:-translate-x-1 transition-transform" /> Registrati come Studente
                            </button>
                        </motion.div>

                        {/* CONTENUTO 2: Visibile quando sei STUDENTE (ti chiede: Sei Host?) */}
                        <motion.div
                            className="absolute inset-0 flex flex-col justify-center items-center p-8 z-30"
                            // FIX: Aggiunto 'initial' per mostrare questo testo subito al caricamento (se parte come studente)
                            initial={{ opacity: 1, pointerEvents: "auto" }}
                            animate={{ opacity: !isHostMode ? 1 : 0, pointerEvents: !isHostMode ? "auto" : "none" }}
                            transition={{ duration: 0.4 }}
                        >
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

// Componente InputGroup
const InputGroup = ({ icon, placeholder, type, name, onChange, errorMessage, disabled, className = "" }) => {
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
                    // MODIFICA QUI SOTTO: cambiata logica padding destro (pr-10 se password, altrimenti pr-3)
                    className={`w-full h-full bg-slate-950/50 border rounded-xl pl-8 ${type === 'password' ? 'pr-10' : 'pr-3'} py-2 text-white placeholder-slate-500 focus:outline-none focus:ring-1 transition-all text-sm flex-1 appearance-none ${type === 'date' ? 'text-slate-400' : ''} ${errorMessage ? 'border-red-500 focus:border-red-500 focus:ring-red-500' : disabled ? 'border-slate-800 cursor-not-allowed' : 'border-slate-700 focus:border-younify-blue focus:ring-younify-blue'}`}
                />
                {type === 'password' && !disabled && (
                    <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-white transition-colors focus:outline-none"
                    >
                        {showPassword ? <EyeOff size={14} /> : <Eye size={14} />}
                    </button>
                )}
            </div>
            <ErrorMessage message={errorMessage} />
        </div>
    );
};

// Updated SearchableDropdown with mobile-specific margin (mb-1 md:mb-2)
const SearchableDropdown = ({ label, icon, options, selected, onSelect, isGrouped, isMulti = false, limit = 5, disabled = false }) => {
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
            <div onClick={() => !disabled && setIsOpen(!isOpen)} className={`w-full bg-slate-950/50 border border-slate-700 rounded-xl py-1.5 md:py-2 pl-9 pr-8 text-left text-sm cursor-pointer hover:border-younify-blue transition-all flex items-center min-h-[38px] ${selected && (isMulti ? selected.length > 0 : selected !== '') ? 'text-white' : 'text-slate-500'}`}>
                <div className="absolute left-3 text-slate-400">{icon}</div>
                <div className="truncate text-xs">{getDisplayLabel()}</div>
                <div className="absolute right-3 text-slate-400"><ChevronDown size={14} className={`transition-transform ${isOpen ? 'rotate-180' : ''}`} /></div>
            </div>
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