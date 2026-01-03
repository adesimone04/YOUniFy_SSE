import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

const Navbar = () => {
    const [hoveredLink, setHoveredLink] = useState(null);

    const navLinks = [
        { name: 'Events', href: '#events' },
        { name: 'Match', href: '#match' },
        { name: 'Thread', href: '#thread' },
    ];

    return (
        <motion.nav
            initial={{ y: -100, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            className="fixed top-0 left-0 w-full z-[100] px-6 py-5 flex justify-center"
        >
            {/* CONTAINER */}
            <div className="w-full max-w-6xl grid grid-cols-3 items-center px-6 py-3 bg-white/[0.05] backdrop-blur-2xl border border-white/10 rounded-full shadow-2xl relative overflow-hidden">

                {/* Layer decorativo */}
                <div className="absolute inset-0 bg-gradient-to-b from-white/[0.08] to-transparent pointer-events-none" />

                {/* 1. Logo */}
                <div className="text-xl font-black tracking-tighter text-white justify-self-start z-10 font-inter">
                    YOUni<span className="text-younify-blue">Fy</span>
                </div>

                {/* 2. Link */}
                <div
                    className="hidden md:flex justify-self-center items-center gap-1 relative z-10"
                    onMouseLeave={() => setHoveredLink(null)}
                >
                    {navLinks.map((link) => (
                        <a
                            key={link.name}
                            href={link.href}
                            onMouseEnter={() => setHoveredLink(link.name)}
                            className="relative px-5 py-2 text-base font-medium text-slate-300 hover:text-white transition-colors duration-300 tracking-wide font-sans"
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
                                        className="absolute inset-0 z-10 rounded-full bg-white/[0.08] border border-white/[0.1] backdrop-blur-md"
                                    />
                                )}
                            </AnimatePresence>
                        </a>
                    ))}
                </div>

                {/* 3. CTA */}
                <div className="justify-self-end z-10">
                    <a
                        href="#"
                        className="px-6 py-2.5 bg-white text-slate-950 rounded-full font-bold text-sm tracking-wide hover:scale-105 hover:shadow-[0_0_20px_rgba(255,255,255,0.4)] transition-all duration-300 font-sans"
                    >
                        Get Started
                    </a>
                </div>
            </div>
        </motion.nav>
    );
};

export default Navbar;