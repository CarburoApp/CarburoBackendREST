import http from 'k6/http';
import { BASE_URL, params, validateResponse, randomSleep } from './config/base.js';

export const options = {
    vus: 50,
    duration: '2m',

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<500'],
    },
};

export default function () {

    let res;

    res = http.get(`${BASE_URL}/combustibles`, params);
    validateResponse(res);

    randomSleep();

    res = http.get(`${BASE_URL}/provincias`, params);
    validateResponse(res);

    randomSleep();

    res = http.get(`${BASE_URL}/municipios/provincia/33`, params);
    validateResponse(res);

    randomSleep();
}