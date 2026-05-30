import { check, sleep } from 'k6';

//export const BASE_URL = 'https://api.carburo.app/api/v1/public';
export const BASE_URL = 'http://localhost:8089/api/v1/public';

export const params = {
    headers: {
        'x-api-key': __ENV.API_KEY,
        'Content-Type': 'application/json',
    },
};

export function validateResponse(res) {

    check(res, {
        'status 200': (r) => r.status === 200,
        'success=true': (r) => {
            try {
                return r.json().success === true;
            } catch {
                return false;
            }
        },
    });

}

export function randomSleep(min = 0.05, max = 0.2) {
    sleep(Math.random() * (max - min) + min);
}