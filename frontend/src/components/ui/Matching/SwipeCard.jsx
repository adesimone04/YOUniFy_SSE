import React from 'react';
import { motion, useMotionValue, useTransform } from 'framer-motion';
import { GraduationCap, Sparkles, BookOpen } from 'lucide-react';

const SwipeCard = ({ user, onSwipeLeft, onSwipeRight, exitDir, style }) => {
    // --- LOGICA SWIPE ---
    const x = useMotionValue(0);
    const rotate = useTransform(x, [-200, 200], [-10, 10]);
    const opacityLike = useTransform(x, [50, 150], [0, 1]);
    const opacityNope = useTransform(x, [-50, -150], [0, 1]);

    // Bordo colorato dinamico
    const borderColor = useTransform(x, [-200, 0, 200], ["rgba(244, 63, 94, 0.5)", "rgba(255,255,255,0.1)", "rgba(34, 197, 94, 0.5)"]);

    const handleDragEnd = (event, info) => {
        const offset = info.offset.x;
        const velocity = info.velocity.x;

        // Swipe RIGHT: offset positivo OPPURE velocità alta verso destra
        if (offset > 100 || (offset > 50 && velocity > 500)) {
            onSwipeRight?.();
        }
        // Swipe LEFT: offset negativo OPPURE velocità alta verso sinistra
        else if (offset < -100 || (offset < -50 && velocity < -500)) {
            onSwipeLeft?.();
        }
    };

    // Gestione sicura dei tag (usa tags o interests)
    const displayTags = user.tags || user.interests || [];

    return (
        <motion.div
            style={{ x, rotate, ...style }}
            drag="x"
            dragConstraints={{ left: 0, right: 0 }}
            onDragEnd={handleDragEnd}
            initial={{ scale: 1, opacity: 1 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{
                x: exitDir * 300,
                opacity: 0,
                rotate: exitDir * 20,
                transition: { duration: 0.3, ease: "easeOut" }
            }}
            className="absolute top-0 left-0 w-full h-full cursor-grab active:cursor-grabbing will-change-transform"
        >
            <motion.div
                style={{ borderColor }}
                className="relative w-full h-full rounded-[32px] overflow-hidden bg-[#1a1a1a] shadow-2xl border border-white/10"
            >
                {/* 1. IMMAGINE (Sempre a colori, niente grayscale) */}
                <img
                    src={user.image}
                    alt={user.name}
                    className="w-full h-full object-cover pointer-events-none select-none"
                    draggable="false"
                />

                {/* 2. TIMBRI VISIVI RIMOSSI - Ora ci sono solo le particelle animate */}

                {/* 3. GRADIENTE SFONDO */}
                <div className="absolute inset-0 bg-gradient-to-t from-black via-black/40 to-transparent opacity-90 pointer-events-none" />

                {/* 4. NUOVA STRUTTURA INFO (Stile Badge/Pillole) */}
                <div className="absolute bottom-0 left-0 right-0 p-6 md:p-8 flex flex-col justify-end z-30 select-none pointer-events-none h-full">

                    {/* Header: Nome + Età */}
                    <div className="flex justify-between items-end mb-3">
                        <div className="flex flex-col">
                            <h2 className="text-2xl md:text-3xl font-extrabold font-sans text-white drop-shadow-lg">
                                {user.name} {user.surname}, <span className="text-xl md:text-2xl font-semibold text-slate-200">{user.age}</span>
                            </h2>
                        </div>
                    </div>

                    {/* Badge Info: Università & Corso (Nuovo Design) */}
                    <div className="flex flex-col gap-2 mb-4">
                        {/* Università */}
                        <div className="flex items-center gap-2.5 text-white/90 text-sm font-medium bg-white/10 backdrop-blur-md w-fit px-3 py-1.5 rounded-xl border border-white/10">
                            <GraduationCap size={16} className="text-younify-blue shrink-0" />
                            <span className="truncate max-w-[260px]">{user.university}</span>
                        </div>

                        {/* Corso */}
                        <div className="flex items-center gap-2.5 text-white/80 text-xs font-medium bg-white/5 backdrop-blur-md w-fit px-3 py-1.5 rounded-xl border border-white/5">
                            <BookOpen size={14} className="text-purple-400 shrink-0" />
                            <span className="truncate max-w-[260px]">{user.course}</span>
                        </div>
                    </div>
                </div>
            </motion.div>
        </motion.div>
    );
};

export default SwipeCard;
