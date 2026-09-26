import { get, post, postRaw } from '@/utils/request';

export const activityApi = {
  // 活动详情：{ activity, schedule, enrollUsers, enrollNum, signInNum, canEnrollXxx, currentUserOrder, currentUserEnrollment }
  detail(activityId) {
    return get('/portal/activity/detail', { activityId });
  },

  // 我的报名分页查询（PageParam：pageNum + pageSize；signInStatus?true-已签到 false-未签到，activityStatus?活动状态 4-已结束；均不传查全部）
  queryMyEnrollments(form) {
    return post('/portal/activityEnrollment/queryMy', form);
  },

  // 报名（裸参；付费活动由后端返回需支付提示，fe-06 走订单流程）
  enroll(activityId) {
    return postRaw('/portal/activityEnrollment/enroll', activityId);
  },

  // 取消报名（仅免费活动、报名截止前）
  cancelEnroll(activityId) {
    return postRaw('/portal/activityEnrollment/cancel', activityId);
  },

  // 收藏 / 取消收藏（裸参）
  addFavorite(activityId) {
    return postRaw('/portal/activity/favorite/add', activityId);
  },
  removeFavorite(activityId) {
    return postRaw('/portal/activity/favorite/remove', activityId);
  },
  // 当前用户是否已收藏
  checkFavorite(activityId) {
    return get('/portal/activity/favorite/check', { activityId });
  },

  // 生成分享链接（裸参）→ { shareUrl, shareToken, expireSeconds }
  generateShare(activityId) {
    return postRaw('/portal/activity/share/generate', activityId);
  },

  // 解析分享链接/码 → activityId（免登录）
  resolveShare(token) {
    return get(`/portal/activity/share/resolve/${token}`);
  },

  // 签到/签退二维码（30 秒过期，重新请求即刷新；码内容 userId+token，由签到员扫）
  signInQrCode() {
    return get('/portal/activityEnrollment/signIn/QRCode');
  },

  // 待签到活动列表（已报名 + 未签到 + 当前处于签到窗口内）
  queryPendingSignInList() {
    return get('/portal/activityEnrollment/pendingSignIn/list');
  },

  // 待签退活动列表（已签到 + 需签退 + 未签退 + 当前处于签退窗口内）
  queryPendingSignOutList() {
    return get('/portal/activityEnrollment/pendingSignOut/list');
  },

  // 待评价活动列表（已报名 + 已签到 + 活动已结束 + 未评价）
  queryPendingEvaluations() {
    return get('/portal/activityEvaluation/pending/list');
  },

  // 待评价数量
  queryPendingEvaluationCount() {
    return get('/portal/activityEvaluation/pending/count');
  },

  // 提交评价：{ activityId, score:1-5, content? }（已结束且已签到才可评，一人一活动一条）
  submitEvaluation(form) {
    return post('/portal/activityEvaluation/submit', form);
  },

  // 评论分页查询（PageParam：activityId + pageNum + pageSize）→ PageResult（list 字段）
  queryComments(form) {
    return post('/portal/activity/comment/query', form);
  },
  // 发表评论：{ activityId, content, toCommentId? }
  addComment(form) {
    return post('/portal/activity/comment/add', form);
  },
  // 点赞 / 撤销点赞 / 删除评论（路径参数）
  likeComment(commentId) {
    return post(`/portal/activity/comment/like/${commentId}`, {});
  },
  unlikeComment(commentId) {
    return post(`/portal/activity/comment/unlike/${commentId}`, {});
  },
  deleteComment(commentId) {
    return post(`/portal/activity/comment/delete/${commentId}`, {});
  },
};
