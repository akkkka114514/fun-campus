<template>
  <view class="profile-container">
    <!-- 用户信息区域 -->
    <view class="user-info-section">
      <view class="user-background">
        <image class="background-image" src="@/static/profile-bg.jpg" mode="aspectFill"></image>
        <view class="gradient-overlay"></view>
      </view>

      <view class="user-card">
        <view class="avatar-section">
          <tm-avatar
              :size="120"
              :round="30"
              :img="userStore.userInfo.avatar || defaultAvatar"
              class="user-avatar"
          ></tm-avatar>

          <view class="user-details" @click="openLink('pages/user/edit')">
            <view class="username">{{ userStore.userInfo.nickname || '未设置昵称' }}</view>
            <view class="user-id">ID: {{ userStore.userInfo.id || '未知' }}</view>
            <tm-icon name="tmicon-edit" :font-size="24" color="#ffffff" class="edit-icon" @click="openLink('pages/user/edit')"></tm-icon>
          </view>
        </view>
      </view>
    </view>

    <!-- 统计信息区域 -->
    <view class="stats-section">
      <view class="stats-card">
        <view class="stat-item" @click="openLink('others/activity/checkin')">
          <view class="stat-icon pending-checkin">
            <tm-icon name="tmicon-calendar-check" :font-size="40" color="#ffffff"></tm-icon>
          </view>
          <view class="stat-info">
            <view class="stat-number">{{ stats.pendingCheckin }}</view>
            <view class="stat-label">待签到</view>
          </view>
        </view>

        <view class="stat-item" @click="openLink('others/activity/checkout')">
          <view class="stat-icon pending-checkout">
            <tm-icon name="tmicon-sign-out" :font-size="40" color="#ffffff"></tm-icon>
          </view>
          <view class="stat-info">
            <view class="stat-number">{{ stats.pendingCheckout }}</view>
            <view class="stat-label">待签退</view>
          </view>
        </view>

        <view class="stat-item" @click="openLink('others/activity/review')">
          <view class="stat-icon pending-review">
            <tm-icon name="tmicon-star-fill" :font-size="40" color="#ffffff"></tm-icon>
          </view>
          <view class="stat-info">
            <view class="stat-number">{{ stats.pendingReview }}</view>
            <view class="stat-label">待评价</view>
          </view>
        </view>
      </view>
    </view>

    <!-- 功能菜单区域 -->
    <view class="menu-section">
      <view class="menu-grid">
        <view class="menu-item" @click="openLink('/pages/user/score')">
          <view class="menu-icon points">
            <tm-icon name="tmicon-gift-fill" :font-size="48" color="#ffffff"></tm-icon>
          </view>
          <text class="menu-label">实践积分</text>
        </view>

        <view class="menu-item" @click="openLink('/pages/user/activity')">
          <view class="menu-icon activities">
            <tm-icon name="tmicon-flag-fill" :font-size="48" color="#ffffff"></tm-icon>
          </view>
          <text class="menu-label">我的活动</text>
        </view>

        <view class="menu-item" @click="openLink('/pages/user/team')">
          <view class="menu-icon tribe">
            <tm-icon name="tmicon-users-fill" :font-size="48" color="#ffffff"></tm-icon>
          </view>
          <text class="menu-label">我的部落</text>
        </view>

        <view class="menu-item" @click="openLink('pages/user/apply')">
          <view class="menu-icon applications">
            <tm-icon name="tmicon-file-text-fill" :font-size="48" color="#ffffff"></tm-icon>
          </view>
          <text class="menu-label">我的申请</text>
        </view>

        <view class="menu-item" @click="openLink('pages/user/evaluate')">
          <view class="menu-icon reviews">
            <tm-icon name="tmicon-heart-fill" :font-size="48" color="#ffffff"></tm-icon>
          </view>
          <text class="menu-label">我的评价</text>
        </view>
      </view>
    </view>

    <!-- 设置与帮助 -->
    <view class="settings-section">
      <tm-cell :margin="[0, 0]" :bottomBorder="true" @click="openLink('others/help/help')">
        <template v-slot:title>
          <view class="flex items-center">
            <tm-icon name="tmicon-question-circle-fill" :font-size="28" color="#555050"></tm-icon>
            <text class="ml-15">帮助中心</text>
          </view>
        </template>
        <template v-slot:right>
          <tm-icon name="tmicon-angle-right" :font-size="28" color="#cccccc"></tm-icon>
        </template>
      </tm-cell>

      <tm-cell :margin="[0, 0]" :bottomBorder="true" @click="openLink('pages/settings/settings')">
        <template v-slot:title>
          <view class="flex items-center">
            <tm-icon name="tmicon-setting-fill" :font-size="28" color="#555050"></tm-icon>
            <text class="ml-15">设置</text>
          </view>
        </template>
        <template v-slot:right>
          <tm-icon name="tmicon-angle-right" :font-size="28" color="#cccccc"></tm-icon>
        </template>
      </tm-cell>

      <tm-cell :margin="[0, 0]" @click="handleLogout">
        <template v-slot:title>
          <view class="flex items-center">
            <tm-icon name="tmicon-poweroff" :font-size="28" color="#ff4757"></tm-icon>
            <text class="ml-15" style="color: #ff4757;">退出登录</text>
          </view>
        </template>
      </tm-cell>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { openLink } from '@/common/tools'

const userStore = useUserStore()
const defaultAvatar = '/static/default-avatar.png'

// 统计数据（实际项目中应该从接口获取）
const stats = ref({
  pendingCheckin: 3,
  pendingCheckout: 1,
  pendingReview: 2
})

// 处理退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: function (res) {
      if (res.confirm) {
        userStore.logout()
        openLink('pages/login/login')
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7fa 0%, #e4edf9 100%);
  padding-bottom: 30rpx;
}

/* 用户信息区域 */
.user-info-section {
  position: relative;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.user-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 280rpx;
  border-radius: 0 0 40rpx 40rpx;
  overflow: hidden;
  z-index: 1;
}

.background-image {
  width: 100%;
  height: 100%;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 100%);
  opacity: 0.9;
}

.user-card {
  position: relative;
  z-index: 2;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10rpx);
  border-radius: 30rpx;
  padding: 40rpx 30rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
  border: 1rpx solid rgba(255, 255, 255, 0.3);
}

.avatar-section {
  display: flex;
  align-items: center;
}

.user-avatar {
  border: 4rpx solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10rpx 20rpx rgba(0, 0, 0, 0.15);
}

.user-details {
  flex: 1;
  margin-left: 30rpx;
  color: #ffffff;
  position: relative;
  padding-right: 50rpx;
}

.username {
  font-size: 36rpx;
  font-weight: 600;
  margin-bottom: 10rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}

.user-id {
  font-size: 26rpx;
  opacity: 0.9;
}

.edit-icon {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  padding: 10rpx;
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 统计信息区域 */
.stats-section {
  padding: 0 30rpx;
  margin-bottom: 30rpx;
}

.stats-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 30rpx;
  padding: 30rpx;
  display: flex;
  justify-content: space-around;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.8);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-10rpx);
  }
}

.stat-icon {
  width: 90rpx;
  height: 90rpx;
  border-radius: 25rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.15);

  &.pending-checkin {
    background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 100%);
  }

  &.pending-checkout {
    background: linear-gradient(135deg, #4a6fcc 0%, #5d8bf4 100%);
  }

  &.pending-review {
    background: linear-gradient(135deg, #ffcc00 0%, #ff9500 100%);
  }
}

.stat-info {
  text-align: center;
}

.stat-number {
  font-size: 36rpx;
  font-weight: 700;
  color: #333;
  margin-bottom: 5rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #666;
}

/* 功能菜单区域 */
.menu-section {
  padding: 0 30rpx;
  margin-bottom: 30rpx;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20rpx;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 30rpx;
  padding: 40rpx 20rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.8);
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-10rpx);
  }
}

.menu-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 25rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.15);

  &.points {
    background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 100%);
  }

  &.activities {
    background: linear-gradient(135deg, #4a6fcc 0%, #5d8bf4 100%);
  }

  &.tribe {
    background: linear-gradient(135deg, #ffcc00 0%, #ff9500 100%);
  }

  &.applications {
    background: linear-gradient(135deg, #2ed573 0%, #1dd1a1 100%);
  }

  &.reviews {
    background: linear-gradient(135deg, #ff6b81 0%, #ff4757 100%);
  }
}

.menu-label {
  font-size: 24rpx;
  color: #333;
  text-align: center;
}

/* 设置区域 */
.settings-section {
  padding: 0 30rpx;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 30rpx;
  overflow: hidden;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.8);
}

.flex {
  display: flex;
}

.items-center {
  align-items: center;
}

.ml-15 {
  margin-left: 15rpx;
}
</style>