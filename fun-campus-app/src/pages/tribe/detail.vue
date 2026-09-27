<template>
  <view class="detail-page">
    <!-- 头部信息 -->
    <view class="head-card">
      <view class="head-main">
        <image
          v-if="detail.icon"
          class="head-icon"
          :src="resolveFileUrl(detail.icon)"
          mode="aspectFill"
        />
        <view v-else class="head-icon head-icon--fallback">
          <text class="head-icon-char">{{ firstChar(detail.name) }}</text>
        </view>
        <view class="head-info">
          <view class="head-name-row">
            <text class="head-name">{{ detail.name || '部落详情' }}</text>
            <text class="belong-tag">{{ belongText(detail.belongTo) }}</text>
          </view>
          <text class="head-meta">主席：{{ detail.presidentName || '待定' }}</text>
          <text class="head-meta">热度 {{ detail.memberNum || 0 }} · {{ joinedText }}</text>
        </view>
      </view>

      <text v-if="detail.description" class="head-desc">{{ detail.description }}</text>

      <!-- 加入状态 / 申请入口 -->
      <view class="head-action">
        <view v-if="detail.joinedFlag" class="action-tag action-tag--joined">已加入该部落</view>
        <view v-else-if="detail.myApplicationStatus === 0" class="action-tag action-tag--wait">
          加入申请审核中，请耐心等待
        </view>
        <view v-else-if="detail.myApplicationStatus === 1" class="action-tag action-tag--joined">
          申请已通过
        </view>
        <view v-else class="apply-btn" @click="openApply">
          {{ detail.myApplicationStatus === 2 ? '重新申请加入' : '申请加入' }}
        </view>
        <text v-if="detail.myApplicationStatus === 2" class="reject-tip">
          上次申请已被驳回，可修改理由后重新提交
        </text>
      </view>
    </view>

    <!-- 成员 / 活动 Tab -->
    <view class="tab-bar">
      <view
        class="tab-item"
        :class="{ 'tab-item--active': activeTab === 'member' }"
        @click="switchTab('member')"
      >
        成员
      </view>
      <view
        class="tab-item"
        :class="{ 'tab-item--active': activeTab === 'activity' }"
        @click="switchTab('activity')"
      >
        部落活动
      </view>
    </view>

    <!-- 成员列表 -->
    <view v-if="activeTab === 'member'" class="list-wrap">
      <view v-if="memberList.length" class="member-list">
        <view v-for="item in memberList" :key="item.portalUserId" class="member-item">
          <image
            v-if="item.avatar"
            class="member-avatar"
            :src="resolveFileUrl(item.avatar)"
            mode="aspectFill"
          />
          <view v-else class="member-avatar member-avatar--fallback">
            <text class="member-char">{{ firstChar(item.username) }}</text>
          </view>
          <view class="member-body">
            <text class="member-name">{{ item.username }}</text>
            <text class="member-time">加入时间：{{ formatDateTime(item.createTime) }}</text>
          </view>
        </view>
        <view class="load-status">
          <text v-if="memberLoadStatus === 'loading'">加载中...</text>
          <text v-else-if="memberLoadStatus === 'nomore'">没有更多了</text>
        </view>
      </view>
      <view v-else class="empty-box">
        <text class="empty-text">{{ memberLoading ? '加载中...' : '暂无成员' }}</text>
      </view>
    </view>

    <!-- 活动列表 -->
    <view v-else class="list-wrap">
      <view v-if="activityList.length">
        <view
          v-for="item in activityList"
          :key="item.activityId"
          class="activity-card"
          @click="goActivity(item)"
        >
          <image
            class="activity-cover"
            :src="resolveFileUrl(item.coverImg)"
            mode="aspectFill"
          />
          <view class="activity-body">
            <view class="activity-title-row">
              <text class="activity-title">{{ item.title }}</text>
              <text class="status-tag" :class="activityStatusClass(item.activityStatus)">
                {{ activityStatusText(item.activityStatus) }}
              </text>
            </view>
            <text class="activity-meta">
              时间：{{ formatTimeRange(item.activityStartTime, item.activityEndTime) }}
            </text>
            <text class="activity-meta">地点：{{ item.position || '待定' }}</text>
            <view class="activity-foot">
              <text class="activity-enroll">已报名 {{ item.enrollNum || 0 }}</text>
              <text v-if="item.paidFlag" class="price-tag">¥{{ fenToYuan(item.priceFen) }}</text>
              <text v-else class="free-tag">免费</text>
            </view>
          </view>
        </view>
        <view class="load-status">
          <text v-if="activityLoadStatus === 'loading'">加载中...</text>
          <text v-else-if="activityLoadStatus === 'nomore'">没有更多了</text>
        </view>
      </view>
      <view v-else class="empty-box">
        <text class="empty-text">{{ activityLoading ? '加载中...' : '该部落暂无活动' }}</text>
      </view>
    </view>

    <!-- 申请加入弹层 -->
    <view v-if="applyVisible" class="sheet-mask" @click="closeApply">
      <view class="sheet-body" @click.stop>
        <text class="sheet-title">申请加入「{{ detail.name }}」</text>
        <textarea
          v-model="applyReason"
          class="sheet-textarea"
          :maxlength="500"
          placeholder="请填写加入理由（500 字以内），如兴趣特长、想参与的方向等"
          placeholder-class="sheet-placeholder"
        />
        <text class="sheet-count">{{ applyReason.length }}/500</text>
        <view class="sheet-actions">
          <view class="sheet-btn sheet-btn--cancel" @click="closeApply">取消</view>
          <view
            class="sheet-btn sheet-btn--primary"
            :class="{ 'sheet-btn--disabled': applying }"
            @click="submitApply"
          >
            {{ applying ? '提交中...' : '提交申请' }}
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue';
import { onLoad, onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { tribeApi } from '@/api/tribe-api';
import { getToken } from '@/utils/auth';
import { resolveFileUrl, formatDateTime, formatTimeRange, fenToYuan } from '@/utils/format';

const MEMBER_PAGE_SIZE = 15;
const ACTIVITY_PAGE_SIZE = 10;

const tribeId = ref(null);
const detail = ref({});
const activeTab = ref('member');

// 成员分页
const memberList = ref([]);
const memberPageNum = ref(1);
const memberTotal = ref(0);
const memberLoading = ref(false);
const memberLoadStatus = ref('loadmore');

// 活动分页
const activityList = ref([]);
const activityPageNum = ref(1);
const activityTotal = ref(0);
const activityLoading = ref(false);
const activityLoadStatus = ref('loadmore');

// 加入申请
const applyVisible = ref(false);
const applyReason = ref('');
const applying = ref(false);

const joinedText = computed(() => (detail.value.joinedFlag ? '你已加入' : '你尚未加入'));

onLoad((options) => {
  tribeId.value = options && options.tribeId ? Number(options.tribeId) : null;
});

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  if (!tribeId.value) {
    uni.showToast({ title: '参数错误', icon: 'none' });
    return;
  }
  if (!detail.value.id) {
    loadDetail();
    loadMembers(true);
    loadActivities(true);
  }
});

onPullDownRefresh(async () => {
  await Promise.all([loadDetail(), loadMembers(true), loadActivities(true)]);
  uni.stopPullDownRefresh();
});

onReachBottom(() => {
  if (activeTab.value === 'member') {
    if (memberLoadStatus.value === 'loadmore' && !memberLoading.value) {
      loadMembers(false);
    }
  } else if (activityLoadStatus.value === 'loadmore' && !activityLoading.value) {
    loadActivities(false);
  }
});

async function loadDetail() {
  if (!tribeId.value) {
    return;
  }
  try {
    const { data } = await tribeApi.detail(tribeId.value);
    detail.value = data || {};
  } catch (e) {
    // 请求层已统一 toast
  }
}

async function loadMembers(reset) {
  if (memberLoading.value && !reset) {
    return;
  }
  memberLoading.value = true;
  if (reset) {
    memberPageNum.value = 1;
  }
  memberLoadStatus.value = 'loading';
  try {
    const { data } = await tribeApi.memberPage({
      tribeId: tribeId.value,
      pageNum: memberPageNum.value,
      pageSize: MEMBER_PAGE_SIZE,
    });
    const records = (data && data.list) || [];
    memberTotal.value = (data && data.total) || 0;
    memberList.value = reset ? records : memberList.value.concat(records);
    if (records.length < MEMBER_PAGE_SIZE || memberList.value.length >= memberTotal.value) {
      memberLoadStatus.value = 'nomore';
    } else {
      memberLoadStatus.value = 'loadmore';
      memberPageNum.value += 1;
    }
  } catch (e) {
    memberLoadStatus.value = 'loadmore';
  } finally {
    memberLoading.value = false;
  }
}

async function loadActivities(reset) {
  if (activityLoading.value && !reset) {
    return;
  }
  activityLoading.value = true;
  if (reset) {
    activityPageNum.value = 1;
  }
  activityLoadStatus.value = 'loading';
  try {
    const { data } = await tribeApi.activityPage({
      tribeId: tribeId.value,
      pageNum: activityPageNum.value,
      pageSize: ACTIVITY_PAGE_SIZE,
    });
    const records = (data && data.list) || [];
    activityTotal.value = (data && data.total) || 0;
    activityList.value = reset ? records : activityList.value.concat(records);
    if (records.length < ACTIVITY_PAGE_SIZE || activityList.value.length >= activityTotal.value) {
      activityLoadStatus.value = 'nomore';
    } else {
      activityLoadStatus.value = 'loadmore';
      activityPageNum.value += 1;
    }
  } catch (e) {
    activityLoadStatus.value = 'loadmore';
  } finally {
    activityLoading.value = false;
  }
}

function switchTab(tab) {
  activeTab.value = tab;
}

function openApply() {
  applyReason.value = '';
  applyVisible.value = true;
}

function closeApply() {
  applyVisible.value = false;
}

async function submitApply() {
  if (applying.value) {
    return;
  }
  const reason = applyReason.value.trim();
  if (!reason) {
    uni.showToast({ title: '请填写加入理由', icon: 'none' });
    return;
  }
  applying.value = true;
  try {
    await tribeApi.apply({ tribeId: tribeId.value, reason });
    applyVisible.value = false;
    uni.showToast({ title: '申请已提交，等待审核', icon: 'none' });
    await loadDetail();
  } catch (e) {
    // 请求层已统一 toast（已加入 / 已有待审核申请等）
  } finally {
    applying.value = false;
  }
}

function goActivity(item) {
  if (!item.activityId) {
    return;
  }
  uni.navigateTo({ url: `/pages/activity/detail?id=${item.activityId}` });
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

function activityStatusText(status) {
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

function activityStatusClass(status) {
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
.detail-page {
  min-height: 100vh;
  background: $fc-bg;
  padding-bottom: 40rpx;
}

.head-card {
  background: $fc-card-bg;
  padding: 32rpx 24rpx 28rpx;
}

.head-main {
  display: flex;
  align-items: center;
}

.head-icon {
  flex-shrink: 0;
  width: 128rpx;
  height: 128rpx;
  border-radius: 32rpx;
  background: #eceef2;
}

.head-icon--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e8eefc;
}

.head-icon-char {
  font-size: 52rpx;
  font-weight: 600;
  color: $fc-primary;
}

.head-info {
  flex: 1;
  margin-left: 28rpx;
  overflow: hidden;
}

.head-name-row {
  display: flex;
  align-items: center;
}

.head-name {
  font-size: 38rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.belong-tag {
  flex-shrink: 0;
  margin-left: 16rpx;
  padding: 2rpx 16rpx;
  border-radius: 999rpx;
  background: #e8eefc;
  color: $fc-primary;
  font-size: 22rpx;
}

.head-meta {
  display: block;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.head-desc {
  display: block;
  margin-top: 24rpx;
  font-size: 28rpx;
  color: $fc-text-main;
  line-height: 1.7;
}

.head-action {
  margin-top: 28rpx;
}

.apply-btn {
  background: $fc-primary;
  border-radius: 44rpx;
  text-align: center;
  padding: 22rpx 0;
  font-size: 30rpx;
  color: #ffffff;
}

.action-tag {
  border-radius: 44rpx;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
}

.action-tag--joined {
  background: #e9f8ef;
  color: #19be6b;
}

.action-tag--wait {
  background: #fff4e6;
  color: #ff9900;
}

.reject-tip {
  display: block;
  margin-top: 12rpx;
  text-align: center;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.tab-bar {
  display: flex;
  background: $fc-card-bg;
  margin-top: 20rpx;
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

.list-wrap {
  padding: 24rpx;
}

.member-list {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 8rpx 24rpx;
}

.member-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f6f8;
}

.member-item:last-child {
  border-bottom: none;
}

.member-avatar {
  flex-shrink: 0;
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: #eceef2;
}

.member-avatar--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e8eefc;
}

.member-char {
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-primary;
}

.member-body {
  flex: 1;
  margin-left: 24rpx;
}

.member-name {
  font-size: 30rpx;
  color: $fc-text-main;
}

.member-time {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
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
  height: 280rpx;
  background: #eceef2;
}

.activity-body {
  padding: 24rpx;
}

.activity-title-row {
  display: flex;
  align-items: center;
}

.activity-title {
  flex: 1;
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-tag {
  flex-shrink: 0;
  margin-left: 16rpx;
  padding: 4rpx 16rpx;
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
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.activity-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20rpx;
}

.activity-enroll {
  font-size: 24rpx;
  color: $fc-text-sub;
}

.price-tag {
  font-size: 26rpx;
  color: $fc-price;
}

.free-tag {
  font-size: 26rpx;
  color: #19be6b;
}

.load-status {
  text-align: center;
  padding: 24rpx 0;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.empty-box {
  padding: 120rpx 0;
  text-align: center;
}

.empty-text {
  font-size: 28rpx;
  color: $fc-text-sub;
}

/* 申请加入弹层 */
.sheet-mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 100;
  display: flex;
  align-items: flex-end;
}

.sheet-body {
  width: 100%;
  background: #ffffff;
  border-radius: 28rpx 28rpx 0 0;
  padding: 36rpx 32rpx calc(36rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
}

.sheet-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: $fc-text-main;
}

.sheet-textarea {
  width: 100%;
  height: 220rpx;
  margin-top: 24rpx;
  padding: 20rpx;
  box-sizing: border-box;
  background: #f5f6f8;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: $fc-text-main;
}

.sheet-placeholder {
  color: #c0c4cc;
}

.sheet-count {
  display: block;
  margin-top: 8rpx;
  text-align: right;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.sheet-actions {
  display: flex;
  margin-top: 28rpx;
}

.sheet-btn {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  border-radius: 44rpx;
  font-size: 30rpx;
}

.sheet-btn--cancel {
  background: #f5f6f8;
  color: $fc-text-main;
  margin-right: 20rpx;
}

.sheet-btn--primary {
  background: $fc-primary;
  color: #ffffff;
}

.sheet-btn--disabled {
  opacity: 0.6;
}
</style>
