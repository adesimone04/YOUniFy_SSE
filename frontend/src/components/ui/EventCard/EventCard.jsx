import React from 'react';
import { motion } from 'framer-motion';
import { MapPin, Calendar } from 'lucide-react';

const EventCard = ({ event, onClick }) => {
    return (
        <motion.div
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => onClick(event)}
            className="group relative flex-shrink-0 w-[280px] md:w-[320px] h-[350px] bg-slate-900/60 backdrop-blur-xl border border-white/10 rounded-[2rem] overflow-hidden shadow-xl hover:border-younify-blue/40 transition-all duration-300 cursor-pointer snap-center"
        >
            {/* Immagine a tutto schermo con sfumatura */}
            <div className="absolute inset-0">
                <img
                    src={event.image}
                    alt={event.title}
                    className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/40 to-transparent" />
            </div>

            {/* Badge Categoria */}
            <div className="absolute top-4 left-4 px-3 py-1 bg-black/50 backdrop-blur-md border border-white/20 rounded-full text-xs font-bold text-white z-20">
                {event.category}
            </div>

            {/* Contenuto in basso */}
            <div className="absolute bottom-0 left-0 w-full p-6 flex flex-col gap-2 z-20">

                {/* Data */}
                <div className="flex items-center gap-2 text-younify-blue text-xs font-bold uppercase tracking-wider">
                    <Calendar size={14} />
                    <span>{event.date.split('•')[0]}</span>
                </div>

                {/* Titolo */}
                <h3 className="text-xl font-black text-white leading-tight line-clamp-2">
                    {event.title}
                </h3>

                {/* Luogo */}
                <div className="flex items-center gap-2 text-slate-300 text-xs">
                    <MapPin size={14} className="text-rose-500" />
                    <span className="truncate">{event.location}</span>
                </div>

            </div>
        </motion.div>
    );
};

export default EventCard;