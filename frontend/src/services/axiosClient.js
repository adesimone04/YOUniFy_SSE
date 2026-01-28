import axios from 'axios';

// modify in prod in .env files
const BASE_URL = import.meta.env.VITE_API_URL;

const axiosClient = axios.create({
    baseURL: BASE_URL,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
    },
    xsrfCookieName: 'XSRF-TOKEN',
    xsrfHeaderName: 'X-XSRF-TOKEN',
});

/// --- HELPER: Legge un cookie specifico ---
const getCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
};

axiosClient.interceptors.request.use(async (config) => { // Nota: async qui

    // Funzione che aspetta
    const sleep = ms => new Promise(r => setTimeout(r, ms));

    let xsrfToken = getCookie('XSRF-TOKEN');

    // SE NON LO TROVIAMO SUBITO, ASPETTIAMO UN ATTIMO E RIPROVIAMO
    if (!xsrfToken) {
        await sleep(500);
        xsrfToken = getCookie('XSRF-TOKEN');
    }

    if (xsrfToken) {
        if (config.method !== 'get' && config.method !== 'head') {
            config.headers['X-XSRF-TOKEN'] = xsrfToken;
        }
    } else {
        console.log('❌ Niente da fare, il cookie è invisibile.');
        console.log('Path attuale:', window.location.pathname);
        console.log('Cookie visibili:', document.cookie);
    }

    return config;
});

// --- INTERCEPTOR RISPOSTE ---
axiosClient.interceptors.response.use(

    (response) => {
        // Se il backend risponde 200 ma con codice TOKEN_REFRESHED nel corpo
        // (dipende da come il tuo backend manda questo codice, se nel body o via status error)
        const internalCode = response.data?.internalCode;
        return response;
    },
    async (error) => {

        const originalRequest = error.config;
        const errorData = error.response?.data;
        const internalCode = errorData?.internalCode;

        // 1. GESTIONE REFRESH TOKEN RIUSCITO (se arriva come errore 4xx)
        if (internalCode === 'TOKEN_REFRESHED') {

            return axiosClient(originalRequest);
        }

        // 2. GESTIONE ERRORI SESSIONE -> LOGIN
        const sessionErrors = [
            'WRONG_REFRESH_TOKEN',
            'TOKEN_NOT_FOUND',
            'TOKEN_REVOKED',
            'TOKEN_EXPIRED',
            'USER_NOT_ABILITATED_FOR_TOKEN'
        ];

        if (sessionErrors.includes(internalCode) && error.response?.status === 401) {

            window.location.href = '/login';
            return Promise.reject(error);
        }

        return Promise.reject(error);
    }
);

export default axiosClient;