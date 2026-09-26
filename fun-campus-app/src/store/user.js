import { defineStore } from 'pinia';
import {
  getToken,
  setToken,
  getCachedUserInfo,
  setCachedUserInfo,
  clearLoginStorage,
} from '@/utils/auth';
import { loginApi } from '@/api/login-api';
import { userApi } from '@/api/user-api';

/**
 * 用户态：token + 当前用户信息（只放用户态，列表数据不进 store）
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: getCachedUserInfo(),
  }),

  getters: {
    isLogin: (state) => !!state.token,
    // 是否可发布活动（首页/我的页按需用）
    canPublishActivity: (state) => !!state.userInfo?.canPublishActivity,
  },

  actions: {
    async login(loginForm) {
      const { data } = await loginApi.login(loginForm);
      this.token = data.token;
      setToken(data.token);
      return data;
    },

    // 拉取当前用户信息（默认有缓存就先用缓存）
    async fetchUserInfo(force = false) {
      if (this.userInfo && !force) {
        return this.userInfo;
      }
      const { data } = await userApi.current();
      this.userInfo = data;
      setCachedUserInfo(data);
      return data;
    },

    async logout() {
      try {
        await loginApi.logout();
      } catch (e) {
        // 退出接口失败也照常清本地登录态
      }
      this.clearLoginState();
    },

    clearLoginState() {
      this.token = '';
      this.userInfo = null;
      clearLoginStorage();
    },
  },
});
