<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <view class="manage px-50 pt-30">
        <view class="flex flex-col-top-center">
          <tm-avatar :size="150" :img="info.cover"></tm-avatar>
          <view class="ml-30">{{info.title || ''}}</view>
        </view>
        <view class="flex flex-between text-align-center py-50">
          <view>
            <view class="number">{{ info.applyCount || 0 }}</view>
            <view class="mt-10">报名人数</view>
          </view>
          <view>
            <view class="number">{{ info.todayApplyCount || 0 }}</view>
            <view class="mt-10">今日新增</view>
          </view>
          <view>
            <view class="number">{{ info.followCount || 0 }}</view>
            <view class="mt-10">关注数</view>
          </view>
        </view>
      </view>
    </tm-sheet>
    <tm-sheet :margin="[0, 30]" :padding="[0, 0]">
      <!-- 组织管理 -->
      <tm-cell :margin="[0, 0]" title="活动设置" :bottomBorder="true" rightIcon=""> </tm-cell>
      <tm-grid :width="750" :col="4">
        <tm-grid-item :height="150" @click="openLink('others/publish/publish?id='+info._id)">
          <tm-icon name="tmicon-user-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="修改活动"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="orange" :height="150" @click="openLink('others/activity/setting?id='+info._id)">
          <tm-icon name="tmicon-cog-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="快速设置"></tm-text>
        </tm-grid-item>
        <!-- <tm-grid-item color="green" :height="150" @click="openLink('others/activity/high?id=1')">
          <tm-icon name="tmicon-heart-fill" :font-size="42"></tm-icon>
          <tm-text :font-size="22" label="高级设置"></tm-text>
        </tm-grid-item> -->
        <tm-grid-item color="pink" :height="150" @click="showWin=true">
          <tm-icon name="tmicon-layergroup-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="删除活动"></tm-text>
        </tm-grid-item>
      </tm-grid>
    </tm-sheet>
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <!-- 报名管理 -->
      <tm-cell :margin="[0, 0]" title="报名管理" :bottomBorder="true" rightIcon=""> </tm-cell>
      <tm-grid :width="750" :col="4">
        <tm-grid-item :height="150" @click="openLink('/others/activity/user?id='+info._id)">
          <tm-icon name="tmicon-user-group-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="报名用户"></tm-text>
        </tm-grid-item>
        <!-- <tm-grid-item color="orange" :height="150" @click="openLink('/others/activity/list')">
          <tm-icon name="tmicon-menu" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="报名明细"></tm-text>
        </tm-grid-item> -->
        <!-- <tm-grid-item color="green" :height="150" @click="toPage('/others/team/team')">
            <tm-icon name="tmicon-heart-fill" :font-size="42"></tm-icon>
            <tm-text :font-size="22" label="报名费"></tm-text>
          </tm-grid-item> -->
        <!-- <tm-grid-item color="pink" :height="150">
          <tm-icon name="tmicon-file-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="导出名单"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="pink" :height="150" @click="openLink('/others/activity/stats')">
          <tm-icon name="tmicon-chart-bar" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="数据统计"></tm-text>
        </tm-grid-item> -->
      </tm-grid>
    </tm-sheet>
    <tm-modal title="温馨提示" content="您确认删除当前活动嘛？" :height="300" okText="确定" v-model:show="showWin" @ok="deleteConfirm"></tm-modal>
  </tm-app>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { openLink } from '@/common/tools';
import { onLoad } from '@dcloudio/uni-app';
import {homeActivity,delActivity} from '@/common/index'
const showWin = ref(false);
const info = ref({
  _id:'',
  title: '',
  cover: '',
  todayApplyCount: 0,
  followCount: 0,
  applyCount: 0,
});
onLoad((e:any) => {
  if(e.id){
    homeActivity({id:e.id}).then(res=>{
      if(res.code === 1000){
        info.value = res.data
      }
    });
  }
});
const deleteConfirm = ()=>{
  delActivity({id:info.value._id}).then(res=>{
    if(res.code === 1000){
      uni.$tm.u.toast('删除成功');
      setTimeout(()=>{
        uni.navigateBack({
          delta: 1
        });
      },2000)
    }else{
      uni.$tm.u.toast(res.message);
    }
  })
}
</script>
<style lang="scss" scoped>
.manage {
  background: linear-gradient(180deg, #3c8af8, #38cddd);
  color: #ffffff;
  .number {
    font-size: 38rpx;
  }
}
</style>
