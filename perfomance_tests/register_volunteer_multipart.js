import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

const createVolunteerTime = new Trend('create_volunteer_time');

export const options = {
    vus: 10,
    duration: '3m',
    thresholds: {
        create_volunteer_time: ['p(95)<800'],
        http_req_failed: ['rate<0.02'],
    },
};

const bin = open('./doc.pdf', 'b');

export default function () {
    const base = __ENV.BASE_URL;


    const suffix = `${__VU}_${__ITER}_${Date.now()}`;
    const dto = {
        id: null,
        name: `Perf User ${suffix}`,
        username: `perf_${suffix}`,
        email: `perf_${suffix}@example.com`,
        password: 'Secret123!',
        role: 'VOLUNTEER',
        active: true,
        isInstitutionActive: false,
        confirmationToken: '',
        institutionId: 0,
    };

    // multipart/form-data:
    const formData = {
        volunteer: http.file(JSON.stringify(dto), 'volunteer.json', 'application/json'),
        file: http.file(bin, 'doc.pdf', 'application/pdf'),
    };

    const res = http.post(`${base}/users/registerVolunteer`, formData);
    createVolunteerTime.add(res.timings.duration);

    check(res, {
        'status 200/201/202': r => [200,201,202].includes(r.status),
    });

    sleep(0.2);
}
