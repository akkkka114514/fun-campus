<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <image class="cover" :src="info.cover"></image>
      <view class="detail">
        <view class="box">
          <view class="inner round-3">
            <view class="avatar">
              <tm-avatar :size="160" :round="25" :img="info.logo"></tm-avatar>
            </view>
            <view class="text-align-center title">{{ info.title }}</view>
            <view class="text-overflow-2 px-30 my-30 tips">
              {{ info.content }}
            </view>
            <view class="flex flex-row-center-center pb-50">
              <view class="btn" :class="{activate:info.is_join}" v-if="info.is_open_join" @click="showJoin">{{info.is_join?'已加入':'加入组织'}}</view>
              <view class="btn mx-20" :class="{activate:info.is_identification}" @click="toIdentification">{{ info.is_identification?'已认证':'未认证' }}</view>
              <view class="btn" :class="{activate:info.is_follow}" @click="toFollow">{{ info.is_follow?'已关注':'关注' }}</view>
            </view>
          </view>
        </view>
        <!--  -->
        <tm-tabs @change="tabsChange" showTabsLineAni :item-width="150" :width="750" :height="90" default-name="0">
          <tm-tabs-pane name="0" title="最新活动"> </tm-tabs-pane>
          <tm-tabs-pane name="1" title="组织相册"> </tm-tabs-pane>
        </tm-tabs>
        <!--  -->
        <view class="list px-30 flex flex-between flex-wrap round-3" v-if="tabsIndex != 1">
          <view class="item pb-20 mb-30 round-3" v-for="(item,index) in list" :key="index" @click="openLink('others/activity/detail?id='+item._id)">
            <image :src="item.cover" style="width: 100%; height: 250rpx"></image>
            <view class="px-20 pt-8">
              <view class="title text-overflow-2"> {{ item.title }} </view>
              <view class="flex align-center mt-8">
                <tm-icon name="tmicon-clock" color="#6d6868" :font-size="24"></tm-icon>
                <text class="tips ml-10">{{ timeText(item.start_date) }}</text>
              </view>
              <view class="flex align-center mt-8">
                <tm-icon name="tmicon-position" color="#6d6868" :font-size="26"></tm-icon>
                <text class="tips ml-10 text-overflow-1">{{item.address}}</text>
              </view>
            </view>
          </view>
        </view>
        <!--  -->
        <view class="pt-30" v-else>
          <tm-timeline>
            <tm-timeline-item v-for="(item,index) in list" :key="index" color="blue" :time="item.name">
              <tm-image-group>
                <tm-image preview :margin="[10,10]" :round="5" :width="170" :height="170" :src="url" v-for="url in item.list" :key="url"></tm-image>
              </tm-image-group>
            </tm-timeline-item>
          </tm-timeline>
          <!--  -->
        </view>
      </view>
    </tm-sheet>
    <tm-modal title="温馨提示" content="您确认加入当前组织吗？" :height="300" okText="确定" v-model:show="showWin" @ok="joinConfirm"></tm-modal>
  </tm-app>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import {teamDetail,followTeam,activityList,photoIndex,teamApply} from '@/common/index'
import { openLink,timeText } from '@/common/tools';
import { useUserStore } from '@/stores/user';
const id = ref('');
const info = ref<any>({});
const userStore = useUserStore();
const list = ref<any[]>([]);
const tabsIndex = ref(0);
const showWin = ref(false);
const isFirst = ref(false);
function getTeamDetail(){
  teamDetail({
    id:id.value
  }).then(res=>{
    if(res.code === 1000){
      info.value = res.data;
    }
  })
}
onLoad((e:any) => {
  if(e.id){
    id.value = e.id;
    getTeamDetail();
    getActivityList();
  }
});
onShow(()=>{
  if(isFirst.value){
    getTeamDetail();
  }
  isFirst.value = true;
})
function toIdentification(){
  if(!info.value.is_identification && userStore.userInfo.user_id === info.value.user_id){
    openLink('/teams/approve/approve?team_id='+id.value)
  }
}
function getActivityList(){
  activityList({
    team_id:id.value,
    limit:1000
  }).then(res=>{
    if(res.code === 1000){
      list.value = res.data;
    }
  })
}
function getPhotoList(){
  photoIndex({
    team_id:id.value,
  }).then(res=>{
    if(res.code === 1000){
      list.value = res.data;
    }
  })
}
function tabsChange(e: number) {
  tabsIndex.value = e;
  if(e == 1){
    getPhotoList();
  }else{
    getActivityList();
  }
}
function toFollow(){
  followTeam({
    id:id.value
  }).then(res=>{
    uni.$tm.u.toast(res.message);
    if(res.code === 1000){
      info.value.is_follow = !info.value.is_follow;
    }
  })
}
function showJoin(){
  if(info.value.is_join){
    openLink('/teams/apply/info?id='+id.value)
  }else{
    showWin.value = true;
  }
}
function joinConfirm(){
  teamApply({
    id:id.value
  }).then(res=>{
    uni.$tm.u.toast(res.message);
    if(res.code === 1000){
      setTimeout(()=>{
        openLink('/teams/apply/info?id='+id.value)
      },1500)
    }
  }).finally(()=>{
    showWin.value = false;
  })
}
</script>
<style lang="scss" scoped>
.cover {
  width: 100%;
  height: 420rpx;
}
.detail {
  position: relative;
  margin-top: -210rpx;
  .box {
    padding: 30rpx;
    .inner {
      background-color: #ffffff;
      position: relative;
      box-shadow: 0 0 20rpx 5rpx rgba(0, 0, 0, 0.1);
      .avatar {
        position: absolute;
        top: -80rpx;
        left: 50%;
        margin-left: -80rpx;
        box-shadow: 0 0 10rpx 0 rgba(0, 0, 0, 0.1);
        border-radius: 50%;
      }
      .title {
        padding-top: 100rpx;
        font-size: 36rpx;
      }
      .tips {
        color: #999999;
        font-size: 26rpx;
      }
      .btn {
        width: 150rpx;
        text-align: center;
        border: 1px solid #e5e5e5;
        border-radius: 10rpx;
        padding: 5rpx 15rpx;
        font-size: 28rpx;
        color: #888888;
        &.activate{
          border-color: #0bbb08;
          color: #0bbb08;
        }
      }
    }
  }
}
.list {
  padding-top: 30rpx;
  .item {
    width: calc((100vw - 90rpx) / 2);
    box-shadow: 0 0 10rpx rgb(230, 230, 230);
    .title{
      min-height: 80rpx;
    }
  }
}
</style>
