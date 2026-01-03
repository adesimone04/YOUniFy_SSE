import React from "react";
// Importiamo l'utility 'cn' (solitamente clsx + tailwind-merge) per unire le classi in modo sicuro,
// gestendo conflitti e condizioni dinamiche.
import { cn } from "../../../utils/cn";

const Card = ({
                  children,
                  className = "",
              }) => {
    return (
        <div
            className={cn(
                // Struttura base: Posizionamento relativo per contenere gli overlay assoluti
                // e arrotondamento massimo per ottenere la forma a "pillola".
                "relative rounded-[9999px] overflow-hidden",

                // Stile "Glassmorphism": Sfondo scuro semi-trasparente combinato con un forte backdrop-blur
                "bg-slate-900/40",
                "backdrop-blur-3xl",

                // Definizione bordi: Bordo sottile semi-trasparente per separazione visiva e ombra per profondità
                "border border-white/10 shadow-2xl",

                // Permette di estendere o sovrascrivere gli stili base passando una className prop
                className
            )}
        >
            {/* Layer decorativo (Overlay):
                Aggiunge un gradiente diagonale molto sottile per simulare un riflesso di luce.
                'pointer-events-none' è fondamentale per permettere le interazioni (click) sugli elementi sottostanti.
            */}
            <div className="absolute inset-0 bg-gradient-to-br from-white/5 to-transparent pointer-events-none" />

            {/* Contenitore del contenuto effettivo:
                'z-10' e 'relative' assicurano che il contenuto (children) appaia sopra l'overlay decorativo.
            */}
            <div className="relative z-10 p-2">{children}</div>
        </div>
    );
};

export default Card;