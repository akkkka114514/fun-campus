<template>
  <view class="home-page">
    <!-- 顶部工具栏 + 筛选条（sticky 吸顶） -->
    <view class="home-sticky">
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
          <view class="home-action" @click="goCalendar">
            <u-icon name="calendar" size="22" color="#303133" />
          </view>
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

      <!-- 筛选条：分类 / 状态 / 时间 -->
      <view class="filter-bar">
        <view
          v-for="item in filterTriggers"
          :key="item.key"
          class="filter-trigger"
          :class="{ 'filter-trigger--active': item.selected || activePanel === item.key }"
          @click="togglePanel(item.key)"
        >
          <text class="filter-trigger-text">{{ item.label }}</text>
          <u-icon
            name="arrow-down"
            size="12"
            :color="item.selected || activePanel === item.key ? '#3c7cff' : '#909399'"
          />
        </view>
        <view v-if="hasFilter" class="filter-reset" @click="resetFilters">
          <u-icon name="reload" size="14" color="#909399" />
          <text class="filter-reset-text">重置</text>
        </view>
      </view>

      <!-- 筛选下拉面板（点击遮罩关闭） -->
      <view v-if="activePanel" class="filter-mask" @click="activePanel = null"></view>
      <view v-if="activePanel" class="filter-panel">
        <view
          v-for="opt in panelOptions"
          :key="String(opt.value)"
          class="filter-option"
          :class="{ 'filter-option--active': filters[activePanel] === opt.value }"
          @click="selectFilter(opt.value)"
        >
          <text>{{ opt.label }}</text>
          <u-icon
            v-if="filters[activePanel] === opt.value"
            name="checkmark"
            size="16"
            color="#3c7cff"
          />
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
      <text class="empty-text">
        {{ loading ? '加载中...' : hasFilter ? '暂无符合筛选条件的活动' : '暂无活动' }}
      </text>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { homeApi } from '@/api/home-api';
import { activityApi } from '@/api/activity-api';
import { refreshMessageBadge } from '@/api/message-api';
import { getToken } from '@/utils/auth';
import { resolveFileUrl, formatDateTime, fenToYuan } from '@/utils/format';

const PAGE_SIZE = 10;

// 筛选选项（状态/时间）；分类选项由接口动态加载
const STATUS_OPTIONS = [
  { label: '不限', value: null },
  { label: '未开始', value: 0 },
  { label: '报名中', value: 1 },
  { label: '报名结束', value: 2 },
  { label: '进行中', value: 3 },
  { label: '已结束', value: 4 },
];
const TIME_OPTIONS = [
  { label: '不限', value: null },
  { label: '今天', value: 1 },
  { label: '近7天', value: 7 },
  { label: '近30天', value: 30 },
];
const STATUS_LABELS = { 0: '未开始', 1: '报名中', 2: '报名结束', 3: '进行中', 4: '已结束' };
const TIME_LABELS = { 1: '今天', 7: '近7天', 30: '近30天' };

const activeTab = ref(1);
const pageNum = ref(1);
const activityList = ref([]);
const total = ref(0);
const loading = ref(false);
// loadmore-可继续加载 loading-加载中 nomore-没有更多
const loadStatus = ref('loadmore');
// 消息未读数（入口红点）
const unreadCount = ref(0);
// 当前筛选条件（null=不限），与分页/切 tab 联动重查
const filters = reactive({ categoryId: null, status: null, timeRange: null });
// 活动分类列表（筛选项，进页加载一次）
const categories = ref([]);
// 当前展开的筛选面板：categoryId/status/timeRange，null=未展开
const activePanel = ref(null);

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
  if (!categories.value.length) {
    loadCategories();
  }
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
    const params = {
      activeActivityPage: activeTab.value,
      pageNum: pageNum.value,
      pageSize: PAGE_SIZE,
    };
    if (filters.categoryId != null) {
      params.categoryId = filters.categoryId;
    }
    if (filters.status != null) {
      params.status = filters.status;
    }
    if (filters.timeRange != null) {
      params.timeRange = filters.timeRange;
    }
    const { data } = await homeApi.homeData(params);
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

// ------- 筛选：分类 / 状态 / 时间 -------

// 触发器文案：未选时显示维度名，已选时显示所选值
function currentFilterLabel(key, fallback) {
  const value = filters[key];
  if (value == null) {
    return fallback;
  }
  if (key === 'categoryId') {
    const hit = categories.value.find((c) => c.id === value);
    return hit ? hit.name : fallback;
  }
  if (key === 'status') {
    return STATUS_LABELS[value] || fallback;
  }
  return TIME_LABELS[value] || fallback;
}

const filterTriggers = computed(() => [
  { key: 'categoryId', label: currentFilterLabel('categoryId', '分类'), selected: filters.categoryId != null },
  { key: 'status', label: currentFilterLabel('status', '状态'), selected: filters.status != null },
  { key: 'timeRange', label: currentFilterLabel('timeRange', '时间'), selected: filters.timeRange != null },
]);

const hasFilter = computed(
  () => filters.categoryId != null || filters.status != null || filters.timeRange != null
);

// 当前面板的选项列表
const panelOptions = computed(() => {
  if (activePanel.value === 'categoryId') {
    return [{ label: '不限', value: null }].concat(
      categories.value.map((c) => ({ label: c.name, value: c.id }))
    );
  }
  if (activePanel.value === 'status') {
    return STATUS_OPTIONS;
  }
  if (activePanel.value === 'timeRange') {
    return TIME_OPTIONS;
  }
  return [];
});

function togglePanel(key) {
  activePanel.value = activePanel.value === key ? null : key;
}

// 筛选条件变化后重置列表重查
function reloadList() {
  activityList.value = [];
  total.value = 0;
  loadStatus.value = 'loadmore';
  loadData(true);
}

function selectFilter(value) {
  if (!activePanel.value) {
    return;
  }
  if (filters[activePanel.value] === value) {
    activePanel.value = null;
    return;
  }
  filters[activePanel.value] = value;
  activePanel.value = null;
  reloadList();
}

function resetFilters() {
  filters.categoryId = null;
  filters.status = null;
  filters.timeRange = null;
  activePanel.value = null;
  reloadList();
}

async function loadCategories() {
  try {
    const { data } = await homeApi.categoryList();
    categories.value = data || [];
  } catch (e) {
    // 分类加载失败不阻塞列表（分类筛选项仅显示"不限"）
  }
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

function goCalendar() {
  uni.navigateTo({ url: '/pages/calendar/calendar' });
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

/* 吸顶区：工具栏 + 筛选条 */
.home-sticky {
  position: sticky;
  top: 0;
  z-index: 10;
  background: #ffffff;
}

.home-toolbar {
  display: flex;
  align-items: center;
  background: #ffffff;
  padding: 0 24rpx;
}

/* 筛选条：分类 / 状态 / 时间 / 重置 */
.filter-bar {
  display: flex;
  align-items: center;
  padding: 0 24rpx 16rpx;
  position: relative;
  z-index: 2;
}

.filter-trigger {
  display: flex;
  align-items: center;
  margin-right: 36rpx;
  padding: 4rpx 0;
}

.filter-trigger-text {
  max-width: 200rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 26rpx;
  color: $fc-text-sub;
  margin-right: 4rpx;
}

.filter-trigger--active .filter-trigger-text {
  color: $fc-primary;
  font-weight: 600;
}

.filter-reset {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.filter-reset-text {
  margin-left: 4rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

/* 透明遮罩：点击任意处收起面板 */
.filter-mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}

.filter-panel {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #ffffff;
  max-height: 480rpx;
  overflow-y: auto;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
  z-index: 11;
}

.filter-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
  font-size: 28rpx;
  color: $fc-text-main;
}

.filter-option--active {
  color: $fc-primary;
  font-weight: 600;
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
