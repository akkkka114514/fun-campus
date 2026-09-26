import { get, post, postRaw } from '@/utils/request';

/**
 * 活动订单 / 支付（fe-06 付费链路）
 * 链路：create 下单锁座 → 打开 cashierUrl 收银台 → 渠道异步回调 → 前端轮询 detail 回查
 */
export const orderApi = {
  // 创建订单（裸参 activityId）→ { orderNo, amountFen, expireTime, cashierUrl }
  create(activityId) {
    return postRaw('/portal/activityOrder/create', activityId);
  },

  // 重新获取支付参数（关掉收银台后再次拉起时调用）
  prepay(orderNo) {
    return get('/portal/activityOrder/prepay', { orderNo });
  },

  // 订单详情（轮询回查）→ ActivityOrderVO
  detail(orderNo) {
    return get('/portal/activityOrder/detail', { orderNo });
  },

  // 取消待支付订单（后端为 @RequestParam，走 query 参数）
  cancel(orderNo) {
    return post('/portal/activityOrder/cancel?orderNo=' + encodeURIComponent(orderNo), {});
  },
};

export default orderApi;
