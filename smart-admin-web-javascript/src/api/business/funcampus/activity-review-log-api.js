/**
 * 活动审核日志 api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2026-01-10 18:34:56
 * @Copyright  akkkka114514
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const activityReviewLogApi = {

  /**
   * 分页查询  @author  akkkka114514
   */
  queryPage : (param) => {
    return postRequest('/activityReviewLog/queryPage', param);
  },

  /**
   * 增加  @author  akkkka114514
   */
  add: (param) => {
      return postRequest('/activityReviewLog/add', param);
  },

  /**
   * 修改  @author  akkkka114514
   */
  update: (param) => {
      return postRequest('/activityReviewLog/update', param);
  },



};
