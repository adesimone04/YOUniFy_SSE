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

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve();
        }
    });
    failedQueue = [];
};

// --- INTERCEPTOR REQUEST ---
axiosClient.interceptors.request.use(
    (config) => {
        const xsrfToken = document.cookie
            .split('; ')
            .find(row => row.startsWith('XSRF-TOKEN='))
            ?.split('=')[1];

        if (xsrfToken) {
            config.headers['X-XSRF-TOKEN'] = xsrfToken;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

// --- INTERCEPTOR RESPONSE ---
axiosClient.interceptors.response.use(
    (response) => {
        const internalCode = response.data?.internalCode;

        return response;
    },
    async (error) => {
        const originalRequest = error.config;
        const errorData = error.response?.data;
        const internalCode = errorData?.internalCode;
        const status = error.response?.status;

        if (originalRequest.url?.includes('/api/auth/refresh')) {
            console.error('Refresh fallito:', errorData?.message || 'Token non valido');
            return Promise.reject(error);
        }

        if (internalCode === 'TOKEN_REFRESHED') {

            if (isRefreshing) {
                processQueue(null);
                isRefreshing = false;
            }

            return axiosClient(originalRequest);
        }

        const sessionErrors = [
            'WRONG_REFRESH_TOKEN',
            'TOKEN_NOT_FOUND',
            'TOKEN_REVOKED',
            'TOKEN_EXPIRED',
            'USER_NOT_ABILITATED_FOR_TOKEN'
        ];

        if (sessionErrors.includes(internalCode)) {
            console.error('Sessione scaduta, redirect al login');

            localStorage.clear();

            if (isRefreshing) {
                processQueue(error);
                isRefreshing = false;
            }

            window.location.href = '/login';
            return Promise.reject(error);
        }

        if (status === 403 && !originalRequest._retry) {

            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                })
                    .then(() => axiosClient(originalRequest))
                    .catch(err => Promise.reject(err));
            }

            originalRequest._retry = true;
            isRefreshing = true;


            try {
                await axiosClient.post('auth/refresh');

                const response = await axiosClient(originalRequest);

                processQueue(null);
                isRefreshing = false;

                return response;

            }
            catch (refreshError) {

                processQueue(refreshError);
                isRefreshing = false;

                const refreshInternalCode = refreshError.response?.data?.internalCode;

                if (sessionErrors.includes(refreshInternalCode)) {
                    console.error('Refresh fallito, sessione scaduta');
                    localStorage.clear();
                    window.location.href = '/login';
                }
                else {

                    console.error('Accesso negato:', originalRequest.url);
                }

                return Promise.reject(refreshError);
            }
        }

        if (status === 401 && sessionErrors.includes(internalCode)) {
            console.error('401 - Redirect al login');
            localStorage.clear();
            window.location.href = '/login';
            return Promise.reject(error);
        }

        return Promise.reject(error);
    }
);

export default axiosClient;