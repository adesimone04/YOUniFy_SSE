import React, {useState, useEffect, useMemo, useRef} from 'react';
import {
    User, Mail, MapPin, Calendar, Camera, Lock, Save,
    Building2, GraduationCap, Award, Shield, Edit3, Star,
    ChevronRight, CheckCircle2, TrendingUp, MessageSquare,
    CalendarDays, X, Instagram, Receipt, CreditCard,
    AlertCircle, School, ChevronDown, CheckCircle,
    Clock, Users, Sparkles, Upload, ImageIcon, ChevronLeft, PenLine, MessageCircle, Heart, Check, Globe2,
} from 'lucide-react';

import {motion, AnimatePresence} from 'framer-motion';
import {toast} from 'react-toastify';
import {useNavigate} from 'react-router-dom';
import InfiniteMenu from '../../components/ui/InfiniteMenu/InfiniteMenu';
import AppNavbar from '../../components/layout/Navbar/AppNavbar';
import profileService from '../../services/profileService';
import eventService from '../../services/eventService';
import matchService from '../../services/matchService';
import {threadService} from '../../services/threadService';


import rawCitiesData from '../../data/comuni.json';
import {ALLOWED_EMAIL_DOMAINS} from '../../data/emailDomains';
import {UNIVERSITY_DATA} from '../../data/universities';
import {CDL_DATA} from '../../data/cdl';


const passwordRegex = /(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&.])[A-Za-z\d@$!%*?&.]{8,}/;
// Match ricevuti


const PremiumInput = ({label, icon: Icon, type = "text", disabled = false, error, ...props}) => {
    return (
        <div className="space-y-2">
            <label className={`block text-xs font-medium uppercase tracking-widest transition-colors ${
                disabled
                    ? 'text-slate-600'
                    : 'text-slate-400'
            }`}>
                {label}
            </label>
            <div className="relative group">
                <Icon className={`absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 z-10 transition-colors ${
                    disabled
                        ? 'text-slate-600'
                        : error
                            ? 'text-rose-400'
                            : 'text-younify-blue'
                }`}/>
                <input
                    type={type}
                    disabled={disabled}
                    className={`w-full border rounded-2xl px-4 py-3.5 pl-11 transition-all duration-300 ${
                        disabled
                            ? 'bg-slate-900/40 backdrop-blur-md border-white/5 cursor-not-allowed text-slate-400/60 placeholder-slate-700'
                            : error
                                ? 'bg-slate-950/50 border-rose-500/50 text-white placeholder-slate-600 focus:border-rose-500 focus:ring-2 focus:ring-rose-500/20'
                                : 'bg-slate-950/50 border-white/10 text-white placeholder-slate-400 focus:outline-none focus:border-younify-blue/50 focus:ring-2 focus:ring-younify-blue/20'  // ← CAMBIATO da placeholder-slate-500 a placeholder-slate-400
                    }`}
                    {...props}
                />
            </div>
            {error && (
                <p className="text-xs text-rose-400 flex items-center gap-1">
                    <AlertCircle className="h-3 w-3"/>
                    {error}
                </p>
            )}
        </div>
    );
};


const PremiumSelect = ({label, icon: Icon, children, disabled = false, error, ...props}) => (
    <div className="space-y-2">
        <label className="block text-xs font-medium text-slate-400 uppercase tracking-widest">
            {label}
        </label>
        <div className="relative group">
            {Icon && (
                <Icon className={`absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 z-10 transition-colors ${
                    disabled ? 'text-slate-700' : error ? 'text-rose-400' : 'text-slate-500 group-focus-within:text-younify-blue'
                }`}/>
            )}
            <select
                disabled={disabled}
                className={`w-full border rounded-2xl px-4 py-3.5 pl-11 pr-10 text-white transition-all duration-300 appearance-none cursor-pointer ${
                    disabled
                        ? 'bg-slate-950/30 border-slate-800/30 cursor-not-allowed text-slate-500'
                        : error
                            ? 'bg-slate-950/50 border-rose-500/50 focus:border-rose-500 focus:ring-2 focus:ring-rose-500/20'
                            : 'bg-slate-950/50 border-white/5 focus:outline-none focus:border-younify-blue/50 focus:ring-2 focus:ring-younify-blue/20'
                }`}
                {...props}
            >
                {children}
            </select>
            <ChevronRight
                className="absolute right-4 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-500 rotate-90 pointer-events-none"/>
        </div>
        {error && (
            <p className="text-xs text-rose-400 flex items-center gap-1">
                <AlertCircle className="h-3 w-3"/>
                {error}
            </p>
        )}
    </div>
);

// ========================================
// COMPONENTE INSTAGRAM INPUT
// ========================================
const InstagramInput = ({value, onChange, disabled = false, error}) => {
    const handleChange = (e) => {
        let val = e.target.value.replace('instagram.com/', '').trim();
        onChange({target: {name: 'contactsInfo', value: val}});
    };

    const iconColor = value && value.length > 0
        ? disabled ? 'text-slate-600' : 'text-blue-500'
        : disabled ? 'text-slate-600' : 'text-slate-500';

    return (
        <div className="space-y-2">
            <label className={`block text-xs font-medium uppercase tracking-widest transition-colors ${
                disabled
                    ? 'text-slate-600'
                    : 'text-slate-400'
            }`}>
                Instagram
            </label>
            <div className="relative group">
                <Instagram
                    className={`absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 z-10 transition-colors ${iconColor}`}/>
                <div
                    className={`absolute left-11 top-1/2 -translate-y-1/2 text-sm pointer-events-none z-10 transition-colors ${
                        disabled ? 'text-slate-600' : 'text-slate-500'  // ← OSCURATO quando disabled
                    }`}>
                    instagram.com/
                </div>
                <input
                    type="text"
                    value={value}
                    onChange={handleChange}
                    disabled={disabled}
                    placeholder="nomeutente"
                    className={`w-full border rounded-2xl px-4 py-3.5 pl-[156px] placeholder-slate-600 transition-all duration-300 ${
                        disabled
                            ? 'bg-slate-900/40 backdrop-blur-md border-white/5 cursor-not-allowed text-slate-400/60'  // ← STESSO STILE degli altri input
                            : error
                                ? 'bg-slate-950/50 border-rose-500/50 text-white focus:border-rose-500 focus:ring-2 focus:ring-rose-500/20'
                                : 'bg-slate-950/50 border-white/5 text-white focus:outline-none focus:border-younify-blue/50 focus:ring-2 focus:ring-younify-blue/20'
                    }`}
                />
            </div>
            {error && (
                <p className="text-xs text-rose-400 flex items-center gap-1">
                    <AlertCircle className="h-3 w-3"/>
                    {error}
                </p>
            )}
        </div>
    );
};


// ========================================
// COMPONENTE MULTI-SELECT UNIVERSITÀ
// ========================================
const UniversityMultiSelect = ({selectedUnis, onToggle, disabled = false, error}) => {
    const [isOpen, setIsOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');

    const allUniversities = useMemo(() => {
        if (Array.isArray(UNIVERSITY_DATA)) {
            return UNIVERSITY_DATA;
        }
        return Object.values(UNIVERSITY_DATA).flat();
    }, []);

    // Filtra università in base alla ricerca
    const filteredUniversities = useMemo(() => {
        if (!searchTerm) return allUniversities;
        return allUniversities.filter(uni =>
            uni.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [allUniversities, searchTerm]);

    return (
        <div className="space-y-2">
            <label className={`block text-xs font-medium uppercase tracking-widest transition-colors ${
                disabled ? 'text-slate-600' : 'text-slate-400'
            }`}>
                Università Partner (Max 5)
            </label>

            {/* Selected Unis Display */}
            <div className={`flex flex-wrap gap-2 min-h-[44px] p-2 rounded-2xl border transition-all ${
                disabled
                    ? 'bg-slate-900/40 backdrop-blur-md border-white/5'
                    : 'bg-slate-950/30 border-slate-800/30'
            }`}>
                {selectedUnis.length === 0 ? (
                    <span className={`text-sm py-1.5 px-2 transition-colors ${
                        disabled ? 'text-slate-600' : 'text-slate-500'
                    }`}>
            Nessuna università selezionata
          </span>
                ) : (
                    selectedUnis.map((uni) => (
                        <div
                            key={uni}
                            className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs transition-all ${
                                disabled
                                    ? 'bg-slate-800/40 border border-slate-700/30 text-slate-500'
                                    : 'bg-younify-blue/20 border border-younify-blue/30 text-slate-300'
                            }`}
                        >
                            <GraduationCap className={`h-3 w-3 transition-colors ${
                                disabled ? 'text-slate-600' : 'text-younify-blue'
                            }`}/>
                            {uni}
                            {!disabled && (
                                <button
                                    onClick={() => onToggle(uni)}
                                    className="hover:text-white transition-colors"
                                    type="button"
                                >
                                    <X className="h-3 w-3"/>
                                </button>
                            )}
                        </div>
                    ))
                )}
            </div>

            {/* Dropdown Button */}
            {!disabled && (
                <button
                    type="button"
                    onClick={() => setIsOpen(!isOpen)}
                    className="w-full flex items-center justify-between px-4 py-3 rounded-2xl bg-slate-950/50 border border-white/10 hover:border-younify-blue/30 transition-all duration-300 text-slate-400 hover:text-white"
                >
          <span className="text-sm">
            {selectedUnis.length >= 5 ? 'Limite raggiunto (5/5)' : 'Seleziona università'}
          </span>
                    <ChevronRight className={`h-4 w-4 transition-transform ${isOpen ? 'rotate-90' : ''}`}/>
                </button>
            )}

            {/* Dropdown List */}
            <AnimatePresence>
                {isOpen && !disabled && (
                    <motion.div
                        initial={{opacity: 0, height: 0}}
                        animate={{opacity: 1, height: 'auto'}}
                        exit={{opacity: 0, height: 0}}
                        className="overflow-hidden rounded-2xl bg-slate-900/90 backdrop-blur-xl border border-white/10 shadow-2xl"
                    >
                        {/* Search Bar */}
                        <div className="p-3 border-b border-white/5">
                            <input
                                type="text"
                                placeholder="Cerca università..."
                                className="w-full bg-slate-950 rounded-lg p-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-younify-blue"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                autoFocus
                            />
                        </div>

                        {/* Universities List */}
                        <div className="max-h-60 overflow-y-auto p-2">
                            {filteredUniversities.length === 0 ? (
                                <div className="p-4 text-center text-slate-500 text-xs">
                                    Nessuna università trovata
                                </div>
                            ) : (
                                filteredUniversities.map((uni) => {
                                    const isSelected = selectedUnis.includes(uni);
                                    const isDisabled = !isSelected && selectedUnis.length >= 5;

                                    return (
                                        <button
                                            key={uni}
                                            type="button"
                                            onClick={() => !isDisabled && onToggle(uni)}
                                            disabled={isDisabled}
                                            className={`w-full text-left px-4 py-3 rounded-xl transition-all duration-200 ${
                                                isSelected
                                                    ? 'bg-younify-blue/20 text-white border border-younify-blue/30'
                                                    : isDisabled
                                                        ? 'text-slate-600 cursor-not-allowed'
                                                        : 'text-slate-300 hover:bg-white/5 hover:text-white'
                                            }`}
                                        >
                                            <div className="flex items-center justify-between">
                                                <span className="text-sm">{uni}</span>
                                                {isSelected && <CheckCircle2 className="h-4 w-4 text-younify-blue"/>}
                                            </div>
                                        </button>
                                    );
                                })
                            )}
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>

            {error && (
                <p className="text-xs text-rose-400 flex items-center gap-1">
                    <AlertCircle className="h-3 w-3"/>
                    {error}
                </p>
            )}
        </div>
    );
};

// ==================== HELPER: COLORI TOPIC ====================
const getTopicColor = (topic) => {
    const colors = {
        'ESAMI': 'text-rose-300 bg-rose-500/20 border-rose-500/30',
        'AFFITTI': 'text-orange-300 bg-orange-500/20 border-orange-500/30',
        'VITA SOCIALE': 'text-purple-300 bg-purple-500/20 border-purple-500/30',
        'APPUNTI': 'text-teal-300 bg-teal-500/20 border-teal-500/30',
        'GENERALE': 'text-slate-300 bg-slate-500/20 border-slate-500/30',
        'EVENTI': 'text-blue-300 bg-blue-500/20 border-blue-500/30',
        'LAVORO': 'text-emerald-300 bg-emerald-500/20 border-emerald-500/30',
        'SPORT': 'text-yellow-300 bg-yellow-500/20 border-yellow-500/30',
        'default': 'text-slate-400 bg-slate-600/20 border-slate-600/30',
    };
    return colors[topic] || colors['default'];
};


// ==================== COMPONENTE: MODALE CONFERMA ELIMINAZIONE ====================
const ConfirmDeleteModal = ({
                                isOpen,
                                onClose,
                                onConfirm,
                                title,
                                message,
                                type = 'default',
                                confirmText = 'Elimina'
                            }) => {
    const getTypeStyles = () => {
        switch (type) {
            case 'event':
                return {
                    gradient: 'from-amber-600 to-amber-600',
                    icon: <CalendarDays size={24}/>,
                    accentColor: 'amber',
                };
            case 'thread':
                return {
                    gradient: 'from-purple-600 to-purple-500',
                    icon: <MessageSquare size={24}/>,
                    accentColor: 'purple',
                };
            case 'comment':
                return {
                    gradient: 'from-blue-600 to-blue-500',
                    icon: <MessageCircle size={24}/>,
                    accentColor: 'blue',
                };

            case 'accept':
                return {
                    gradient: 'from-emerald-500 to-green-600',
                    icon: <CheckCircle size={24}/>,
                    accentColor: 'emerald',
                };

            case 'reject':
                return {
                    gradient: 'from-rose-600 to-red-600',
                    icon: <X size={24}/>,
                    accentColor: 'rose',
                };

            case 'cancel':
                return {
                    gradient: 'from-slate-600 to-slate-700',
                    icon: <Clock size={24}/>,
                    accentColor: 'slate',
                };
            default:
                return {
                    gradient: 'from-rose-600 to-red-600',
                    icon: <AlertCircle size={24}/>,
                    accentColor: 'rose',
                };
        }
    };

    const styles = getTypeStyles();

    if (!isOpen) return null;

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-[200] flex items-center justify-center p-4">
                {/* Backdrop */}
                <motion.div
                    initial={{opacity: 0}}
                    animate={{opacity: 1}}
                    exit={{opacity: 0}}
                    onClick={onClose}
                    className="absolute inset-0 bg-black/60 backdrop-blur-md"
                />

                {/* Modal Card */}
                <motion.div
                    initial={{scale: 0.9, opacity: 0, y: 20}}
                    animate={{scale: 1, opacity: 1, y: 0}}
                    exit={{scale: 0.9, opacity: 0, y: 20}}
                    transition={{type: 'spring', stiffness: 300, damping: 30}}
                    className="relative w-full max-w-md bg-[#0F131F] border border-white/10 rounded-3xl shadow-2xl overflow-hidden"
                >
                    {/* Header Gradient */}
                    <div className={`absolute top-0 left-0 w-full h-2 bg-gradient-to-r ${styles.gradient}`}></div>

                    {/* Content */}
                    <div className="p-6 md:p-8">
                        {/* Icon */}
                        <div className="flex justify-center mb-6">
                            <div
                                className={`relative w-16 h-16 rounded-full bg-${styles.accentColor}-500/20 flex items-center justify-center`}>
                                <div
                                    className={`absolute inset-0 bg-${styles.accentColor}-500/20 blur-xl rounded-full animate-pulse`}></div>
                                <div className={`relative text-${styles.accentColor}-400`}>
                                    {styles.icon}
                                </div>
                            </div>
                        </div>

                        {/* Title */}
                        <h3 className="text-xl md:text-2xl font-black text-white text-center mb-3">
                            {title}
                        </h3>

                        {/* Message */}
                        <p className="text-sm md:text-base text-slate-400 text-center leading-relaxed mb-8">
                            {message}
                        </p>

                        {/* Buttons */}
                        <div className="flex gap-3">
                            <motion.button
                                whileHover={{scale: 1.02}}
                                whileTap={{scale: 0.98}}
                                onClick={onClose}
                                className="flex-1 px-4 py-3 rounded-xl bg-slate-800 text-slate-300 font-semibold border border-slate-700 hover:bg-slate-700 transition-colors"
                            >
                                Annulla
                            </motion.button>

                            <motion.button
                                whileHover={{scale: 1.02}}
                                whileTap={{scale: 0.98}}
                                onClick={() => {
                                    onConfirm();
                                    onClose();
                                }}
                                className={`flex-1 px-4 py-3 rounded-xl bg-gradient-to-r ${styles.gradient} text-white font-semibold shadow-lg hover:shadow-xl transition-all`}
                            >
                                {confirmText}
                            </motion.button>
                        </div>
                    </div>
                </motion.div>
            </div>
        </AnimatePresence>
    );
};

// ==================== COMPONENTE: BANNER THREAD (PROFILO) ====================
const ProfileThreadItem = ({thread, onDelete}) => {
    const truncateText = (text, maxLength = 150) => {
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    };

    return (
        <motion.div
            initial={{opacity: 0, x: -20}}
            animate={{opacity: 1, x: 0}}
            exit={{opacity: 0, x: 20}}
            transition={{duration: 0.3}}
            className="group relative flex flex-col gap-3 p-5 rounded-2xl bg-slate-950/30 backdrop-blur-xl border border-white/10 hover:border-purple-500/30 transition-all overflow-hidden"
        >
            {/* Header: Topic + Data */}
            <div className="flex items-center justify-between gap-3">
                <span
                    className={`px-3 py-1 rounded-full text-[15px] font-bold border truncate ${getTopicColor(thread.flag)}`}>
                    {thread.flag}
                </span>
                <span className="text-xs text-slate-500 flex-shrink-0">
                    {new Date(thread.publicationDate).toLocaleDateString('it-IT', {
                        day: 'numeric',
                        month: 'short'
                    })}
                </span>
            </div>

            {/* Contenuto Thread */}
            <p className="text-lg text-slate-300 leading-relaxed font-light">
                {truncateText(thread.content)}
            </p>

            {/* Info aggiuntive: CFU + Università */}
            <div className="flex items-center gap-3 text-xs text-slate-500">
                <span className="flex items-center gap-1">
                    <Award size={12}/>
                    CFU: {thread.CFU}
                </span>

            </div>

            {/* Bottone Elimina */}
            <div className="flex justify-end" onClick={(e) => e.stopPropagation()}>
                <motion.button
                    whileHover={{scale: 1.05}}
                    whileTap={{scale: 0.95}}
                    onClick={() => onDelete(thread.ID)}
                    className="p-2 rounded-xl bg-rose-500/20 text-rose-400 hover:bg-rose-500 hover:text-white border border-rose-500/30 transition-all"
                    title="Elimina Thread"
                >
                    <X size={16}/>
                </motion.button>
            </div>
        </motion.div>
    );
};


// ==================== COMPONENTE: BANNER COMMENTO (PROFILO) ====================
const ProfileCommentItem = ({comment, onDelete}) => {
    const truncateText = (text, maxLength = 120) => {
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    };

    return (
        <motion.div
            initial={{opacity: 0, x: -20}}
            animate={{opacity: 1, x: 0}}
            exit={{opacity: 0, x: 20}}
            transition={{duration: 0.3}}
            className="group relative flex flex-col gap-3 p-5 rounded-2xl bg-slate-950/30 backdrop-blur-xl border border-white/10 hover:border-blue-500/30 transition-all overflow-hidden"
        >
            {/* Header: Data */}
            <div className="flex items-start justify-between gap-3">
                <div className="flex-1 min-w-0">
                    {comment.threadTitle && (
                        <>
                            <p className="text-[10px] text-slate-500 uppercase font-bold mb-1">
                                In risposta a
                            </p>
                            <p className="text-lg text-blue-400 font-semibold truncate">
                                {comment.threadTitle}
                            </p>
                        </>
                    )}
                </div>
                <span className="text-xs text-slate-500 flex-shrink-0">
                    {new Date(comment.publicationDate).toLocaleDateString('it-IT', {
                        day: 'numeric',
                        month: 'short'
                    })}
                </span>
            </div>

            {/* Contenuto Commento */}
            <p className="text-base text-slate-300 leading-relaxed font-light">
                {truncateText(comment.content)}
            </p>

            {/* Info aggiuntive: CFU (se > 0) */}
            {comment.CFU > 0 && (
                <div className="flex items-center gap-1 text-xs text-slate-500">
                    <Award size={12}/>
                    CFU: {comment.CFU}
                </div>
            )}

            {/* Bottone Elimina */}
            <div className="flex justify-end" onClick={(e) => e.stopPropagation()}>
                <motion.button
                    whileHover={{scale: 1.05}}
                    whileTap={{scale: 0.95}}
                    onClick={() => onDelete(comment.ID)}
                    className="p-2 rounded-xl bg-rose-500/20 text-rose-400 hover:bg-rose-500 hover:text-white border border-rose-500/30 transition-all"
                    title="Elimina Commento"
                >
                    <X size={16}/>
                </motion.button>
            </div>
        </motion.div>
    );
};


// ==================== COMPONENTE: CARD RICHIESTA MATCH ====================
const MatchRequestCard = ({user, type, onAccept, onReject, onCancel}) => {

    const isReceived = type === 'received';

    return (
        <motion.div
            initial={{opacity: 0, scale: 0.95}}
            animate={{opacity: 1, scale: 1}}
            exit={{opacity: 0, scale: 0.95}}
            transition={{duration: 0.3}}
            className="group relative flex items-center gap-4 p-5 rounded-2xl bg-slate-950/30 backdrop-blur-xl border border-white/10 hover:border-emerald-500/30 transition-all overflow-hidden"
        >
            {/* Avatar */}
            <div className="relative flex-shrink-0">
                <div className="absolute inset-0 bg-emerald-500/20 blur-xl rounded-full"/>
                <div className="relative w-16 h-16 rounded-full overflow-hidden ring-2 ring-white/10">
                    <img src={user.senderPhoto} alt={user.senderName} className="w-full h-full object-cover"/>
                </div>
                {isReceived && (
                    <div
                        className="absolute -bottom-1 -right-1 w-6 h-6 rounded-full bg-emerald-500 border-2 border-slate-900 flex items-center justify-center">
                        <Heart size={12} className="text-white fill-white"/>
                    </div>
                )}
            </div>

            {/* Info */}
            <div className="flex-1 min-w-0">
                <h4 className="text-base font-bold text-white mb-1 truncate">{user.senderName}</h4>
                <p className="text-sm text-slate-400 truncate mb-2">{user.senderCdl}</p>

                {!isReceived && (
                    <span className="inline-flex items-center gap-1 text-xs text-slate-500">
            <Clock size={12}/>
            In attesa di risposta
          </span>
                )}
            </div>

            {/* Bottoni Azione */}
            <div className="flex items-center gap-2 flex-shrink-0">
                {isReceived ? (
                    <>
                        <motion.button
                            whileHover={{scale: 1.05}}
                            whileTap={{scale: 0.95}}
                            onClick={onAccept}
                            className="p-2.5 rounded-xl bg-emerald-500/20 text-emerald-400 hover:bg-emerald-500 hover:text-white border border-emerald-500/30 transition-all"
                            title="Accetta"
                        >
                            <Check size={18}/>
                        </motion.button>
                        <motion.button
                            whileHover={{scale: 1.05}}
                            whileTap={{scale: 0.95}}
                            onClick={onReject}
                            className="p-2.5 rounded-xl bg-rose-500/20 text-rose-400 hover:bg-rose-500 hover:text-white border border-rose-500/30 transition-all"
                            title="Rifiuta"
                        >
                            <X size={18}/>
                        </motion.button>
                    </>
                ) : (
                    <motion.button
                        whileHover={{scale: 1.05}}
                        whileTap={{scale: 0.95}}
                        onClick={onCancel}
                        className="px-4 py-2 rounded-xl text-sm font-semibold bg-slate-800/50 text-slate-400 hover:bg-rose-500/20 hover:text-rose-400 border border-slate-700/50 hover:border-rose-500/30 transition-all"
                    >
                        Annulla
                    </motion.button>
                )}
            </div>
        </motion.div>
    );
};

// ==================== COMPONENTE: LISTA MATCH (Vista Lista) ====================
const MatchList = ({matches}) => {
    return (
        <div className="space-y-3">
            {matches.map((match, index) => (
                <motion.div
                    key={match.matchID}
                    initial={{opacity: 0, x: -20}}
                    animate={{opacity: 1, x: 0}}
                    transition={{delay: index * 0.05}}
                    className="group flex items-center justify-between p-4 rounded-2xl bg-slate-950/30 backdrop-blur-xl border border-white/10 hover:border-purple-500/30 transition-all"
                >
                    {/* Left: Avatar + Info */}
                    <div className="flex items-center gap-4 flex-1 min-w-0">
                        <div
                            className="relative w-14 h-14 rounded-full overflow-hidden ring-2 ring-white/10 flex-shrink-0">
                            <img src={match.matcherPhoto} alt={match.matcherName}
                                 className="w-full h-full object-cover"/>
                        </div>
                        <div className="flex-1 min-w-0">
                            <h4 className="text-base font-bold text-white mb-0.5 truncate">{match.matcherName}</h4>
                            <p className="text-sm text-slate-400 truncate">{match.matcherUni}, </p>
                            <p className="text-sm text-slate-400 truncate">{match.matcherDegree}</p>
                        </div>
                    </div>

                    {/* Right: Instagram Button */}
                    <motion.a
                        href={`https://${match.matcherContact}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        whileHover={{scale: 1.05}}
                        whileTap={{scale: 0.95}}
                        className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-purple-600 to-pink-600 text-white font-semibold text-sm shadow-lg hover:shadow-purple-500/30 transition-all flex-shrink-0"
                    >
                        <Instagram size={16}/>
                        <span className="hidden sm:inline">Contatta</span>
                    </motion.a>
                </motion.div>
            ))}
        </div>
    );
};


// ==================== COMPONENTE BANNER EVENTO (PROFILO) ====================
const ProfileEventItem = ({event, isHost, onView, onUnjoin, onEdit, onDelete}) => {
    const isEventPast = new Date(event.dateAndTime) < new Date();

    return (
        <motion.div
            initial={{opacity: 0, x: -20}}
            animate={{opacity: 1, x: 0}}
            exit={{opacity: 0, x: 20}}
            transition={{duration: 0.3}}
            className={`group relative flex items-center gap-4 p-4 rounded-2xl bg-slate-900/40 backdrop-blur-xl border border-white/10 hover:border-younify-blue/30 transition-all cursor-pointer overflow-hidden ${
                isEventPast ? 'opacity-70' : ''
            }`}
            onClick={onView}
        >
            {/* Immagine */}
            <div
                className="relative w-20 h-20 md:w-24 md:h-24 rounded-xl overflow-hidden flex-shrink-0 ring-2 ring-white/10">
                <img
                    src={event.posterUrl}
                    alt={event.name}
                    className={`w-full h-full object-cover group-hover:scale-110 transition-transform duration-300 ${
                        isEventPast ? 'grayscale' : ''
                    }`}
                />
                {isEventPast && (
                    <div className="absolute inset-0 bg-black/60 flex items-center justify-center">
                        <Clock size={20} className="text-slate-400"/>
                    </div>
                )}
            </div>

            {/* Info Centro */}
            <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
          <span className="text-xs font-bold text-younify-blue uppercase">
            {event.city}
          </span>
                    {isEventPast && (
                        <span
                            className="px-2 py-0.5 text-[10px] font-bold text-slate-500 bg-slate-800/50 rounded-full uppercase border border-slate-700/50">
              Concluso
            </span>
                    )}
                </div>
                <h4 className="text-base md:text-lg font-bold text-white mb-1 truncate">
                    {event.name}
                </h4>
                <div className="flex items-center gap-3 text-xs md:text-sm text-slate-400">
                    <div className="flex items-center gap-1">
                        <Calendar size={14} className="flex-shrink-0"/>
                        <span className="truncate">
              {new Date(event.dateAndTime).toLocaleDateString('it-IT', {
                  day: 'numeric',
                  month: 'short',
                  hour: '2-digit',
                  minute: '2-digit'
              })}
            </span>
                    </div>
                    <div className="flex items-center gap-1">
                        <Users size={14} className="flex-shrink-0"/>
                        <span>{event.participantsCount}</span>
                    </div>
                </div>
            </div>

            {/* Bottoni Azione */}
            <div className="flex items-center gap-2 flex-shrink-0" onClick={(e) => e.stopPropagation()}>
                {isHost ? (
                    // HOST: Modifica + Elimina SOLO se NON è passato
                    <>
                        {!isEventPast ? (
                            <>
                                <motion.button
                                    whileHover={{scale: 1.05}}
                                    whileTap={{scale: 0.95}}
                                    onClick={() => onEdit(event)}
                                    className="p-2 md:p-2.5 rounded-xl bg-younify-blue/20 text-younify-blue hover:bg-younify-blue hover:text-white border border-younify-blue/30 transition-all"
                                    title="Modifica"
                                >
                                    <Edit3 size={16}/>
                                </motion.button>
                                <motion.button
                                    whileHover={{scale: 1.05}}
                                    whileTap={{scale: 0.95}}
                                    onClick={onDelete}
                                    className="p-2 md:p-2.5 rounded-xl bg-rose-500/20 text-rose-400 hover:bg-rose-500 hover:text-white border border-rose-500/30 transition-all"
                                    title="Elimina"
                                >
                                    <X size={16}/>
                                </motion.button>
                            </>
                        ) : (
                            // Badge "Storico" per eventi passati (HOST)
                            <div
                                className="px-3 py-2 md:px-4 md:py-2 rounded-xl text-sm font-semibold bg-slate-800/50 text-slate-500 border border-slate-700/50 flex items-center gap-2">
                                <Clock size={14}/>
                                <span className="hidden md:inline">Evento passato</span>
                            </div>
                        )}
                    </>
                ) : (
                    // USER: Disdici solo se NON è passato
                    <>
                        {!isEventPast ? (
                            <motion.button
                                whileHover={{scale: 1.05}}
                                whileTap={{scale: 0.95}}
                                onClick={onUnjoin}
                                className="px-3 py-2 md:px-4 md:py-2 rounded-xl text-sm font-semibold transition-all flex items-center gap-2 bg-rose-500/20 text-rose-400 hover:bg-rose-500 hover:text-white border border-rose-500/30"
                                title="Disdici"
                            >
                                <X size={14}/>
                                <span className="hidden md:inline">Disdici</span>
                            </motion.button>
                        ) : (
                            // Badge "Passato" per eventi passati (USER)
                            <div
                                className="px-3 py-2 md:px-4 md:py-2 rounded-xl text-sm font-semibold bg-slate-800/50 text-slate-500 border border-slate-700/50 flex items-center gap-2">
                                <Clock size={14}/>
                                <span className="hidden md:inline">Passato</span>
                            </div>
                        )}
                    </>
                )}
            </div>
        </motion.div>
    );
};


// ========================================
// COMPONENTE PRINCIPALE
// ========================================
const ProfilePage = () => {

    const [isHost, setIsHost] = useState(false);
    const navigate = useNavigate();

    const [profileData, setProfileData] = useState({
        // Campi comuni
        avatar: '',
        email: '',
        university: '',
        degreeProgram: '',
        cfuCount: 0,

        // Campi USER
        firstName: '',
        lastName: '',
        dateOfBirth: '',
        contactsInfo: '',
        matchCount: 0,

        // Campi HOST
        hostName: '',
        province: '',
        city: '',
        uniList: [],
        piva: '',
        pec: '',

        // Dati aggiuntivi (da backend)
        eventsCreated: [],
        eventsJoined: [],
        threads: [],
        comments: [],
        matching: {
            received: [],
            sent: [],
            history: []
        }
    });

    const [originalData, setOriginalData] = useState({});

    const [avatarPreview, setAvatarPreview] = useState('load');
    const [isEditing, setIsEditing] = useState(false);
    const [errors, setErrors] = useState({});
    const [isEditingUniversity, setIsEditingUniversity] = useState(false);
    const [universityErrors, setUniversityErrors] = useState({});
    const [isEditingPassword, setIsEditingPassword] = useState(false); // ← NUOVO
    const [passwordErrors, setPasswordErrors] = useState({}); // ← NUOVO
    const [passwords, setPasswords] = useState({
        newPassword: '',
        confirmPassword: '',
    });
    const [events, setEvents] = useState([]);
    const [detailEvent, setDetailEvent] = useState(null);
    const [showParticipantsPanel, setShowParticipantsPanel] = useState(false);

    // ==================== STATI MODIFICA EVENTO ====================
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editFormData, setEditFormData] = useState({
        name: '',
        description: '',
        dateAndTime: '',
        street: '',
        city: '',
        CAP: '',
        poster: null,
    });
    const [editEventId, setEditEventId] = useState(null);
    const [editFormErrors, setEditFormErrors] = useState({});
    const [editPreviewImage, setEditPreviewImage] = useState(null);
    const [editFileError, setEditFileError] = useState('');
    const [showEditCityDropdown, setShowEditCityDropdown] = useState(false);
    const fileInputEditRef = useRef(null);
    const [threads, setThreads] = useState([]);
    const [comments, setComments] = useState([]);
    const [socialTab, setSocialTab] = useState('threads');
    const [matchReceived, setMatchReceived] = useState([]);
    const [matchSent, setMatchSent] = useState([]);
    const [matchHistory, setMatchHistory] = useState([]);
    const [matchViewMode, setMatchViewMode] = useState('list');
    const [showAllReceived, setShowAllReceived] = useState(false);
    const [showAllSent, setShowAllSent] = useState(false);
    const INITIAL_DISPLAY_LIMIT = 5;
    const [showAllEvents, setShowAllEvents] = useState(false);
    const [showAllThreads, setShowAllThreads] = useState(false);
    const [showAllComments, setShowAllComments] = useState(false);
    const [showAllHistory, setShowAllHistory] = useState(false);
    const INITIAL_ITEMS_LIMIT = 5;

// Richieste da mostrare (limitate o tutte)
    const displayedReceived = showAllReceived ? matchReceived : matchReceived.slice(0, INITIAL_DISPLAY_LIMIT);
    const displayedSent = showAllSent ? matchSent : matchSent.slice(0, INITIAL_DISPLAY_LIMIT);
    const displayedEvents = showAllEvents ? events : events.slice(0, INITIAL_ITEMS_LIMIT);
    const displayedThreads = showAllThreads ? threads : threads.slice(0, INITIAL_ITEMS_LIMIT);
    const displayedComments = showAllComments ? comments : comments.slice(0, INITIAL_ITEMS_LIMIT);
    const displayedHistory = showAllHistory ? matchHistory : matchHistory.slice(0, INITIAL_ITEMS_LIMIT);

    // ==================== VALIDAZIONE DATA MINIMA ====================
    const getMinDateTimeForEdit = () => {
        const now = new Date();
        now.setHours(now.getHours() + 1); // Minimo 1 ora nel futuro
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        return now.toISOString().slice(0, 16);
    };

    const [minEditDateTime, setMinEditDateTime] = useState(getMinDateTimeForEdit());
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const loadProfileData = async () => {
            try {
                setIsLoading(true);
                const isHostRole = localStorage.getItem('isHost') === 'true';
                setIsHost(isHostRole);

                // 1. Carica dati profilo base
                const response = isHostRole
                    ? await profileService.getHostProfile()
                    : await profileService.getUserProfile();

                if (response.data) {

                    const mappedData = {
                        ...response.data,
                        cfuCount: response.data.totalCfu || 0,
                        matchCount: response.data.totalMatch || 0,
                        degreeProgram: response.data.cdl,
                        ...(isHostRole && {
                            uniList: response.data.uniList
                                ? (typeof response.data.uniList === 'string'
                                    ? response.data.uniList.split(', ').filter(u => u)
                                    : response.data.uniList)
                                : []
                        }),
                        pec: response.data.PEC || "",
                        piva: response.data.PIVA || ""
                    };

                    setProfileData(mappedData);
                    setOriginalData(mappedData);
                    setAvatarPreview(mappedData.avatar || '');
                }

                // 2. CARICA EVENTI
                if (isHostRole) {
                    const eventsResponse = await eventService.getMyEvents();
                    setEvents(eventsResponse?.data?.content || []);

                } else {
                    const eventsResponse = await eventService.getJoinedEvents();
                    setEvents(eventsResponse?.data?.content || []);
                }

                try {
                    const [myThreads, myComments] = await Promise.all([
                        threadService.getMyThreads(0, 100),
                        threadService.getMyComments(0, 100)
                    ]);


                    setThreads(myThreads?.data?.data?.content || []);
                    setComments(myComments?.data?.data?.content || []);

                } catch (error) {

                    toast.warning('Thread e commenti non disponibili');
                }

                // 4. CARICA MATCHING (solo per USER)
                if (!isHostRole) {
                    const [receivedResponse, sentResponse, historyResponse] = await Promise.all([
                        matchService.getReceivedPendingMatches(),
                        matchService.getSentPendingMatches(),
                        matchService.getAcceptedMatches()
                    ]);

                    setMatchReceived(receivedResponse?.data?.content || []);
                    setMatchSent(sentResponse?.data?.content || []);
                    setMatchHistory(historyResponse?.data?.content || []);
                }

            } catch (error) {
                toast.error('Impossibile caricare il profilo');
            } finally {
                setIsLoading(false);
            }
        };

        loadProfileData();
    }, []);


// Aggiorna minDateTime quando si apre il modale
    useEffect(() => {
        if (isEditModalOpen) {
            setMinEditDateTime(getMinDateTimeForEdit());
        }
    }, [isEditModalOpen]);


    // STATI MODALE CONFERMA
    const [confirmModal, setConfirmModal] = useState({
        isOpen: false,
        title: '',
        message: '',
        type: 'default',
        confirmText: 'Elimina',  // ← AGGIUNGI QUESTA RIGA
        onConfirm: () => {
        },
    });

// Helper per aprire il modale
    const openConfirmModal = (config) => {
        setConfirmModal({
            isOpen: true,
            ...config,
        });
    };


    const closeConfirmModal = () => setConfirmModal({
        isOpen: false,
        title: '',
        message: '',
        type: 'default',
        confirmText: 'Elimina',
        onConfirm: () => {
        }
    });


// Lista comuni (già presente in ProfilePage? Se no, aggiungi)
    const comuniList = Array.isArray(rawCitiesData) ? rawCitiesData : rawCitiesData?.default || [];

// Validazione città
    const isEditCityValid = (() => {
        if (editFormData.city.length === 0) return true;
        return comuniList.some((c) => c.nome.toLowerCase() === editFormData.city.toLowerCase());
    })();

    // Dopo tutti gli useState, aggiungi questi useMemo:

// Controlla se ci sono modifiche nei Dati Personali
    const hasPersonalDataChanges = useMemo(() => {
        if (!isEditing) return false;

        if (isHost) {
            return (
                profileData.hostName !== originalData.hostName ||
                profileData.province !== originalData.province ||
                profileData.city !== originalData.city ||
                JSON.stringify(profileData.uniList.sort()) !== JSON.stringify(originalData.uniList.sort())
            );
        } else {
            return (
                profileData.firstName !== originalData.firstName ||
                profileData.lastName !== originalData.lastName ||
                profileData.dateOfBirth !== originalData.dateOfBirth ||
                profileData.contactsInfo !== originalData.contactsInfo
            );
        }
    }, [profileData, originalData, isEditing, isHost]);

// Controlla se ci sono modifiche nei Dati Universitari
    const hasUniversityChanges = useMemo(() => {
        if (!isEditingUniversity) return false;

        return (
            profileData.email !== originalData.email ||
            profileData.university !== originalData.university ||
            profileData.degreeProgram !== originalData.degreeProgram
        );
    }, [profileData.email, profileData.university, profileData.degreeProgram, originalData.email, originalData.university, originalData.degreeProgram, isEditingUniversity]);

// Controlla se la password è valida e completa
    const isPasswordValid = useMemo(() => {
        if (!isEditingPassword) return false;

        return (
            passwords.newPassword.length > 0 &&
            passwordRegex.test(passwords.newPassword) &&
            passwords.newPassword === passwords.confirmPassword &&
            passwords.confirmPassword.length > 0
        );
    }, [passwords.newPassword, passwords.confirmPassword, isEditingPassword]);


    // Logica Province/Città
    const italianPlaces = useMemo(() => {
        const places = {};
        if (!rawCitiesData || !Array.isArray(rawCitiesData)) return {};

        rawCitiesData.forEach(city => {
            const nomeProvincia = city.provincia?.nome || city.provincia;
            const nomeComune = city.nome;
            if (nomeProvincia && nomeComune) {
                if (!places[nomeProvincia]) places[nomeProvincia] = [];
                places[nomeProvincia].push(nomeComune);
            }
        });
        Object.keys(places).forEach(prov => places[prov].sort());

        return places;
    }, []);

    const availableCities = useMemo(() => {
        return profileData.province ? italianPlaces[profileData.province] || [] : [];
    }, [profileData.province, italianPlaces]);

    const getMaxDate = () => {
        const today = new Date();
        const currentMonth = today.getMonth(); // 0=Gennaio, ..., 8=Settembre
        const currentYear = today.getFullYear();
        let referenceYear;

        // Se siamo da Gennaio (0) ad Agosto (7), l'anno accademico è quello iniziato l'anno scorso
        if (currentMonth < 8) {
            referenceYear = currentYear - 1;
        } else {
            // Da Settembre in poi, siamo nel nuovo anno accademico
            referenceYear = currentYear;
        }

        // 18 anni compiuti entro il 30 Aprile dell'anno successivo all'immatricolazione
        const cutoffYear = referenceYear - 18;
        return `${cutoffYear}-04-30`;
    };

    const getMinDate = () => {
        return "1900-01-01"; // Data minima assoluta
    };

    const validateEmail = (email) => {
        return ALLOWED_EMAIL_DOMAINS.some(domain => email.endsWith(domain));
    };

// ==================== HANDLERS THREAD & COMMENTI ====================
    // Handler Elimina Thread
    const handleDeleteThread = (threadId) => {

        const thread = threads.find(t => t.ID === threadId);

        if (!thread) {
            return;
        }

        openConfirmModal({
            title: 'Elimina Thread',
            message: 'Sei sicuro di voler eliminare questo thread? Verranno eliminati anche tutti i commenti associati.',
            type: 'thread',
            onConfirm: async () => {
                try {

                    await threadService.deleteThread(threadId);

                    // Aggiorna stato locale
                    setThreads(prev => prev.filter(t => t.ID !== threadId));
                    toast.success('Thread eliminato');
                } catch (error) {

                    if (error.response?.data?.message) {

                        toast.error(error.response.data.message);
                    } else {
                        toast.error('Impossibile eliminare il thread. Potrebbe avere commenti associati.');
                    }
                }
            }
        });
    };


    // Handler Elimina Commento
    const handleDeleteComment = (commentId) => {
        const comment = comments.find(c => c.id === commentId);

        openConfirmModal({
            title: 'Elimina Commento',
            message: `Sei sicuro di voler eliminare questo commento? Questa azione non può essere annullata.`,
            type: 'comment',
            onConfirm: async () => {
                try {

                    await threadService.deleteComment(commentId);

                    setComments(prev => prev.filter(c => c.id !== commentId));
                    toast.success('Commento eliminato');
                } catch (error) {
                    toast.error('Impossibile eliminare il commento');
                }
            }
        });
    };


    const handleUnjoinEvent = (event) => {
        if (!event) {
            return;
        }

        const eventId = event.ID;
        const eventName = event.name;

        openConfirmModal({
            title: 'Disdici Partecipazione',
            message: `Confermi di voler disdire la partecipazione a "${eventName}"?`,
            type: 'event',
            confirmText: 'Disdici',
            onConfirm: async () => {

                await eventService.unjoinEvent(eventId);
                setEvents(prev => prev.filter(ev => ev.ID !== eventId));
            }
        });
    };

    const handleDeleteEvent = (event) => {
        if (!event) {
            return;
        }

        const eventId = event.ID;
        const eventName = event.name;

        openConfirmModal({
            title: 'Elimina Evento',
            message: `Sei sicuro di voler eliminare "${eventName}"?`,
            type: 'event',
            confirmText: 'Elimina',
            onConfirm: async () => {

                await eventService.deleteEvent(eventId);
                setEvents(prev => prev.filter(ev => ev.ID !== eventId));
            }
        });
    };


    const handleEditEvent = (eventId) => {
        toast.info('Funzione modifica in sviluppo');
    };

    const handleViewEventDetail = (event) => {
        setDetailEvent(event);
    };

    useEffect(() => {
        if (!detailEvent) {
            setShowParticipantsPanel(false);
        }
    }, [detailEvent]);

    // ==================== HANDLERS MATCHING ====================

// Handler Accetta Match
    const handleAcceptMatch = async (matchId) => {
        const match = matchReceived.find(m => m.matchID === matchId);
        if (!match) return;

        openConfirmModal({
            title: 'Accetta Match',
            message: `Confermi di voler accettare il match con ${match.senderName}?`,
            type: 'accept',
            confirmText: 'Accetta',
            onConfirm: async () => {
                try {
                    await matchService.reactToMatch(matchId, 'ACCEPTED');
                    setMatchReceived(prev => prev.filter(m => m.matchID !== matchId));
                    setMatchHistory(prev => [match, ...prev]);
                } catch (error) {

                    toast.error('Impossibile accettare il match');
                }
            }
        });
    };

    const handleRejectMatch = async (matchId) => {
        const match = matchReceived.find(m => m.matchID === matchId);
        if (!match) return;

        openConfirmModal({
            title: 'Rifiuta Match',
            message: `Sei sicuro di voler rifiutare il match con ${match.senderName}?`,
            type: 'reject',
            confirmText: 'Rifiuta',
            onConfirm: async () => {
                try {

                    await matchService.reactToMatch(matchId, 'REJECTED');
                    setMatchReceived(prev => prev.filter(m => m.matchID !== matchId));
                } catch (error) {

                    toast.error('Impossibile rifiutare il match');
                }
            }
        });
    };


// Handler Annulla Richiesta Inviata
    const handleCancelSentMatch = async (matchId) => {
        const match = matchSent.find(m => m.matchID === matchId);
        if (!match) return;

        openConfirmModal({
            title: 'Annulla Richiesta',
            message: `Vuoi annullare la richiesta di match inviata a ${match.senderName}?`,
            type: 'cancel',
            confirmText: 'Ritira richiesta',
            onConfirm: async () => {
                try {

                    await matchService.deleteMatch(matchId);

                    // Rimuovi dallo stato locale
                    setMatchSent(prev => prev.filter(m => m.matchID !== matchId));

                    // Toast gestito automaticamente da matchHandler
                } catch (error) {

                    toast.error('Impossibile annullare la richiesta');
                }
            }
        });
    };


// ==================== useEffect: Carica Dati Matching ====================
// Aggiungi nel useEffect principale (cerca quello che carica eventi/threads)


    const handleInputChange = (e) => {
        const {name, value} = e.target;

        // Aggiorna il valore
        if (name === "province") {
            setProfileData(prev => ({...prev, province: value, city: ""}));
        } else {
            setProfileData(prev => ({...prev, [name]: value}));
        }

        // Validazione in tempo reale
        let errorMsg = null;

        if (name === "firstName" || name === "lastName") {
            // Se c'è del testo ma è inferiore a 3 caratteri
            if (value && value.trim().length < 3) {
                errorMsg = "Minimo 3 caratteri";
            }
        }

        if (name === "dateOfBirth") {
            const maxAllowed = getMaxDate(); // es. "2007-04-30"
            const minAllowed = getMinDate(); // "1900-01-01"

            if (value > maxAllowed) {
                errorMsg = "Non puoi modificare con date di nascita di persone che non hanno ancora l'età per iscriversi all'università";
            } else if (value < minAllowed) {
                errorMsg = "Data non valida";
            }
        }

        if (name === "contactsInfo") {
            // Rimuove eventuali errori mentre scrive
            if (value && value.trim().length > 0) {
                errorMsg = null;
            }
        }

        // Aggiorna gli errori
        setErrors(prev => ({...prev, [name]: value ? errorMsg : null}));
    };

    // Dopo handleInputChange, aggiungi:
    const handleUniversityChange = (e) => {
        const {name, value} = e.target;

        setProfileData(prev => ({...prev, [name]: value}));

        // Validazione real-time per email
        let errorMsg = null;
        if (name === "email") {
            if (value && !validateEmail(value)) {
                errorMsg = "Email universitaria non valida";
            }
        }

        setUniversityErrors(prev => ({...prev, [name]: value ? errorMsg : null}));
    };

    const handleUniversitySelect = (field, value) => {
        setProfileData(prev => ({...prev, [field]: value}));
        setUniversityErrors(prev => ({...prev, [field]: null}));
    };

    const handleEditUniversity = () => {
        setIsEditingUniversity(true);
    };

    const handleCancelUniversity = () => {
        setProfileData(originalData);
        setIsEditingUniversity(false);
        setUniversityErrors({});
        toast.info("Modifiche annullate");
    };

    const handleSaveUniversity = async () => {
        // Validazione completa
        const newErrors = {};

        // Valida Email
        if (!profileData.email) {
            newErrors.email = "Email richiesta";
        } else if (!validateEmail(profileData.email)) {
            newErrors.email = "Email universitaria non valida";
        }

        // Valida Università
        if (!profileData.university) {
            newErrors.university = "Seleziona un'università";
        }

        // Valida Corso di Laurea
        if (!profileData.degreeProgram) {
            newErrors.degreeProgram = "Seleziona un corso";
        }

        // Se ci sono errori, non salvare
        if (Object.keys(newErrors).length > 0) {
            setUniversityErrors(newErrors);
            toast.error("Correggi gli errori prima di salvare");
            return;
        }


        try {

            const editUniversityRequest = {
                email: profileData.email,
                university: profileData.university,
                cdl: profileData.degreeProgram
            };


            const response = await profileService.changeUniversity(editUniversityRequest, navigate);


            setOriginalData(profileData);
            setIsEditingUniversity(false);
            setUniversityErrors({});

        } catch (error) {

            // Gestione errori specifici
            if (error.response?.status === 400) {

                toast.error(error.response.data?.message || "Dati non validi");
            } else if (error.response?.status === 409) {

                toast.error("Email già in uso da un altro account");
            } else if (error.response?.status === 500) {

                toast.error("Errore di validazione. Verifica i dati inseriti");
            } else {
                toast.error("Errore durante il salvataggio. Riprova più tardi");
            }
        }

    };


// Dopo handleSaveUniversity, aggiungi:
    const handlePasswordChange = (e) => {
        const {name, value} = e.target;

        setPasswords(prev => ({...prev, [name]: value}));

        // Validazione real-time
        let errorMsg = null;
        if (name === "newPassword") {
            if (value && !passwordRegex.test(value)) {
                errorMsg = "Min. 8 car. Maiusc, minusc, num e simbolo";
            }
        }
        if (name === "confirmPassword") {
            if (value && value !== passwords.newPassword) {
                errorMsg = "Le password non coincidono";
            }
        }

        setPasswordErrors(prev => ({...prev, [name]: value ? errorMsg : null}));
    };

    const handleEditPassword = () => {
        setIsEditingPassword(true);
    };

    const handleCancelPassword = () => {
        setPasswords({newPassword: '', confirmPassword: ''});
        setIsEditingPassword(false);
        setPasswordErrors({});
        toast.info("Modifiche annullate");
    };

    const handleSavePassword = async () => {
        // Validazione
        const newErrors = {};

        if (!passwords.newPassword) {
            newErrors.newPassword = 'Password richiesta';
        } else if (!passwordRegex.test(passwords.newPassword)) {
            newErrors.newPassword = 'Min. 8 car. Maiusc, minusc, num e simbolo';
        }

        if (!passwords.confirmPassword) {
            newErrors.confirmPassword = 'Conferma password';
        } else if (passwords.newPassword !== passwords.confirmPassword) {
            newErrors.confirmPassword = 'Le password non coincidono';
        }

        if (Object.keys(newErrors).length > 0) {
            setPasswordErrors(newErrors);
            toast.error('Correggi gli errori prima di salvare');
            return;
        }


        const changePassRequest = {
            password: passwords.newPassword,
            confirmPassword: passwords.confirmPassword
        };

        await profileService.changePassword(changePassRequest);

        // Reset
        setPasswords({oldPassword: '', newPassword: '', confirmPassword: ''});
        setIsEditingPassword(false);
        setPasswordErrors({});
    };


    const handleUniToggle = (uni) => {
        setProfileData((prev) => {
            const currentList = prev.uniList || [];
            if (currentList.includes(uni)) {
                return {...prev, uniList: currentList.filter(item => item !== uni)};
            } else {
                return currentList.length < 5 ? {...prev, uniList: [...currentList, uni]} : prev;
            }
        });
    };

    const handleAvatarUpload = (e) => {
        const file = e.target.files?.[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setAvatarPreview(reader.result);
                toast.success('Foto profilo aggiornata!');
            };
            reader.readAsDataURL(file);
        }
    };

    const handleEdit = () => {
        setIsEditing(true);
    };

    const handleCancel = () => {
        setProfileData(originalData);
        setIsEditing(false);
        setErrors({});
        toast.info("Modifiche annullate");
    };


    const [isSaving, setIsSaving] = useState(false);
    const handleSave = async () => {

        if (isSaving) {
            return;
        }


        const newErrors = {};

        if (isHost) {
            // Validazione HOST
            if (!profileData.hostName) newErrors.hostName = 'Nome attività richiesto';
            else if (profileData.hostName.trim().length < 3) newErrors.hostName = 'Minimo 3 caratteri';

            if (!profileData.province) newErrors.province = 'Provincia richiesta';
            if (!profileData.city) newErrors.city = 'Città richiesta';
        } else {
            // Validazione USER
            if (!profileData.firstName) newErrors.firstName = 'Nome richiesto';
            else if (profileData.firstName.trim().length < 3) newErrors.firstName = 'Minimo 3 caratteri';

            if (!profileData.lastName) newErrors.lastName = 'Cognome richiesto';
            else if (profileData.lastName.trim().length < 3) newErrors.lastName = 'Minimo 3 caratteri';

            if (!profileData.dateOfBirth) newErrors.dateOfBirth = 'Data di nascita richiesta';
            else {
                const maxAllowed = getMaxDate();
                const minAllowed = getMinDate();
                if (profileData.dateOfBirth > maxAllowed) {
                    newErrors.dateOfBirth = 'Non hai ancora l\'età per iscriverti';
                } else if (profileData.dateOfBirth < minAllowed) {
                    newErrors.dateOfBirth = 'Data non valida';
                }
            }

            const instaUser = profileData.contactsInfo.replace(/instagram\.com/g, '').trim();
            if (!instaUser) newErrors.contactsInfo = 'Username Instagram richiesto';
        }

        // Se ci sono errori, non salvare
        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            toast.error('Correggi gli errori prima di salvare');
            return;
        }

        setIsSaving(true);

        try {
            if (isHost) {
                // Prepara i dati per HOST
                const editHostInfoRequest = {
                    hostName: profileData.hostName,
                    province: profileData.province,
                    city: profileData.city,
                    uniList: Array.isArray(profileData.uniList)
                        ? profileData.uniList.join(', ')
                        : profileData.uniList
                };

                await profileService.editHostProfile(editHostInfoRequest);

            }
            else {
                // Prepara i dati per USER
                const editUserInfoRequest = {
                    firstName: profileData.firstName,
                    lastName: profileData.lastName,
                    dateOfBirth: profileData.dateOfBirth,
                    contactsInfo: profileData.contactsInfo.replace(/instagram\.com/g, '').trim()
                };

                await profileService.editUserProfile(editUserInfoRequest);
            }

            setOriginalData(profileData);
            setIsEditing(false);
            setErrors({});

        } finally {
            setIsSaving(false);
        }
    };


    // ==================== HANDLERS MODIFICA EVENTO ====================

// Funzione per convertire data ISO in formato datetime-local
    const formatDateForInput = (isoDate) => {
        const date = new Date(isoDate);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day}T${hours}:${minutes}`;
    };

// Apre il modale con dati pre-compilati
    const handleOpenEditModal = (event) => {
        setEditEventId(event.ID);
        setEditFormData({
            name: event.name,
            description: event.description,
            dateAndTime: formatDateForInput(event.date),
            street: event.street,
            city: event.city,
            CAP: event.CAP || '',
            poster: null,
        });
        setEditPreviewImage(event.posterUrl);
        setEditFormErrors({});
        setEditFileError('');
        setIsEditModalOpen(true);
    };

// Gestione input form
    const handleEditInputChange = (field, value) => {
        setEditFormData((prev) => ({...prev, [field]: value}));
        if (editFormErrors[field]) {
            setEditFormErrors((prev) => ({...prev, [field]: null}));
        }
    };

// Upload immagine
    const handleEditImageUpload = (e) => {
        const file = e.target.files[0];
        if (file) {
            setEditFormErrors((prev) => ({...prev, poster: null}));

            if (file.size > 10 * 1024 * 1024) {
                return setEditFileError('Il file supera i 10MB');
            }

            const allowedTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/jpg'];
            if (!allowedTypes.includes(file.type)) {
                return setEditFileError('Formato non valido. Usa JPG, PNG o WEBP.');
            }

            setEditFileError('');
            const reader = new FileReader();
            reader.onloadend = () => {
                setEditPreviewImage(reader.result);
                setEditFormData({...editFormData, poster: file});
            };
            reader.readAsDataURL(file);
        }
    };


    const handleEditRemoveImage = (e) => {
        e.stopPropagation();
        setEditPreviewImage(null);
        setEditFormData({...editFormData, poster: null});
        if (fileInputEditRef.current) {
            fileInputEditRef.current.value = '';
        }
    };


// ==================== SALVA MODIFICHE EVENTO (CON VALIDAZIONI COMPLETE) ====================
    const handleSaveEdit = async (e) => {
        e.preventDefault();

        const errors = {};

        // VALIDAZIONE LOCANDINA
        if (!editPreviewImage) errors.poster = "La locandina è obbligatoria.";

        // VALIDAZIONE TITOLO
        if (!editFormData.name.trim()) errors.name = "Il titolo è obbligatorio.";
        else if (editFormData.name.length > 100) errors.name = "Massimo 100 caratteri.";

        // VALIDAZIONE DESCRIZIONE
        if (!editFormData.description.trim()) errors.description = "La descrizione è obbligatoria.";
        else if (editFormData.description.length > 200) errors.description = "Massimo 200 caratteri.";

        // VALIDAZIONE CITTÀ
        if (!editFormData.city.trim()) errors.city = "La città è obbligatoria.";
        else if (editFormData.city.length > 30) errors.city = "Massimo 30 caratteri.";
        else {
            const cityExists = comuniList.some(c => c.nome.toLowerCase() === editFormData.city.toLowerCase());
            if (!cityExists) errors.city = "Seleziona una città dalla lista.";
        }

        // VALIDAZIONE INDIRIZZO
        if (!editFormData.street.trim()) errors.street = "L'indirizzo è obbligatorio.";
        else if (editFormData.street.length > 60) errors.street = "Massimo 60 caratteri.";

        // VALIDAZIONE CAP
        if (!editFormData.CAP.trim()) errors.CAP = "Il CAP è obbligatorio.";
        else if (editFormData.CAP.length !== 5) errors.CAP = "Il CAP deve essere di 5 cifre.";
        else if (!/^\d{5}$/.test(editFormData.CAP)) errors.CAP = "Il CAP deve contenere solo numeri.";

        // VALIDAZIONE DATA (minimo 1 ora nel futuro)
        if (!editFormData.dateAndTime) errors.dateAndTime = "La data è obbligatoria.";
        else if (editFormData.dateAndTime < minEditDateTime) errors.dateAndTime = "La data deve essere futura (min. 1 ora).";

        // Se ci sono errori, blocca il salvataggio
        if (Object.keys(errors).length > 0) {
            setEditFormErrors(errors);
            toast.error("Correggi gli errori prima di salvare");
            return;
        }


        try {
            // Prepara FormData per inviare anche il file
            const formData = new FormData();
            formData.append('name', editFormData.name);
            formData.append('description', editFormData.description);
            formData.append('dateAndTime', editFormData.dateAndTime);
            formData.append('street', editFormData.street);
            formData.append('city', editFormData.city);
            formData.append('CAP', editFormData.CAP);

            // Solo se è stato caricato un nuovo file
            if (editFormData.poster) {
                formData.append('poster', editFormData.poster);
            }


            await eventService.editEvent(editEventId, editFormData);

            const updatedEvent = {
                ID: editEventId,  // Maiuscolo come nel backend
                name: editFormData.name,
                description: editFormData.description,
                dateAndTime: editFormData.dateAndTime,
                street: editFormData.street,
                city: editFormData.city,
                CAP: editFormData.CAP,
                posterUrl: editPreviewImage,  // URL della preview
                participantsCount: events.find(ev => ev.ID === editEventId)?.participantsCount || 0,
            };

            setEvents(prev => prev.map(ev => ev.ID === editEventId ? {...ev, ...updatedEvent} : ev));
            setIsEditModalOpen(false);
            toast.success("Evento aggiornato con successo!");

        } catch (error) {


            if (error.response?.status === 403) {

                toast.error("Non hai i permessi per modificare questo evento");
            } else if (error.response?.status === 404) {

                toast.error("Evento non trovato");
            } else {
                toast.error("Errore durante la modifica. Riprova più tardi.");
            }
        }
    };


// Helper per stile input (copia da EventsPage)
    const getEditInputStyle = (fieldName) => {
        const hasError = !!editFormErrors[fieldName];
        return `block w-full min-w-0 bg-white/5 rounded-xl px-3 md:px-4 py-1.5 md:py-3.5 text-sm md:text-base text-white placeholder:text-slate-500 focus:outline-none focus:bg-white/10 transition-all font-medium border ${
            hasError
                ? 'border-rose-500 focus:border-rose-500'
                : 'border-white/10 focus:border-younify-blue/50'
        }`;
    };

    // Prima del return principale, aggiungi questo componente:

    const SearchableDropdown = ({
                                    label,
                                    icon: Icon,
                                    options,
                                    selected,
                                    onSelect,
                                    isGrouped,
                                    errorMessage,
                                    disabled = false
                                }) => {
        const [isOpen, setIsOpen] = useState(false);
        const [searchTerm, setSearchTerm] = useState("");

        const handleSelectOption = (opt) => {
            if (disabled) return;
            onSelect(opt);
            setIsOpen(false);
            setSearchTerm("");
        };

        const renderOptions = () => {
            if (!isGrouped) {
                const filtered = options.filter(opt =>
                    opt.toLowerCase().includes(searchTerm.toLowerCase())
                );

                if (filtered.length === 0) {
                    return <div className="p-4 text-center text-slate-500 text-xs">Nessun risultato</div>;
                }

                return filtered.map((opt, idx) => {
                    const isSelected = selected === opt;
                    return (
                        <button
                            key={idx}
                            type="button"
                            onClick={() => handleSelectOption(opt)}
                            className={`w-full text-left px-4 py-3 rounded-xl transition-all duration-200 ${
                                isSelected
                                    ? 'bg-younify-blue/20 text-white border border-younify-blue/30'
                                    : 'text-slate-300 hover:bg-white/5 hover:text-white'
                            }`}
                        >
                            <div className="flex items-center justify-between">
                                <span className="text-sm">{opt}</span>
                                {isSelected && <CheckCircle2 className="h-4 w-4 text-younify-blue"/>}
                            </div>
                        </button>
                    );
                });
            } else {
                return Object.keys(options).map((category, idx) => {
                    const categoryOptions = options[category].filter(opt =>
                        opt.toLowerCase().includes(searchTerm.toLowerCase())
                    );

                    if (categoryOptions.length === 0) return null;

                    return (
                        <div key={idx} className="border-b border-white/5 last:border-0">
                            <div
                                className="px-2 py-1 text-[10px] font-bold text-slate-500 bg-slate-950/80 sticky top-0">
                                {category}
                            </div>
                            {categoryOptions.map((opt, i) => {
                                const isSelected = selected === opt;
                                return (
                                    <button
                                        key={i}
                                        type="button"
                                        onClick={() => handleSelectOption(opt)}
                                        className={`w-full text-left px-4 py-3 rounded-xl transition-all duration-200 ${
                                            isSelected
                                                ? 'bg-younify-blue/20 text-white border border-younify-blue/30'
                                                : 'text-slate-300 hover:bg-white/5 hover:text-white'
                                        }`}
                                    >
                                        <div className="flex items-center justify-between">
                                            <span className="text-sm">{opt}</span>
                                            {isSelected && <CheckCircle2 className="h-4 w-4 text-younify-blue"/>}
                                        </div>
                                    </button>
                                );
                            })}
                        </div>
                    );
                });
            }
        };

        return (
            <div className="space-y-2">
                <label className={`block text-xs font-medium uppercase tracking-widest transition-colors ${
                    disabled ? 'text-slate-600' : 'text-slate-400'
                }`}>
                    {label}
                </label>

                {/* Campo di selezione */}
                {!disabled && (
                    <button
                        type="button"
                        onClick={() => setIsOpen(!isOpen)}
                        className="w-full flex items-center justify-between px-4 py-3 rounded-2xl bg-slate-950/50 border border-white/10 hover:border-younify-blue/30 transition-all duration-300 text-left"
                    >
                        <div className="flex items-center gap-3 flex-1 min-w-0">
                            <Icon className="h-4 w-4 text-younify-blue shrink-0"/>
                            <span className={`text-sm truncate ${selected ? 'text-white' : 'text-slate-400'}`}>
              {selected || label}
            </span>
                        </div>
                        <ChevronRight
                            className={`h-4 w-4 text-slate-400 transition-transform shrink-0 ${isOpen ? 'rotate-90' : ''}`}/>
                    </button>
                )}

                {/* Campo disabled (solo visualizzazione) */}
                {disabled && (
                    <div
                        className="w-full flex items-center gap-3 px-4 py-3 rounded-2xl bg-slate-900/40 backdrop-blur-md border border-white/5 cursor-not-allowed">
                        <Icon className="h-4 w-4 text-slate-600 shrink-0"/>
                        <span className="text-sm text-slate-400/60 truncate">
            {selected || label}
          </span>
                    </div>
                )}

                {/* Dropdown List - SI ESPANDE NEL FLUSSO */}
                <AnimatePresence>
                    {isOpen && !disabled && (
                        <motion.div
                            initial={{opacity: 0, height: 0}}
                            animate={{opacity: 1, height: 'auto'}}
                            exit={{opacity: 0, height: 0}}
                            className="overflow-hidden rounded-2xl bg-slate-900/90 backdrop-blur-xl border border-white/10 shadow-2xl"
                        >
                            {/* Search Bar */}
                            <div className="p-3 border-b border-white/5">
                                <input
                                    type="text"
                                    placeholder="Cerca..."
                                    className="w-full bg-slate-950 rounded-lg p-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-younify-blue"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    autoFocus
                                />
                            </div>

                            {/* Options List */}
                            <div className="max-h-60 overflow-y-auto p-2">
                                {renderOptions()}
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>

                {errorMessage && (
                    <p className="text-xs text-rose-400 flex items-center gap-1">
                        <AlertCircle className="h-3 w-3"/>
                        {errorMessage}
                    </p>
                )}
            </div>
        );
    };

    // COMPONENTE DROPDOWN MODALITÀ VISTA (coerente con altri dropdown)
    const ViewModeDropdown = ({selected, onChange}) => {
        const [isOpen, setIsOpen] = useState(false);

        const options = [
            {value: 'list', label: 'Vista Lista', icon: Users},
            {value: 'globe', label: 'Vista Mappamondo', icon: Globe2}
        ];

        const selectedOption = options.find(opt => opt.value === selected);

        return (
            <div className="space-y-2">
                {/* Bottone principale */}
                <button
                    type="button"
                    onClick={() => setIsOpen(!isOpen)}
                    className="w-full flex items-center justify-between px-4 py-3 rounded-2xl bg-slate-950/50 border border-white/10 hover:border-purple-500/30 transition-all duration-300 text-left"
                >
                    <div className="flex items-center gap-3 flex-1 min-w-0">
                        {selectedOption && (
                            <>
                                <selectedOption.icon className="h-4 w-4 text-purple-400 shrink-0"/>
                                <span className="text-sm truncate text-white font-semibold">
                {selectedOption.label}
              </span>
                            </>
                        )}
                    </div>
                    <ChevronRight
                        size={16}
                        className={`text-slate-400 transition-transform shrink-0 ${isOpen ? 'rotate-90' : ''}`}
                    />
                </button>

                {/* Dropdown List */}
                <AnimatePresence>
                    {isOpen && (
                        <motion.div
                            initial={{opacity: 0, height: 0}}
                            animate={{opacity: 1, height: 'auto'}}
                            exit={{opacity: 0, height: 0}}
                            className="overflow-hidden rounded-2xl bg-slate-900/90 backdrop-blur-xl border border-white/10 shadow-2xl"
                        >
                            <div className="p-2">
                                {options.map((option) => {
                                    const isSelected = selected === option.value;
                                    return (
                                        <button
                                            key={option.value}
                                            type="button"
                                            onClick={() => {
                                                onChange(option.value);
                                                setIsOpen(false);
                                            }}
                                            className={`w-full text-left px-4 py-3 rounded-xl transition-all duration-200 ${
                                                isSelected
                                                    ? 'bg-purple-500/20 text-white border border-purple-500/30'
                                                    : 'text-slate-300 hover:bg-white/5 hover:text-white'
                                            }`}
                                        >
                                            <div className="flex items-center justify-between">
                                                <div className="flex items-center gap-3">
                                                    <option.icon className="h-4 w-4"/>
                                                    <span className="text-sm">{option.label}</span>
                                                </div>
                                                {isSelected && (
                                                    <CheckCircle2 className="h-4 w-4 text-purple-400"/>
                                                )}
                                            </div>
                                        </button>
                                    );
                                })}
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>
            </div>
        );
    };


    return (
        <div
            className="fixed inset-0 text-slate-100 bg-slate-900/20 backdrop-blur-sm lg:bg-transparent lg:backdrop-blur-none overflow-hidden"
            style={{height: '100dvh'}}
        >
            {/* NAVBAR */}
            <div className="relative z-50 shrink-0 px-4 pt-3 pb-2 md:pb-4 lg:block lg:px-0 lg:pt-0">
                <AppNavbar/>
            </div>

            {/* MAIN CONTENT */}
            <div className="absolute inset-0 overflow-y-auto scroll-smooth lg:pl-32">
                <div className="max-w-6xl mx-auto px-4 md:px-6 lg:px-12 pt-20 md:pt-8 lg:pt-8 pb-32">

                    {/* ========================================
    PROFILE HERO HEADER (Horizontal Layout - No Cover)
======================================== */}
                    <motion.div
                        initial={{opacity: 0, y: -20}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.6, ease: [0.22, 1, 0.36, 1]}}
                        className="relative rounded-3xl bg-slate-900/40 backdrop-blur-xl border border-white/10 shadow-2xl overflow-hidden mb-8 lg:mb-12"
                    >
                        {/* Background Gradient */}
                        <div
                            className="absolute inset-0 bg-gradient-to-br from-younify-blue/5 via-purple-600/5 to-pink-600/5 pointer-events-none"/>

                        {/* Content Container */}
                        <div className="relative p-6 md:p-8 lg:p-10">

                            <div
                                className="flex flex-col xl:flex-row xl:items-center xl:justify-between gap-6 xl:gap-8 flex-wrap">

                                {/* LEFT SIDE - Avatar + Info + Contacts */}
                                <div className="flex flex-col sm:flex-row items-center sm:items-start gap-6 flex-1">
                                    {/* Avatar */}
                                    <div className="relative flex-shrink-0">
                                        <div
                                            className="absolute inset-0 rounded-full bg-gradient-to-br from-younify-blue via-purple-500 to-pink-500 blur-xl opacity-60 animate-pulse"/>
                                        <div
                                            className="relative h-32 w-32 md:h-36 md:w-36 rounded-full overflow-hidden border-4 border-slate-900 shadow-2xl ring-4 ring-white/10">
                                            <img
                                                src={avatarPreview}
                                                alt="Profile"
                                                className="h-full w-full object-cover"
                                            />
                                        </div>
                                        <label
                                            htmlFor="avatar-upload"
                                            className="absolute bottom-0 right-0 flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-younify-blue to-purple-600 border-2 border-slate-900 shadow-lg cursor-pointer hover:scale-110 hover:rotate-12 transition-all duration-300 group"
                                        >
                                            <Camera
                                                className="h-4 w-4 text-white group-hover:scale-110 transition-transform"/>
                                            <input
                                                type="file"
                                                id="avatar-upload"
                                                accept="image/*"
                                                className="hidden"
                                                onChange={handleAvatarUpload}
                                            />
                                        </label>
                                    </div>

                                    {/* Info Block */}
                                    <div className="text-center sm:text-left flex-1">
                                        <h2 className="text-2xl md:text-3xl font-bold text-white mb-1">
                                            {isHost ? profileData.hostName : `${profileData.firstName} ${profileData.lastName}`}
                                        </h2>

                                        <div
                                            className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/5 border border-white/10 backdrop-blur-sm mb-3">
                                            <div className="h-2 w-2 rounded-full bg-emerald-400 animate-pulse"/>
                                            <span className="text-xs font-medium text-slate-300">
                                              {isHost ? 'Host Partner' : 'Studente'}
                                            </span>
                                        </div>

                                        <div className="space-y-2 text-sm text-slate-400">
                                            <div className="flex items-center justify-center sm:justify-start gap-2">
                                                <Mail className="h-3.5 w-3.5"/>
                                                <span
                                                    className="truncate">{isHost ? profileData.pec : profileData.email}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                {/* CFU Card */}
                                <div className="flex gap-4 justify-center xl:justify-end flex-wrap">
                                    <div
                                        className="relative px-8 py-6 rounded-3xl bg-gradient-to-br from-slate-900/90 to-slate-800/90 backdrop-blur-xl border border-white/20 shadow-2xl">
                                        <div className="text-center">
                                            {/* CFU Number */}
                                            <div className="relative mb-3">
                                                <div
                                                    className="text-4xl md:text-5xl font-black bg-gradient-to-br from-white via-younify-blue to-purple-400 bg-clip-text text-transparent tracking-tighter leading-none">
                                                    {profileData.cfuCount?.toLocaleString() ?? '0'}
                                                </div>
                                            </div>
                                            {/* Label */}
                                            <div className="flex items-center justify-center gap-2 mb-2">
                                                <div
                                                    className="h-px w-8 bg-gradient-to-r from-transparent to-slate-600"/>
                                                <span
                                                    className="text-xs text-slate-400 uppercase tracking-[0.2em] font-bold">
                                                    CFU Totali
                                                    </span>
                                                <div
                                                    className="h-px w-8 bg-gradient-to-l from-transparent to-slate-600"/>
                                            </div>
                                        </div>
                                    </div>
                                    {/* Matching Card - SOLO PER USER */}
                                    {!isHost && (
                                        <div
                                            className="relative px-8 py-6 rounded-3xl bg-gradient-to-br from-slate-900/90 to-slate-800/90 backdrop-blur-xl border border-white/20 shadow-2xl">
                                            <div className="text-center">
                                                {/* CFU Number */}
                                                <div className="relative mb-3">
                                                    <div
                                                        className="text-4xl md:text-5xl font-black bg-gradient-to-br from-red-500 via-rose-400 to-pink-500 bg-clip-text text-transparent tracking-tighter leading-none">
                                                        {profileData.matchCount?.toLocaleString() ?? 0}
                                                    </div>
                                                </div>
                                                {/* Label */}
                                                <div className="flex items-center justify-center gap-2 mb-2">
                                                    <div
                                                        className="h-px w-8 bg-gradient-to-r from-transparent to-slate-600"></div>
                                                    <span
                                                        className="text-xs text-slate-400 uppercase tracking-[0.2em] font-bold">
                    Match Totali
                </span>
                                                    <div
                                                        className="h-px w-8 bg-gradient-to-l from-transparent to-slate-600"></div>
                                                </div>
                                            </div>
                                        </div>
                                    )}

                                </div>
                            </div>
                        </div>
                    </motion.div>


                    {/* ========================================
              DATI PERSONALI (Con Modifica)
          ======================================== */}
                    <motion.section
                        initial={{opacity: 0, y: 30}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.6, delay: 0.2, ease: [0.22, 1, 0.36, 1]}}
                        className="rounded-3xl bg-slate-900/40 backdrop-blur-xl border border-white/10 shadow-2xl p-6 md:p-8 overflow-hidden mb-6"
                    >
                        {/* Header con Bottoni */}
                        <div
                            className="mb-6 md:mb-8 flex flex-col md:flex-row md:items-start md:justify-between gap-4 md:gap-0">
                            <div>
                                <p className="text-xs font-medium text-slate-500 uppercase tracking-widest mb-2">
                                    Impostazioni
                                </p>
                                <div className="flex items-center gap-3">
                                    <div
                                        className="flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-younify-blue/20 to-purple-600/20 border border-younify-blue/20">
                                        {isHost ? (
                                            <Building2 className="h-5 w-5 text-younify-blue"/>
                                        ) : (
                                            <User className="h-5 w-5 text-younify-blue"/>
                                        )}
                                    </div>
                                    <div>
                                        <h3 className="text-2xl font-bold text-white">Dati Personali</h3>
                                        <p className="text-sm text-slate-400">
                                            {isHost ? 'Informazioni della tua attività' : 'Gestisci le tue informazioni'}
                                        </p>
                                    </div>
                                </div>
                            </div>

                            {/* Bottoni Modifica/Annulla/Salva */}
                            <div className="flex items-center gap-3 w-full md:w-auto justify-end">
                                {!isEditing ? (
                                    <motion.button
                                        whileHover={{scale: 1.05}}
                                        whileTap={{scale: 0.95}}
                                        onClick={handleEdit}
                                        className="flex items-center gap-2 px-4 py-2 rounded-xl bg-gradient-to-r from-younify-blue to-purple-600 text-white text-sm font-semibold shadow-lg hover:shadow-younify-blue/40 transition-all duration-300"
                                    >
                                        <Edit3 className="h-4 w-4"/>
                                        Modifica
                                    </motion.button>
                                ) : (
                                    <>
                                        <motion.button
                                            whileHover={{scale: 1.05}}
                                            whileTap={{scale: 0.95}}
                                            onClick={handleCancel}
                                            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-800 text-slate-300 text-sm font-semibold border border-slate-700 hover:bg-slate-700 transition-all duration-300"
                                        >
                                            <X className="h-4 w-4"/>
                                            Annulla
                                        </motion.button>

                                        <motion.button
                                            whileHover={{scale: hasPersonalDataChanges ? 1.05 : 1}}
                                            whileTap={{scale: hasPersonalDataChanges ? 0.95 : 1}}
                                            onClick={handleSave}
                                            disabled={!hasPersonalDataChanges}
                                            className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold shadow-lg transition-all duration-300 ${
                                                hasPersonalDataChanges
                                                    ? 'bg-gradient-to-r from-emerald-500 to-green-600 text-white hover:shadow-emerald-500/40 cursor-pointer'
                                                    : 'bg-slate-800/50 text-slate-500 cursor-not-allowed'
                                            }`}
                                        >
                                            <Save className="h-4 w-4"/>
                                            Salva
                                        </motion.button>
                                    </>
                                )}
                            </div>
                        </div>


                        {/* Form Fields */}
                        <div className="space-y-5">
                            {isHost ? (
                                <>
                                    <PremiumInput
                                        label="Nome Attività"
                                        icon={Building2}
                                        name="hostName"
                                        value={profileData.hostName}
                                        onChange={handleInputChange}
                                        disabled={!isEditing}
                                        placeholder="Inserisci il nome dell'attività"
                                    />

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                                        <PremiumInput
                                            label="Partita IVA"
                                            icon={Receipt}
                                            name="piva"
                                            value={profileData.piva}
                                            disabled={true}
                                        />

                                        <PremiumInput
                                            label="PEC"
                                            icon={Mail}
                                            type="email"
                                            name="pec"
                                            value={profileData.pec}
                                            disabled={true}
                                        />
                                    </div>

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                                        <SearchableDropdown
                                            label="Provincia"
                                            icon={MapPin}
                                            options={Object.keys(italianPlaces)}
                                            selected={profileData.province}
                                            onSelect={(val) => handleInputChange({
                                                target: {
                                                    name: 'province',
                                                    value: val
                                                }
                                            })}
                                            isGrouped={false}
                                            disabled={!isEditing}
                                            errorMessage={errors.province}
                                        />

                                        <SearchableDropdown
                                            label="Città"
                                            icon={MapPin}
                                            options={availableCities}
                                            selected={profileData.city}
                                            onSelect={(val) => handleInputChange({target: {name: 'city', value: val}})}
                                            isGrouped={false}
                                            disabled={!isEditing || !profileData.province}
                                            errorMessage={errors.city}
                                        />
                                    </div>


                                    {/* Multi-Select Università */}
                                    <UniversityMultiSelect
                                        selectedUnis={profileData.uniList || []}
                                        onToggle={handleUniToggle}
                                        disabled={!isEditing}
                                    />
                                </>
                            ) : (
                                <>
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                                        <PremiumInput
                                            label="Nome"
                                            icon={User}
                                            name="firstName"
                                            value={profileData.firstName}
                                            onChange={handleInputChange}
                                            disabled={!isEditing}
                                            error={errors.firstName}
                                            placeholder="Il tuo nome"
                                        />

                                        <PremiumInput
                                            label="Cognome"
                                            icon={User}
                                            name="lastName"
                                            value={profileData.lastName}
                                            onChange={handleInputChange}
                                            disabled={!isEditing}
                                            error={errors.lastName}
                                            placeholder="Il tuo cognome"
                                        />
                                    </div>

                                    <PremiumInput
                                        label="Data di Nascita"
                                        icon={Calendar}
                                        type="date"
                                        name="dateOfBirth"
                                        value={profileData.dateOfBirth}
                                        onChange={handleInputChange}
                                        disabled={!isEditing}
                                        error={errors.dateOfBirth}
                                        max={getMaxDate()}
                                        min={getMinDate()}
                                    />

                                    <InstagramInput
                                        value={profileData.contactsInfo.replace(/^instagram\.com\//i, '').replace(/^@/, '')}
                                        onChange={handleInputChange}
                                        disabled={!isEditing}
                                        error={errors.contactsInfo}
                                    />

                                </>
                            )}
                        </div>
                    </motion.section>

                    {/* ========================================
    DATI UNIVERSITARI (Nuova Sezione Separata)
======================================== */}

                    {!isHost && (
                        <motion.section
                            initial={{opacity: 0, y: 30}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.6, delay: 0.3, ease: [0.22, 1, 0.36, 1]}}
                            className="rounded-3xl bg-slate-900/40 backdrop-blur-xl border border-white/10 shadow-2xl p-6 md:p-8 overflow-hidden mb-6"
                        >
                            {/* Header con Bottoni */}
                            <div
                                className="mb-6 md:mb-8 flex flex-col md:flex-row md:items-start md:justify-between gap-4 md:gap-0">
                                <div>
                                    <p className="text-xs font-medium text-slate-500 uppercase tracking-widest mb-2">
                                        Istruzione
                                    </p>
                                    <div className="flex items-center gap-3">
                                        <div
                                            className="flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-purple-500/20 to-pink-600/20 border border-purple-500/20">
                                            <GraduationCap className="h-5 w-5 text-purple-400"/>
                                        </div>
                                        <div>
                                            <h3 className="text-2xl font-bold text-white">Dati Universitari</h3>
                                            <p className="text-sm text-slate-400">Gestisci email, università e corso</p>
                                        </div>
                                    </div>
                                </div>

                                {/* Bottoni Modifica/Annulla/Salva */}
                                <div className="flex items-center gap-3 w-full md:w-auto justify-end">
                                    {!isEditingUniversity ? (
                                        <motion.button
                                            whileHover={{scale: 1.05}}
                                            whileTap={{scale: 0.95}}
                                            onClick={handleEditUniversity}
                                            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-gradient-to-r from-purple-500 to-pink-600 text-white text-sm font-semibold shadow-lg hover:shadow-purple-500/40 transition-all duration-300"
                                        >
                                            <Edit3 className="h-4 w-4"/>
                                            Modifica
                                        </motion.button>
                                    ) : (
                                        <>
                                            <motion.button
                                                whileHover={{scale: 1.05}}
                                                whileTap={{scale: 0.95}}
                                                onClick={handleCancelUniversity}
                                                className="flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-800 text-slate-300 text-sm font-semibold border border-slate-700 hover:bg-slate-700 transition-all duration-300"
                                            >
                                                <X className="h-4 w-4"/>
                                                Annulla
                                            </motion.button>

                                            <motion.button
                                                whileHover={{scale: hasUniversityChanges ? 1.05 : 1}}
                                                whileTap={{scale: hasUniversityChanges ? 0.95 : 1}}
                                                onClick={handleSaveUniversity}
                                                disabled={!hasUniversityChanges}
                                                className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold shadow-lg transition-all duration-300 ${
                                                    hasUniversityChanges
                                                        ? 'bg-gradient-to-r from-emerald-500 to-green-600 text-white hover:shadow-emerald-500/40 cursor-pointer'
                                                        : 'bg-slate-800/50 text-slate-500 cursor-not-allowed'
                                                }`}
                                            >
                                                <Save className="h-4 w-4"/>
                                                Salva
                                            </motion.button>
                                        </>
                                    )}
                                </div>
                            </div>

                            {/* Form Fields */}
                            <div className="space-y-5">
                                <PremiumInput
                                    label="Email Universitaria"
                                    icon={Mail}
                                    type="email"
                                    name="email"
                                    value={profileData.email}
                                    onChange={handleUniversityChange}
                                    disabled={!isEditingUniversity}
                                    error={universityErrors.email}
                                />

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                                    <SearchableDropdown
                                        label="Università"
                                        icon={School}
                                        options={UNIVERSITY_DATA}
                                        selected={profileData.university}
                                        onSelect={(val) => handleUniversitySelect('university', val)}
                                        isGrouped={true}
                                        disabled={!isEditingUniversity}
                                        errorMessage={universityErrors.university}
                                    />

                                    {isLoading ? (
                                        <div className="animate-pulse">
                                            <div className="h-12 bg-slate-800 rounded-2xl"></div>
                                        </div>
                                    ) : (
                                        <SearchableDropdown
                                            label="Corso di Laurea"
                                            icon={GraduationCap}
                                            options={CDL_DATA}
                                            selected={profileData.degreeProgram || ''}
                                            onSelect={(val) => handleUniversitySelect('degreeProgram', val)}
                                            isGrouped={true}
                                            disabled={!isEditingUniversity}
                                            errorMessage={universityErrors.degreeProgram}
                                        />
                                    )}
                                </div>
                            </div>
                        </motion.section>
                    )}

                    {/* ========================================
    SICUREZZA - Password (Solo Indicatori)
======================================== */}
                    <motion.section
                        initial={{opacity: 0, y: 30}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.6, delay: 0.4, ease: [0.22, 1, 0.36, 1]}}
                        className="rounded-3xl bg-slate-900/40 backdrop-blur-xl border border-white/10 shadow-2xl p-6 md:p-8 overflow-hidden mb-6"
                    >
                        {/* Header con Bottoni */}
                        <div
                            className="mb-6 md:mb-8 flex flex-col md:flex-row md:items-start md:justify-between gap-4 md:gap-0">
                            <div>
                                <p className="text-xs font-medium text-slate-500 uppercase tracking-widest mb-2">
                                    Protezione
                                </p>
                                <div className="flex items-center gap-3">
                                    <div
                                        className="flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-rose-500/20 to-pink-600/20 border border-rose-500/20">
                                        <Shield className="h-5 w-5 text-rose-400"/>
                                    </div>
                                    <div>
                                        <h3 className="text-2xl font-bold text-white">Sicurezza</h3>
                                        <p className="text-sm text-slate-400">Modifica la tua password</p>
                                    </div>
                                </div>
                            </div>

                            {/* Bottoni Modifica/Annulla/Salva */}
                            <div className="flex items-center gap-3 w-full md:w-auto justify-end">
                                {!isEditingPassword ? (
                                    <motion.button
                                        whileHover={{scale: 1.05}}
                                        whileTap={{scale: 0.95}}
                                        onClick={handleEditPassword}
                                        className="flex items-center gap-2 px-4 py-2 rounded-xl bg-gradient-to-r from-rose-500 to-pink-600 text-white text-sm font-semibold shadow-lg hover:shadow-rose-500/40 transition-all duration-300"
                                    >
                                        <Edit3 className="h-4 w-4"/>
                                        Modifica
                                    </motion.button>
                                ) : (
                                    <>
                                        <motion.button
                                            whileHover={{scale: 1.05}}
                                            whileTap={{scale: 0.95}}
                                            onClick={handleCancelPassword}
                                            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-800 text-slate-300 text-sm font-semibold border border-slate-700 hover:bg-slate-700 transition-all duration-300"
                                        >
                                            <X className="h-4 w-4"/>
                                            Annulla
                                        </motion.button>

                                        <motion.button
                                            whileHover={{scale: 1.05}}
                                            whileTap={{scale: 0.95}}
                                            onClick={handleSavePassword}
                                            disabled={!passwordRegex.test(passwords.newPassword) || passwords.newPassword !== passwords.confirmPassword || !passwords.confirmPassword}
                                            className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold shadow-lg transition-all duration-300 ${
                                                passwordRegex.test(passwords.newPassword) && passwords.newPassword === passwords.confirmPassword && passwords.confirmPassword
                                                    ? 'bg-gradient-to-r from-emerald-500 to-green-600 text-white hover:shadow-emerald-500/40'
                                                    : 'bg-slate-800/50 text-slate-500 cursor-not-allowed'
                                            }`}
                                        >
                                            <Save className="h-4 w-4"/>
                                            Salva
                                        </motion.button>
                                    </>
                                )}
                            </div>
                        </div>

                        {/* Form Fields - Password e Conferma uno affianco all'altro */}
                        <div className="space-y-5">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                                <PremiumInput
                                    label="Nuova Password"
                                    icon={Lock}
                                    type={isEditingPassword ? "password" : "text"}
                                    name="newPassword"
                                    value={isEditingPassword ? passwords.newPassword : 'Nuova password'}
                                    onChange={handlePasswordChange}
                                    disabled={!isEditingPassword}
                                    // ← RIMOSSO error={passwordErrors.newPassword}
                                    placeholder="Nuova password"
                                />

                                <PremiumInput
                                    label="Conferma Password"
                                    icon={Lock}
                                    type={isEditingPassword ? "password" : "text"}
                                    name="confirmPassword"
                                    value={isEditingPassword ? passwords.confirmPassword : 'Ripeti la nuova password'}
                                    onChange={handlePasswordChange}
                                    disabled={!isEditingPassword || passwords.newPassword.length === 0}
                                    // ← RIMOSSO error={passwordErrors.confirmPassword}
                                    placeholder="Ripeti la nuova password"
                                />
                            </div>

                            {/* Indicatori Validazione - Sempre visibili quando si modifica */}
                            <AnimatePresence>
                                {isEditingPassword && (
                                    <motion.div
                                        initial={{opacity: 0, height: 0}}
                                        animate={{opacity: 1, height: 'auto'}}
                                        exit={{opacity: 0, height: 0}}
                                        className="space-y-3 p-4 rounded-xl bg-slate-950/30 border border-white/5"
                                    >
                                        {/* Requisiti password */}
                                        <div className={`flex items-center gap-2 text-sm transition-colors ${
                                            passwordRegex.test(passwords.newPassword) ? 'text-emerald-400' : 'text-slate-500'
                                        }`}>
                                            <div
                                                className={`flex h-5 w-5 items-center justify-center rounded-full border transition-all ${
                                                    passwordRegex.test(passwords.newPassword)
                                                        ? 'border-emerald-400 bg-emerald-400/10'
                                                        : 'border-slate-600 bg-slate-800/30'
                                                }`}>
                                                {passwordRegex.test(passwords.newPassword) && (
                                                    <CheckCircle2 className="h-3 w-3"/>
                                                )}
                                            </div>
                                            <span className="font-medium">
              Minimo 8 caratteri con maiuscola, minuscola, numero e simbolo
            </span>
                                        </div>

                                        {/* Password coincidono */}
                                        <div className={`flex items-center gap-2 text-sm transition-colors ${
                                            passwords.newPassword === passwords.confirmPassword && passwords.confirmPassword
                                                ? 'text-emerald-400'
                                                : 'text-slate-500'
                                        }`}>
                                            <div
                                                className={`flex h-5 w-5 items-center justify-center rounded-full border transition-all ${
                                                    passwords.newPassword === passwords.confirmPassword && passwords.confirmPassword
                                                        ? 'border-emerald-400 bg-emerald-400/10'
                                                        : 'border-slate-600 bg-slate-800/30'
                                                }`}>
                                                {passwords.newPassword === passwords.confirmPassword && passwords.confirmPassword && (
                                                    <CheckCircle2 className="h-3 w-3"/>
                                                )}
                                            </div>
                                            <span className="font-medium">
              Le password coincidono
            </span>
                                        </div>
                                    </motion.div>
                                )}
                            </AnimatePresence>
                        </div>
                    </motion.section>


                    {/* ==================== SEZIONE EVENTI ==================== */}
                    <motion.section
                        initial={{opacity: 0, y: 30}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.6, delay: 0.5, ease: [0.22, 1, 0.36, 1]}}
                        className="rounded-3xl bg-slate-900/40 backdrop-blur-xl border border-white/10 shadow-2xl p-6 md:p-8 mb-6 overflow-hidden">
                        {/* Header */}
                        <div className="mb-6">
                            <p className="text-xs font-medium text-slate-500 uppercase tracking-widest mb-2">
                                {isHost ? 'Gestione' : 'Partecipazioni'}
                            </p>
                            <div className="flex items-center gap-3">
                                <div
                                    className="flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-amber-500/20 to-orange-600/20 border border-amber-500/20">
                                    <CalendarDays className="h-5 w-5 text-amber-400"/>
                                </div>
                                <div>
                                    <h3 className="text-2xl font-bold text-white">Eventi</h3>
                                    <p className="text-sm text-slate-400">
                                        {isHost ? 'Eventi creati dalla tua attività' : 'Eventi a cui partecipi'}
                                    </p>
                                </div>
                            </div>
                        </div>

                        {/* Lista Eventi */}
                        <AnimatePresence mode="popLayout">
                            {events.length > 0 ? (
                                <div className="space-y-3">
                                    {displayedEvents.map((event) => (
                                        <ProfileEventItem
                                            key={event.ID}
                                            event={event}
                                            isHost={isHost}
                                            onView={() => handleViewEventDetail(event)}
                                            onUnjoin={() => handleUnjoinEvent(event)}
                                            onEdit={() => handleOpenEditModal(event)}
                                            onDelete={() => handleDeleteEvent(event)}
                                        />
                                    ))}
                                    {/* Bottone Mostra Altri Eventi */}
                                    {events.length > INITIAL_ITEMS_LIMIT && (
                                        <motion.button
                                            whileHover={{scale: 1.02}}
                                            whileTap={{scale: 0.98}}
                                            onClick={() => setShowAllEvents(!showAllEvents)}
                                            className="w-full mt-4 py-3 rounded-xl bg-slate-800/50 border border-white/10 text-slate-400 hover:text-white hover:border-younify-blue/30 transition-all flex items-center justify-center gap-2 font-semibold text-sm"
                                        >
                                            {showAllEvents ? (
                                                <>
                                                    <ChevronLeft size={16}/>
                                                    Mostra meno
                                                </>
                                            ) : (
                                                <>
                                                    Carica altro ({events.length - INITIAL_ITEMS_LIMIT})
                                                    <ChevronRight size={16}/>
                                                </>
                                            )}
                                        </motion.button>
                                    )}
                                </div>
                            ) : (
                                <div className="flex flex-col items-center justify-center py-12 text-center">
                                    <div className="relative mb-6">
                                        <div
                                            className="absolute inset-0 bg-gradient-to-br from-amber-500/20 to-orange-500/20 rounded-full blur-2xl"/>
                                        <div
                                            className="relative flex h-20 w-20 items-center justify-center rounded-full bg-slate-950/50 border border-white/10">
                                            <Sparkles className="h-9 w-9 text-amber-400/40"/>
                                        </div>
                                    </div>
                                    <p className="text-slate-400 text-sm max-w-md">
                                        {isHost
                                            ? 'Non hai ancora creato eventi. Inizia a organizzare per coinvolgere gli studenti!'
                                            : 'Non partecipi ancora a nessun evento. Esplora la sezione Eventi per scoprirne di nuovi!'}
                                    </p>
                                </div>
                            )}
                        </AnimatePresence>
                    </motion.section>

                    {/* ==================== PART 6: SEZIONE STORICO THREAD & COMMENTI ==================== */}
                    <motion.section
                        initial={{opacity: 0, y: 30}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.6, delay: 0.4, ease: [0.22, 1, 0.36, 1]}}
                        className="rounded-3xl bg-slate-900/40 backdrop-blur-xl border border-white/10 shadow-2xl p-6 md:p-8 overflow-hidden mb-6"
                    >
                        {/* Header con Tab Switcher */}
                        <div className="mb-6 md:mb-8">
                            <div
                                className="flex flex-col md:flex-row md:items-start md:justify-between gap-4 md:gap-0 mb-6">
                                <div>
                                    <p className="text-xs font-medium text-slate-500 uppercase tracking-widest mb-2">
                                        Attività Sociale
                                    </p>
                                    <div className="flex items-center gap-3">
                                        <div
                                            className="flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-purple-600/20 to-blue-600/20 border border-purple-500/20">
                                            <MessageSquare className="h-5 w-5 text-purple-400"/>
                                        </div>
                                        <div>
                                            <h3 className="text-2xl font-bold text-white">Thread & Commenti</h3>
                                            <p className="text-sm text-slate-400">Le tue interazioni nella community</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Tab Buttons */}
                            <div className="flex gap-3 p-1.5 bg-slate-950/50 rounded-2xl border border-white/5">
                                <button
                                    onClick={() => setSocialTab('threads')}
                                    className={`flex-1 px-4 py-3 rounded-xl font-semibold text-sm transition-all duration-300 ${
                                        socialTab === 'threads'
                                            ? 'bg-gradient-to-r from-purple-600 to-purple-500 text-white shadow-lg shadow-purple-500/20'
                                            : 'text-slate-400 hover:text-white hover:bg-white/5'
                                    }`}
                                >
                                    <div className="flex items-center justify-center gap-2">
                                        <PenLine size={16}/>
                                        <span>I miei Thread</span>
                                        <span className="px-2 py-0.5 rounded-full bg-white/10 text-xs font-bold">
            {threads.length}
          </span>
                                    </div>
                                </button>
                                <button
                                    onClick={() => setSocialTab('comments')}
                                    className={`flex-1 px-4 py-3 rounded-xl font-semibold text-sm transition-all duration-300 ${
                                        socialTab === 'comments'
                                            ? 'bg-gradient-to-r from-blue-600 to-blue-500 text-white shadow-lg shadow-blue-500/20'
                                            : 'text-slate-400 hover:text-white hover:bg-white/5'
                                    }`}
                                >
                                    <div className="flex items-center justify-center gap-2">
                                        <MessageCircle size={16}/>
                                        <span>I miei Commenti</span>
                                        <span className="px-2 py-0.5 rounded-full bg-white/10 text-xs font-bold">
            {comments.length}
          </span>
                                    </div>
                                </button>
                            </div>
                        </div>

                        {/* Contenuto Tab: Thread */}
                        <AnimatePresence mode="wait">
                            {socialTab === 'threads' && (
                                <motion.div
                                    key="threads"
                                    initial={{opacity: 0, x: -20}}
                                    animate={{opacity: 1, x: 0}}
                                    exit={{opacity: 0, x: 20}}
                                    transition={{duration: 0.3}}
                                    className="space-y-4"
                                >
                                    {threads.length > 0 ? (
                                        displayedThreads.map((thread) => (
                                                <ProfileThreadItem
                                                    key={thread.ID}
                                                    thread={thread}
                                                    onDelete={() => handleDeleteThread(thread.ID)}
                                                />
                                            )
                                        )
                                    ) : (
                                        <div className="flex flex-col items-center justify-center py-16 text-center">
                                            <div className="relative mb-6">
                                                <div
                                                    className="absolute inset-0 bg-purple-500/20 blur-3xl rounded-full"/>
                                                <div
                                                    className="relative w-20 h-20 rounded-full bg-slate-900/50 border border-white/10 flex items-center justify-center">
                                                    <Sparkles size={32} className="text-slate-600"/>
                                                </div>
                                            </div>
                                            <p className="text-lg font-semibold text-slate-400 mb-2">
                                                Nessun thread pubblicato
                                            </p>
                                            <p className="text-sm text-slate-500 max-w-md">
                                                Condividi le tue domande, esperienze o consigli con la community
                                                universitaria.
                                            </p>
                                        </div>
                                    )}
                                    {threads.length > INITIAL_ITEMS_LIMIT && (
                                        <motion.button
                                            whileHover={{scale: 1.02}}
                                            whileTap={{scale: 0.98}}
                                            onClick={() => setShowAllThreads(!showAllThreads)}
                                            className="w-full mt-4 py-3 rounded-xl bg-slate-800/50 border border-white/10 text-slate-400 hover:text-white hover:border-purple-500/30 transition-all flex items-center justify-center gap-2 font-semibold text-sm"
                                        >
                                            {showAllThreads ? (
                                                <>
                                                    <ChevronLeft size={16}/>
                                                    Mostra meno
                                                </>
                                            ) : (
                                                <>
                                                    Carica altro ({threads.length - INITIAL_ITEMS_LIMIT})
                                                    <ChevronRight size={16}/>
                                                </>
                                            )}
                                        </motion.button>
                                    )}

                                </motion.div>
                            )}

                            {/* Contenuto Tab: Commenti */}
                            {socialTab === 'comments' && (
                                <motion.div
                                    key="comments"
                                    initial={{opacity: 0, x: -20}}
                                    animate={{opacity: 1, x: 0}}
                                    exit={{opacity: 0, x: 20}}
                                    transition={{duration: 0.3}}
                                    className="space-y-4"
                                >
                                    {comments.length > 0 ? (
                                        displayedComments.map((comment) => (
                                            <ProfileCommentItem
                                                key={comment.ID}
                                                comment={comment}
                                                onDelete={() => handleDeleteComment(comment.ID)}
                                            />
                                        ))
                                    ) : (
                                        <div className="flex flex-col items-center justify-center py-16 text-center">
                                            <div className="relative mb-6">
                                                <div className="absolute inset-0 bg-blue-500/20 blur-3xl rounded-full"/>
                                                <div
                                                    className="relative w-20 h-20 rounded-full bg-slate-900/50 border border-white/10 flex items-center justify-center">
                                                    <MessageSquare size={32} className="text-slate-600"/>
                                                </div>
                                            </div>
                                            <p className="text-lg font-semibold text-slate-400 mb-2">
                                                Nessun commento pubblicato
                                            </p>
                                            <p className="text-sm text-slate-500 max-w-md">
                                                Inizia a partecipare alle discussioni rispondendo ai thread della
                                                community.
                                            </p>
                                        </div>
                                    )}
                                    {comments.length > INITIAL_ITEMS_LIMIT && (
                                        <motion.button
                                            whileHover={{scale: 1.02}}
                                            whileTap={{scale: 0.98}}
                                            onClick={() => setShowAllComments(!showAllComments)}
                                            className="w-full mt-4 py-3 rounded-xl bg-slate-800/50 border border-white/10 text-slate-400 hover:text-white hover:border-blue-500/30 transition-all flex items-center justify-center gap-2 font-semibold text-sm"
                                        >
                                            {showAllComments ? (
                                                <>
                                                    <ChevronLeft size={16}/>
                                                    Mostra meno
                                                </>
                                            ) : (
                                                <>
                                                    Carica altro ({comments.length - INITIAL_ITEMS_LIMIT})
                                                    <ChevronRight size={16}/>
                                                </>
                                            )}
                                        </motion.button>
                                    )}

                                </motion.div>
                            )}
                        </AnimatePresence>
                    </motion.section>

                    {/* ==================== PART 7: SEZIONE MATCHING DASHBOARD ==================== */}
                    {!isHost && (
                        <motion.section
                            initial={{opacity: 0, y: 30}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.6, delay: 0.5, ease: [0.22, 1, 0.36, 1]}}
                            className="rounded-3xl bg-slate-900/40 backdrop-blur-xl border border-white/10 shadow-2xl p-6 md:p-8 overflow-hidden mb-8"
                        >
                            {/* Header */}
                            <div className="mb-6 md:mb-8">
                                <div className="flex items-start justify-between gap-4 mb-6">
                                    <div>
                                        <p className="text-xs font-medium text-slate-500 uppercase tracking-widest mb-2">
                                            Connessioni
                                        </p>
                                        <div className="flex items-center gap-3">
                                            <div
                                                className="flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-emerald-600/20 to-purple-600/20 border border-emerald-500/20">
                                                <Heart className="h-5 w-5 text-emerald-400"/>
                                            </div>
                                            <div>
                                                <h3 className="text-2xl font-bold text-white">Matching Dashboard</h3>
                                                <p className="text-sm text-slate-400">Gestisci le tue richieste di
                                                    match</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                {/* Dashboard Richieste: 2 Colonne */}
                                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
                                    {/* Colonna Sinistra - Richieste Ricevute */}
                                    <div className="space-y-4">
                                        <div className="flex items-center justify-between">
                                            <h4 className="text-lg font-bold text-white flex items-center gap-2">
                                                <div
                                                    className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></div>
                                                Richieste Ricevute
                                            </h4>
                                            <span
                                                className="px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-400 text-xs font-bold border border-emerald-500/30">
      {matchReceived.length}
    </span>
                                        </div>

                                        {/* Lista Card (limitata o completa) */}
                                        <div
                                            className="space-y-3 max-h-[600px] overflow-y-auto scrollbar-thin scrollbar-thumb-white/10 scrollbar-track-transparent">
                                            <AnimatePresence>
                                                {displayedReceived.length > 0 ? (
                                                    displayedReceived.map((match) => (
                                                        <MatchRequestCard
                                                            key={match.id}
                                                            user={match}
                                                            type="received"
                                                            onAccept={() => handleAcceptMatch(match.matchID)}
                                                            onReject={() => handleRejectMatch(match.matchID)}
                                                        />
                                                    ))
                                                ) : (
                                                    <div
                                                        className="flex flex-col items-center justify-center py-12 text-center bg-slate-950/30 rounded-2xl border border-white/5">
                                                        <div className="relative mb-4">
                                                            <div
                                                                className="absolute inset-0 bg-emerald-500/20 blur-2xl rounded-full"></div>
                                                            <div
                                                                className="relative w-16 h-16 rounded-full bg-slate-900/50 border border-white/10 flex items-center justify-center">
                                                                <Heart size={28} className="text-slate-600"/>
                                                            </div>
                                                        </div>
                                                        <p className="text-sm font-semibold text-slate-400 mb-1">
                                                            Nessuna richiesta ricevuta
                                                        </p>
                                                        <p className="text-xs text-slate-500">
                                                            Le nuove richieste appariranno qui
                                                        </p>
                                                    </div>
                                                )}
                                            </AnimatePresence>
                                        </div>

                                        {/* Bottone "Mostra Altre" */}
                                        {matchReceived.length > INITIAL_DISPLAY_LIMIT && (
                                            <motion.button
                                                whileHover={{scale: 1.02}}
                                                whileTap={{scale: 0.98}}
                                                onClick={() => setShowAllReceived(!showAllReceived)}
                                                className="w-full py-3 rounded-xl bg-slate-800/50 text-slate-400 hover:bg-emerald-500/20 hover:text-emerald-400 border border-slate-700/50 hover:border-emerald-500/30 transition-all font-semibold text-sm flex items-center justify-center gap-2"
                                            >
                                                {showAllReceived ? (
                                                    <>
                                                        <ChevronDown size={16} className="rotate-180"/>
                                                        Mostra meno
                                                    </>
                                                ) : (
                                                    <>
                                                        <ChevronDown size={16}/>
                                                        Mostra
                                                        altre {matchReceived.length - INITIAL_DISPLAY_LIMIT} richieste
                                                    </>
                                                )}
                                            </motion.button>
                                        )}
                                    </div>


                                    {/* Colonna Destra - Richieste Inviate */}
                                    <div className="space-y-4">
                                        <div className="flex items-center justify-between">
                                            <h4 className="text-lg font-bold text-white flex items-center gap-2">
                                                <div className="w-2 h-2 rounded-full bg-purple-400 animate-pulse"></div>
                                                Richieste Inviate
                                            </h4>
                                            <span
                                                className="px-3 py-1 rounded-full bg-purple-500/20 text-purple-400 text-xs font-bold border border-purple-500/30">
                                              {matchSent.length}
                                            </span>
                                        </div>

                                        <div
                                            className="space-y-3 max-h-[600px] overflow-y-auto scrollbar-thin scrollbar-thumb-white/10 scrollbar-track-transparent">
                                            <AnimatePresence>
                                                {displayedSent.length > 0 ? (
                                                    displayedSent.map((match) => (
                                                        <MatchRequestCard
                                                            key={match.matchID}
                                                            user={match}
                                                            type="sent"
                                                            onCancel={() => handleCancelSentMatch(match.matchID)}

                                                        />
                                                    ))
                                                ) : (
                                                    <div
                                                        className="flex flex-col items-center justify-center py-12 text-center bg-slate-950/30 rounded-2xl border border-white/5">
                                                        <div className="relative mb-4">
                                                            <div
                                                                className="absolute inset-0 bg-purple-500/20 blur-2xl rounded-full"></div>
                                                            <div
                                                                className="relative w-16 h-16 rounded-full bg-slate-900/50 border border-white/10 flex items-center justify-center">
                                                                <Clock size={28} className="text-slate-600"/>
                                                            </div>
                                                        </div>
                                                        <p className="text-sm font-semibold text-slate-400 mb-1">
                                                            Nessuna richiesta inviata
                                                        </p>
                                                        <p className="text-xs text-slate-500">
                                                            Vai alla sezione Matching per trovare match
                                                        </p>
                                                    </div>
                                                )}
                                            </AnimatePresence>
                                        </div>

                                        {matchSent.length > INITIAL_DISPLAY_LIMIT && (
                                            <motion.button
                                                whileHover={{scale: 1.02}}
                                                whileTap={{scale: 0.98}}
                                                onClick={() => setShowAllSent(!showAllSent)}
                                                className="w-full py-3 rounded-xl bg-slate-800/50 text-slate-400 hover:bg-purple-500/20 hover:text-purple-400 border border-slate-700/50 hover:border-purple-500/30 transition-all font-semibold text-sm flex items-center justify-center gap-2"
                                            >
                                                {showAllSent ? (
                                                    <>
                                                        <ChevronDown size={16} className="rotate-180"/>
                                                        Mostra meno
                                                    </>
                                                ) : (
                                                    <>
                                                        <ChevronDown size={16}/>
                                                        Mostra
                                                        altre {matchSent.length - INITIAL_DISPLAY_LIMIT} richieste
                                                    </>
                                                )}
                                            </motion.button>
                                        )}
                                    </div>

                                </div>
                            </div>

                            {/* Sezione Storico Match */}
                            <div className="border-t border-white/5 pt-6 md:pt-8">
                                <div
                                    className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
                                    <div>
                                        <h4 className="text-xl font-bold text-white mb-1">Storico Match</h4>
                                        <p className="text-sm text-slate-400">
                                            {matchHistory.length} {matchHistory.length === 1 ? 'connessione' : 'connessioni'} confermata/e
                                        </p>
                                    </div>

                                    {/* Select View Mode - Dropdown Coerente */}
                                    <ViewModeDropdown
                                        selected={matchViewMode}
                                        onChange={setMatchViewMode}
                                    />
                                </div>

                                {/* Contenuto Vista */}
                                <AnimatePresence mode="wait">
                                    {matchHistory.length > 0 ? (
                                        matchViewMode === 'list' ? (
                                            <motion.div
                                                key="list-view"
                                                initial={{opacity: 0, y: 20}}
                                                animate={{opacity: 1, y: 0}}
                                                exit={{opacity: 0, y: -20}}
                                                transition={{duration: 0.3}}
                                            >
                                                <MatchList matches={displayedHistory}/>
                                                {/* Bottone Mostra Altri Match Storico */}
                                                {matchHistory.length > INITIAL_ITEMS_LIMIT && (
                                                    <motion.button
                                                        whileHover={{scale: 1.02}}
                                                        whileTap={{scale: 0.98}}
                                                        onClick={() => setShowAllHistory(!showAllHistory)}
                                                        className="w-full mt-4 py-3 rounded-xl bg-slate-800/50 border border-white/10 text-slate-400 hover:text-white hover:border-purple-500/30 transition-all flex items-center justify-center gap-2 font-semibold text-sm"
                                                    >
                                                        {showAllHistory ? (
                                                            <>
                                                                <ChevronLeft size={16}/>
                                                                Mostra meno
                                                            </>
                                                        ) : (
                                                            <>
                                                                Carica altro
                                                                ({matchHistory.length - INITIAL_ITEMS_LIMIT})
                                                                <ChevronRight size={16}/>
                                                            </>
                                                        )}
                                                    </motion.button>
                                                )}

                                            </motion.div>
                                        ) : (
                                            <motion.div
                                                key="globe-view"
                                                initial={{opacity: 0, scale: 0.95}}
                                                animate={{opacity: 1, scale: 1}}
                                                exit={{opacity: 0, scale: 0.95}}
                                                transition={{duration: 0.3}}
                                                className="relative rounded-2xl overflow-hidden border border-white/10 bg-slate-950/50"
                                                style={{height: '600px'}}
                                            >
                                                <InfiniteMenu
                                                    items={displayedHistory.map((match, index) => ({
                                                        image: match.senderName,
                                                        link: `https://instagram.com/${match.contactsInfo}`,
                                                        title: match.senderName,
                                                        description: match.stud,
                                                    }))}
                                                    scale={1}
                                                />
                                                {/* Bottone Mostra Storico Completo */}
                                                {matchHistory.length > INITIAL_ITEMS_LIMIT && (
                                                    <motion.button
                                                        whileHover={{scale: 1.02}}
                                                        whileTap={{scale: 0.98}}
                                                        onClick={() => setShowAllHistory(!showAllHistory)}
                                                        className="w-full mt-4 py-3 rounded-xl bg-slate-800/50 border border-white/10 text-slate-400 hover:text-white hover:border-purple-500/30 transition-all flex items-center justify-center gap-2 font-semibold text-sm"
                                                    >
                                                    </motion.button>
                                                )}

                                            </motion.div>
                                        )
                                    ) : (
                                        <motion.div
                                            key="empty-state"
                                            initial={{opacity: 0}}
                                            animate={{opacity: 1}}
                                            className="flex flex-col items-center justify-center py-20 text-center bg-slate-950/30 rounded-2xl border border-white/5"
                                        >
                                            <div className="relative mb-6">
                                                <div
                                                    className="absolute inset-0 bg-purple-500/20 blur-3xl rounded-full"/>
                                                <div
                                                    className="relative w-20 h-20 rounded-full bg-slate-900/50 border border-white/10 flex items-center justify-center">
                                                    <Users size={32} className="text-slate-600"/>
                                                </div>
                                            </div>
                                            <p className="text-lg font-semibold text-slate-400 mb-2">
                                                Nessun match confermato
                                            </p>
                                            <p className="text-sm text-slate-500 max-w-md">
                                                Accetta le richieste ricevute o cerca nuovi match per costruire la tua
                                                rete universitaria.
                                            </p>
                                        </motion.div>
                                    )}
                                </AnimatePresence>
                            </div>
                        </motion.section>
                    )}


                    {/* ==================== MODALE DETTAGLIO EVENTO ==================== */}
                    <AnimatePresence>
                        {detailEvent && (
                            <div className="fixed inset-0 z-[100] flex items-center justify-center p-0 md:p-4">
                                {/* Backdrop */}
                                <motion.div
                                    initial={{opacity: 0}}
                                    animate={{opacity: 1}}
                                    exit={{opacity: 0}}
                                    transition={{duration: 0.3}}
                                    onClick={() => setDetailEvent(null)}
                                    className="absolute inset-0 bg-black/60 backdrop-blur-md"
                                />

                                {/* Card Principale + Panel Laterale */}
                                <div
                                    className="relative z-10 flex flex-col md:flex-row items-end md:items-start justify-end md:justify-center w-full h-full md:h-auto md:max-h-[90vh] pointer-events-none">
                                    {/* CARD PRINCIPALE */}
                                    <motion.div
                                        layout
                                        initial={{opacity: 0, y: 100}}
                                        animate={{opacity: 1, y: 0}}
                                        exit={{opacity: 0, y: 100}}
                                        transition={{type: 'spring', stiffness: 300, damping: 30}}
                                        className="w-full md:w-[600px] h-auto max-h-[92vh] md:max-h-[90vh] bg-[#0F131F] border-t md:border border-white/10 rounded-t-[2.5rem] md:rounded-[2.5rem] overflow-hidden shadow-2xl flex flex-col pointer-events-auto"
                                    >
                                        {/* Immagine Header */}
                                        <div className="relative h-48 md:h-64 shrink-0">
                                            <img
                                                src={detailEvent.posterUrl}
                                                className={`w-full h-full object-cover ${new Date(detailEvent.dateAndTime) < new Date() ? 'grayscale opacity-80' : ''}`}
                                                alt=""
                                            />
                                            <div
                                                className="absolute inset-0 bg-gradient-to-t from-[#0F131F] via-transparent to-transparent"/>

                                            {/* Bottone Chiudi */}
                                            <button
                                                onClick={() => setDetailEvent(null)}
                                                className="absolute top-6 right-6 p-2 bg-black/30 backdrop-blur-xl rounded-full text-white border border-white/10 hover:bg-white/10 z-50"
                                            >
                                                <X size={20}/>
                                            </button>

                                            {/* Info Overlay */}
                                            <div className="absolute bottom-6 left-6 right-6 md:left-8 md:right-8">
                                                <div className="flex gap-2 mb-2 md:mb-3">
                <span
                    className="px-3 py-1 bg-younify-blue/80 backdrop-blur-md rounded-lg text-[10px] md:text-xs font-bold text-white uppercase">
                  {detailEvent.city}
                </span>
                                                    {new Date(detailEvent.dateAndTime) < new Date() && (
                                                        <span
                                                            className="px-3 py-1 bg-slate-800/80 backdrop-blur-md rounded-lg text-[10px] md:text-xs font-bold text-slate-400 uppercase">
                    Concluso
                  </span>
                                                    )}
                                                </div>
                                                <h2 className="text-2xl md:text-5xl font-black text-white leading-tight break-words">
                                                    {detailEvent.name}
                                                </h2>
                                                <p className="text-slate-400 text-sm md:text-base mt-2 font-medium flex items-center gap-2">
                                                    <Users size={16} className="opacity-70"/>
                                                    Organizzato da <span
                                                    className="text-white font-semibold">{detailEvent.hostName}</span>
                                                </p>
                                            </div>
                                        </div>

                                        {/* Contenuto Scrollabile */}
                                        <div
                                            className="p-5 md:p-8 overflow-y-auto space-y-5 md:space-y-8 flex-1 scrollbar-thin scrollbar-thumb-white/10">
                                            {/* Info Cards */}
                                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 md:gap-4">
                                                <div
                                                    className="bg-white/5 p-3 md:p-4 rounded-2xl border border-white/5">
                                                    <Calendar className="text-younify-blue mb-2"/>
                                                    <p className="text-[10px] md:text-xs text-slate-500 font-bold uppercase">Data</p>
                                                    <p className="text-white text-sm md:text-base font-medium">
                                                        {new Date(detailEvent.dateAndTime).toLocaleString('it-IT', {
                                                            weekday: 'short',
                                                            day: 'numeric',
                                                            month: 'short',
                                                            hour: '2-digit',
                                                            minute: '2-digit',
                                                        })}
                                                    </p>
                                                </div>

                                                <div
                                                    className="bg-white/5 p-3 md:p-4 rounded-2xl border border-white/5">
                                                    <MapPin className="text-rose-500 mb-2"/>
                                                    <p className="text-[10px] md:text-xs text-slate-500 font-bold uppercase">Dove</p>
                                                    <p className="text-white text-sm md:text-base font-medium">{detailEvent.street}</p>
                                                </div>
                                            </div>

                                            {/* Descrizione */}
                                            <div>
                                                <h3 className="text-base md:text-lg font-bold text-white mb-2">Descrizione</h3>
                                                <p className="text-slate-400 leading-relaxed text-sm md:text-lg break-words whitespace-pre-wrap">
                                                    {detailEvent.description}
                                                </p>
                                            </div>

                                            {/* Partecipanti (Cliccabile) */}
                                            <div
                                                onClick={() => setShowParticipantsPanel(!showParticipantsPanel)}
                                                className={`flex items-center justify-between p-3 md:p-4 rounded-2xl border cursor-pointer transition-all group ${
                                                    showParticipantsPanel
                                                        ? 'bg-younify-blue/10 border-younify-blue/30'
                                                        : 'bg-white/5 border-white/5 hover:bg-white/10'
                                                }`}
                                            >
                                                <div className="flex items-center gap-3">
                                                    <div className={`p-2 rounded-full transition-colors ${
                                                        showParticipantsPanel
                                                            ? 'bg-younify-blue text-white'
                                                            : 'bg-younify-blue/20 text-younify-blue group-hover:bg-younify-blue group-hover:text-white'
                                                    }`}>
                                                        <Users size={20}/>
                                                    </div>
                                                    <div>
                                                        <p className={`text-sm font-bold ${showParticipantsPanel ? 'text-younify-blue' : 'text-white'}`}>
                                                            Lista Partecipanti
                                                        </p>
                                                        <p className="text-xs text-slate-400">{detailEvent.participantsCount} persone</p>
                                                    </div>
                                                </div>
                                                {showParticipantsPanel ? (
                                                    <ChevronLeft size={20} className="text-younify-blue"/>
                                                ) : (
                                                    <ChevronRight size={20}
                                                                  className="text-slate-500 group-hover:text-white"/>
                                                )}
                                            </div>
                                        </div>

                                        {/* Footer Actions (Solo User) */}
                                        {!isHost && (
                                            <div
                                                className="p-5 md:p-6 border-t border-white/5 bg-[#0F131F] backdrop-blur-xl flex gap-4 z-20 shrink-0 pb-8 md:pb-6">
                                                <motion.button
                                                    onClick={() => {
                                                        const isEventPast = new Date(detailEvent.dateAndTime) < new Date();
                                                        if (!isEventPast) {
                                                            handleUnjoinEvent(detailevent.ID);
                                                            setDetailEvent(null);
                                                        }
                                                    }}
                                                    disabled={new Date(detailEvent.dateAndTime) < new Date()}
                                                    whileTap={new Date(detailEvent.dateAndTime) < new Date() ? {} : {scale: 0.95}}
                                                    className={`relative flex-1 h-14 md:h-16 rounded-2xl font-bold text-lg flex items-center justify-center overflow-hidden border shadow-lg transition-all ${
                                                        new Date(detailEvent.dateAndTime) < new Date()
                                                            ? 'bg-white/5 border-white/10 text-slate-500 cursor-not-allowed'
                                                            : 'bg-rose-500 border-rose-400 text-white hover:bg-rose-600'
                                                    }`}
                                                >
                                                    <AnimatePresence mode="wait" initial={false}>
                                                        {new Date(detailEvent.dateAndTime) < new Date() ? (
                                                            <motion.div
                                                                key="past"
                                                                initial={{y: 20, opacity: 0}}
                                                                animate={{y: 0, opacity: 1}}
                                                                exit={{y: -20, opacity: 0}}
                                                                className="flex items-center gap-2"
                                                            >
                                                                <span>Evento Passato</span>
                                                                <Clock size={20}/>
                                                            </motion.div>
                                                        ) : (
                                                            <motion.div
                                                                key="unjoin"
                                                                initial={{y: 20, opacity: 0}}
                                                                animate={{y: 0, opacity: 1}}
                                                                exit={{y: -20, opacity: 0}}
                                                                className="flex items-center gap-2"
                                                            >
                                                                <span>Disdici Partecipazione</span>
                                                                <X size={20}/>
                                                            </motion.div>
                                                        )}
                                                    </AnimatePresence>
                                                </motion.button>
                                            </div>
                                        )}
                                    </motion.div>

                                    {/* CARD PARTECIPANTI (Laterale Desktop / Bottom Mobile) */}
                                    <AnimatePresence>
                                        {showParticipantsPanel && (
                                            <motion.div
                                                initial={window.innerWidth < 768 ? {y: 100} : {
                                                    width: 0,
                                                    opacity: 0,
                                                    marginLeft: 0
                                                }}
                                                animate={window.innerWidth < 768 ? {y: 0} : {
                                                    width: 350,
                                                    opacity: 1,
                                                    marginLeft: 24
                                                }}
                                                exit={window.innerWidth < 768 ? {y: 100} : {
                                                    width: 0,
                                                    opacity: 0,
                                                    marginLeft: 0
                                                }}
                                                transition={{duration: 0.4, ease: [0.16, 1, 0.3, 1]}}
                                                className={`bg-[#0F131F] border border-white/10 shadow-2xl flex flex-col overflow-hidden pointer-events-auto ${
                                                    window.innerWidth < 768
                                                        ? 'absolute inset-x-0 bottom-0 h-[85vh] rounded-t-[2.5rem] z-50'
                                                        : 'rounded-[2.5rem] max-h-[90vh] shrink-0'
                                                }`}
                                            >
                                                <div
                                                    className={`${window.innerWidth < 768 ? 'w-full' : 'w-[350px]'} flex flex-col h-full`}>
                                                    {/* Header */}
                                                    <div
                                                        className="p-6 border-b border-white/5 flex justify-between items-center bg-[#0F131F]">
                                                        <h3 className="text-xl font-bold text-white">Partecipanti</h3>
                                                        <button
                                                            onClick={() => setShowParticipantsPanel(false)}
                                                            className="p-2 hover:bg-white/10 rounded-full text-slate-400 hover:text-white transition-colors"
                                                        >
                                                            <X size={20}/>
                                                        </button>
                                                    </div>

                                                    {/* Lista */}
                                                    <div
                                                        className="flex-1 overflow-y-auto p-4 space-y-2 scrollbar-thin scrollbar-thumb-white/10">
                                                        {detailEvent.participantsList && detailEvent.participantsList.length > 0 ? (
                                                            detailEvent.participantsList.map((user, idx) => (
                                                                <motion.div
                                                                    key={idx}
                                                                    initial={{opacity: 0, x: -10}}
                                                                    animate={{opacity: 1, x: 0}}
                                                                    transition={{delay: idx * 0.05}}
                                                                    className="flex items-center gap-4 p-3 rounded-xl hover:bg-white/5 transition-colors"
                                                                >
                                                                    <img
                                                                        src={user.avatar}
                                                                        alt=""
                                                                        className="w-10 h-10 rounded-full object-cover ring-2 ring-white/10"
                                                                    />
                                                                    <span
                                                                        className="text-slate-200 font-medium">{user.name}</span>
                                                                </motion.div>
                                                            ))
                                                        ) : (
                                                            <div className="py-10 text-center text-slate-500">
                                                                <Users size={32} className="mx-auto mb-2 opacity-30"/>
                                                                <p>Ancora nessun partecipante.</p>
                                                            </div>
                                                        )}
                                                    </div>
                                                </div>
                                            </motion.div>
                                        )}
                                    </AnimatePresence>
                                </div>
                            </div>
                        )}
                    </AnimatePresence>

                </div>
            </div>

            {/* ==================== MODALE MODIFICA EVENTO ==================== */}
            <AnimatePresence>
                {isEditModalOpen && isHost && (
                    <div
                        className="fixed inset-0 z-[120] flex items-center justify-center p-0 md:p-4 overflow-x-hidden">
                        {/* Backdrop */}
                        <motion.div
                            initial={{opacity: 0}}
                            animate={{opacity: 1}}
                            exit={{opacity: 0}}
                            onClick={() => setIsEditModalOpen(false)}
                            className="absolute inset-0 bg-slate-950/80 backdrop-blur-md"
                        />

                        {/* Card Modale */}
                        <motion.div
                            initial={{scale: 0.95, opacity: 0, y: 20}}
                            animate={{scale: 1, opacity: 1, y: 0}}
                            exit={{scale: 0.95, opacity: 0, y: 20}}
                            className="relative w-full h-[96dvh] md:h-auto max-w-4xl bg-[#0F131F] border-t md:border border-white/10 rounded-t-[2rem] md:rounded-[2.5rem] shadow-2xl flex flex-col overflow-hidden max-h-[100dvh] md:max-h-[90vh] mt-auto md:mt-0"
                        >
                            {/* Header */}
                            <div
                                className="p-4 md:p-6 border-b border-white/5 flex justify-between items-center bg-[#0F131F] z-10 shrink-0">
                                <h2 className="text-lg md:text-2xl font-black text-white tracking-tight">Modifica
                                    Evento</h2>
                                <button
                                    onClick={() => setIsEditModalOpen(false)}
                                    className="p-2 hover:bg-white/10 rounded-full text-slate-400 hover:text-white transition-colors"
                                >
                                    <X size={24}/>
                                </button>
                            </div>

                            {/* Form */}
                            <form
                                className="flex-1 overflow-y-auto p-3 md:p-8 space-y-3 md:space-y-6 scrollbar-thin scrollbar-thumb-white/10">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3 md:gap-8 h-full">
                                    {/* COLONNA SINISTRA */}
                                    <div className="flex flex-col gap-3 md:gap-6">
                                        {/* Upload Foto */}
                                        <div className="space-y-1 md:space-y-2">
                                            <div className="flex justify-between">
                                                <label
                                                    className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1">
                                                    Locandina
                                                </label>
                                                {editFormErrors.poster && (
                                                    <span className="text-[10px] md:text-xs font-bold text-rose-500">
                      {editFormErrors.poster}
                    </span>
                                                )}
                                            </div>
                                            <div
                                                onClick={() => fileInputEditRef.current?.click()}
                                                className={`relative w-full h-28 md:h-56 rounded-xl border ${
                                                    editFormErrors.poster || editFileError
                                                        ? 'border-rose-500'
                                                        : 'border-white/10 hover:border-younify-blue/50'
                                                } bg-white/0.02 flex flex-col items-center justify-center cursor-pointer overflow-hidden group transition-all`}
                                            >
                                                {editPreviewImage ? (
                                                    <>
                                                        <img
                                                            src={editPreviewImage}
                                                            alt="Uploaded"
                                                            className="w-full h-full object-cover opacity-80 group-hover:opacity-100 transition-opacity"
                                                        />
                                                        <div
                                                            className="absolute inset-0 flex items-center justify-center bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity">
                                                            <Upload size={24} className="text-white drop-shadow-lg"/>
                                                        </div>
                                                        <button
                                                            onClick={handleEditRemoveImage}
                                                            type="button"
                                                            className="absolute top-1 right-1 p-1 bg-black/60 hover:bg-rose-500/90 text-white rounded-full transition-colors z-20 backdrop-blur-md shadow-lg border border-white/10"
                                                        >
                                                            <X size={12}/>
                                                        </button>
                                                    </>
                                                ) : (
                                                    <div
                                                        className="flex flex-col items-center text-slate-500 group-hover:text-slate-300 transition-colors">
                                                        <ImageIcon size={20}
                                                                   className={`mb-1 ${editFormErrors.poster ? 'text-rose-500' : ''}`}/>
                                                        <p className="text-[10px] md:text-sm font-medium">Carica
                                                            foto</p>
                                                    </div>
                                                )}
                                                <input
                                                    type="file"
                                                    ref={fileInputEditRef}
                                                    onChange={handleEditImageUpload}
                                                    accept="image/jpeg, image/png, image/webp, image/jpg"
                                                    className="hidden"
                                                />
                                            </div>
                                            {editFileError && (
                                                <div
                                                    className="flex items-center gap-1 text-rose-500 text-[10px] md:text-xs mt-1 ml-1">
                                                    <AlertCircle size={12}/>
                                                    {editFileError}
                                                </div>
                                            )}
                                        </div>

                                        {/* Descrizione Desktop */}
                                        <div className="space-y-1 md:space-y-2 flex-1 flex-col hidden md:flex">
                                            <div className="flex justify-between">
                                                <label
                                                    className="text-xs font-bold text-slate-500 uppercase ml-1">Descrizione</label>
                                                {editFormErrors.description ? (
                                                    <span
                                                        className="text-xs font-bold text-rose-500">{editFormErrors.description}</span>
                                                ) : (
                                                    <span
                                                        className={`text-xs font-bold ${editFormData.description.length > 200 ? 'text-rose-500' : 'text-slate-600'}`}>
                      {editFormData.description.length}/200
                    </span>
                                                )}
                                            </div>
                                            <textarea
                                                maxLength={200}
                                                value={editFormData.description}
                                                onChange={(e) => handleEditInputChange('description', e.target.value)}
                                                placeholder="Dettagli..."
                                                className={`${getEditInputStyle('description')} resize-none flex-1 min-h-[150px]`}
                                            />
                                        </div>
                                    </div>

                                    {/* COLONNA DESTRA */}
                                    <div className="flex flex-col gap-2 md:gap-5 pt-0 md:pt-1 min-w-0">
                                        {/* Nome Evento */}
                                        <div className="space-y-1 md:space-y-2">
                                            <div className="flex justify-between">
                                                <label
                                                    className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1">Nome
                                                    Evento</label>
                                                {editFormErrors.name ? (
                                                    <span
                                                        className="text-[10px] md:text-xs font-bold text-rose-500">{editFormErrors.name}</span>
                                                ) : (
                                                    <span
                                                        className={`text-[10px] md:text-xs font-bold ${editFormData.name.length > 100 ? 'text-rose-500' : 'text-slate-600'}`}>
                      {editFormData.name.length}/100
                    </span>
                                                )}
                                            </div>
                                            <input
                                                type="text"
                                                maxLength={100}
                                                value={editFormData.name}
                                                onChange={(e) => handleEditInputChange('name', e.target.value)}
                                                placeholder="Es. Erasmus Party"
                                                className={getEditInputStyle('name')}
                                            />
                                        </div>

                                        {/* Data */}
                                        <div className="space-y-1 md:space-y-2 w-full min-w-0">
                                            <div className="flex justify-between">
                                                <label
                                                    className="text-10px md:text-xs font-bold text-slate-500 uppercase ml-1 flex items-center gap-1.5">
                                                    <Clock size={12}/>
                                                    Data
                                                </label>
                                                {editFormErrors.dateAndTime && (
                                                    <span className="text-10px md:text-xs font-bold text-rose-500">
                                                    {editFormErrors.dateAndTime}
                                                  </span>
                                                )}
                                            </div>
                                            <input
                                                type="datetime-local"
                                                min={minEditDateTime}
                                                value={editFormData.dateAndTime}
                                                onChange={(e) => handleEditInputChange('dateAndTime', e.target.value)}
                                                style={{boxSizing: 'border-box'}}
                                                className={`${getEditInputStyle('dateAndTime')} w-full max-w-full appearance-none [-webkit-calendar-picker-indicator]:invert [-webkit-calendar-picker-indicator]:opacity-50`}
                                            />
                                        </div>


                                        {/* Città */}
                                        <div className="space-y-1 md:space-y-2 relative z-20">
                                            <div className="flex justify-between">
                                                <label
                                                    className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1">Città</label>
                                                {editFormErrors.city ? (
                                                    <span
                                                        className="text-[10px] md:text-xs font-bold text-rose-500">{editFormErrors.city}</span>
                                                ) : (
                                                    <span
                                                        className={`text-[10px] md:text-xs font-bold ${editFormData.city.length > 30 ? 'text-rose-500' : editFormData.city.length > 0 && !isEditCityValid ? 'text-amber-500' : 'text-slate-600'}`}>
                      {editFormData.city.length > 0 && !isEditCityValid ? 'Non valida' : `${editFormData.city.length}/30`}
                    </span>
                                                )}
                                            </div>
                                            <div className="relative">
                                                <input
                                                    type="text"
                                                    maxLength={30}
                                                    value={editFormData.city}
                                                    onChange={(e) => {
                                                        handleEditInputChange('city', e.target.value);
                                                        if (e.target.value.length > 0) setShowEditCityDropdown(true);
                                                    }}
                                                    onFocus={() => {
                                                        if (editFormData.city.length > 0) setShowEditCityDropdown(true);
                                                    }}
                                                    onBlur={() => setTimeout(() => setShowEditCityDropdown(false), 200)}
                                                    placeholder="Cerca..."
                                                    className={`${getEditInputStyle('city')} ${editFormData.city.length > 0 && !isEditCityValid ? 'border-amber-500/50 focus:border-amber-500' : ''}`}
                                                />

                                                {/* Dropdown Città */}
                                                <AnimatePresence>
                                                    {showEditCityDropdown && editFormData.city.length > 1 && (
                                                        <motion.div
                                                            initial={{opacity: 0, y: 5}}
                                                            animate={{opacity: 1, y: 0}}
                                                            exit={{opacity: 0, y: 5}}
                                                            className="absolute top-full left-0 right-0 mt-2 bg-slate-900 border border-white/10 rounded-xl max-h-40 overflow-y-auto shadow-2xl z-50 scrollbar-thin scrollbar-thumb-white/20"
                                                        >
                                                            <div className="p-1">
                                                                {comuniList
                                                                    .filter((c) => c.nome.toLowerCase().startsWith(editFormData.city.toLowerCase()))
                                                                    .slice(0, 50)
                                                                    .map((c, i) => (
                                                                        <button
                                                                            key={i}
                                                                            type="button"
                                                                            className="w-full text-left p-2.5 hover:bg-white/10 rounded-lg text-slate-300 hover:text-white text-sm transition-colors"
                                                                            onClick={() => {
                                                                                setEditFormData({
                                                                                    ...editFormData,
                                                                                    city: c.nome
                                                                                });
                                                                                setShowEditCityDropdown(false);
                                                                                if (editFormErrors.city) {
                                                                                    setEditFormErrors((prev) => ({
                                                                                        ...prev,
                                                                                        city: null
                                                                                    }));
                                                                                }
                                                                            }}
                                                                        >
                                                                            {c.nome}
                                                                        </button>
                                                                    ))}
                                                                {comuniList.filter((c) => c.nome.toLowerCase().startsWith(editFormData.city.toLowerCase())).length === 0 && (
                                                                    <div
                                                                        className="p-3 text-center text-slate-500 text-sm">Nessuna
                                                                        città trovata</div>
                                                                )}
                                                            </div>
                                                        </motion.div>
                                                    )}
                                                </AnimatePresence>
                                            </div>
                                        </div>

                                        {/* Indirizzo + CAP */}
                                        <div className="grid grid-cols-3 gap-2 md:gap-4">
                                            <div className="col-span-2 space-y-1 md:space-y-2">
                                                <div className="flex justify-between">
                                                    <label
                                                        className="text-[10px] md:text-xs font-bold text-slate-500 uppercase ml-1">Indirizzo</label>
                                                    {editFormErrors.street && <span
                                                        className="text-[10px] md:text-xs font-bold text-rose-500">!</span>}
                                                </div>
                                                <input
                                                    type="text"
                                                    maxLength={60}
                                                    value={editFormData.street}
                                                    onChange={(e) => handleEditInputChange('street', e.target.value)}
                                                    placeholder="Via Roma 1"
                                                    className={getEditInputStyle('street')}
                                                />
                                            </div>

                                            {/* CAP */}
                                            <div className="space-y-1 md:space-y-2 relative z-20">
                                                <div className="flex justify-between">
                                                    <label
                                                        className="text-10px md:text-xs font-bold text-slate-500 uppercase ml-1">
                                                        CAP
                                                    </label>
                                                    {editFormErrors.CAP ? (
                                                        <span className="text-10px md:text-xs font-bold text-rose-500">
                                                        {editFormErrors.CAP}
                                                      </span>
                                                    ) : (
                                                        <span
                                                            className={`text-10px md:text-xs font-bold ${
                                                                editFormData.CAP.length === 5
                                                                    ? 'text-green-500'
                                                                    : editFormData.CAP.length > 0
                                                                        ? 'text-amber-500'
                                                                        : 'text-slate-600'
                                                            }`}
                                                        >
                                                        {editFormData.CAP.length}/5
                                                      </span>
                                                    )}
                                                </div>
                                                <div className="relative">
                                                    <input
                                                        type="text"
                                                        inputMode="numeric"
                                                        pattern="[0-9]*"
                                                        maxLength={5}
                                                        value={editFormData.CAP}
                                                        onChange={(e) => {
                                                            // Accetta solo numeri
                                                            const value = e.target.value.replace(/\D/g, '');
                                                            handleEditInputChange('CAP', value);
                                                        }}
                                                        placeholder="12345"
                                                        className={getEditInputStyle('CAP')}
                                                    />
                                                </div>
                                            </div>

                                        </div>

                                        {/* Errori Indirizzo/CAP */}
                                        {(editFormErrors.street || editFormErrors.CAP) && (
                                            <div className="flex gap-4">
                                                <div className="col-span-2 w-2/3">
                                                    {editFormErrors.street &&
                                                        <p className="text-[10px] text-rose-500 ml-1">{editFormErrors.street}</p>}
                                                </div>
                                                <div className="col-span-1 w-1/3">
                                                    {editFormErrors.CAP &&
                                                        <p className="text-[10px] text-rose-500 ml-1">{editFormErrors.CAP}</p>}
                                                </div>
                                            </div>
                                        )}

                                        {/* Descrizione Mobile */}
                                        <div className="space-y-1 flex flex-col md:hidden">
                                            <div className="flex justify-between">
                                                <label
                                                    className="text-[10px] font-bold text-slate-500 uppercase ml-1">Descrizione</label>
                                                {editFormErrors.description ? (
                                                    <span
                                                        className="text-[10px] font-bold text-rose-500">{editFormErrors.description}</span>
                                                ) : (
                                                    <span
                                                        className={`text-[10px] font-bold ${editFormData.description.length > 200 ? 'text-rose-500' : 'text-slate-600'}`}>
                      {editFormData.description.length}/200
                    </span>
                                                )}
                                            </div>
                                            <textarea
                                                maxLength={200}
                                                rows={2}
                                                value={editFormData.description}
                                                onChange={(e) => handleEditInputChange('description', e.target.value)}
                                                placeholder="Dettagli..."
                                                className={`${getEditInputStyle('description')} resize-none py-1.5`}
                                            />
                                        </div>
                                    </div>
                                </div>
                            </form>

                            {/* Footer */}
                            <div className="p-3 md:p-6 border-t border-white/5 bg-[#0F131F] shrink-0 pb-6 md:pb-6">
                                <button
                                    onClick={handleSaveEdit}
                                    className="w-full py-3 md:py-4 rounded-xl font-bold bg-white text-black hover:bg-slate-200 transition-colors shadow-lg uppercase tracking-wide text-sm active:scale-[0.99]"
                                >
                                    Salva Modifiche
                                </button>
                            </div>
                        </motion.div>


                    </div>
                )}
            </AnimatePresence>

            {/* MODALE CONFERMA ELIMINAZIONE */}
            <ConfirmDeleteModal
                isOpen={confirmModal.isOpen}
                onClose={closeConfirmModal}
                onConfirm={confirmModal.onConfirm}
                title={confirmModal.title}
                message={confirmModal.message}
                type={confirmModal.type}
                confirmText={confirmModal.confirmText}
            />

        </div>
    );
};
export default ProfilePage;