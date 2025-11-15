<template>
  <tm-app>
    <tm-sheet :margin="[0, 0]" :padding="[30, 30]">
      <tm-form @submit="confirm" :margin="[0, 0]" :padding="[0, 0]" v-model="formData" :label-width="120">
        <view class=""> 组织类型 </view>
        <tm-form-item :margin="[0]" field="type">
          <tm-radio-group v-model.lazy="formData.type" :disabled="formData.status === 0">
            <tm-radio :value="1" label="个人"></tm-radio>
            <tm-radio :value="2" label="企业"></tm-radio>
          </tm-radio-group>
        </tm-form-item>
        <!--  -->
        <view class="pt-30"> 管理员 </view>
        <tm-form-item :margin="[0]" required label="姓名" field="name" :rules="[{ required: true, message: '请输入姓名' }]">
          <tm-input :disabled="formData.status === 0" v-model.lazy="formData.name" :showClear="formData.status !== 0"></tm-input>
        </tm-form-item>
        <tm-form-item :margin="[0]" required label="手机" field="mobile" :rules="[{ required: true, message: '请输入手机号码' }]">
          <tm-input :disabled="formData.status === 0" v-model.lazy="formData.mobile" :showClear="formData.status !== 0"></tm-input>
        </tm-form-item>
        <tm-form-item :margin="[0]" required label="微信号" field="wechat" :rules="[{ required: true, message: '请输入微信号' }]">
          <tm-input :disabled="formData.status === 0" v-model.lazy="formData.wechat" :showClear="formData.status !== 0"></tm-input>
        </tm-form-item>
        <!--  -->
        <view class="pt-30"> 身份证 </view>
        <tm-form-item :margin="[50]" required field="idCardFront" :rules="[{ required: true, message: '请上传身份证人像面' }]">
          <view class="identity" @click="chooseIdentity(1)">
            <image style="width: 80%; height: 200rpx" :src="formData.idCardFront || '../../static/identity2.png'" mode="aspectFit"></image>
            <view class="tips mt-15">点击上传身份证人像面</view>
          </view>
        </tm-form-item>
        <tm-form-item :margin="[50]" required field="idCardBack" :rules="[{ required: true, message: '请上传身份证国徽面' }]">
          <view class="identity" @click="chooseIdentity(2)">
            <image style="width: 80%; height: 200rpx" :src="formData.idCardBack || '../../static/identity1.png'" mode="aspectFit"></image>
            <view class="tips mt-15">点击上传身份证国徽面</view>
          </view>
        </tm-form-item>
        <view class="flex-col-top-center mt-50 mx-10">
          <tm-checkbox :size="35" v-model="agree" label="点击代表您已阅读并同意"></tm-checkbox> <text class="text-blue">《用户报名服务协议》</text>
        </view>
        <tm-form-item :border="false">
          <template v-if="formData.status === 1">
            <tm-alert color="green" text :border="1" :content="[{content:'认证已通过'}]" :height="80"></tm-alert>
          </template>
          <template v-if="formData.status === 2">
            <tm-alert color="pink" text :border="1" :content="[{content:formData.message}]" :height="80"></tm-alert>
          </template>
          <tm-button
            :margin="[10]"
            :shadow="0"
            :disabled="formData.status === 0"
            :round="20"
            size="small"
            form-type="submit"
            block
            :label="formData.status===0?'审核中':formData.status===void 0?'提交认证':'重新认证'"
          ></tm-button>
        </tm-form-item>
      </tm-form>
    </tm-sheet>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import {getApproveInfo,approve,upload} from '@/common/index'
const agree = ref(false);
const formData = ref<any>({
  type:1,
  name:'',
  mobile:'',
  wechat:'',
  idCardFront:'',
  idCardBack:'',
  id:'',
});
// 获取认证信息
onLoad((e:any)=>{
  if(e.team_id){
    formData.value.id = e.team_id;
    getApproveInfo({
      id:e.team_id
    }).then(res=>{
      if(res.code === 1000){
       if(res.data.name !== void 0){
          formData.value = res.data;
          formData.value.type = res.data.type || 1;
          if(res.data.status === 0){
            agree.value = true;
          }
        }
      }
    })
  }
})
function chooseIdentity(type: number) {
  if(formData.value.status === 0){    
    return;
  }
  uni.chooseImage({
    count: 1,
    success: (res: any) => {
      uni.showLoading({
        title: '上传中...',
        mask: true,
      });
      upload(res.tempFiles[0]).then((url: any) => {
          if (type == 1) {
            formData.value.idCardFront = url;
          } else {
            formData.value.idCardBack = url;
          }
      }).finally(()=>{
        uni.hideLoading();
      });
    },
  });
}
function confirm(e: any) {
  if (e.validate) {
    if(!agree.value){
      uni.$tm.u.toast('请先同意《用户报名服务协议》');
      return;
    }
    approve(e.data).then(res => {
      if (res.code == 1000) {
        uni.$tm.u.toast('提交成功');
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      }else{
        uni.$tm.u.toast(res.message);
      }
    });
  }
}
</script>
<style lang="scss" scoped>
.identity{
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid #ccc;
  padding: 20px;
  border-radius: 10px;
}
</style>
