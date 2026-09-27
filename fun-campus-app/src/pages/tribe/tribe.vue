<template>
  <view class="tribe-page">
    <!-- 搜索栏 + 我的申请入口 -->
    <view class="tribe-toolbar">
      <view class="search-box">
        <u-icon name="search" size="18" color="#909399" />
        <input
          v-model="keyword"
          class="search-input"
          placeholder="搜索部落名称"
          confirm-type="search"
          placeholder-class="input-placeholder"
          @confirm="onSearch"
          @focus="onSearchFocus"
          @blur="onSearchBlur"
        />
        <text v-if="keyword" class="search-clear" @click="clearKeyword">×</text>
      </view>
      <view class="search-btn" @click="onSearch">搜索</view>
      <view class="apply-entry" @click="goMyApplication">我的申请</view>
    </view>

    <!-- 搜索历史 -->
    <view v-if="historyVisible && historyList.length" class="history-box">
      <view class="history-head">
        <text class="history-title">搜索历史</text>
        <text class="history-clear" @click="clearHistory">清空</text>
      </view>
      <view class="history-tags">
        <view
          v-for="item in historyList"
          :key="item"
          class="history-tag"
          @click="applyHistory(item)"
        >
          {{ item }}
        </view>
      </view>
    </view>

    <!-- 排序筛选 -->
    <view class="sort-bar">
      <view
        class="sort-chip"
        :class="{ 'sort-chip--active': sortType === 1 }"
        @click="switchSort(1)"
      >
        默认排序
      </view>
      <view
        class="sort-chip"
        :class="{ 'sort-chip--active': sortType === 2 }"
        @click="switchSort(2)"
      >
        热度优先
      </view>
    </view>

    <!-- 部落列表 -->
    <view v-if="list.length" class="tribe-list">
      <view
        v-for="item in list"
        :key="item.id"
        class="tribe-card"
        @click="goDetail(item.id)"
      >
        <image
          v-if="item.icon"
          class="tribe-icon"
          :src="resolveFileUrl(item.icon)"
          mode="aspectFill"
        />
        <view v-else class="tribe-icon tribe-icon--fallback">
          <text class="tribe-icon-char">{{ firstChar(item.name) }}</text>
        </view>
        <view class="tribe-body">
          <view class="tribe-name-row">
            <text class="tribe-name">{{ item.name }}</text>
            <text v-if="item.joinedFlag" class="joined-tag">已加入</text>
          </view>
          <text class="tribe-meta">
            {{ belongText(item.belongTo) }} · 主席：{{ item.presidentName || '待定' }}
          </text>
          <view class="tribe-foot">
            <text class="hot-text">热度 {{ item.memberNum || 0 }}</text>
          </view>
        </view>
        <text class="tribe-arrow">></text>
      </view>

      <view class="load-status">
        <text v-if="loadStatus === 'loading'">加载中...</text>
        <text v-else-if="loadStatus === 'nomore'">没有更多了</text>
      </view>
    </view>

    <!-- 空态 -->
    <view v-else class="empty">
      <text class="empty-text">{{ loading ? '加载中...' : emptyText }}</text>
    </view>

    <view class="page-tip">查看部落详情、提交加入申请，审核结果可在「我的申请」查看</view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { tribeApi } from '@/api/tribe-api';
import { getToken } from '@/utils/auth';
import { resolveFileUrl } from '@/utils/format';

const PAGE_SIZE = 10;
const HISTORY_KEY = 'fc_tribe_search_history';
const HISTORY_MAX = 10;

const keyword = ref('');
const sortType = ref(1);
const pageNum = ref(1);
const list = ref([]);
const total = ref(0);
const loading = ref(false);
// loadmore-可继续加载 loading-加载中 nomore-没有更多
const loadStatus = ref('loadmore');

// 搜索历史
const historyList = ref([]);
const historyVisible = ref(false);
let blurTimer = null;

// 请求序号：并发/快速切换时丢弃过期响应
let requestSeq = 0;

const emptyText = computed(() => (keyword.value ? '未找到相关部落' : '暂无部落'));

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  historyList.value = uni.getStorageSync(HISTORY_KEY) || [];
  if (!list.value.length) {
    loadData(true);
  }
});

onPullDownRefresh(async () => {
  await loadData(true);
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
    const { data } = await tribeApi.queryPage({
      pageNum: pageNum.value,
      pageSize: PAGE_SIZE,
      keyword: keyword.value.trim() || undefined,
      sortType: sortType.value,
    });
    if (seq !== requestSeq) {
      return;
    }
    const records = (data && data.list) || [];
    total.value = (data && data.total) || 0;
    list.value = reset ? records : list.value.concat(records);
    if (records.length < PAGE_SIZE || list.value.length >= total.value) {
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

function onSearch() {
  historyVisible.value = false;
  const value = keyword.value.trim();
  saveHistory(value);
  loadData(true);
}

function saveHistory(value) {
  if (!value) {
    return;
  }
  const next = [value, ...historyList.value.filter((item) => item !== value)].slice(0, HISTORY_MAX);
  historyList.value = next;
  uni.setStorageSync(HISTORY_KEY, next);
}

function onSearchFocus() {
  if (blurTimer) {
    clearTimeout(blurTimer);
    blurTimer = null;
  }
  historyVisible.value = true;
}

function onSearchBlur() {
  // 延迟隐藏，避免点击历史标签前面板先收起
  blurTimer = setTimeout(() => {
    historyVisible.value = false;
  }, 200);
}

function applyHistory(value) {
  keyword.value = value;
  onSearch();
}

function clearKeyword() {
  keyword.value = '';
  loadData(true);
}

function clearHistory() {
  uni.showModal({
    title: '提示',
    content: '确定清空搜索历史吗？',
    success: (res) => {
      if (res.confirm) {
        historyList.value = [];
        uni.removeStorageSync(HISTORY_KEY);
      }
    },
  });
}

function switchSort(value) {
  if (sortType.value === value) {
    return;
  }
  sortType.value = value;
  list.value = [];
  total.value = 0;
  loadStatus.value = 'loadmore';
  loadData(true);
}

function goDetail(tribeId) {
  uni.navigateTo({ url: `/pages/tribe/detail?tribeId=${tribeId}` });
}

function goMyApplication() {
  uni.navigateTo({ url: '/pages/tribe/my-application' });
}

function firstChar(name) {
  return name ? name.slice(0, 1) : '?';
}

function belongText(belongTo) {
  if (belongTo === 1) {
    return '组织';
  }
  if (belongTo === 2) {
    return '院系';
  }
  return '未分类';
}
</script>

<style lang="scss" scoped>
.tribe-page {
  min-height: 100vh;
  background: $fc-bg;
}

.tribe-toolbar {
  display: flex;
  align-items: center;
  background: #ffffff;
  padding: 20rpx 24rpx;
  position: sticky;
  top: 0;
  z-index: 20;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f5f6f8;
  border-radius: 36rpx;
  padding: 0 24rpx;
  height: 68rpx;
}

.search-input {
  flex: 1;
  margin-left: 12rpx;
  font-size: 28rpx;
  color: $fc-text-main;
}

.input-placeholder {
  color: #c0c4cc;
}

.search-clear {
  padding: 0 8rpx;
  font-size: 36rpx;
  color: #c0c4cc;
  line-height: 1;
}

.search-btn {
  flex-shrink: 0;
  margin-left: 20rpx;
  padding: 12rpx 24rpx;
  border-radius: 32rpx;
  background: $fc-primary;
  color: #ffffff;
  font-size: 26rpx;
}

.apply-entry {
  flex-shrink: 0;
  margin-left: 20rpx;
  font-size: 26rpx;
  color: $fc-primary;
}

.history-box {
  background: #ffffff;
  padding: 8rpx 24rpx 24rpx;
  border-top: 1rpx solid #f5f6f8;
}

.history-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 0;
}

.history-title {
  font-size: 26rpx;
  color: $fc-text-sub;
}

.history-clear {
  font-size: 26rpx;
  color: $fc-primary;
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
}

.history-tag {
  margin: 8rpx 16rpx 8rpx 0;
  padding: 10rpx 26rpx;
  border-radius: 28rpx;
  background: #f5f6f8;
  font-size: 26rpx;
  color: $fc-text-main;
}

.sort-bar {
  display: flex;
  align-items: center;
  padding: 8rpx 24rpx 20rpx;
  background: #ffffff;
}

.sort-chip {
  margin-right: 20rpx;
  padding: 8rpx 28rpx;
  border-radius: 28rpx;
  background: #f5f6f8;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.sort-chip--active {
  background: #e8eefc;
  color: $fc-primary;
  font-weight: 600;
}

.tribe-list {
  padding: 24rpx;
}

.tribe-card {
  display: flex;
  align-items: center;
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 28rpx 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.tribe-icon {
  flex-shrink: 0;
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  background: #eceef2;
}

.tribe-icon--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e8eefc;
}

.tribe-icon-char {
  font-size: 40rpx;
  font-weight: 600;
  color: $fc-primary;
}

.tribe-body {
  flex: 1;
  margin-left: 24rpx;
  overflow: hidden;
}

.tribe-name-row {
  display: flex;
  align-items: center;
}

.tribe-name {
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.joined-tag {
  flex-shrink: 0;
  margin-left: 16rpx;
  padding: 2rpx 14rpx;
  border-radius: 999rpx;
  border: 1rpx solid #19be6b;
  color: #19be6b;
  font-size: 22rpx;
}

.tribe-meta {
  display: block;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tribe-foot {
  display: flex;
  align-items: center;
  margin-top: 12rpx;
}

.hot-text {
  font-size: 24rpx;
  color: $fc-price;
}

.tribe-arrow {
  flex-shrink: 0;
  margin-left: 16rpx;
  font-size: 28rpx;
  color: #c0c4cc;
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

.page-tip {
  text-align: center;
  font-size: 24rpx;
  color: $fc-text-sub;
  padding: 8rpx 32rpx 40rpx;
  line-height: 1.6;
}
</style>
