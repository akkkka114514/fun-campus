<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <tm-cell :margin="[0, 0]" :bottomBorder="true">
        <template v-slot:title>
          <view class="flex align-center">
            <text class="mr-10">禁止分享</text>
            <tm-icon name="tmicon-question-circle" :font-size="28" @click="showWin = true,key='share'"></tm-icon>
          </view>
        </template>
        <template v-slot:right>
          <tm-switch v-model="settingForm.is_share" :defaultValue="settingForm.is_share"></tm-switch>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true">
        <template v-slot:title>
          <view class="flex align-center">
            <text class="mr-10">报名可取消</text>
            <tm-icon name="tmicon-question-circle" :font-size="28" @click="showWin = true,key='cancel'"></tm-icon>
          </view>
        </template>
        <template v-slot:right>
          <tm-switch v-model="settingForm.is_cancel" :defaultValue="settingForm.is_cancel"></tm-switch>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true">
        <template v-slot:title>
          <view class="flex align-center">
            <text class="mr-10">报名可修改</text>
            <tm-icon name="tmicon-question-circle" :font-size="28" @click="showWin = true,key='edit'"></tm-icon>
          </view>
        </template>
        <template v-slot:right>
          <tm-switch v-model="settingForm.is_edit" :defaultValue="settingForm.is_edit"></tm-switch>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true">
        <template v-slot:title>
          <view class="flex align-center">
            <text class="mr-10">报名需审核</text>
            <tm-icon name="tmicon-question-circle" :font-size="28" @click="showWin = true,key='audit'"></tm-icon>
          </view>
        </template>
        <template v-slot:right>
          <tm-switch v-model="settingForm.is_audit" :defaultValue="settingForm.is_audit"></tm-switch>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true">
        <template v-slot:title>
          <view class="flex align-center">
            <text class="mr-10">是否隐藏浏览数</text>
            <tm-icon name="tmicon-question-circle" :font-size="28" @click="showWin = true,key='hidden_views'"></tm-icon>
          </view>
        </template>
        <template v-slot:right>
          <tm-switch v-model="settingForm.is_hidden_views" :defaultValue="settingForm.is_hidden_views"></tm-switch>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true">
        <template v-slot:title>
          <view class="flex align-center">
            <text class="mr-10">是否隐藏报名数</text>
            <tm-icon name="tmicon-question-circle" :font-size="28" @click="showWin = true,key='hidden_will_num'"></tm-icon>
          </view>
        </template>
        <template v-slot:right>
          <tm-switch v-model="settingForm.is_hidden_will_num" :defaultValue="settingForm.is_hidden_will_num"></tm-switch>
        </template>
      </tm-cell>
      <tm-cell :margin="[0, 0]" :bottomBorder="true">
        <template v-slot:title>
          <view class="flex align-center">
            <text class="mr-10">公开报名用户</text>
            <tm-icon name="tmicon-question-circle" :font-size="28" @click="showWin = true,key='public_will'"></tm-icon>
          </view>
        </template>
        <template v-slot:right>
          <tm-switch v-model="settingForm.is_public_will" :defaultValue="settingForm.is_public_will"></tm-switch>
        </template>
      </tm-cell>
    </tm-sheet>
    <tm-modal color="white" okColor="#3c8af8" :closeable="true" :height="300" okLinear="left" title="温馨提示" :content="tips[key]" v-model:show="showWin">
      <template v-slot:button>
        <text></text>
      </template>
    </tm-modal>
    <view class="footer py-10 px-50">
      <tm-button :round="20" type="primary" block label="确定" @click="confirm"></tm-button>
    </view>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { myActivityInfo,activitySetting } from '@/common/index';
const showWin = ref(false);
const key = ref('share');
const tips = ref<any>({
  share: '禁止别人分享，只有发起人和管理员可以分享活动',
  cancel: '报名用户可以自己取消报名，否则只有发起人和管理员才可以取消报名',
  edit: '报名用户可以自己修改报名信息',
  audit: '管理员审核不通过时，报名会被取消，若有付款，则会自动退款，审核通过后不可修改报名',
  hidden_views: '隐藏活动的浏览数',
  hidden_will_num: '隐藏活动的报名数',
  public_will: '用户报名后可以查看活动的报名用户列表'
})
const settingForm = ref({
  id: '',
  is_share: false,
  is_cancel: false,
  is_edit: false,
  is_audit: false,
  is_hidden_views: false,
  is_hidden_will_num: false,
  is_public_will: false
});
onLoad(async (e: any) => {
  if (e.id) {
    myActivityInfo({ id: e.id }).then(res => {
      if (res.code === 1000) {
        settingForm.value.id = res.data._id;
        settingForm.value.is_share = res.data.is_share;
        settingForm.value.is_cancel = res.data.is_cancel;
        settingForm.value.is_edit = res.data.is_edit;
        settingForm.value.is_audit = res.data.is_audit;
        settingForm.value.is_hidden_views = res.data.is_hidden_views;
        settingForm.value.is_hidden_will_num = res.data.is_hidden_will_num;
        settingForm.value.is_public_will = res.data.is_public_will;
      }
    })
  }
})
function confirm() {
  activitySetting(settingForm.value).then(res => {
    uni.$tm.u.toast(res.message);
    if (res.code === 1000) {      
      setTimeout(() => {
        uni.navigateBack({
          delta: 1
        })
      }, 1000);
    }
  })
}
</script>
<style lang="scss" scoped>
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 0 10rpx rgba(0, 0, 0, 0.1);
}
</style>
