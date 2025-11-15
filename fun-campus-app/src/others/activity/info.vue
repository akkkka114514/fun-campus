<template>
  <tm-app color="white">
    <tm-sheet :margin="[0, 0]" :padding="[0, 0]">
      <view class="member px-50 pt-30">
        <view class="flex-row-center-center">
          <view class="bg-avatar">
            <tm-avatar :size="150" :round="25" :img="userStore.userInfo.avatar"></tm-avatar>
          </view>
        </view>
        <view class="pa-30 flex-row-center-center">{{userStore.userInfo.nickname}}</view>
      </view>
      <!-- 支付费用 -->
      <view class="mt-15">
        <tm-cell :margin="[0, 0]" :bottomBorder="true" v-if="info.is_audit">
          <template v-slot:title>
            <view class="flex">
              <text class="ml-10">审核状态</text>
            </view>
          </template>
          <template v-slot:right>
            <text>{{info.status==1?'已通过':info.status == 2?'已拒绝':'待审核'}}</text>
          </template>
        </tm-cell>
        <tm-cell :margin="[0, 0]" :bottomBorder="true" v-for="(item,index) in info.form_data" :key="index">
          <template v-slot:title>
            <view class="flex">
              <text class="ml-10">{{ item.label }}</text>
            </view>
          </template>
          <template v-slot:right>
            <text>{{ item.value }}</text>
          </template>
        </tm-cell>

        <view class="footer">
          <view class="flex" :class="info.is_cancel && (info.is_edit && info.is_form)?'flex-between':'row-center'">
            <view class="" v-if="info.is_cancel" @click="cancel">
              <tm-icon name="tmicon-minus-circle"></tm-icon>
              <view class="mt-5 tips">取消报名</view>
            </view>
            <view class="" v-if="info.is_edit && info.is_form">
              <tm-icon name="tmicon-edit"></tm-icon>
              <view class="mt-5 tips">修改报名</view>
            </view>
            <!-- <view class="">
              <tm-icon name="tmicon-transanction"></tm-icon>
              <view class="mt-5 tips">立即支付</view>
            </view> -->
          </view>
        </view>
      </view>
    </tm-sheet>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { onLoad } from '@dcloudio/uni-app';
import {applyInfo,applyCancel} from '@/common/index'
const userStore = useUserStore();
const info = ref<any>({});
onLoad((e:any) => {
  if(e.id){
    applyInfo({id:e.id}).then(res=>{
        if(res.code === 1000){
            info.value = res.data
        }
    })
  }
});
function cancel(){
    uni.showModal({
        title: '提示',
        content: '确定取消报名吗？',
        success: function (res) {
            if (res.confirm) {
                applyCancel({id:info.value._id}).then(res=>{
                    uni.$tm.u.toast(res.message)
                    if(res.code === 1000){
                       uni.navigateBack()
                    }
                })
            }
        }
    });
}
</script>
<style lang="scss" scoped>
.member {
  background: linear-gradient(180deg, #3c8af8, #38cddd);
  color: #ffffff;
  .bg-avatar {
    background-color: #ffffff;
    border-radius: 50%;
    padding: 3rpx;
    box-shadow: 0 0 10rpx rgba(0, 0, 0, 0.3);
  }
}
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 0 10rpx rgba(0, 0, 0, 0.1);
  padding: 15rpx 150rpx;
}
.row-center{
    justify-content: center;
}
</style>
