/**
 * 活动签到管理员 api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2026-02-18 15:10:19
 * @Copyright  akkkka114514
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const activitySigninManagerApi = {

  /**
   * 分页查询  @author  akkkka114514
   */
  queryPage : (param) => {
    return postRequest('/activitySigninManager/queryPage', param);
  },

  /**
   * 增加  @author  akkkka114514
   */
  add: (param) => {
      return postRequest('/activitySigninManager/add', param);
  },

  /**
   * 修改  @author  akkkka114514
   */
  update: (param) => {
      return postRequest('/activitySigninManager/update', param);
  },


  /**
   * 删除  @author  akkkka114514
   */
  delete: (id) => {
      return getRequest(`/activitySigninManager/delete/${id}`);
  },

  /**
   * 批量删除  @author  akkkka114514
   */
  batchDelete: (idList) => {
      return postRequest('/activitySigninManager/batchDelete', idList);
  },

};
