<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <view class="manage px-50 pt-30">
        <view class="flex flex-col-top-center">
          <tm-avatar :size="150" :img="info.logo"></tm-avatar>
          <view class="ml-30">{{ info.title }}</view>
        </view>
        <view class="flex flex-between text-align-center py-50">
          <view>
            <view class="number">{{ info.activityCount }}</view>
            <view class="mt-10">活动总数</view>
          </view>
          <view>
            <view class="number">{{ info.joinCount }}</view>
            <view class="mt-10">累计报名人数</view>
          </view>
          <view>
            <view class="number">{{ info.followCount }}</view>
            <view class="mt-10">关注</view>
          </view>
        </view>
      </view>
    </tm-sheet>
    <tm-sheet :margin="[0, 30]" :padding="[0, 0]">
      <!-- 组织管理 -->
      <tm-cell :margin="[0, 0]" title="组织管理" :bottomBorder="true" rightIcon=""> </tm-cell>
      <tm-grid :width="750" :col="4">
        <tm-grid-item :height="150" @click="openLink('teams/activity/activity?team_id=' + (info._id || ''))">
          <tm-icon name="tmicon-flag-fill" :font-size="40"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="全部活动"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="orange" :height="150" @click="openLink('teams/create/create?id=' + (info._id || ''))">
          <tm-icon name="tmicon-edit" :font-size="44"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="修改组织"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="green" :height="150" @click="openLink('teams/approve/approve?team_id=' + (info._id || ''))">
          <tm-icon name="tmicon-md-ribbon" :font-size="44"></tm-icon>
          <tm-text :font-size="22" label="组织认证"></tm-text>
        </tm-grid-item>
        <!-- <tm-grid-item color="pink" :height="150" @click="openLink('teams/manager/manager')">
          <tm-icon name="tmicon-layergroup-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="管理员"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="pink" :height="150" @click="openLink('teams/blacklist/blacklist')">
          <tm-icon name="tmicon-layergroup-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="黑名单"></tm-text>
        </tm-grid-item> -->
        <tm-grid-item color="pink" :height="150" @click="showWin = true">
          <tm-icon name="tmicon-delete" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="删除组织"></tm-text>
        </tm-grid-item>
      </tm-grid>
    </tm-sheet>
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <!-- 会员管理 -->
      <!-- <tm-cell :margin="[0, 0]" title="会员管理" :bottomBorder="true" rightIcon=""> </tm-cell>
      <tm-grid :width="750" :col="4">
        <tm-grid-item :height="150" @click="toPage('/others/follow/follow')">
          <tm-icon name="tmicon-user-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="设置"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="orange" :height="150" @click="toPage('/others/activity/activity')">
          <tm-icon name="tmicon-cog-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="会员列表"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="green" :height="150" @click="toPage('/others/team/team')">
          <tm-icon name="tmicon-heart-fill" :font-size="42"></tm-icon>
          <tm-text :font-size="22" label="会员审核"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="pink" :height="150" @click="toPage('/others/publish/publish')">
          <tm-icon name="tmicon-layergroup-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="会员费"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="pink" :height="150" @click="toPage('/others/publish/publish')">
          <tm-icon name="tmicon-layergroup-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="会员等级"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="pink" :height="150" @click="toPage('/others/publish/publish')">
          <tm-icon name="tmicon-layergroup-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="邀请会员"></tm-text>
        </tm-grid-item>
        <tm-grid-item color="pink" :height="150" @click="toPage('/others/publish/publish')">
          <tm-icon name="tmicon-layergroup-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="导出会员"></tm-text>
        </tm-grid-item>
      </tm-grid> -->
    </tm-sheet>
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <!-- 相册管理 -->
      <tm-cell :margin="[0, 0]" title="相册管理" :bottomBorder="true" rightIcon=""> </tm-cell>
      <tm-grid :width="750" :col="4">
        <tm-grid-item :height="150" @click="openLink('teams/images/images?team_id=' + (info._id || ''))">
          <tm-icon name="tmicon-picture-fill" :font-size="42"></tm-icon>
          <tm-text _class="pt-10" :font-size="22" label="相册"></tm-text>
        </tm-grid-item>
      </tm-grid>
    </tm-sheet>
    <tm-modal title="温馨提示" content="您确认删除当前组织嘛？" :height="300" okText="确定" v-model:show="showWin" @ok="deleteConfirm"></tm-modal>
  </tm-app>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { openLink } from '@/common/tools';
import { onLoad } from '@dcloudio/uni-app';
import { homeTeam, delTeam } from '@/common/index';
const showWin = ref(false);
const info = ref({
  _id: '',
  title: '组织名称',
  logo: '',
  activityCount: 0,
  joinCount: 0,
  followCount: 0
});
onLoad((e: any) => {
  if (e.id) {
    homeTeam({
      id: e.id
    }).then(res => {
      if (res.code === 1000) {
        info.value = res.data;
      } else {
        uni.$tm.u.toast(res.message);
      }
    })
  }
});
function deleteConfirm() {
  delTeam({
    id: info.value._id
  }).then(res => {
    if (res.code === 1000) {
      uni.$tm.u.toast('删除成功');
      showWin.value = false;
      uni.navigateBack();
    } else {
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
