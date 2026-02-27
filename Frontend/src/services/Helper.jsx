import React from "react";
import axios from "axios"

const BASE_URL = "http://localhost:8084";
export const myAxios = axios.create({ baseURL: BASE_URL,withCredentials: true });

//----- private axios for access authorize methods -------
export const privateAxios = axios.create({
    baseURL: "http://localhost:8084",
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    }
});

// 1. THIS ATTACHES THE TOKEN (THIS IS REQUIRED!) -----------
privateAxios.interceptors.request.use(
    (config) => {
    // const token = localStorage.getItem("access_token");

    // if (token) {
    //     config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
}, 
(error) => Promise.reject(error)
);

// 2. THIS HANDLES THE 401 ERROR
privateAxios.interceptors.response.use(
    (response) => response,
    (error) =>{

        const originalRequest = error.config;

        // if gateway return 401 (as it should)
        if (error.response && error.response.status === 401 && !!originalRequest._retry) {
            // localStorage.removeItem("access_token") // clear access token

            //redirect the whole browser window to login flow
            window.location.href = "http://localhost:8084/oauth2/authorization/my-gateway-client";
        }

        return Promise.reject(error);
    }
);