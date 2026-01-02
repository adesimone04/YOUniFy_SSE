import React from 'react';
import styles from './Footer.module.css';

const Footer = () => {
    return (
        <footer className={styles.footer}>
            <div className={styles.footerContainer}>
                {/* Section 1: Brand */}
                <div className={styles.footerBrand}>
                        <div className={styles.footerLogoText}>
                            YOUni<span className={styles.highlight}>Fy</span>
                        </div>
                    <div className={styles.footerTagline}>
                        uniamo studenti. uniamo università.
                    </div>
                </div>

                <div className={styles.footerNav}>
                    <div className={styles.footerNavLinks}>
                        <a href="#">ABOUT US</a>
                    </div>

                    <div className={styles.footerDivider}></div>

                    <div className={styles.footerNavLinks}>
                        <a href="#events">EVENTS</a>
                        <a href="#match">MATCH</a>
                        <a href="#thread">THREAD</a>
                    </div>

                    <div className={styles.footerDivider}></div>

                    <div className={styles.footerNavLinks}>
                        <a href="#instagram">INSTAGRAM</a>
                    </div>

                    <div className={styles.footerDivider}></div>

                    <div className={styles.footerNavLinks}>
                        <div className={styles.contactSection}>
                            <span className={styles.contactLabel}>FOR HELP, PLEASE CONTACT US BY:</span>
                            <a href="mailto:younifytester@gmail.com">younifytester@gmail.com</a>
                        </div>
                    </div>

                </div>
            </div>
        </footer>
    );
};

export default Footer;