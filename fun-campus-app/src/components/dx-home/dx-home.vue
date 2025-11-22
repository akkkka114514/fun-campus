<template>
  <view class="home-container">
    <!-- 轮播图区域 -->
    <view class="bg-white carousel-section">
      <tm-carousel autoplay :margin="[0, 0]" :round="16" :width="750" :height="380" :list="appStore.app_carousel"></tm-carousel>
    </view>
    
    <!-- 快捷功能区 -->
    <view class="py-20 bg-white quick-actions">
      <tm-grid :width="750" :col="4">
        <tm-grid-item :height="180" @click="openLink('/others/follow/follow')" class="action-item">
          <image class="icon" src="../../static/a2.png"></image>
          <tm-text :font-size="22" label="我的关注"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="orange" :height="180" @click="openLink('/others/activity/activity')" class="action-item">
          <image class="icon" src="../../static/a3.png"></image>
          <tm-text :font-size="22" label="我的活动"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="green" :height="180" @click="openLink('/teams/index/index')" class="action-item">
          <image class="icon" src="../../static/a4.png"></image>
          <tm-text :font-size="22" label="我的组织"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="pink" :height="180" @click="openLink('/others/publish/publish')" class="action-item">
          <image class="icon" src="../../static/a1.png"></image>
          <tm-text :font-size="22" label="发布活动"></tm-text>
        </tm-grid-item>
      </tm-grid>
    </view>
    
    <!-- 推荐活动 -->
    <view class="section-header bg-white">
      <view class="section-title text-black">推荐活动</view>
      <view class="more-link" @click="openLink('/others/activity/activity')">
        <text class="more-text">更多</text>
        <tm-icon name="tmicon-angle-right" color="#999999"></tm-icon>
      </view>
    </view>
    <view class="list bg-white">
      <view class="item flex-row-center-between pa-30" v-for="(item, index) in activity" :key="index" @click="openLink('others/activity/detail?id=' + item._id)">
        <view>
          <image class="round-3 cover-image" :src="item.cover"></image>
        </view>
        <view class="pl-20 flex flex-1 flex-col flex-between content-area">
          <view>
            <view class="title text-overflow-2">{{ item.title }}</view>
            <view class="address mt-20 flex-col-top-center">
              <tm-icon name="tmicon-position" color="#999999" :font-size="24"></tm-icon>
              <text class="ml-8 text-overflow-1 address-text">{{ item.address }}</text>
            </view>
          </view>
          <view class="time flex flex-between">
            <view class="flex-col-top-center">
              <tm-icon name="tmicon-time-fill" color="#999999"></tm-icon>
              <text class="tips">{{ timeText(item.start_date) }}</text>
            </view>
            <text class="tips" v-if="!item.is_hidden_views">浏览数：{{ item.views || 0 }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 推荐组织 -->
    <view class="section-header bg-white mt-30">
      <view class="section-title text-black">推荐组织</view>
      <view class="more-link" @click="openLink('/teams/index/index')">
        <text class="more-text">更多</text>
        <tm-icon name="tmicon-angle-right" color="#999999"></tm-icon>
      </view>
    </view>
    <view class="bg-white recommend-section">
      <tm-scrollx :width="750" :height="300">
        <view class="recommend">
          <view class="item my-30 mr-30 flex flex-between pa-30 round-3" @click="openLink('teams/detail/detail?id=' + item._id)" v-for="(item, index) in team" :key="index">
            <view>
              <tm-avatar :size="180" :img="item.logo"></tm-avatar>
            </view>
            <view class="flex-1 ml-30 right">
              <view class="top flex align-center">
                <view class="name">{{ item.title }}</view>
                <view class="ml-15 flex align-center" v-if="!item.is_identification">
                  <tm-icon name="tmicon-minus-circle" color="#999999" :font-size="24"></tm-icon>
                  <text class="ml-5 approve">未认证</text>
                </view>
                <view class="ml-15 flex align-center" v-else>
                  <tm-icon name="tmicon-check-circle-fill" color="#2ed573" :font-size="24"></tm-icon>
                  <text class="ml-5 success">已认证</text>
                </view>
              </view>
              <view class="tips">
                <text>活动数量:</text>
                <text class="mx-10 num">{{ item.activity_count }}</text>
                <text>场</text>
              </view>
              <view>
                <view class="btn" :class="{ 'follow': item.is_follow }" @click.stop="toFollow(item._id)">{{
                  item.is_follow ? '已关注' : '关注' }}</view>
              </view>
            </view>
          </view>
          <view style="color: #ffffff">1</view>
        </view>
      </tm-scrollx>
    </view>
    
    <!-- 最新活动 -->
    <view class="section-header bg-white mt-30">
      <view class="section-title text-black">最新活动</view>
      <view class="more-link" @click="openLink('/others/activity/activity')">
        <text class="more-text">更多</text>
        <tm-icon name="tmicon-angle-right" color="#999999"></tm-icon>
      </view>
    </view>
    <view class="list2 bg-white px-30 flex flex-between flex-wrap round-3">
      <view class="item pb-20 mb-30 round-3" v-for="(item, index) in newActivity" :key="index" @click="openLink('/others/activity/detail?id=' + item._id)">
        <image class="cover-image-small" :src="item.cover"></image>
        <view class="px-20 pt-10 content-area-small">
          <view class="title text-overflow-2"> {{ item.title }} </view>
          <view class="flex align-center mt-15">
            <tm-icon name="tmicon-clock" color="#6d6868" :font-size="24"></tm-icon>
            <text class="tips ml-10">{{ timeText(item.start_date) }}</text>
          </view>
          <view class="flex align-center mt-8">
            <tm-icon name="tmicon-position" color="#6d6868" :font-size="24"></tm-icon>
            <text class="tips ml-10 text-overflow-1">{{ item.address }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { openLink,timeText } from '@/common/tools';
import { useAppStore } from '@/stores/app'
import { indexHome,followTeam } from '@/common/index'
const appStore = useAppStore();
const activity = ref<any>([]);
const newActivity = ref<any>([]);
const team = ref<any>([]);

function init() {
	indexHome().then(res => {
		if (res.code === 1000) {
			activity.value = res.data.activity;
			newActivity.value = res.data.newActivity;
			team.value = res.data.team;
		}
	})
}

onMounted(() => {
  init();
});

function toFollow(id: string) {
	// 关注
  followTeam({id}).then(res=>{
    uni.$tm.u.toast(res.message);
    if(res.code===1000){
      init()
    }
  })
}
</script>
<style lang="scss" scoped>
.home-container {
  padding-bottom: 20rpx;
}

.carousel-section {
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
  margin: 0 24rpx 20rpx;
}

.quick-actions {
  border-radius: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
  margin: 0 24rpx 20rpx;
  padding: 30rpx 0;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.icon {
	width: 90rpx;
	height: 90rpx;
	margin-bottom: 20rpx;
  border-radius: 18rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.03);
  margin: 0 24rpx 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.more-link {
  display: flex;
  align-items: center;
  padding: 10rpx 15rpx;
  border-radius: 30rpx;
  transition: background-color 0.2s ease;
  
  &:active {
    background-color: #f5f5f5;
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
    transition: background-color 0.2s ease;
    
    &:not(:last-child) {
      border-bottom: 2rpx solid #f0f0f0;
    }
    
    &:active {
      background-color: #f9f9f9;
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
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    
    &:active {
      transform: scale(0.98);
    }
    
    &:hover {
      box-shadow: 0 10rpx 24rpx rgba(0, 0, 0, 0.08);
    }
	}
}

.cover-image {
  width: 220rpx;
  height: 200rpx;
  border-radius: 12rpx;
  object-fit: cover;
}

.cover-image-small {
  width: 100%;
  height: 250rpx;
  object-fit: cover;
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
}

.recommend-section {
  border-radius: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
  margin: 0 24rpx 20rpx;
  overflow: hidden;
}

.recommend {
	display: flex;
	padding-left: 30rpx;

	.item {
		position: relative;
		box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.04);
    border-radius: 16rpx;
    transition: transform 0.2s ease;
    
    &:active {
      transform: scale(0.98);
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
			color: #3c8af8;
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
      transition: all 0.2s ease;

			&.follow {
				border: 2rpx solid #3c8af8;
				color: #3c8af8;
				background-color: rgba(60, 138, 248, 0.1);
			}
      
      &:active {
        transform: translateY(-50%) scale(0.95);
      }
		}
	}
}
</style>