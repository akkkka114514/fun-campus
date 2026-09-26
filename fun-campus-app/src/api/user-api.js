import { get, post } from '@/utils/request';

export const userApi = {
  // 当前用户信息（学校/学院/年级名称、积分、头像、手机号）
  current() {
    return get('/portal/portalUser/current');
  },

  // 更新资料：nickname / avatarFileKey / phone / gender
  updateProfile(profileForm) {
    return post('/portal/portalUser/updateProfile', profileForm);
  },

  // 实践积分明细（已结束且已签到的活动）
  gradeScoreDetailList() {
    return get('/portal/portalUser/gradeScoreDetail/list');
  },

  // 信誉分变动记录（爽约扣分）
  creditScoreLogList() {
    return get('/portal/portalUser/creditScoreLog/list');
  },
};
