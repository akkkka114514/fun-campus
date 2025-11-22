import { createSSRApp } from 'vue';
import * as Pinia from 'pinia';
import tmui from './tmui';
import App from './App.vue';
// 引入并配置全局API前缀
import { http } from './common/config';

export function createApp() {
  const app = createSSRApp(App);
  app.use(tmui, {} as Tmui.tmuiConfig);
  
  // 在 tmui 初始化之后再设置 fetch
  uni.$tm.fetch = http;
  
  return {
    app,
    Pinia,
  };
}