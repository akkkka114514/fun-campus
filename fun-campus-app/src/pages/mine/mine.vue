<template>
  <view class="mine-page">
    <!-- 用户卡片 -->
    <view class="user-card">
      <view class="avatar-wrap">
        <image v-if="avatarUrl" class="user-avatar" :src="avatarUrl" mode="aspectFill" />
        <text v-else class="avatar-text">{{ firstChar }}</text>
      </view>
      <view class="user-info">
        <text class="user-name">{{ userInfo.username || '未登录' }}</text>
        <text class="user-sub">{{ schoolText }}</text>
      </view>
    </view>

    <!-- 学分 / 信誉分 -->
    <view class="score-card">
      <view class="score-item">
        <text class="score-value">{{ scoreText(userInfo.gradeScore) }}</text>
        <text class="score-label">实践学分</text>
      </view>
      <view class="score-divider" />
      <view class="score-item">
        <text class="score-value">{{ scoreText(userInfo.creditScore) }}</text>
        <text class="score-label">信誉分</text>
      </view>
    </view>

    <!-- 功能菜单（fe-08 依次实现具体页面） -->
    <view class="menu-card">
      <view
        v-for="menu in menuList"
        :key="menu.key"
        class="menu-item"
        @click="onMenuClick(menu)"
      >
        <text class="menu-text">{{ menu.text }}</text>
        <text class="menu-arrow">></text>
      </view>
    </view>

    <view class="logout-btn" @click="handleLogout">退出登录</view>
  </view>
</template>

<script setup>
import { computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useUserStore } from '@/store/user';
import { refreshMessageBadge } from '@/api/message-api';
import { getToken } from '@/utils/auth';
import { resolveFileUrl } from '@/utils/format';

const userStore = useUserStore();

const userInfo = computed(() => userStore.userInfo || {});

const avatarUrl = computed(() =>
  userInfo.value.avatar ? resolveFileUrl(userInfo.value.avatar) : ''
);

const firstChar = computed(() => {
  const name = userInfo.value.username || '';
  return name ? name.slice(0, 1).toUpperCase() : '?';
});

const schoolText = computed(() => {
  const parts = [
    userInfo.value.schoolName,
    userInfo.value.collegeName,
    userInfo.value.gradeName,
  ].filter(Boolean);
  return parts.length ? parts.join(' · ') : '学校信息未设置';
});

const menuList = [
  { key: 'signinCode', text: '我的签到码', url: '/pages/signin/code' },
  { key: 'myActivity', text: '我的活动' },
  { key: 'myOrder', text: '我的订单' },
  { key: 'profile', text: '资料编辑' },
];

function scoreText(score) {
  return score == null ? '-' : String(Number(score));
}

function onMenuClick(menu) {
  if (menu.url) {
    uni.navigateTo({ url: menu.url });
    return;
  }
  uni.showToast({ title: '开发中，敬请期待', icon: 'none' });
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: async (res) => {
      if (!res.confirm) {
        return;
      }
      await userStore.logout();
      uni.reLaunch({ url: '/pages/login/login' });
    },
  });
}

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  refreshMessageBadge();
  // 每次进入强制刷新用户信息（失败时请求层会统一处理）
  userStore.fetchUserInfo(true).catch(() => {});
});
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 24rpx;
  box-sizing: border-box;
}

.user-card {
  display: flex;
  align-items: center;
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 40rpx 32rpx;
}

.avatar-wrap {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: #e8eefc;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.user-avatar {
  width: 100%;
  height: 100%;
}

.avatar-text {
  font-size: 48rpx;
  font-weight: 600;
  color: $fc-primary;
}

.user-info {
  margin-left: 24rpx;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 36rpx;
  font-weight: 600;
  color: $fc-text-main;
}

.user-sub {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.score-card {
  display: flex;
  background: $fc-card-bg;
  border-radius: 20rpx;
  margin-top: 24rpx;
  padding: 32rpx 0;
}

.score-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-value {
  font-size: 40rpx;
  font-weight: 700;
  color: $fc-primary;
}

.score-label {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.score-divider {
  width: 1rpx;
  background: #f0f1f3;
  margin: 8rpx 0;
}

.menu-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  margin-top: 24rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #f5f6f8;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-text {
  font-size: 30rpx;
  color: $fc-text-main;
}

.menu-arrow {
  font-size: 28rpx;
  color: #c0c4cc;
}

.logout-btn {
  margin-top: 48rpx;
  background: $fc-card-bg;
  border-radius: 20rpx;
  text-align: center;
  padding: 32rpx 0;
  font-size: 30rpx;
  color: #fa3534;
}
</style>
