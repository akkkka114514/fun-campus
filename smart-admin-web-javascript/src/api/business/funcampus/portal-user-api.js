/**
 * 前端用户 api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2025-10-09 16:29:35
 * @Copyright  akkkka114514
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const portalUserApi = {

  /**
   * 分页查询  @author  akkkka114514
   */
  queryPage : (param) => {
    return postRequest('/portalUser/queryPage', param);
  },

  /**
   * 增加  @author  akkkka114514
   */
  add: (param) => {
      return postRequest('/portalUser/add', param);
  },

  /**
   * 修改  @author  akkkka114514
   */
  update: (param) => {
      return postRequest('/portalUser/update', param);
  },


  /**
   * 删除  @author  akkkka114514
   */
  delete: (id) => {
      return getRequest(`/portalUser/delete/${id}`);
  },

  /**
   * 批量删除  @author  akkkka114514
   */
  batchDelete: (idList) => {
      return postRequest('/portalUser/batchDelete', idList);
  },

};
