import { get } from '@/utils/request';

export const homeApi = {
  // 首页活动列表：activeActivityPage 1-本校 2-全局；返回 { mySchoolActivities: Page, globalActivities: Page }
  homeData(params) {
    return get('/portal/homeData', params);
  },
};
