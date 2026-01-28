import React, { useState, useMemo, useEffect } from 'react';
import { toast } from 'react-toastify';
import { Bell, CalendarClock, Users, Calendar } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import axiosClient from "../../services/axiosClient";
import AppNavbar from '../../components/layout/Navbar/AppNavbar';

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

const adaptNotification = (backendNotif) => {
    const typeMapping = {
        'MATCH': 'MATCHING_REQUEST',
        'EVENT': 'EVENT_REMINDER',
        'SYSTEM': 'SYSTEM'
    };

    return {
        id: backendNotif.ID?.toString() || String(Math.random()),
        type: typeMapping[backendNotif.type] || 'SYSTEM',
        actor: {
            name: backendNotif.senderName || 'Younify',
            avatar: backendNotif.senderPhoto || 'https://via.placeholder.com/120'
        },
        content: backendNotif.content || '',
        createdAt: backendNotif.date || new Date().toISOString(),
        isRead: false
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

const NotificationsPage = () => {
    const [notifications, setNotifications] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                setIsLoading(true);
                setError(null);

                const response = await axiosClient.get('/notify/view', {
                    params: { page: 0, size: 50 },
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: true
                });

                const notificationsData = response.data?.data?.content || [];
                const adaptedNotifications = notificationsData.map(adaptNotification);
                setNotifications(adaptedNotifications);
            } catch (error) {

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
    }, []);

    const sortedNotifications = useMemo(
        () => [...notifications].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)),
        [notifications]
    );

    const hasNotifications = notifications.length > 0;

    return (
        <div className="fixed inset-0 w-full h-full font-sans text-slate-200 selection:bg-younify-blue/30 selection:text-white overflow-hidden bg-slate-900/60 backdrop-blur-md lg:bg-transparent lg:backdrop-blur-none">

            {/* NAVBAR */}
            <div className="relative z-50">
                <AppNavbar />
            </div>

            {/* CONTENUTO SCROLLABILE */}
            <div className="absolute inset-0 overflow-y-auto z-10 scroll-smooth [&::-webkit-scrollbar]:hidden">
                <div className="pt-12 px-4 lg:pl-32">

                    {/* HEADER */}
                    <div className="relative z-10 pt-4 pb-6 flex flex-col items-start">
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
                    </div>

                    {/* LISTA NOTIFICHE */}
                    <div className="max-w-5xl mx-auto pb-40">
                        {error ? (
                            <div className="flex flex-col items-center justify-center text-center px-8 py-20">
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
                            <div className="space-y-3">
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
                                                <div className="absolute inset-px rounded-2xl bg-gradient-to-r from-younify-blue/0 via-purple-500/0 to-pink-500/0 opacity-0 group-hover:opacity-100 group-hover:from-younify-blue/5 group-hover:via-purple-500/5 group-hover:to-pink-500/5 transition-opacity pointer-events-none" />

                                                <div className={`relative flex h-10 w-10 md:h-11 md:w-11 items-center justify-center rounded-xl ${cfg.bg} flex-shrink-0 self-center`}>
                                                    <Icon className={`h-5 w-5 md:h-5 md:w-5 ${cfg.iconColor}`} />
                                                </div>

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
                            <div className="flex flex-col items-center justify-center text-center px-8 py-20">
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
                    </div>
                </div>
            </div>
        </div>
    );
};

export default NotificationsPage;
