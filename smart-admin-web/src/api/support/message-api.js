import { getRequest, postRequest } from '/@/lib/axios';

export const messageApi = {
  // 通知消息-分页查询我收到的消息
  queryMessage: (param) => {
    return postRequest('/backend/message/queryMyMessage', param);
  },
  // 通知消息-查询我的未读消息数
  queryUnreadCount: () => {
    return getRequest('/backend/message/getUnreadCount');
  },
  // 通知消息-标记我的消息为已读
  updateReadFlag: (messageId) => {
    return getRequest(`/backend/message/read/${messageId}`);
  },

  //通知消息-分页查询（管理端）   @author 卓大
  queryAdminMessage: (param) => {
    return postRequest('/backend/message/query', param);
  },

  //通知消息-新建（管理端）  @author 卓大
  sendMessages: (param) => {
    return postRequest('/backend/message/sendMessages', param);
  },

  //通知消息-删除（管理端） @author 卓大
  deleteMessage: (messageId) => {
    return getRequest(`/backend/message/delete/${messageId}`);
  },

  //通知消息-分页查询接收人（管理端，支持后台用户/前台用户）
  queryReceiverUser: (param) => {
    return postRequest('/backend/message/receiver/query', param);
  },
};
