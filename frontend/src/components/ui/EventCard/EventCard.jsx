import React from 'react';
import { motion } from 'framer-motion';
import { MapPin, Calendar, Users } from 'lucide-react';

const EventCard = ({ event, onClick }) => {
    return (
        <motion.div
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => onClick(event)}
            className="group relative w-full bg-slate-900/60 backdrop-blur-xl border border-white/10 rounded-[1.5rem] md:rounded-[2rem] overflow-hidden shadow-xl hover:border-younify-blue/40 transition-all duration-300 cursor-pointer mb-6 md:mb-8 break-inside-avoid"
        >
            {/* Immagine: Altezza ridotta su mobile */}
            <div className="relative w-full">
                <img
                    src={event.image}
                    alt={event.title}
                    className="w-full h-auto object-cover transition-transform duration-700 group-hover:scale-110"
                    style={{ minHeight: '160px' }} // Ridotto per mobile (prima 200px)
                />
                {/* Overlay scuro per leggibilità */}
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/20 to-transparent" />
            </div>

            {/* Contenuto in basso: Padding ridotto su mobile */}
            <div className="absolute bottom-0 left-0 w-full p-4 md:p-6 flex flex-col gap-2 md:gap-3 z-20">

                {/* Data e Partecipanti */}
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-1.5 md:gap-2 text-younify-blue text-[10px] md:text-xs font-bold uppercase tracking-wider bg-black/60 px-2 py-1 rounded-lg backdrop-blur-sm">
                        <Calendar size={12} className="md:w-3.5 md:h-3.5" />
                        <span>{new Date(event.date).toLocaleDateString('it-IT', { day: 'numeric', month: 'short' })}</span>
                    </div>
                    {event.participantsCount > 0 && (
                        <div className="flex items-center gap-1 text-slate-300 text-[10px] md:text-xs font-medium bg-black/60 px-2 py-1 rounded-full border border-white/10 backdrop-blur-sm">
                            <Users size={10} className="md:w-3 md:h-3" />
                            <span>{event.participantsCount}</span>
                        </div>
                    )}
                </div>

                {/* Titolo: Font ridotto su mobile */}
                <h3 className="text-lg md:text-2xl font-black text-white leading-tight drop-shadow-lg line-clamp-2">
                    {event.title}
                </h3>

                {/* Luogo: Font ridotto su mobile */}
                <div className="flex items-center gap-1.5 md:gap-2 text-slate-300 text-xs md:text-sm drop-shadow-md">
                    <MapPin size={14} className="text-rose-500 shrink-0 md:w-4 md:h-4" />
                    <span className="truncate">{event.location}</span>
                </div>

            </div>
        </motion.div>
    );
};

export default EventCard;