import React from 'react';
import { NavLink } from 'react-router-dom';
// Ho importato 'Ticket' per gli eventi e 'Hash' per i thread
import { CalendarHeart, Users, MessagesSquare, Bell, User } from 'lucide-react';
import { motion } from 'framer-motion';

const AppNavbar = ({ mobileAction, shouldShrink }) => {
    const navItems = [
        { name: 'Eventi', path: '/events', icon: <CalendarHeart size={22} strokeWidth={2.5} /> },

        { name: 'Match', path: '/match', icon: <Users size={22} strokeWidth={2.5} /> },

        { name: 'Thread', path: '/thread', icon: <MessagesSquare size={22} strokeWidth={2.5} /> },

        { name: 'Notifiche', path: '/notifications', icon: <Bell size={22} strokeWidth={2.5} /> },

        { name: 'Profilo', path: '/profile', icon: <User size={22} strokeWidth={2.5} /> },
    ];

    return (
        <>
        <div className="fixed left-6 top-1/2 -translate-y-1/2 z-50 hidden lg:flex flex-col justify-center pointer-events-none">

            {/* BARRA DI NAVIGAZIONE GLASSMORPHIC */}
            <nav className="pointer-events-auto relative flex flex-col items-center gap-6 p-4 bg-slate-900/40 backdrop-blur-xl border border-white/10 rounded-[2rem] shadow-2xl shadow-black/50">

                {navItems.map((item) => (
                    <NavLink
                        key={item.name}
                        to={item.path}
                        className="relative group flex items-center justify-center w-12 h-12 outline-none select-none"
                    >
                        {({ isActive }) => (
                            <>
                                {/* --- SLIDER ATTIVO --- */}
                                {isActive && (
                                    <motion.div
                                        layoutId="navbar-active-pill"
                                        className="absolute inset-0 bg-blue-600 rounded-xl shadow-[0_0_20px_rgba(37,99,235,0.5)]"
                                        transition={{
                                            type: "spring",
                                            stiffness: 350,
                                            damping: 30
                                        }}
                                    />
                                )}

                                {/* --- ICONA --- */}
                                <div className={`relative z-10 transition-all duration-300 transform 
                                    ${isActive
                                    ? 'text-white scale-110'
                                    : 'text-slate-400 group-hover:text-white group-hover:scale-110'
                                }`}
                                >
                                    {item.icon}
                                </div>

                                {/* --- TOOLTIP --- */}
                                <div className="absolute left-full ml-5 px-3 py-1.5 bg-slate-900/90 backdrop-blur-md border border-white/10 text-white text-xs font-bold rounded-lg opacity-0 -translate-x-2 group-hover:opacity-100 group-hover:translate-x-0 transition-all duration-300 pointer-events-none whitespace-nowrap shadow-xl z-50">
                                    {item.name}
                                    <div className="absolute top-1/2 -left-1 -mt-1 border-4 border-transparent border-r-slate-900/90" />
                                </div>
                            </>
                        )}
                    </NavLink>
                ))}
            </nav>
        </div>

            {/* MOBILE NAVBAR + ACTION BUTTON */}
            {/* Container flessibile */}
            <div className="fixed bottom-6 left-4 right-4 z-50 lg:hidden flex items-center justify-center gap-2 pointer-events-none">
                {/* Navbar: Si restringe dolcemente quando shouldShrink è true */}
                <nav className={`pointer-events-auto flex items-center justify-around p-3 
    bg-slate-900/70 
    backdrop-blur-xl 
    border border-white/10 
    rounded-[2rem] 
    shadow-2xl shadow-black/50 
    transition-all duration-500 ease-in-out max-w-sm 
    ${shouldShrink ? 'w-[78%]' : 'w-full'}`}
                >
                    {navItems.map((item) => (
                        <NavLink
                            key={item.name}
                            to={item.path}
                            className="relative group flex items-center justify-center w-10 h-10 outline-none select-none"
                        >
                            {({ isActive }) => (
                                <>
                                    {isActive && (
                                        <motion.div
                                            layoutId="navbar-active-mobile"
                                            className="absolute inset-0 bg-blue-600 rounded-xl shadow-[0_0_15px_rgba(37,99,235,0.4)]"
                                            transition={{ type: "spring", stiffness: 350, damping: 30 }}
                                        />
                                    )}
                                    <div className={`relative z-10 transition-all duration-300 transform ${isActive ? 'text-white scale-105' : 'text-slate-400'}`}>
                                        {React.cloneElement(item.icon, { size: 20 })}
                                    </div>
                                </>
                            )}
                        </NavLink>
                    ))}
                </nav>

                {/* Bottone FAB (Appare solo se mobileAction esiste) */}
                <div className={`pointer-events-auto shrink-0 transition-all duration-500 ease-in-out ${shouldShrink ? 'w-[60px] opacity-100 translate-x-0' : 'w-0 opacity-0 translate-x-10'}`}>
                    {mobileAction}
                </div>
            </div>
            </>
    );
};

export default AppNavbar;