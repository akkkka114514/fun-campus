<script setup lang="ts">
import { ref } from 'vue';
import tmTabs from '@/tmui/components/tm-tabs/tm-tabs.vue';
import tmTabsPane from '@/tmui/components/tm-tabs-pane/tm-tabs-pane.vue';
import tmText from '@/tmui/components/tm-text/tm-text.vue';
import tmIcon from '@/tmui/components/tm-icon/tm-icon.vue';
</script>

<template>
  <view class="apply-page">
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
      <!-- 待审核选项卡 -->
      <tm-tabs-pane name="pending" title="待审核">
        <view class="content-section">
          <view class="empty-state" v-if="true">
            <tm-icon name="tmicon-info-circle" :font-size="60" color="#cccccc"></tm-icon>
            <tm-text :font-size="28" color="#999" label="暂无待审核的申请"></tm-text>
          </view>
          
          <view class="apply-item" v-for="item in 2" :key="item">
            <view class="apply-card">
              <view class="apply-header">
                <view class="activity-info">
                  <tm-text :font-size="30" label="校园文化节志愿者招募" :bold="true"></tm-text>
                  <tm-text :font-size="24" color="#999" label="申请时间：2023-06-01 14:30"></tm-text>
                </view>
                <view class="status-tag pending-tag">
                  <tm-text :font-size="22" label="待审核"></tm-text>
                </view>
              </view>
              <view class="apply-content">
                <tm-text :font-size="26" label="申请理由：我对这个活动非常感兴趣，希望能参与其中贡献自己的力量。"></tm-text>
              </view>
              <view class="apply-footer">
                <tm-text :font-size="24" color="#999" label="申请人：张三"></tm-text>
              </view>
            </view>
          </view>
        </view>
      </tm-tabs-pane>
      
      <!-- 已通过选项卡 -->
      <tm-tabs-pane name="approved" title="已通过">
        <view class="content-section">
          <view class="empty-state" v-if="false">
            <tm-icon name="tmicon-info-circle" :font-size="60" color="#cccccc"></tm-icon>
            <tm-text :font-size="28" color="#999" label="暂无已通过的申请"></tm-text>
          </view>
          
          <view class="apply-item" v-for="item in 1" :key="item">
            <view class="apply-card approved-card">
              <view class="apply-header">
                <view class="activity-info">
                  <tm-text :font-size="30" label="校园歌手大赛工作人员" :bold="true"></tm-text>
                  <tm-text :font-size="24" color="#999" label="审核时间：2023-06-02 09:15"></tm-text>
                </view>
                <view class="status-tag approved-tag">
                  <tm-text :font-size="22" label="已通过"></tm-text>
                </view>
              </view>
              <view class="apply-content">
                <tm-text :font-size="26" label="审核意见：您的申请已通过，请按时参加活动。"></tm-text>
              </view>
              <view class="apply-footer">
                <tm-text :font-size="24" color="#999" label="审核人：李老师"></tm-text>
              </view>
            </view>
          </view>
        </view>
      </tm-tabs-pane>
      
      <!-- 已驳回选项卡 -->
      <tm-tabs-pane name="rejected" title="已驳回">
        <view class="content-section">
          <view class="empty-state" v-if="true">
            <tm-icon name="tmicon-info-circle" :font-size="60" color="#cccccc"></tm-icon>
            <tm-text :font-size="28" color="#999" label="暂无被驳回的申请"></tm-text>
          </view>
          
          <view class="apply-item" v-for="item in 1" :key="item">
            <view class="apply-card rejected-card">
              <view class="apply-header">
                <view class="activity-info">
                  <tm-text :font-size="30" label="毕业典礼志愿者" :bold="true"></tm-text>
                  <tm-text :font-size="24" color="#999" label="审核时间：2023-05-28 16:45"></tm-text>
                </view>
                <view class="status-tag rejected-tag">
                  <tm-text :font-size="22" label="已驳回"></tm-text>
                </view>
              </view>
              <view class="apply-content">
                <tm-text :font-size="26" label="驳回原因：名额已满，感谢您的参与。"></tm-text>
              </view>
              <view class="apply-footer">
                <tm-text :font-size="24" color="#999" label="审核人：王主任"></tm-text>
              </view>
            </view>
          </view>
        </view>
      </tm-tabs-pane>
    </tm-tabs>
  </view>
</template>

<style scoped>
.apply-page {
  background-color: #f8f8f8;
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

.apply-item {
  margin-bottom: 30rpx;
}

.apply-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border-left: 8rpx solid #eeeeee;
}

.apply-card:hover {
  transform: translateY(-5rpx);
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
}

.approved-card {
  border-left-color: #4caf50;
}

.rejected-card {
  border-left-color: #f44336;
}

.apply-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.activity-info {
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

.rejected-tag {
  background-color: #ffebee;
  color: #f44336;
}

.apply-content {
  padding: 20rpx 0;
  border-top: 1rpx dashed #f0f0f0;
  border-bottom: 1rpx dashed #f0f0f0;
  margin-bottom: 20rpx;
}

.apply-footer {
  display: flex;
  justify-content: flex-end;
}
</style>