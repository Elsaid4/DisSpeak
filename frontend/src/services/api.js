import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

// aggiungi automaticamente il token JWT ad ogni richiesta
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// chiamate autenticazione
export const authApi = {
    login: (credentials) => api.post('/auth/login', credentials),
    register: (userData) => api.post('/auth/register', userData),
};

// chiamate server e canali
export const serverApi = {
    getAll: () => api.get('/servers'),
    getById: (id) => api.get(`/servers/${id}`),
    create: (data) => api.post('/servers', data),
};

export const channelApi = {
    getByServer: (serverId) => api.get(`/servers/${serverId}/channels`),
    create: (serverId, data) => api.post(`/servers/${serverId}/channels`, data),
};

// chiamate messaggi
export const messageApi = {
    getByChannel: (channelId) => api.get(`/channels/${channelId}/messages`),
};

export default api;