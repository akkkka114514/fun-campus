/*
 *  员工
 *
 * @Author:    1024创新实验室-主任：卓大
 * @Date:      2022-09-03 21:59:15
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */

import { getRequest, postEncryptRequest, postRequest } from '/@/lib/axios';

export const employeeApi = {
  /**
   * 查询所有员工 @author 卓大
   */
  queryAll: () => {
    return getRequest('/employee/queryAll');
  },
  /**
   * 员工管理查询
   */
  queryBackendUser: (params) => {
    return postRequest('/employee/query', params);
  },
  /**
   * 添加员工
   */
  addBackendUser: (params) => {
    return postRequest('/employee/add', params);
  },
  /**
   * 更新员工信息
   */
  updateBackendUser: (params) => {
    return postRequest('/employee/update', params);
  },
  /**
   * 更新员工个人中心信息
   */
  updateCenter: (params) => {
    return postRequest('/employee/update/center', params);
  },
  /**
   * 更新登录人头像
   */
  updateAvatar: (params) => {
    return postRequest('/employee/update/avatar', params);
  },
  /**
   * 删除员工
   */
  deleteBackendUser: (employeeId) => {
    return getRequest(`/employee/delete/${employeeId}`);
  },
  /**
   * 批量删除员工
   */
  batchDeleteBackendUser: (employeeIdList) => {
    return postRequest('/employee/update/batch/delete', employeeIdList);
  },
  /**
   * 批量调整员工部门
   */
  batchUpdateDepartmentBackendUser: (updateParam) => {
    return postRequest('/employee/update/batch/department', updateParam);
  },
  /**
   * 重置员工密码
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
   * 更新员工禁用状态
   */
  updateDisabled: (employeeId) => {
    return getRequest(`/employee/update/disabled/${employeeId}`);
  },
  /**
   * 查询员工-根据部门id
   */
  queryBackendUserByDeptId: (departmentId) => {
    return getRequest(`/employee/getAllBackendUserByDepartmentId/${departmentId}`);
  },
};
