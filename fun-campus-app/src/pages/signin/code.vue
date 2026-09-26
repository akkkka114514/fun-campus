<template>
  <view class="code-page">
    <!-- 二维码卡片 -->
    <view class="code-card">
      <view v-if="qrCodeImage" class="qr-wrap" @click="fetchCode">
        <image class="qr-image" :src="qrCodeImage" mode="aspectFit" />
      </view>
      <view v-else class="qr-placeholder" @click="fetchCode">
        <text class="qr-placeholder-text">{{ loading ? '生成中...' : '生成失败，点击重试' }}</text>
      </view>

      <text class="code-tip">请向签到员出示此二维码</text>

      <view class="countdown-row">
        <text class="countdown-text">
          {{ countdown > 0 ? countdown + ' 秒后自动刷新' : loading ? '正在刷新...' : '已暂停刷新' }}
        </text>
        <text class="refresh-btn" @click="fetchCode">刷新</text>
      </view>
    </view>

    <!-- 使用说明 -->
    <view class="code-note">
      <text class="note-title">使用说明</text>
      <text class="note-item">· 二维码 30 秒有效，到期自动更新</text>
      <text class="note-item">· 签到或签退成功后，此码自动作废</text>
      <text class="note-item">· 一人一码，请勿截图分享给他人</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onHide, onUnload } from '@dcloudio/uni-app';
import { activityApi } from '@/api/activity-api';
import { getToken } from '@/utils/auth';

const qrCodeImage = ref('');
const loading = ref(false);
const countdown = ref(0);

let countdownTimer = null;

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  // 每次进入重新生成（旧码可能已随离开的这段时间过期）
  fetchCode();
});

onHide(stopCountdown);
onUnload(stopCountdown);

async function fetchCode() {
  if (loading.value) {
    return;
  }
  loading.value = true;
  stopCountdown();
  try {
    const { data } = await activityApi.signInQrCode();
    qrCodeImage.value = data.qrCodeImage;
    // 后端固定 30 秒，取返回值为准
    startCountdown(Number(data.expireSeconds) || 30);
  } catch (e) {
    // 失败原因由请求层 toast；不自动循环重试，等用户点击或手动刷新
    countdown.value = 0;
  } finally {
    loading.value = false;
  }
}

function startCountdown(seconds) {
  countdown.value = seconds;
  countdownTimer = setInterval(() => {
    countdown.value -= 1;
    if (countdown.value <= 0) {
      // 到期自动刷新（覆盖旧 token，旧码立即失效）
      fetchCode();
    }
  }, 1000);
}

function stopCountdown() {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
}
</script>

<style lang="scss" scoped>
.code-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 48rpx 32rpx;
  box-sizing: border-box;
}

.code-card {
  background: $fc-card-bg;
  border-radius: 24rpx;
  padding: 64rpx 32rpx 48rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.qr-wrap {
  width: 560rpx;
  height: 560rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx solid #f0f1f3;
  border-radius: 16rpx;
}

.qr-image {
  width: 520rpx;
  height: 520rpx;
}

.qr-placeholder {
  width: 560rpx;
  height: 560rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx dashed #d9dce1;
  border-radius: 16rpx;
}

.qr-placeholder-text {
  font-size: 28rpx;
  color: $fc-text-sub;
}

.code-tip {
  margin-top: 40rpx;
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-text-main;
}

.countdown-row {
  margin-top: 20rpx;
  display: flex;
  align-items: center;
}

.countdown-text {
  font-size: 26rpx;
  color: $fc-text-sub;
}

.refresh-btn {
  margin-left: 24rpx;
  font-size: 26rpx;
  color: $fc-primary;
}

.code-note {
  background: $fc-card-bg;
  border-radius: 20rpx;
  margin-top: 24rpx;
  padding: 32rpx;
  display: flex;
  flex-direction: column;
}

.note-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $fc-text-main;
  margin-bottom: 16rpx;
}

.note-item {
  font-size: 26rpx;
  color: $fc-text-sub;
  line-height: 48rpx;
}
</style>
