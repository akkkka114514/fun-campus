import { get, post } from '@/utils/request';

export const userApi = {
  // 当前用户信息（学校/学院/年级名称、积分、头像、手机号）
  current() {
    return get('/portal/portalUser/current');
  },

  // 更新资料：avatarFileKey / phone / gender
  updateProfile(profileForm) {
    return post('/portal/portalUser/updateProfile', profileForm);
  },
};
