import { get, post, postRaw } from '@/utils/request';

export const creditApi = {
  // 提交申请：{ title, semester, content?, imageList?: [fileKey ≤9], reviewUserId }
  apply(form) {
    return post('/portal/creditApplication/apply', form);
  },

  // 编辑申请（仅待审核）：apply 字段 + id
  update(form) {
    return post('/portal/creditApplication/update', form);
  },

  // 删除申请（仅待审核，裸参 id）
  remove(id) {
    return postRaw('/portal/creditApplication/delete', id);
  },

  // 申请详情（仅本人）
  detail(id) {
    return get(`/portal/creditApplication/detail/${id}`);
  },

  // 我的申请列表（status? 0-待审核 1-已通过 2-已驳回，不传查全部）
  myList(status) {
    return get('/portal/creditApplication/myList', status == null ? {} : { status });
  },

  // 审核人候选列表（本校可审核用户；返回 { id, username, organizationId, organizationName }）
  reviewerList() {
    return get('/portal/creditApplication/reviewer/query');
  },
};
