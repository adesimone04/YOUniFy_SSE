import axiosClient from './axiosClient';

export const threadService = {

    getThreadsByDate: (page, size) => {
        return axiosClient.get(`/thread?page=${page}&size=${size}`);
    },

    getThreadsByFlag: (flag, page, size) => {
        return axiosClient.get(`/thread/flag/${flag}?page=${page}&size=${size}`);
    },

    getThreadsByUniversity: (university, page, size) => {
        return axiosClient.get(`/thread/uni/${encodeURIComponent(university)}?page=${page}&size=${size}`);
    },

    getThreadsByUniversityAndFlag: (university, flag, page, size) => {
        return axiosClient.get(`/thread/${encodeURIComponent(university)}/${flag}?page=${page}&size=${size}`);
    },

    createThread: (payload) => {
        return axiosClient.post('/thread/post', payload);
    },

    voteThread: (threadId, voteType) => {
        return axiosClient.post('/thread/react', {
            threadId,
            voteType
        });
    },

    deleteThread: (threadId) => {
        return axiosClient.delete(`/thread/delete/${threadId}`);
    },

    getComments: (threadId, page = 0, size = 10) => {
        return axiosClient.get(`/thread/comment/${threadId}?page=${page}&size=${size}`);
    },

    postComment: (threadId, content) => {
        return axiosClient.post('/thread/comment/post', {
            threadId,
            content
        });
    },

    voteComment: (commentId, voteType) => {
        return axiosClient.post('/thread/comment/react', {
            commentId,
            voteType
        });
    },

    deleteComment: (commentId) => {
        return axiosClient.delete(`/thread/comment/delete/${commentId}`);
    },

    getFlags: () => {
        return axiosClient.get('/thread/utility/flag');
    },

    getMyThreads: (page = 0, size = 10) => {
        return axiosClient.get(`/thread/summary/posted?page=${page}&size=${size}`);
    },

    getMyComments: (page = 0, size = 10) => {
        return axiosClient.get(`thread/summary/commented?page=${page}&size=${size}`);
    }
};
