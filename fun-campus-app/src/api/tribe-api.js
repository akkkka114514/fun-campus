import { get, post } from '@/utils/request';

export const tribeApi = {
  // 本校部落分页：{ pageNum, pageSize, keyword?, sortType? 1-默认 2-热度（成员数）降序 }
  queryPage(form) {
    return post('/portal/tribe/queryPage', form);
  },

  // 部落详情：{ id, name, icon, description, belongTo, presidentName, memberNum, joinedFlag, myApplicationStatus }
  detail(tribeId) {
    return get('/portal/tribe/detail', { tribeId });
  },

  // 成员分页：{ tribeId, pageNum, pageSize, keyword? }
  memberPage(form) {
    return post('/portal/tribe/member/queryPage', form);
  },

  // 部落发起的活动分页：{ tribeId, pageNum, pageSize }
  activityPage(form) {
    return post('/portal/tribe/activity/queryPage', form);
  },

  // 申请加入：{ tribeId, reason }（仅本校部落；已加入/已有待审核申请会被后端拦截）
  apply(form) {
    return post('/portal/tribe/application/apply', form);
  },

  // 我的加入申请列表（含审核状态与审核意见）
  myApplicationList() {
    return get('/portal/tribe/application/myList');
  },
};
