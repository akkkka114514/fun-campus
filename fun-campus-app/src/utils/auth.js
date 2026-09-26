/**
 * 登录态本地存储读写
 */
const TOKEN_KEY = 'FUN_CAMPUS_APP_TOKEN';
const USER_INFO_KEY = 'FUN_CAMPUS_APP_USER';

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || '';
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token);
}

export function clearToken() {
  uni.removeStorageSync(TOKEN_KEY);
}

export function getCachedUserInfo() {
  return uni.getStorageSync(USER_INFO_KEY) || null;
}

export function setCachedUserInfo(userInfo) {
  uni.setStorageSync(USER_INFO_KEY, userInfo);
}

export function clearCachedUserInfo() {
  uni.removeStorageSync(USER_INFO_KEY);
}

/**
 * 登录态变化时的统一清理入口
 */
export function clearLoginStorage() {
  clearToken();
  clearCachedUserInfo();
}
