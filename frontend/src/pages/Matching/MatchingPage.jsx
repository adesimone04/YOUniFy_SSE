import React, { useState, useEffect, useMemo, useRef } from 'react';
import { RotateCcw, X, Heart, GraduationCap, BookOpen, Filter, Search, ChevronRight, SlidersHorizontal, Sparkles, Star } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

// Imports Componenti
import AppNavbar from '../../components/layout/Navbar/AppNavbar';
import SwipeCard from '../../components/matching/SwipeCard';

// Imports Dati Reali
import { UNIVERSITY_DATA } from '../../data/universities';
import { CDL_DATA } from '../../data/cdl';


// --- COMPONENTE PARTICELLE ANIMAZIONE ---
const SwipeParticles = ({ type }) => {
    const particleCount = type === 'like' ? 12 : 8;
    const particles = Array.from({ length: particleCount });

    if (type === 'like') {
        return (
            <div className="absolute inset-0 pointer-events-none z-[100]">
                {particles.map((_, i) => (
                    <motion.div
                        key={i}
                        initial={{
                            x: '50%',
                            y: '50%',
                            scale: 0,
                            opacity: 1
                        }}
                        animate={{
                            x: `${50 + Math.cos((i / particleCount) * Math.PI * 2) * 150}%`,
                            y: `${50 + Math.sin((i / particleCount) * Math.PI * 2) * 150}%`,
                            scale: [0, 1.5, 0],
                            opacity: [1, 1, 0]
                        }}
                        transition={{
                            duration: 0.8,
                            ease: "easeOut",
                            delay: i * 0.03
                        }}
                        className="absolute"
                    >
                        <Heart
                            size={20}
                            className="text-green-500 fill-green-500"
                            style={{
                                filter: 'drop-shadow(0 0 8px rgba(34,197,94,0.8))'
                            }}
                        />
                    </motion.div>
                ))}
                {/* Onda esplosiva verde */}
                <motion.div
                    initial={{ scale: 0, opacity: 0.6 }}
                    animate={{ scale: 3, opacity: 0 }}
                    transition={{ duration: 0.6, ease: "easeOut" }}
                    className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-32 h-32 rounded-full border-4 border-green-500"
                    style={{
                        boxShadow: '0 0 40px rgba(34,197,94,0.6)'
                    }}
                />
            </div>
        );
    }

    // Animazione NOPE
    return (
        <div className="absolute inset-0 pointer-events-none z-[100]">
            {/* X grandi che si allontanano */}
            {particles.map((_, i) => (
                <motion.div
                    key={i}
                    initial={{
                        x: '50%',
                        y: '50%',
                        scale: 0,
                        opacity: 1,
                        rotate: 0
                    }}
                    animate={{
                        x: `${50 + Math.cos((i / particleCount) * Math.PI * 2) * 120}%`,
                        y: `${50 + Math.sin((i / particleCount) * Math.PI * 2) * 120}%`,
                        scale: [0, 1.2, 0],
                        opacity: [1, 0.8, 0],
                        rotate: 360
                    }}
                    transition={{
                        duration: 0.7,
                        ease: "easeOut",
                        delay: i * 0.04
                    }}
                    className="absolute"
                >
                    <X
                        size={24}
                        strokeWidth={4}
                        className="text-rose-500"
                        style={{
                            filter: 'drop-shadow(0 0 8px rgba(244,63,94,0.8))'
                        }}
                    />
                </motion.div>
            ))}
            {/* Onda esplosiva rossa */}
            <motion.div
                initial={{ scale: 0, opacity: 0.6 }}
                animate={{ scale: 3, opacity: 0 }}
                transition={{ duration: 0.6, ease: "easeOut" }}
                className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-32 h-32 rounded-full border-4 border-rose-500"
                style={{
                    boxShadow: '0 0 40px rgba(244,63,94,0.6)'
                }}
            />
            {/* X gigante centrale */}
            <motion.div
                initial={{ scale: 0, opacity: 0, rotate: 0 }}
                animate={{
                    scale: [0, 2.5, 0],
                    opacity: [0, 0.8, 0],
                    rotate: [0, 180, 360]
                }}
                transition={{ duration: 0.8, ease: "easeOut" }}
                className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2"
            >
                <X
                    size={120}
                    strokeWidth={6}
                    className="text-rose-500"
                    style={{
                        filter: 'drop-shadow(0 0 30px rgba(244,63,94,1))'
                    }}
                />
            </motion.div>
        </div>
    );
};

// --- COMPONENTE FILTRI (invariato) ---
const FilterSection = ({
                           selectedUni, setSelectedUni,
                           isUniOpen, setIsUniOpen,
                           uniSearch, setUniSearch, uniInputRef,
                           filteredUniversities,
                           selectedCourse, setSelectedCourse,
                           isCourseOpen, setIsCourseOpen,
                           courseSearch, setCourseSearch, courseInputRef,
                           filteredCdlCategories,
                           setShowMobileFilters
                       }) => {
    const handleSelectUni = (uni) => {
        setSelectedUni(uni);
        setIsUniOpen(false);
    };

    const handleSelectCourse = (course) => {
        setSelectedCourse(course);
        setIsCourseOpen(false);
    };

    return (
        <div className="w-full space-y-5 pointer-events-auto">
            <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-younify-blue/20 via-purple-500/10 to-transparent border border-white/10 p-4 backdrop-blur-xl"
            >
                <div className="absolute inset-0 bg-gradient-to-br from-younify-blue/5 to-purple-500/5 opacity-50" />
                <div className="relative flex items-center gap-3">
                    <div className="relative">
                        <div className="absolute inset-0 bg-younify-blue/30 blur-lg rounded-full" />
                        <div className="relative p-2.5 bg-gradient-to-br from-younify-blue to-purple-600 rounded-xl shadow-lg">
                            <Sparkles size={18} className="text-white" />
                        </div>
                    </div>
                    <div>
                        <h3 className="text-base font-bold text-white tracking-tight">Preferenze di match</h3>
                        <p className="text-[10px] text-slate-400 font-medium">Applica filtri per trova il partner di studio perfetto</p>
                    </div>
                </div>
            </motion.div>

            <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
                className="space-y-2 relative z-[101]"
            >
                <div className="flex items-center justify-between px-1">
                    <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Università</label>
                </div>
                <button
                    onClick={() => { setIsUniOpen(!isUniOpen); setIsCourseOpen(false); setUniSearch(""); }}
                    className={`group w-full flex items-center justify-between bg-gradient-to-br from-white/[0.07] to-white/[0.02] hover:from-white/[0.12] hover:to-white/[0.04] border backdrop-blur-xl ${
                        isUniOpen
                            ? 'border-younify-blue/50 ring-2 ring-younify-blue/20 shadow-[0_0_20px_rgba(59,130,246,0.15)]'
                            : 'border-white/10 hover:border-white/20'
                    } rounded-2xl py-3.5 px-4 text-sm font-medium transition-all duration-300`}
                >
                    <div className="flex items-center gap-3 overflow-hidden">
                        <div className={`p-1.5 rounded-lg transition-all duration-300 ${
                            isUniOpen
                                ? 'bg-younify-blue/20 shadow-lg shadow-younify-blue/20'
                                : 'bg-white/5 group-hover:bg-white/10'
                        }`}>
                            <GraduationCap size={16} className={`transition-colors ${isUniOpen ? 'text-younify-blue' : 'text-slate-400 group-hover:text-slate-300'}`} />
                        </div>
                        <span className={`truncate text-xs md:text-sm font-medium transition-colors ${selectedUni ? 'text-white' : 'text-slate-400 group-hover:text-slate-300'}`}>
                            {selectedUni || "La tua università"}
                        </span>
                    </div>
                    <ChevronRight size={16} className={`transition-all duration-300 ${isUniOpen ? 'rotate-90 text-younify-blue scale-110' : 'text-slate-500 group-hover:text-slate-400'}`} />
                </button>

                <AnimatePresence>
                    {isUniOpen && (
                        <motion.div
                            initial={{ opacity: 0, y: -10, scale: 0.95 }}
                            animate={{ opacity: 1, y: 0, scale: 1 }}
                            exit={{ opacity: 0, y: -10, scale: 0.95 }}
                            transition={{ duration: 0.2, ease: [0.16, 1, 0.3, 1] }}
                            className="absolute top-full left-0 right-0 mt-2 bg-[#0a0f1a]/95 backdrop-blur-2xl border border-white/10 rounded-2xl shadow-2xl shadow-black/20 z-[120] flex flex-col overflow-hidden max-h-64"
                        >
                            <div className="p-3 border-b border-white/5 bg-gradient-to-b from-white/[0.02] to-transparent sticky top-0 z-10">
                                <div className="relative group">
                                    <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-younify-blue transition-colors" />
                                    <input
                                        ref={uniInputRef}
                                        type="text"
                                        placeholder="Cerca l'università..."
                                        value={uniSearch}
                                        onChange={(e) => setUniSearch(e.target.value)}
                                        onClick={(e) => e.stopPropagation()}
                                        className="w-full bg-white/5 hover:bg-white/[0.07] focus:bg-white/[0.08] border border-white/10 focus:border-younify-blue/50 rounded-xl py-2 pl-9 pr-3 text-xs text-white placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-younify-blue/20 transition-all"
                                    />
                                </div>
                            </div>

                            <div className="overflow-y-auto p-2 scrollbar-thin scrollbar-thumb-white/10 scrollbar-track-transparent hover:scrollbar-thumb-white/20">
                                {filteredUniversities.length > 0 ? (
                                    filteredUniversities.map((uni, idx) => (
                                        <motion.button
                                            key={idx}
                                            initial={{ opacity: 0, x: -10 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            transition={{ delay: idx * 0.02 }}
                                            onClick={() => handleSelectUni(uni)}
                                            className={`w-full text-left px-3 py-2.5 rounded-xl text-xs font-medium transition-all duration-200 ${
                                                selectedUni === uni
                                                    ? 'bg-gradient-to-r from-younify-blue to-purple-600 text-white shadow-lg shadow-younify-blue/20'
                                                    : 'text-slate-300 hover:bg-white/5 hover:text-white active:scale-[0.98]'
                                            }`}
                                        >
                                            {uni}
                                        </motion.button>
                                    ))
                                ) : (
                                    <div className="p-4 text-center">
                                        <div className="w-12 h-12 mx-auto mb-2 rounded-full bg-white/5 flex items-center justify-center">
                                            <Search size={16} className="text-slate-500" />
                                        </div>
                                        <p className="text-[11px] text-slate-500 font-medium">No results found</p>
                                    </div>
                                )}
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>
            </motion.div>

            <AnimatePresence>
                {selectedUni && (
                    <motion.div
                        initial={{ opacity: 0, height: 0 }}
                        animate={{ opacity: 1, height: 'auto' }}
                        exit={{ opacity: 0, height: 0 }}
                        transition={{ duration: 0.3, ease: [0.16, 1, 0.3, 1] }}
                        className="space-y-2 relative overflow-visible z-[100]"
                    >
                        <div className="flex items-center justify-between px-1">
                            <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Corso di Laurea</label>
                        </div>
                        <button
                            onClick={() => { setIsCourseOpen(!isCourseOpen); setIsUniOpen(false); setCourseSearch(""); }}
                            className={`group w-full flex items-center justify-between bg-gradient-to-br from-white/[0.07] to-white/[0.02] hover:from-white/[0.12] hover:to-white/[0.04] border backdrop-blur-xl ${
                                isCourseOpen
                                    ? 'border-purple-500/50 ring-2 ring-purple-500/20 shadow-[0_0_20px_rgba(168,85,247,0.15)]'
                                    : 'border-white/10 hover:border-white/20'
                            } rounded-2xl py-3.5 px-4 text-sm font-medium transition-all duration-300`}
                        >
                            <div className="flex items-center gap-3 overflow-hidden">
                                <div className={`p-1.5 rounded-lg transition-all duration-300 ${
                                    isCourseOpen
                                        ? 'bg-purple-500/20 shadow-lg shadow-purple-500/20'
                                        : 'bg-white/5 group-hover:bg-white/10'
                                }`}>
                                    <BookOpen size={16} className={`transition-colors ${isCourseOpen ? 'text-purple-400' : 'text-slate-400 group-hover:text-slate-300'}`} />
                                </div>
                                <span className={`truncate text-xs md:text-sm font-medium transition-colors ${selectedCourse ? 'text-white' : 'text-slate-400 group-hover:text-slate-300'}`}>
                                    {selectedCourse || "Seleziona il corso di studi"}
                                </span>
                            </div>
                            {selectedCourse ? (
                                <button
                                    onClick={(e) => { e.stopPropagation(); setSelectedCourse(""); }}
                                    className="p-1 hover:bg-white/10 rounded-lg transition-all active:scale-90"
                                >
                                    <X size={14} className="text-slate-400 hover:text-white" />
                                </button>
                            ) : (
                                <ChevronRight size={16} className={`transition-all duration-300 ${isCourseOpen ? 'rotate-90 text-purple-400 scale-110' : 'text-slate-500 group-hover:text-slate-400'}`} />
                            )}
                        </button>

                        <AnimatePresence>
                            {isCourseOpen && (
                                <motion.div
                                    initial={{ opacity: 0, y: -10, scale: 0.95 }}
                                    animate={{ opacity: 1, y: 0, scale: 1 }}
                                    exit={{ opacity: 0, y: -10, scale: 0.95 }}
                                    transition={{ duration: 0.2, ease: [0.16, 1, 0.3, 1] }}
                                    className="absolute top-full left-0 right-0 mt-2 bg-[#0a0f1a]/95 backdrop-blur-2xl border border-white/10 rounded-2xl shadow-2xl shadow-black/20 z-[120] flex flex-col overflow-hidden max-h-64"
                                >
                                    <div className="p-3 border-b border-white/5 bg-gradient-to-b from-white/[0.02] to-transparent sticky top-0 z-10">
                                        <div className="relative group">
                                            <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500 group-focus-within:text-purple-400 transition-colors" />
                                            <input
                                                ref={courseInputRef}
                                                type="text"
                                                placeholder="Cerca il corso..."
                                                value={courseSearch}
                                                onChange={(e) => setCourseSearch(e.target.value)}
                                                onClick={(e) => e.stopPropagation()}
                                                className="w-full bg-white/5 hover:bg-white/[0.07] focus:bg-white/[0.08] border border-white/10 focus:border-purple-500/50 rounded-xl py-2 pl-9 pr-3 text-xs text-white placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-purple-500/20 transition-all"
                                            />
                                        </div>
                                    </div>

                                    <div className="overflow-y-auto p-2 scrollbar-thin scrollbar-thumb-white/10 scrollbar-track-transparent hover:scrollbar-thumb-white/20">
                                        <button
                                            onClick={() => { setSelectedCourse(""); setIsCourseOpen(false); }}
                                            className="w-full text-left px-3 py-2.5 mb-1 rounded-xl text-slate-400 hover:text-white hover:bg-white/5 text-xs font-medium transition-all active:scale-[0.98]"
                                        >
                                            Tutti i corsi
                                        </button>
                                        {filteredCdlCategories.length > 0 ? (
                                            filteredCdlCategories.map(([category, courses]) => (
                                                <div key={category} className="mt-2">
                                                    <div className="px-3 py-1.5 text-[10px] font-bold text-purple-400 uppercase tracking-wider bg-gradient-to-r from-purple-500/10 to-transparent rounded-lg mb-1 flex items-center gap-2">
                                                        <div className="w-1 h-1 rounded-full bg-purple-400" />
                                                        {category}
                                                    </div>
                                                    {courses.map((course, idx) => (
                                                        <motion.button
                                                            key={course}
                                                            initial={{ opacity: 0, x: -10 }}
                                                            animate={{ opacity: 1, x: 0 }}
                                                            transition={{ delay: idx * 0.02 }}
                                                            onClick={() => handleSelectCourse(course)}
                                                            className={`w-full text-left px-3 py-2.5 rounded-xl text-xs font-medium truncate transition-all duration-200 ${
                                                                selectedCourse === course
                                                                    ? 'bg-gradient-to-r from-purple-600 to-purple-700 text-white shadow-lg shadow-purple-600/20'
                                                                    : 'text-slate-300 hover:bg-white/5 hover:text-white active:scale-[0.98]'
                                                            }`}
                                                        >
                                                            {course}
                                                        </motion.button>
                                                    ))}
                                                </div>
                                            ))
                                        ) : (
                                            <div className="p-4 text-center">
                                                <div className="w-12 h-12 mx-auto mb-2 rounded-full bg-white/5 flex items-center justify-center">
                                                    <Search size={16} className="text-slate-500" />
                                                </div>
                                                <p className="text-[11px] text-slate-500 font-medium">No courses found</p>
                                            </div>
                                        )}
                                    </div>
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};

// --- PAGINA PRINCIPALE CON ANIMAZIONI ---
const MatchingPage = () => {
    const [currentUser, setCurrentUser] = useState(null);
    const [selectedUni, setSelectedUni] = useState("");
    const [selectedCourse, setSelectedCourse] = useState("");
    const [cards, setCards] = useState("");
    const [isUniOpen, setIsUniOpen] = useState(false);
    const [isCourseOpen, setIsCourseOpen] = useState(false);
    const [showMobileFilters, setShowMobileFilters] = useState(false);
    const [exitDir, setExitDir] = useState(0);
    const [uniSearch, setUniSearch] = useState("");
    const [courseSearch, setCourseSearch] = useState("");

    // Stato per le animazioni
    const [showParticles, setShowParticles] = useState(null);
    const [buttonPressed, setButtonPressed] = useState(null);

    const uniInputRef = useRef(null);
    const courseInputRef = useRef(null);

    const allUniversities = useMemo(() => Object.values(UNIVERSITY_DATA).flat().sort(), []);

    const filteredUniversities = useMemo(() => {
        if (!uniSearch) return allUniversities;
        return allUniversities.filter(u => u.toLowerCase().includes(uniSearch.toLowerCase()));
    }, [allUniversities, uniSearch]);

    const cdlCategories = useMemo(() => Object.entries(CDL_DATA), []);

    const filteredCdlCategories = useMemo(() => {
        if (!courseSearch) return cdlCategories;
        return cdlCategories.map(([category, courses]) => {
            const filteredCourses = courses.filter(c => c.toLowerCase().includes(courseSearch.toLowerCase()));
            return [category, filteredCourses];
        }).filter(([_, courses]) => courses.length > 0);
    }, [cdlCategories, courseSearch]);

    useEffect(() => {
        let filtered="";
        if (selectedUni) {
            filtered = filtered.filter(u => {
                const userUni = u.university.toLowerCase().trim();
                const filterUni = selectedUni.toLowerCase().trim();
                return userUni.includes(filterUni) || filterUni.includes(userUni);
            });
        }
        if (selectedCourse) {
            filtered = filtered.filter(u => {
                const userCourse = u.course.toLowerCase().trim();
                const filterCourse = selectedCourse.toLowerCase().trim();
                return userCourse.includes(filterCourse) || filterCourse.includes(userCourse);
            });
        }
        setCards(filtered);
    }, [selectedUni, selectedCourse]);

    const swipe = (direction) => {
        if (cards.length > 0) {
            setExitDir(direction === 'right' ? 1 : -1);
            setShowParticles(direction === 'right' ? 'like' : 'nope');
            setButtonPressed(direction === 'right' ? 'like' : 'nope');

            setTimeout(() => {
                setCards((prev) => prev.slice(1));
                setShowParticles(null);
                setButtonPressed(null);
                setExitDir(0);
            }, 1000);
        }
    };

    const handleReset = () => {
        setSelectedUni("");
        setSelectedCourse("");
        setCards("");
    };


    useEffect(() => {
        const handleKeyDown = (e) => {
            if (e.key === 'ArrowLeft') swipe('left');
            if (e.key === 'ArrowRight') swipe('right');
        };
        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, [cards]);

    const filterProps = {
        selectedUni, setSelectedUni,
        isUniOpen, setIsUniOpen,
        uniSearch, setUniSearch, uniInputRef,
        filteredUniversities,
        selectedCourse, setSelectedCourse,
        isCourseOpen, setIsCourseOpen,
        courseSearch, setCourseSearch, courseInputRef,
        filteredCdlCategories,
        setShowMobileFilters
    };

    return (
        <div
            className="fixed inset-0 w-full font-sans text-slate-200 flex flex-col bg-slate-900/60 backdrop-blur-md lg:bg-transparent lg:backdrop-blur-none"
            style={{
                height: '100dvh',
                paddingTop: 'env(safe-area-inset-top)',
                paddingBottom: 'max(env(safe-area-inset-bottom), 16px)'
            }}
        >
            {/* NAVBAR + FAB */}
            <div className="relative z-50 shrink-0 px-4 pt-3 pb-2 flex items-center justify-between gap-2 md:pb-4 lg:block lg:px-0 lg:pt-0">
                <div className="flex-1 md:w-full">
                    <AppNavbar />
                </div>

                <motion.button
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={() => setShowMobileFilters(true)}
                    className="md:hidden relative flex items-center justify-center w-12 h-12 bg-gradient-to-br from-white/10 to-white/5 backdrop-blur-xl border border-white/20 rounded-2xl text-white shadow-lg shadow-black/10 overflow-hidden group"
                >
                    <div className="absolute inset-0 bg-gradient-to-br from-younify-blue/20 to-purple-600/20 opacity-0 group-hover:opacity-100 transition-opacity" />
                    <SlidersHorizontal size={20} className="relative z-10" />
                </motion.button>
            </div>

            {/* MODALE FILTRI MOBILE */}
            <AnimatePresence>
                {showMobileFilters && (
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        className="fixed inset-0 z-[200] bg-gradient-to-br from-[#0a0f1a] via-[#0f1420] to-[#0a0f1a] flex flex-col p-6 md:hidden"
                        style={{
                            paddingTop: 'max(env(safe-area-inset-top), 24px)',
                            paddingBottom: 'max(env(safe-area-inset-bottom), 24px)'
                        }}
                    >
                        <div className="absolute top-20 left-1/2 -translate-x-1/2 w-[300px] h-[300px] bg-younify-blue/20 blur-[100px] rounded-full pointer-events-none" />

                        <motion.div
                            initial={{ y: -20, opacity: 0 }}
                            animate={{ y: 0, opacity: 1 }}
                            className="relative flex items-center justify-between mb-6 shrink-0"
                        >
                            <div>
                                <h2 className="text-2xl font-black text-white tracking-tight">Filtri</h2>
                                <p className="text-xs text-slate-400 mt-1">Personalizza le tue preferenze</p>
                            </div>
                            <motion.button
                                whileHover={{ scale: 1.05, rotate: 90 }}
                                whileTap={{ scale: 0.95 }}
                                onClick={() => setShowMobileFilters(false)}
                                className="p-2.5 bg-white/5 hover:bg-white/10 rounded-xl text-white backdrop-blur-xl border border-white/10 transition-all"
                            >
                                <X size={20}/>
                            </motion.button>
                        </motion.div>

                        <div className="flex-1 overflow-y-auto -mx-2 px-2 scrollbar-thin scrollbar-thumb-white/10 scrollbar-track-transparent">
                            <FilterSection {...filterProps} />
                        </div>

                        <motion.div
                            initial={{ y: 20, opacity: 0 }}
                            animate={{ y: 0, opacity: 1 }}
                            transition={{ delay: 0.2 }}
                            className="mt-6 pt-4 shrink-0 border-t border-white/10"
                        >
                            <motion.button
                                whileHover={{ scale: 1.02 }}
                                whileTap={{ scale: 0.98 }}
                                onClick={() => setShowMobileFilters(false)}
                                className="relative w-full py-4 bg-gradient-to-r from-younify-blue to-purple-600 text-white font-bold rounded-2xl shadow-lg shadow-younify-blue/20 overflow-hidden group"
                            >
                                <div className="absolute inset-0 bg-gradient-to-r from-purple-600 to-younify-blue opacity-0 group-hover:opacity-100 transition-opacity" />
                                <span className="relative z-10 flex items-center justify-center gap-2">
                                    Applica filtri
                                    <Sparkles size={16} />
                                </span>
                            </motion.button>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence>

            {/* CONTENUTO PRINCIPALE */}
            <div className="flex-1 w-full max-w-[90rem] mx-auto px-4 md:px-6 lg:pl-40 lg:pr-8 flex flex-col pt-0 md:pt-2 lg:pt-4 min-h-0">

                {/* TITOLO - ✅ FIX: Più in basso (mt-6), Più grande (text-3xl), Spazio tra parole fixato */}
                <div className="text-center mt-6 mb-4 md:mb-2 lg:mb-4 relative z-20 shrink-0">
                    <motion.div
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.8, ease: [0.16, 1, 0.3, 1] }}
                        className="relative"
                    >
                        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[120%] h-[200%] bg-gradient-to-r from-younify-blue/10 via-purple-500/10 to-pink-500/10 blur-[60px] opacity-60 pointer-events-none" />

                        {/* MODIFICA:
                            1. text-3xl (più grande su mobile)
                            2. tracking-tighter (lettere più vicine per farlo entrare in una riga)
                        */}
                        <h1 className="relative text-3xl sm:text-4xl md:text-5xl lg:text-6xl xl:text-7xl font-black tracking-tighter leading-[1.1] whitespace-nowrap">
                            <span className="text-white drop-shadow-2xl">Find your </span>
                            {/* Spazio vuoto manuale per evitare che si attacchino */}
                            <span className="inline-block px-1 py-1 text-transparent bg-clip-text bg-gradient-to-r from-younify-blue via-purple-400 to-pink-500 animate-gradient drop-shadow-[0_0_30px_rgba(59,130,246,0.5)]">
                                study soulmate
                            </span>
                        </h1>

                        <motion.p
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.2, duration: 0.8 }}
                            className="text-slate-400 text-xs md:text-sm lg:text-lg mt-3 md:mt-2 lg:mt-2 font-medium max-w-2xl mx-auto leading-relaxed"
                        >
                            Filtra, scegli e poi?{' '}
                            <span className="relative inline-block">
                                <span className="absolute inset-0 bg-gradient-to-r from-younify-blue/30 to-purple-600/30 blur-lg" />
                                <span className="relative text-transparent bg-clip-text bg-gradient-to-r from-younify-blue to-purple-600 font-bold">
                                    MATCH
                                </span>
                            </span>
                        </motion.p>
                    </motion.div>
                </div>

                {/* AREA CENTRALE */}
                <div className="flex-1 flex flex-col md:grid md:grid-cols-12 lg:grid-cols-12 md:gap-4 lg:gap-6 items-center justify-center">

                    {/* FILTRI DESKTOP/TABLET - ✅ FIX: Più larghi (lg:max-w-[400px]) e 5 colonne */}
                    <div className="hidden md:flex md:col-span-5 lg:col-span-5 flex-col justify-center items-start md:pl-24 lg:pl-16 order-2 md:order-1 lg:order-1 relative z-[60] pointer-events-none h-full">
                        <motion.div
                            initial={{ opacity: 0, x: -40 }}
                            animate={{ opacity: 1, x: 0 }}
                            transition={{ delay: 0.3, duration: 0.8, ease: [0.16, 1, 0.3, 1] }}
                            // MODIFICA: lg:max-w-[400px] li rende più larghi e meno alti
                            className="relative w-full md:max-w-[280px] lg:max-w-[400px] pointer-events-auto group"
                        >
                            <div className="absolute -inset-1 bg-gradient-to-r from-younify-blue/20 via-purple-500/20 to-pink-500/20 rounded-[2.5rem] blur-xl opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
                            <div className="relative bg-gradient-to-br from-[#0f1420]/80 to-[#0a0f1a]/60 backdrop-blur-2xl border border-white/10 rounded-[2rem] p-5 lg:p-6 shadow-2xl shadow-black/20">
                                <FilterSection {...filterProps} />
                            </div>
                        </motion.div>
                    </div>

                    {/* CARD + BOTTONI - ✅ FIX: 7 colonne (per fare spazio ai filtri) */}
                    <div className="-mt-8 md:mt-0 flex-1 lg:h-full md:col-span-7 lg:col-span-7 flex flex-col justify-center items-center lg:items-start md:pl-0 lg:pl-24 order-1 md:order-2 lg:order-2 w-full relative z-20 pb-8 md:pb-0 lg:pb-12">

                        <div className="flex-1 flex flex-col items-center justify-center w-full gap-4 md:gap-6 lg:gap-6 max-w-full">

                            {/* STACK CARD - Dimensioni invariate, va bene così per l'altezza 810px */}
                            <motion.div
                                initial={{ opacity: 0, scale: 0.9 }}
                                animate={{ opacity: 1, scale: 1 }}
                                transition={{ delay: 0.4, duration: 0.6 }}
                                className="relative w-full max-w-[min(78vw,330px)] md:max-w-[280px] lg:max-w-[320px] xl:max-w-[420px] aspect-[3/4.2] flex-shrink-0 z-30"
                            >
                                <motion.div
                                    animate={{
                                        opacity: [0.3, 0.6, 0.3],
                                        scale: [1, 1.05, 1],
                                    }}
                                    transition={{
                                        duration: 3,
                                        repeat: Infinity,
                                        ease: "easeInOut"
                                    }}
                                    className="absolute -inset-4 bg-gradient-to-r from-younify-blue/30 via-purple-500/30 to-pink-500/30 rounded-[3rem] blur-2xl pointer-events-none"
                                />

                                {/* PARTICELLE ANIMAZIONE */}
                                <AnimatePresence>
                                    {showParticles && <SwipeParticles type={showParticles} />}
                                </AnimatePresence>

                                {/*<AnimatePresence custom={exitDir} mode="popLayout">
                                    {cards.map((user, index) => {
                                        if (index === 1) {
                                            return (
                                                <motion.div
                                                    key={user.id}
                                                    initial={{ opacity: 0, scale: 0.9 }}
                                                    animate={{ opacity: 1, scale: 0.92, y: 20 }}
                                                    exit={{ opacity: 0 }}
                                                    transition={{ duration: 0.4 }}
                                                    className="absolute inset-0 z-30 rounded-[36px] overflow-hidden bg-slate-900/40 pointer-events-none blur-[4px] grayscale-[0.5] opacity-50 shadow-2xl"
                                                >
                                                    <img src={user.image} className="w-full h-full object-cover" alt="" />
                                                </motion.div>
                                            );
                                        }
                                        if (index === 0) {
                                            return (
                                                <motion.div
                                                    key={user.id}
                                                    initial={{ scale: 0.9, opacity: 0 }}
                                                    animate={{ scale: 1, opacity: 1 }}
                                                    exit={{
                                                        scale: 0.8,
                                                        y: 100,
                                                        opacity: 0,
                                                        transition: { duration: 0.4, ease: [0.16, 1, 0.3, 1] }
                                                    }}
                                                    transition={{ type: "spring", stiffness: 260, damping: 20 }}
                                                    className="absolute inset-0 z-50 rounded-[36px] shadow-2xl shadow-black/40"
                                                    style={{ cursor: 'grab' }}
                                                >
                                                    <SwipeCard user={user} onSwipe={swipe} style={{ width: '100%', height: '100%' }} />
                                                </motion.div>
                                            );
                                        }
                                        return null;
                                    })}
                                </AnimatePresence>*/}

                                {/* EMPTY STATE */}
                                {cards.length === 0 && (
                                    <motion.div
                                        initial={{ opacity: 0, scale: 0.9 }}
                                        animate={{ opacity: 1, scale: 1 }}
                                        className="absolute inset-0 flex flex-col items-center justify-center text-center bg-gradient-to-br from-white/[0.08] to-white/[0.02] backdrop-blur-2xl rounded-[36px] border border-white/10 p-6 shadow-2xl"
                                    >
                                        <motion.div
                                            animate={{ rotate: 360 }}
                                            transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
                                            className="relative w-20 h-20 mb-6"
                                        >
                                            <div className="absolute inset-0 bg-gradient-to-r from-younify-blue/30 to-purple-600/30 rounded-full blur-xl" />
                                            <div className="relative w-full h-full bg-gradient-to-br from-white/10 to-white/5 rounded-full flex items-center justify-center backdrop-blur-xl border border-white/20">
                                                <RotateCcw size={32} className="text-white" />
                                            </div>
                                        </motion.div>
                                        <h2 className="text-2xl font-black text-white mb-2">Fine della lista</h2>
                                        <p className="text-slate-400 mb-8 text-sm max-w-[200px]">Non ci sono più profili affini ai tuoi filtri</p>
                                        <motion.button
                                            whileHover={{ scale: 1.05 }}
                                            whileTap={{ scale: 0.95 }}
                                            onClick={handleReset}
                                            className="relative px-8 py-3 bg-gradient-to-r from-younify-blue to-purple-600 text-white font-bold rounded-2xl shadow-lg shadow-younify-blue/20 overflow-hidden group"
                                        >
                                            <div className="absolute inset-0 bg-gradient-to-r from-purple-600 to-younify-blue opacity-0 group-hover:opacity-100 transition-opacity" />
                                            <span className="relative z-10 flex items-center gap-2">
                                                Resetta Filtri
                                                <RotateCcw size={16} />
                                            </span>
                                        </motion.button>
                                    </motion.div>
                                )}
                            </motion.div>

                            {/* BOTTONI CON ANIMAZIONI */}
                            {cards.length > 0 && (
                                <motion.div
                                    initial={{ opacity: 0, y: 30 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: 0.5 }}
                                    className="flex items-center gap-6 md:gap-8 lg:gap-12 z-30 shrink-0"
                                >
                                    {/* Bottone NOPE CON ANIMAZIONE */}
                                    <motion.button
                                        animate={buttonPressed === 'nope' ? {
                                            scale: [1, 0.8, 1.3, 1],
                                            rotate: [0, -10, 10, 0]
                                        } : {}}
                                        transition={{ duration: 0.4 }}
                                        whileHover={{ scale: 1.08 }}
                                        whileTap={{ scale: 0.85 }}
                                        onClick={() => swipe('left')}
                                        className="relative group w-16 h-16 md:w-20 md:h-20 lg:w-20 lg:h-20 rounded-full"
                                    >
                                        {buttonPressed === 'nope' && (
                                            <motion.div
                                                initial={{ scale: 1, opacity: 0.8 }}
                                                animate={{ scale: 2.5, opacity: 0 }}
                                                transition={{ duration: 0.6 }}
                                                className="absolute inset-0 rounded-full border-4 border-rose-500"
                                            />
                                        )}
                                        <div className="absolute inset-0 bg-rose-500/20 blur-xl opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
                                        <div className="relative w-full h-full bg-gradient-to-br from-[#1a1a1a] to-[#0f0f0f] border-2 border-rose-500/30 group-hover:border-rose-500 rounded-full flex items-center justify-center backdrop-blur-xl transition-all duration-300 shadow-lg shadow-black/20">
                                            <motion.div
                                                animate={buttonPressed === 'nope' ? { rotate: 180, scale: 1.2 } : {}}
                                                transition={{ duration: 0.3 }}
                                            >
                                                <X size={32} strokeWidth={3} className="text-rose-500 group-hover:text-white group-hover:rotate-90 transition-all duration-300" />
                                            </motion.div>
                                        </div>
                                    </motion.button>

                                    {/* Bottone LIKE CON ANIMAZIONE */}
                                    <motion.button
                                        animate={buttonPressed === 'like' ? {
                                            scale: [1, 0.8, 1.3, 1],
                                        } : {}}
                                        transition={{ duration: 0.4 }}
                                        whileHover={{ scale: 1.08 }}
                                        whileTap={{ scale: 0.85 }}
                                        onClick={() => swipe('right')}
                                        className="relative group w-16 h-16 md:w-20 md:h-20 lg:w-20 lg:h-20 rounded-full"
                                    >
                                        {buttonPressed === 'like' && (
                                            <>
                                                <motion.div
                                                    initial={{ scale: 1, opacity: 0.8 }}
                                                    animate={{ scale: 2.5, opacity: 0 }}
                                                    transition={{ duration: 0.6 }}
                                                    className="absolute inset-0 rounded-full border-4 border-green-500"
                                                />
                                                {/* Cuori che volano */}
                                                {[...Array(6)].map((_, i) => (
                                                    <motion.div
                                                        key={i}
                                                        initial={{ scale: 0, x: 0, y: 0, opacity: 1 }}
                                                        animate={{
                                                            scale: [0, 1, 0],
                                                            x: Math.cos((i / 6) * Math.PI * 2) * 60,
                                                            y: Math.sin((i / 6) * Math.PI * 2) * 60,
                                                            opacity: [1, 1, 0]
                                                        }}
                                                        transition={{ duration: 0.8, delay: i * 0.05 }}
                                                        className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2"
                                                    >
                                                        <Heart size={16} className="text-green-500 fill-green-500" />
                                                    </motion.div>
                                                ))}
                                            </>
                                        )}
                                        <div className="absolute inset-0 bg-green-500/20 blur-xl opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
                                        <div className="relative w-full h-full bg-gradient-to-br from-[#1a1a1a] to-[#0f0f0f] border-2 border-green-500/30 group-hover:border-green-500 rounded-full flex items-center justify-center backdrop-blur-xl transition-all duration-300 shadow-lg shadow-black/20">
                                            <motion.div
                                                animate={buttonPressed === 'like' ? {
                                                    scale: [1, 1.3, 1],
                                                    rotate: [0, 10, -10, 0]
                                                } : {}}
                                                transition={{ duration: 0.4 }}
                                            >
                                                <Heart size={32} strokeWidth={3} fill="currentColor" className="text-green-500 group-hover:text-white opacity-50 group-hover:opacity-100 group-hover:scale-110 transition-all duration-300" />
                                            </motion.div>
                                        </div>
                                    </motion.button>
                                </motion.div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MatchingPage;
