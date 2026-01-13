import axiosClient from './axiosClient';

const authService = {
    // Login
    login: (credentials) => {
        return axiosClient.post('/auth/login', credentials);
    },

    // Registrazioni
    registerStudent: (data) => {
        return axiosClient.post('/auth/user/signup', data);
    },

    registerHost: (data) => {
        return axiosClient.post('/auth/host/signup', data);
    },

    // 2FA - Verifica
    verify2FA: (accountId, codeInserted) => {
        return axiosClient.post('/auth/2FA', { ID: accountId, codeInserted: codeInserted });
    },

    // 2FA - Resend (Richiesta specifica)
    // Il backend si aspetta l'email per sapere a chi rinviare
    resend2FACode: (accountId) => {
        return axiosClient.post('/auth/resend/2FA', { ID: accountId });
    },

    logout: () => {
        return axiosClient.post('/auth/logout');
    }
};

export default authService;