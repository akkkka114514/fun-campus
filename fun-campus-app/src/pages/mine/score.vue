<template>
  <view class="score-page">
    <!-- 顶部总览（随 Tab 切换） -->
    <view class="summary-card" :class="{ 'summary-credit': activeTab === 'credit' }">
      <text class="summary-value">{{ activeTab === 'grade' ? scoreText(userInfo.gradeScore) : scoreText(userInfo.creditScore) }}</text>
      <text class="summary-label">{{ activeTab === 'grade' ? '实践积分' : '信誉分' }}</text>
      <text class="summary-tip">{{ activeTab === 'grade' ? '完成活动并按时签到，即可累积实践积分' : '初始 100 分，活动结束未签到（爽约）扣 5 分' }}</text>
    </view>

    <!-- Tab 切换 -->
    <view class="tab-bar">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ 'tab-active': activeTab === tab.key }"
        @click="switchTab(tab.key)"
      >
        <text class="tab-text">{{ tab.text }}</text>
      </view>
    </view>

    <!-- 实践积分明细 -->
    <view v-if="activeTab === 'grade'" class="list-card">
      <view v-if="gradeList.length">
        <view v-for="item in gradeList" :key="item.activityId" class="record-item">
          <view class="record-main">
            <text class="record-title">{{ item.activityTitle }}</text>
            <text class="record-time">完成时间：{{ formatDateTime(item.finishTime) }}</text>
          </view>
          <text class="record-score record-plus">+{{ scoreText(item.score) }}</text>
        </view>
      </view>
      <view v-else class="empty-box">暂无实践积分记录</view>
    </view>

    <!-- 信誉分变动记录 -->
    <view v-else class="list-card">
      <view v-if="creditList.length">
        <view v-for="item in creditList" :key="item.activityId" class="record-item">
          <view class="record-main">
            <text class="record-title">{{ item.reason }}</text>
            <text class="record-time">发生时间：{{ formatDateTime(item.changeTime) }}</text>
          </view>
          <text class="record-score record-minus">{{ scoreText(item.changeScore) }}</text>
        </view>
      </view>
      <view v-else class="empty-box">暂无信誉分变动记录</view>
    </view>

    <view class="page-tip">明细由报名与签到记录实时推导：已结束且已签到活动计实践分，爽约则扣减信誉分</view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue';
import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { useUserStore } from '@/store/user';
import { userApi } from '@/api/user-api';
import { getToken } from '@/utils/auth';
import { formatDateTime } from '@/utils/format';

const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo || {});

const tabs = [
  { key: 'grade', text: '实践积分明细' },
  { key: 'credit', text: '信誉分变动记录' },
];
const activeTab = ref('grade');

const gradeList = ref([]);
const creditList = ref([]);
const gradeLoaded = ref(false);
const creditLoaded = ref(false);

onLoad((options) => {
  if (options && options.tab === 'credit') {
    activeTab.value = 'credit';
  }
});

async function loadGrade() {
  if (gradeLoaded.value) {
    return;
  }
  const { data } = await userApi.gradeScoreDetailList();
  gradeList.value = data || [];
  gradeLoaded.value = true;
}

async function loadCredit() {
  if (creditLoaded.value) {
    return;
  }
  const { data } = await userApi.creditScoreLogList();
  creditList.value = data || [];
  creditLoaded.value = true;
}

async function loadActive() {
  try {
    if (activeTab.value === 'grade') {
      await loadGrade();
    } else {
      await loadCredit();
    }
  } catch (e) {
    // 请求层已统一提示
  }
}

function switchTab(key) {
  if (activeTab.value === key) {
    return;
  }
  activeTab.value = key;
  loadActive();
}

function scoreText(score) {
  return score == null ? '-' : String(Number(score));
}

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  // 顶部总分依赖用户信息，强制刷新保证最新
  userStore.fetchUserInfo(true).catch(() => {});
  loadActive();
});

onPullDownRefresh(async () => {
  gradeLoaded.value = false;
  creditLoaded.value = false;
  try {
    await userStore.fetchUserInfo(true);
    await loadActive();
  } catch (e) {
    // 请求层已统一提示
  } finally {
    uni.stopPullDownRefresh();
  }
});
</script>

<style lang="scss" scoped>
.score-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 24rpx;
  box-sizing: border-box;
}

.summary-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 32rpx;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #3c7cff 0%, #6a9bff 100%);
}

.summary-credit {
  background: linear-gradient(135deg, #ff9f43 0%, #ffc069 100%);
}

.summary-value {
  font-size: 72rpx;
  font-weight: 700;
  color: #ffffff;
}

.summary-label {
  margin-top: 8rpx;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.92);
}

.summary-tip {
  margin-top: 16rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.78);
}

.tab-bar {
  display: flex;
  background: $fc-card-bg;
  border-radius: 20rpx;
  margin-top: 24rpx;
  padding: 8rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  border-radius: 14rpx;
}

.tab-active {
  background: #e8eefc;
}

.tab-text {
  font-size: 28rpx;
  color: $fc-text-sub;
}

.tab-active .tab-text {
  color: $fc-primary;
  font-weight: 600;
}

.list-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  margin-top: 24rpx;
  padding: 8rpx 24rpx;
}

.record-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #f5f6f8;
}

.record-item:last-child {
  border-bottom: none;
}

.record-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-right: 20rpx;
  overflow: hidden;
}

.record-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-time {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.record-score {
  flex-shrink: 0;
  font-size: 36rpx;
  font-weight: 700;
}

.record-plus {
  color: $fc-primary;
}

.record-minus {
  color: #fa3534;
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
  padding: 24rpx 16rpx;
  line-height: 1.6;
}
</style>
