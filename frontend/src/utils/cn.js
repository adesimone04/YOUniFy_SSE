import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";

/*
 * Utility 'cn' (ClassNames) per la gestione avanzata delle classi CSS in React.
 * Questa funzione risolve due problemi principali:
 * 1. Applicazione condizionale delle classi (es. isHover && "bg-red-500") tramite 'clsx'.
 * 2. Risoluzione dei conflitti di Tailwind (es. unire "px-2" e "px-4" senza che il CSS si rompa) tramite 'twMerge'.
 * * @param {...any} inputs - Lista variabile di stringhe, oggetti o array di classi.
 * @returns {string} - Una stringa di classi CSS valida e ottimizzata.
 */
export function cn(...inputs) {
    // Prima 'clsx' processa gli argomenti (gestendo oggetti, array e valori falsy).
    // Poi 'twMerge' prende il risultato e rimuove le classi Tailwind ridondanti o in conflitto,
    // garantendo che l'ultima classe specificata abbia sempre la priorità.
    return twMerge(clsx(inputs));
}