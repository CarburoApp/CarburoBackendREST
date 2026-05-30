import http from 'k6/http';
import { BASE_URL, params, validateResponse, randomSleep } from './config/base.js';

const days = [1, 5, 15, 30];

export const options = {
    vus: 30,
    duration: '2m',

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<2500'],
    },
};

export default function () {

    const dias = days[Math.floor(Math.random() * days.length)];

    const estacion = Math.floor(Math.random() * 1000) + 1;

    const res = http.get(
        `${BASE_URL}/estaciones-de-servicio/${estacion}/precios-combustibles?dias=${dias}`,
        params
    );

    validateResponse(res);

    randomSleep(0.1, 0.3);
}