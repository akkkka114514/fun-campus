<template>
  <view class="home-page">
    <!-- 顶部栏：本校/全局切换 + 扫码/消息入口 -->
    <view class="home-toolbar">
      <view class="home-tabs">
        <view
          class="home-tab"
          :class="{ 'home-tab--active': activeTab === 1 }"
          @click="switchTab(1)"
        >
          本校活动
        </view>
        <view
          class="home-tab"
          :class="{ 'home-tab--active': activeTab === 2 }"
          @click="switchTab(2)"
        >
          全局活动
        </view>
      </view>
      <view class="home-actions">
        <view class="home-action" @click="onScan">
          <u-icon name="scan" size="22" color="#303133" />
        </view>
        <view class="home-action" @click="goMessage">
          <u-icon name="bell" size="22" color="#303133" />
          <view v-if="unreadCount > 0" class="action-badge">
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </view>
        </view>
      </view>
    </view>

    <!-- 活动列表 -->
    <view v-if="activityList.length" class="activity-list">
      <view
        v-for="item in activityList"
        :key="item.activity.id"
        class="activity-card"
        @click="goDetail(item)"
      >
        <image
          class="activity-cover"
          :src="resolveFileUrl(item.activity.coverImg)"
          mode="aspectFill"
        />
        <view class="activity-body">
          <text class="activity-title">{{ item.activity.title }}</text>
          <text class="activity-meta">
            时间：{{ formatDateTime(item.schedule && item.schedule.activityStartTime) }}
          </text>
          <text class="activity-meta">
            地点：{{ item.activity.position || '待定' }}
          </text>
          <view class="activity-footer">
            <text class="activity-school">
              {{ item.activity.activityBelongToSchoolName || '' }}
            </text>
            <view v-if="item.activity.paidFlag" class="activity-tag activity-tag--price">
              ¥{{ fenToYuan(item.activity.priceFen) }}
            </view>
            <view v-else class="activity-tag activity-tag--free">免费</view>
          </view>
        </view>
      </view>

      <view class="load-status">
        <text v-if="loadStatus === 'loading'">加载中...</text>
        <text v-else-if="loadStatus === 'nomore'">没有更多了</text>
      </view>
    </view>

    <!-- 空态 -->
    <view v-else class="empty">
      <text class="empty-text">{{ loading ? '加载中...' : '暂无活动' }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { homeApi } from '@/api/home-api';
import { activityApi } from '@/api/activity-api';
import { refreshMessageBadge } from '@/api/message-api';
import { getToken } from '@/utils/auth';
import { resolveFileUrl, formatDateTime, fenToYuan } from '@/utils/format';

const PAGE_SIZE = 10;

const activeTab = ref(1);
const pageNum = ref(1);
const activityList = ref([]);
const total = ref(0);
const loading = ref(false);
// loadmore-可继续加载 loading-加载中 nomore-没有更多
const loadStatus = ref('loadmore');
// 消息未读数（入口红点）
const unreadCount = ref(0);

// 请求序号：并发/快速切 tab 时丢弃过期响应
let requestSeq = 0;

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  refreshMessageBadge().then((count) => {
    unreadCount.value = count;
  });
  if (!activityList.value.length) {
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
  // 触底加载防重；reset（下拉/切 tab）不受限制
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
    const { data } = await homeApi.homeData({
      activeActivityPage: activeTab.value,
      pageNum: pageNum.value,
      pageSize: PAGE_SIZE,
    });
    if (seq !== requestSeq) {
      return;
    }
    const page = activeTab.value === 1 ? data.mySchoolActivities : data.globalActivities;
    const records = (page && page.records) || [];
    total.value = (page && page.total) || 0;
    activityList.value = reset ? records : activityList.value.concat(records);
    if (records.length < PAGE_SIZE || activityList.value.length >= total.value) {
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

function switchTab(tab) {
  if (activeTab.value === tab) {
    return;
  }
  activeTab.value = tab;
  activityList.value = [];
  total.value = 0;
  loadStatus.value = 'loadmore';
  loadData(true);
}

function goDetail(item) {
  const activityId = item.activity && item.activity.id;
  if (!activityId) {
    return;
  }
  uni.navigateTo({ url: '/pages/activity/detail?id=' + activityId });
}

function goMessage() {
  uni.switchTab({ url: '/pages/message/message' });
}

function onScan() {
  // #ifdef H5
  // H5 无原生扫码能力：降级为粘贴分享链接/码（App/小程序走 uni.scanCode）
  uni.showModal({
    title: '扫一扫',
    editable: true,
    placeholderText: '当前环境不支持摄像头扫码，请粘贴分享链接或分享码',
    success: (res) => {
      if (res.confirm && res.content) {
        resolveAndGo(res.content);
      }
    },
  });
  // #endif
  // #ifndef H5
  uni.scanCode({
    success: (res) => resolveAndGo(res.result),
  });
  // #endif
}

// 从分享链接/码文本中提取 shareToken（链接形如 {baseUrl}/activity/share/{token}）
function extractShareToken(rawText) {
  const text = String(rawText || '').trim();
  if (!text) {
    return '';
  }
  const match = text.match(/activity\/share\/([A-Za-z0-9]+)/);
  if (match) {
    return match[1];
  }
  // 直接粘贴 token 的情况
  if (/^[A-Za-z0-9]{16,64}$/.test(text)) {
    return text;
  }
  return '';
}

async function resolveAndGo(rawText) {
  const token = extractShareToken(rawText);
  if (!token) {
    uni.showToast({ title: '无法识别的分享内容', icon: 'none' });
    return;
  }
  try {
    const { data } = await activityApi.resolveShare(token);
    if (!data) {
      return;
    }
    uni.navigateTo({ url: '/pages/activity/detail?id=' + data });
  } catch (e) {
    // 分享失效等失败原因由请求层 toast
  }
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: $fc-bg;
}

.home-toolbar {
  display: flex;
  align-items: center;
  background: #ffffff;
  padding: 0 24rpx;
  position: sticky;
  top: 0;
  z-index: 10;
}

.home-tabs {
  flex: 1;
  display: flex;
}

.home-actions {
  display: flex;
  align-items: center;
}

.home-action {
  position: relative;
  padding: 16rpx 8rpx 16rpx 28rpx;
  display: flex;
  align-items: center;
}

.action-badge {
  position: absolute;
  top: 2rpx;
  right: -6rpx;
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  border-radius: 16rpx;
  background: #fa3534;
  color: #ffffff;
  font-size: 20rpx;
  line-height: 32rpx;
  text-align: center;
  box-sizing: border-box;
}

.home-tab {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 30rpx;
  color: $fc-text-sub;
  position: relative;
}

.home-tab--active {
  color: $fc-text-main;
  font-weight: 600;
}

.home-tab--active::after {
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

.activity-list {
  padding: 24rpx;
}

.activity-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.activity-cover {
  width: 100%;
  height: 320rpx;
  background: #eceef2;
}

.activity-body {
  padding: 24rpx;
}

.activity-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-meta {
  display: block;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.activity-footer {
  display: flex;
  align-items: center;
  margin-top: 20rpx;
}

.activity-school {
  flex: 1;
  font-size: 24rpx;
  color: $fc-text-sub;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-tag {
  margin-left: 16rpx;
  padding: 4rpx 16rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
}

.activity-tag--price {
  color: $fc-price;
  border: 1rpx solid $fc-price;
}

.activity-tag--free {
  color: #19be6b;
  border: 1rpx solid #19be6b;
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
