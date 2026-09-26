<template>
  <view class="eval-page">
    <view class="eval-card">
      <text class="eval-title">{{ activityTitle }}</text>

      <view class="star-row">
        <text
          v-for="n in 5"
          :key="n"
          class="star"
          :class="{ starActive: n <= score }"
          @click="score = n"
          >★</text
        >
      </view>
      <text class="star-tip">{{ scoreText }}</text>

      <textarea
        v-model="content"
        class="eval-input"
        placeholder="说说你的活动体验（选填，最多 1000 字）"
        maxlength="1000"
      />

      <view class="submit-btn" :class="{ 'submit-disabled': submitting }" @click="submit">
        {{ submitting ? '提交中...' : '提交评价' }}
      </view>
    </view>

    <view class="eval-note">活动结束后可评价，每人每活动仅可评价一次</view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { activityApi } from '@/api/activity-api';
import { getToken } from '@/utils/auth';

const activityId = ref(null);
const activityTitle = ref('活动评价');
const score = ref(5);
const content = ref('');
const submitting = ref(false);

const scoreText = computed(() => {
  const map = { 1: '很差', 2: '较差', 3: '一般', 4: '满意', 5: '非常满意' };
  return map[score.value] || '';
});

onLoad((options = {}) => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  activityId.value = Number(options.activityId) || null;
  if (options.title) {
    activityTitle.value = decodeURIComponent(options.title);
  }
});

async function submit() {
  if (!activityId.value) {
    uni.showToast({ title: '活动参数缺失', icon: 'none' });
    return;
  }
  if (!score.value) {
    uni.showToast({ title: '请先选择评分', icon: 'none' });
    return;
  }
  if (submitting.value) {
    return;
  }
  submitting.value = true;
  try {
    await activityApi.submitEvaluation({
      activityId: activityId.value,
      score: score.value,
      content: content.value.trim() || null,
    });
    uni.showToast({ title: '评价成功', icon: 'success' });
    setTimeout(() => uni.navigateBack(), 900);
  } catch (e) {
    // 失败原因由请求层统一 toast
  } finally {
    submitting.value = false;
  }
}
</script>

<style lang="scss" scoped>
.eval-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 24rpx;
  box-sizing: border-box;
}

.eval-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 40rpx 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.eval-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-text-main;
  text-align: center;
}

.star-row {
  margin-top: 40rpx;
  display: flex;
}

.star {
  font-size: 72rpx;
  color: #e3e5e9;
  margin: 0 12rpx;
}

.star-active {
  color: #ffb800;
}

.star-tip {
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.eval-input {
  margin-top: 40rpx;
  width: 100%;
  box-sizing: border-box;
  height: 240rpx;
  background: #f7f8fa;
  border-radius: 16rpx;
  padding: 24rpx;
  font-size: 28rpx;
  color: $fc-text-main;
}

.submit-btn {
  margin-top: 48rpx;
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: 44rpx;
  background: $fc-primary;
  color: #ffffff;
  font-size: 30rpx;
}

.submit-disabled {
  opacity: 0.6;
}

.eval-note {
  margin-top: 24rpx;
  text-align: center;
  font-size: 24rpx;
  color: $fc-text-sub;
}
</style>
