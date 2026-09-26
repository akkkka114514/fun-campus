/**
 * 学分认定申请（管理端审核） api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2026-09-26 10:00:00
 * @Copyright  akkkka114514
 */
import { postRequest } from '/@/lib/axios';

export const creditApplicationApi = {

  /**
   * 指派给我的申请分页查询  @author  akkkka114514
   */
  queryPage: (param) => {
    return postRequest('/backend/creditApplication/queryPage', param);
  },

  /**
   * 审核申请（通过/驳回）  @author  akkkka114514
   */
  review: (param) => {
    return postRequest('/backend/creditApplication/review', param);
  },

};
