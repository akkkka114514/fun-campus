<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[30, 30]">
      <tm-cell showAvatar avatar="https://picsum.photos/200" rightText="2023-04-21 20:22" :margin="[0, 0]" :bottomBorder="true">
        <template v-slot:title>
          <view class="flex-col-top-center"><text class="mr-15">昵称</text> <tm-tag :round="10" outlined size="s" label="所有者"></tm-tag></view>
        </template>
      </tm-cell>
      <tm-cell showAvatar avatar="https://picsum.photos/200" rightText="2023-04-21 20:22" :margin="[0, 0]" :bottomBorder="true" v-for="i in 30" :key="i">
        <template v-slot:title>
          <view class="flex-col-top-center"><text class="mr-15">昵称</text> <tm-tag :round="10" outlined size="s" label="管理员"></tm-tag></view>
        </template>
      </tm-cell>
      <view class="gap"></view>
      <view class="footer flex-col-center-center py-10 px-50">
        <!-- <view class="left flex">
      <tm-button outlined :round="20" type="primary" size="large" label="转让组织" @click="add"></tm-button>
    </view> -->
        <!-- <view class="right flex ml-30"> -->
        <tm-button :round="20" type="primary" size="large" label="添加管理员" @click="openLink('teams/manager/share')"></tm-button>
        <!-- </view> -->
      </view>
    </tm-sheet>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { openLink } from '@/common/tools';
</script>
<style lang="scss" scoped>
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 0 10rpx rgba(0, 0, 0, 0.1);
  .left,
  .right {
    width: 100%;
  }
}
.gap {
  height: 150rpx;
}
</style>
