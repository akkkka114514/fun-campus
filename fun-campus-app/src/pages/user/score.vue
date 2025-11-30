<script setup lang="ts">
import { ref } from 'vue'
import DxTabs from '@/components/dx-tabs/dx-tabs.vue'

const activeTab = ref(0)
const tabs = ref(['活动类', '成果类'])
</script>

<template>
  <view class="score-container">
    <dx-tabs 
      :tabs="tabs" 
      v-model="activeTab"
      active-color="#ff8c42"
      indicator-color="#ff8c42"
      active-bg-color="rgba(255, 140, 66, 0.1)"
    >
    </dx-tabs>
    
    <view class="content-container">
      <!-- 活动类内容 -->
      <view v-if="activeTab === 0" class="tab-content activity-content">
        <view class="total-score">
          <text class="total-label">活动类总积分</text>
          <text class="total-value">30</text>
        </view>
        
        <view class="score-list">
          <view class="score-item">
            <view class="item-info">
              <text class="item-title">文体艺术</text>
            </view>
            <view class="item-score positive">+15</view>
          </view>
          
          <view class="score-item">
            <view class="item-info">
              <text class="item-title">学术创新</text>
            </view>
            <view class="item-score positive">+10</view>
          </view>
          
          <view class="score-item">
            <view class="item-info">
              <text class="item-title">实习创业</text>
            </view>
            <view class="item-score positive">+5</view>
          </view>

          <view class="score-item">
            <view class="item-info">
              <text class="item-title">道德修养</text>
            </view>
            <view class="item-score positive">+5</view>
          </view>

          <view class="score-item">
            <view class="item-info">
              <text class="item-title">技能培训</text>
            </view>
            <view class="item-score positive">+5</view>
          </view>
        </view>
      </view>
      
      <!-- 成果类内容 -->
      <view v-if="activeTab === 1" class="tab-content achievement-content">
        <view class="total-score">
          <text class="total-label">成果类总积分</text>
          <text class="total-value">120</text>
        </view>
        
        <view class="score-list">
          <view class="score-item">
            <view class="item-info">
              <text class="item-title">学术活动类</text>
            </view>
            <view class="item-score positive">+30</view>
          </view>
          
          <view class="score-item">
            <view class="item-info">
              <text class="item-title">志愿服务类</text>
            </view>
            <view class="item-score positive">+50</view>
          </view>
          
          <view class="score-item">
            <view class="item-info">
              <text class="item-title">社会实践类</text>
            </view>
            <view class="item-score positive">+40</view>
          </view>
          <view class="score-item">
            <view class="item-info">
              <text class="item-title">成长成才类</text>
            </view>
            <view class="item-score positive">+40</view>
          </view>
          <view class="score-item">
            <view class="item-info">
              <text class="item-title">竞赛奖励类</text>
            </view>
            <view class="item-score positive">+40</view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.score-container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20rpx;
}

.content-container {
  padding: 20rpx;
}

.tab-content {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.content-header {
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.content-title {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 10rpx;
}

.content-subtitle {
  font-size: 26rpx;
  color: #999999;
}

.score-list {
  margin-bottom: 30rpx;
}

.score-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-info {
  flex: 1;
}

.item-title {
  display: block;
  font-size: 30rpx;
  color: #333333;
  margin-bottom: 10rpx;
}

.item-date {
  font-size: 24rpx;
  color: #999999;
}

.item-score {
  font-size: 32rpx;
  font-weight: 600;
  
  &.positive {
    color: #ff8c42;
  }
  
  &.negative {
    color: #ff4757;
  }
}

.total-score {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background: linear-gradient(90deg, #fff9f2 0%, #ffffff 100%);
  border-radius: 15rpx;
  border: 1rpx solid #ffccbc;
}

.total-label {
  font-size: 30rpx;
  font-weight: 500;
  color: #333333;
}

.total-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #ff8c42;
}
</style>