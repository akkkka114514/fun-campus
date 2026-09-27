import { get, post } from '@/utils/request';

export const noticeApi = {
  // 校内公告分页：{ pageNum, pageSize, noticeTypeId?, keywords? } → PageResult（list 字段）
  query(form) {
    return post('/portal/notice/queryNotice', form);
  },

  // 公告详情（服务端同时累加页面浏览量）→ PortalNoticeDetailVO
  detail(noticeId) {
    return get(`/portal/notice/detail/${noticeId}`);
  },

  // 公告分类列表 → [{ noticeTypeId, noticeTypeName }]
  typeList() {
    return get('/portal/notice/typeList');
  },
};
