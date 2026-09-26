/**
 * 参与部落的用户 api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2026-02-18 15:42:50
 * @Copyright  akkkka114514
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const tribeUserApi = {

  /**
   * 分页查询  @author  akkkka114514
   */
  queryPage : (param) => {
    return postRequest('/backend/tribeUser/queryPage', param);
  },

  /**
   * 增加  @author  akkkka114514
   */
  add: (param) => {
      return postRequest('/backend/tribeUser/add', param);
  },

  /**
   * 修改  @author  akkkka114514
   */
  update: (param) => {
      return postRequest('/backend/tribeUser/update', param);
  },


  /**
   * 删除  @author  akkkka114514
   */
  delete: (id) => {
      return getRequest(`/backend/tribeUser/delete/${id}`);
  },

  /**
   * 批量删除  @author  akkkka114514
   */
  batchDelete: (idList) => {
      return postRequest('/backend/tribeUser/batchDelete', idList);
  },

};
