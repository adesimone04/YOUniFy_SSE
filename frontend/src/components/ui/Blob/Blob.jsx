import React from 'react';

// Componente funzionale riutilizzabile per generare forme sfumate (Blobs) nello sfondo.
const Blob = ({ variant }) => {
    // Costruzione dinamica della stringa delle classi CSS.
    // 1. styles.blob: Classe base condivisa (definisce dimensioni, filtri blur, animazioni base).
    // 2. styles[variant]: Accesso dinamico alla classe specifica del colore (es. styles.blue, styles.indigo).
    const blobClass = `${styles.blob} ${styles[variant]}`;

    // Renderizza un semplice div che fungerà da elemento decorativo visivo grazie al CSS applicato.
    return <div className={blobClass} />;
};

export default Blob;