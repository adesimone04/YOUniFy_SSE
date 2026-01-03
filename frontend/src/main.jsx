import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
// Importazione degli stili globali (Tailwind + Custom CSS).
// Essendo importato qui, viene applicato a tutta l'applicazione prima di qualsiasi altro stile.
import './index.css'

// 1. Selezione del nodo DOM radice:
// React cerca nel file 'index.html' un elemento con id="root".
// Questo sarà l'unico contenitore HTML gestito interamente da React.
const rootElement = document.getElementById('root');

// 2. Creazione della React Root (React 18+):
// 'createRoot' abilita le funzionalità di concorrenza di React 18, migliorando le performance.
ReactDOM.createRoot(rootElement).render(
    // 3. React StrictMode:
    // Strumento di sviluppo che aiuta a identificare problemi comuni.
    // Nota: In sviluppo, esegue i componenti due volte (double-render) per rilevare side-effects impuri.
    // Questo comportamento non avviene in produzione.
    <React.StrictMode>
        {/* Renderizza il componente principale che contiene tutta la logica dell'app */}
        <App />
    </React.StrictMode>,
)