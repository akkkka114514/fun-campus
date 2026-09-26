import { get, post } from '@/utils/request';

export const messageApi = {
  // 我的消息分页：{ pageNum, pageSize, readFlag?, searchWord? } → PageResult（list 字段）
  queryMy(form) {
    return post('/portal/message/queryMyMessage', form);
  },

  // 我的未读消息数量 → Long
  getUnreadCount() {
    return get('/portal/message/getUnreadCount');
  },

  // 标记我的消息为已读（GET 路径参数）
  read(messageId) {
    return get(`/portal/message/read/${messageId}`);
  },
};

// 消息 tab 在 tabBar 中的下标（首页0 / 部落1 / 消息2 / 我的3）
const MESSAGE_TAB_INDEX = 2;

/**
 * 刷新消息 tabBar 未读角标（tab 页 onShow 里调用；非 tab 页/失败时静默）
 * @returns {Promise<number>} 未读数量
 */
export async function refreshMessageBadge() {
  try {
    const { data } = await messageApi.getUnreadCount();
    const count = Number(data) || 0;
    if (count > 0) {
      uni.setTabBarBadge({ index: MESSAGE_TAB_INDEX, text: count > 99 ? '99+' : String(count) });
    } else {
      uni.removeTabBarBadge({ index: MESSAGE_TAB_INDEX });
    }
    return count;
  } catch (e) {
    return 0;
  }
}
