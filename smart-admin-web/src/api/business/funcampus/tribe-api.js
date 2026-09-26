/**
 * 部落 api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2026-01-15 13:27:15
 * @Copyright  akkkka114514
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const tribeApi = {

  /**
   * 分页查询  @author  akkkka114514
   */
  queryPage : (param) => {
    return postRequest('/backend/tribe/queryPage', param);
  },

  /**
   * 简易列表（按学校+关键字，供下拉选择）  @author  akkkka114514
   */
  simpleList: (schoolId, keyword) => {
    return getRequest('/backend/tribe/simpleList', { schoolId, keyword });
  },

  /**
   * 增加  @author  akkkka114514
   */
  add: (param) => {
      return postRequest('/backend/tribe/add', param);
  },

  /**
   * 修改  @author  akkkka114514
   */
  update: (param) => {
      return postRequest('/backend/tribe/update', param);
  },


  /**
   * 删除  @author  akkkka114514
   */
  delete: (id) => {
      return getRequest(`/backend/tribe/delete/${id}`);
  },

  /**
   * 批量删除  @author  akkkka114514
   */
  batchDelete: (idList) => {
      return postRequest('/backend/tribe/batchDelete', idList);
  },

};
