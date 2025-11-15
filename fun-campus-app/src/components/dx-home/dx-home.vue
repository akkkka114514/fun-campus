<template>
  <view class="bg-white">
    <tm-carousel autoplay :margin="[0, 0]" :round="0" :width="750" :height="380" :list="appStore.app_carousel"></tm-carousel>
  </view>
  <view class="py-20 bg-white">
    <tm-grid :width="750" :col="4">
      <tm-grid-item :height="180" @click="openLink('/others/follow/follow')">
        <image class="icon" src="../../static/a2.png"></image>
        <tm-text :font-size="22" label="我的关注"></tm-text>
      </tm-grid-item>
      <tm-grid-item color="orange" :height="180" @click="openLink('/others/activity/activity')">
        <image class="icon" src="../../static/a3.png"></image>
        <tm-text :font-size="22" label="我的活动"></tm-text>
      </tm-grid-item>
      <tm-grid-item color="green" :height="180" @click="openLink('/teams/index/index')">
        <image class="icon" src="../../static/a4.png"></image>
        <tm-text :font-size="22" label="我的组织"></tm-text>
      </tm-grid-item>
      <tm-grid-item color="pink" :height="180" @click="openLink('/others/publish/publish')">
        <image class="icon" src="../../static/a1.png"></image>
        <tm-text :font-size="22" label="发布活动"></tm-text>
      </tm-grid-item>
    </tm-grid>
  </view>
  <view class="flex-row-center-between pt-30 px-30 mt-30 bg-white">
    <view class="text-black">推荐活动</view>
    <view>
      <tm-icon name="tmicon-angle-right" color="#999999"></tm-icon>
    </view>
  </view>
  <view class="list bg-white">
    <view class="item flex-row-center-between pa-30" v-for="(item, index) in activity" :key="index" @click="openLink('others/activity/detail?id=' + item._id)">
      <view>
        <image class="round-3" :src="item.cover" style="width: 220rpx; height: 200rpx"></image>
      </view>
      <view class="pl-20 flex flex-1 flex-col flex-between" style="height: 200rpx">
        <view>
          <view class="title text-overflow-2">{{ item.title }}</view>
          <view class="address mt-20 flex-col-top-center">
            <tm-icon name="tmicon-position" color="#999999" :font-size="24"></tm-icon>
            <text class="ml-8 text-overflow-1">{{ item.address }}</text>
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
  <view class="flex-row-center-between pt-30 px-30 mt-30 bg-white">
    <view class="text-black">推荐组织</view>
    <view>
      <tm-icon name="tmicon-angle-right" color="#999999"></tm-icon>
    </view>
  </view>
  <view class="bg-white">
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
                <tm-icon name="tmicon-check-circle-fill" color="#0bbb08" :font-size="24"></tm-icon>
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
								item.is_follow ? '已关注' : '未关注' }}</view>
            </view>
          </view>
        </view>
        <view style="color: #ffffff">1</view>
      </view>
    </tm-scrollx>
  </view>
  <view class="flex-row-center-between pa-30 mt-30 bg-white">
    <view class="text-black">最新活动</view>
    <view>
      <tm-icon name="tmicon-angle-right" color="#999999"></tm-icon>
    </view>
  </view>
  <view class="list2 bg-white px-30 flex flex-between flex-wrap round-3">
    <view class="item pb-20 mb-30 round-3" v-for="(item, index) in newActivity" :key="index" @click="openLink('/others/activity/detail?id=' + item._id)">
      <image :src="item.cover" style="width: 100%; height: 250rpx"></image>
      <view class="px-20 pt-10">
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
</template>

<script setup lang="ts">
import { ref } from 'vue';
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
init();
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
.icon {
	width: 90rpx;
	height: 90rpx;
	margin-bottom: 20rpx;
}

.list {
	.item {
		&:not(:last-child) {
			border-bottom: 2rpx solid rgb(230, 230, 230);
		}
	}
}

.list2 {
	.item {
		width: calc((100vw - 90rpx) / 2);
		box-shadow: 0 0 10rpx rgb(230, 230, 230);
		overflow: hidden;
	}
}

.address {
	font-size: 26rpx;
}

.recommend {
	display: flex;
	padding-left: 30rpx;

	.item {
		position: relative;
		box-shadow: 0 0 10rpx rgb(230, 230, 230);

		.right {
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			width: 420rpx;
		}

		.num {
			color: #3c8af8;
			font-size: 35rpx;
		}

		.btn {
			border: 1px solid #999999;
			color: #999999;
			border-radius: 5px;
			padding: 3px 5px;
			position: absolute;
			right: 30rpx;
			top: 50%;
			transform: translateY(-50%);
			font-size: 26rpx;

			&.follow {
				border: 1px solid #3c8af8;
				color: #3c8af8;
			}
		}
	}
}
</style>
