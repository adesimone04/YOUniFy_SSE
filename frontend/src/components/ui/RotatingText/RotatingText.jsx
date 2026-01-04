// File: src/components/ui/RotatingText/RotatingText.jsx
import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { cn } from "../../../utils/cn";

const RotatingText = ({ words, interval = 3000, className = "" }) => {
    const [index, setIndex] = useState(0);

    useEffect(() => {
        const timer = setInterval(() => {
            setIndex((prevIndex) => (prevIndex + 1) % words.length);
        }, interval);
        return () => clearInterval(timer);
    }, [words, interval]);

    const variants = {
        initial: { y: "100%", opacity: 0 }, // Parte completamente sotto
        animate: { y: 0, opacity: 1 },       // Al centro
        exit: { y: "-100%", opacity: 0 },    // Esce completamente sopra
    };

    return (
        <span className={cn("inline-flex flex-col h-[1.2em] overflow-hidden relative align-bottom", className)}>
            <AnimatePresence mode="wait">
                <motion.span
                    key={index}
                    variants={variants}
                    initial="initial"
                    animate="animate"
                    exit="exit"
                    transition={{ duration: 0.5, ease: "easeInOut" }}
                    // CAMBIATO: da text-blue-500 a text-younify-blue
                    className="absolute left-0 w-full font-bold text-younify-blue whitespace-nowrap"
                >
                    {words[index]}
                </motion.span>
            </AnimatePresence>
            {/* Questo span invisibile mantiene le dimensioni corrette del contenitore */}
            <span className="invisible font-bold whitespace-nowrap">{words[index]}</span>
        </span>
    );
};

export default RotatingText;