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
    return postRequest('/backend/activityEnrollment/queryPage', param);
  },

  /**
   * 删除  @author  akkkka114514
   */
  delete: (activityId, userId) => {
      return getRequest(`/backend/activityEnrollment/delete/${activityId}/${userId}`);
  },

  /**
   * 批量删除  @author  akkkka114514
   */
  batchDelete: (idList) => {
      return postRequest('/backend/activityEnrollment/batchDelete', idList);
  },

  /**
   * 报名活动  @author  akkkka114514
   */
  enroll: (activityId) => {
      return postRequest('/portal/activityEnrollment/enroll', activityId);
  },

  /**
   * 生成签到/签退二维码（含UUID Token，30秒过期可刷新）  @author  akkkka114514
   */
  signInQRCode: () => {
      return getRequest('/portal/activityEnrollment/signIn/QRCode');
  },

  /**
   * 扫码签到（须处于活动签到时间窗口内）  @author  akkkka114514
   */
  signInByQRCode: (param) => {
      return postRequest('/portal/activityEnrollment/signIn/byQRCode', param);
  },

  /**
   * 扫码签退（须活动需要签退且在签退时间窗口内）  @author  akkkka114514
   */
  signOutByQRCode: (param) => {
      return postRequest('/portal/activityEnrollment/signOut/byQRCode', param);
  },

};
