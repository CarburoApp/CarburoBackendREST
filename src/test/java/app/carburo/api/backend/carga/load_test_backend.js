import http from 'k6/http';
import { check, sleep } from 'k6';

// =============================================================================
// CONFIGURACIÓN GLOBAL MODIFICABLE
// =============================================================================
const BASE_URL = 'http://localhost:8089';
const API_KEY = '<<<<<<<<<<<<<<<< API - KEY - AQUÍ >>>>>>>>>>>>>>>>';

// Límites de IDs para simulación probabilística
const MAX_MUNICIPIO_ID = 8000;
const MAX_ESTACION_ID = 12000;
const MAX_DIAS_HISTORICO = 30;

// =============================================================================
// ENRUTAMIENTO Y CURVAS DE CARGA
// =============================================================================
export const options = {
    scenarios: {

        // 1. Carga normal (uso real móvil ocasional)
        escenario_carga_normal: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 10 },
                { duration: '3m', target: 10 },
                { duration: '30s', target: 0 },
            ],
            gracefulRampDown: '30s',
            exec: 'ejecutarPruebasPublicas',
        },

        // 2. Estrés moderado
        escenario_estres: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 25 },
                { duration: '2m', target: 45 },
                { duration: '1m', target: 0 },
            ],
            startTime: '4m30s',
            exec: 'ejecutarPruebasPublicas',
        },

        // 3. Pico realista
        escenario_picos: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 60 },
                { duration: '1m', target: 60 },
                { duration: '20s', target: 0 },
            ],
            startTime: '9m',
            exec: 'ejecutarPruebasPublicas',
        },
    },

    thresholds: {
        'http_req_failed': ['rate<0.01'],
        'http_req_duration{grupo:grupo1}': ['p(95)<200'],
        'http_req_duration{grupo:grupo2}': ['p(95)<5000'],
    },
};

// =============================================================================
// UTILIDADES
// =============================================================================
function getRandomId(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function getRandomCoordinates() {
    const lat = (Math.random() * (43.5 - 36.0) + 36.0).toFixed(6);
    const lon = (Math.random() * (3.3 - (-9.3)) + (-9.3)).toFixed(6);
    return { lat, lon };
}

// =============================================================================
// TEST PRINCIPAL
// =============================================================================
export function ejecutarPruebasPublicas() {

    const paramsGrupo1 = {
        headers: {
            'X-API-KEY': API_KEY,
            'Content-Type': 'application/json',
        },
        tags: { grupo: 'grupo1' },
    };

    const paramsGrupo2 = {
        headers: {
            'X-API-KEY': API_KEY,
            'Content-Type': 'application/json',
        },
        tags: { grupo: 'grupo2' },
    };

    // =========================================================
    // GRUPO 1: CONSULTAS LIGERAS (CATÁLOGOS)
    // =========================================================

    let resCombustibles = http.get(
        `${BASE_URL}/api/v1/public/combustibles`,
        paramsGrupo1
    );
    check(resCombustibles, {
        'Combustibles OK': (r) => r.status === 200
    });

    let resCCAA = http.get(
        `${BASE_URL}/api/v1/public/comunidades-autonomas`,
        paramsGrupo1
    );
    check(resCCAA, {
        'CCAA OK': (r) => r.status === 200
    });

    let randomProvinciaId = getRandomId(1, 50);
    let resMunicipiosProv = http.get(
        `${BASE_URL}/api/v1/public/municipios/provincia/${randomProvinciaId}/con-estaciones-de-servicio`,
        paramsGrupo1
    );
    check(resMunicipiosProv, {
        'Municipios por provincia OK': (r) => r.status === 200
    });

    sleep(1);

    // =========================================================
    // GRUPO 2: CONSULTAS PESADAS
    // =========================================================

    let randomMunicipioId = getRandomId(1, MAX_MUNICIPIO_ID);
    let resEstacionesMunicipio = http.get(
        `${BASE_URL}/api/v1/public/estaciones-de-servicio/municipio/${randomMunicipioId}`,
        paramsGrupo2
    );
    check(resEstacionesMunicipio, {
        'Estaciones por municipio OK': (r) => r.status === 200
    });

    const coords = getRandomCoordinates();
    let resCercanas = http.get(
        `${BASE_URL}/api/v1/public/estaciones-de-servicio/cercanas?latitud=${coords.lat}&longitud=${coords.lon}&limite=1`,
        paramsGrupo2
    );
    check(resCercanas, {
        'Geolocalización OK': (r) => r.status === 200
    });

    let randomEstacionId = getRandomId(1, MAX_ESTACION_ID);
    let diasHistorico = getRandomId(3, MAX_DIAS_HISTORICO);

    let resHistorico = http.get(
        `${BASE_URL}/api/v1/public/estaciones-de-servicio/${randomEstacionId}/precios-combustibles?dias=${diasHistorico}`,
        paramsGrupo2
    );
    check(resHistorico, {
        'Histórico OK o 404': (r) => r.status === 200 || r.status === 404
    });

    sleep(1.5);
}