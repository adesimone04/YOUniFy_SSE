import React, { useState, useMemo, useEffect } from 'react';
import { toast } from 'react-toastify';
import { Bell, CalendarClock, Users, Calendar } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import axiosClient from "../../services/axiosClient";
import AppNavbar from '../../components/layout/Navbar/AppNavbar';

const now = new Date();

const daysAgo = (n) => {
    const d = new Date(now);
    d.setDate(d.getDate() - n);
    return d.toISOString();
};

const hoursAgo = (h) => {
    const d = new Date(now);
    d.setHours(d.getHours() - h);
    return d.toISOString();
};

// === Helper UI (solo 2 tipi) ===============================================
const typeConfig = {
    EVENT_REMINDER: {
        icon: CalendarClock,
        bg: 'bg-amber-500/15',
        iconColor: 'text-amber-400',
    },
    MATCHING_REQUEST: {
        icon: Users,
        bg: 'bg-purple-500/15',
        iconColor: 'text-purple-400',
    },
    SYSTEM: {
        icon: Bell,
        bg: 'bg-blue-500/15',
        iconColor: 'text-blue-400',
    },
};

// === ADAPTER: Backend DTO -> Frontend UI ===
/**
 Questo adapter mappa i dati del backend al formato UI.
 *
 * Backend Response Structure (NotificationResponse.java):
 * {
 *   ID: Long,
 *   senderName: String,          // Nome del sender
 *   senderPhoto: String,         // CONTIENE LA FOTO PROFILO (URL)
 *   type: "MATCH" | "EVENT" | "SYSTEM",
 *   content: String,
 *   date: String (ISO 8601)
 * }
 */

const adaptNotification = (backendNotif) => {
    // Mappa il type del backend ai tipi UI per le icone
    const typeMapping = {
        'MATCH': 'MATCHING_REQUEST',
        'EVENT': 'EVENT_REMINDER',
        'SYSTEM': 'SYSTEM'
    };

    return {
        id: backendNotif.ID?.toString() || String(Math.random()),  // ID univoco
        type: typeMapping[backendNotif.type] || 'SYSTEM',          // Mappa tipo per icone
        actor: {
            name: backendNotif.senderName || 'Younify',            // Nome sender
            avatar: backendNotif.senderPhoto ||
                'https://via.placeholder.com/120'              // Fallback se mancante
        },
        content: backendNotif.content || '',                       // Contenuto notifica
        createdAt: backendNotif.date || new Date().toISOString(), // Data ISO 8601
        isRead: false                                              // Default false (backend non fornisce)
    };
};

const formatRelativeTime = (iso) => {
    const d = new Date(iso);
    const now = new Date();
    const diffMs = now - d;
    const diffMin = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMin / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMin < 1) return 'Ora';
    if (diffMin < 60) return `${diffMin}m fa`;
    if (diffHours < 24) return `${diffHours}h fa`;
    if (diffDays === 1) return 'Ieri';
    if (diffDays < 7) return `${diffDays}g fa`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)}sett fa`;
    return d.toLocaleDateString('it-IT', { day: 'numeric', month: 'short' });
};

// === COMPONENTE PRINCIPALE === //
const NotificationsPage = () => {
    const [notifications, setNotifications] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                setIsLoading(true);
                setError(null);

                console.log('Chiamata API a /api/notify/view...');


                // Chiamata API reale
                const response = await axiosClient.get('/notify/view', {
                    params: {
                        page: 0,
                        size: 50
                    },
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    withCredentials: true  // Invia cookie di autenticazione
                });

                console.log('✅ Risposta raw:', response);
                console.log('📦 Data wrapper:', response.data);
                console.log('📄 Content array:', response.data?.data?.content);


                // Estrai l'array di notifiche dalla risposta
                // Struttura: response.data.data.content
                const notificationsData = response.data?.data?.content || [];
                console.log(`📊 Trovate ${notificationsData.length} notifiche`);

                // Mappa ogni notifica dal formato backend al formato UI
                const adaptedNotifications = notificationsData.map(adaptNotification);
                console.log('🔄 Notifiche adattate:', adaptedNotifications);


                setNotifications(adaptedNotifications);
            } catch (error) {
                console.error('Errore nel caricamento delle notifiche:', error);

                if (error.response?.status === 403) {
                    toast.error('Accesso negato. Effettua il login.');
                } else if (error.response?.status === 500) {
                    toast.error('Errore del server. Riprova più tardi.');
                } else {
                    toast.error('Impossibile caricare le notifiche.');
                }

                setNotifications([]);
                setError(null);
            } finally {
                setIsLoading(false);
            }
        };

        fetchNotifications();
    }, []); // Esegue solo al mount del componente

    // Ordina cronologicamente (più recente prima)
    const sortedNotifications = useMemo(
        () =>
            [...notifications].sort(
                (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
            ),
        [notifications]
    );

    const hasNotifications = notifications.length > 0;

    return (
        <div
            className="fixed inset-0 flex flex-col text-slate-100 bg-slate-900/20 backdrop-blur-sm lg:bg-transparent lg:backdrop-blur-none"
            style={{
                height: '100dvh',
                paddingTop: 'env(safe-area-inset-top)',
                paddingBottom: 'max(env(safe-area-inset-bottom), 16px)',
            }}
        >
            {/* NAVBAR */}
            <div className="relative z-50 shrink-0 px-4 pt-3 pb-2 flex items-center justify-between gap-2 md:pb-4 lg:block lg:px-0 lg:pt-0">
                <div className="flex-1 md:w-full">
                    <AppNavbar />
                </div>
            </div>

            {/* Contenuto principale */}
            <div className="flex-1 w-full max-w-5xl mx-auto px-4 md:px-6 lg:pl-32 lg:pr-8 flex flex-col pt-2 md:pt-4 lg:pt-8 pb-24 md:pb-24 lg:pb-6 space-y-4 md:space-y-6 overflow-hidden">
                {/* Header - Solo Badge come Titolo Principale */}
                <header className="relative z-10 flex-shrink-0 mt-1">
                    <motion.div
                        initial={{ opacity: 0, y: -20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.6 }}
                        className="inline-flex items-center gap-3 rounded-2xl bg-white/5 px-6 py-3 border border-white/10 backdrop-blur-xl shadow-lg"
                    >
                        <div className="relative">
                            <div className="absolute inset-0 bg-younify-blue/30 blur-lg rounded-full" />
                            <Calendar className="relative h-5 w-5 text-younify-light" />
                        </div>
                        <span className="text-base md:text-base font-bold tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-white to-gray-400">
              Ultimi 30 giorni
            </span>
                    </motion.div>
                </header>

                {/* Wrapper lista */}
                <motion.div
                    layout
                    className="relative flex-1 overflow-hidden rounded-3xl border border-white/10 bg-slate-800/20 backdrop-blur-3xl isolate"
                >
                    {/* Container scrollabile */}
                    {error ? (
                        // ❌ ERROR STATE
                        <div className="h-full flex flex-col items-center justify-center text-center px-8 py-10">
                            <div className="relative mb-6">
                                <div className="absolute inset-0 rounded-full bg-gradient-to-r from-red-500/30 to-orange-500/30 blur-2xl" />
                                <div className="relative flex h-20 w-20 items-center justify-center rounded-full bg-white/5 border border-red-500/20">
                                    <Bell className="h-9 w-9 text-red-400" />
                                </div>
                            </div>
                            <h2 className="text-xl md:text-2xl font-semibold text-white mb-2">
                                Errore di connessione
                            </h2>
                            <p className="text-sm md:text-base text-slate-400 max-w-md mb-4">
                                {error}
                            </p>
                            <button
                                onClick={() => window.location.reload()}
                                className="px-6 py-2.5 bg-younify-blue/20 hover:bg-younify-blue/30 text-younify-light rounded-xl border border-younify-blue/30 transition-all font-medium"
                            >
                                Riprova
                            </button>
                        </div>
                    ) : hasNotifications ? (
                        // ✅ DATA STATE (lista notifiche)
                        <div className="h-full overflow-y-auto px-3 md:px-5 pt-20 md:pt-24 pb-20 md:pb-24 space-y-3 scrollbar-thin scrollbar-thumb-white/10 scrollbar-track-transparent">
                            <AnimatePresence>
                                {sortedNotifications.map((n) => {
                                    const cfg = typeConfig[n.type] ?? typeConfig.EVENT_REMINDER;
                                    const Icon = cfg.icon;
                                    return (
                                        <motion.div
                                            key={n.id}
                                            layout
                                            initial={{ opacity: 0, y: 12, scale: 0.98 }}
                                            animate={{ opacity: 1, y: 0, scale: 1 }}
                                            exit={{ opacity: 0, y: -12, scale: 0.95 }}
                                            transition={{ duration: 0.18 }}
                                            className={`group relative rounded-2xl border border-white/10 bg-white/[0.03] hover:bg-white/[0.06] backdrop-blur-xl px-3.5 py-3 md:px-4 md:py-3.5 flex gap-3 md:gap-4 items-start shadow-[0_18px_45px_rgba(0,0,0,0.45)] ${
                                                n.isRead ? 'opacity-70' : 'opacity-100'
                                            }`}
                                        >
                                            {/* glow */}
                                            <div className="absolute inset-px rounded-2xl bg-gradient-to-r from-younify-blue/0 via-purple-500/0 to-pink-500/0 opacity-0 group-hover:opacity-100 group-hover:from-younify-blue/5 group-hover:via-purple-500/5 group-hover:to-pink-500/5 transition-opacity pointer-events-none" />

                                            {/* Icona tipo */}
                                            <div
                                                className={`relative flex h-10 w-10 md:h-11 md:w-11 items-center justify-center rounded-xl ${cfg.bg} flex-shrink-0 self-center`}
                                            >
                                                <Icon className={`h-5 w-5 md:h-5 md:w-5 ${cfg.iconColor}`} />
                                            </div>

                                            {/* Contenuto */}
                                            <div className="relative flex-1 min-w-0">
                                                <div className="flex items-start justify-between gap-2">
                                                    <div className="flex items-start gap-2.5 min-w-0 flex-1">
                                                        <div className="h-8 w-8 rounded-full overflow-hidden border border-white/15 flex-shrink-0">
                                                            <img
                                                                src={n.actor.avatar}
                                                                alt={n.actor.name}
                                                                className="h-full w-full object-cover"
                                                            />
                                                        </div>
                                                        <div className="min-w-0 flex-1">
                                                            <p className="text-base md:text-base font-semibold text-white break-words">
                                                                {n.actor.name}
                                                            </p>
                                                            <p className="text-[12px] md:text-sm text-slate-300/90 break-words">
                                                                {n.content}
                                                            </p>
                                                        </div>
                                                    </div>
                                                    <span className="ml-2 mt-0.5 text-[10px] md:text-[11px] text-slate-500 whitespace-nowrap flex-shrink-0">
                                        {formatRelativeTime(n.createdAt)}
                                    </span>
                                                </div>
                                            </div>
                                        </motion.div>
                                    );
                                })}
                            </AnimatePresence>
                        </div>
                    ) : (
                        // 📭 EMPTY STATE (anche durante caricamento)
                        <div className="h-full flex flex-col items-center justify-center text-center px-8 py-10">
                            <div className="relative mb-6">
                                <div className="absolute inset-0 rounded-full bg-gradient-to-r from-younify-blue/30 to-purple-500/30 blur-2xl" />
                                <div className="relative flex h-20 w-20 items-center justify-center rounded-full bg-white/5 border border-white/10 shadow-xl shadow-black/40">
                                    <Bell className="h-9 w-9 text-younify-blue" />
                                </div>
                            </div>
                            <h2 className="text-xl md:text-2xl font-semibold text-white mb-2">
                                Nessuna notifica
                            </h2>
                            <p className="text-sm md:text-base text-slate-400 max-w-md">
                                Al momento non ci sono nuove notifiche. Ti avviseremo quando ci saranno aggiornamenti su eventi o richieste di matching.
                            </p>
                        </div>
                    )}
                    <div className="absolute bottom-0 inset-x-0 h-20 bg-gradient-to-t from-slate-800/95 via-slate-800/50 to-transparent md:hidden pointer-events-none z-10 rounded-b-3xl" />
                </motion.div>
            </div>
        </div>
    );
};

export default NotificationsPage;
