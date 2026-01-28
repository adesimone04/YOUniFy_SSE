// services/eventService.js
import axiosClient from "./axiosClient.js"
import { handleAuthResponse } from '../utils/eventHandler.js';
import { toast } from 'react-toastify';

class EventService {

    // GET /api/event - Ottieni tutti gli eventi paginati
    async getAllEvents(page = 0, size = 10) {
        try {
            const response = await axiosClient.get('/event', {
                params: { page, size }
            });
            handleAuthResponse(response.data);
            return response.data; // ApiResponse con  Page<EventResponse>
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // GET /api/event/city/{city} - Filtra eventi per città
    async getEventsByCity(city, page = 0, size = 10) {
        try {
            const response = await axiosClient.get(`/event/city/${encodeURIComponent(city)}`, {
                params: { page, size }
            });
            handleAuthResponse(response.data);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // GET /api/event/utility/cities - Ottieni lista città
    async getCities() {
        try {
            const response = await axiosClient.get('/event/utility/cities');
            handleAuthResponse(response.data);
            return response.data; // ApiResponse con  List<Map<String, String>>
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // GET /api/event/myEvents - Eventi dell'host autenticato
    async getMyEvents(page = 0, size = 10) {
        try {
            const response = await axiosClient.get('/event/myEvents', {
                params: { page, size }
            });
            handleAuthResponse(response.data);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // GET /api/event/joined - Eventi a cui l'utente partecipa
    async getJoinedEvents(page = 0, size = 10) {
        try {
            const response = await axiosClient.get('/event/joined', {
                params: { page, size }
            });
            handleAuthResponse(response.data);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // POST /api/event/post - Crea nuovo evento (HOST only)
    async createEvent(eventData) {
        try {
            const formData = new FormData();
            formData.append('name', eventData.name);
            formData.append('description', eventData.description);

            // Converti la data in formato ISO Instant per Java
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
            handleAuthResponse(response.data);
            return response.data; // ApiResponse con message: "POSTED_SUCCESSFULLY"
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // POST /api/event/edit/{eventId} - Modifica evento esistente (HOST only)
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

            // poster è opzionale per edit
            if (eventData.poster) {
                formData.append('poster', eventData.poster);
            }

            const response = await axiosClient.post(`/event/edit/${eventId}`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            handleAuthResponse(response.data);
            return response.data; // ApiResponse con message: "EDITED_SUCCESSFULLY"
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // DELETE /api/event/delete/{eventId} - Elimina evento (HOST/ADMIN only)
    async deleteEvent(eventId) {
        try {
            const response = await axiosClient.delete(`/event/delete/${eventId}`);
            handleAuthResponse(response.data);
            return response.data; // ApiResponse con message: "DELETED_SUCCESSFULLY"
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // POST /api/event/join/{eventId} - Partecipa a un evento (USER only)
    async joinEvent(eventId) {
        try {
            const response = await axiosClient.post(`/event/join/${eventId}`);
            handleAuthResponse(response.data);
            return response.data; // ApiResponse con message: "JOINED_SUCCESSFULLY"
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // POST /api/event/unjoin/{eventId} - Annulla partecipazione (USER only)
    async unjoinEvent(eventId) {
        try {
            const response = await axiosClient.post(`/event/unjoin/${eventId}`);
            handleAuthResponse(response.data);
            return response.data; // ApiResponse con message: "UNJOINED_SUCCESSFULLY"
        } catch (error) {
            throw this.handleError(error);
        }
    }

    // Gestione errori centralizzata


    handleError(error) {
        console.error('EventService Error:', error);
        console.error('📋 Response completa:', error.response);
        console.error('📋 Response ', error.response?.data);
        console.error('📋 Response status:', error.response?.status);

        if (error.response) {
            const responseData = error.response.data;
            const status = error.response.status;

            if (responseData?.internalCode) {
                handleAuthResponse(responseData);
                return error;
            }

            if (status === 403) {
                toast.error('Non hai i permessi per questa operazione.');
            } else if (status === 404) {
                toast.error('Risorsa non trovata.');
            } else{
                const message = responseData?.message || 'Errore del server';
                toast.error(message);
            }
        } else if (error.request) {
            toast.error('Impossibile connettersi al server');
        } else {
            toast.error('Errore nella richiesta: ' + error.message);
        }
        return error;
    }
}

export default new EventService();
