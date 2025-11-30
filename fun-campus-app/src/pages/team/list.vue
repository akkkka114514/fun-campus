<template>
  <view class="team-list-page">
    <!-- 顶部横条 -->
    <view class="top-bar">
      <view class="left-section">
        <button class="nav-button" @click="goBack">
          <text class="icon-font">&#xe600;</text>
        </button>
      </view>
      
      <view class="center-section">
        <text class="page-title">二课部落</text>
      </view>
      
      <view class="right-section">
        <button class="nav-button" @click="showSearch">
          <text class="icon-font">&#xe601;</text>
        </button>
        <button class="nav-button" @click="showFilter">
          <text class="icon-font">&#xe602;</text>
        </button>
      </view>
    </view>
    
    <!-- 页面内容区域 -->
    <view class="content-area">
      <!-- 这里放置页面主要内容 -->
    </view>
  </view>
</template>

<script setup lang="ts">
// 返回功能
function goBack() {
  console.log('返回上一页');
  // 实现返回逻辑
}

// 显示搜索功能
function showSearch() {
  console.log('显示搜索');
  // 实现搜索逻辑
}

// 显示筛选功能
function showFilter() {
  console.log('显示筛选');
  // 实现筛选逻辑
}
</script>

<style scoped>
.team-list-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.top-bar {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  background-color: #ffffff;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  padding: 0 20rpx;
  position: relative;
  z-index: 100;
}

.left-section, .right-section {
  display: flex;
  flex-direction: row;
  align-items: center;
  min-width: 120rpx;
}

.center-section {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.page-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.nav-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  background-color: transparent;
  border: none;
  margin: 0 10rpx;
  transition: all 0.3s ease;
}

.nav-button:active {
  background-color: #f0f0f0;
  transform: scale(0.9);
}

.icon-font {
  font-family: "iconfont" !important;
  font-size: 36rpx;
  font-style: normal;
  color: #666666;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20rpx;
}
</style>