import { get, post } from '@/utils/request';

export const loginApi = {
  // 获取图形验证码：captchaUuid + captchaBase64Image（含 data:image/png;base64 前缀）+ expireSeconds
  getCaptcha() {
    return get('/portal/login/getCaptcha');
  },

  // 门户登录：username / password / captchaCode / captchaUuid / loginDevice
  // TODO: 后端启用 apiEncrypt 后，password 需要加密再传
  login(loginForm) {
    return post('/portal/login', loginForm);
  },

  // 退出登录
  logout() {
    return post('/portal/logout');
  },
};
