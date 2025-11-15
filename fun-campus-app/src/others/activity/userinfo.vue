<template>
  <view class="member px-50 pt-30">
    <view class="flex-row-center-center">
      <view class="bg-avatar">
        <tm-avatar :size="150" :round="25" img="https://picsum.photos/200/300"></tm-avatar>
      </view>
    </view>
    <view class="pa-30 flex-row-center-center">昵称</view>
  </view>
  <view class="mt-15">
    <tm-cell :margin="[0, 0]" :bottomBorder="true">
      <template v-slot:title>
        <view class="flex">
          <text class="ml-10">审核状态</text>
        </view>
      </template>
      <template v-slot:right>
        <text>审核中</text>
      </template>
    </tm-cell>
    <tm-cell :margin="[0, 0]" :bottomBorder="true">
      <template v-slot:title>
        <view class="flex">
          <text class="ml-10">姓名</text>
        </view>
      </template>
      <template v-slot:right>
        <text>1号</text>
      </template>
    </tm-cell>
    <tm-cell :margin="[0, 0]" :bottomBorder="true">
      <template v-slot:title>
        <view class="flex">
          <text class="ml-10">手机号码</text>
        </view>
      </template>
      <template v-slot:right>
        <text>18333333333</text>
      </template>
    </tm-cell>
    <tm-cell :margin="[0, 0]" :bottomBorder="true">
      <template v-slot:title>
        <view class="flex">
          <text class="ml-10">微信号</text>
        </view>
      </template>
      <template v-slot:right>
        <text>1159</text>
      </template>
    </tm-cell>
    <tm-cell :margin="[0, 0]" :bottomBorder="true">
      <template v-slot:title>
        <view class="flex">
          <text class="ml-10">报名时间</text>
        </view>
      </template>
      <template v-slot:right>
        <text>2022-20-36</text>
      </template>
    </tm-cell>
    <tm-cell :margin="[0, 0]" rightIcon="">
      <template v-slot:title>
        <view class="flex">
          <text class="ml-10">添加备注</text>
        </view>
      </template>
    </tm-cell>
    <view class="px-30 pt-10">
      <tm-input></tm-input>
      <view class="flex mt-20">
        <tm-tag type="primary" size="small" label="保存备注"></tm-tag>
        <tm-tag type="primary" size="small" label="复制全部报名信息"></tm-tag>
      </view>
    </view>
    <view class="footer pa-15 px-50">
      <view class="flex flex-between">
        <view class="">
          <tm-icon name="tmicon-clock"></tm-icon>
          <view>标记</view>
        </view>
        <view class="">
          <tm-icon name="tmicon-clock"></tm-icon>
          <view>拉黑</view>
        </view>
        <view class="">
          <tm-icon name="tmicon-clock"></tm-icon>
          <view>取消报名</view>
        </view>
        <view class="">
          <tm-icon name="tmicon-clock"></tm-icon>
          <view>审核</view>
        </view>
      </view>
    </view>
  </view>
</template>
<script lang="ts"></script>
<style lang="scss" scoped>
.member {
  background: linear-gradient(180deg, #3c8af8, #38cddd);
  color: #ffffff;
  .bg-avatar {
    background-color: #ffffff;
    border-radius: 50%;
    padding: 3rpx;
    box-shadow: 0 0 10rpx rgba(0, 0, 0, 0.3);
  }
}
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 0 10rpx rgba(0, 0, 0, 0.1);
}
</style>
