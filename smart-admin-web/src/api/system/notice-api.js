/*
 * @Description: 公告信息、企业动态
 * @version:
 * @Author: zhuoda
 * @Date: 2022-08-16 20:34:36
 */
import { postRequest, getRequest } from '/src/lib/axios';

export const noticeApi = {
  // ---------------- 通知公告类型 -----------------------

  // 通知公告类型-获取全部 @author zhuoda
  getAllNoticeTypeList() {
    return getRequest('backend/noticeType/getAll');
  },

  // 通知公告类型-添加 @author zhuoda
  addNoticeType(name) {
    return getRequest(`backend/noticeType/add/${name}`);
  },

  // 通知公告类型-修改 @author zhuoda
  updateNoticeType(noticeTypeId, name) {
    return getRequest(`backend/noticeType/update/${noticeTypeId}/${name}`);
  },
  // 通知公告类型-删除 @author zhuoda
  deleteNoticeType(noticeTypeId) {
    return getRequest(`backend/noticeType/delete/${noticeTypeId}`);
  },

  // ---------------- 通知公告管理 -----------------------

  // 通知公告-分页查询 @author zhuoda
  queryNotice(param) {
    return postRequest('backend/notice/query', param);
  },

  // 通知公告-添加 @author zhuoda
  addNotice(param) {
    return postRequest('backend/notice/add', param);
  },

  // 通知公告-更新 @author zhuoda
  updateNotice(param) {
    return postRequest('backend/notice/update', param);
  },

  // 通知公告-删除 @author zhuoda
  deleteNotice(noticeId) {
    return getRequest(`backend/notice/delete/${noticeId}`);
  },

  // 通知公告-更新详情 @author zhuoda
  getUpdateNoticeInfo(noticeId) {
    return getRequest(`backend/notice/getUpdateVO/${noticeId}`);
  },

  // --------------------- 【后台用户】查看 通知公告 -------------------------

  // 通知公告-后台用户-查看详情 @author zhuoda
  view(noticeId) {
    return getRequest(`backend/notice/backend-user/view/${noticeId}`);
  },

  // 通知公告-后台用户-查询 @author zhuoda
  queryBackendUserNotice(param) {
    return postRequest('backend/notice/backend-user/query', param);
  },

  // 【后台用户】通知公告-查询 查看记录 @author zhuoda
  queryViewRecord(param) {
    return postRequest('backend/notice/backend-user/queryViewRecord', param);
  },
};
