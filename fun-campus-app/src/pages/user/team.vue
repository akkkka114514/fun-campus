<script setup lang="ts">
import { ref } from 'vue';
import tmTabs from '@/tmui/components/tm-tabs/tm-tabs.vue';
import tmTabsPane from '@/tmui/components/tm-tabs-pane/tm-tabs-pane.vue';
import tmText from '@/tmui/components/tm-text/tm-text.vue';
import tmIcon from '@/tmui/components/tm-icon/tm-icon.vue';
</script>

<template>
  <view class="team-page">
    <tm-tabs 
      default-name="pending" 
      :width="750" 
      color="#FFFFFF"
      active-color="#FFFFFF"
      active-font-color="#333333"
      :show-tabs-line-ani="true"
      tabs-line-ani-color="#ff8c42"
      :item-height="80"
    >
      <tm-tabs-pane name="pending" title="加入待审核">
        <view class="content-section">
          <view class="empty-state" v-if="true">
            <tm-icon name="tmicon-info-circle" :font-size="60" color="#cccccc"></tm-icon>
            <tm-text :font-size="28" color="#999" label="暂无待审核的团队"></tm-text>
          </view>
          
          <view class="team-item" v-for="item in 3" :key="item">
            <view class="team-card">
              <view class="team-header">
                <image class="team-avatar" src="/static/logo.png"></image>
                <view class="team-info">
                  <tm-text :font-size="30" label="团队名称"></tm-text>
                  <tm-text :font-size="24" color="#999" label="申请时间：2023-06-01"></tm-text>
                </view>
                <view class="status-tag pending-tag">
                  <tm-text :font-size="22" label="待审核"></tm-text>
                </view>
              </view>
              <view class="team-content">
                <tm-text :font-size="26" label="团队简介：这是一个非常棒的团队，专注于各种校园活动的组织与开展。"></tm-text>
              </view>
            </view>
          </view>
        </view>
      </tm-tabs-pane>
      
      <tm-tabs-pane name="approved" title="已通过">
        <view class="content-section">
          <view class="empty-state" v-if="false">
            <tm-icon name="tmicon-info-circle" :font-size="60" color="#cccccc"></tm-icon>
            <tm-text :font-size="28" color="#999" label="暂无已通过的团队"></tm-text>
          </view>
          
          <view class="team-item" v-for="item in 2" :key="item">
            <view class="team-card">
              <view class="team-header">
                <image class="team-avatar" src="/static/logo.png"></image>
                <view class="team-info">
                  <tm-text :font-size="30" label="团队名称"></tm-text>
                  <tm-text :font-size="24" color="#999" label="通过时间：2023-06-01"></tm-text>
                </view>
                <view class="status-tag approved-tag">
                  <tm-text :font-size="22" label="已通过"></tm-text>
                </view>
              </view>
              <view class="team-content">
                <tm-text :font-size="26" label="团队简介：这是一个非常棒的团队，专注于各种校园活动的组织与开展。"></tm-text>
              </view>
            </view>
          </view>
        </view>
      </tm-tabs-pane>
    </tm-tabs>
  </view>
</template>

<style scoped>
.team-page {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.content-section {
  padding: 20rpx 30rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-state tm-icon {
  margin-bottom: 30rpx;
}

.team-item {
  margin-bottom: 30rpx;
}

.team-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.team-card:hover {
  transform: translateY(-5rpx);
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
}

.team-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.team-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  border: 2rpx solid #eeeeee;
}

.team-info {
  flex: 1;
}

.status-tag {
  padding: 8rpx 20rpx;
  border-radius: 30rpx;
  font-weight: 500;
}

.pending-tag {
  background-color: #fff3e0;
  color: #ff8c42;
}

.approved-tag {
  background-color: #e8f5e9;
  color: #4caf50;
}

.team-content {
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}
</style>