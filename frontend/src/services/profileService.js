import axiosClient from "./axiosClient.js"
import { handleProfileResponse } from '../utils/profileHandler.js';
import { toast } from 'react-toastify';

class ProfileService {

    async getUserProfile() {
        try {
            const response = await axiosClient.get('/profile/user');
            handleProfileResponse(response.data);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    async getHostProfile() {
        try {
            const response = await axiosClient.get('/profile/host');
            handleProfileResponse(response.data);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    async editUserProfile(editUserInfoRequest) {
        try {
            const response = await axiosClient.put('/profile/user/edit', editUserInfoRequest);
            handleProfileResponse(response.data);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    async editHostProfile(editHostInfoRequest) {
        try {
            const response = await axiosClient.put('/profile/host/edit', editHostInfoRequest);

            if (response.status === 200 || response.status === 204) {
                toast.success('Profilo aggiornato con successo!');
                return { success: true };
            }

            handleProfileResponse(response.data);
            return response.data;

        } catch (error) {

            if (error.response?.status === 200) {
                toast.success('Profilo aggiornato con successo!');
                return { success: true };
            }
            throw this.handleError(error);
        }
    }

    async changeUniversity(editUniversityRequest, navigate) {
        try {
            const response = await axiosClient.put('/profile/change/university', editUniversityRequest);
            handleProfileResponse(response.data, navigate);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    async confirm2FAUniversity(accountId, code, userEmail, university, navigate) {
        try {
            const response = await axiosClient.post('/profile/2FA', {ID: accountId, codeInserted: code, email: userEmail, university: university});
            handleProfileResponse(response.data, navigate);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    async changePassword(changePassRequest) {
        try {
            const response = await axiosClient.put('/profile/change/password', changePassRequest);
            handleProfileResponse(response.data);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    handleError(error) {

        if (error.response) {
            const responseData = error.response.data;
            const status = error.response.status;

            if (responseData?.internalCode) {
                handleProfileResponse(responseData);
                return error;
            }

            if (status === 403) {
                toast.error('Non hai i permessi per questa operazione.');
            }
            else if (status === 404) {
                toast.error('Risorsa non trovata.');
            }
            else {
                const message = responseData?.message || 'Errore del server';
                toast.error(message);
            }
        }
        else if (error.request) {
            toast.error('Impossibile connettersi al server');
        }
        else {
            toast.error('Errore nella richiesta: ' + error.message);
        }

        return error;
    }
}

export default new ProfileService();