import http from 'k6/http';
import { BASE_URL, params, validateResponse } from './config/base.js';

export const options = {
    stages: [
        { duration: '30s', target: 50 },
        { duration: '1m', target: 150 },
        { duration: '1m', target: 300 },
        { duration: '30s', target: 500 },
        { duration: '30s', target: 0 },
    ],

    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<5000'],
    },
};

export default function () {

    const res = http.get(
        `${BASE_URL}/estaciones-de-servicio/cercanas?latitud=43.36&longitud=-5.85&limite=10`,
        params
    );

    validateResponse(res);
}