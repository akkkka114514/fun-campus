/**
 * 活动报名订单 api 封装（管理端）
 *
 * @Author:    akkkka114514
 * @Date:      2026-09-26 10:00:00
 * @Copyright  akkkka114514
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const activityOrderApi = {

  /**
   * 订单分页查询（管理端全量）  @author  akkkka114514
   */
  queryPage: (param) => {
    return postRequest('/backend/activityOrder/queryPage', param);
  },

  /**
   * 订单详情（管理端）  @author  akkkka114514
   */
  detail: (orderNo) => {
    return getRequest(`/backend/activityOrder/detail?orderNo=${orderNo}`);
  },

  /**
   * 退款单分页查询（管理端全量）  @author  akkkka114514
   */
  refundQueryPage: (param) => {
    return postRequest('/backend/activityOrder/refundQueryPage', param);
  },

  /**
   * 退款失败重试  @author  akkkka114514
   */
  refundRetry: (refundNo) => {
    return postRequest(`/backend/activityOrder/refundRetry?refundNo=${refundNo}`);
  },

};
