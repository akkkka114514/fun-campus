import { get } from '@/utils/request';

export const homeApi = {
  // 首页活动列表：activeActivityPage 1-本校 2-全局；
  // 可选筛选：categoryId（分类）/ status（0-未开始 1-报名中 2-报名结束 3-进行中 4-已结束）/ timeRange（1|7|30，活动开始时间在未来 N 天内）
  homeData(params) {
    return get('/portal/homeData', params);
  },
  // 活动分类列表（首页筛选用）→ [{ id, name }]
  categoryList() {
    return get('/portal/activity/category/list');
  },
};
