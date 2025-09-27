import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';


const logintime = new Trend('login_time');

export const options = {
    vus : 20,
    duration: '3m',
    thresholds: {
        'login_time': ['p(95)<500'],
    },
};

function extractTokenOrCookie(res) {
    const token = res.json('token') || res.json('accessToken');
    return { token, cookies: res.cookies };
}

export default function () {

    const res = http.get(`${__ENV.BASE_URL}/auth/demo/volunteer`, null);
    logintime.add(res.timings.duration);

    const { token } = extractTokenOrCookie(res);

    check(res, {
        'status 200/201': r => r.status === 200 || r.status === 201,
        'tem token ou cookie': _ => !!token || (res.cookies && Object.keys(res.cookies).length > 0),
    });

}


