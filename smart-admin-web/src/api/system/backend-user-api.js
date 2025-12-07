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
    return getRequest('/employee/queryAll');
  },
  /**
   * 后台用户管理查询
   */
  queryBackendUser: (params) => {
    return postRequest('/employee/query', params);
  },
  /**
   * 添加后台用户
   */
  addBackendUser: (params) => {
    return postRequest('/employee/add', params);
  },
  /**
   * 更新后台用户信息
   */
  updateBackendUser: (params) => {
    return postRequest('/employee/update', params);
  },
  /**
   * 更新后台用户个人中心信息
   */
  updateCenter: (params) => {
    return postRequest('/employee/update/center', params);
  },
  /**
   * 删除后台用户
   */
  deleteBackendUser: (employeeId) => {
    return getRequest(`/employee/delete/${employeeId}`);
  },
  /**
   * 批量删除后台用户
   */
  batchDeleteBackendUser: (employeeIdList) => {
    return postRequest('/employee/update/batch/delete', employeeIdList);
  },
  /**
   * 重置后台用户密码
   */
  resetPassword: (employeeId) => {
    return getRequest(`/employee/update/password/reset/${employeeId}`);
  },
  /**
   * 修改密码
   */
  updateBackendUserPassword: (param) => {
    return postEncryptRequest('/employee/update/password', param);
  },
  /**
   * 获取密码复杂度
   */
  getPasswordComplexityEnabled: () => {
    return getRequest('/employee/getPasswordComplexityEnabled');
  },
  /**
   * 更新后台用户禁用状态
   */
  updateDisabled: (employeeId) => {
    return getRequest(`/employee/update/disabled/${employeeId}`);
  },
};
