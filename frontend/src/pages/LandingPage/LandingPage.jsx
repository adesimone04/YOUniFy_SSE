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

    const [isLoaded, setIsLoaded] = useState(false);
    const [activeTab, setActiveTab] = useState('studenti');

    useEffect(() => {
        setIsLoaded(true);
    }, []);

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
            <div className="fixed inset-0 z-0 pointer-events-none">
                <div className={`${styles.blob} ${styles.blue}`}></div>
                <div className={`${styles.blob} ${styles.indigo}`}></div>
                <div className={`${styles.blob} ${styles.rose}`}></div>
                <div className={`${styles.blob} ${styles.teal}`}></div>
                <div className={`${styles.blob} ${styles.purple}`}></div>
                <div className={`${styles.blob} ${styles.cyan}`}></div>
            </div>

            {/* --- HERO SECTION --- */}
            <section className="relative z-10 min-h-screen flex items-center justify-center text-center px-4 md:px-6 font-inter pt-20 md:pt-0">
                <motion.main
                    initial={{ opacity: 0, y: 20 }}
                    animate={isLoaded ? { opacity: 1, y: 0 } : {}}
                    transition={{ duration: 1.5 }}
                    className="flex flex-col items-center"
                >
                    <h1 className="text-5xl sm:text-7xl md:text-8xl font-black tracking-tighter text-white leading-none font-inter break-words max-w-full">
                        YOUni<span className="text-younify-blue drop-shadow-[0_0_20px_rgba(29,78,216,0.4)] font-inter">Fy</span>
                    </h1>

                    <div className="mt-6 md:mt-8 text-slate-400 tracking-[0.2em] md:tracking-[0.3em] text-lg md:text-xl font-bold flex flex-wrap justify-center items-baseline gap-2 md:gap-4 font-sans lowercase leading-none">
                        <span>uniamo</span>
                        <RotatingText words={['studenti', 'università']} interval={3500} />
                    </div>
                </motion.main>
            </section>

            {/* --- TYPEWRITER SECTION --- */}
            <section className="relative z-10 max-w-7xl mx-auto px-4 md:px-8 py-20 md:py-32 flex flex-col gap-10 md:gap-6 font-sans">
                <div className="max-w-6xl font-inter min-h-[5rem] md:h-20">
                    <TextType
                        text={[
                            "Laddove oggi esistono spazi confusi e insicuri...",
                            "...noi nasciamo per eliminarne il rumore."
                        ]}
                        className="text-2xl sm:text-3xl md:text-4xl font-black text-white leading-[1.2] tracking-tight font-inter"
                        typingSpeed={60}
                        deletingSpeed={30}
                        pauseDuration={2000}
                        loop={true}
                        startOnVisible={true}
                        cursorClassName="text-younify-blue ml-1"
                        cursorCharacter="|"
                    />
                </div>
                <div className="ml-auto max-w-4xl text-left md:text-right mt-4 md:mt-0">
                    <TextType
                        text="Con lo scopo di costruire una community autentica, basata su connessioni personali reali, stiamo ridefinendo il modo in cui gli studenti vivono la propria università."
                        className="text-lg md:text-xl font-medium text-white leading-relaxed font-sans"
                        typingSpeed={20}
                        startOnVisible={true}
                        loop={false}
                        showCursor={false}
                        initialDelay={8700}
                    />
                </div>
            </section>

            {/* --- SECTION 1: EVENTS --- */}
            <section id="events" className="relative z-10 w-full py-20 md:py-32 px-4 md:px-8">
                <div className="max-w-6xl mx-auto flex flex-col items-center gap-10 md:gap-16 font-inter text-center">

                    <h2 className="text-2xl md:text-4xl font-black text-white font-inter px-2">
                        Un nuovo modo di <br className="md:hidden"/>
                        <span className="text-younify-blue font-inter"> vivere</span> e
                        <span className="text-younify-blue font-inter"> promuovere</span> gli
                        <span className="text-younify-blue font-inter"> eventi</span>
                    </h2>

                    <div className="relative p-1 bg-slate-900/50 backdrop-blur-md border border-white/10 rounded-full flex items-center w-full max-w-[320px] font-sans shadow-xl">
                        <motion.div
                            className="absolute bg-younify-blue rounded-full z-0 inset-y-1 left-1"
                            style={{ width: 'calc(50% - 4px)' }}
                            animate={{ x: activeTab === 'studenti' ? '0%' : '100%' }}
                            transition={{ type: "spring", stiffness: 300, damping: 30 }}
                        />
                        <button onClick={() => setActiveTab('studenti')} className={`relative z-10 flex-1 py-2 text-xs font-bold uppercase tracking-widest ${activeTab === 'studenti' ? 'text-white' : 'text-slate-300'}`}>Studenti</button>
                        <button onClick={() => setActiveTab('host')} className={`relative z-10 flex-1 py-2 text-xs font-bold uppercase tracking-widest ${activeTab === 'host' ? 'text-white' : 'text-slate-300'}`}>Host</button>
                    </div>

                    <div className="w-full max-w-4xl font-sans text-left">
                        <AnimatePresence mode="wait">
                            <motion.div key={activeTab} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} className="flex flex-col gap-4 md:gap-5">
                                {EVENT_CONTENT[activeTab].map((item, index) => (
                                    <div key={index} className="flex flex-col sm:flex-row items-start sm:items-center gap-4 sm:gap-6 p-5 md:p-6 bg-slate-900/40 backdrop-blur-md border border-white/10 rounded-3xl group hover:bg-slate-900/60 transition-all">
                                        <div className="p-3 bg-younify-blue/10 rounded-2xl text-younify-blue shrink-0">{item.icon}</div>
                                        <p className="text-lg md:text-xl font-medium text-slate-300 group-hover:text-white font-sans leading-snug">{item.text}</p>
                                    </div>
                                ))}
                            </motion.div>
                        </AnimatePresence>
                    </div>
                </div>
            </section>

            {/* --- SECTION 2: MATCH --- */}
            <section id="match" className="relative z-10 w-full max-w-7xl mx-auto px-4 md:px-8 pt-20 md:pt-40 pb-20">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-10 items-center">

                    {/* Colonna Testo (ORDER 1) */}
                    <div className="lg:col-span-2 space-y-8 md:space-y-12 z-20 order-1">
                        <h2 className="text-2xl md:text-4xl font-black text-white leading-tight font-inter tracking-tight">
                            Sei un fuorisede o uno studente in cerca di <span className="text-younify-blue font-inter">nuovi compagni d'avventura</span> intraprendenti come te?
                        </h2>
                        <div className="flex flex-col gap-6 md:gap-10 font-sans">
                            <div className="flex items-start md:items-center gap-4 md:gap-6 group">
                                <div className="p-3 bg-rose-500/10 rounded-xl border border-rose-500/20 shadow-lg shrink-0"><Heart className="text-rose-500" size={24} /></div>
                                <p className="text-lg md:text-xl font-medium text-slate-300 group-hover:text-white leading-relaxed font-sans">
                                    Effettua matching con studenti con interessi simili
                                </p>
                            </div>
                            <div className="flex items-start md:items-center gap-4 md:gap-6 group">
                                <div className="p-3 bg-teal-500/10 rounded-xl border border-teal-500/20 shadow-lg shrink-0"><Share2 className="text-teal-500" size={24} /></div>
                                <p className="text-lg md:text-xl font-medium text-slate-300 group-hover:text-white leading-relaxed font-sans">
                                    Scambia informazioni di contatto social per creare e rafforzare interazioni autentiche
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Colonna MetaBalls (ORDER 2) */}
                    <div className="w-full lg:w-[140%] lg:-mr-32 h-[400px] md:h-[600px] flex items-center justify-center relative z-10 order-2 overflow-hidden lg:overflow-visible">
                        <div className="absolute inset-0 w-full h-full scale-100 md:scale-110">
                            <MetaBalls
                                color="#1d4ed8"
                                cursorBallColor="#1d4ed8"
                                cursorBallSize={1.5}
                                enableTransparency={true}
                                animationSize={30}
                                ballCount={10}
                                speed={0.5}
                            />
                        </div>
                    </div>
                </div>
            </section>

            {/* --- CAROSELLI --- */}
            <section className="relative z-20 w-full pt-16 md:pt-32 flex flex-col gap-4 md:gap-6 overflow-hidden">
                <LogoLoop
                    logos={UNIVERSITY_TAGS_1}
                    direction="left"
                    speed={60}
                    gap={10}
                    pauseOnHover={true}
                    renderItem={(item) => (
                        <div className="px-4 py-2 md:px-5 md:py-3 rounded-full border border-white/10 bg-white/5 backdrop-blur-md text-slate-300 font-medium font-sans text-sm md:text-lg whitespace-nowrap hover:bg-white/10 hover:border-younify-blue/50 hover:text-white transition-colors cursor-default">
                            {item.title}
                        </div>
                    )}
                />
                <LogoLoop
                    logos={UNIVERSITY_TAGS_2}
                    direction="right"
                    speed={50}
                    gap={10}
                    pauseOnHover={true}
                    renderItem={(item) => (
                        <div className="px-4 py-2 md:px-5 md:py-3 rounded-full border border-white/10 bg-white/5 backdrop-blur-md text-slate-300 font-medium font-sans text-sm md:text-lg whitespace-nowrap hover:bg-white/10 hover:border-younify-blue/50 hover:text-white transition-colors cursor-default">
                            {item.title}
                        </div>
                    )}
                />
            </section>

            {/* --- SECTION 3: THREAD --- */}
            <section id="thread" className="relative z-10 w-full max-w-7xl mx-auto px-4 md:px-8 py-16 md:py-24">
                <div className="flex flex-col items-center text-center mb-10 md:mb-16">
                    <h2 className="text-2xl md:text-4xl font-black text-white tracking-tight font-inter leading-tight max-w-5xl">
                        Uno spazio di confronto e condivisione di idee,
                        <span className="text-younify-blue block mt-2">sano e rispettoso.</span>
                    </h2>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 md:gap-8">
                    {[
                        { icon: <MessageSquarePlus size={32} />, color: "indigo", text: "Pubblica domande, idee e curiosità" },
                        { icon: <Hash size={32} />, color: "rose", text: "Partecipa a discussioni su topic mirati" },
                        { icon: <HeartHandshake size={32} />, color: "teal", text: "Scambia consigli e informazioni preziose con un intera community studentesca" }
                    ].map((feature, idx) => (
                        <div key={idx} className="p-6 md:p-8 rounded-[2rem] bg-white/5 border border-white/10 hover:bg-white/10 hover:border-younify-blue/30 transition-all duration-300 group">
                            <div className={`w-14 h-14 bg-${feature.color}-500/20 rounded-2xl flex items-center justify-center mb-6 text-${feature.color}-400 group-hover:scale-110 transition-transform`}>
                                {feature.icon}
                            </div>
                            <p className="text-lg md:text-xl font-medium text-slate-300 group-hover:text-white font-sans leading-relaxed">
                                {feature.text}
                            </p>
                        </div>
                    ))}
                </div>
            </section>

            {/* Carosello Inferiore */}
            <section className="relative z-20 w-full pb-10 md:pb-20 flex flex-col gap-4 md:gap-6 overflow-hidden">
                <LogoLoop
                    logos={UNIVERSITY_TAGS_2}
                    direction="left"
                    speed={60}
                    gap={10}
                    pauseOnHover={true}
                    renderItem={(item) => (
                        <div className="px-4 py-2 md:px-5 md:py-3 rounded-full border border-white/10 bg-white/5 backdrop-blur-md text-slate-300 font-medium font-sans text-sm md:text-lg whitespace-nowrap hover:bg-white/10 hover:border-younify-blue/50 hover:text-white transition-colors cursor-default">
                            {item.title}
                        </div>
                    )}
                />
                <LogoLoop
                    logos={UNIVERSITY_TAGS_1}
                    direction="right"
                    speed={50}
                    gap={10}
                    pauseOnHover={true}
                    renderItem={(item) => (
                        <div className="px-4 py-2 md:px-5 md:py-3 rounded-full border border-white/10 bg-white/5 backdrop-blur-md text-slate-300 font-medium font-sans text-sm md:text-lg whitespace-nowrap hover:bg-white/10 hover:border-younify-blue/50 hover:text-white transition-colors cursor-default">
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