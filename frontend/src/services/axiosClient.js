import axios from 'axios';

// URL base (modifica se necessario per prod)
const BASE_URL = import.meta.env.VITE_API_URL || 'https://localhost:8443/api';

const axiosClient = axios.create({
    baseURL: BASE_URL,
    withCredentials: true, // FONDAMENTALE: Salva/Invia i cookie JWT automaticamente
    headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
    },
    xsrfCookieName: 'XSRF-TOKEN',
    xsrfHeaderName: 'X-XSRF-TOKEN',
});

// --- INTERCEPTOR RISPOSTE ---
axiosClient.interceptors.response.use(
    (response) => {
        // Se il backend risponde 200 ma con codice TOKEN_REFRESHED nel corpo
        // (dipende da come il tuo backend manda questo codice, se nel body o via status error)
        const internalCode = response.data?.internalCode;

        if (internalCode === 'TOKEN_REFRESHED') {
            console.log("Token refreshato (Body 200), tutto apposto.");
            // Continua normalmente
        }
        return response;
    },
    async (error) => {
        const originalRequest = error.config;
        const errorData = error.response?.data;
        const internalCode = errorData?.internalCode;

        console.log("Interceptor Error:", internalCode);

        // 1. GESTIONE REFRESH TOKEN RIUSCITO (se arriva come errore 4xx)
        if (internalCode === 'TOKEN_REFRESHED') {
            console.log("Token refreshato, ripeto chiamata...");
            // Ripete la chiamata originale
            return axiosClient(originalRequest);
        }

        // 2. GESTIONE ERRORI SESSIONE -> LOGIN
        // "non è riuscito a refreshare e vai su login"
        const sessionErrors = [
            'WRONG_REFRESH_TOKEN',
            'TOKEN_NOT_FOUND',
            'TOKEN_REVOKED',
            'TOKEN_EXPIRED',
            'USER_NOT_ABILITATED_FOR_TOKEN' // "rimandalo sulla landing oppure di nuovo su login"
        ];

        if (sessionErrors.includes(internalCode) || error.response?.status === 401) {
            console.warn(`Sessione scaduta (${internalCode}). Redirect al Login.`);
            window.location.href = '/login';
            return Promise.reject(error);
        }

        return Promise.reject(error);
    }
);

export default axiosClient;