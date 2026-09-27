<template>
  <view class="credit-page">
    <!-- 状态 Tab -->
    <view class="tab-bar">
      <view
        v-for="tab in tabs"
        :key="tab.status"
        class="tab-item"
        :class="{ 'tab-item--active': activeStatus === tab.status }"
        @click="switchTab(tab.status)"
      >
        {{ tab.text }}
      </view>
    </view>

    <!-- 申请列表 -->
    <view v-if="list.length" class="apply-list">
      <view
        v-for="item in list"
        :key="item.id"
        class="apply-card"
        @click="goDetail(item.id)"
      >
        <view class="card-head">
          <text class="card-title">{{ item.title }}</text>
          <text class="status-chip" :class="statusClass(item.status)">{{ item.statusName || statusText(item.status) }}</text>
        </view>
        <text class="card-meta">学期：{{ item.semester }}</text>
        <text class="card-meta">申请时间：{{ formatDateTime(item.createTime) }}</text>
        <text v-if="item.status === 2 && item.reviewRemark" class="card-reject">
          驳回原因：{{ item.reviewRemark }}
        </text>
      </view>
    </view>

    <view v-else class="empty">
      <text class="empty-text">{{ loading ? '加载中...' : '暂无申请记录' }}</text>
    </view>

    <!-- 新建申请入口 -->
    <view class="create-btn" @click="goCreate">+ 新建申请</view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { creditApi } from '@/api/credit-api';
import { getToken } from '@/utils/auth';
import { formatDateTime } from '@/utils/format';

const tabs = [
  { status: 0, text: '待审核' },
  { status: 1, text: '已通过' },
  { status: 2, text: '已驳回' },
];

const activeStatus = ref(0);
const list = ref([]);
const loading = ref(false);

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  loadData();
});

onPullDownRefresh(async () => {
  await loadData();
  uni.stopPullDownRefresh();
});

async function loadData() {
  if (loading.value) {
    return;
  }
  loading.value = true;
  try {
    const { data } = await creditApi.myList(activeStatus.value);
    list.value = data || [];
  } catch (e) {
    // 请求层已统一 toast
  } finally {
    loading.value = false;
  }
}

function switchTab(status) {
  if (activeStatus.value === status) {
    return;
  }
  activeStatus.value = status;
  list.value = [];
  loadData();
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/credit/detail?id=${id}` });
}

function goCreate() {
  uni.navigateTo({ url: '/pages/credit/form' });
}

function statusText(status) {
  if (status === 0) {
    return '待审核';
  }
  if (status === 1) {
    return '已通过';
  }
  if (status === 2) {
    return '已驳回';
  }
  return '未知';
}

function statusClass(status) {
  if (status === 1) {
    return 'status-chip--pass';
  }
  if (status === 2) {
    return 'status-chip--reject';
  }
  return 'status-chip--wait';
}
</script>

<style lang="scss" scoped>
.credit-page {
  min-height: 100vh;
  background: $fc-bg;
  padding-bottom: 160rpx;
}

.tab-bar {
  display: flex;
  background: #ffffff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 26rpx 0;
  font-size: 30rpx;
  color: $fc-text-sub;
  position: relative;
}

.tab-item--active {
  color: $fc-text-main;
  font-weight: 600;
}

.tab-item--active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 6rpx;
  transform: translateX(-50%);
  width: 48rpx;
  height: 6rpx;
  border-radius: 6rpx;
  background: $fc-primary;
}

.apply-list {
  padding: 24rpx;
}

.apply-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 28rpx 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  flex: 1;
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-chip {
  flex-shrink: 0;
  margin-left: 16rpx;
  padding: 4rpx 20rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
}

.status-chip--wait {
  color: #ff9900;
  border: 1rpx solid #ff9900;
}

.status-chip--pass {
  color: #19be6b;
  border: 1rpx solid #19be6b;
}

.status-chip--reject {
  color: #fa3534;
  border: 1rpx solid #fa3534;
}

.card-meta {
  display: block;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.card-reject {
  display: block;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: #fa3534;
  line-height: 1.6;
}

.empty {
  padding-top: 240rpx;
  display: flex;
  justify-content: center;
}

.empty-text {
  font-size: 28rpx;
  color: $fc-text-sub;
}

.create-btn {
  position: fixed;
  left: 40rpx;
  right: 40rpx;
  bottom: calc(40rpx + env(safe-area-inset-bottom));
  background: $fc-primary;
  border-radius: 44rpx;
  text-align: center;
  padding: 26rpx 0;
  font-size: 32rpx;
  color: #ffffff;
  box-shadow: 0 8rpx 24rpx rgba(60, 124, 255, 0.35);
}
</style>
