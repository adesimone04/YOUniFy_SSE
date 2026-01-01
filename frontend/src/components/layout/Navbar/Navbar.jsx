import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

const Navbar = () => {
    // Stato per tracciare quale link è attualmente sotto il cursore del mouse.
    // Viene usato per renderizzare condizionalmente l'highlight di sfondo.
    const [hoveredLink, setHoveredLink] = useState(null);

    // Configurazione dei link di navigazione.
    // L'uso di un array è per facilitare l'aggiunta o la rimozione di voci in futuro.
    const navLinks = [
        { name: 'Events', href: '#events' },
        { name: 'Match', href: '#match' },
        { name: 'Thread', href: '#thread' },
    ];

    return (
        // Utilizziamo motion.nav per animare l'entrata dell'intera navbar
        // initial: stato di partenza (nascosta in alto e trasparente)
        // animate: stato finale (posizione originale e visibile)
        <motion.nav
            initial={{ y: -100, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            className="fixed top-0 left-0 w-full z-[100] px-6 py-6"
        >
            {/* Container Principale Navbar
                Stile "Glassmorphism": sfondo semi-trasparente (bg-white/[0.05]) + sfocatura (backdrop-blur-2xl)
                Layout: Grid a 3 colonne per allineare Logo (sinistra), Link (centro), CTA (Call to Action) (destra)
            */}
            <div className="max-w-7xl mx-auto grid grid-cols-3 items-center px-8 py-3 bg-white/[0.05] backdrop-blur-2xl border border-white/10 rounded-full shadow-2xl relative overflow-hidden">

                {/* Layer decorativo: Gradiente superiore per simulare un riflesso di luce sul vetro */}
                <div className="absolute inset-0 bg-gradient-to-b from-white/[0.08] to-transparent pointer-events-none" />

                {/* 1. Sezione Logo */}
                <div className="text-2xl font-black tracking-tighter text-white justify-self-start z-10 font-inter">
                    YOUni<span className="text-younify-blue">Fy</span>
                </div>

                {/* 2. Sezione Link di Navigazione */}
                <div
                    className="hidden md:flex justify-self-center items-center gap-2 relative z-10"
                    onMouseLeave={() => setHoveredLink(null)} // Reset dello stato quando il mouse esce dall'area dei link
                >
                    {navLinks.map((link) => (
                        <a
                            key={link.name}
                            href={link.href}
                            onMouseEnter={() => setHoveredLink(link.name)} // Imposta il link attivo per l'highlight
                            className="relative px-6 py-2 text-lg font-semibold text-slate-300 hover:text-white transition-colors duration-300 tracking-wide font-sans"
                        >
                            {/* Testo del link con z-index superiore per stare sopra l'highlight */}
                            <span className="relative z-20">{link.name}</span>

                            {/* Gestione dell'animazione "Highlight" (nav-glow).
                                AnimatePresence permette di animare l'uscita (exit) del componente quando viene smontato.
                            */}
                            <AnimatePresence>
                                {hoveredLink === link.name && (
                                    <motion.span
                                        layoutId="nav-glow" // Framer Motion riconosce questo ID e anima il movimento tra un link e l'altro
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

                {/* 3. Sezione CTA (Call to Action) */}
                <div className="justify-self-end z-10">
                    <a
                        href="#"
                        className="px-6 py-2.5 bg-white text-slate-950 rounded-full font-bold text-sm tracking-wide hover:scale-105 hover:shadow-[0_0_20px_rgba(255,255,255,0.4)] transition-all duration-300 font-sans"
                    >
                        Get started
                    </a>
                </div>
            </div>
        </motion.nav>
    );
};

export default Navbar;