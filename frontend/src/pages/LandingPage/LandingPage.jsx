import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
    ShieldCheck, Users, Compass, Megaphone, BarChart3, Bell, Heart, Share2,
    MessageSquarePlus, Hash, HeartHandshake
} from 'lucide-react';

// Navbar globale
import Navbar from '../../components/layout/Navbar/Navbar';
// Componenti UI personalizzati per animazioni specifiche
import RotatingText from '../../components/ui/RotatingText/RotatingText';
import TextType from '../../components/ui/TextType/TextType';
import MetaBalls from '../../components/ui/MetaBalls/MetaBalls';
import { LogoLoop } from '../../components/ui/LogoLoop/LogoLoop';
// Stili CSS Module per effetti specifici (es. background blobs)
import styles from './LandingPage.module.css';
import WebApp from '../../components/business/InstallAppGuide';
import Footer from '../../components/layout/Footer/Footer';

const LandingPage = () => {

    // Stato per gestire il caricamento iniziale e scatenare le animazioni di ingresso
    const [isLoaded, setIsLoaded] = useState(false);

    // Stato per gestire il tab attivo nella sezione "Events" (slider tra 'studenti' e 'host')
    const [activeTab, setActiveTab] = useState('studenti');

    // Effect: Imposta isLoaded a true al montaggio del componente
    useEffect(() => {
        setIsLoaded(true);
    }, []);

    // Lista 1: Tag visualizzati nel primo carosello (LogoLoop)
    const UNIVERSITY_TAGS_1 = [
        { title: "Ingegneria Informatica 💻" },
        { title: "Medicina e Chirurgia 🩺" },
        { title: "Economia Aziendale 📊" },
        { title: "Psicologia 🧠" },
        { title: "Giurisprudenza ⚖️" },
        { title: "Design della Comunicazione 🎨" },
        { title: "Lettere Moderne 📚" },
        { title: "Architettura 📐" }
    ];

    // Lista 2: Tag visualizzati nel secondo carosello (LogoLoop)
    const UNIVERSITY_TAGS_2 = [
        { title: "Sessione Estiva ☀️" },
        { title: "Cercasi Coinquilino 🏠" },
        { title: "Pausa Caffè ☕️" },
        { title: "Esame passato! 🎓" },
        { title: "Vita da Fuorisede 🚆" },
        { title: "Aperitivo post-lezione 🍹" },
        { title: "Notte prima degli esami 🌙" },
        { title: "Gruppo Studio 📝" }
    ];

    // Configurazione del contenuto per la sezione Events
    // Mappa le chiavi 'studenti' e 'host' con icone e testi corrispondenti
    const EVENT_CONTENT = {
        studenti: [
            { icon: <Compass size={24} />, text: "Esplora eventi ad hoc sulla base dei tuoi interessi" },
            { icon: <Users size={24} />, text: "Visualizza i partecipanti in anteprima per socializzare pre e post evento" },
            { icon: <ShieldCheck size={24} />, text: "Aderisci solo ad eventi sicuri, pensati per gli studenti e promossi da Host certificati" }
        ],
        host: [
            { icon: <Megaphone size={24} />, text: "Promuovi e personalizza gli eventi in un unico posto, verso un pubblico mirato e certificato" },
            { icon: <BarChart3 size={24} />, text: "Monitora in tempo reale l'andamento delle adesioni" },
            { icon: <Bell size={24} />, text: "Gestisci e notifica facilmente le informazioni utili ai partecipanti" }
        ]
    };

    return (
        <div className="relative min-h-screen w-full bg-slate-950 flex flex-col items-center overflow-x-hidden font-sans">
            <Navbar />

            {/* --- Background Layer --- */}
            {/* Elementi decorativi sfocati (Blobs) posizionati in fixed per non influenzare lo scroll */}
            <div className="fixed inset-0 z-0 pointer-events-none">
                <div className={`${styles.blob} ${styles.blue}`}></div>
                <div className={`${styles.blob} ${styles.indigo}`}></div>
                <div className={`${styles.blob} ${styles.rose}`}></div>
                <div className={`${styles.blob} ${styles.teal}`}></div>
                <div className={`${styles.blob} ${styles.purple}`}></div>
                <div className={`${styles.blob} ${styles.cyan}`}></div>
            </div>

            {/* Titolo principale con animazione di entrata */}
            <section className="relative z-10 min-h-screen flex items-center justify-center text-center px-6 font-inter">
                <motion.main
                    initial={{ opacity: 0, y: 20 }}
                    animate={isLoaded ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 1.5 }}
                >
                    <h1 className="text-8xl md:text-9xl font-black tracking-tighter text-white leading-none font-inter">
                        YOUni<span className="text-younify-blue drop-shadow-[0_0_20px_rgba(29,78,216,0.4)] font-inter">Fy</span>
                    </h1>

                    {/* Sottotitolo con testo rotante - MODIFICATO QUI */}
                    <div className="mt-8 text-slate-400 tracking-[0.3em] text-2xl md:text-2xl font-bold flex items-baseline justify-center gap-4 font-sans lowercase leading-none">
                        <span>uniamo</span>
                        <RotatingText words={['studenti', 'università']} interval={3500} />
                    </div>
                </motion.main>
            </section>

            {/* Utilizza l'effetto macchina da scrivere (TextType) */}
            <section className="relative z-10 max-w-7xl mx-auto px-8 py-32 flex flex-col gap-6 font-sans">

                {/* Blocco 1: Loop infinito di scrittura/cancellazione */}
                <div className="max-w-6xl font-inter h-20">
                    <TextType
                        text={[
                            "Laddove oggi esistono spazi confusi e insicuri...",
                            "...noi nasciamo per eliminarne il rumore."
                        ]}
                        className="text-3xl md:text-4xl font-black text-white leading-[1.1] tracking-tight font-inter"
                        typingSpeed={50}
                        deletingSpeed={30}
                        pauseDuration={2000}
                        loop={true}
                        startOnVisible={true}
                        cursorClassName="text-younify-blue ml-1"
                        cursorCharacter="|"
                    />
                </div>

                {/* Blocco 2: Scrittura singola (si ferma alla fine) */}
                <div className="ml-auto max-w-4xl text-right">
                    <TextType
                        text="Con lo scopo di costruire una community autentica, basata su connessioni personali reali, stiamo ridefinendo il modo in cui gli studenti vivono la propria università."
                        className="text-xl md:text-xl font-medium text-white leading-relaxed font-sans"
                        typingSpeed={30}
                        startOnVisible={true}
                        loop={false}
                        showCursor={false}
                        initialDelay={1000}
                    />
                </div>
            </section>

            {/* --- SECTION 1: EVENTS --- */}
            {/* Include il sistema di tab per cambiare contenuto */}
            <section id="events" className="relative z-10 w-full py-32 px-8">
                <div className="max-w-6xl mx-auto flex flex-col items-center gap-16 font-inter text-center">
                    <h2 className="text-3xl md:text-5xl font-black text-white font-inter">Un nuovo modo di vivere e promuovere gli eventi</h2>

                    {/* Switcher Tab personalizzato */}
                    <div className="relative p-1 bg-slate-900/50 backdrop-blur-md border border-white/10 rounded-full flex items-center w-80 font-sans shadow-xl">
                        {/* Sfondo animato che segue il tab attivo */}
                        <motion.div className="absolute h-10 bg-younify-blue rounded-full z-0"
                                    animate={{ x: activeTab === 'studenti' ? 4 : 154 }}
                                    transition={{ type: "spring", stiffness: 300, damping: 30 }}
                                    style={{ width: '154px' }}
                        />
                        <button onClick={() => setActiveTab('studenti')} className={`relative z-10 flex-1 py-2 text-xs font-bold uppercase tracking-widest ${activeTab === 'studenti' ? 'text-white' : 'text-slate-300'}`}>Studenti</button>
                        <button onClick={() => setActiveTab('host')} className={`relative z-10 flex-1 py-2 text-xs font-bold uppercase tracking-widest ${activeTab === 'host' ? 'text-white' : 'text-slate-300'}`}>Host</button>
                    </div>

                    {/* Contenuto dinamico delle card gestito con AnimatePresence per transizioni fluide */}
                    <div className="w-full max-w-4xl font-sans text-left">
                        <AnimatePresence mode="wait">
                            <motion.div key={activeTab} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} className="flex flex-col gap-5">
                                {EVENT_CONTENT[activeTab].map((item, index) => (
                                    <div key={index} className="flex items-center gap-6 p-6 bg-slate-900/40 backdrop-blur-md border border-white/10 rounded-3xl group hover:bg-slate-900/60 transition-all">
                                        <div className="p-3 bg-younify-blue/10 rounded-2xl text-younify-blue">{item.icon}</div>
                                        <p className="text-xl font-medium text-slate-300 group-hover:text-white font-sans">{item.text}</p>
                                    </div>
                                ))}
                            </motion.div>
                        </AnimatePresence>
                    </div>
                </div>
            </section>

            {/* --- SECTION 2: MATCH --- */}
            {/* Layout a griglia con animazione WebGL laterale */}
            <section id="match" className="relative z-10 w-full max-w-7xl mx-auto px-8 pt-40 pb-20">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-10 items-center">
                    {/* Colonna Sinistra: Testo Descrittivo */}
                    <div className="lg:col-span-2 space-y-12 z-20">
                        <h2 className="text-3xl md:text-4xl font-black text-white leading-tight font-inter tracking-tight">
                            Sei un fuorisede o uno studente in cerca di <span className="text-younify-blue font-inter">nuovi compagni d'avventura</span> intraprendenti come te?
                        </h2>
                        <div className="flex flex-col gap-10 font-sans">
                            <div className="flex items-center gap-6 group">
                                <div className="p-3 bg-rose-500/10 rounded-xl border border-rose-500/20 shadow-lg"><Heart className="text-rose-500" size={24} /></div>
                                <p className="text-xl md:text-2xl font-medium text-slate-300 group-hover:text-white leading-relaxed font-sans">
                                    Effettua matching con studenti con interessi simili
                                </p>
                            </div>
                            <div className="flex items-center gap-6 group">
                                <div className="p-3 bg-teal-500/10 rounded-xl border border-teal-500/20 shadow-lg"><Share2 className="text-teal-500" size={24} /></div>
                                <p className="text-xl md:text-2xl font-medium text-slate-300 group-hover:text-white leading-relaxed font-sans">
                                    Scambia informazioni di contatto social per creare e rafforzare interazioni autentiche
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Colonna Destra: Componente MetaBalls (WebGL) */}
                    <div className="w-full lg:w-[140%] lg:-mr-32 h-[600px] flex items-center justify-center relative z-10">
                        <div className="absolute inset-0 w-full h-full scale-110">
                            <MetaBalls
                                color="#1d4ed8"
                                cursorBallColor="#1d4ed8"
                                cursorBallSize={1.5}
                                enableTransparency={true}
                                animationSize={40}
                                ballCount={15}
                                speed={0.5}
                            />
                        </div>
                    </div>
                </div>
            </section>

            {/* Carosello infinito sopra la sezione Thread */}
            <section className="relative z-20 w-full pt-32 flex flex-col gap-6">
                <LogoLoop
                    logos={UNIVERSITY_TAGS_1}
                    direction="left"
                    speed={60}
                    gap={20}
                    pauseOnHover={true}
                    renderItem={(item) => (
                        <div className="px-5 py-3 rounded-full border border-white/10 bg-white/5 backdrop-blur-md text-slate-300 font-medium font-sans text-lg whitespace-nowrap hover:bg-white/10 hover:border-younify-blue/50 hover:text-white transition-colors cursor-default">
                            {item.title}
                        </div>
                    )}
                />
                <LogoLoop
                    logos={UNIVERSITY_TAGS_2}
                    direction="right"
                    speed={50}
                    gap={20}
                    pauseOnHover={true}
                    renderItem={(item) => (
                        <div className="px-5 py-3 rounded-full border border-white/10 bg-white/5 backdrop-blur-md text-slate-300 font-medium font-sans text-lg whitespace-nowrap hover:bg-white/10 hover:border-younify-blue/50 hover:text-white transition-colors cursor-default">
                            {item.title}
                        </div>
                    )}
                />
            </section>

            {/* --- SECTION 3: THREAD --- */}
            {/* Sezione informativa con layout a 3 colonne */}
            <section id="thread" className="relative z-10 w-full max-w-7xl mx-auto px-8 py-24">
                <div className="flex flex-col items-center text-center mb-16">
                    <h2 className="text-3xl md:text-5xl font-black text-white tracking-tight font-inter leading-tight max-w-5xl">
                        Uno spazio di confronto e condivisione di idee, <span className="text-younify-blue">sano e rispettoso</span>.
                    </h2>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {/* Card Feature 1 */}
                    <div className="p-8 rounded-[2rem] bg-white/5 border border-white/10 hover:bg-white/10 hover:border-younify-blue/30 transition-all duration-300 group">
                        <div className="w-14 h-14 bg-indigo-500/20 rounded-2xl flex items-center justify-center mb-6 text-indigo-400 group-hover:scale-110 transition-transform">
                            <MessageSquarePlus size={32} />
                        </div>
                        <p className="text-xl font-medium text-slate-300 group-hover:text-white font-sans leading-relaxed">
                            Pubblica domande, idee e curiosità
                        </p>
                    </div>

                    {/* Card Feature 2 */}
                    <div className="p-8 rounded-[2rem] bg-white/5 border border-white/10 hover:bg-white/10 hover:border-younify-blue/30 transition-all duration-300 group">
                        <div className="w-14 h-14 bg-rose-500/20 rounded-2xl flex items-center justify-center mb-6 text-rose-400 group-hover:scale-110 transition-transform">
                            <Hash size={32} />
                        </div>
                        <p className="text-xl font-medium text-slate-300 group-hover:text-white font-sans leading-relaxed">
                            Partecipa a discussioni su topic mirati
                        </p>
                    </div>

                    {/* Card Feature 3 */}
                    <div className="p-8 rounded-[2rem] bg-white/5 border border-white/10 hover:bg-white/10 hover:border-younify-blue/30 transition-all duration-300 group">
                        <div className="w-14 h-14 bg-teal-500/20 rounded-2xl flex items-center justify-center mb-6 text-teal-400 group-hover:scale-110 transition-transform">
                            <HeartHandshake size={32} />
                        </div>
                        <p className="text-xl font-medium text-slate-300 group-hover:text-white font-sans leading-relaxed">
                            Scambia consigli e informazioni preziose con un intera community studentesca
                        </p>
                    </div>
                </div>
            </section>

            {/* Carosello infinito inferiore (ordine invertito) */}
            <section className="relative z-20 w-full pb-20 flex flex-col gap-6">
                <LogoLoop
                    logos={UNIVERSITY_TAGS_2}
                    direction="left"
                    speed={60}
                    gap={20}
                    pauseOnHover={true}
                    renderItem={(item) => (
                        <div className="px-5 py-3 rounded-full border border-white/10 bg-white/5 backdrop-blur-md text-slate-300 font-medium font-sans text-lg whitespace-nowrap hover:bg-white/10 hover:border-younify-blue/50 hover:text-white transition-colors cursor-default">
                            {item.title}
                        </div>
                    )}
                />
                <LogoLoop
                    logos={UNIVERSITY_TAGS_1}
                    direction="right"
                    speed={50}
                    gap={20}
                    pauseOnHover={true}
                    renderItem={(item) => (
                        <div className="px-5 py-3 rounded-full border border-white/10 bg-white/5 backdrop-blur-md text-slate-300 font-medium font-sans text-lg whitespace-nowrap hover:bg-white/10 hover:border-younify-blue/50 hover:text-white transition-colors cursor-default">
                            {item.title}
                        </div>
                    )}
                />
            </section>
            <WebApp />
            <Footer />
        </div>
    );
};

export default LandingPage;