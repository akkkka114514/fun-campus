<template>
  <view class="message-page">
    <!-- 筛选：全部 / 未读 -->
    <view class="msg-tabs">
      <view
        class="msg-tab"
        :class="{ 'msg-tab--active': filterUnread === false }"
        @click="switchFilter(false)"
      >
        全部
      </view>
      <view
        class="msg-tab"
        :class="{ 'msg-tab--active': filterUnread === true }"
        @click="switchFilter(true)"
      >
        未读
      </view>
    </view>

    <!-- 消息列表 -->
    <view v-if="messageList.length" class="msg-list">
      <view
        v-for="msg in messageList"
        :key="msg.messageId"
        class="msg-item"
        @click="readMessage(msg)"
      >
        <view class="msg-head">
          <view class="msg-dot" :class="{ 'msg-dot--read': msg.readFlag }" />
          <text class="msg-title" :class="{ 'msg-title--read': msg.readFlag }">
            {{ msg.title }}
          </text>
          <text class="msg-time">{{ formatDateTime(msg.createTime) }}</text>
        </view>
        <text class="msg-content">{{ msg.content }}</text>
      </view>

      <view class="load-status">
        <text v-if="loadStatus === 'loading'">加载中...</text>
        <text v-else-if="loadStatus === 'nomore'">没有更多了</text>
      </view>
    </view>

    <!-- 空态 -->
    <view v-else class="empty">
      <text class="empty-text">
        {{ loading ? '加载中...' : filterUnread ? '暂无未读消息' : '暂无消息' }}
      </text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { messageApi, refreshMessageBadge } from '@/api/message-api';
import { getToken } from '@/utils/auth';
import { formatDateTime } from '@/utils/format';

const PAGE_SIZE = 15;

// false-全部 true-仅未读
const filterUnread = ref(false);
const pageNum = ref(1);
const messageList = ref([]);
const total = ref(0);
const loading = ref(false);
// loadmore-可继续加载 loading-加载中 nomore-没有更多
const loadStatus = ref('loadmore');

// 请求序号：并发/快速切筛选时丢弃过期响应
let requestSeq = 0;

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  refreshMessageBadge();
  // 每次进入刷新列表（可能有新消息进来）
  loadData(true);
});

onPullDownRefresh(async () => {
  await loadData(true);
  refreshMessageBadge();
  uni.stopPullDownRefresh();
});

onReachBottom(() => {
  if (loadStatus.value === 'loadmore' && !loading.value) {
    loadData(false);
  }
});

async function loadData(reset) {
  if (loading.value && !reset) {
    return;
  }
  const seq = ++requestSeq;
  loading.value = true;
  if (reset) {
    pageNum.value = 1;
  }
  loadStatus.value = 'loading';
  try {
    const form = {
      pageNum: pageNum.value,
      pageSize: PAGE_SIZE,
      searchCount: true,
    };
    if (filterUnread.value) {
      form.readFlag = false;
    }
    const { data } = await messageApi.queryMy(form);
    if (seq !== requestSeq) {
      return;
    }
    const records = (data && data.list) || [];
    total.value = (data && data.total) || 0;
    messageList.value = reset ? records : messageList.value.concat(records);
    if (records.length < PAGE_SIZE || messageList.value.length >= total.value) {
      loadStatus.value = 'nomore';
    } else {
      loadStatus.value = 'loadmore';
      pageNum.value += 1;
    }
  } catch (e) {
    if (seq === requestSeq) {
      // 请求层已统一 toast
      loadStatus.value = 'loadmore';
    }
  } finally {
    if (seq === requestSeq) {
      loading.value = false;
    }
  }
}

function switchFilter(onlyUnread) {
  if (filterUnread.value === onlyUnread) {
    return;
  }
  filterUnread.value = onlyUnread;
  messageList.value = [];
  total.value = 0;
  loadStatus.value = 'loadmore';
  loadData(true);
}

async function readMessage(msg) {
  if (msg.readFlag) {
    return;
  }
  try {
    await messageApi.read(msg.messageId);
    msg.readFlag = true;
    refreshMessageBadge();
  } catch (e) {
    // 请求层已统一 toast
  }
}
</script>

<style lang="scss" scoped>
.message-page {
  min-height: 100vh;
  background: $fc-bg;
}

.msg-tabs {
  display: flex;
  background: #ffffff;
  padding: 0 24rpx;
  position: sticky;
  top: 0;
  z-index: 10;
}

.msg-tab {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 30rpx;
  color: $fc-text-sub;
  position: relative;
}

.msg-tab--active {
  color: $fc-text-main;
  font-weight: 600;
}

.msg-tab--active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 8rpx;
  transform: translateX(-50%);
  width: 48rpx;
  height: 6rpx;
  border-radius: 6rpx;
  background: $fc-primary;
}

.msg-list {
  padding: 24rpx;
}

.msg-item {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 28rpx 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.msg-head {
  display: flex;
  align-items: center;
}

.msg-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  background: #fa3534;
  flex-shrink: 0;
}

.msg-dot--read {
  background: transparent;
}

.msg-title {
  flex: 1;
  margin-left: 12rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-title--read {
  font-weight: 400;
  color: $fc-text-sub;
}

.msg-time {
  margin-left: 16rpx;
  font-size: 24rpx;
  color: #c0c4cc;
  flex-shrink: 0;
}

.msg-content {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  margin-top: 16rpx;
  font-size: 26rpx;
  line-height: 40rpx;
  color: $fc-text-sub;
  word-break: break-all;
}

.load-status {
  text-align: center;
  padding: 24rpx 0;
  font-size: 24rpx;
  color: $fc-text-sub;
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
