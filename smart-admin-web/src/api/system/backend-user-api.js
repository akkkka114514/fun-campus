/*
 *  后台用户
 *
 * @Author:    1024创新实验室-主任：卓大
 * @Date:      2022-09-03 21:59:15
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */

import { getRequest, postEncryptRequest, postRequest } from '/@/lib/axios';

export const backendUserApi = {
  /**
   * 查询所有后台用户 @author 卓大
   */
  queryAll: () => {
    return getRequest('/backend/backendUser/queryAll');
  },
  /**
   * 后台用户管理查询
   */
  queryBackendUser: (params) => {
    return postRequest('/backend/backendUser/query', params);
  },
  /**
   * 添加后台用户
   */
  addBackendUser: (params) => {
    return postRequest('/backend/backendUser/add', params);
  },
  /**
   * 更新后台用户信息
   */
  updateBackendUser: (params) => {
    return postRequest('/backend/backendUser/update', params);
  },
  /**
   * 更新后台用户个人中心信息
   */
  updateCenter: (params) => {
    return postRequest('/backend/backendUser/update/center', params);
  },
  /**
   * 删除后台用户（后端仅提供批量删除接口）
   */
  deleteBackendUser: (backendUserId) => {
    return postRequest('/backend/backendUser/update/batch/delete', [backendUserId]);
  },
  /**
   * 批量删除后台用户
   */
  batchDeleteBackendUser: (backendUserIdList) => {
    return postRequest('/backend/backendUser/update/batch/delete', backendUserIdList);
  },
  /**
   * 重置后台用户密码
   */
  resetPassword: (backendUserId) => {
    return getRequest(`/backend/backendUser/update/password/reset/${backendUserId}`);
  },
  /**
   * 修改密码
   */
  updateBackendUserPassword: (param) => {
    return postEncryptRequest('/backend/backendUser/update/password', param);
  },
  /**
   * 获取密码复杂度
   */
  getPasswordComplexityEnabled: () => {
    return getRequest('/backend/backendUser/getPasswordComplexityEnabled');
  },
  /**
   * 更新后台用户禁用状态
   */
  updateDisabled: (backendUserId) => {
    return getRequest(`/backend/backendUser/update/disabled/${backendUserId}`);
  },
};
