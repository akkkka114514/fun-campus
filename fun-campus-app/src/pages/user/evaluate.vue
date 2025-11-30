<script setup lang="ts">
import { ref } from 'vue';
import tmTabs from '@/tmui/components/tm-tabs/tm-tabs.vue';
import tmTabsPane from '@/tmui/components/tm-tabs-pane/tm-tabs-pane.vue';
import tmText from '@/tmui/components/tm-text/tm-text.vue';
import tmIcon from '@/tmui/components/tm-icon/tm-icon.vue';
</script>

<template>
  <view class="evaluate-page">
    <tm-tabs 
      default-name="pending" 
      :width="750" 
      color="#FFFFFF"
      active-color="#FFFFFF"
      active-font-color="#333333"
      :show-tabs-line-ani="true"
      tabs-line-ani-color="#ff8c42"
      :item-height="80"
      :gutter="0"
    >
      <!-- 待评价选项卡 -->
      <tm-tabs-pane name="pending" title="待评价">
        <view class="content-section">
          <view class="empty-state" v-if="true">
            <tm-icon name="tmicon-info-circle" :font-size="60" color="#cccccc"></tm-icon>
            <tm-text :font-size="28" color="#999" label="暂无待评价的活动"></tm-text>
          </view>
          
          <view class="evaluate-item" v-for="item in 2" :key="item">
            <view class="evaluate-card">
              <view class="activity-header">
                <image class="activity-cover" src="/static/logo.png"></image>
                <view class="activity-info">
                  <tm-text :font-size="30" label="校园文化节" :bold="true"></tm-text>
                  <tm-text :font-size="24" color="#999" label="活动时间：2023-06-01"></tm-text>
                  <tm-text :font-size="24" color="#999" label="参与时间：2023-06-02"></tm-text>
                </view>
              </view>
              <view class="evaluate-action">
                <tm-text :font-size="26" color="#ff8c42" label="立即评价"></tm-text>
              </view>
            </view>
          </view>
        </view>
      </tm-tabs-pane>
      
      <!-- 已评价选项卡 -->
      <tm-tabs-pane name="evaluated" title="已评价">
        <view class="content-section">
          <view class="empty-state" v-if="false">
            <tm-icon name="tmicon-info-circle" :font-size="60" color="#cccccc"></tm-icon>
            <tm-text :font-size="28" color="#999" label="暂无已评价的活动"></tm-text>
          </view>
          
          <view class="evaluate-item" v-for="item in 1" :key="item">
            <view class="evaluate-card evaluated-card">
              <view class="activity-header">
                <image class="activity-cover" src="/static/logo.png"></image>
                <view class="activity-info">
                  <tm-text :font-size="30" label="校园歌手大赛" :bold="true"></tm-text>
                  <tm-text :font-size="24" color="#999" label="活动时间：2023-05-28"></tm-text>
                  <tm-text :font-size="24" color="#999" label="评价时间：2023-05-29"></tm-text>
                </view>
              </view>
              <view class="evaluate-preview">
                <tm-text :font-size="26" label="评分："></tm-text>
                <view class="stars">
                  <tm-icon name="tmicon-star-fill" color="#ffcc00" :font-size="24"></tm-icon>
                  <tm-icon name="tmicon-star-fill" color="#ffcc00" :font-size="24"></tm-icon>
                  <tm-icon name="tmicon-star-fill" color="#ffcc00" :font-size="24"></tm-icon>
                  <tm-icon name="tmicon-star-fill" color="#ffcc00" :font-size="24"></tm-icon>
                  <tm-icon name="tmicon-star-fill" color="#ffcc00" :font-size="24"></tm-icon>
                </view>
              </view>
              <view class="evaluate-content">
                <tm-text :font-size="26" label="评价内容：活动组织得很好，期待下次还能参与！"></tm-text>
              </view>
            </view>
          </view>
        </view>
      </tm-tabs-pane>
    </tm-tabs>
  </view>
</template>

<style scoped>
.evaluate-page {
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

.evaluate-item {
  margin-bottom: 30rpx;
}

.evaluate-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.evaluate-card:hover {
  transform: translateY(-5rpx);
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
}

.evaluated-card {
  border-left: 8rpx solid #4caf50;
}

.activity-header {
  display: flex;
  margin-bottom: 20rpx;
}

.activity-cover {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
}

.activity-info {
  flex: 1;
}

.evaluate-action {
  display: flex;
  justify-content: center;
  padding: 20rpx;
  border-top: 1rpx solid #f0f0f0;
  margin-top: 20rpx;
}

.evaluate-preview {
  display: flex;
  align-items: center;
  padding: 10rpx 0;
}

.stars {
  display: flex;
  margin-left: 10rpx;
}

.evaluate-content {
  padding-top: 20rpx;
  border-top: 1rpx dashed #f0f0f0;
  margin-top: 20rpx;
}
</style>