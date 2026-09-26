import { defineConfig } from 'vite';
import uni from '@dcloudio/vite-plugin-uni';

export default defineConfig({
  plugins: [uni()],
  server: {
    // 强制监听 IPv4，否则部分环境 localhost 只绑 [::1] 导致浏览器连接被拒
    host: '0.0.0.0',
    port: 5173,
  },
  css: {
    preprocessorOptions: {
      scss: {
        // uview-plus 内部还在用旧式 @import / legacy sass API，屏蔽弃用告警避免刷屏
        silenceDeprecations: ['legacy-js-api', 'import', 'global-builtin', 'color-functions'],
      },
    },
  },
});
