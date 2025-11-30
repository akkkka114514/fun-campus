<template>
  <tm-app>
    <view class="demo-container">
      <view class="demo-title">DxTabs 水平选项卡演示</view>
      
      <!-- 基础用法 -->
      <view class="demo-section">
        <view class="section-title">基础用法</view>
        <dx-tabs 
          :tabs="['推荐', '热门', '最新', '关注']" 
          v-model="activeTab1"
        >
        </dx-tabs>
        
        <view class="tab-content">
          <view v-if="activeTab1 === 0">
            <view class="content-item">推荐内容</view>
            <view class="content-item">推荐内容</view>
            <view class="content-item">推荐内容</view>
          </view>
          <view v-if="activeTab1 === 1">
            <view class="content-item">热门内容</view>
            <view class="content-item">热门内容</view>
            <view class="content-item">热门内容</view>
          </view>
          <view v-if="activeTab1 === 2">
            <view class="content-item">最新内容</view>
            <view class="content-item">最新内容</view>
            <view class="content-item">最新内容</view>
          </view>
          <view v-if="activeTab1 === 3">
            <view class="content-item">关注内容</view>
            <view class="content-item">关注内容</view>
            <view class="content-item">关注内容</view>
          </view>
        </view>
      </view>
      
      <!-- 自定义样式 -->
      <view class="demo-section">
        <view class="section-title">自定义样式</view>
        <dx-tabs 
          :tabs="['选项一', '选项二', '选项三']" 
          v-model="activeTab2"
          active-color="#19be6b"
          indicator-color="#19be6b"
          active-bg-color="rgba(25, 190, 107, 0.1)"
        >
        </dx-tabs>
        
        <view class="tab-content">
          <view v-if="activeTab2 === 0">
            <view class="content-item green">绿色主题内容</view>
          </view>
          <view v-if="activeTab2 === 1">
            <view class="content-item green">绿色主题内容</view>
          </view>
          <view v-if="activeTab2 === 2">
            <view class="content-item green">绿色主题内容</view>
          </view>
        </view>
      </view>
    </view>
  </tm-app>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import DxTabs from './dx-tabs.vue'

const activeTab1 = ref(0)
const activeTab2 = ref(0)
</script>

<style scoped lang="scss">
.demo-container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.demo-title {
  font-size: 36rpx;
  font-weight: bold;
  text-align: center;
  padding: 30rpx 0;
  color: #333333;
}

.demo-section {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 30rpx;
  color: #333333;
}

.tab-content {
  padding: 20rpx 0;
}

.content-item {
  padding: 30rpx;
  background-color: #fff9f2;
  border-radius: 15rpx;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  color: #ff8c42;
  border: 1rpx solid #ffe8d5;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  &.green {
    background-color: #f0f9f4;
    color: #19be6b;
    border: 1rpx solid #d5f2e0;
  }
}
</style>