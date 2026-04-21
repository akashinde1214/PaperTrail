import axios from 'axios';

const fromEnv = process.env.EXPO_PUBLIC_API_BASE_URL;

export const API_BASE_URL = fromEnv || 'http://192.168.43.77:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 20000
});

export function attachToken(token) {
  if (token) {
    api.defaults.headers.common.Authorization = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common.Authorization;
  }
}

export default api;
