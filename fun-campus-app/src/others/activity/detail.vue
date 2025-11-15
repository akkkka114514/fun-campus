<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <view class="cover">
        <image :src="info.cover"></image>
        <view class="follow" :class="{ active: info.is_follow }" @click="toFollow(1)">{{ info.is_follow ? '已关注' : '关注' }}
        </view>
      </view>
      <view class="title pa-30"> {{ info.title || '' }} </view>
    </tm-sheet>

    <tm-sheet :margin="[0, 30]" :padding="[0, 0]">
      <tm-cell :margin="[0, 0]" :bottomBorder="true" rightIcon="">
        <template v-slot:title>
          <view class="flex-col-top-center">
            <tm-icon name="tmicon-clock" color="#777777" :font-size="28"></tm-icon>
            <text class="ml-15">开始时间</text>
          </view>
        </template>
        <template v-slot:right>
          <view> {{ timeText('start_date') }} </view>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true" rightIcon="">
        <template v-slot:title>
          <view class="flex-col-top-center">
            <tm-icon name="tmicon-clock" color="#777777" :font-size="28"></tm-icon>
            <text class="ml-15">报名截止时间</text>
          </view>
        </template>
        <template v-slot:right>
          <view> {{ timeText('closing_date') }} </view>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true" rightIcon="">
        <template v-slot:title>
          <view class="flex-col-top-center">
            <tm-icon name="tmicon-clock" color="#777777" :font-size="28"></tm-icon>
            <text class="ml-15">结束时间</text>
          </view>
        </template>
        <template v-slot:right>
          <view> {{ timeText('end_date') }} </view>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true" rightIcon="">
        <template v-slot:title>
          <view class="flex-col-top-center">
            <tm-icon name="tmicon-user-group" color="#777777" :font-size="28"></tm-icon>
            <text class="ml-15">{{ info.applyCount || 0 }}人已报名</text>
          </view>
        </template>
        <template v-slot:right>
          <view class="flex join-list">
            <image class="avatar" v-for="(item, index) in info.applyList" :key="index" :src="item.avatar"></image>
          </view>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true" rightIcon="" @click="callPhone(info.mobile)">
        <template v-slot:title>
          <view class="flex-col-top-center">
            <tm-icon name="tmicon-phone-fill" color="#777777" :font-size="28"></tm-icon>
            <text class="ml-15">联系电话</text>
          </view>
        </template>
        <template v-slot:right>
          <view> {{ info.mobile }} </view>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true" @click="openMap">
        <template v-slot:title>
          <view class="flex-col-top-center pr-30">
            <tm-icon name="tmicon-position-fill"></tm-icon>
            <text class="ml-15">{{ info.address || '' }}</text>
          </view>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true" rightIcon="">
        <template v-slot:title>
          <view class="flex-col-top-center">
            <text class="mr-15">浏览量：{{ info.views || 0 }}</text>
            <text class="ml-50">关注数：{{ info.followCount || 0 }}</text>
          </view>
        </template>
      </tm-cell>
    </tm-sheet>

    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <!-- 组织 -->
      <view class="item my-30 flex flex-between pa-30" @click="openLink('teams/detail/detail?id=' + info.team?._id)">
        <view>
          <tm-avatar :size="180" :img="info.team?.logo"></tm-avatar>
        </view>
        <view class="flex-1 ml-30 right">
          <view class="top flex">
            <view class="name">{{ info.team?.title }}</view>
            <!-- <view class="approve ml-15">{{info.team?.is_identification?'已认证':'未认证'}}</view> -->
            <view class="ml-15 flex align-center" v-if="!info.team?.is_identification">
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
            <text class="mx-10 num">{{ info.team?.activityCount || 0 }}</text>
            <text>场</text>
          </view>
          <view>
            <view class="btn" :class="{ 'follow': info.team?.is_follow }" @click.stop="toFollow(2)">
              {{
                info.team?.is_follow ? '已关注' : '关注' }}</view>
          </view>
        </view>
      </view>
    </tm-sheet>

    <tm-sheet :margin="[0, 30]" :padding="[0, 0]">
      <!-- 介绍 -->
      <view class="pa-30">
        <tm-html :content="info.content"></tm-html>
      </view>
    </tm-sheet>
    <view class="gap"></view>
    <view class="footer flex-col-center-center py-10 px-50">
      <view class="left">
        <view class="flex flex-between" :class="!info.is_self ? 'operate' : 'pr-50'">
          <view @click="openLink('/pages/index/index')">
            <tm-icon name="tmicon-home" :font-size="32"></tm-icon>
            <view class="mt-5 text-size-s">首页</view>
          </view>
          <!-- #ifdef MP_WEIXIN -->
          <button class="footer-btn" open-type="share">
            <tm-icon name="tmicon-share" :font-size="32"></tm-icon>
            <view class="mt-5 text-size-s">分享</view>
          </button>
          <!-- #endif -->
          <!-- #ifdef APP-PLUS -->
          <button class="footer-btn" open-type="share">
            <tm-icon name="tmicon-share" :font-size="32"></tm-icon>
            <view class="mt-5 text-size-s">分享</view>
          </button>
          <!-- #endif -->
          <!-- #ifdef H5 -->
          <button class="footer-btn" @click="share">
            <tm-icon name="tmicon-share" :font-size="32"></tm-icon>
            <view class="mt-5 text-size-s">分享</view>
          </button>
          <!-- #endif -->
          <view class="pt-5" v-if="info.is_self" @click="openLink('others/activity/manage?id=' + info._id)">
            <tm-icon name="tmicon-all" :font-size="28"></tm-icon>
            <view class="mt-5 text-size-s">管理</view>
          </view>
        </view>
      </view>
      <view class="right">
        <tm-button :round="20" v-if="info.is_apply" type="primary" block label="我的报名"
          @click="openLink('others/activity/info?id=' + info._id)"></tm-button>
        <tm-button :round="20" v-else type="primary" :disabled="isEnd" block :label="isEnd ? '已截止' : '立即报名'"
          @click="toApply"></tm-button>
      </view>
    </view>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref, computed } from 'vue';
import { openLink } from '@/common/tools';
import { onLoad, onShow,onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app';
import { activityDetail, followActivity, followTeam, applySave } from '@/common/index'
import * as dayjs from "@/tmui/tool/dayjs/esm/index"
import { callPhone } from '@/tmui/tool/function/util';
// #ifdef H5
import { setClipboardData } from '@/tmui/tool/function/util';
// #endif
const info = ref<any>({});
const id = ref('');
const isFirst = ref(false);
const timeText = computed(() => (field: string) => {
  return dayjs.default(info.value[field]).format('YYYY-MM-DD HH:mm')
})
const isEnd = computed(() => {
  return dayjs.default().isAfter(dayjs.default(info.value.closing_date))
})
function getActivityDetail() {
  activityDetail({ id: id.value }).then(res => {
    if (res.code === 1000) {
      info.value = res.data;
    }
  })
}
onLoad((e: any) => {
  if (e.id) {
    id.value = e.id;
    getActivityDetail();
  }
});
// #ifdef MP-WEIXIN
onShareAppMessage(() => {
  return {
    title: info.value.title,
    path: '/others/activity/detail?id=' + info.value._id,
    imageUrl: info.value.cover
  }
})
onShareTimeline(() => {
  return {
    title: info.value.title,
    imageUrl: info.value.cover
  }
})
// #endif
onShow(() => {
  if (isFirst.value) {
    getActivityDetail();
  }
  isFirst.value = true;
})
// #ifdef H5
function share(){
  setClipboardData(window.location.href);
  uni.$tm.u.toast('链接已复制');
}
// #endif
function toFollow(type: number) {
  console.log('toFollow');
  if (type === 1) {
    followActivity({ id: info.value._id }).then(res => {
      uni.$tm.u.toast(res.message);
      if (res.code === 1000) {
        info.value.is_follow = !info.value.is_follow;
      }
    })
  } else {
    followTeam({ id: info.value.team._id }).then(res => {
      uni.$tm.u.toast(res.message);
      if (res.code === 1000) {
        info.value.team.is_follow = !info.value.team.is_follow;
      }
    })
  }
}
function openMap() {
  uni.openLocation({
    latitude: info.value.lat,
    longitude: info.value.lon,
    name: info.value.address,
    address: info.value.address,
    scale: 18
  });
}
function toApply() {
  if (isEnd.value) {
    uni.$tm.u.toast('活动已结束')
    return
  }
  // 是否有表单 or 报名费用（二期加）
  if (info.value.is_form && !info.value.has_field) {
    return uni.$tm.u.toast('联系管理员填写表单')
  }
  if (info.value.is_form || info.value.is_cost) {
    return openLink('others/activity/apply?id=' + info.value._id)
  }
  // 直接报名
  applySave({ id: info.value._id }).then(res => {
    uni.$tm.u.toast(res.message)
    if (res.code === 1000) {
      openLink('others/activity/info?id=' + info.value._id)
    }
  })
}
</script>
<style lang="scss" scoped>
.cover {
  width: 100%;
  height: 450rpx;
  position: relative;

  image {
    width: 100%;
    height: 100%;
  }

  .follow {
    background: #0163ff;
    color: #ffffff;
    border: 1px solid #3c8af8;
    position: absolute;
    right: 30rpx;
    bottom: 30rpx;
    z-index: 99;
    padding: 8rpx 20rpx;
    border-radius: 10rpx;

    &.active {
      background: rgba(0, 0, 0, 0.3);
      border: 1px solid rgba(0, 0, 0, 0.5);
    }
  }
}

.item {
  position: relative;

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
    padding: 8rpx 25rpx;
    position: absolute;
    right: 30rpx;
    top: 50%;
    transform: translateY(-50%);
    border-radius: 10rpx;
    border: 1px solid #999999;
    color: #999999;

    &.follow {
      border: 1px solid #3c8af8;
      color: #3c8af8;
    }
  }
}

.join-list {
  padding-right: 20rpx;

  .avatar {
    width: 45rpx;
    height: 45rpx;
    border-radius: 50%;
    margin-right: -20rpx;
  }
}

.gap {
  height: 150rpx;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 0 10rpx rgba(0, 0, 0, 0.1);

  .operate {
    padding-left: 20rpx;
    padding-right: 100rpx;
  }

  .left {
    width: 50%;
  }

  .right {
    width: 50%;
  }

  .footer-btn {
    background: transparent;
    box-sizing: border-box;
    padding: 0;
    margin: 0;
    font-size: 30rpx;
    line-height: inherit;

    &::after {
      border: none;
    }
  }
}
</style>
