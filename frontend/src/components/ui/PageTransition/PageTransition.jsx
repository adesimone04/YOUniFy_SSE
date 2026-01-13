import React from 'react';
import { motion } from 'framer-motion';

const PageTransition = ({ children, className }) => {
    return (
        <motion.div
            className={className}
            initial={{ opacity: 0, scale: 0.99 }} // Quasi impercettibile
            animate={{
                opacity: 1,
                scale: 1,
                transition: {
                    duration: 0.5,
                    ease: "easeInOut"
                }
            }}
            exit={{
                opacity: 0,
                scale: 1.01, // Si espande leggermente verso lo spettatore
                transition: {
                    duration: 0.4,
                    ease: "easeInOut"
                }
            }}
        >
            {children}
        </motion.div>
    );
};

export default PageTransition;