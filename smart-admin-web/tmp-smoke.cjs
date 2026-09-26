// 临时 smoke 测试脚本：模拟前端 SM4 加密登录 + 批量验证 queryPage 端点
// 运行：node tmp-smoke.cjs （工作目录 smart-admin-web）
const { sm4 } = require('sm-crypto');

const BASE = 'http://127.0.0.1:1024';
const KEY = Buffer.from('1024lab__1024lab', 'utf8').toString('hex');

function encPassword(pwd) {
  const hex = sm4.encrypt(pwd, KEY);
  return Buffer.from(hex, 'utf8').toString('base64');
}

async function post(path, body, token) {
  const res = await fetch(BASE + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}) },
    body: JSON.stringify(body),
  });
  try {
    return await res.json();
  } catch (e) {
    return { code: res.status, msg: 'NON-JSON RESPONSE' };
  }
}

async function get(path) {
  const res = await fetch(BASE + path);
  return await res.json();
}

(async () => {
  const cap = (await get('/backend/login/getCaptcha')).data;
  const loginRes = await post('/backend/login', {
    username: 'akkkka',
    password: encPassword('Qw020829@qazwsx'),
    captchaCode: cap.captchaText,
    captchaUuid: cap.captchaUuid,
    loginDevice: 1,
  });
  const token = loginRes.data && loginRes.data.token;
  console.log('LOGIN:', loginRes.code, loginRes.ok, token ? 'TOKEN_OK' : JSON.stringify(loginRes).slice(0, 300));
  if (!token) return;

  const endpoints = [
    '/backend/activity/query',
    '/backend/gradeInfo/queryPage',
    '/backend/activityCategory/queryPage',
    '/backend/activityCanEnrollTribe/queryPage',
    '/backend/activitySigninManager/queryPage',
    '/backend/activityReviewLog/queryPage',
    '/backend/activityCanEnrollCollege/queryPage',
    '/backend/activityCanEnrollGrade/queryPage',
    '/backend/schoolInfo/queryPage',
    '/backend/organizationInfo/queryPage',
    '/backend/collegeInfo/queryPage',
    '/backend/portalUser/queryPage',
    '/backend/tribeUser/queryPage',
    '/backend/tribe/queryPage',
  ];

  for (const ep of endpoints) {
    const r = await post(ep, { pageNum: 1, pageSize: 10 }, token);
    if (r.ok && r.data) {
      console.log(ep, '=> OK total=' + r.data.total + ' list=' + (r.data.list || []).length);
    } else {
      console.log(ep, '=> FAIL code=' + r.code + ' msg=' + String(r.msg || '').slice(0, 60));
    }
  }

  // 活动查询样例数据
  const ra = await post('/backend/activity/query', { pageNum: 1, pageSize: 5 }, token);
  if (ra.ok && ra.data.list && ra.data.list.length) {
    const first = ra.data.list[0];
    console.log('ACTIVITY[0].activity:', JSON.stringify(first.activity).slice(0, 300));
    console.log('ACTIVITY[0].schedule:', JSON.stringify(first.schedule).slice(0, 200));
  }
  // 带 keyword 过滤
  const rk = await post('/backend/activity/query', { pageNum: 1, pageSize: 5, keyword: '测试' }, token);
  console.log('ACTIVITY keyword=测试 =>', rk.ok ? 'OK total=' + rk.data.total : 'FAIL ' + String(rk.msg).slice(0, 60));
})().catch((e) => console.error('SMOKE ERROR:', e.message));
