import axios from 'axios';
import type { AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import type { IAuthResponse } from '../types';

export const getEnvironment = () => {
  const { hostname, port } = window.location;
  const isLocalhost = hostname === 'localhost' || hostname === '127.0.0.1' || hostname.startsWith('192.168.');

  if (!isLocalhost) {
    return { name: 'PRD' as const, label: 'Produção', color: '#10b981' }; // Verde esmeralda
  }

  // Identifica DEV (Vite Dev Server) e HML (Nginx/Docker local) baseado nas portas padrão
  const isViteDev = port === '3000' || port === '3001' || port === '5173';
  if (isViteDev) {
    return { name: 'DEV' as const, label: 'Desenvolvimento', color: '#ef4444' }; // Vermelho
  }

  return { name: 'HML' as const, label: 'Homologação (Docker)', color: '#f59e0b' }; // Laranja/Amarelo
};

const getBackendUrl = (): string => {
  const envUrl = import.meta.env.BACK_END_NOVA_ALIANCA;
  const { hostname, origin } = window.location;

  const isLocalhost = hostname === 'localhost' || hostname === '127.0.0.1' || hostname.startsWith('192.168.');

  if (!isLocalhost) {
    if (envUrl && !envUrl.includes('localhost') && !envUrl.includes('192.168.')) {
      return envUrl;
    }
    // Mantém https://dominio:porta (ex: 8443) e adiciona o prefixo /api
    return `${origin}/api`;
  }

  // DEV ou HML Docker rodando localmente
  return `http://${hostname}:8086`;
};

const API_URL = getBackendUrl();

const api = axios.create({
  baseURL: API_URL,
});

// Interceptor de Requisições
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Se for requisição de autenticação, não envia o token do usuário logado
    if (config.url === '/oauth/token') {
      return config;
    }

    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor de Respostas para tratar expiração de token (401)
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      const isLoginRequest = error.config && error.config.url === '/oauth/token';
      if (!isLoginRequest) {
        // Token expirado ou inválido
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// Métodos auxiliares fortemente tipados
export const backEndService = {
  // Chamada de Autenticação OAuth2 Password Flow
  login: async (username: string, password: string): Promise<IAuthResponse> => {
    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('username', username);
    params.append('password', password);

    // Credenciais do cliente OAuth2 registradas no backend: front_nova_alianca / nova123
    const clientId = 'front_nova_alianca';
    const clientSecret = '18af63e3001dcae1ca594292f23ddc3871dffcc4007c686c2aa73080661fcc2b';
    const basicAuth = 'Basic ' + btoa(`${clientId}:${clientSecret}`);

    const config: AxiosRequestConfig = {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': basicAuth,
      },
    };

    const response = await api.post<IAuthResponse>('/oauth/token', params, config);
    return response.data;
  },

  // Chamadas genéricas tipadas
  get: async <T>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    const response = await api.get<T>(url, config);
    return response.data;
  },

  post: async <T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> => {
    const response = await api.post<T>(url, data, config);
    return response.data;
  },

  put: async <T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> => {
    const response = await api.put<T>(url, data, config);
    return response.data;
  },

  delete: async <T>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    const response = await api.delete<T>(url, config);
    return response.data;
  },
};

export default api;
