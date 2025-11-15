<template>
  <tm-app color="white">
    <tm-sheet :margin="[0, 0]" :padding="[30, 30]">
      <view>
        <tm-text label="方式一：点击右上角 ... 转发给被邀请人"></tm-text>
        <tm-divider></tm-divider>
        <tm-text label="方式一：请向被邀请人展示下方二维码"></tm-text>
      </view>
      <view class="box">
        <view class="inner">
          <view class="pt-30 text-align-center"> 扫码成为管理员 </view>
          <tm-divider></tm-divider>
          <view class="code px-50">
            <image src="../../static/logo.png" mode="aspectFit"></image>
          </view>
          <view class="pb-50 pt-20 text-align-center">有效时间：30分钟</view>
        </view>
      </view>
      <view class="pt-30 footer">
        <tm-button :round="20" block type="primary">保存到相册</tm-button>
      </view>
    </tm-sheet>
  </tm-app>
</template>
<script lang="ts" setup></script>
<style lang="scss" scoped>
.box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 50rpx;
  .inner {
    border: 1px solid #ccc;
    .code {
      width: 450rpx;
      height: 400rpx;
      image {
        width: 100%;
        height: 100%;
      }
    }
  }
}
.footer {
  padding-left: 120rpx;
  padding-right: 120rpx;
}
</style>
