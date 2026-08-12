/**
 * 活动报名关系 api 封装
 *
 * @Author:    akkkka114514
 * @Date:      2025-10-02 13:54:42
 * @Copyright  akkkka114514
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const activityEnrollmentApi = {

  /**
   * 分页查询  @author  akkkka114514
   */
  queryPage : (param) => {
    return postRequest('/activityEnrollment/queryPage', param);
  },

  /**
   * 增加  @author  akkkka114514
   */
  add: (param) => {
      return postRequest('/activityEnrollment/add', param);
  },

  /**
   * 修改  @author  akkkka114514
   */
  update: (param) => {
      return postRequest('/activityEnrollment/update', param);
  },


  /**
   * 删除  @author  akkkka114514
   */
  delete: (id) => {
      return getRequest(`/activityEnrollment/delete/${id}`);
  },

  /**
   * 批量删除  @author  akkkka114514
   */
  batchDelete: (idList) => {
      return postRequest('/activityEnrollment/batchDelete', idList);
  },

  /**
   * 报名活动  @author  akkkka114514
   */
  enroll: (activityId) => {
      return postRequest('/activityEnrollment/enroll', activityId);
  },

  /**
   * 生成签到二维码  @author  akkkka114514
   */
  signInQRCode: () => {
      return getRequest('/activityEnrollment/signIn/QRCode');
  },

  /**
   * 扫码签到  @author  akkkka114514
   */
  signInByQRCode: (param) => {
      return postRequest('/activityEnrollment/signIn/byQRCode', param);
  },

  /**
   * 扫码签退  @author  akkkka114514
   */
  signOutByQRCode: (param) => {
      return postRequest('/activityEnrollment/signOut/byQRCode', param);
  },

};
