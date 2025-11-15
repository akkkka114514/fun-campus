<template>
  <view class="list bg-white">
    <view class="item flex-row-center-between pa-30" v-for="(item, index) in activity" :key="index" @click="openLink('others/activity/detail?id=' + item._id)">
      <view>
        <image class="round-3" :src="item.cover" style="width: 220rpx; height: 200rpx"></image>
      </view>
      <view class="pl-20 flex flex-1 flex-col flex-between" style="height: 200rpx">
        <view>
          <view class="title text-overflow-2">{{ item.title }}</view>
          <view class="address mt-20 flex">
            <tm-icon name="tmicon-position" color="#999999" :font-size="24"></tm-icon>
            <text class="ml-8 text-overflow-1">{{ item.address }}</text>
          </view>
        </view>
        <view class="time flex flex-between">
          <view>
            <tm-icon name="tmicon-time-fill" color="#999999"></tm-icon>
            <text class="tips">{{ timeText(item.start_date) }}</text>
          </view>
          <text class="tips" v-if="!item.is_hidden_views">浏览数：{{ item.views || 0 }}</text>
        </view>
      </view>
    </view>
  </view>
  <view class="pt-50" v-if="!activity.length">
    <tm-result :showBtn="false" title="暂无数据～" subTitle=" " v-if="!activity.length && !loading"></tm-result>
  </view>
</template>
<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app';
import { ref } from 'vue';
import { openLink, timeText } from '@/common/tools';
import { activityList } from '@/common/index'
const activity = ref<any>([]);
const loading = ref(false);
onLoad((e:any)=>{
  if(e.team_id){
    loading.value = true
    activityList({team_id:e.team_id,limit:1000}).then(res=>{
      if(res.code === 1000){
        activity.value = res.data
      }
    }).finally(()=>{
      loading.value = false
    })
  }
})
</script>
<style lang="scss" scoped>
.list {
  .item {
    &:not(:last-child) {
      border-bottom: 2rpx solid rgb(230, 230, 230);
    }
  }
}
</style>
