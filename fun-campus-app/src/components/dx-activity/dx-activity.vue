<template>
  <view class="bg-white">
    <view class="activity-top">
      <view class="px-30 pt-30 pb-10">
        <tm-input prefix="tmicon-search" v-model="param.keyword"></tm-input>
      </view>
      <view>
        <tm-tabs @change="tabsChange" showTabsLineAni :item-width="80" :width="750" :height="90" default-name="1">
          <tm-tabs-pane v-for="(item, index) in props.categoryList" :key="index" :name="item._id" :title="item.name"></tm-tabs-pane>
        </tm-tabs>
      </view>
    </view>
    <view class="list px-30 flex flex-between flex-wrap round-3" :class="{'pb-30':list.length}">
      <view class="item pb-20 mt-30 round-3" v-for="(item, index) in list" :key="index" @click="openLink('others/activity/detail?id=' + item._id)">
        <image :src="item.cover" style="width: 100%; height: 250rpx"></image>
        <view class="px-20">
          <view class="title text-overflow-2"> {{ item.title }} </view>
          <view class="flex align-center flex-between mt-10">
            <text class="tips">{{ timeText(item.start_date) }}</text>
            <!-- <text class="price">免费</text> -->
          </view>
          <view class="flex-col-top-center mt-10">
            <tm-icon name="tmicon-position" color="#6d6868" :font-size="24"></tm-icon>
            <text class="tips ml-10 text-overflow-1">{{ item.address }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
  <!-- 数据空 -->
  <view class="load-more px-50 pb-30" v-if="list.length">
    <tm-divider align="center" :label="hasMore && loading?'加载中...':hasMore?'上拉加载更多～':'没有更多啦～'"></tm-divider>
  </view>
  <view class="mt-50 py-50" v-else>
    <tm-result :showBtn="false" title="暂无数据～" subTitle=" " v-if="!list.length && !loading"></tm-result>
  </view>
</template>
<script lang="ts" setup>
import { ref, reactive,watch } from 'vue';
import { activityList } from '@/common/index'
import { openLink,timeText } from '@/common/tools';
import { debounce } from '@/tmui/tool/function/util';
const props = defineProps<{
  categoryList: any[];
}>();
const hasMore = ref(true);
const loading = ref(false);
const list = ref<any[]>([]);
const param = reactive<any>({
  parent_id: '',
  nextDate: '',
  keyword: '',
  limit:20
})
watch(()=>param.keyword, () => {
  debounce(()=>{
    getActivityList();
  }, 500)
})
function tabsChange(e: string) {
  param.parent_id = e;
  getActivityList();
}
function getActivityList(is_more = false) {
  uni.showLoading({
    title: '加载中...'
  })
  activityList(param).then((res) => {
    console.log('res', res);
    if (res.code === 1000) {
      if (res.data.length < param.limit || res.data.length === 0) {
        hasMore.value = false
      }
      if (is_more) {
        list.value = list.value.concat(res.data)
      } else {
        list.value = res.data
      }
    }
  }).finally(() => {
    uni.hideLoading()
  });
}
getActivityList();
//
</script>
<style lang="scss" scoped>
.activity-top {
  position: fixed;
  // #ifndef H5
  top: 0;
  // #endif
  // #ifdef H5
  top: 44px;
  // #endif
  left: 0;
  z-index: 999;
  background: #ffffff;
}

.list {
  // #ifdef MP-WEIXIN || APP-PLUS
  padding-top: 190rpx;
  // #endif
  // #ifdef H5
  padding-top: calc(130rpx + 35px);
  // #endif
  .item {
    width: calc((100vw - 90rpx) / 2);
    box-shadow: 0 0 10rpx rgb(230, 230, 230);
  }
}
</style>
