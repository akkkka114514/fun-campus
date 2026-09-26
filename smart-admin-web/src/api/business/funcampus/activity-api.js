/**
 * 活动管理 api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2025-09-04 13:41:42
 * @Copyright  akkkka114514
 */
import { postRequest } from '/@/lib/axios';

export const activityApi = {

  /**
   * 分页查询  @author  akkkka114514
   */
  queryPage : (param) => {
    return postRequest('/backend/activity/query', param);
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
