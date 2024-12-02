import qs from "qs";
import axios, {AxiosRequestConfig} from "axios";
import {getAuthData} from "./storage";

type LoginResponse =
    {
        access_token: string,
        token_type: string,
        expires_in: number,
        scope: string,
        userId: number,
        userFistName: string
    }

// export const BASE_URL_BACK = process.env.REACT_APP_BACKEND_URL ?? 'http://condominio-spring-patrick.duckdns.org:8085';
export const BASE_URL_BACK = process.env.REACT_APP_BACKEND_URL ?? 'http://localhost:8086';
const CLIENT_ID = process.env.REACT_APP_CLIENT_ID ?? 'front_nova_alianca'
const CLIENT_SECRET = process.env.REACT_APP_CLIENT_SECRET ?? 'nova123'
const tokenKey = 'authData'

type LoginData = {
    username: string;
    password: string;
}

export const requestBackendLogin = (loginData: LoginData) => {
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': 'Basic ' + window.btoa(CLIENT_ID + ':' + CLIENT_SECRET)
    }

    const data = qs.stringify({
        ...loginData,
        grant_type: 'password'
    })

    return axios({
        method: 'POST',
        baseURL: BASE_URL_BACK,
        url: '/oauth/token',
        data,
        headers
    })
}


export const requestBackend = (config : AxiosRequestConfig) => {
    const headers = config.withCredentials ? {
        ...config.headers,   // para pegar os header ja passado na chamada e acrescentar o cod abaixo
        'Authorization': 'Bearer ' + getAuthData().access_token
    } : config.headers

    return axios({...config, baseURL : BASE_URL_BACK , headers: headers}); //... spred opaator para desconstruir o obj

}

export const requestBackendbyUser = (config : AxiosRequestConfig) => {
    const headers = config.withCredentials ? {
        ...config.headers,   // para pegar os header ja passado na chamada e acrescentar o cod abaixo
        'Authorization': 'Bearer ' + getAuthData().access_token
    } : config.headers

    const url = config.url + '/' + getAuthData().userId


    return axios({...config, url : url, baseURL : BASE_URL_BACK , headers: headers}); //... spred opaator para desconstruir o obj

}




