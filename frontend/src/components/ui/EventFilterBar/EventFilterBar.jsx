import React, { useState, useRef, useEffect, forwardRef } from 'react';
import { Search, MapPin, ChevronDown, Check, X, Plus } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

import comuniDataRaw from '../../../data/comuni.json';

const EventFilterBar = forwardRef(({
                                       searchQuery,
                                       setSearchQuery,
                                       selectedCity,
                                       setSelectedCity,
                                       onActionClick,
                                       showAction
                                   }, ref) => {
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [citySearch, setCitySearch] = useState("");
    const dropdownRef = useRef(null);

    const comuniList = Array.isArray(comuniDataRaw) ? comuniDataRaw : (comuniDataRaw?.default || []);
    const showSuggestions = citySearch.length >= 2;
    const filteredComuni = showSuggestions
        ? comuniList.filter(c => c.nome && c.nome.toLowerCase().startsWith(citySearch.toLowerCase())).slice(0, 50)
        : [];

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsDropdownOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    return (
        <div className="w-full max-w-3xl mx-auto relative z-[60]" ref={ref}> {/* Ref per il FAB */}
            <div ref={dropdownRef} className="flex flex-col w-full bg-slate-900/40 backdrop-blur-2xl border border-white/10 rounded-[2rem] shadow-2xl transition-all duration-300 hover:bg-slate-900/50 focus-within:bg-slate-900/60 focus-within:border-younify-blue/50">

                {/* --- SEZIONE MOBILE: LAYOUT A COLONNA (STACK) --- */}
                <div className="flex flex-col md:hidden">
                    {/* 1. Crea Evento (Solo Host) */}
                    {showAction && (
                        <button
                            onClick={onActionClick}
                            className="w-full py-4 flex items-center justify-center gap-2 bg-younify-blue/10 active:bg-younify-blue/20 border-b border-white/5 text-younify-blue font-bold text-sm uppercase tracking-wide first:rounded-t-[2rem]"
                        >
                            <Plus size={18} strokeWidth={3} />
                            Crea Evento
                        </button>
                    )}

                    {/* 2. Cerca */}
                    <div className="relative flex items-center h-14 px-4 border-b border-white/5 group">
                        <Search className="text-slate-400 group-focus-within:text-younify-blue transition-colors shrink-0 mr-3" size={18} />
                        <input
                            type="text"
                            placeholder="Cerca eventi..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="w-full h-full bg-transparent border-none outline-none text-slate-200 placeholder:text-slate-500 text-sm font-medium"
                        />
                        {searchQuery && (
                            <button onClick={() => setSearchQuery("")} className="p-1 bg-white/10 rounded-full"><X size={12} className="text-slate-300" /></button>
                        )}
                    </div>

                    {/* 3. Città */}
                    <div className="relative h-14">
                        <button
                            onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                            className="w-full h-full flex items-center justify-between px-4 hover:bg-white/5 last:rounded-b-[2rem] transition-colors"
                        >
                            <div className="flex items-center gap-3">
                                <div className={`p-1.5 rounded-full ${selectedCity ? 'bg-younify-blue text-white' : 'bg-white/5 text-slate-400'}`}>
                                    <MapPin size={16} />
                                </div>
                                <span className={`text-sm font-bold ${selectedCity ? "text-white" : "text-slate-400"}`}>
                                    {selectedCity || "Tutta Italia"}
                                </span>
                            </div>
                            <ChevronDown size={16} className={`text-slate-500 transition-transform ${isDropdownOpen ? 'rotate-180 text-younify-blue' : ''}`} />
                        </button>
                    </div>
                </div>

                {/* --- SEZIONE DESKTOP: LAYOUT ORIGINALE (RIGA) --- */}
                <div className="hidden md:flex flex-col w-full">
                    {showAction && (
                        <div className="w-full border-b border-white/5 relative z-10">
                            <button onClick={onActionClick} className="w-full py-4 flex items-center justify-center gap-2 bg-white/5 hover:bg-white/10 text-white font-bold text-sm uppercase rounded-t-[2rem]">
                                <Plus size={18} className="text-younify-blue" /> Crea Nuovo Evento
                            </button>
                        </div>
                    )}
                    <div className="flex items-center w-full h-16 px-2 relative z-20">
                        <div className="flex-1 relative flex items-center h-full pl-4 group">
                            <Search className="text-slate-400 group-focus-within:text-younify-blue shrink-0" size={20} />
                            <input type="text" placeholder="Cerca eventi..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} className="w-full h-full bg-transparent outline-none text-slate-200 px-3 text-base font-medium" />
                            {searchQuery && <button onClick={() => setSearchQuery("")} className="p-1 hover:bg-white/10 rounded-full mr-2"><X size={14} className="text-slate-400" /></button>}
                        </div>
                        <div className="w-px h-8 bg-white/10 mx-2" />
                        <div className="relative flex-[0.6]">
                            <button onClick={() => setIsDropdownOpen(!isDropdownOpen)} className="w-full h-12 flex items-center justify-between px-4 hover:bg-white/5 rounded-xl group text-left">
                                <div className="flex items-center gap-3">
                                    <div className={`p-1.5 rounded-full ${selectedCity ? 'bg-younify-blue text-white' : 'bg-white/5 text-slate-400'}`}><MapPin size={16} /></div>
                                    <div className="flex flex-col"><span className="text-[10px] font-bold text-slate-500 uppercase">Dove</span><span className={`text-sm font-bold ${selectedCity ? "text-white" : "text-slate-400"}`}>{selectedCity || "Tutta Italia"}</span></div>
                                </div>
                                <ChevronDown size={16} className={`text-slate-500 transition-transform ${isDropdownOpen ? 'rotate-180 text-younify-blue' : ''}`} />
                            </button>
                        </div>
                    </div>
                </div>

                {/* DROPDOWN MENU (CONDIVISO) */}
                <AnimatePresence>
                    {isDropdownOpen && (
                        <motion.div
                            initial={{ opacity: 0, y: 10, scale: 0.98 }}
                            animate={{ opacity: 1, y: 0, scale: 1 }}
                            exit={{ opacity: 0, y: 10, scale: 0.98 }}
                            className="absolute top-full right-0 mt-4 w-full md:w-[350px] bg-[#0F131F] border border-white/10 rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[400px] z-[100]"
                        >
                            <div className="p-4 border-b border-white/5 bg-[#0F131F] sticky top-0 z-10">
                                <div className="relative">
                                    <input type="text" autoFocus placeholder="Digita comune..." value={citySearch} onChange={(e) => setCitySearch(e.target.value)} className="w-full bg-white/5 border border-white/10 rounded-xl py-3 pl-10 pr-4 text-sm text-white focus:outline-none focus:border-younify-blue" />
                                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" size={16} />
                                </div>
                            </div>
                            <div className="overflow-y-auto p-2 scrollbar-thin">
                                <button onClick={() => { setSelectedCity(""); setIsDropdownOpen(false); }} className="w-full text-left px-4 py-3 rounded-xl hover:bg-white/5 text-slate-300 text-sm font-medium mb-1 flex items-center gap-2">
                                    🌍 <span className={!selectedCity ? "font-bold text-white" : ""}>Tutta Italia</span>{!selectedCity && <Check size={14} className="ml-auto text-younify-blue" />}
                                </button>
                                {showSuggestions && <div className="h-px bg-white/5 my-2 mx-4" />}
                                {showSuggestions ? (
                                    filteredComuni.length > 0 ? (
                                        filteredComuni.map((comune, idx) => (
                                            <button key={idx} onClick={() => { setSelectedCity(comune.nome); setIsDropdownOpen(false); }} className={`w-full flex items-center justify-between px-4 py-3 rounded-xl hover:bg-white/5 text-sm mb-1 ${selectedCity === comune.nome ? 'bg-younify-blue/10 text-younify-blue font-bold' : 'text-slate-300'}`}>
                                                <span>{comune.nome} <span className="text-slate-500 text-xs font-normal">({comune.provincia.nome})</span></span>{selectedCity === comune.nome && <Check size={16} />}
                                            </button>
                                        ))
                                    ) : (<div className="p-4 text-center text-slate-500 text-xs italic">Nessun comune trovato.</div>)
                                ) : (citySearch.length > 0 && <div className="p-4 text-center text-slate-500 text-xs">Digita almeno 2 caratteri...</div>)}
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>
            </div>
        </div>
    );
});

export default EventFilterBar;