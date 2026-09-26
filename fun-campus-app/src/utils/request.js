/**
 * uni.request 的 Promise 封装，行为对齐 smart-admin-web 的 src/lib/axios.js：
 * - baseURL 从环境变量 VITE_APP_API_URL 取（.env.development 里配置）
 * - 每个请求带 Authorization: Bearer <token>
 * - code !== 1 统一 toast；30007/30008 登录失效清 token 踢到登录页
 * - GET 走 query 参数、POST 走 body
 */
import { getToken, clearLoginStorage } from './auth';

export const API_BASE_URL = import.meta.env.VITE_APP_API_URL || 'http://127.0.0.1:1024';

// 登录失效的 error code（与管理端 axios.js 保持一致）
const LOGIN_EXPIRED_CODES = [30007, 30008];

// 登录失效后避免并发请求重复跳转
let redirectingToLogin = false;

function toLogin() {
  if (redirectingToLogin) {
    return;
  }
  redirectingToLogin = true;
  clearLoginStorage();
  uni.showToast({ title: '您没有登录，请重新登录', icon: 'none' });
  setTimeout(() => {
    uni.reLaunch({
      url: '/pages/login/login',
      complete: () => {
        redirectingToLogin = false;
      },
    });
  }, 300);
}

function request(options) {
  const { url, method = 'GET', header = {}, timeout = 15000 } = options;
  // 裸参接口会传数字（后端 @RequestBody Long）：uni.request 对 Number 类型会告警，这里统一转成 JSON 原文
  const data = typeof options.data === 'number' ? JSON.stringify(options.data) : options.data;
  const token = getToken();
  if (token) {
    header.Authorization = 'Bearer ' + token;
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: API_BASE_URL + url,
      method,
      data,
      header,
      timeout,
      success: (res) => {
        const body = res.data;
        // 非 JSON 响应直接返回原始结果（如 HTML 收银台页）
        if (!body || typeof body !== 'object') {
          resolve(res);
          return;
        }
        if (body.code && body.code !== 1) {
          if (LOGIN_EXPIRED_CODES.includes(body.code)) {
            toLogin();
            reject(body);
            return;
          }
          uni.showToast({ title: body.msg || '请求失败', icon: 'none' });
          reject(body);
          return;
        }
        resolve(body);
      },
      fail: (err) => {
        uni.showToast({ title: '网络连接错误', icon: 'none' });
        reject(err);
      },
    });
  });
}

export function get(url, params) {
  return request({ url, method: 'GET', data: params });
}

export function post(url, data) {
  return request({ url, method: 'POST', data });
}

/**
 * 裸参 POST：后端有一批 @RequestBody Long activityId 这类接口，body 直接就是数字/字符串
 */
export function postRaw(url, raw) {
  return request({ url, method: 'POST', data: raw });
}

export default { get, post, postRaw };
