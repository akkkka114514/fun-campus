<template>
  <web-view :src="url" />
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

// 收银台容器：App / 小程序端用 web-view 打开渠道收银台（H5 端直接 window.open）
const url = ref('');

onLoad((options) => {
  url.value = options.url ? decodeURIComponent(options.url) : '';
});
</script>
