<template>
  <view class="member px-50 pt-30">
    <view class="flex flex-col-top-center">
      <tm-avatar :size="150" :round="25" :img="userStore.userInfo.avatar || appStore.app_logo"></tm-avatar>
      <view class="ml-30 flex-1 flex-row-center-between">
        <template v-if="userStore.isLogin">
          <view class="flex-1" @click="openLink('pages/user/edit')">
            <view class="text-size-xl">{{ userStore.userInfo.nickname }}</view>
            <view class="mt-15">{{ userStore.userInfo.mobile }}</view>
          </view>
        </template>
        <template v-else>
          <view class="flex-1" @click="openLink('pages/login/login')">立即登录</view>
        </template>
		<view>
			<tm-icon :font-size="30" color="#ffffff" name="tmicon-angle-right"></tm-icon>
		</view>
      </view>
    </view>
    <view class="flex flex-between text-align-center py-50">
      <view>
        <view class="number">0</view>
        <view class="mt-10">参与活动数</view>
      </view>
      <view>
        <view class="number">0</view>
        <view class="mt-10">发起活动数</view>
      </view>
      <view>
        <view class="number">0</view>
        <view class="mt-10">管理组织数</view>
      </view>
    </view>
  </view>
  <view class="pa-30">
    <tm-cell :margin="[0, 0]" :bottomBorder="true" @click="openLink('others/activity/activity')">
      <template v-slot:title>
        <view class="flex-col-top-center">
          <tm-icon name="tmicon-flag" :font-size="28" color="#555050"></tm-icon>
          <text class="ml-15">我的活动</text>
        </view>
      </template>
    </tm-cell>
    <tm-cell :margin="[0, 0]" :bottomBorder="true" @click="openLink('teams/index/index')">
      <template v-slot:title>
        <view class="flex-col-top-center">
          <tm-icon name="tmicon-layergroup-fill" :font-size="28" color="#555050"></tm-icon>
          <text class="ml-15">我的组织</text>
        </view>
      </template>
    </tm-cell>
    <tm-cell :margin="[0, 0]" :bottomBorder="true" @click="openLink('others/follow/follow')">
      <template v-slot:title>
        <view class="flex-col-top-center">
          <tm-icon name="tmicon-md-heart-empty" :font-size="28" color="#555050"></tm-icon>
          <text class="ml-15">我的关注</text>
        </view>
      </template>
    </tm-cell>
    <!-- <tm-cell :margin="[0, 0]" :bottomBorder="true">
      <template v-slot:title>
        <view class="flex">
          <tm-icon name="tmicon-renminbi3" size="30"></tm-icon>
          <text class="ml-15">支付订单</text>
        </view>
      </template>
    </tm-cell> -->
    <tm-cell :margin="[0, 0]" :bottomBorder="true" @click="openLink('others/help/help')">
      <template v-slot:title>
        <view class="flex-col-top-center">
          <tm-icon name="tmicon-question-circle" :font-size="28" color="#555050"></tm-icon>
          <text class="ml-15">帮助中心</text>
        </view>
      </template>
    </tm-cell>
    <tm-cell :margin="[0, 0]" @click="callPhone('18333333333')">
      <template v-slot:title>
        <view class="flex-col-top-center">
          <tm-icon name="tmicon-headset" :font-size="28" color="#555050"></tm-icon>
          <text class="ml-15">联系客服</text>
        </view>
      </template>
    </tm-cell>
	<tm-cell :margin="[0, 0]" @click="userStore.logout">
      <template v-slot:title>
        <view class="flex-col-top-center">
          <tm-icon name="tmicon-poweroff" :font-size="28" color="#555050"></tm-icon>
          <text class="ml-15">退出登录</text>
        </view>
      </template>
    </tm-cell>
  </view>
</template>
<script lang="ts" setup>
import { openLink } from '@/common/tools';
import { callPhone } from '@/tmui/tool/function/util';
import { useUserStore } from '@/stores/user';
import { useAppStore } from '@/stores/app';
const appStore = useAppStore();
const userStore = useUserStore();
</script>
<style lang="scss" scoped>
.member {
  background: linear-gradient(180deg, #3c8af8, #38cddd);
  color: #ffffff;

  .number {
    font-size: 38rpx;
  }
}
</style>
