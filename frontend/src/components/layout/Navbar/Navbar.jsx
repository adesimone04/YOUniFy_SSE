import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from "react-router-dom";

const Navbar = () => {
    const [hoveredLink, setHoveredLink] = useState(null);
    const navigate = useNavigate();

    const navLinks = [
        { name: 'Events', href: '#events' },
        { name: 'Match', href: '#match' },
        { name: 'Thread', href: '#thread' },
    ];

    return (
        <motion.nav
            initial={{ y: -100, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            className="fixed top-0 left-0 w-full z-[100] px-4 md:px-6 py-5 flex flex-col items-center"
        >
            {/* CONTAINER PRINCIPALE (Pillola) */}
            <div className="w-full max-w-6xl relative z-50">

                <div className="flex items-center justify-between px-6 py-3 backdrop-blur-xl border border-white/10 rounded-full shadow-2xl relative overflow-hidden">

                    <div className="absolute inset-0 bg-gradient-to-b from-white/10 via-transparent to-transparent pointer-events-none" />

                    {/* 1. Logo (Allineamento Corretto) */}
                    <div
                        className="text-xl font-black tracking-tighter text-white z-10 font-inter cursor-pointer flex items-center leading-none h-full"
                        onClick={() => navigate('/')}
                    >
                        YOUni<span className="text-younify-blue">Fy</span>
                    </div>

                    {/* 2. Link Desktop (Allineamento Corretto) */}
                    <div
                        className="hidden md:flex items-center gap-1 relative z-10"
                        onMouseLeave={() => setHoveredLink(null)}
                    >
                        {navLinks.map((link) => (
                            <a
                                key={link.name}
                                href={link.href}
                                onMouseEnter={() => setHoveredLink(link.name)}
                                className="relative px-5 py-2 text-base font-medium text-slate-300 hover:text-white transition-colors duration-300 tracking-wide font-sans flex items-center"
                            >
                                <span className="relative z-20">{link.name}</span>

                                <AnimatePresence>
                                    {hoveredLink === link.name && (
                                        <motion.span
                                            layoutId="nav-glow"
                                            initial={{ opacity: 0 }}
                                            animate={{ opacity: 1 }}
                                            exit={{ opacity: 0 }}
                                            transition={{ type: "spring", stiffness: 350, damping: 30 }}
                                            // Glow sull'hover: Bianco molto trasparente
                                            className="absolute inset-0 z-10 rounded-full bg-white/10 border border-white/10 backdrop-blur-md"
                                        />
                                    )}
                                </AnimatePresence>
                            </a>
                        ))}
                    </div>

                    {/* 3. CTA (Bottone) (Allineamento Corretto) */}
                    <div className="z-10 flex items-center">
                        <a
                            href="#"
                            onClick={(e) => {
                                e.preventDefault();
                                navigate("/auth");
                            }}
                            className="px-6 py-2 md:py-2.5 bg-white text-slate-950 rounded-full font-bold text-sm tracking-wide hover:scale-105 hover:shadow-[0_0_20px_rgba(255,255,255,0.4)] transition-all duration-300 font-sans flex items-center justify-center leading-none shadow-lg"
                        >
                            Get Started
                        </a>
                    </div>

                </div>
            </div>
        </motion.nav>
    );
};

export default Navbar;