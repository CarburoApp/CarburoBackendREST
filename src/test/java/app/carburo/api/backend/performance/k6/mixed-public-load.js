import http from 'k6/http';
import {BASE_URL, params, randomSleep, validateResponse} from './config/base.js';

const coords = [
    [43.36, -5.85],
    [40.41, -3.70],
    [41.38, 2.17],
];

export const options = {
    stages: [
        {duration: '30s', target: 10},
        {duration: '1m', target: 50},
        {duration: '1m', target: 100},
        {duration: '30s', target: 150},
        {duration: '30s', target: 0},
    ],
};

export default function () {

    let res;

    // catálogos
    if (Math.random() < 0.3) {

        res = http.get(`${BASE_URL}/combustibles`, params);

        validateResponse(res);
    }

    // provincia
    if (Math.random() < 0.5) {

        res = http.get(
            `${BASE_URL}/estaciones-de-servicio/provincia/33`,
            params
        );

        validateResponse(res);
    }

    // cercanas
    if (Math.random() < 0.7) {

        const coord = coords[Math.floor(Math.random() * coords.length)];

        res = http.get(
            `${BASE_URL}/estaciones-de-servicio/cercanas?latitud=${coord[0]}&longitud=${coord[1]}&limite=5`,
            params
        );

        validateResponse(res);
    }

    // históricos
    if (Math.random() < 0.2) {

        res = http.get(
            `${BASE_URL}/estaciones-de-servicio/1/precios-combustibles?dias=5`,
            params
        );

        validateResponse(res);
    }

    randomSleep(0.1, 0.4);
}