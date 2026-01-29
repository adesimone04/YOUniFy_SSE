import axiosClient from './axiosClient';

const authService = {

    login: (credentials) => {
        return axiosClient.post('/auth/login', credentials);
    },

    registerStudent: (data) => {
        return axiosClient.post('/auth/user/signup', data);
    },

    registerHost: (data) => {
        return axiosClient.post('/auth/host/signup', data);
    },

    verify2FA: (accountId, codeInserted) => {
        return axiosClient.post('/auth/2FA', { ID: accountId, codeInserted: codeInserted });
    },

    resend2FACode: (accountId) => {
        return axiosClient.post('/auth/resend/2FA', { ID: accountId });
    },

    logout: () => {
        return axiosClient.post('/auth/logout');
    }
};

export default authService;