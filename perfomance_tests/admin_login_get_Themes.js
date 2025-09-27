import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

const loginTime = new Trend('login_time');
const getThemesTime = new Trend('get_themes_time');
const flowTime = new Trend('flow_total_time');

export const options = {
    vus: 10,
    duration: '3m',
    thresholds: {
        login_time: ['p(95)<300'],
        get_themes_time: ['p(95)<300'],
        http_req_failed: ['rate<0.01'],
    },
};

function authHeadersFrom(res) {
    const token = res.json('token') || res.json('accessToken');
    if (token) return { headers: { Authorization: `Bearer ${token}` } };
    return { headers: {} };
}

export default function () {
    const base = __ENV.BASE_URL;
    const start = Date.now();

    const loginRes = http.get(`${base}/auth/demo/admin`, null);
    loginTime.add(loginRes.timings.duration);
    check(loginRes, { 'login ok': r => r.status === 200 || r.status === 201 });

    const auth = authHeadersFrom(loginRes);

    const themesRes = http.get(`${base}/themes`, auth);
    getThemesTime.add(themesRes.timings.duration);
    check(themesRes, { 'themes 200': r => r.status === 200 });

    flowTime.add(Date.now() - start);
    sleep(0.2);
}
