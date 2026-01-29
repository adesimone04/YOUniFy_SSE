import axiosClient from "./axiosClient.js"
import { handleEventResponse } from '../utils/eventHandler.js';
import { toast } from 'react-toastify';

class EventService {

    async getAllEvents(page = 0, size = 10) {

        try {
            const response = await axiosClient.get('/event', {
                params: { page, size }
            });
            handleEventResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async getEventsByCity(city, page = 0, size = 10) {

        try {
            const response = await axiosClient.get(`/event/city/${encodeURIComponent(city)}`, {
                params: { page, size }
            });
            handleEventResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async getCities() {

        try {

            const response = await axiosClient.get('/event/utility/cities');
            handleEventResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async getMyEvents(page = 0, size = 10) {

        try {
            const response = await axiosClient.get('/event/myEvents', {
                params: { page, size }
            });
            handleEventResponse(response.data);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    async getJoinedEvents(page = 0, size = 10) {

        try {
            const response = await axiosClient.get('/event/joined', {
                params: { page, size }
            });
            handleEventResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async createEvent(eventData) {

        try {

            const formData = new FormData();
            formData.append('name', eventData.name);
            formData.append('description', eventData.description);

            const dateInstant = new Date(eventData.dateAndTime).toISOString();
            formData.append('dateAndTime', dateInstant);

            formData.append('street', eventData.street);
            formData.append('city', eventData.city);
            formData.append('CAP', eventData.CAP);

            if (eventData.poster) {
                formData.append('poster', eventData.poster);
            }

            const response = await axiosClient.post('/event/post', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            handleEventResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async editEvent(eventId, eventData) {

        try {

            const formData = new FormData();
            formData.append('name', eventData.name);
            formData.append('description', eventData.description);

            const dateInstant = new Date(eventData.dateAndTime).toISOString();
            formData.append('dateAndTime', dateInstant);

            formData.append('street', eventData.street);
            formData.append('city', eventData.city);
            formData.append('CAP', eventData.CAP);

            if (eventData.poster) {
                formData.append('poster', eventData.poster);
            }

            const response = await axiosClient.post(`/event/edit/${eventId}`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            handleEventResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async deleteEvent(eventId) {

        try {
            const response = await axiosClient.delete(`/event/delete/${eventId}`);
            handleEventResponse(response.data);
            return response.data;
        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async joinEvent(eventId) {

        try {
            const response = await axiosClient.post(`/event/join/${eventId}`);
            handleEventResponse(response.data);
            return response.data;

        }
        catch (error) {

            throw this.handleError(error);
        }
    }

    async unjoinEvent(eventId) {

        try {

            const response = await axiosClient.post(`/event/unjoin/${eventId}`);
            handleEventResponse(response.data);
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

                handleEventResponse(responseData);
                return error;
            }

            if (status === 403) {

                toast.error('Non hai i permessi per questa operazione.');
            }
            else if (status === 404) {

                toast.error('Risorsa non trovata.');
            }
            else{

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

export default new EventService();
