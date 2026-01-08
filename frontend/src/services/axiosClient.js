import axios from 'axios';

// in prod use (eg. https://api.younify.it)
const BASE_URL = import.meta.env.VITE_API_URL || 'https://localhost:8443/api';

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

export default axiosClient;