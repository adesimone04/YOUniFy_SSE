import axiosClient from './axiosClient';

export const threadService = {
    // ==================== THREAD ====================

    // Tutti i thread (nessun filtro)
    getThreadsByDate: (page, size) => {
        return axiosClient.get(`/thread?page=${page}&size=${size}`);
    },

    // Filtro solo per FLAG/Topic
    getThreadsByFlag: (flag, page, size) => {
        return axiosClient.get(`/thread/flag/${flag}?page=${page}&size=${size}`);
    },

    // Filtro solo per Università
    getThreadsByUniversity: (university, page, size) => {
        return axiosClient.get(`/thread/uni/${encodeURIComponent(university)}?page=${page}&size=${size}`);
    },

    // Filtro COMBINATO (università + flag)
    getThreadsByUniversityAndFlag: (university, flag, page, size) => {
        return axiosClient.get(`/thread/${encodeURIComponent(university)}/${flag}?page=${page}&size=${size}`);
    },

    // Crea thread
    createThread: (payload) => {
        return axiosClient.post('/thread/post', payload);
    },

    // Vota thread
    voteThread: (threadId, voteType) => {
        return axiosClient.post('/thread/react', {
            threadId,
            voteType
        });
    },

    // Elimina thread
    deleteThread: (threadId) => {
        return axiosClient.delete(`/thread/delete/${threadId}`);
    },

    // ==================== COMMENTI ====================

// Ottieni commenti di un thread (paginati)
    getComments: (threadId, page = 0, size = 10) => {
        return axiosClient.get(`/thread/comment/${threadId}?page=${page}&size=${size}`);
    },

// Crea un commento
    postComment: (threadId, content) => {
        return axiosClient.post('/thread/comment/post', {
            threadId,
            content
        });
    },

// Vota un commento
    voteComment: (commentId, voteType) => {
        return axiosClient.post('/thread/comment/react', {
            commentId,
            voteType
        });
    },

// Elimina un commento
    deleteComment: (commentId) => {
        return axiosClient.delete(`/thread/comment/delete/${commentId}`);
    },


    // ==================== UTILITY ====================

    // Ottieni lista flags
    getFlags: () => {
        return axiosClient.get('/thread/utility/flag');
    },

    // Ottieni thread postati dall'utente
    getMyThreads: (page = 0, size = 10) => {
        return axiosClient.get(`/thread/summary/posted?page=${page}&size=${size}`);
    },

    // Ottieni totale CFU utente
    getMyCFU: () => {
        return axiosClient.get('/thread/summary/cfu');
    }
};
