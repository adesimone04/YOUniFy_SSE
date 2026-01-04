import React from 'react';
import styles from './InstallAppGuide.module.css';
// Importa la tua nuova immagine
import combinedMockup from '../../assets/images/iphone-ipad-mockup.png';

const InstallAppGuide = () => {
    return (
        <section className={styles.guideContainer}>

            <div className={styles.contentWrapper}>
                <div className={styles.header}>
                    <h2 className="text-3xl md:text-4xl font-black text-white font-inter">
                        Stanco di doverci sempre <span className="text-younify-blue font-inter">cercare sul browser </span>?
                    </h2>

                    <div className={styles.subtitle}>
                        <p className="text-xl md:text-xl font-medium text-slate-300 group-hover:text-white leading-relaxed font-sans">
                            Puoi aggiungerci alla schermata Home del tuo dispositivo!
                        </p>
                    </div>
                </div>

                <div className={styles.visualGuide}>

                    {/* Step 1: L'Icona (Resta 'stepCard' normale) */}
                    <div className={styles.step}>
                        <div className={styles.stepCard}>
                            <div className={styles.iconContainer}>
                                <div className={styles.appIcon}>
                                    <svg width="80" height="80" viewBox="0 0 80 80" fill="none">
                                        <rect width="80" height="80" rx="18" fill="#000000"/>
                                        <text x="50%" y="50%" dominantBaseline="middle" textAnchor="middle" fontSize="14">
                                            <tspan fill="white">YOUni</tspan>
                                            <tspan fill="#3b82f6">Fy</tspan>
                                        </text>
                                    </svg>
                                </div>
                                <div className={styles.plusSymbol}>+</div>
                            </div>
                            <div className={styles.stepNumber}>1</div>
                            <p className={styles.stepDescription}>Aggiungendoci alla tua Home</p>
                        </div>
                    </div>

                    {/* Step 2: Immagine Grande (Usa 'stepCardLarge') */}
                    <div className={styles.step}>
                        {/* CAMBIATO QUI: stepCardLarge invece di stepCard */}
                        <div className={styles.stepCardLarge}>

                            <div className={styles.imageWrapper}>
                                <img
                                    src={combinedMockup}
                                    alt="Anteprima App su dispositivi"
                                    className={styles.mockupImage}
                                />
                            </div>

                            <div className={styles.stepNumber}>2</div>
                            <p className={styles.stepDescription}>YOUniFy sarà pronta per essere raggiunta comodamente.</p>
                        </div>
                    </div>

                </div>
            </div>
        </section>
    );
};

export default InstallAppGuide;