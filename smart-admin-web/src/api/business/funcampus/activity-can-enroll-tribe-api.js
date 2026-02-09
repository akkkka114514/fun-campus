/**
 * 活动能报名的部落 api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2026-01-15 13:18:01
 * @Copyright  akkkka114514
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const activityCanEnrollTribeApi = {

  /**
   * 分页查询  @author  akkkka114514
   */
  queryPage : (param) => {
    return postRequest('/activityCanEnrollTribe/queryPage', param);
  },

  /**
   * 增加  @author  akkkka114514
   */
  add: (param) => {
      return postRequest('/activityCanEnrollTribe/add', param);
  },

  /**
   * 修改  @author  akkkka114514
   */
  update: (param) => {
      return postRequest('/activityCanEnrollTribe/update', param);
  },


  /**
   * 删除  @author  akkkka114514
   */
  delete: (id) => {
      return getRequest(`/activityCanEnrollTribe/delete/${id}`);
  },

  /**
   * 批量删除  @author  akkkka114514
   */
  batchDelete: (idList) => {
      return postRequest('/activityCanEnrollTribe/batchDelete', idList);
  },

};
