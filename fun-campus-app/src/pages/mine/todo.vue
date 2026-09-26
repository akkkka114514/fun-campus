<template>
  <view class="todo-page">
    <!-- 待签到 -->
    <view class="todo-section">
      <view class="section-head">
        <text class="section-title">待签到</text>
        <text v-if="signInList.length" class="section-badge">{{ signInList.length }}</text>
      </view>
      <view v-if="signInList.length">
        <view v-for="item in signInList" :key="item.activityId" class="todo-card">
          <view class="card-main">
            <text class="card-title">{{ item.activityTitle }}</text>
            <text v-if="item.position" class="card-sub">地点：{{ item.position }}</text>
            <text class="card-window">签到窗口：{{ formatTimeRange(item.windowStartTime, item.windowEndTime) }}</text>
          </view>
          <view class="card-btn" @click="goSignCode">去亮码</view>
        </view>
      </view>
      <view v-else class="empty-box">当前没有待签到的活动</view>
    </view>

    <!-- 待签退 -->
    <view class="todo-section">
      <view class="section-head">
        <text class="section-title">待签退</text>
        <text v-if="signOutList.length" class="section-badge">{{ signOutList.length }}</text>
      </view>
      <view v-if="signOutList.length">
        <view v-for="item in signOutList" :key="item.activityId" class="todo-card">
          <view class="card-main">
            <text class="card-title">{{ item.activityTitle }}</text>
            <text v-if="item.position" class="card-sub">地点：{{ item.position }}</text>
            <text class="card-window">签退窗口：{{ formatTimeRange(item.windowStartTime, item.windowEndTime) }}</text>
          </view>
          <view class="card-btn" @click="goSignCode">去亮码</view>
        </view>
      </view>
      <view v-else class="empty-box">当前没有待签退的活动</view>
    </view>

    <!-- 待评价 -->
    <view class="todo-section">
      <view class="section-head">
        <text class="section-title">待评价</text>
        <text v-if="evalList.length" class="section-badge">{{ evalList.length }}</text>
      </view>
      <view v-if="evalList.length">
        <view v-for="item in evalList" :key="item.activityId" class="todo-card">
          <view class="card-main">
            <text class="card-title">{{ item.title }}</text>
            <text class="card-window">结束时间：{{ formatDateTime(item.activityEndTime) }}</text>
          </view>
          <view class="card-btn" @click="goEvaluate(item)">去评价</view>
        </view>
      </view>
      <view v-else class="empty-box">当前没有待评价的活动</view>
    </view>

    <view class="page-tip">签到 / 签退请向签到员出示「我的签到码」</view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { activityApi } from '@/api/activity-api';
import { getToken } from '@/utils/auth';
import { formatDateTime, formatTimeRange } from '@/utils/format';

const signInList = ref([]);
const signOutList = ref([]);
const evalList = ref([]);
const loading = ref(false);

async function loadAll() {
  if (loading.value) {
    return;
  }
  loading.value = true;
  try {
    const [r1, r2, r3] = await Promise.allSettled([
      activityApi.queryPendingSignInList(),
      activityApi.queryPendingSignOutList(),
      activityApi.queryPendingEvaluations(),
    ]);
    signInList.value = r1.status === 'fulfilled' ? r1.value.data || [] : [];
    signOutList.value = r2.status === 'fulfilled' ? r2.value.data || [] : [];
    evalList.value = r3.status === 'fulfilled' ? r3.value.data || [] : [];
  } finally {
    loading.value = false;
    uni.stopPullDownRefresh();
  }
}

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  loadAll();
});

onPullDownRefresh(() => {
  loadAll();
});

// 学员侧为「亮码 + 签到员扫码」模式：待签到 / 待签退统一点击去出示签到码
function goSignCode() {
  uni.navigateTo({ url: '/pages/signin/code' });
}

function goEvaluate(item) {
  uni.navigateTo({
    url: `/pages/activity/evaluate?activityId=${item.activityId}&title=${encodeURIComponent(item.title || '')}`,
  });
}
</script>

<style lang="scss" scoped>
.todo-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 24rpx;
  box-sizing: border-box;
}

.todo-section {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 8rpx 24rpx 16rpx;
  margin-bottom: 24rpx;
}

.section-head {
  display: flex;
  align-items: center;
  padding: 24rpx 0 8rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
}

.section-badge {
  margin-left: 12rpx;
  min-width: 36rpx;
  height: 36rpx;
  line-height: 36rpx;
  padding: 0 10rpx;
  box-sizing: border-box;
  text-align: center;
  border-radius: 18rpx;
  background: $fc-primary;
  color: #ffffff;
  font-size: 22rpx;
}

.todo-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f6f8;
}

.todo-card:last-child {
  border-bottom: none;
}

.card-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-right: 20rpx;
  overflow: hidden;
}

.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-sub {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.card-window {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.card-btn {
  flex-shrink: 0;
  padding: 14rpx 28rpx;
  border-radius: 32rpx;
  background: $fc-primary;
  color: #ffffff;
  font-size: 26rpx;
}

.empty-box {
  padding: 48rpx 0;
  text-align: center;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.page-tip {
  text-align: center;
  font-size: 24rpx;
  color: $fc-text-sub;
  padding: 8rpx 0 24rpx;
}
</style>
