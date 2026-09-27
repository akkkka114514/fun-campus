<template>
  <view class="message-page">
    <!-- 主 tab：消息 / 公告 -->
    <view class="main-tabs">
      <view
        class="main-tab"
        :class="{ 'main-tab--active': mainTab === 'message' }"
        @click="switchMainTab('message')"
      >
        消息
      </view>
      <view
        class="main-tab"
        :class="{ 'main-tab--active': mainTab === 'notice' }"
        @click="switchMainTab('notice')"
      >
        公告
      </view>
    </view>

    <!-- ==================== 消息面板 ==================== -->
    <template v-if="mainTab === 'message'">
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
    </template>

    <!-- ==================== 公告面板 ==================== -->
    <template v-else>
      <!-- 分类筛选（全部 / 新闻 / 通知 ...） -->
      <scroll-view class="notice-types" scroll-x>
        <view class="notice-types-inner">
          <view
            class="notice-type"
            :class="{ 'notice-type--active': noticeTypeId === null }"
            @click="switchNoticeType(null)"
          >
            全部
          </view>
          <view
            v-for="item in noticeTypes"
            :key="item.noticeTypeId"
            class="notice-type"
            :class="{ 'notice-type--active': noticeTypeId === item.noticeTypeId }"
            @click="switchNoticeType(item.noticeTypeId)"
          >
            {{ item.noticeTypeName }}
          </view>
        </view>
      </scroll-view>

      <!-- 公告列表 -->
      <view v-if="noticeList.length" class="notice-list">
        <view
          v-for="notice in noticeList"
          :key="notice.noticeId"
          class="notice-item"
          @click="openNotice(notice)"
        >
          <view class="notice-head">
            <text class="notice-title">{{ notice.title }}</text>
            <text v-if="notice.noticeTypeName" class="notice-tag">{{ notice.noticeTypeName }}</text>
          </view>
          <view class="notice-foot">
            <text v-if="notice.source || notice.author" class="notice-meta">
              {{ notice.source || notice.author }}
            </text>
            <text class="notice-meta">{{ formatDateTime(notice.publishTime) }}</text>
            <text class="notice-meta">{{ notice.pageViewCount || 0 }} 浏览</text>
          </view>
        </view>

        <view class="load-status">
          <text v-if="noticeLoadStatus === 'loading'">加载中...</text>
          <text v-else-if="noticeLoadStatus === 'nomore'">没有更多了</text>
        </view>
      </view>

      <!-- 空态 -->
      <view v-else class="empty">
        <text class="empty-text">{{ noticeLoading ? '加载中...' : '暂无公告' }}</text>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { messageApi, refreshMessageBadge } from '@/api/message-api';
import { noticeApi } from '@/api/notice-api';
import { getToken } from '@/utils/auth';
import { formatDateTime } from '@/utils/format';

const PAGE_SIZE = 15;

// 主 tab：message-消息 notice-公告
const mainTab = ref('message');

// ==================== 消息面板 ====================
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

// ==================== 公告面板 ====================
const noticeTypes = ref([]);
// null-全部分类
const noticeTypeId = ref(null);
const noticePageNum = ref(1);
const noticeList = ref([]);
const noticeTotal = ref(0);
const noticeLoading = ref(false);
// loadmore-可继续加载 loading-加载中 nomore-没有更多
const noticeLoadStatus = ref('loadmore');

// 分类接口只拉一次（失败则下次切 tab 重试）
let noticeTypesLoaded = false;
// 请求序号：并发/快速切分类时丢弃过期响应
let noticeRequestSeq = 0;

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  refreshMessageBadge();
  // 每次进入刷新消息列表（可能有新消息进来）
  loadData(true);
  // 公告 tab 已加载过则同步刷新（从详情页返回能看到最新列表）
  if (mainTab.value === 'notice' && noticeList.value.length) {
    loadNoticeData(true);
  }
});

onPullDownRefresh(async () => {
  if (mainTab.value === 'notice') {
    await loadNoticeData(true);
  } else {
    await loadData(true);
    refreshMessageBadge();
  }
  uni.stopPullDownRefresh();
});

onReachBottom(() => {
  if (mainTab.value === 'notice') {
    if (noticeLoadStatus.value === 'loadmore' && !noticeLoading.value) {
      loadNoticeData(false);
    }
  } else if (loadStatus.value === 'loadmore' && !loading.value) {
    loadData(false);
  }
});

function switchMainTab(tab) {
  if (mainTab.value === tab) {
    return;
  }
  mainTab.value = tab;
  if (tab === 'notice') {
    loadNoticeTypes();
    // 首次切换（或上次加载失败为空）时加载公告列表
    if (!noticeList.value.length) {
      loadNoticeData(true);
    }
  }
}

// ==================== 消息 ====================
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

// ==================== 公告 ====================
async function loadNoticeTypes() {
  if (noticeTypesLoaded) {
    return;
  }
  try {
    const { data } = await noticeApi.typeList();
    noticeTypes.value = data || [];
    noticeTypesLoaded = true;
  } catch (e) {
    // 请求层已统一 toast；下次切 tab 可重试
  }
}

async function loadNoticeData(reset) {
  if (noticeLoading.value && !reset) {
    return;
  }
  const seq = ++noticeRequestSeq;
  noticeLoading.value = true;
  if (reset) {
    noticePageNum.value = 1;
  }
  noticeLoadStatus.value = 'loading';
  try {
    const form = {
      pageNum: noticePageNum.value,
      pageSize: PAGE_SIZE,
      searchCount: true,
    };
    if (noticeTypeId.value !== null) {
      form.noticeTypeId = noticeTypeId.value;
    }
    const { data } = await noticeApi.query(form);
    if (seq !== noticeRequestSeq) {
      return;
    }
    const records = (data && data.list) || [];
    noticeTotal.value = (data && data.total) || 0;
    noticeList.value = reset ? records : noticeList.value.concat(records);
    if (records.length < PAGE_SIZE || noticeList.value.length >= noticeTotal.value) {
      noticeLoadStatus.value = 'nomore';
    } else {
      noticeLoadStatus.value = 'loadmore';
      noticePageNum.value += 1;
    }
  } catch (e) {
    if (seq === noticeRequestSeq) {
      // 请求层已统一 toast
      noticeLoadStatus.value = 'loadmore';
    }
  } finally {
    if (seq === noticeRequestSeq) {
      noticeLoading.value = false;
    }
  }
}

function switchNoticeType(typeId) {
  if (noticeTypeId.value === typeId) {
    return;
  }
  noticeTypeId.value = typeId;
  noticeList.value = [];
  noticeTotal.value = 0;
  noticeLoadStatus.value = 'loadmore';
  loadNoticeData(true);
}

function openNotice(notice) {
  uni.navigateTo({ url: `/pages/notice/notice-detail?noticeId=${notice.noticeId}` });
}
</script>

<style lang="scss" scoped>
.message-page {
  min-height: 100vh;
  background: $fc-bg;
}

.main-tabs {
  display: flex;
  background: #ffffff;
  padding: 0 32rpx;
  position: sticky;
  top: 0;
  z-index: 11;
}

.main-tab {
  margin-right: 48rpx;
  padding: 26rpx 0;
  font-size: 34rpx;
  color: $fc-text-sub;
  position: relative;
}

.main-tab--active {
  color: $fc-text-main;
  font-weight: 600;
}

.main-tab--active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 10rpx;
  transform: translateX(-50%);
  width: 44rpx;
  height: 6rpx;
  border-radius: 6rpx;
  background: $fc-primary;
}

.msg-tabs {
  display: flex;
  background: #ffffff;
  padding: 0 24rpx;
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

.notice-types {
  background: #ffffff;
  white-space: nowrap;
  padding: 20rpx 0;
}

.notice-types-inner {
  display: inline-flex;
  padding: 0 24rpx;
}

.notice-type {
  flex-shrink: 0;
  margin-right: 20rpx;
  padding: 10rpx 28rpx;
  border-radius: 40rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
  background: #f2f4f8;
}

.notice-type--active {
  color: #ffffff;
  background: $fc-primary;
}

.notice-list {
  padding: 24rpx;
}

.notice-item {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 28rpx 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.notice-head {
  display: flex;
  align-items: center;
}

.notice-title {
  flex: 1;
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-tag {
  flex-shrink: 0;
  margin-left: 16rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  color: $fc-primary;
  background: rgba(60, 124, 255, 0.1);
}

.notice-foot {
  display: flex;
  align-items: center;
  margin-top: 16rpx;
}

.notice-meta {
  margin-right: 24rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
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
