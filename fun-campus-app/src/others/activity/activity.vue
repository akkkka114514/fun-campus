<template>
  <tm-app color="white">
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]" :round="3" :shadow="0">
      <tm-tabs @change="tabsChange" showTabsLineAni :item-width="150" :width="750" :height="90" default-name="2">
        <tm-tabs-pane name="0" title="我参与的"> </tm-tabs-pane>
        <tm-tabs-pane name="1" title="我管理的"> </tm-tabs-pane>
      </tm-tabs>
      <!--  -->
      <view class="activity-list">
        <view class="item flex-row-center-between pa-30" :class="{ 'border-b-2': list.length - 1 !== index || !index }"
          v-for="(item, index) in list" :key="index" @click="openLink('others/activity/detail?id=' + (tabIndex==1?item._id:item.activity_id))">
          <view>
            <image class="round-3" :src="item.cover" style="width: 220rpx; height: 200rpx"></image>
          </view>
          <view class="pl-20 flex flex-1 flex-col flex-between" style="height: 200rpx">
            <view>
              <view class="title text-overflow-2">{{ item.title }}</view>
              <view class="address mt-10 flex">
                <tm-icon name="tmicon-position" color="#999999" :font-size="24"></tm-icon>
                <text class="ml-8 text-overflow-1">{{ item.address }}</text>
              </view>
            </view>
            <view class="flex flex-between align-center pb-10 mt-15">
              <view class="flex-col-top-center">
                <tm-icon name="tmicon-clock" color="#999999" :fontSize="24"></tm-icon>
                <text class="ml-8 tips">{{ timeText(item.start_date) }}</text>
              </view>
              <view v-if="tabIndex == 1">
                <view class="btn" @click.stop="openLink('others/activity/manage?id=' + item._id)">管理</view>
              </view>
            </view>
          </view>
        </view>
      </view>
      <view class="load-more" v-if="list.length">
        <tm-divider align="center" :label="hasMore && loading ? '加载中...' : hasMore ? '上拉加载更多～' : '没有更多啦～'"></tm-divider>
      </view>
      <view class="mt-50 pt-50" v-else>
        <tm-result :showBtn="false" title="暂无数据～" subTitle=" " v-if="!list.length && !loading"></tm-result>
      </view>
    </tm-sheet>
  </tm-app>
</template>
<script lang="ts" setup>
import { openLink,timeText } from '@/common/tools';
import { myActivityList } from '@/common/index'
import { ref } from 'vue';
import { onReachBottom } from '@dcloudio/uni-app';
const tabIndex = ref(0);
const list = ref<any>([]);
const hasMore = ref(true);
const loading = ref(false);
const param = ref({
  nextDate: '',
  limit: 10,
  type: 0,
})
function getList(is_more = false) {
  loading.value = true;
  param.value.type = tabIndex.value;
  myActivityList(param.value).then(res => {
    console.log(res);
    if (res.code === 1000) {
      if (res.data.length < param.value.limit || res.data.length === 0) {
        hasMore.value = false
      }
      if (is_more) {
        list.value = list.value.concat(res.data)
      } else {
        list.value = res.data
      }
    }
  }).finally(() => {
    loading.value = false;
  })
}
getList();
onReachBottom(() => {
  if (hasMore.value) {
    param.value.nextDate = list.value[list.value.length - 1].create_date;
    getList();
  }
})
function tabsChange(e: number) {
  tabIndex.value = e;
  param.value.nextDate = '';
  list.value = [];
  getList();
}
</script>
<style lang="scss" scoped>
.activity-list {
  .item {
    &:not(:last-child) {
      border-bottom: 2rpx solid rgb(230, 230, 230);
    }

    .title {
      font-size: 30rpx;
    }

    .address {
      font-size: 26rpx;
    }

    .btn {
      border: 1px solid #ffa09b;
      border-radius: 5px;
      padding: 3px 8px;
      font-size: 26rpx;
      color: #ff716a;
    }
  }
}
</style>
