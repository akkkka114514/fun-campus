<template>
  <tm-app color="white">
    <tm-tabs @change="tabsChange" showTabsLineAni :item-width="80" :width="750" :height="90" default-name="0">
      <tm-tabs-pane name="1" title="活动"> </tm-tabs-pane>
      <tm-tabs-pane name="2" title="组织"> </tm-tabs-pane>
    </tm-tabs>
    <!--  -->
    <view class="activity-list bg-white" v-if="tabsIndex === '1'">
      <view class="item flex-row-center-between pa-30" v-for="(item,index) in list" :key="index" @click="openLink('others/activity/detail?id=' + item._id)">
        <view class="cover">
          <image class="round-3" :src="item.cover" style="width: 250rpx; height: 220rpx"></image>
        </view>
        <view class="pl-20 flex flex-col flex-between" style="height: 210rpx">
          <view class="title text-overflow-2"> {{ item.title }} </view>
          <view class="address flex-col-top-center">
            <tm-icon name="tmicon-position" color="#999999" :font-size="24"></tm-icon>
            <text class="ml-8 text-overflow-1">{{ item.address }}</text>
          </view>
          <view class="time flex-col-top-center">
            <tm-icon name="tmicon-time-fill" color="#999999"></tm-icon>
            <text class="tips">{{ timeText(item.start_date) }}</text>
          </view>
        </view>
      </view>
    </view>
    <!--  -->
    <view class="team-list bg-white" v-else>
      <view class="item flex flex-between pa-30" v-for="(item, index) in list" :key="index" @click="openLink('teams/detail/detail?id='+item._id)">
        <view>
          <tm-avatar :size="180" :img="item.logo"></tm-avatar>
        </view>
        <view class="flex-1 ml-30 right">
          <view class="top flex">
            <view class="name">{{item.title}}</view>
            <view class="ml-15 flex align-center" v-if="!item.is_identification">
              <tm-icon name="tmicon-minus-circle" color="#999999" :font-size="24"></tm-icon>
              <text class="ml-5 approve">未认证</text>
            </view>
            <view class="ml-15 flex align-center" v-else>
              <tm-icon name="tmicon-check-circle-fill" color="#0bbb08" :font-size="24"></tm-icon>
              <text class="ml-5 success">已认证</text>
            </view>
          </view>
          <view>
            <text>活动数量:</text>
            <text class="mx-10 num">{{item.activity_count}}</text>
            <text>场</text>
          </view>
          <view>
            <!-- <view class="btn">已关注</view> -->
          </view>
        </view>
      </view>
    </view>

    <view class="mt-50 pt-50" v-if="!list.length && !loading">
      <tm-result :showBtn="false" title="暂无数据～" subTitle=" "></tm-result>
    </view>
  </tm-app>
</template>
<script lang="ts" setup>
import { openLink,timeText } from '@/common/tools';
import { ref } from 'vue';
import {followList} from '@/common/index'
const loading = ref(false);
const tabsIndex = ref('1');
const list = ref<any>([]);
function getFollowList(){
  loading.value = true;
  followList({type:tabsIndex.value}).then(res=>{
    console.log(res)
    if(res.code === 1000){
      list.value = res.data
    }
  }).finally(()=>{
    loading.value = false;
  })
}
getFollowList()
function tabsChange(e: string) {
  tabsIndex.value = e;
  list.value = [];
  getFollowList()
}
</script>
<style lang="scss" scoped>
.activity-list {
  .item {
    &:not(:last-child) {
      border-bottom: 2rpx solid rgb(230, 230, 230);
    }
  }
}
.team-list{
  .item {
    position: relative;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    .cover{
      position: relative;
    }
    .right {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }
    .num {
      color: #3c8af8;
      font-size: 35rpx;
    }
    .btn {
      border: 1px solid #e5e5e5;
      border-radius: 5px;
      padding: 5px 10px;
      position: absolute;
      right: 30rpx;
      top: 50%;
      transform: translateY(-50%);
    }
  }
}
</style>
