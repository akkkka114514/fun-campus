<template>
  <view class="calendar-page">
    <!-- 顶部：范围切换 + 月份切换 -->
    <view class="cal-toolbar">
      <view class="page-tabs">
        <view
          class="page-tab"
          :class="{ 'page-tab--active': activePage === 1 }"
          @click="switchPage(1)"
        >
          本校活动
        </view>
        <view
          class="page-tab"
          :class="{ 'page-tab--active': activePage === 2 }"
          @click="switchPage(2)"
        >
          全局活动
        </view>
      </view>
    </view>

    <view class="cal-card">
      <!-- 月份导航 -->
      <view class="month-nav">
        <view class="month-btn" @click="prevMonth">&lt;</view>
        <text class="month-label">{{ monthLabel }}</text>
        <view class="month-btn" @click="nextMonth">&gt;</view>
      </view>

      <!-- 星期表头 -->
      <view class="week-head">
        <text v-for="w in weekNames" :key="w" class="week-cell">{{ w }}</text>
      </view>

      <!-- 日期格子 -->
      <view class="day-grid">
        <view
          v-for="cell in gridCells"
          :key="cell.key"
          class="day-cell"
          :class="{
            'day-cell--empty': cell.empty,
            'day-cell--selected': cell.isSelected,
            'day-cell--today': cell.isToday && !cell.isSelected,
          }"
          @click="selectDay(cell)"
        >
          <template v-if="!cell.empty">
            <text class="day-num">{{ cell.day }}</text>
            <view v-if="cell.hasActivity" class="day-dot" />
          </template>
        </view>
      </view>
    </view>

    <!-- 活动列表：当日 / 本月全部 -->
    <view class="list-head">
      <view class="list-tabs">
        <view
          class="list-tab"
          :class="{ 'list-tab--active': listMode === 'day' }"
          @click="listMode = 'day'"
        >
          当日活动
        </view>
        <view
          class="list-tab"
          :class="{ 'list-tab--active': listMode === 'month' }"
          @click="listMode = 'month'"
        >
          本月全部
        </view>
      </view>
      <text class="list-summary">{{ listSummary }}</text>
    </view>

    <view v-if="displayList.length" class="activity-list">
      <view
        v-for="item in displayList"
        :key="item.id"
        class="activity-card"
        @click="goDetail(item)"
      >
        <image
          class="activity-cover"
          :src="resolveFileUrl(item.coverImg)"
          mode="aspectFill"
        />
        <view class="activity-body">
          <view class="activity-title-row">
            <text class="activity-title">{{ item.title }}</text>
            <text class="status-tag" :class="statusClass(item.status)">{{ statusText(item.status) }}</text>
          </view>
          <text class="activity-meta">
            时间：{{ formatTimeRange(item.activityStartTime, item.activityEndTime) }}
          </text>
          <text class="activity-meta">地点：{{ item.position || '待定' }}</text>
          <text v-if="activePage === 2 && item.activityBelongToSchoolName" class="activity-meta">
            学校：{{ item.activityBelongToSchoolName }}
          </text>
        </view>
      </view>
    </view>

    <view v-else class="empty">
      <text class="empty-text">
        {{ loading ? '加载中...' : listMode === 'day' ? '当天暂无活动' : '本月暂无活动' }}
      </text>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue';
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { activityApi } from '@/api/activity-api';
import { getToken } from '@/utils/auth';
import { resolveFileUrl, formatTimeRange } from '@/utils/format';

const weekNames = ['日', '一', '二', '三', '四', '五', '六'];

const now = new Date();
const year = ref(now.getFullYear());
const month = ref(now.getMonth() + 1);

const activePage = ref(1);
const listMode = ref('day');
const selectedKey = ref(dateKey(year.value, month.value, now.getDate()));

const activities = ref([]);
// 日期 -> 该日覆盖的活动列表（活动跨天时每天都会命中）
const dayMap = ref(new Map());
const loading = ref(false);

const todayKey = dateKey(now.getFullYear(), now.getMonth() + 1, now.getDate());

const monthLabel = computed(() => `${year.value}年${month.value}月`);

const gridCells = computed(() => {
  const cells = [];
  const firstWeekday = new Date(year.value, month.value - 1, 1).getDay();
  const daysInMonth = new Date(year.value, month.value, 0).getDate();
  for (let i = 0; i < firstWeekday; i++) {
    cells.push({ empty: true, key: `empty-${i}` });
  }
  for (let d = 1; d <= daysInMonth; d++) {
    const key = dateKey(year.value, month.value, d);
    cells.push({
      empty: false,
      key,
      day: d,
      hasActivity: dayMap.value.has(key),
      isToday: key === todayKey,
      isSelected: key === selectedKey.value,
    });
  }
  return cells;
});

const displayList = computed(() => {
  if (listMode.value === 'month') {
    return activities.value;
  }
  if (!selectedKey.value) {
    return [];
  }
  return dayMap.value.get(selectedKey.value) || [];
});

const listSummary = computed(() => {
  if (listMode.value === 'month') {
    return `共 ${activities.value.length} 场`;
  }
  if (!selectedKey.value) {
    return '未选择日期';
  }
  const [, m, d] = selectedKey.value.split('-');
  return `${Number(m)}月${Number(d)}日 · ${displayList.value.length} 场`;
});

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  if (!activities.value.length) {
    loadData();
  }
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
    const lastDay = new Date(year.value, month.value, 0).getDate();
    const { data } = await activityApi.calendar({
      startDate: dateKey(year.value, month.value, 1),
      endDate: dateKey(year.value, month.value, lastDay),
      activeActivityPage: activePage.value,
    });
    activities.value = (data || []).slice().sort((a, b) => {
      const ta = a.activityStartTime || '';
      const tb = b.activityStartTime || '';
      return ta < tb ? -1 : ta > tb ? 1 : 0;
    });
    buildDayMap();
  } catch (e) {
    // 请求层已统一 toast
  } finally {
    loading.value = false;
  }
}

// 活动时间与月份有交集的每一天都打点
function buildDayMap() {
  const map = new Map();
  const monthStart = dateKey(year.value, month.value, 1);
  const lastDay = new Date(year.value, month.value, 0).getDate();
  const monthEnd = dateKey(year.value, month.value, lastDay);
  activities.value.forEach((item) => {
    const start = (item.activityStartTime || '').slice(0, 10);
    const end = (item.activityEndTime || item.activityStartTime || '').slice(0, 10);
    if (!start) {
      return;
    }
    let cursor = start < monthStart ? monthStart : start;
    const last = !end || end > monthEnd ? monthEnd : end;
    while (cursor <= last) {
      if (!map.has(cursor)) {
        map.set(cursor, []);
      }
      map.get(cursor).push(item);
      cursor = nextDayKey(cursor);
    }
  });
  dayMap.value = map;
}

function selectDay(cell) {
  if (cell.empty) {
    return;
  }
  selectedKey.value = cell.key;
  listMode.value = 'day';
}

function prevMonth() {
  if (month.value === 1) {
    month.value = 12;
    year.value -= 1;
  } else {
    month.value -= 1;
  }
  afterMonthChange();
}

function nextMonth() {
  if (month.value === 12) {
    month.value = 1;
    year.value += 1;
  } else {
    month.value += 1;
  }
  afterMonthChange();
}

function afterMonthChange() {
  selectedKey.value = '';
  activities.value = [];
  dayMap.value = new Map();
  loadData();
}

function switchPage(page) {
  if (activePage.value === page) {
    return;
  }
  activePage.value = page;
  activities.value = [];
  dayMap.value = new Map();
  loadData();
}

function goDetail(item) {
  if (!item.id) {
    return;
  }
  uni.navigateTo({ url: `/pages/activity/detail?id=${item.id}` });
}

function dateKey(y, m, d) {
  const mm = String(m).padStart(2, '0');
  const dd = String(d).padStart(2, '0');
  return `${y}-${mm}-${dd}`;
}

function nextDayKey(key) {
  const [y, m, d] = key.split('-').map(Number);
  const next = new Date(y, m - 1, d + 1);
  return dateKey(next.getFullYear(), next.getMonth() + 1, next.getDate());
}

function statusText(status) {
  if (status === 0) {
    return '待报名';
  }
  if (status === 1) {
    return '报名中';
  }
  if (status === 2) {
    return '报名已结束';
  }
  if (status === 3) {
    return '进行中';
  }
  if (status === 4) {
    return '已结束';
  }
  if (status === 9) {
    return '待报名审核';
  }
  return '未知';
}

function statusClass(status) {
  if (status === 1) {
    return 'status-tag--enrolling';
  }
  if (status === 3) {
    return 'status-tag--ongoing';
  }
  if (status === 0 || status === 9) {
    return 'status-tag--waiting';
  }
  return 'status-tag--done';
}
</script>

<style lang="scss" scoped>
.calendar-page {
  min-height: 100vh;
  background: $fc-bg;
  padding-bottom: 40rpx;
}

.cal-toolbar {
  background: #ffffff;
  padding: 0 24rpx;
}

.page-tabs {
  display: flex;
}

.page-tab {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 30rpx;
  color: $fc-text-sub;
  position: relative;
}

.page-tab--active {
  color: $fc-text-main;
  font-weight: 600;
}

.page-tab--active::after {
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

.cal-card {
  background: $fc-card-bg;
  margin: 24rpx;
  border-radius: 20rpx;
  padding: 24rpx 16rpx 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.month-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24rpx 20rpx;
}

.month-btn {
  width: 64rpx;
  height: 64rpx;
  line-height: 60rpx;
  text-align: center;
  border-radius: 50%;
  background: #f5f6f8;
  color: $fc-text-main;
  font-size: 30rpx;
}

.month-label {
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-text-main;
}

.week-head {
  display: flex;
}

.week-cell {
  flex: 1;
  text-align: center;
  font-size: 24rpx;
  color: $fc-text-sub;
  padding: 12rpx 0;
}

.day-grid {
  display: flex;
  flex-wrap: wrap;
  padding-top: 8rpx;
}

.day-cell {
  position: relative;
  width: calc(100% / 7);
  height: 88rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.day-cell--empty {
  visibility: hidden;
}

.day-num {
  font-size: 28rpx;
  color: $fc-text-main;
  width: 64rpx;
  height: 64rpx;
  line-height: 64rpx;
  text-align: center;
  border-radius: 50%;
}

.day-cell--selected .day-num {
  background: $fc-primary;
  color: #ffffff;
  font-weight: 600;
}

.day-cell--today .day-num {
  border: 2rpx solid $fc-primary;
  box-sizing: border-box;
  color: $fc-primary;
}

.day-dot {
  position: absolute;
  bottom: 2rpx;
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  background: $fc-price;
}

.list-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8rpx 24rpx 20rpx;
}

.list-tabs {
  display: flex;
}

.list-tab {
  margin-right: 20rpx;
  padding: 8rpx 28rpx;
  border-radius: 28rpx;
  background: #ffffff;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.list-tab--active {
  background: #e8eefc;
  color: $fc-primary;
  font-weight: 600;
}

.list-summary {
  font-size: 24rpx;
  color: $fc-text-sub;
}

.activity-list {
  padding: 0 24rpx;
}

.activity-card {
  display: flex;
  background: $fc-card-bg;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.activity-cover {
  flex-shrink: 0;
  width: 200rpx;
  height: 200rpx;
  background: #eceef2;
}

.activity-body {
  flex: 1;
  padding: 20rpx;
  overflow: hidden;
}

.activity-title-row {
  display: flex;
  align-items: center;
}

.activity-title {
  flex: 1;
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-tag {
  flex-shrink: 0;
  margin-left: 12rpx;
  padding: 2rpx 14rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
}

.status-tag--enrolling {
  color: #19be6b;
  border: 1rpx solid #19be6b;
}

.status-tag--ongoing {
  color: #ff9900;
  border: 1rpx solid #ff9900;
}

.status-tag--waiting {
  color: $fc-primary;
  border: 1rpx solid $fc-primary;
}

.status-tag--done {
  color: #909399;
  border: 1rpx solid #909399;
}

.activity-meta {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty {
  padding-top: 120rpx;
  display: flex;
  justify-content: center;
}

.empty-text {
  font-size: 28rpx;
  color: $fc-text-sub;
}
</style>
