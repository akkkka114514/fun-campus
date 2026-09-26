/**
 * 活动管理 api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2025-09-04 13:41:42
 * @Copyright  akkkka114514
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const activityApi = {

  /**
   * 分页查询  @author  akkkka114514
   */
  queryPage : (param) => {
    return postRequest('/backend/activity/query', param);
  },

  /**
   * 活动详情（含时间表，编辑回显）  @author  akkkka114514
   */
  detail: (activityId) => {
    return getRequest('/backend/activity/detail', { activityId });
  },

  /**
   * 增加  @author  akkkka114514
   */
  add: (param) => {
      return postRequest('/backend/activity/add', param);
  },

  /**
   * 修改  @author  akkkka114514
   */
  update: (param) => {
      return postRequest('/backend/activity/update', param);
  },

  /**
   * 删除  @author  akkkka114514
   */
  delete: (id) => {
      return postRequest('/backend/activity/delete', id);
  },

  /**
   * 批量删除  @author  akkkka114514
   */
  batchDelete: (idList) => {
      return postRequest('/backend/activity/batchDelete', idList);
  },

};
