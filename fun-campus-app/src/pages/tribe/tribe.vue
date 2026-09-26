<template>
  <view class="placeholder">
    <text class="placeholder-title">部落</text>
    <text class="placeholder-desc">部落功能开发中，敬请期待</text>
  </view>
</template>

<script setup></script>

<style lang="scss" scoped>
.placeholder {
  min-height: 100vh;
  background: $fc-bg;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.placeholder-title {
  font-size: 36rpx;
  font-weight: 600;
  color: $fc-text-main;
}

.placeholder-desc {
  margin-top: 16rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
}
</style>
