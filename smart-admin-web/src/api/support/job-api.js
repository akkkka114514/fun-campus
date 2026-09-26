/*
 * job api
 *
 * @Author:    huke
 * @Date:      2024/06/25
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const jobApi = {
  // 分页查询 @huke
  queryJob: (param) => {
    return postRequest('/backend/job/query', param);
  },
  // 定时任务-查询详情 @huke
  queryJobInfo: (param) => {
    return getRequest(`/backend/job/${param}`);
  },
  // 执行任务 @huke
  executeJob: (param) => {
    return postRequest('/backend/job/execute', param);
  },
  // 定时任务-新增-任务信息 @huke
  addJob: (param) => {
    return postRequest('/backend/job/add', param);
  },
  // 定时任务-更新-任务信息 @huke
  updateJob: (param) => {
    return postRequest('/backend/job/update', param);
  },
  // 定时任务-更新-开启状态 @huke
  updateJobEnabled: (param) => {
    return postRequest('/backend/job/update/enabled', param);
  },
  // 定时任务-执行记录-分页查询 @huke
  queryJobLog: (param) => {
    return postRequest('/backend/job/log/query', param);
  },
  // 定时任务-删除  @zhuoda
  deleteJob: (param) => {
    return getRequest(`/backend/job/delete?jobId=${param}`);
  },
};
