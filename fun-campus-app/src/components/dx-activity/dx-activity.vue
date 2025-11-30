<template>
  <view class="bg-white">
    <view class="list px-30 flex flex-between flex-wrap round-3" :class="{'pb-30':list.length}">
      <view class="item pb-20 mt-30 round-3" v-for="(item, index) in list" :key="index" @click="openLink('others/activity/detail?id=' + item._id)">
        <image :src="item.cover" style="width: 100%; height: 250rpx"></image>
        <view class="px-20">
          <view class="title text-overflow-2"> {{ item.title }} </view>
          <view class="flex align-center flex-between mt-10">
            <text class="tips">{{ timeText(item.start_date) }}</text>
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
import { ref, reactive,watch, computed } from 'vue';
import { activityList } from '@/common/index'
import { openLink,timeText } from '@/common/tools';
import { debounce } from '@/tmui/tool/function/util';

const props = defineProps({
  activeTab: {
    type: Number,
    default: 0
  }
});

const hasMore = ref(true);
const loading = ref(false);
const list = ref<any[]>([]);

// 使用计算属性来响应父组件传递的 activeTab 值变化
const currentTab = computed(() => props.activeTab);

// 监听 tab 变化，重新加载数据
watch(currentTab, (newVal) => {
  loadData(newVal);
}, { immediate: true });

function loadData(tabIndex: number) {
  loading.value = true;
  // 模拟 API 调用，实际应该根据 tabIndex 请求不同的数据
  activityList({ page: 1, type: tabIndex }).then((res: any) => {
    if (res.code == 1000) {
      list.value = res.data.list;
      hasMore.value = res.data.hasMore;
    }
    loading.value = false;
  });
}

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
  padding-top: 110rpx;
  // #endif
  // #ifdef H5
  padding-top: calc(50rpx + 35px);
  // #endif
  .item {
    width: calc((100vw - 90rpx) / 2);
    box-shadow: 0 0 10rpx rgb(230, 230, 230);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    
    &:hover {
      transform: translateY(-10rpx);
      box-shadow: 0 20rpx 30rpx rgba(255, 140, 66, 0.2);
    }
  }
}
</style>