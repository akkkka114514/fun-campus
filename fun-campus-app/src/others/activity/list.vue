<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]">
      <tm-table :table-data="demoData" :stripe="true" :showHeader="true" @rowClick="onClick" :width="690"></tm-table>
    </tm-sheet>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
const demoData = ref({
  fields: {
    columns: ['num', 'nickname', 'name', 'mobile', 'wechat_id', 'status'],
  },
  header: [
    {
      field: 'num',
      name: '序号',
    },
    {
      field: 'nickname',
      name: '昵称',
    },
    {
      field: 'name',
      name: '姓名',
    },
    {
      field: 'mobile',
      name: '手机',
    },
    {
      field: 'wechat_id',
      name: '微信号',
    },
    {
      field: 'status',
      name: '状态',
    },
  ],
  data: [
    {
      num: '1',
      nickname: '昵称',
      name: '姓名',
      mobile: '18333333333',
      wechat_id: '微信号',
      status: '待审核',
    },
    {
      num: '1',
      nickname: '昵称',
      name: '姓名',
      mobile: '18333333333',
      wechat_id: '微信号',
      status: '待审核',
    },
    {
      num: '1',
      nickname: '昵称',
      name: '姓名',
      mobile: '18333333333',
      wechat_id: '微信号',
      status: '待审核',
    },
  ],
});

function onClick(row: number, col: number) {
  console.log(row, col);
}
</script>
