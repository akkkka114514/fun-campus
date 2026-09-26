<template>
  <view class="my-act-page">
    <!-- 分类 Tab -->
    <view class="act-tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="act-tab"
        :class="{ 'act-tab--active': activeTab.key === tab.key }"
        @click="switchTab(tab)"
      >
        {{ tab.text }}
      </view>
    </view>

    <!-- 报名列表 -->
    <view v-if="activityList.length" class="act-list">
      <view
        v-for="item in activityList"
        :key="item.activityId"
        class="act-card"
        @click="goDetail(item)"
      >
        <view class="act-head">
          <text class="act-title">{{ item.activityTitle || '活动' }}</text>
          <text class="act-badge" :class="'badge--' + badgeType(item)">{{ badgeText(item) }}</text>
        </view>

        <view class="act-row">
          <text class="act-label">活动时间</text>
          <text class="act-value">{{ formatTimeRange(item.activityStartTime, item.activityEndTime) || '--' }}</text>
        </view>
        <view class="act-row" v-if="item.position">
          <text class="act-label">活动地点</text>
          <text class="act-value">{{ item.position }}</text>
        </view>
        <view class="act-row">
          <text class="act-label">报名时间</text>
          <text class="act-value">{{ formatDateTime(item.enrollTime) || '--' }}</text>
        </view>

        <view class="act-foot">
          <text class="act-score">+{{ scoreText(item.scoreCanGet) }} 学分</text>
          <text class="act-price" :class="{ 'act-price--free': !item.paidFlag }">
            {{ item.paidFlag ? '¥' + fenToYuan(item.priceFen) : '免费' }}
          </text>
        </view>
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
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { activityApi } from '@/api/activity-api';
import { getToken } from '@/utils/auth';
import { formatDateTime, formatTimeRange, fenToYuan } from '@/utils/format';

const PAGE_SIZE = 10;

// 分类 Tab：已报名（全部）/ 已签到（signInStatus=true）/ 已完成（活动状态 4-已结束）
const tabs = [
  { key: 'enrolled', text: '已报名', signInStatus: null, activityStatus: null },
  { key: 'signedIn', text: '已签到', signInStatus: true, activityStatus: null },
  { key: 'finished', text: '已完成', signInStatus: null, activityStatus: 4 },
];

const EMPTY_TEXT = {
  enrolled: '暂无报名记录',
  signedIn: '暂无已签到活动',
  finished: '暂无已完成活动',
};

const activeTab = ref(tabs[0]);
const activityList = ref([]);
const pageNum = ref(1);
const total = ref(0);
const loading = ref(false);
// loadmore-可继续加载 loading-加载中 nomore-没有更多
const loadStatus = ref('loadmore');

// 请求序号：并发/快速切 Tab 时丢弃过期响应
let requestSeq = 0;

const emptyText = ref(EMPTY_TEXT.enrolled);

// 徽标：活动已结束 > 已签退/已签到 > 已报名
function badgeType(item) {
  if (item.activityStatus === 4) {
    return 'finished';
  }
  if (item.signInStatus) {
    return 'signed';
  }
  return 'enrolled';
}

function badgeText(item) {
  if (item.activityStatus === 4) {
    return '已结束';
  }
  if (item.signInStatus) {
    return item.needSignOut && item.signOutStatus ? '已签退' : '已签到';
  }
  return '已报名';
}

function scoreText(score) {
  return score == null ? '0' : String(Number(score));
}

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  // 每次进入刷新（签到/签退后状态可能已变化）
  loadData(true);
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
    const form = {
      pageNum: pageNum.value,
      pageSize: PAGE_SIZE,
      searchCount: true,
    };
    if (activeTab.value.signInStatus != null) {
      form.signInStatus = activeTab.value.signInStatus;
    }
    if (activeTab.value.activityStatus != null) {
      form.activityStatus = activeTab.value.activityStatus;
    }
    const { data } = await activityApi.queryMyEnrollments(form);
    if (seq !== requestSeq) {
      return;
    }
    const records = (data && data.list) || [];
    total.value = (data && data.total) || 0;
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
  if (activeTab.value.key === tab.key) {
    return;
  }
  activeTab.value = tab;
  emptyText.value = EMPTY_TEXT[tab.key] || EMPTY_TEXT.enrolled;
  activityList.value = [];
  total.value = 0;
  loadStatus.value = 'loadmore';
  loadData(true);
}

// 点击卡片：跳活动详情
function goDetail(item) {
  if (!item.activityId) {
    return;
  }
  uni.navigateTo({ url: '/pages/activity/detail?id=' + item.activityId });
}
</script>

<style lang="scss" scoped>
.my-act-page {
  min-height: 100vh;
  background: $fc-bg;
}

.act-tabs {
  display: flex;
  background: #ffffff;
  padding: 0 24rpx;
  position: sticky;
  top: 0;
  z-index: 10;
}

.act-tab {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 30rpx;
  color: $fc-text-sub;
  position: relative;
}

.act-tab--active {
  color: $fc-text-main;
  font-weight: 600;
}

.act-tab--active::after {
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

.act-list {
  padding: 24rpx;
}

.act-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 28rpx 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.act-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.act-title {
  flex: 1;
  margin-right: 16rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.act-badge {
  flex-shrink: 0;
  font-size: 24rpx;
  font-weight: 600;
}

.badge--enrolled {
  color: $fc-primary;
}

.badge--signed {
  color: #07c160;
}

.badge--finished {
  color: $fc-text-sub;
}

.act-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-top: 14rpx;
  font-size: 24rpx;
}

.act-label {
  flex-shrink: 0;
  margin-right: 24rpx;
  color: $fc-text-sub;
}

.act-value {
  text-align: right;
  color: #606266;
  word-break: break-all;
}

.act-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #f0f1f3;
}

.act-score {
  font-size: 26rpx;
  font-weight: 600;
  color: $fc-primary;
}

.act-price {
  font-size: 26rpx;
  font-weight: 600;
  color: $fc-price;
}

.act-price--free {
  color: #07c160;
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
