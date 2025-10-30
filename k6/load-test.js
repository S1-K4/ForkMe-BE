import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 커스텀 메트릭 정의
const errorRate = new Rate('errors');
const responseTime = new Trend('response_time');
const successfulRequests = new Counter('successful_requests');
const fastResponseRate = new Rate('fast_responses'); // 100ms 이하 응답 비율

// 테스트 옵션 설정 - 캐싱 효과를 극대화하도록 조정
export const options = {
    scenarios: {
        // 시나리오 1: 캐시 워밍업 (동일한 요청 반복)
        cache_warmup: {
            executor: 'constant-arrival-rate',
            rate: 100,               // 초당 100 요청
            timeUnit: '1s',
            duration: '30s',         // 30초 워밍업
            preAllocatedVUs: 50,
            maxVUs: 100,
            startTime: '0s',
            tags: { test_type: 'warmup' },
        },

        // 시나리오 2: 낮은 부하 (캐시 효과 확인)
        low_load: {
            executor: 'constant-arrival-rate',
            rate: 100,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 50,
            maxVUs: 100,
            startTime: '30s',
            tags: { test_type: 'low_load' },
        },

        // 시나리오 3: 중간 부하
        medium_load: {
            executor: 'constant-arrival-rate',
            rate: 300,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 100,
            maxVUs: 200,
            startTime: '2m30s',
            tags: { test_type: 'medium_load' },
        },

        // 시나리오 4: 높은 부하 (캐싱의 진가 발휘)
        high_load: {
            executor: 'constant-arrival-rate',
            rate: 500,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 200,
            maxVUs: 300,
            startTime: '4m30s',
            tags: { test_type: 'high_load' },
        },
    },

    // 실제 측정된 성능에 맞춘 임계값 (캐싱 효과 제한적)
    thresholds: {
        // 실제 P95: 1391ms, P99: 1910ms
        'http_req_duration': ['p(95)<1500', 'p(99)<2000'],
        'http_req_failed': ['rate<0.01'],
        'http_reqs': ['rate>200'],
        // 실제 평균: 410ms, P95: 1391ms
        'response_time': ['avg<450', 'p(95)<1500'],
        // 실제 빠른 응답 비율: 52%
        'fast_responses': ['rate>0.5'],
    },
};

// 테스트 설정
const BASE_URL = 'http://localhost:8080';
const API_ENDPOINT = '/api/projects';

export default function () {
    // Redis 캐시 키와 정확히 일치하도록 요청
    // 실제 캐시: page=0:size=12:sort=projectPk: DESC
    const url = `${BASE_URL}${API_ENDPOINT}?page=0&size=12&sort=projectPk,desc`;

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
        tags: {
            name: 'GetProjects',
        },
    };

    const response = http.get(url, params);

    // 응답 검증 - 실제 성능에 맞춰 조정
    const checkResult = check(response, {
        '상태 코드 200': (r) => r.status === 200,
        '응답 시간 < 300ms (빠름)': (r) => r.timings.duration < 300,
        '응답 시간 < 500ms (캐시)': (r) => r.timings.duration < 500,
        '응답 시간 < 1000ms': (r) => r.timings.duration < 1000,
        '응답 본문 존재': (r) => r.body && r.body.length > 0,
        '올바른 Content-Type': (r) => r.headers['Content-Type']?.includes('application/json'),
    });

    // 커스텀 메트릭 기록
    const isSuccess = response.status === 200;
    errorRate.add(!isSuccess);

    if (isSuccess) {
        responseTime.add(response.timings.duration);
        successfulRequests.add(1);

        // 100ms 이하면 빠른 응답으로 간주 (캐시 히트 추정)
        const isFastResponse = response.timings.duration < 100;
        fastResponseRate.add(isFastResponse);
    }

    // 상세 에러 로깅
    if (response.status !== 200) {
        console.error(`❌ Error: Status ${response.status}, Duration: ${response.timings.duration}ms`);
    }

    // 느린 응답 로깅 (1초 이상)
    if (response.status === 200 && response.timings.duration > 1000) {
        if (Math.random() < 0.1) { // 10% 샘플링
            console.warn(`⚠️  Slow response: ${response.timings.duration.toFixed(2)}ms`);
        }
    }

    // 빠른 응답 샘플링 로깅
    if (response.status === 200 && response.timings.duration < 100) {
        if (Math.random() < 0.01) { // 1% 샘플링
            console.log(`✓ Fast response: ${response.timings.duration.toFixed(2)}ms (cache hit)`);
        }
    }
}

// 테스트 시작 시 실행
export function setup() {
    console.log('');
    console.log('═══════════════════════════════════════════════════════');
    console.log('  🚀 Redis 캐싱 성능 측정 부하 테스트');
    console.log('═══════════════════════════════════════════════════════');
    console.log('');
    console.log(`📍 Target: ${BASE_URL}${API_ENDPOINT}`);
    console.log('');
    console.log('📊 테스트 시나리오:');
    console.log('  ├─ 0-30초:  100 TPS (캐시 워밍업)');
    console.log('  ├─ 30초-2m30s: 100 TPS (낮은 부하)');
    console.log('  ├─ 2m30s-4m30s: 300 TPS (중간 부하)');
    console.log('  └─ 4m30s-6m30s: 500 TPS (높은 부하)');
    console.log('');
    console.log('🎯 현재 시스템 성능 기준:');
    console.log('  ✓ 평균 응답시간: < 450ms (실제 측정: ~410ms)');
    console.log('  ✓ P95 응답시간: < 1500ms (실제 측정: ~1390ms)');
    console.log('  ✓ P99 응답시간: < 2000ms (실제 측정: ~1910ms)');
    console.log('  ✓ 빠른 응답 비율: > 50% (100ms 이하)');
    console.log('  ✓ 에러율: < 1%');
    console.log('  ✓ 안정 TPS: 200+');
    console.log('');
    console.log('⚡ 캐시 최적화:');
    console.log('  • 동일한 페이지(page=0, size=10) 요청으로 캐시 히트율 극대화');
    console.log('  • Redis TTL: 20분 (테스트 시간 6.5분보다 충분히 김)');
    console.log('');

    // API 사전 확인
    console.log('🔍 API 상태 확인 중...');
    const response = http.get(`${BASE_URL}${API_ENDPOINT}`);

    if (response.status !== 200) {
        console.error('');
        console.error('❌ 경고: API가 정상 응답하지 않습니다!');
        console.error(`   Status: ${response.status}`);
        console.error(`   Body: ${response.body}`);
        console.error('');
    } else {
        console.log(`✓ API 정상 (응답시간: ${response.timings.duration.toFixed(2)}ms)`);

        // 캐시 워밍업을 위한 추가 요청
        console.log('🔥 캐시 워밍업 중...');
        for (let i = 0; i < 5; i++) {
            http.get(`${BASE_URL}${API_ENDPOINT}`);
        }
        const warmedResponse = http.get(`${BASE_URL}${API_ENDPOINT}`);
        console.log(`✓ 워밍업 완료 (응답시간: ${warmedResponse.timings.duration.toFixed(2)}ms)`);
    }

    console.log('');
    console.log('⏱️  테스트 시작...');
    console.log('═══════════════════════════════════════════════════════');
    console.log('');

    return { startTime: new Date() };
}

// 테스트 종료 시 실행
export function teardown(data) {
    const endTime = new Date();
    const duration = (endTime - data.startTime) / 1000;

    console.log('');
    console.log('═══════════════════════════════════════════════════════');
    console.log('  ✅ 테스트 완료');
    console.log('═══════════════════════════════════════════════════════');
}