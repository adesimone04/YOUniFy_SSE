import React, { useState, useRef, useEffect } from 'react';
import {
    Plus, X, Calendar, MapPin, Sparkles, ArrowRight, ShieldAlert,
    Image as ImageIcon, Upload, Users, Clock, AlertCircle,
    ChevronRight, ChevronLeft, CalendarPlus
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

// Componenti
import AppNavbar from '../../components/layout/Navbar/AppNavbar';
import EventCard from '../../components/ui/EventCard/EventCard';
import EventFilterBar from '../../components/ui/EventFilterBar/EventFilterBar';
import eventService from '../../services/eventService';
import { ToastContainer } from 'react-toastify';
import Masonry from 'react-masonry-css';
import 'react-toastify/dist/ReactToastify.css';

// Dati Comuni
import comuniDataRaw from '../../data/comuni.json';

// --- HERO TITLE ---
const HeroTitle = () => (
    <div className="relative z-10 pt-4 pb-2 flex flex-col items-center text-center px-4">
        <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, ease: [0.16, 1, 0.3, 1] }}
            className="relative"
        >
            <div className="absolute -inset-x-20 -inset-y-10 bg-younify-blue/20 blur-[100px] rounded-full opacity-50 pointer-events-none" />
            <h1 className="text-5xl md:text-8xl font-black tracking-tight mb-6 relative z-10 flex flex-col md:block items-center">
                <div className="flex items-center justify-center gap-0">
                    <span className="text-transparent bg-clip-text bg-gradient-to-b from-white to-white/40">YOUni</span>
                    <span className="text-younify-blue drop-shadow-[0_0_20px_rgba(29,78,216,0.5)]">Fy</span>
                </div>
                <span className="text-transparent bg-clip-text bg-gradient-to-b from-white to-white/40 pl-2 pr-6 pb-2 inline-block">Events</span>
            </h1>
        </motion.div>
        <motion.p initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2, duration: 0.8 }} className="text-slate-400 text-base md:text-xl max-w-xl leading-relaxed font-medium">
            Unisciti agli eventi più esclusivi della tua città. <br className="hidden md:block"/> Connettiti, impara e divertiti.
        </motion.p>
    </div>
);

const EventsPage = () => {
    // --- STATI ---
    const isHost = localStorage.getItem('isHost') === 'true';

    const [events, setEvents] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const [selectedCity, setSelectedCity] = useState("");

    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [loading, setLoading] = useState(false);
    const loadMoreRef = useRef(null);
    const loadingRef = useRef(false);
    const hasMoreRef = useRef(true);
    const [previousEventCount, setPreviousEventCount] = useState(0);

    const [detailEvent, setDetailEvent] = useState(null);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [showParticipantsPanel, setShowParticipantsPanel] = useState(false);

    // Stato FAB & Navbar
    const [showFab, setShowFab] = useState(false);
    const filterBarRef = useRef(null);

    // --- FORM CREAZIONE ---
    const [newEvent, setNewEvent] = useState({
        name: '', description: '', dateAndTime: '', street: '', city: '', CAP: '', poster: null
    });
    const [formErrors, setFormErrors] = useState({});
    const [previewImage, setPreviewImage] = useState(null);
    const [fileError, setFileError] = useState("");
    const fileInputRef = useRef(null);

    const comuniList = Array.isArray(comuniDataRaw) ? comuniDataRaw : (comuniDataRaw?.default || []);
    const [showCityDropdown, setShowCityDropdown] = useState(false);

    const [isMobile, setIsMobile] = useState(window.innerWidth < 768);

    // Aggiungi questa funzione prima del return
    const isCityValid = () => {
        if (newEvent.city.length === 0) return true; // Empty is ok (verrà validato al submit)
        return comuniList.some(c => c.nome.toLowerCase() === newEvent.city.toLowerCase());
    };


    useEffect(() => {
        const handleResize = () => setIsMobile(window.innerWidth < 768);
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    const loadEvents = async (pageNumber) => {
        if (loadingRef.current) {
            return;
        }

        if (pageNumber > 0 && !hasMoreRef.current) {
            return;
        }

        try {
            setLoading(true);
            loadingRef.current = true;

            const response = await eventService.getAllEvents(pageNumber, 10);

            if (response.data?.content) {
                const backendEvents = response.data.content.map(event => ({
                    id: event.ID,
                    title: event.name,
                    city: event.city,
                    date: event.dateAndTime,
                    location: event.street,
                    organizer: event.hostName,
                    description: event.description,
                    image: event.posterUrl,
                    participantsCount: event.joinedUsers?.length || 0,
                    joined: event.joined,
                    participantsList: event.joinedUsers?.map(user => ({
                        name: user.fullName,
                        avatar: user.profileImageUrl
                    })) || []
                }));


                if (pageNumber === 0) {
                    setEvents(backendEvents);
                    setPreviousEventCount(0);
                } else {
                    setPreviousEventCount(events.length);
                    setEvents(prev => [...prev, ...backendEvents]);
                }

                setHasMore(!response.data.last);
                hasMoreRef.current = !response.data.last;
            }
        } catch (error) {
            console.error('❌ Errore caricamento eventi:', error);
        } finally {
            setLoading(false);
            loadingRef.current = false;
        }
    };



    useEffect(() => {
        loadEvents(0);
    }, []);

    useEffect(() => {
        const currentRef = loadMoreRef.current;


        if (!currentRef || events.length === 0) {
            return;
        }


        const observer = new IntersectionObserver(
            (entries) => {

                if (entries[0].isIntersecting && !loadingRef.current && hasMoreRef.current) {
                    setPage(prevPage => {
                        const nextPage = prevPage + 1;
                        loadEvents(nextPage);
                        return nextPage;
                    });
                }
            },
            { threshold: 0.1, rootMargin: '100px' }
        );

        observer.observe(currentRef);

        return () => {
            if (currentRef) observer.unobserve(currentRef);
            observer.disconnect();
        };
    }, [events.length]);




    // Observer per FAB
    useEffect(() => {
        const observer = new IntersectionObserver(
            ([entry]) => {
                setShowFab(!entry.isIntersecting && isHost);
            },
            { threshold: 0 }
        );

        if (filterBarRef.current) {
            observer.observe(filterBarRef.current);
        }

        return () => {
            if (filterBarRef.current) observer.unobserve(filterBarRef.current);
        };
    }, [isHost]);

    // DEFINIZIONE FAB
    const fabButton = (
        <button
            onClick={() => setIsCreateModalOpen(true)}
            className="w-14 h-14 bg-younify-blue text-white rounded-full shadow-lg flex items-center justify-center border border-white/20 active:scale-90 transition-transform"
        >
            <CalendarPlus size={24} strokeWidth={2.5} />
        </button>
    );

    // Date Logic
    const getMinDateTime = () => {
        const now = new Date();
        now.setHours(now.getHours() + 1);
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        return now.toISOString().slice(0, 16);
    };
    const [minDateTime, setMinDateTime] = useState(getMinDateTime());

    useEffect(() => {
        if (isCreateModalOpen) {
            setMinDateTime(getMinDateTime());
            setFormErrors({});
            if (!newEvent.dateAndTime) setNewEvent(prev => ({ ...prev, dateAndTime: getMinDateTime() }));
        }
    }, [isCreateModalOpen]);

    useEffect(() => {
        if (!detailEvent) setShowParticipantsPanel(false);
    }, [detailEvent]);

    const getInputStyle = (fieldName) => {
        const hasError = !!formErrors[fieldName];
        return `block w-full min-w-0 bg-white/5 rounded-xl px-3 md:px-4 py-1.5 md:py-3.5 text-sm md:text-base text-white placeholder:text-slate-500 focus:outline-none focus:bg-white/10 transition-all font-medium border ${hasError ? 'border-rose-500 focus:border-rose-500' : 'border-white/10 focus:border-younify-blue/50'}`;
    };

    const handleImageUpload = (e) => {
        const file = e.target.files[0];
        if (file) {
            setFormErrors(prev => ({ ...prev, poster: null }));
            if (file.size > 10 * 1024 * 1024) return setFileError("Il file supera i 10MB");
            const allowedTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/jpg'];
            if (!allowedTypes.includes(file.type)) return setFileError("Formato non valido. Usa JPG, PNG o WEBP.");
            setFileError("");
            const reader = new FileReader();
            reader.onloadend = () => {
                setPreviewImage(reader.result);
                setNewEvent({ ...newEvent, poster: file, image: reader.result });
            };
            reader.readAsDataURL(file);
        }
    };

    const handleRemoveImage = (e) => {
        e.stopPropagation();
        setPreviewImage(null);
        setNewEvent({ ...newEvent, poster: null });
        if (fileInputRef.current) fileInputRef.current.value = "";
    };



    const handleSubmit = async (e) => {
        e.preventDefault();

        const errors = {};

        if (!newEvent.poster && !previewImage)
            errors.poster = "La locandina è obbligatoria.";

        if (!newEvent.city.trim()) {
            errors.city = 'La città è obbligatoria.';
        } else {
            const cityExists = comuniList.some(
                c => c.nome.toLowerCase() === newEvent.city.toLowerCase()
            );
            if (!cityExists) {
                errors.city = 'Seleziona una città dalla lista.';
            }
        }

        if (!newEvent.CAP.trim()) {
            errors.CAP = 'Il CAP è obbligatorio.';
        } else if (newEvent.CAP.length !== 5) {
            errors.CAP = 'Il CAP deve essere di 5 cifre.';
        } else if (!/^\d{5}$/.test(newEvent.CAP)) {
            errors.CAP = 'Il CAP deve contenere solo numeri.';
        }


        if (newEvent.dateAndTime < minDateTime)
            errors.dateAndTime = "La data deve essere futura (min. 1 ora).";
        if (!newEvent.name.trim())
            errors.name = "Il titolo è obbligatorio.";
        else if (newEvent.name.length > 100)
            errors.name = "Massimo 100 caratteri.";
        if (!newEvent.description.trim())
            errors.description = "La descrizione è obbligatoria.";
        else if (newEvent.description.length > 200)
            errors.description = "Massimo 200 caratteri.";
        if (!newEvent.city.trim())
            errors.city = "La città è obbligatoria.";
        else if (newEvent.city.length > 30)
            errors.city = "Massimo 30 caratteri.";
        if (!newEvent.street.trim())
            errors.street = "L'indirizzo è obbligatorio.";
        else if (newEvent.street.length > 60)
            errors.street = "Massimo 60 caratteri.";
        if (!newEvent.CAP.trim())
            errors.CAP = "Il CAP è obbligatorio.";
        else if (newEvent.CAP.length !== 5)
            errors.CAP = "Il CAP deve essere di 5 cifre.";

        if (Object.keys(errors).length > 0) {
            setFormErrors(errors);
            return;
        }

        try {
            const response = await eventService.createEvent(newEvent);

            if (response.message === "POSTED_SUCCESSFULLY") {
                const eventsResponse = await eventService.getAllEvents(0, 20);

                const backendEvents = eventsResponse.data.content.map(event => ({
                    id: event.ID,
                    title: event.name,
                    city: event.city,
                    date: event.dateAndTime,
                    location: event.street,
                    organizer: event.hostName,
                    description: event.description,
                    image: event.posterUrl,
                    participantsCount: event.joinedUsers?.length || 0,
                    joined: event.joined,
                    participantsList: event.joinedUsers?.map(user => ({
                        name: user.fullName,
                        avatar: user.profileImageUrl
                    })) || []
                }));

                setEvents(backendEvents);
                setIsCreateModalOpen(false);
                setNewEvent({ name: '', description: '', dateAndTime: '', street: '', city: '', CAP: '', poster: null });
                setPreviewImage(null);
                setFormErrors({});
            }
        } catch (error) {
            alert(error.message || 'Errore nella creazione dell\'evento');
        }
    };


    const handleInputChange = (field, value) => {
        setNewEvent(prev => ({ ...prev, [field]: value }));
        if (formErrors[field]) setFormErrors(prev => ({ ...prev, [field]: null }));
    };

    const handleJoinToggle = async (eventId) => {
        if (isHost) return;

        try {
            // Verifica se l'evento è passato
            const event = events.find(ev => ev.id === eventId);
            const isEventPast = new Date(event.date) < new Date();

            if (isEventPast) {
                alert('Non puoi partecipare a un evento già passato');
                return;
            }

            // Chiama l'API
            if (event.joined) {
                await eventService.unjoinEvent(eventId);
            } else {
                await eventService.joinEvent(eventId);
            }

            // Aggiorna stato locale
            const updateEvent = (ev) => {
                const newStatus = !ev.joined;
                return {
                    ...ev,
                    joined: newStatus,
                    participantsCount: newStatus ? ev.participantsCount + 1 : ev.participantsCount - 1
                };
            };

            setEvents(events.map(ev => ev.id === eventId ? updateEvent(ev) : ev));
            if (detailEvent?.id === eventId) setDetailEvent(updateEvent(detailEvent));

        } catch (error) {
            alert(error.message || 'Errore nella gestione della partecipazione');
        }
    };



    const toggleRole = () => {
        localStorage.setItem('user_role', isHost ? 'student' : 'host');
        window.location.reload();
    };

    const filteredEvents = events.filter(ev => {
        const matchSearch = ev.title.toLowerCase().includes(searchQuery.toLowerCase());
        const matchCity = selectedCity === "" || ev.city.toLowerCase() === selectedCity.toLowerCase();
        return matchSearch && matchCity;
    });

    return (
        <div className="fixed inset-0 w-full h-full font-sans text-slate-200 selection:bg-younify-blue/30 selection:text-white overflow-hidden bg-slate-900/60 backdrop-blur-md lg:bg-transparent lg:backdrop-blur-none">

            <div className="relative z-50">
                <AppNavbar shouldShrink={showFab} mobileAction={isHost ? fabButton : null} />
            </div>

            <div className="absolute inset-0 overflow-y-auto z-10 scroll-smooth [&::-webkit-scrollbar]:hidden">
                <div className="pt-12 px-4 lg:pl-32">
                    <HeroTitle />
                    <div className="max-w-4xl mx-auto mt-20 md:mt-40 mb-16 relative z-50">
                        {/* Ref per IntersectionObserver */}
                        <EventFilterBar
                            ref={filterBarRef}
                            searchQuery={searchQuery} setSearchQuery={setSearchQuery}
                            selectedCity={selectedCity} setSelectedCity={setSelectedCity}
                            onActionClick={() => setIsCreateModalOpen(true)} showAction={isHost}
                        />
                    </div>
                </div>

                <div className="max-w-7xl mx-auto px-4 md:px-8 pb-40 lg:pl-32">
                    {filteredEvents.length > 0 ? (
                        <>
                            <Masonry
                                breakpointCols={{
                                    default: 3,
                                    1024: 2,
                                    768: 1
                                }}
                                className="flex -ml-8 w-auto"
                                columnClassName="pl-8 bg-clip-padding"
                            >
                                {filteredEvents.map((event, index) => {
                                    const isNewEvent = index >= previousEventCount;

                                    return (
                                        <motion.div
                                            key={event.id}
                                            onClick={() => setDetailEvent(event)}
                                            className="mb-8"
                                            initial={isNewEvent ? {
                                                opacity: 0,
                                                y: 50,
                                                scale: 0.9
                                            } : false}
                                            animate={{
                                                opacity: 1,
                                                y: 0,
                                                scale: 1
                                            }}
                                            transition={{
                                                duration: 0.5,
                                                delay: isNewEvent ? (index - previousEventCount) * 0.08 : 0,
                                                ease: [0.16, 1, 0.3, 1]
                                            }}
                                        >
                                            <EventCard event={event} onClick={() => setDetailEvent(event)} />
                                        </motion.div>
                                    );
                                })}
                            </Masonry>

                            {/* ✅ ELEMENTO SENTINELLA */}
                            <div ref={loadMoreRef} className="h-20 flex justify-center items-center py-8 mt-8">
                                {loading && (
                                    <div className="text-white text-sm animate-pulse flex items-center gap-2">
                                        <div className="w-2 h-2 bg-younify-blue rounded-full animate-bounce"></div>
                                        <div className="w-2 h-2 bg-younify-blue rounded-full animate-bounce delay-100"></div>
                                        <div className="w-2 h-2 bg-younify-blue rounded-full animate-bounce delay-200"></div>
                                        <span className="ml-2">Caricamento eventi...</span>
                                    </div>
                                )}
                            </div>

                            {!hasMore && (
                                <p className="text-center text-slate-400 py-6 text-sm">
                                    Non ci sono altri eventi disponibili
                                </p>
                            )}
                        </>
                    ) : (
                        <div className="flex flex-col items-center justify-center py-20 text-slate-500">
                            <Sparkles size={48} className="mb-4 opacity-50" />
                            <p className="text-xl font-medium">Nessun evento trovato.</p>
                        </div>
                    )}
                </div>



            </div>

            {/* --- MODALE DETTAGLIO + LISTA PARTECIPANTI --- */}
            <AnimatePresence>
                {detailEvent && (
                    <div className="fixed inset-0 z-[100] flex items-center justify-center p-0 md:p-4">
                        <motion.div
                            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} transition={{ duration: 0.3 }}
                            onClick={() => setDetailEvent(null)} className="absolute inset-0 bg-black/60 backdrop-blur-md"
                        />

                        <div className="relative z-10 flex flex-col md:flex-row items-end md:items-start justify-end md:justify-center w-full h-full md:h-auto md:max-h-[90vh] pointer-events-none">
                            {/* CARD PRINCIPALE */}
                            <motion.div
                                layout
                                initial={{ opacity: 0, y: "100%" }}
                                animate={{ opacity: 1, y: 0 }}
                                exit={{ opacity: 0, y: "100%" }}
                                transition={{ type: "spring", stiffness: 300, damping: 30 }}
                                className="w-full md:w-[600px] h-auto max-h-[92vh] md:max-h-[90vh] bg-[#0F131F] border-t md:border border-white/10 rounded-t-[2.5rem] md:rounded-[2.5rem] overflow-hidden shadow-2xl flex flex-col pointer-events-auto"
                            >
                                <div className="relative h-48 md:h-64 shrink-0">
                                    <img src={detailEvent.image} className="w-full h-full object-cover" alt="" />
                                    <div className="absolute inset-0 bg-gradient-to-t from-[#0F131F] via-transparent to-transparent" />
                                    <button onClick={() => setDetailEvent(null)} className="absolute top-6 right-6 p-2 bg-black/30 backdrop-blur-xl rounded-full text-white border border-white/10 hover:bg-white/10 z-50"><X size={20} /></button>
                                    <div className="absolute bottom-6 left-6 right-6 md:left-8 md:right-8">
                                        <div className="flex gap-2 mb-2 md:mb-3">
                                            <span className="px-3 py-1 bg-younify-blue/80 backdrop-blur-md rounded-lg text-[10px] md:text-xs font-bold text-white uppercase">{detailEvent.city}</span>
                                        </div>
                                        <h2 className="text-2xl md:text-5xl font-black text-white leading-tight break-words">
                                            {detailEvent.title}
                                        </h2>
                                        <p className="text-slate-400 text-sm md:text-base mt-2 font-medium flex items-center gap-2">
                                            <Users size={16} className="opacity-70" />
                                            Organizzato da <span className="text-white font-semibold">{detailEvent.organizer}</span>
                                        </p>


                                    </div>
                                </div>

                                <div className="p-5 md:p-8 overflow-y-auto space-y-5 md:space-y-8 flex-1 scrollbar-thin scrollbar-thumb-white/10">
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3 md:gap-4">
                                        <div className="bg-white/5 p-3 md:p-4 rounded-2xl border border-white/5"><Calendar className="text-younify-blue mb-2" /><p className="text-[10px] md:text-xs text-slate-500 font-bold uppercase">Data</p><p className="text-white text-sm md:text-base font-medium">{new Date(detailEvent.date).toLocaleString('it-IT', {weekday:'short', day:'numeric', month:'short', hour:'2-digit', minute:'2-digit'})}</p></div>
                                        <div className="bg-white/5 p-3 md:p-4 rounded-2xl border border-white/5"><MapPin className="text-rose-500 mb-2" /><p className="text-[10px] md:text-xs text-slate-500 font-bold uppercase">Dove</p><p className="text-white text-sm md:text-base font-medium">{detailEvent.location}</p></div>
                                    </div>
                                    <div><h3 className="text-base md:text-lg font-bold text-white mb-2">Descrizione</h3><p className="text-slate-400 leading-relaxed text-sm md:text-lg break-words whitespace-pre-wrap">
                                        {detailEvent.description}
                                    </p>
                                    </div>
                                    <div onClick={() => setShowParticipantsPanel(!showParticipantsPanel)} className={`flex items-center justify-between p-3 md:p-4 rounded-2xl border cursor-pointer transition-all group ${showParticipantsPanel ? 'bg-younify-blue/10 border-younify-blue/30' : 'bg-white/5 border-white/5 hover:bg-white/10'}`}>
                                        <div className="flex items-center gap-3"><div className={`p-2 rounded-full transition-colors ${showParticipantsPanel ? 'bg-younify-blue text-white' : 'bg-younify-blue/20 text-younify-blue group-hover:bg-younify-blue group-hover:text-white'}`}><Users size={20} /></div><div><p className={`text-sm font-bold ${showParticipantsPanel ? 'text-younify-blue' : 'text-white'}`}>Lista Partecipanti</p><p className="text-xs text-slate-400">{detailEvent.participantsCount} persone partecipano</p></div></div>
                                        {showParticipantsPanel ? <ChevronLeft size={20} className="text-younify-blue" /> : <ChevronRight size={20} className="text-slate-500 group-hover:text-white" />}
                                    </div>
                                </div>

                                {!isHost && (
                                    <div className="p-5 md:p-6 border-t border-white/5 bg-[#0F131F] backdrop-blur-xl flex gap-4 z-20 shrink-0 pb-8 md:pb-6">
                                        <motion.button
                                            onClick={() => {
                                                const isEventPast = new Date(detailEvent.date) < new Date();
                                                if (!isEventPast) {
                                                    handleJoinToggle(detailEvent.id);
                                                }
                                            }}
                                            disabled={new Date(detailEvent.date) < new Date()}
                                            animate={{
                                                backgroundColor: (() => {
                                                    const isEventPast = new Date(detailEvent.date) < new Date();
                                                    if (isEventPast) return 'rgba(255, 255, 255, 0.05)';
                                                    return detailEvent.joined ? 'rgba(255, 255, 255, 0.05)' : '#ffffff';
                                                })(),
                                                borderColor: (() => {
                                                    const isEventPast = new Date(detailEvent.date) < new Date();
                                                    if (isEventPast) return 'rgba(255, 255, 255, 0.1)';
                                                    return detailEvent.joined ? 'rgba(255, 255, 255, 0.1)' : 'transparent';
                                                })(),
                                                color: (() => {
                                                    const isEventPast = new Date(detailEvent.date) < new Date();
                                                    if (isEventPast) return '#94a3b8';
                                                    return detailEvent.joined ? '#ffffff' : '#000000';
                                                })()
                                            }}
                                            transition={{ duration: 0.3 }}
                                            whileTap={new Date(detailEvent.date) < new Date() ? {} : { scale: 0.95 }}
                                            className={`relative flex-1 h-14 md:h-16 rounded-2xl font-bold text-lg flex items-center justify-center overflow-hidden border ${
                                                new Date(detailEvent.date) < new Date()
                                                    ? 'border-white/10'
                                                    : detailEvent.joined
                                                        ? 'border-white/10'
                                                        : 'border-transparent'
                                            } shadow-lg cursor-pointer disabled:cursor-not-allowed disabled:opacity-60`}
                                        >
                                            <AnimatePresence mode="wait" initial={false}>
                                                {(() => {
                                                    const isEventPast = new Date(detailEvent.date) < new Date();

                                                    if (isEventPast) {
                                                        return (
                                                            <motion.div
                                                                key="past"
                                                                initial={{ y: 20, opacity: 0 }}
                                                                animate={{ y: 0, opacity: 1 }}
                                                                exit={{ y: -20, opacity: 0 }}
                                                                transition={{ type: "spring", stiffness: 300, damping: 20 }}
                                                                className="flex items-center gap-2"
                                                            >
                                                                <span>Evento Passato</span>
                                                                <Clock size={20} />
                                                            </motion.div>
                                                        );
                                                    }

                                                    if (detailEvent.joined) {
                                                        return (
                                                            <motion.div
                                                                key="joined"
                                                                initial={{ y: 20, opacity: 0 }}
                                                                animate={{ y: 0, opacity: 1 }}
                                                                exit={{ y: -20, opacity: 0 }}
                                                                transition={{ type: "spring", stiffness: 300, damping: 20 }}
                                                                className="flex items-center gap-2 text-rose-400"
                                                            >
                                                                <span>Disdici Partecipazione</span>
                                                                <X size={20} />
                                                            </motion.div>
                                                        );
                                                    }

                                                    return (
                                                        <motion.div
                                                            key="join"
                                                            initial={{ y: 20, opacity: 0 }}
                                                            animate={{ y: 0, opacity: 1 }}
                                                            exit={{ y: -20, opacity: 0 }}
                                                            transition={{ type: "spring", stiffness: 300, damping: 20 }}
                                                            className="flex items-center gap-2"
                                                        >
                                                            <span>Partecipa all'Evento</span>
                                                            <ArrowRight size={20} />
                                                        </motion.div>
                                                    );
                                                })()}
                                            </AnimatePresence>
                                        </motion.button>
                                    </div>
                                )}

                            </motion.div>

                            {/* CARD PARTECIPANTI */}
                            <AnimatePresence>
                                {showParticipantsPanel && (
                                    <motion.div
                                        initial={isMobile ? { y: "100%" } : { width: 0, opacity: 0, marginLeft: 0 }}
                                        animate={isMobile ? { y: 0 } : { width: 350, opacity: 1, marginLeft: 24 }}
                                        exit={isMobile ? { y: "100%" } : { width: 0, opacity: 0, marginLeft: 0 }}

                                        transition={{ duration: 0.4, ease: [0.16, 1, 0.3, 1] }}
                                        className={`bg-[#0F131F] border border-white/10 shadow-2xl flex flex-col overflow-hidden pointer-events-auto ${isMobile ? 'absolute inset-x-0 bottom-0 h-[85vh] rounded-t-[2.5rem] z-50' : 'rounded-[2.5rem] max-h-[90vh] shrink-0'}`}
                                    >
                                        <div className={`${isMobile ? 'w-full' : 'w-[350px]'} flex flex-col h-full`}>
                                            <div className="p-6 border-b border-white/5 flex justify-between items-center bg-[#0F131F]"><h3 className="text-xl font-bold text-white">Partecipanti</h3><button onClick={() => setShowParticipantsPanel(false)} className="p-2 hover:bg-white/10 rounded-full text-slate-400 hover:text-white transition-colors"><X size={20} /></button></div>
                                            <div className="flex-1 overflow-y-auto p-4 space-y-2 scrollbar-thin scrollbar-thumb-white/10">
                                                {(detailEvent.participantsList || detailEvent.avatars || []).length > 0 ? (
                                                    (detailEvent.participantsList || detailEvent.avatars.map((av, i) => ({name: `Utente ${i+1}`, avatar: av}))).map((user, idx) => (
                                                        <motion.div key={idx} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: idx * 0.05 }} className="flex items-center gap-4 p-3 rounded-xl hover:bg-white/5 transition-colors">
                                                            <img src={user.avatar} alt="" className="w-10 h-10 rounded-full object-cover ring-2 ring-white/10" /><span className="text-slate-200 font-medium">{user.name}</span>
                                                        </motion.div>
                                                    ))
                                                ) : (<div className="py-10 text-center text-slate-500"><Users size={32} className="mx-auto mb-2 opacity-30" /><p>Ancora nessun partecipante.</p></div>)}
                                            </div>
                                        </div>
                                    </motion.div>
                                )}
                            </AnimatePresence>
                        </div>
                    </div>
                )}
            </AnimatePresence>

            {/* MODALE CREAZIONE (Versione Super Compatta No-Scroll) */}
            <AnimatePresence>
                {isCreateModalOpen && isHost && (
                    <div className="fixed inset-0 z-[120] flex items-center justify-center p-0 md:p-4 overflow-x-hidden">
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setIsCreateModalOpen(false)} className="absolute inset-0 bg-slate-950/80 backdrop-blur-md" />
                        <motion.div
                            initial={{ scale: 0.95, opacity: 0, y: 20 }}
                            animate={{ scale: 1, opacity: 1, y: 0 }}
                            exit={{ scale: 0.95, opacity: 0, y: 20 }}
                            className="relative w-full h-[96dvh] md:h-auto max-w-4xl bg-[#0F131F] border-t md:border border-white/10 rounded-t-[2rem] md:rounded-[2.5rem] shadow-2xl flex flex-col overflow-hidden max-h-[100dvh] md:max-h-[90vh] mt-auto md:mt-0"
                        >
                            <div className="p-4 md:p-6 border-b border-white/5 flex justify-between items-center bg-[#0F131F] z-10 shrink-0">
                                <h2 className="text-lg md:text-2xl font-black text-white tracking-tight">Nuovo Evento</h2>
                                <button onClick={() => setIsCreateModalOpen(false)} className="p-2 hover:bg-white/10 rounded-full text-slate-400 hover:text-white transition-colors"><X size={24} /></button>
                            </div>

                            {/* MODIFICA: p-3 per ridurre margini laterali */}
                            <form className="flex-1 overflow-y-auto p-3 md:p-8 space-y-3 md:space-y-6 scrollbar-thin scrollbar-thumb-white/10">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3 md:gap-8 h-full">
                                    <div className="flex flex-col gap-3 md:gap-6">

                                        {/* FOTO (Ridotta a h-20) */}
                                        <div className="space-y-1 md:space-y-2">
                                            <div className="flex justify-between">
                                                <label className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1">Locandina *</label>
                                                {formErrors.poster && <span className="text-[10px] md:text-xs font-bold text-rose-500">{formErrors.poster}</span>}
                                            </div>
                                            <div onClick={() => fileInputRef.current?.click()} className={`relative w-full h-28 md:h-56 rounded-xl border ${formErrors.poster || fileError ? 'border-rose-500' : 'border-white/10 hover:border-younify-blue/50'} bg-white/[0.02] flex flex-col items-center justify-center cursor-pointer overflow-hidden group transition-all`}>
                                                {previewImage ? (
                                                    <>
                                                        <img src={previewImage} alt="Uploaded" className="w-full h-full object-cover opacity-80 group-hover:opacity-100 transition-opacity" />
                                                        <div className="absolute inset-0 flex items-center justify-center bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity"><Upload size={24} className="text-white drop-shadow-lg" /></div>
                                                        <button onClick={handleRemoveImage} type="button" className="absolute top-1 right-1 p-1 bg-black/60 hover:bg-rose-500/90 text-white rounded-full transition-colors z-20 backdrop-blur-md shadow-lg border border-white/10"><X size={12} /></button>
                                                    </>
                                                ) : (
                                                    <div className="flex flex-col items-center text-slate-500 group-hover:text-slate-300 transition-colors"><ImageIcon size={20} className={`mb-1 ${formErrors.poster ? 'text-rose-500' : ''}`} /><p className="text-[10px] md:text-sm font-medium">Carica foto</p></div>
                                                )}
                                                <input type="file" ref={fileInputRef} onChange={handleImageUpload} accept="image/jpeg, image/png, image/webp, image/jpg" className="hidden" />
                                            </div>
                                            {fileError && <div className="flex items-center gap-1 text-rose-500 text-[10px] md:text-xs mt-1 ml-1"><AlertCircle size={12}/> {fileError}</div>}
                                        </div>

                                        {/* DESCRIZIONE DESKTOP */}
                                        <div className="space-y-1 md:space-y-2 flex-1 flex flex-col hidden md:flex">
                                            <div className="flex justify-between">
                                                <label className="text-xs font-bold text-slate-500 uppercase ml-1">Descrizione *</label>
                                                {formErrors.description ? <span className="text-xs font-bold text-rose-500">{formErrors.description}</span> : <span className={`text-xs font-bold ${newEvent.description.length > 200 ? 'text-rose-500' : 'text-slate-600'}`}>{newEvent.description.length}/200</span>}
                                            </div>
                                            <textarea maxLength={200} value={newEvent.description} onChange={(e) => handleInputChange('description', e.target.value)} placeholder="Dettagli..." className={`${getInputStyle('description')} resize-none flex-1 min-h-[150px]`} />
                                        </div>
                                    </div>

                                    {/* COLONNA DESTRA (Compattata gap-2) */}
                                    <div className="flex flex-col gap-2 md:gap-5 pt-0 md:pt-1 min-w-0">
                                        <div className="space-y-1 md:space-y-2">
                                            <div className="flex justify-between">
                                                <label className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1">Nome Evento *</label>
                                                {formErrors.name ? <span className="text-[10px] md:text-xs font-bold text-rose-500">{formErrors.name}</span> : <span className={`text-[10px] md:text-xs font-bold ${newEvent.name.length > 100 ? 'text-rose-500' : 'text-slate-600'}`}>{newEvent.name.length}/100</span>}
                                            </div>
                                            <input type="text" maxLength={100} value={newEvent.name} onChange={(e) => handleInputChange('name', e.target.value)} placeholder="Es. Erasmus Party" className={getInputStyle('name')} />
                                        </div>

                                        {/* DATA (Fix Layout) */}
                                        <div className="space-y-1 md:space-y-2 w-full min-w-0">
                                            <div className="flex justify-between">
                                                <label className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1 flex items-center gap-1.5"><Clock size={12} /> Data *</label>
                                                {formErrors.dateAndTime && <span className="text-[10px] md:text-xs font-bold text-rose-500">{formErrors.dateAndTime}</span>}
                                            </div>
                                            <input
                                                type="datetime-local"
                                                min={minDateTime}
                                                value={newEvent.dateAndTime}
                                                onChange={(e) => handleInputChange('dateAndTime', e.target.value)}
                                                style={{ boxSizing: 'border-box' }}
                                                className={`${getInputStyle('dateAndTime')} w-full max-w-full appearance-none [&::-webkit-calendar-picker-indicator]:invert [&::-webkit-calendar-picker-indicator]:opacity-50`}
                                            />
                                        </div>

                                        <div className="space-y-1 md:space-y-2 relative z-20">
                                            <div className="flex justify-between">
                                                <label className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1">
                                                    Città *
                                                </label>
                                                {formErrors.city ? (
                                                    <span className="text-[10px] md:text-xs font-bold text-rose-500">
                                                    {formErrors.city}
                                                    </span>
                                                ) : (
                                                    <span className={`text-[10px] md:text-xs font-bold ${
                                                        newEvent.city.length > 30
                                                            ? 'text-rose-500'
                                                            : newEvent.city.length > 0 && !isCityValid()
                                                                ? 'text-amber-500'
                                                                : 'text-slate-600'
                                                    }`}>
                {newEvent.city.length > 0 && !isCityValid()
                    ? 'Non valida'
                    : `${newEvent.city.length}/30`
                }
            </span>
                                                )}
                                            </div>
                                            <div className="relative">
                                                <input
                                                    type="text"
                                                    maxLength={30}
                                                    value={newEvent.city}
                                                    onChange={(e) => {
                                                        handleInputChange('city', e.target.value);
                                                        if(e.target.value.length > 0) setShowCityDropdown(true);
                                                    }}
                                                    onFocus={() => {
                                                        if(newEvent.city.length > 0) setShowCityDropdown(true);
                                                    }}
                                                    onBlur={() => {
                                                        setTimeout(() => setShowCityDropdown(false), 200);
                                                    }}
                                                    placeholder="Cerca..."
                                                    className={`${getInputStyle('city')} ${
                                                        newEvent.city.length > 0 && !isCityValid()
                                                            ? 'border-amber-500/50 focus:border-amber-500'
                                                            : ''
                                                    }`}
                                                />

                                                {/* Icona di validazione */}
                                                {newEvent.city.length > 0 && (
                                                    <div className="absolute right-3 top-1/2 -translate-y-1/2">
                                                        {isCityValid() ? (
                                                            <motion.div
                                                                initial={{ scale: 0 }}
                                                                animate={{ scale: 1 }}
                                                                className="text-green-500"
                                                            >

                                                            </motion.div>
                                                        ) : (
                                                            <motion.div
                                                                initial={{ scale: 0 }}
                                                                animate={{ scale: 1 }}
                                                                className="text-amber-500"
                                                            >
                                                                ⚠
                                                            </motion.div>
                                                        )}
                                                    </div>
                                                )}

                                                <AnimatePresence>
                                                    {showCityDropdown && newEvent.city.length >= 1 && (
                                                        <motion.div
                                                            initial={{ opacity: 0, y: 5 }}
                                                            animate={{ opacity: 1, y: 0 }}
                                                            exit={{ opacity: 0, y: 5 }}
                                                            className="absolute top-full left-0 right-0 mt-2 bg-slate-900 border border-white/10 rounded-xl max-h-40 overflow-y-auto shadow-2xl z-50 scrollbar-thin scrollbar-thumb-white/20"
                                                        >
                                                            <div className="p-1">
                                                                {comuniList
                                                                    .filter(c => c.nome.toLowerCase().startsWith(newEvent.city.toLowerCase()))
                                                                    .slice(0, 50)
                                                                    .map((c, i) => (
                                                                        <button
                                                                            key={i}
                                                                            type="button"
                                                                            className="w-full text-left p-2.5 hover:bg-white/10 rounded-lg text-slate-300 hover:text-white text-sm transition-colors"
                                                                            onClick={() => {
                                                                                setNewEvent({...newEvent, city: c.nome});
                                                                                setShowCityDropdown(false);
                                                                                if (formErrors.city) {
                                                                                    setFormErrors(prev => ({...prev, city: null}));
                                                                                }
                                                                            }}
                                                                        >
                                                                            {c.nome}
                                                                        </button>
                                                                    ))
                                                                }
                                                                {comuniList.filter(c => c.nome.toLowerCase().startsWith(newEvent.city.toLowerCase())).length === 0 && (
                                                                    <div className="p-3 text-center text-slate-500 text-sm">
                                                                        Nessuna città trovata
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </motion.div>
                                                    )}
                                                </AnimatePresence>
                                            </div>
                                        </div>


                                        <div className="grid grid-cols-3 gap-2 md:gap-4">
                                            <div className="col-span-2 space-y-1 md:space-y-2">
                                                <div className="flex justify-between">
                                                    <label className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1">Indirizzo *</label>
                                                    {formErrors.street && <span className="text-[10px] md:text-xs font-bold text-rose-500">!</span>}
                                                </div>
                                                <input type="text" maxLength={60} value={newEvent.street} onChange={(e) => handleInputChange('street', e.target.value)} placeholder="Via Roma 1" className={getInputStyle('street')} />
                                            </div>
                                            <div className="space-y-1 md:space-y-2 relative z-20">
                                                <div className="flex justify-between">
                                                    <label className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1">
                                                        CAP *
                                                    </label>
                                                    {formErrors.CAP ? (
                                                        <span className="text-[10px] md:text-xs font-bold text-rose-500">
                {formErrors.CAP}
            </span>
                                                    ) : (
                                                        <span className={`text-[10px] md:text-xs font-bold ${
                                                            newEvent.CAP.length === 5
                                                                ? 'text-green-500'
                                                                : newEvent.CAP.length > 0
                                                                    ? 'text-amber-500'
                                                                    : 'text-slate-600'
                                                        }`}>
                {newEvent.CAP.length}/5
            </span>
                                                    )}
                                                </div>
                                                <div className="relative">
                                                    <input
                                                        type="text"
                                                        inputMode="numeric"
                                                        pattern="[0-9]*"
                                                        maxLength={5}
                                                        value={newEvent.CAP}
                                                        onChange={(e) => {
                                                            const value = e.target.value.replace(/\D/g, '');
                                                            handleInputChange('CAP', value);
                                                        }}
                                                        placeholder="12345"
                                                        className={getInputStyle('CAP')}
                                                    />


                                                </div>
                                            </div>

                                        </div>

                                        {(formErrors.street || formErrors.CAP) && (
                                            <div className="flex gap-4">
                                                <div className="col-span-2 w-2/3">{formErrors.street && <p className="text-[10px] text-rose-500 ml-1">{formErrors.street}</p>}</div>
                                                <div className="col-span-1 w-1/3">{formErrors.CAP && <p className="text-[10px] text-rose-500 ml-1">{formErrors.CAP}</p>}</div>
                                            </div>
                                        )}

                                        <div className="space-y-1 flex flex-col md:hidden">
                                            <div className="flex justify-between">
                                                <label className="text-[10px] font-bold text-slate-500 uppercase ml-1">Descrizione *</label>
                                                {formErrors.description ? <span className="text-[10px] font-bold text-rose-500">{formErrors.description}</span> : <span className={`text-[10px] font-bold ${newEvent.description.length > 200 ? 'text-rose-500' : 'text-slate-600'}`}>{newEvent.description.length}/200</span>}
                                            </div>
                                            <textarea maxLength={200} rows={2} value={newEvent.description} onChange={(e) => handleInputChange('description', e.target.value)} placeholder="Dettagli..." className={`${getInputStyle('description')} resize-none py-1.5`} />
                                        </div>
                                    </div>
                                </div>
                            </form>
                            <div className="p-3 md:p-6 border-t border-white/5 bg-[#0F131F] shrink-0 pb-6 md:pb-6">
                                <button onClick={handleSubmit} className="w-full py-3 md:py-4 rounded-xl font-bold bg-white text-black hover:bg-slate-200 transition-colors shadow-lg uppercase tracking-wide text-sm active:scale-[0.99]">Pubblica Evento</button>
                            </div>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>
        </div>
    );
};

export default EventsPage;