<template>
  <view class="apply-page">
    <view v-if="list.length" class="apply-list">
      <view
        v-for="item in list"
        :key="item.id"
        class="apply-card"
        @click="goTribe(item)"
      >
        <view class="card-head">
          <text class="card-title">{{ item.tribeName || '部落' }}</text>
          <text class="status-chip" :class="statusClass(item.status)">{{ statusText(item.status) }}</text>
        </view>
        <text class="card-reason">申请理由：{{ item.reason || '（未填写）' }}</text>
        <view class="card-foot">
          <text class="card-time">申请时间：{{ formatDateTime(item.createTime) }}</text>
          <text class="card-arrow">查看部落 ></text>
        </view>
        <view v-if="item.status !== 0" class="review-box">
          <text class="review-line">审核时间：{{ formatDateTime(item.reviewTime) }}</text>
          <text v-if="item.reviewRemark" class="review-line">审核意见：{{ item.reviewRemark }}</text>
        </view>
      </view>
    </view>

    <view v-else class="empty">
      <text class="empty-text">{{ loading ? '加载中...' : '暂无加入申请记录' }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { tribeApi } from '@/api/tribe-api';
import { getToken } from '@/utils/auth';
import { formatDateTime } from '@/utils/format';

const list = ref([]);
const loading = ref(false);

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  loadData();
});

async function loadData() {
  if (loading.value) {
    return;
  }
  loading.value = true;
  try {
    const { data } = await tribeApi.myApplicationList();
    list.value = data || [];
  } catch (e) {
    // 请求层已统一 toast
  } finally {
    loading.value = false;
  }
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

function goTribe(item) {
  if (!item.tribeId) {
    return;
  }
  uni.navigateTo({ url: `/pages/tribe/detail?tribeId=${item.tribeId}` });
}
</script>

<style lang="scss" scoped>
.apply-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 24rpx;
  box-sizing: border-box;
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

.card-reason {
  display: block;
  margin-top: 16rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
  line-height: 1.6;
}

.card-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20rpx;
}

.card-time {
  font-size: 24rpx;
  color: $fc-text-sub;
}

.card-arrow {
  font-size: 24rpx;
  color: $fc-primary;
}

.review-box {
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #f5f6f8;
}

.review-line {
  display: block;
  font-size: 24rpx;
  color: $fc-text-sub;
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
</style>
