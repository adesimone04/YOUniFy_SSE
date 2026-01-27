import React from 'react';
import { motion } from 'framer-motion';
import { MapPin, Calendar, Users, Clock } from 'lucide-react';

const EventCard = ({ event, onClick }) => {
    // Verifica se l'evento è passato
    const isEventPast = new Date(event.date) < new Date();

    return (
        <motion.div
            whileHover={{ scale: isEventPast ? 1 : 1.02 }}
            whileTap={{ scale: isEventPast ? 1 : 0.98 }}
            onClick={() => onClick(event)}
            className={`group relative w-full bg-slate-900/60 backdrop-blur-xl border border-white/10 rounded-[1.5rem] md:rounded-[2rem] overflow-hidden shadow-xl transition-all duration-300 cursor-pointer mb-6 md:mb-8 break-inside-avoid ${
                isEventPast ? 'opacity-75 hover:border-white/20' : 'hover:border-younify-blue/40'
            }`}
        >
            {/* Immagine: Altezza ridotta su mobile */}
            <div className="relative w-full">
                <img
                    src={event.image}
                    alt={event.title}
                    className={`w-full h-auto object-cover transition-transform duration-700 ${
                        isEventPast
                            ? 'blur-[2px] grayscale group-hover:scale-100'
                            : 'group-hover:scale-110'
                    }`}
                    style={{ minHeight: '160px' }}
                />

                {/* Overlay scuro per leggibilità */}
                <div className={`absolute inset-0 ${
                    isEventPast
                        ? 'bg-gradient-to-t from-slate-950 via-slate-900/80 to-slate-800/60'
                        : 'bg-gradient-to-t from-slate-950 via-slate-950/20 to-transparent'
                }`} />

                {/* Badge "EVENTO PASSATO" - In primo piano */}
                {isEventPast && (
                    <div className="absolute inset-0 flex items-center justify-center z-30 pointer-events-none">
                        <motion.div
                            initial={{ opacity: 0, scale: 0.8 }}
                            animate={{ opacity: 1, scale: 1 }}
                            transition={{ duration: 0.3 }}
                            className="bg-slate-800/70 backdrop-blur-md px-4 py-2 md:px-6 md:py-3 rounded-full border border-slate-600/60 pointer-events-auto"
                        >
                            <div className="flex items-center gap-2 text-slate-300">
                                <Clock size={16} className="md:w-5 md:h-5" />
                                <span className="text-xs md:text-sm font-bold uppercase tracking-wider">
                    Evento Passato
                </span>
                            </div>
                        </motion.div>
                    </div>
                )}

            </div>

            {/* Contenuto in basso: Padding ridotto su mobile */}
            <div className="absolute bottom-0 left-0 w-full p-4 md:p-6 flex flex-col gap-2 md:gap-3 z-20">

                {/* Data e Partecipanti */}
                <div className="flex items-center justify-between">
                    <div className={`flex items-center gap-1.5 md:gap-2 text-[10px] md:text-xs font-bold uppercase tracking-wider px-2 py-1 rounded-lg backdrop-blur-sm ${
                        isEventPast
                            ? 'text-slate-500 bg-black/60'
                            : 'text-younify-blue bg-black/60'
                    }`}>
                        <Calendar size={12} className="md:w-3.5 md:h-3.5" />
                        <span>{new Date(event.date).toLocaleDateString('it-IT', { day: 'numeric', month: 'short' })}</span>
                    </div>
                    {event.participantsCount > 0 && (
                        <div className={`flex items-center gap-1 text-[10px] md:text-xs font-medium px-2 py-1 rounded-full border backdrop-blur-sm ${
                            isEventPast
                                ? 'text-slate-500 bg-black/60 border-white/5'
                                : 'text-slate-300 bg-black/60 border-white/10'
                        }`}>
                            <Users size={10} className="md:w-3 md:h-3" />
                            <span>{event.participantsCount}</span>
                        </div>
                    )}
                </div>

                {/* Titolo: Font ridotto su mobile */}
                <h3 className={`text-lg md:text-2xl font-black leading-tight drop-shadow-lg line-clamp-2 ${
                    isEventPast ? 'text-slate-400' : 'text-white'
                }`}>
                    {event.title}
                </h3>

                {/* Luogo: Font ridotto su mobile */}
                <div className={`flex items-center gap-1.5 md:gap-2 text-xs md:text-sm drop-shadow-md ${
                    isEventPast ? 'text-slate-500' : 'text-slate-300'
                }`}>
                    <MapPin size={14} className={`shrink-0 md:w-4 md:h-4 ${
                        isEventPast ? 'text-slate-600' : 'text-rose-500'
                    }`} />
                    <span className="truncate">{event.location}</span>
                </div>

            </div>
        </motion.div>
    );
};

export default EventCard;
