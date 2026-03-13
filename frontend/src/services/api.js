import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api' // Spring Boot default port
});

export const submitHackathonProject = async (data) => {
    // Pass headers explicitly for the multipart upload
    const response = await api.post('/submissions', data, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
    return response.data;
};

export const getAllSubmissions = async () => {
    const response = await api.get('/submissions');
    return response.data;
};

export const getSubmissionDetails = async (id) => {
    const response = await api.get(`/submissions/${id}`);
    return response.data;
};

export const triggerEvaluation = async (id) => {
    const response = await api.post(`/submissions/${id}/evaluate`);
    return response.data;
};

export default api;
