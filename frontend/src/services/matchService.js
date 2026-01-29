import axiosClient from "./axiosClient.js"
import { handleMatchResponse } from '../utils/matchHandler.js';
import { toast } from 'react-toastify';

class MatchService {

    async getMatchCards(page = 0, size = 10) {

        try {

            const response = await axiosClient.get('/match', {
                params: { page, size }
            });
            handleMatchResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async getMatchCardsByUni(university, page = 0, size = 10) {

        try {

            const response = await axiosClient.get(`/match/${encodeURIComponent(university)}`, {
                params: { page, size }
            });
            handleMatchResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async getMatchCardsByUniAndCdl(university, cdl, page = 0, size = 10) {

        try {

            const response = await axiosClient.get(`/match/${encodeURIComponent(university)}/${encodeURIComponent(cdl)}`, {
                params: { page, size }
            });
            handleMatchResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async getSentPendingMatches(page = 0, size = 10) {

        try {
            const response = await axiosClient.get('/match/sent', {
                params: { page, size }
            });
            handleMatchResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async getReceivedPendingMatches(page = 0, size = 10) {

        try {

            const response = await axiosClient.get('/match/received', {
                params: { page, size }
            });
            handleMatchResponse(response.data);
            return response.data;

        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async getAcceptedMatches(page = 0, size = 10) {

        try {

            const response = await axiosClient.get('/match/accepted', {
                params: { page, size }
            });
            handleMatchResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async getTotalMatched() {

        try {

            const response = await axiosClient.get('/match/totalMatched');
            handleMatchResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }


    async sendMatch(receiverID) {

        try {

            const response = await axiosClient.post('/match/send', {
                receiverID: receiverID
            });
            handleMatchResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async reactToMatch(matchId, status) {

        try {

            const response = await axiosClient.post('/match/react', {
                matchId: matchId,
                status: status
            });
            handleMatchResponse(response.data);
            return response.data;

        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async deleteMatch(matchId) {

        try {

            const response = await axiosClient.delete(`/match/notAnymore/${matchId}`);
            handleMatchResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }


    handleError(error) {

        if (error.response) {

            const responseData = error.response.data;
            const status = error.response.status;

            if (responseData?.internalCode) {

                handleMatchResponse(responseData);
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

export default new MatchService();
