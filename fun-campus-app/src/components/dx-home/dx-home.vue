<template>
  <view class="home-container">
    <!-- 快捷功能区 -->
    <view class="py-20 bg-white quick-actions">
      <tm-grid :width="750" :col="4" class="transparent-grid">
        <tm-grid-item :height="180" @click="openLink('/others/follow/follow')" class="action-item transparent-grid-item">
          <image class="icon" src="../../static/a2.png"></image>
          <tm-text :font-size="22" label="学分认定"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="orange" :height="180" @click="openLink('/pages/team/list')" class="action-item transparent-grid-item">
          <image class="icon" src="../../static/a3.png"></image>
          <tm-text :font-size="22" label="二课部落"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="green" :height="180" @click="openLink('/teams/index/index')" class="action-item transparent-grid-item">
          <image class="icon" src="../../static/a4.png"></image>
          <tm-text :font-size="22" label="校园生活"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="blue" :height="180" @click="openLink('/others/publish/publish')" class="action-item transparent-grid-item">
          <image class="icon" src="../../static/a1.png"></image>
          <tm-text :font-size="22" label="活动日历"></tm-text>
        </tm-grid-item>
      </tm-grid>
    </view>
    
    <!-- 活动列表 -->
    <view class="activity-list">
      <!-- 水平选项卡 -->
      <view class="horizontal-tabs">
        <view class="tab-header">
          <view
              class="tab-item"
              :class="{ active: activeTab === 0 }"
              @click="activeTab = 0"
          >
            二课活动
          </view>
          <view
              class="tab-item"
              :class="{ active: activeTab === 1 }"
              @click="activeTab = 1"
          >
            全国活动
          </view>
          <view
              class="tab-indicator"
              :style="{
            width: '50%',
            transform: `translateX(${activeTab * 100}%)`,
            backgroundColor: '#ff8c42'
          }"
          ></view>
        </view>
      </view>
      
      <!-- 活动卡片列表 -->
      <view class="activity-cards">
        <view class="card-list">
          <view 
            v-for="(activity, index) in (activeTab === 0 ? mySchoolActivities : globalActivities)" 
            :key="index" 
            class="card-item"
            @click="goToActivityDetail(activity.id)"
          >
            <view class="card-content">
              <tm-text :font-size="28" :label="activity.title" class="card-title"></tm-text>
              <tm-text :font-size="24" :label="activity.description" class="card-desc"></tm-text>
              <view class="card-footer">
                <tm-text :font-size="22" :label="activity.position" class="card-location"></tm-text>
              </view>
            </view>
          </view>
          
          <!-- 示例卡片，用于演示样式 -->
          <view class="card-item" v-if="(activeTab === 0 ? mySchoolActivities : globalActivities).length === 0">
            <view class="card-content">
              <tm-text :font-size="28" :label="(activeTab === 0 ? '本校特色活动' : '全国大型活动')" class="card-title"></tm-text>
              <tm-text :font-size="24" label="暂无相关活动信息" class="card-desc"></tm-text>
              <view class="card-footer">
                <tm-text :font-size="22" label="敬请期待" class="card-time"></tm-text>
                <tm-text :font-size="22" label="" class="card-location"></tm-text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { openLink } from '@/common/tools';
import axios from "axios";
import { homeData } from "@/common/Api";

const activeTab = ref(0);
const globalActivities= ref([]);
const mySchoolActivities = ref([]);

function init() {
  uni.getStorage({
    key: 'userInfo',
    success: function(res){
      console.log(res.data.token)
      axios.defaults.headers.common['Authorization'] = 'Bearer ' + res.data.token;
    },
    fail: function(){
      uni.$tm.u.toast('请先登录');
    }
  })
  
  // 获取首页活动数据
  homeData(1, 1, 10).then(res => {
    console.log(res.data)
    // 解析活动数据并保存
    if (res.data.code === 0) {
      globalActivities.value = res.data.data.globalActivities.records || [];
      mySchoolActivities.value = res.data.data.mySchoolActivities.records || [];
    }
  }).catch(e => {
    uni.$tm.u.toast(e);
  })
}

// 跳转到活动详情页
function goToActivityDetail(id) {
  openLink(`/pages/activity/detail?id=${id}`);
}

onMounted(() => {
  init();
});
</script>
<style lang="scss" scoped>
.home-container {
  padding-bottom: 20rpx;
  position: relative;
  overflow: hidden;
}

.carousel-section {
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
  margin: 0 24rpx 20rpx;
  position: relative;
  transform: translateY(0);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0% {
    transform: translateY(0) translateX(0);
  }
  50% {
    transform: translateY(-10rpx) translateX(5rpx);
  }
  100% {
    transform: translateY(0) translateX(0);
  }
}

.quick-actions {
  border-radius: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
  margin: 0 24rpx 20rpx;
  padding: 30rpx 0;
  background: linear-gradient(135deg, #4a6fcc 0%, #3a5fbb 100%);
  position: relative;
  overflow: hidden;
}

.quick-actions::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0) 70%);
  transform: rotate(30deg);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
  
  &:active {
    transform: scale(0.95);
  }
  
  &:hover {
    transform: translateY(-10rpx);
  }
}

.icon {
	width: 90rpx;
	height: 90rpx;
	margin-bottom: 20rpx;
  border-radius: 18rpx;
  box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.1) rotate(5deg);
    box-shadow: 0 12rpx 20rpx rgba(0, 0, 0, 0.15);
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.03);
  margin: 0 24rpx 20rpx;
  background: linear-gradient(90deg, #4a6fcc 0%, #5d8bf4 50%, #5d8bf4 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
    transform: translateX(-100%);
    animation: shimmer 2s infinite;
  }
}

@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  position: relative;
  z-index: 2;
  text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1);
}

.more-link {
  display: flex;
  align-items: center;
  padding: 10rpx 15rpx;
  border-radius: 30rpx;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10rpx);
  position: relative;
  z-index: 2;
  
  &:active {
    background-color: #f5f5f5;
    transform: scale(0.95);
  }
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.5);
    transform: translateY(-2rpx);
    box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
  }
}

.more-text {
  font-size: 26rpx;
  color: #999;
  margin-right: 8rpx;
}

.list {
  border-radius: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
  margin: 0 24rpx 20rpx;
  overflow: hidden;
  
	.item {
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &:not(:last-child) {
      border-bottom: 2rpx solid #f0f0f0;
    }
    
    &:active {
      background-color: #f9f9f9;
    }
    
    &:hover {
      transform: translateX(10rpx);
      box-shadow: 5rpx 5rpx 15rpx rgba(0,0,0,0.1);
    }
    
    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
      transform: translateX(-100%);
      transition: transform 0.6s ease;
    }
    
    &:hover::before {
      transform: translateX(100%);
    }
	}
}

.list2 {
  border-radius: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
  margin: 0 24rpx 20rpx;
  padding: 20rpx 0;
  overflow: hidden;
  
	.item {
		width: calc((100vw - 110rpx) / 2);
		box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.04);
		overflow: hidden;
    border-radius: 16rpx;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:active {
      transform: scale(0.98);
    }
    
    &:hover {
      box-shadow: 0 15rpx 30rpx rgba(0, 0, 0, 0.15);
      transform: translateY(-10rpx) scale(1.02);
    }
	}
}

.cover-image {
  width: 220rpx;
  height: 200rpx;
  border-radius: 12rpx;
  object-fit: cover;
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.05);
  }
}

.cover-image-small {
  width: 100%;
  height: 250rpx;
  object-fit: cover;
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.05);
  }
}

.content-area {
  height: 200rpx;
}

.content-area-small {
  padding: 15rpx 20rpx;
}

.address {
	font-size: 26rpx;
}

.address-text {
  max-width: 280rpx;
}

.title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  transition: all 0.3s ease;
  
  &:hover {
    color: #4a6fcc;
  }
}

.recommend-section {
  border-radius: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
  margin: 0 24rpx 20rpx;
  overflow: hidden;
  background: linear-gradient(135deg, #5d8bf4 0%, #4a6fcc 100%);
  padding: 10rpx;
}

.recommend {
	display: flex;
	padding-left: 30rpx;

	.item {
		position: relative;
		box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.04);
    border-radius: 16rpx;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background: white;
    
    &:active {
      transform: scale(0.98);
    }
    
    &:hover {
      transform: translateY(-10rpx) scale(1.02);
      box-shadow: 0 15rpx 30rpx rgba(0, 0, 0, 0.15);
    }

		.right {
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			width: 420rpx;
		}

		.name {
      font-size: 30rpx;
      font-weight: 500;
      color: #333;
    }

		.num {
			color: #4a6fcc;
			font-size: 32rpx;
      font-weight: 600;
		}

		.btn {
			border: 2rpx solid #ddd;
			color: #666;
			border-radius: 50rpx;
			padding: 8rpx 24rpx;
			position: absolute;
			right: 30rpx;
			top: 50%;
			transform: translateY(-50%);
			font-size: 24rpx;
      transition: all 0.3s ease;
      background: linear-gradient(45deg, #5d8bf4, #7ba6ff);

			&.follow {
				border: 2rpx solid #4a6fcc;
				color: #4a6fcc;
				background: linear-gradient(45deg, #a1c4fd, #c2e9fb);
			}
      
      &:active {
        transform: translateY(-50%) scale(0.95);
      }
      
      &:hover {
        transform: translateY(-55%);
        box-shadow: 0 5rpx 15rpx rgba(0,0,0,0.1);
      }
		}
	}
}

@keyframes move {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(100rpx, 100rpx);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.transparent-grid {
  background: transparent !important;

  ::v-deep .tm-grid {
    background: transparent !important;
  }
}

.transparent-grid-item {
  background: transparent !important;
  backdrop-filter: none !important;

  ::v-deep .tm-grid-item {
    background: transparent !important;
    backdrop-filter: none !important;
  }
}
.horizontal-tabs {
  background-color: #ffffff;
}

.tab-header {
  position: relative;
  display: flex;
  flex-direction: row;
  height: 80rpx;
  border-bottom: 1rpx solid #eeeeee;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #666666;
  transition: color 0.3s ease;

  &.active {
    color: #ff8c42;
    font-weight: 500;
  }
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 6rpx;
  border-radius: 3rpx;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background-color: #ff8c42;
}

/* 活动卡片样式 */
.activity-cards {
  padding: 0 20rpx;
}

.card-list {
  display: flex;
  flex-direction: column;
}

.card-item {
  width: 100%;
  background: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
  margin-bottom: 20rpx;
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  &:active {
    transform: scale(0.99);
  }
  
  &:hover {
    transform: translateY(-5rpx);
    box-shadow: 0 12rpx 25rpx rgba(0, 0, 0, 0.1);
  }
}

.card-cover {
  width: 100%;
  height: 240rpx;
  object-fit: cover;
}

.card-content {
  padding: 20rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 10rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15rpx;
  border-top: 1rpx solid #f0f0f0;
}

.card-time, .card-location {
  font-size: 24rpx;
  color: #999;
  display: flex;
  align-items: center;
  
  &::before {
    margin-right: 10rpx;
    font-size: 24rpx;
  }
}

.card-time::before {
  content: "🕐";
}

.card-location::before {
  content: "📍";
}
</style>
