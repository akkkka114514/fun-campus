import { Http } from '../tmui/tool/lib/lwu-request';

// 创建全局Http实例并配置baseUrl
export const http = new Http();

// 设置全局配置
const config = http['config'];
config.baseUrl = {
  dev: 'http://localhost:1024',
  pro: 'http://localhost:1024'
};

// 导出配置好的请求方法
export const request = {
  get: (url: string, data: object = {}, options: any = {}) => Http.get(url, data, options),
  // 确保POST请求正确传递参数和选项
  post: (url: string, data: object = {}, options: any = {}) => http.post(url, data, options),
  put: (url: string, data: object = {}, options: any = {}) => http.put(url, data, options),
  delete: (url: string, data: object = {}, options: any = {}) => http.delete(url, data, options),
  request: (url: string, data: object = {}, options: any = {}) => Http.request(url, data, options)
};