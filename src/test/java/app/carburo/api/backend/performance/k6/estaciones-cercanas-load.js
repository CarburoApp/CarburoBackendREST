import http from 'k6/http';
import { BASE_URL, params, validateResponse, randomSleep } from './config/base.js';

const coords = [
    [43.36, -5.85],
    [40.41, -3.70],
    [41.38, 2.17],
    [37.38, -5.99],
];

export const options = {
    stages: [
        { duration: '30s', target: 20 },
        { duration: '1m', target: 50 },
        { duration: '1m', target: 100 },
        { duration: '30s', target: 0 },
    ],

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<2000'],
    },
};

export default function () {

    const coord = coords[Math.floor(Math.random() * coords.length)];

    const limite = Math.floor(Math.random() * 10) + 1;

    const res = http.get(
        `${BASE_URL}/estaciones-de-servicio/cercanas?latitud=${coord[0]}&longitud=${coord[1]}&limite=${limite}`,
        params
    );

    validateResponse(res);

    randomSleep(0.1, 0.3);
}