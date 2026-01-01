import React from 'react';
// Importazione del componente pagina principale (LandingPage).
// Questo file funge da entry point visivo per la struttura della pagina.
import LandingPage from './pages/LandingPage/LandingPage';

/*
 * Componente Root (App).
 * Funge da contenitore principale per l'intera applicazione React.
 * Attualmente, l'applicazione è strutturata come una "Single Page" che renderizza la LandingPage.
 */
function App() {
    return (
        // Container globale dell'applicazione.
        // La classe "App" può essere utilizzata per stili globali o reset CSS specifici.
        <div className="App">
            <LandingPage />
        </div>
    );
}

export default App;