import React, { useState, useRef, useEffect } from 'react';
import { Plus, X, MapPin, Calendar, Image as ImageIcon, AlignLeft, Type, ChevronDown, Check, Users, UserPlus, Upload, Trash2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

// Import Componenti
import AppNavbar from '../../components/layout/Navbar/AppNavbar';
import EventCard from '../../components/ui/EventCard/EventCard';
import styles from '../LandingPage/LandingPage.module.css';

// --- COMPONENTE CUSTOM DROPDOWN ---
const CustomDropdown = ({ options, value, onChange, icon: Icon, placeholder }) => {
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    return (
        <div className="relative w-full" ref={dropdownRef}>
            <button
                type="button"
                onClick={() => setIsOpen(!isOpen)}
                className={`w-full flex items-center justify-between bg-slate-900/60 backdrop-blur-xl border border-white/10 rounded-2xl px-4 py-3.5 text-sm font-medium transition-all shadow-lg group hover:border-younify-blue/50 ${isOpen ? 'border-younify-blue ring-1 ring-younify-blue/50' : ''}`}
            >
                <div className="flex items-center gap-3 overflow-hidden">
                    {Icon && <Icon size={18} className="text-slate-400 group-hover:text-younify-blue transition-colors shrink-0" />}
                    <span className={`truncate ${value === placeholder ? 'text-slate-400' : 'text-white'}`}>
                        {value}
                    </span>
                </div>
                <ChevronDown size={16} className={`text-slate-500 transition-transform duration-300 ${isOpen ? 'rotate-180 text-younify-blue' : ''}`} />
            </button>

            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, y: -10 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: -10 }}
                        transition={{ duration: 0.2 }}
                        className="absolute top-full left-0 mt-2 w-full bg-slate-900 border border-white/10 rounded-xl shadow-2xl z-50 max-h-60 overflow-y-auto overflow-x-hidden scrollbar-thin scrollbar-thumb-slate-700 scrollbar-track-transparent"
                    >
                        {options.map((option, idx) => (
                            <div
                                key={idx}
                                onClick={(e) => {
                                    e.stopPropagation();
                                    onChange(option);
                                    setIsOpen(false);
                                }}
                                className={`px-4 py-3 text-sm cursor-pointer flex items-center justify-between transition-colors
                                    ${value === option ? 'bg-younify-blue/10 text-younify-blue' : 'text-slate-300 hover:bg-white/5 hover:text-white'}
                                `}
                            >
                                <span className="truncate">{option}</span>
                                {value === option && <Check size={14} />}
                            </div>
                        ))}
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};

// --- HELPER: Genera partecipanti mock ---
const generateParticipants = (count) => {
    const names = ["Marco", "Giulia", "Luca", "Sara", "Davide", "Elena", "Francesco", "Chiara", "Alessandro", "Martina", "Matteo", "Sofia"];
    const surnames = ["Rossi", "Bianchi", "Ferrari", "Esposito", "Ricci", "Rizzo", "Gallo", "Costa", "Fontana", "Conti"];

    return Array.from({ length: count }, (_, i) => {
        const randomName = names[Math.floor(Math.random() * names.length)];
        const randomSurname = surnames[Math.floor(Math.random() * surnames.length)];
        const isMatch = Math.random() < 0.3;

        return {
            id: i,
            name: `${randomName} ${randomSurname}`,
            isMatch: isMatch,
            avatar: randomName.charAt(0)
        };
    });
};

const EventsPage = () => {

    const CITIES = ["Tutte le Città", "Milano", "Roma", "Napoli", "Torino", "Bologna", "Firenze", "Padova", "Bari"];

    const INITIAL_EVENTS = [
        {
            id: 1,
            title: "Erasmus Welcome Party",
            city: "Milano",
            category: "Party",
            date: "Venerdì, 22 Ott • 22:00",
            location: "Club Haus 80's",
            organizer: "ESN Milano",
            description: "La festa di benvenuto ufficiale per tutti gli studenti Erasmus! Ingresso libero con tessera ESN.",
            image: "https://images.unsplash.com/photo-1541532713592-79a0317b6b77?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
            participantsCount: 142,
            participantsList: generateParticipants(142),
            isJoined: false
        },
        {
            id: 2,
            title: "Workshop: AI & Future",
            city: "Milano",
            category: "Workshop",
            date: "Martedì, 26 Ott • 14:30",
            location: "Politecnico, Aula Magna",
            organizer: "PoliMi Data Science",
            description: "Un pomeriggio dedicato all'intelligenza artificiale con ospiti da Google e Microsoft.",
            image: "https://images.unsplash.com/photo-1591115765373-5207764f72e7?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
            participantsCount: 56,
            participantsList: generateParticipants(56),
            isJoined: false
        },
        {
            id: 3,
            title: "Aperitivo Ingegneria",
            city: "Roma",
            category: "Sociale",
            date: "Giovedì, 28 Ott • 18:00",
            location: "Bar Bianco",
            organizer: "Rappresentanti Studenti",
            description: "Birra, spritz e networking per scaricare la tensione post-esami.",
            image: "https://images.unsplash.com/photo-1561025700-1c4b82d46e3d?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
            participantsCount: 89,
            participantsList: generateParticipants(89),
            isJoined: true
        },
    ];

    const [events, setEvents] = useState(INITIAL_EVENTS);
    const [detailEvent, setDetailEvent] = useState(null);
    const [participantsEvent, setParticipantsEvent] = useState(null);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [activeCity, setActiveCity] = useState("Tutte le Città");

    const [newEvent, setNewEvent] = useState({
        title: "",
        city: "Milano",
        date: "",
        time: "",
        location: "",
        description: "",
        image: "" // Qui salveremo l'URL generato dal blob
    });

    // Stato per gestire errori nel caricamento file
    const [fileError, setFileError] = useState("");

    const filteredEvents = events.filter(event => {
        return activeCity === "Tutte le Città" || event.city === activeCity;
    });

    // --- HANDLER UPLOAD IMMAGINE ---
    const handleImageUpload = (e) => {
        const file = e.target.files[0];
        setFileError(""); // Reset errori

        if (!file) return;

        // 1. Validazione Estensione
        const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            setFileError("Formato non valido. Usa JPG, PNG o WEBP.");
            return;
        }

        // 2. Validazione Dimensione (10MB)
        const maxSize = 10 * 1024 * 1024; // 10MB in bytes
        if (file.size > maxSize) {
            setFileError("Il file è troppo grande. Max 10MB.");
            return;
        }

        // 3. Creazione Preview URL
        const previewUrl = URL.createObjectURL(file);
        setNewEvent(prev => ({ ...prev, image: previewUrl }));
    };

    const handleRemoveImage = (e) => {
        e.stopPropagation(); // Evita di riaprire il file picker
        setNewEvent(prev => ({ ...prev, image: "" }));
    };

    const handleJoinEvent = (eventId) => {
        setEvents(prev => prev.map(ev => {
            if (ev.id === eventId) {
                const newJoined = !ev.isJoined;
                return {
                    ...ev,
                    isJoined: newJoined,
                    participantsCount: newJoined ? ev.participantsCount + 1 : ev.participantsCount - 1
                };
            }
            return ev;
        }));
        if (detailEvent && detailEvent.id === eventId) {
            setDetailEvent(prev => ({
                ...prev,
                isJoined: !prev.isJoined,
                participantsCount: !prev.isJoined ? prev.participantsCount + 1 : prev.participantsCount - 1
            }));
        }
    };

    const handleCreateEvent = (e) => {
        e.preventDefault();
        const formattedDate = newEvent.date
            ? `${new Date(newEvent.date).toLocaleDateString('it-IT', { weekday: 'long', day: 'numeric', month: 'short' })} • ${newEvent.time}`
            : "Data da definire";

        const eventToAdd = {
            id: Date.now(),
            title: newEvent.title || "Nuovo Evento",
            city: newEvent.city,
            category: "Generale",
            date: formattedDate,
            location: newEvent.location || "Online",
            organizer: "Tu",
            description: newEvent.description || "Nessuna descrizione.",
            // Fallback immagine se l'utente non ne carica una
            image: newEvent.image || "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=800&q=80",
            participantsCount: 1,
            participantsList: generateParticipants(1),
            isJoined: true
        };

        setEvents([eventToAdd, ...events]);
        setNewEvent({ title: "", city: "Milano", date: "", time: "", location: "", description: "", image: "" });
        setIsCreateModalOpen(false);
    };

    const handleUserClick = (user) => {
        console.log("Apro card utente:", user.name);
    };

    return (
        <div className="relative min-h-screen w-full bg-slate-950 font-sans pb-40 text-slate-200 overflow-hidden">

            <AppNavbar />

            {/* Sfondo Blob */}
            <div className="fixed inset-0 z-0 pointer-events-none opacity-40">
                <div className={`${styles.blob} ${styles.blue}`}></div>
                <div className={`${styles.blob} ${styles.purple}`}></div>
            </div>

            <div className="relative z-10 max-w-4xl mx-auto px-4 pt-10">

                {/* HEADER */}
                <div className="flex flex-col gap-1 mb-8 text-center md:text-left">
                    <h1 className="text-4xl md:text-5xl font-black text-white font-inter tracking-tighter">
                        YOUni<span className="text-younify-blue">Fy</span> Events
                    </h1>
                </div>

                {/* FILTRO + TASTO CREA */}
                <div className="flex gap-3 mb-8 items-stretch z-20 relative">
                    <div className="flex-1">
                        <CustomDropdown options={CITIES} value={activeCity} onChange={setActiveCity} icon={MapPin} placeholder="Seleziona Città" />
                    </div>
                    <button onClick={() => setIsCreateModalOpen(true)} className="bg-white text-slate-950 px-5 rounded-2xl font-bold shadow-lg hover:bg-slate-200 hover:scale-105 transition-all flex items-center justify-center shrink-0 active:scale-95 group">
                        <Plus size={24} strokeWidth={3} className="group-hover:rotate-90 transition-transform duration-300"/>
                    </button>
                </div>

                {/* --- LISTA EVENTI HORIZONTAL SCROLL --- */}
                <div className="relative -mx-4 px-4">
                    {filteredEvents.length > 0 ? (
                        <div className="flex gap-6 overflow-x-auto pb-8 pt-2 px-2 snap-x snap-mandatory scrollbar-hide">
                            {filteredEvents.map(event => (
                                <EventCard
                                    key={event.id}
                                    event={event}
                                    onClick={(ev) => setDetailEvent(ev)}
                                />
                            ))}
                            <div className="w-2 shrink-0" />
                        </div>
                    ) : (
                        <div className="text-center py-20 bg-white/5 rounded-[2rem] border border-white/5 border-dashed backdrop-blur-sm mx-2">
                            <p className="text-slate-400 font-medium">Nessun evento in questa città.</p>
                        </div>
                    )}
                </div>
            </div>

            {/* --- MODALE 1: DETTAGLIO EVENTO --- */}
            <AnimatePresence>
                {detailEvent && (
                    <div className="fixed inset-0 z-[100] flex items-end md:items-center justify-center sm:px-4">
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setDetailEvent(null)} className="absolute inset-0 bg-slate-950/80 backdrop-blur-md" />

                        <motion.div
                            initial={{ y: "100%" }} animate={{ y: 0 }} exit={{ y: "100%" }}
                            transition={{ type: "spring", damping: 25, stiffness: 300 }}
                            className="relative w-full max-w-lg bg-slate-900 border-t border-x border-white/10 rounded-t-[2.5rem] md:rounded-[2.5rem] md:border-b shadow-2xl overflow-hidden max-h-[90vh] flex flex-col"
                        >
                            <div className="relative h-64 shrink-0">
                                <img src={detailEvent.image} className="w-full h-full object-cover" alt="Cover" />
                                <div className="absolute inset-0 bg-gradient-to-t from-slate-900 via-transparent to-transparent" />
                                <button onClick={() => setDetailEvent(null)} className="absolute top-4 right-4 p-2 bg-black/40 backdrop-blur-md rounded-full text-white hover:bg-white/20 transition-colors">
                                    <X size={20} />
                                </button>
                                <div className="absolute bottom-4 left-6">
                                    <span className="px-3 py-1 bg-younify-blue text-white text-xs font-bold rounded-full mb-2 inline-block shadow-lg shadow-blue-900/40">
                                        {detailEvent.category} • {detailEvent.city}
                                    </span>
                                    <h2 className="text-3xl font-black text-white leading-tight">{detailEvent.title}</h2>
                                </div>
                            </div>

                            <div className="p-6 overflow-y-auto">
                                <div className="flex flex-col gap-4 text-sm text-slate-300 mb-6">
                                    <div className="flex items-center gap-3">
                                        <Calendar className="text-slate-400" size={18} />
                                        <span className="font-medium text-white">{detailEvent.date}</span>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <MapPin className="text-slate-400" size={18} />
                                        <span>{detailEvent.location}</span>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <Users className="text-slate-400" size={18} />
                                        <span>Organizzato da <span className="text-white">{detailEvent.organizer}</span></span>
                                    </div>
                                </div>

                                <h3 className="text-white font-bold mb-2 flex items-center gap-2"><AlignLeft size={16}/> Descrizione</h3>
                                <p className="text-slate-400 leading-relaxed mb-8 text-sm">
                                    {detailEvent.description}
                                </p>
                            </div>

                            <div className="p-4 border-t border-white/5 bg-slate-900/50 backdrop-blur-md flex gap-3 mt-auto">
                                <button
                                    onClick={() => setParticipantsEvent(detailEvent)}
                                    className="flex-1 py-3 rounded-xl bg-white/5 hover:bg-white/10 text-white font-bold text-sm border border-white/5 transition-colors flex items-center justify-center gap-2"
                                >
                                    <Users size={18} /> Lista ({detailEvent.participantsCount})
                                </button>
                                <button
                                    onClick={() => handleJoinEvent(detailEvent.id)}
                                    className={`flex-1 py-3 rounded-xl font-bold text-sm flex items-center justify-center gap-2 transition-all shadow-lg
                                        ${detailEvent.isJoined
                                        ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/50'
                                        : 'bg-younify-blue text-white hover:bg-blue-600 shadow-blue-900/20'
                                    }
                                    `}
                                >
                                    {detailEvent.isJoined ? <><Check size={18} /> Partecipi</> : <><UserPlus size={18} /> Aderisci</>}
                                </button>
                            </div>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>

            {/* --- MODALE 2: LISTA PARTECIPANTI --- */}
            <AnimatePresence>
                {participantsEvent && (
                    <div className="fixed inset-0 z-[110] flex items-center justify-center px-4">
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setParticipantsEvent(null)} className="absolute inset-0 bg-slate-950/80 backdrop-blur-md" />

                        <motion.div
                            initial={{ opacity: 0, scale: 0.9, y: 20 }} animate={{ opacity: 1, scale: 1, y: 0 }} exit={{ opacity: 0, scale: 0.9, y: 20 }}
                            className="relative w-full max-w-sm bg-slate-900 border border-white/10 rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[70vh]"
                        >
                            <div className="flex justify-between items-center p-5 border-b border-white/5 bg-slate-900/95 backdrop-blur z-10">
                                <h2 className="text-lg font-bold text-white">Partecipanti</h2>
                                <button onClick={() => setParticipantsEvent(null)} className="p-2 rounded-full hover:bg-white/10"><X size={20} /></button>
                            </div>

                            <div className="overflow-y-auto p-4 space-y-6 scrollbar-thin scrollbar-thumb-white/10 scrollbar-track-transparent">

                                {/* MATCH */}
                                <div>
                                    <h3 className="text-xs font-bold text-younify-blue uppercase tracking-wider mb-3 ml-1">I tuoi Match</h3>
                                    <div className="space-y-2">
                                        {participantsEvent.participantsList.filter(u => u.isMatch).map((person, idx) => (
                                            <motion.div
                                                key={`match-${person.id}`} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: idx * 0.05 }}
                                                onClick={() => handleUserClick(person)}
                                                className="flex items-center gap-3 p-3 rounded-xl bg-younify-blue/10 border border-younify-blue/30 hover:bg-younify-blue/20 cursor-pointer transition-colors"
                                            >
                                                <div className="w-10 h-10 rounded-full bg-gradient-to-br from-younify-blue to-purple-500 p-[2px]">
                                                    <div className="w-full h-full rounded-full bg-slate-900 flex items-center justify-center text-white font-bold text-sm">
                                                        {person.avatar}
                                                    </div>
                                                </div>
                                                <span className="text-white font-bold text-sm block">{person.name}</span>
                                            </motion.div>
                                        ))}
                                    </div>
                                </div>

                                {/* ALTRI */}
                                <div>
                                    <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-3 ml-1">Tutti gli altri</h3>
                                    <div className="space-y-2">
                                        {participantsEvent.participantsList.filter(u => !u.isMatch).map((person, idx) => (
                                            <motion.div
                                                key={`other-${person.id}`} initial={{ opacity: 0 }} animate={{ opacity: 1 }}
                                                onClick={() => handleUserClick(person)}
                                                className="flex items-center gap-3 p-2.5 rounded-xl hover:bg-white/5 cursor-pointer transition-colors border border-transparent hover:border-white/5"
                                            >
                                                <div className="w-9 h-9 rounded-full bg-slate-800 flex items-center justify-center text-slate-400 font-bold text-xs border border-white/5">
                                                    {person.avatar}
                                                </div>
                                                <span className="text-slate-300 font-medium text-sm">{person.name}</span>
                                            </motion.div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>

            {/* --- MODALE CREA EVENTO --- */}
            <AnimatePresence>
                {isCreateModalOpen && (
                    <div className="fixed inset-0 z-[120] flex items-center justify-center px-4 overflow-hidden">

                        <motion.div
                            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                            onClick={() => setIsCreateModalOpen(false)}
                            className="absolute inset-0 bg-slate-950/80 backdrop-blur-md"
                        />

                        <motion.div
                            initial={{ opacity: 0, scale: 0.95, y: 40 }}
                            animate={{ opacity: 1, scale: 1, y: 0 }}
                            exit={{ opacity: 0, scale: 0.95, y: 40 }}
                            onClick={(e) => e.stopPropagation()}
                            className="relative w-full max-w-lg bg-slate-900 border border-white/10 rounded-[2rem] p-6 md:p-8 shadow-2xl flex flex-col max-h-[90vh] overflow-y-auto scrollbar-hide"
                        >
                            <div className="flex justify-between items-center mb-6">
                                <div>
                                    <h2 className="text-2xl font-black text-white">Crea Evento</h2>
                                    <p className="text-slate-400 text-xs mt-1">Organizza qualcosa di epico</p>
                                </div>
                                <button onClick={() => setIsCreateModalOpen(false)} className="p-2 rounded-full bg-white/5 hover:bg-white/10 transition-colors">
                                    <X size={20} />
                                </button>
                            </div>

                            <form onSubmit={handleCreateEvent} className="flex flex-col gap-5">
                                {/* Titolo */}
                                <div className="space-y-1">
                                    <label className="text-xs font-bold text-slate-400 ml-1">TITOLO EVENTO</label>
                                    <input required name="title" value={newEvent.title} onChange={(e) => setNewEvent({...newEvent, title: e.target.value})} placeholder="Es. Festa di Fine Sessione" className="w-full bg-slate-950/50 border border-white/10 rounded-xl py-3.5 px-4 text-white focus:border-younify-blue outline-none" />
                                </div>

                                {/* Città (Full Width) */}
                                <div className="space-y-1">
                                    <label className="text-xs font-bold text-slate-400 ml-1">CITTÀ</label>
                                    <CustomDropdown options={CITIES.slice(1)} value={newEvent.city} onChange={(val) => setNewEvent({...newEvent, city: val})} icon={MapPin} placeholder="Città" />
                                </div>

                                {/* Data e Ora */}
                                <div className="flex gap-4">
                                    <div className="flex-1 space-y-1">
                                        <label className="text-xs font-bold text-slate-400 ml-1">DATA</label>
                                        <input required type="date" name="date" value={newEvent.date} onChange={(e) => setNewEvent({...newEvent, date: e.target.value})} className="w-full bg-slate-950/50 border border-white/10 rounded-xl py-3.5 px-4 text-white focus:border-younify-blue outline-none [color-scheme:dark]" />
                                    </div>
                                    <div className="w-1/3 space-y-1">
                                        <label className="text-xs font-bold text-slate-400 ml-1">ORA</label>
                                        <input required type="time" name="time" value={newEvent.time} onChange={(e) => setNewEvent({...newEvent, time: e.target.value})} className="w-full bg-slate-950/50 border border-white/10 rounded-xl py-3.5 px-4 text-white text-center focus:border-younify-blue outline-none [color-scheme:dark]" />
                                    </div>
                                </div>

                                {/* Luogo Specifico */}
                                <div className="space-y-1">
                                    <label className="text-xs font-bold text-slate-400 ml-1">LUOGO (Indirizzo)</label>
                                    <input required name="location" value={newEvent.location} onChange={(e) => setNewEvent({...newEvent, location: e.target.value})} placeholder="Es. Via Roma 10" className="w-full bg-slate-950/50 border border-white/10 rounded-xl py-3.5 px-4 text-white focus:border-younify-blue outline-none" />
                                </div>

                                {/* UPLOAD IMMAGINE (Nuovo) */}
                                <div className="space-y-1">
                                    <label className="text-xs font-bold text-slate-400 ml-1">COPERTINA (Max 10MB)</label>
                                    <label className="relative flex flex-col items-center justify-center w-full h-32 border-2 border-dashed border-white/10 rounded-xl bg-slate-950/30 hover:bg-slate-950/50 transition-colors cursor-pointer group overflow-hidden">

                                        {newEvent.image ? (
                                            <>
                                                <img src={newEvent.image} alt="Preview" className="w-full h-full object-cover" />
                                                <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                                                    <Trash2 size={24} className="text-white" />
                                                    {/* Pulsante invisibile per rimuovere */}
                                                    <button type="button" onClick={handleRemoveImage} className="absolute inset-0 w-full h-full" />
                                                </div>
                                            </>
                                        ) : (
                                            <div className="flex flex-col items-center text-slate-500">
                                                <Upload size={24} className="mb-2 group-hover:text-younify-blue transition-colors" />
                                                <span className="text-xs font-medium">Clicca per caricare</span>
                                                <span className="text-[10px] mt-1">JPG, PNG, WEBP</span>
                                            </div>
                                        )}

                                        {/* Input File Nascosto */}
                                        <input
                                            type="file"
                                            className="hidden"
                                            accept="image/jpeg, image/png, image/jpg, image/webp"
                                            onChange={handleImageUpload}
                                        />
                                    </label>
                                    {/* Messaggio Errore */}
                                    {fileError && <p className="text-rose-500 text-xs ml-1 mt-1">{fileError}</p>}
                                </div>

                                {/* Descrizione */}
                                <div className="space-y-1">
                                    <label className="text-xs font-bold text-slate-400 ml-1">DESCRIZIONE</label>
                                    <textarea required name="description" value={newEvent.description} onChange={(e) => setNewEvent({...newEvent, description: e.target.value})} rows="4" placeholder="Dettagli..." className="w-full bg-slate-950/50 border border-white/10 rounded-xl py-3 px-4 text-white focus:border-younify-blue outline-none resize-none" />
                                </div>

                                <button type="submit" className="mt-4 w-full bg-younify-blue text-white font-bold py-4 rounded-xl shadow-lg hover:bg-blue-600 transition-all">Pubblica Evento</button>
                            </form>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>

        </div>
    );
};

export default EventsPage;