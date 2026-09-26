<!--
  *  后台用户 管理
  *
  * @Author:    1024创新实验室-主任：卓大
  * @Date:      2022-08-08 20:46:18
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
-->
<template>
  <div class="height100">
    <BackendUserList />
  </div>
</template>
<script setup>
  import BackendUserList from './components/backend-user-list/index.vue';
</script>
<style scoped lang="less">
  .height100 {
    height: 100%;
  }
</style>
