import React, { useState, useRef, useEffect, useLayoutEffect, useMemo } from 'react';
import {
    Plus, ChevronDown, X, Send, MapPin, Hash, Check, Sparkles,
    BookOpen, Home, Coffee, TrendingUp, Users, PenLine, Filter, Layers
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { toast } from 'react-toastify';

import AppNavbar from '../../components/layout/Navbar/AppNavbar';
import TextType from '../../components/ui/TextType/TextType';
import ThreadCard from '../../components/ui/ThreadCard/ThreadCard';

import { threadService } from '../../services/threadService';
import { UNIVERSITY_DATA } from '../../data/universities';

// === HELPER: Calcolo tempo trascorso ===
const calculateTimeAgo = (dateString) => {
    if(!dateString) return "Recentemente";
    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now - date) / 1000);
    if (diffInSeconds < 60) return "Adesso";
    if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m fa`;
    if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h fa`;
    return `${Math.floor(diffInSeconds / 86400)}g fa`;
};

const getTopicIcon = (topicLabel) => {
    switch (topicLabel) {
        case "Esami": return <BookOpen size={16} />;
        case "Affitti": return <Home size={16} />;
        case "Vita Sociale": return <Coffee size={16} />;
        case "Appunti": return <TrendingUp size={16} />;
        case "Docenti": return <Users size={16} />;
        default: return <Hash size={16} />;
    }
};

const CustomDropdown = ({ options, value, onChange, icon: Icon, placeholder }) => {
    const [isOpen, setIsOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const dropdownRef = useRef(null);
    return (
        <div className="relative w-full" ref={dropdownRef}>
            <button type="button" onClick={() => setIsOpen(!isOpen)} className={`w-full flex items-center justify-between bg-white/5 hover:bg-white/10 backdrop-blur-md border border-white/10 rounded-2xl px-4 py-3 text-sm font-medium transition-all duration-300 group ${isOpen ? 'border-younify-blue ring-1 ring-younify-blue bg-white/10' : ''}`}>
                <div className="flex items-center gap-3 overflow-hidden">
                    {Icon && <Icon size={16} className="text-slate-400 group-hover:text-younify-blue transition-colors shrink-0" />}
                    <span className={`truncate ${value === placeholder ? 'text-slate-500' : 'text-white'}`}>{value}</span>
                </div>
                <ChevronDown size={14} className={`text-slate-500 transition-transform duration-300 ${isOpen ? 'rotate-180 text-younify-blue' : ''}`} />
            </button>
            <AnimatePresence>
                {isOpen && (
                    <motion.div initial={{ opacity: 0, y: 5, height: 0 }} animate={{ opacity: 1, y: 0, height: 'auto' }} exit={{ opacity: 0, y: 5, height: 0 }} className="absolute top-full left-0 mt-2 w-full bg-[#0F131F] border border-white/20 rounded-xl shadow-[0_10px_40px_rgba(0,0,0,0.5)] z-[100] max-h-60 overflow-hidden flex flex-col ring-1 ring-black/50">
                        {options.length > 10 && (<div className="p-2 border-b border-white/5"><input type="text" placeholder="Cerca..." className="w-full bg-white/5 text-xs text-white p-2 rounded-lg border border-white/10 focus:outline-none focus:border-younify-blue" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} autoFocus /></div>)}
                        <div className="overflow-y-auto scrollbar-thin scrollbar-thumb-slate-700 scrollbar-track-transparent">
                            {options.filter(opt => opt.toLowerCase().includes(searchTerm.toLowerCase())).map((option, idx) => (
                                <div key={idx} onClick={() => { onChange(option); setIsOpen(false); setSearchTerm(""); }} className={`px-4 py-3 text-xs cursor-pointer hover:bg-white/10 flex justify-between items-center transition-colors border-b border-white/5 last:border-0 ${value === option ? 'text-younify-blue font-bold bg-younify-blue/10' : 'text-slate-300'}`}>{option}{value === option && <Check size={14} />}</div>
                            ))}
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};

const ThreadPage = () => {
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [isLoadingMore, setIsLoadingMore] = useState(false);

    const [threads, setThreads] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [flags, setFlags] = useState([]);

    const [activeUni, setActiveUni] = useState("Tutte le Università");
    const [activeTopic, setActiveTopic] = useState("Tutti gli argomenti");

    const [isTopicExpanded, setIsTopicExpanded] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);

    const [newThreadContent, setNewThreadContent] = useState("");
    const [newThreadTopicValue, setNewThreadTopicValue] = useState("GENERAL");

    const MAX_CHARS = 500;

    const UNIVERSITIES = useMemo(() => {
        const flatList = Object.values(UNIVERSITY_DATA).flat().sort();
        return ["Tutte le Università", ...flatList];
    }, []);

    const mapThreadToFrontend = (dto) => {
        const flagObj = flags.find(f => f.value === dto.flag);

        return {
            id: dto.ID || dto.id || dto.threadId,
            content: dto.content,
            university: dto.University || dto.university || "Generale",

            topic: flagObj?.label || dto.flag || "Generale",      // Per visualizzazione
            topicValue: dto.flag || flagObj?.value || "GENERALE", // Per filtro

            author: dto.userUsername || dto.hostUsername || dto.authorName || "Anonimo",
            timeAgo: calculateTimeAgo(dto.publicationDate || dto.creationDate || dto.timestamp),
            votes: dto.CFU || dto.cfu || 0,
            userVote: dto.voteType || null,
            profileImage: dto.profileImage || null,
        };
    };

    // 1. CARICAMENTO FLAGS (VERSIONE DEBUG)
    useEffect(() => {
        const fetchFlags = async () => {
            try {
                const res = await threadService.getFlags();

                // DEBUG: Guarda come scendiamo di due livelli
                // res = Oggetto Axios
                // res.data = Body della risposta (ApiResponse)
                // res.data.data = L'array vero e proprio
                const responseBody = res.data;
                const flagsArray = responseBody?.data;

                if (Array.isArray(flagsArray)) {
                    setFlags(flagsArray);

                    // Imposta il default
                    if (flagsArray.length > 0) {
                        setNewThreadTopicValue(flagsArray[0].value);
                    }
                }

            } catch (error) {
                setFlags([{value: 'GENERAL', label: 'Generale'}]);
            }
        };
        fetchFlags();
    }, []);

    const loadThreads = async (reset = false) => {
        const nextPage = reset ? 0 : page;

        if (!hasMore && !reset) return;

        if (reset) {
            setIsLoading(true);
            setPage(0);
            setHasMore(true);
        } else {
            setIsLoadingMore(true);
        }

        try {
            const res = await threadService.getThreadsByDate(nextPage, 10);

            const { content, last } = res.data.data;

            let filteredThreads = content.map(mapThreadToFrontend);

            const hasUniFilter = activeUni !== "Tutte le Università";
            const hasTopicFilter = activeTopic !== "Tutti gli argomenti";

            if (hasUniFilter) {
                filteredThreads = filteredThreads.filter(t =>
                    t?.university?.toLowerCase().includes(activeUni.toLowerCase())
                );
            }

            if (hasTopicFilter) {
                const selectedFlagValue = flags.find(f => f.label === activeTopic)?.value;

                filteredThreads = filteredThreads.filter(t => {
                    const match = t?.topicValue === selectedFlagValue;
                    return match;
                });
            }

            const uniqueThreads = filteredThreads.filter((thread, index, self) =>
                index === self.findIndex(t => t.id === thread.id)
            );

            setThreads(prev => reset ? uniqueThreads : [...prev, ...uniqueThreads]);
            setPage(nextPage + 1);
            setHasMore(!last);

        } catch (error) {
            if (reset) setThreads([]);
            setHasMore(false);
        } finally {
            setIsLoading(false);
            setIsLoadingMore(false);
        }
    };

    useEffect(() => {
        // 👇 RESETTA TUTTO quando cambi filtro
        setPage(0);
        setThreads([]);
        setHasMore(true);
        loadThreads(true);
    }, [activeUni, activeTopic, flags]);


    useEffect(() => {
        const handleScrollWindow = () => {
            const scrollPosition = window.innerHeight + window.scrollY;
            const threshold = document.body.offsetHeight - 300;

            if (scrollPosition >= threshold && !isLoadingMore && hasMore && !isLoading) {
                loadThreads(false);
            }
        };

        window.addEventListener('scroll', handleScrollWindow);
        return () => window.removeEventListener('scroll', handleScrollWindow);
    }, [hasMore, isLoadingMore, isLoading, page, activeUni, activeTopic, flags]);

    useEffect(() => {
        const handleScroll = () => {
            // Controlla ENTRAMBI i ref (desktop + mobile)
            const desktop = desktopScrollRef.current;
            const mobile = mobileScrollRef.current;
            const currentScroll = desktop || mobile;

            if (!currentScroll) return;

            const scrollPosition = currentScroll.scrollTop + currentScroll.clientHeight;
            const threshold = currentScroll.scrollHeight - 300;

            if (scrollPosition >= threshold && !isLoadingMore && hasMore && !isLoading) {
                loadThreads(false);
            }
        };

        // Aggiungi listener AI TUOI REF esistenti
        if (desktopScrollRef.current) {
            desktopScrollRef.current.addEventListener('scroll', handleScroll);
        }
        if (mobileScrollRef.current) {
            mobileScrollRef.current.addEventListener('scroll', handleScroll);
        }

        return () => {
            if (desktopScrollRef.current) {
                desktopScrollRef.current.removeEventListener('scroll', handleScroll);
            }
            if (mobileScrollRef.current) {
                mobileScrollRef.current.removeEventListener('scroll', handleScroll);
            }
        };
    }, [hasMore, isLoadingMore, isLoading, page, activeUni, activeTopic, flags]);
    const handlePublish = async (e) => {
        e.preventDefault();
        const payload = {
            content: newThreadContent,
            flag: newThreadTopicValue
        };

        try {
            const res = await threadService.createThread(payload);

            // Controlla sia res che res.data per internalCode
            const responseData = res.data || res;
            const isSuccess =
                responseData.internalCode === 'POSTED_SUCCESSFULLY' ||
                res.status === 200 ||
                res.status === 201;

            if (isSuccess) {
                toast.success("Thread pubblicato!");
                setNewThreadContent("");
                setIsModalOpen(false);

                // IMPORTANTE: Resetta i filtri prima di ricaricare
                setActiveUni("Tutte le Università");
                setActiveTopic("Tutti gli argomenti");

                // Aspetta che i filtri si aggiornino
                setTimeout(async () => {
                    await loadThreads();
                }, 200);
            }
        } catch (error) {
            const msg = error.response?.data?.message || "Errore nella pubblicazione";
            toast.error(msg);
        }
    };




    // --- Gestione scroll e layout mobile ---
    const [isFabOpen, setIsFabOpen] = useState(false);
    const [showFab, setShowFab] = useState(false);
    const filtersRef = useRef(null);
    const desktopScrollRef = useRef(null);
    const mobileScrollRef = useRef(null);

    useLayoutEffect(() => {
        const savedPosition = sessionStorage.getItem("threadPageScroll");
        if (savedPosition) {
            if (desktopScrollRef.current) desktopScrollRef.current.scrollTop = parseInt(savedPosition);
            if (mobileScrollRef.current) mobileScrollRef.current.scrollTop = parseInt(savedPosition);
        }
    }, []);

    const handleScrollSave = (e) => {
        sessionStorage.setItem("threadPageScroll", e.target.scrollTop);
    };


    useEffect(() => {
        const observer = new IntersectionObserver(
            ([entry]) => {
                setShowFab(!entry.isIntersecting);
                if (entry.isIntersecting) setIsFabOpen(false);
            },
            { threshold: 0.1 }
        );
        if (filtersRef.current) observer.observe(filtersRef.current);
        return () => { if (filtersRef.current) observer.unobserve(filtersRef.current); };
    }, []);

    const scrollToFilters = () => {
        if (filtersRef.current) {
            filtersRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
            setIsFabOpen(false);
        }
    };

    const MobileFab = (
        <div className={`relative transition-all duration-300 ${showFab ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10 pointer-events-none'}`}>
            <AnimatePresence>
                {isFabOpen && (
                    <div className="absolute bottom-full right-0 mb-4 flex flex-col items-end gap-3 min-w-[220px]">
                        <motion.button initial={{ opacity: 0, y: 10, scale: 0.8 }} animate={{ opacity: 1, y: 0, scale: 1 }} exit={{ opacity: 0, y: 10, scale: 0.8 }} onClick={scrollToFilters} className="flex items-center gap-2 bg-slate-800 text-white px-4 py-2 rounded-full shadow-lg border border-white/10 font-bold text-xs whitespace-nowrap">
                            <span>Filtra per Ateneo e/o Topic</span>
                            <div className="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center text-younify-light"><Filter size={16} /></div>
                        </motion.button>
                        <motion.button initial={{ opacity: 0, y: 10, scale: 0.8 }} animate={{ opacity: 1, y: 0, scale: 1 }} exit={{ opacity: 0, y: 10, scale: 0.8 }} transition={{ delay: 0.1 }} onClick={() => { setIsModalOpen(true); setIsFabOpen(false); }} className="flex items-center gap-2 bg-white text-slate-900 px-4 py-2 rounded-full shadow-lg font-bold text-xs whitespace-nowrap">
                            <span>Nuovo Thread</span>
                            <div className="w-8 h-8 rounded-full bg-slate-200 flex items-center justify-center text-slate-900"><PenLine size={16} /></div>
                        </motion.button>
                    </div>
                )}
            </AnimatePresence>
            <motion.button whileTap={{ scale: 0.9 }} onClick={() => setIsFabOpen(!isFabOpen)} className={`w-[60px] h-[60px] rounded-[22px] shadow-xl flex items-center justify-center text-white border border-white/20 transition-all ${isFabOpen ? 'bg-slate-800 rotate-45' : 'bg-gradient-to-tr from-younify-blue to-purple-600'}`}>
                <Plus size={28} strokeWidth={2.5} />
            </motion.button>
        </div>
    );

    return (
        <div className="fixed inset-0 w-full h-full font-sans text-slate-200 selection:bg-younify-blue/30 selection:text-white overflow-hidden flex bg-slate-900/60 backdrop-blur-md lg:bg-transparent lg:backdrop-blur-none">
            <AppNavbar mobileAction={MobileFab} shouldShrink={showFab} />
            <div className="relative z-10 flex-1 flex flex-col h-full pl-0 lg:pl-32 pr-0 lg:pr-8 pt-0 lg:pt-24">
                <div className="hidden lg:flex max-w-7xl mx-auto w-full h-full flex flex-col">
                    <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.8 }} className="shrink-0 flex items-baseline justify-start gap-2 mb-8 select-none">
                        <h1 className="text-5xl md:text-6xl font-black text-white font-inter tracking-tighter leading-none">YOUni<span className="text-younify-blue drop-shadow-[0_0_15px_rgba(29,78,216,0.6)]">Fy</span></h1>
                        <div className="flex items-baseline text-3xl font-mono text-slate-500 font-bold"><span className="text-younify-blue mr-1">/</span><TextType text={["thread"]} className="text-slate-400" cursorClassName="text-younify-blue" typingSpeed={100} loop={true} /></div>
                    </motion.div>

                    <div className="flex flex-col lg:flex-row gap-8 items-start flex-1 min-h-0 overflow-hidden">
                        <div className="w-full lg:w-64 shrink-0 h-full overflow-y-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:'none'] [scrollbar-width:'none'] pb-4">
                            <h3 className="text-slate-500 font-bold text-xs uppercase tracking-widest mb-4 pl-2 sticky top-0 bg-transparent z-10">Argomenti</h3>
                            <div className="flex flex-col gap-2">
                                {/* 1. Tasto "Tutti gli argomenti" (Stile Originale) */}
                                <button
                                    onClick={() => setActiveTopic("Tutti gli argomenti")}
                                    className={`relative flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-300 shrink-0 whitespace-nowrap text-left z-10 ${activeTopic === "Tutti gli argomenti" ? 'bg-younify-blue text-white shadow-lg shadow-younify-blue/20' : 'bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white border border-transparent hover:border-white/5'}`}
                                >
    <span className={activeTopic === "Tutti gli argomenti" ? 'text-white' : 'text-slate-500'}>
        <Layers size={16} />
    </span>
                                    <span className="flex-1">Tutti gli argomenti</span>
                                    {activeTopic === "Tutti gli argomenti" && <motion.div layoutId="activeTopic" className="absolute inset-0 rounded-xl border border-white/20" />}
                                </button>

                                {/* 2. Lista Dinamica Flags (Stile Originale + Logica Label/Value) */}
                                <motion.div
                                    initial={false}
                                    animate={{ height: isTopicExpanded ? 'auto' : 0, opacity: isTopicExpanded ? 1 : 0 }}
                                    transition={{ duration: 0.4, ease: [0.04, 0.62, 0.23, 0.98] }}
                                    className="overflow-hidden flex flex-col gap-2"
                                >
                                    {/* CONTROLLO SICUREZZA: flags esiste ed è un array? */}
                                    {flags && flags.length > 0 ? (
                                        flags.map((flag, index) => {
                                            // LOGICA ROBUSTA: Gestisce sia oggetti {label, value} che stringhe semplici
                                            const label = flag.label || flag;
                                            const value = flag.value || flag;
                                            const isActive = activeTopic === label;

                                            return (
                                                <button
                                                    key={value + index} // Usa il value come chiave unica
                                                    onClick={() => setActiveTopic(label)} // Imposta il filtro usando la Label
                                                    className={`relative flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-300 shrink-0 whitespace-nowrap text-left ${isActive ? 'bg-younify-blue text-white shadow-lg shadow-younify-blue/20' : 'bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white border border-transparent hover:border-white/5'}`}
                                                >
                    <span className={isActive ? 'text-white' : 'text-slate-500'}>
                        {getTopicIcon(label)}
                    </span>
                                                    <span className="flex-1">{label}</span>

                                                    {/* Elemento grafico per l'effetto di selezione */}
                                                    {isActive && <motion.div layoutId="activeTopic" className="absolute inset-0 rounded-xl border border-white/20" />}
                                                </button>
                                            );
                                        })
                                    ) : (
                                        // Fallback in caso di array vuoto (mantiene lo stile base)
                                        <div className="text-slate-500 text-xs p-4 text-center border border-white/5 rounded-xl bg-white/5">
                                            Nessun argomento disponibile
                                        </div>
                                    )}
                                </motion.div>
                                <motion.button layout onClick={() => setIsTopicExpanded(!isTopicExpanded)} className="flex items-center gap-2 px-4 py-2 text-xs font-bold text-younify-blue hover:text-white transition-colors mt-1 ml-1">{isTopicExpanded ? (<>Meno <ChevronDown className="rotate-180" size={14} /></>) : (<>Vedi tutti <ChevronDown size={14} /></>)}</motion.button>
                            </div>
                        </div>

                        <div className="flex-1 flex flex-col min-w-0 h-full overflow-hidden relative z-0">
                            <div className="shrink-0 bg-[#0F131F]/60 backdrop-blur-2xl border border-white/10 p-3 rounded-3xl shadow-xl mb-6 flex gap-3 items-center w-full ring-1 ring-white/5 z-40 relative">
                                <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-white/20 to-transparent opacity-30 pointer-events-none rounded-3xl" />
                                <div className="flex-1"><CustomDropdown options={UNIVERSITIES} value={activeUni} onChange={setActiveUni} icon={MapPin} placeholder="Università" /></div>
                                <motion.button whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} onClick={() => setIsModalOpen(true)} className="bg-white text-slate-950 px-5 py-3 rounded-2xl font-bold flex items-center justify-center gap-2 shadow-lg shadow-white/10 hover:bg-slate-200 transition-colors whitespace-nowrap"><Plus size={20} strokeWidth={3} className="text-younify-blue" /><span className="hidden sm:inline">Nuovo Thread</span></motion.button>
                            </div>

                            <div className="absolute top-[calc(3rem+1.5rem+2px)] left-0 right-0 h-32 z-20 pointer-events-none backdrop-blur-xl [mask-image:linear-gradient(to_bottom,black,transparent)]"></div>

                            <div ref={desktopScrollRef} onScroll={handleScrollSave} className="flex-1 min-h-0 overflow-y-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:'none'] [scrollbar-width:'none'] pr-2 pb-20 [mask-image:linear-gradient(to_bottom,transparent_0%,black_15%,black_85%,transparent_100%)]">
                                <div className="flex flex-col gap-6 max-w-3xl mx-auto relative z-0 py-8">
                                    <AnimatePresence>
                                        {isLoading ? (
                                            <div className="text-center p-10 text-slate-500">Caricamento...</div>
                                        ) : threads.length > 0 ? (
                                            threads
                                                .filter(t => t && t.id)
                                                .map(thread => (
                                                    <ThreadCard
                                                        key={thread.id}
                                                        thread={thread}
                                                        layoutId={`thread-card-${thread.id}`}
                                                        onVoteSuccess={() => {
                                                            setPage(0);
                                                            setThreads([]);
                                                            setHasMore(true);
                                                            loadThreads(true);
                                                        }}
                                                    />
                                                ))
                                        ) : (
                                            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="col-span-full py-20 text-center flex flex-col items-center justify-center text-slate-500 border border-dashed border-white/10 rounded-3xl"><Sparkles size={40} className="mb-4 text-slate-600" /><p>Nessun thread trovato.</p><button onClick={() => { setActiveUni("Tutte le Università"); setActiveTopic("Tutti gli argomenti"); loadThreads(); }} className="text-younify-blue font-bold hover:underline mt-2">Resetta filtri</button></motion.div>
                                        )}
                                    </AnimatePresence>
                                    {isLoadingMore && (
                                        <div className="text-center text-slate-400 py-4 text-sm">
                                            Carico altri thread...
                                        </div>
                                    )}

                                    {!hasMore && threads.length > 0 && (
                                        <div className="text-center text-slate-500 py-4 text-xs">
                                            Hai raggiunto il primo thread.
                                        </div>
                                    )}
                                </div>
                            </div>
                            <div className="absolute bottom-0 left-0 right-0 h-40 z-20 pointer-events-none backdrop-blur-xl [mask-image:linear-gradient(to_top,black,transparent)]"></div>
                        </div>
                    </div>
                </div>
            </div>

            <AnimatePresence>
                {isModalOpen && (
                    <div className="fixed inset-0 z-[100] flex items-center justify-center px-4">
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setIsModalOpen(false)} className="absolute inset-0 bg-slate-950/80 backdrop-blur-sm" />
                        <motion.div initial={{ scale: 0.9, opacity: 0, y: 50 }} animate={{ scale: 1, opacity: 1, y: 0 }} exit={{ scale: 0.9, opacity: 0, y: 50 }} className="relative w-full max-w-lg bg-[#0B0F19] border border-white/10 rounded-[2rem] p-6 shadow-2xl overflow-hidden">
                            <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-younify-blue via-purple-500 to-younify-blue"></div>
                            <div className="flex justify-between items-center mb-6"><h3 className="text-xl font-black text-white font-inter">Nuovo Thread</h3><button onClick={() => setIsModalOpen(false)} className="p-2 rounded-full hover:bg-white/10 text-slate-400 hover:text-white transition-colors"><X size={20} /></button></div>
                            <form onSubmit={handlePublish}>
                                <div className="mb-4">
                                    <label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest mb-2 block">Topic</label>
                                    <div className="flex flex-wrap gap-2">
                                        {flags.map(f => (
                                            <button key={f.value} type="button" onClick={() => setNewThreadTopicValue(f.value)} className={`px-3 py-1.5 rounded-lg text-xs font-bold border transition-all ${newThreadTopicValue === f.value ? 'bg-younify-blue border-younify-blue text-white shadow-lg shadow-blue-500/20' : 'bg-white/5 border-white/10 text-slate-400 hover:text-white hover:bg-white/10'}`}>{f.label}</button>
                                        ))}
                                    </div>
                                </div>
                                <div className="relative mb-6">
                                    <div className="flex justify-between mb-2"><label className="text-[10px] font-bold text-slate-500 uppercase tracking-widest block">Messaggio</label><span className="text-[10px] font-mono text-slate-600">{newThreadContent.length}/{MAX_CHARS}</span></div>
                                    <textarea value={newThreadContent} onChange={(e) => setNewThreadContent(e.target.value)} maxLength={MAX_CHARS} placeholder="Scrivi qualcosa..." rows={5} className="w-full bg-slate-950/50 border border-white/10 text-white rounded-2xl p-4 focus:outline-none focus:border-younify-blue focus:ring-1 focus:ring-younify-blue transition-all resize-none text-sm placeholder:text-slate-600 shadow-inner" />
                                </div>
                                <button type="submit" disabled={!newThreadContent.trim()} className="mt-2 w-full bg-younify-blue hover:bg-blue-600 disabled:opacity-50 text-white font-bold py-3.5 rounded-xl flex items-center justify-center gap-2 shadow-lg shadow-blue-900/20 active:scale-95 transition-all"><Send size={18} /> Pubblica</button>
                            </form>
                        </motion.div>
                    </div>
                )}
            </AnimatePresence>

            <div ref={mobileScrollRef} onScroll={handleScrollSave} className="lg:hidden w-full h-full overflow-y-auto [&::-webkit-scrollbar]:hidden pb-28">
                <div className="px-4 pt-8 pb-4 flex flex-col gap-6">
                    <div className="flex items-baseline justify-center gap-1.5 select-none">
                        <h1 className="text-3xl font-black text-white font-inter tracking-tighter leading-none">YOUni<span className="text-younify-blue drop-shadow-[0_0_15px_rgba(29,78,216,0.6)]">Fy</span></h1>
                        <div className="flex items-baseline text-xl font-mono font-bold text-slate-500"><span className="text-younify-blue mr-0.5">/</span><TextType text={["thread"]} className="text-slate-400" cursorClassName="text-younify-blue" typingSpeed={100} loop={true} /></div>
                    </div>

                    <div ref={filtersRef} className="bg-[#0F131F]/60 backdrop-blur-xl border border-white/10 p-3 rounded-3xl shadow-xl relative z-10 flex flex-col gap-3">
                        <div className="w-full"><CustomDropdown options={UNIVERSITIES} value={activeUni} onChange={setActiveUni} icon={MapPin} placeholder="Università" /></div>
                        <div className="w-full flex gap-2">
                            <div className="flex-1"><CustomDropdown options={["Tutti gli argomenti", ...flags.map(f => f.label)]} value={activeTopic} onChange={setActiveTopic} icon={Hash} placeholder="Argomento" /></div>
                            <button onClick={() => setIsModalOpen(true)} className="bg-white text-slate-900 w-12 rounded-2xl flex items-center justify-center shadow-lg active:scale-95 transition-transform shrink-0"><Plus size={20} strokeWidth={3} /></button>
                        </div>
                    </div>

                    <div className="flex flex-col gap-5 relative z-0">
                        <AnimatePresence mode="popLayout">
                            {isLoading ? (<p className="text-center text-slate-500">Caricamento...</p>) : threads.length > 0 ? (
                                threads
                                    .filter(t => t && t.id)
                                    .map(thread => (
                                        <ThreadCard
                                            key={thread.id}
                                            thread={thread}
                                            onVoteSuccess={() => {
                                                setPage(0);
                                                setThreads([]);
                                                setHasMore(true);
                                                loadThreads(true);
                                            }}
                                        />
                                    ))
                            ) : (
                                <div className="py-10 text-center text-slate-500 border border-dashed border-white/10 rounded-3xl"><p className="text-sm">Nessun thread trovato.</p></div>
                            )}
                        </AnimatePresence>
                        {isLoadingMore && (
                            <div className="text-center text-slate-400 py-4 text-sm">
                                Carico altri thread...
                            </div>
                        )}

                        {!hasMore && threads.length > 0 && (
                            <div className="text-center text-slate-500 py-4 text-xs">
                                Hai raggiunto il primo thread.
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ThreadPage;