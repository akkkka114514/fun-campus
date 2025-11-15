<template>
  <tm-app color="white">
    <view class="top">
      <view class="logo">
        <tm-avatar :round="26" :size="150" :img="appStore.app_logo"></tm-avatar>
      </view>
    </view>
    <view class="login">
      <view class="box pt-50">
        <tm-form @submit="confirm" ref="form" v-model="loginForm" :label-width="190">
          <tm-form-item required field="username" :rules="[{ required: true, message: '请输入账号' }]">
            <tm-input :inputPadding="[20, 0]" :round="20" prefix="tmicon-account" v-model.lazy="loginForm.username" placeholder="请输入账号" :showBottomBotder="false"> </tm-input>
          </tm-form-item>
          <tm-form-item required field="password" :rules="[{ required: true, message: '请输入密码' }]">
            <tm-input :inputPadding="[20, 0]" type="password" :round="20" prefix="tmicon-lock" v-model.lazy="loginForm.password" placeholder="请输入密码" :showBottomBotder="false"> </tm-input>
          </tm-form-item>
          <tm-form-item required field="captcha" :border="false" :rules="[{ required: true, message: '请输入验证码' }]">
            <tm-input :inputPadding="[20, 0]" :round="20" prefix="tmicon-picture" v-model.lazy="loginForm.captcha" placeholder="请输入验证码" :showBottomBotder="false">
              <template #right>
                <image :src="captchaUrl" style="width: 133rpx; height: 60rpx; margin-right: -20rpx"></image>
              </template>
            </tm-input>
          </tm-form-item>
          <tm-form-item :border="false">
            <tm-button :margin="[10]" :shadow="0" :round="20" size="small" form-type="submit" block label="立即注册"></tm-button>
            <view class="login-tips flex flex-between px-30">
              <text class="" @click="openLink('pages/login/forgot')">忘记密码</text>
              <text class="" @click="openLink('pages/login/login', 1)">已有账号?立即登录</text>
            </view>
          </tm-form-item>
        </tm-form>
      </view>
    </view>
  </tm-app>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { openLink } from '@/common/tools';
import { register, getCaptcha } from '@/common/index';
import { useAppStore } from '@/stores/app';
const appStore = useAppStore();
const captchaUrl = ref('');
const loginForm = ref({
  username: '',
  password: '',
  captcha: '',
});
function captcha() {
  getCaptcha({ scene: 'register' }).then(res => {
    if (res.code == 1000) {
      captchaUrl.value = res.data;
    }
  });
}
captcha();
function confirm(e: any) {
  if (e.validate) {
    register({
      ...e.data,
    }).then(res => {
      if (res.code == 1000) {
        openLink('pages/login/login', 1);
      }else{
        captcha();
        uni.$tm.u.toast(res.message);
      }
    });
  }
}
</script>

<style lang="scss" scoped>
.top {
  background-color: #3c8af8;
  height: 350rpx;
  position: relative;
  .logo {
    position: absolute;
    left: 50%;
    top: 170rpx;
    transform: translate(-50%, -50%);
    z-index: 100;
  }
}
.login {
  flex: 1;
  margin-top: -170rpx;
  position: relative;
  padding: 0 30rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  .box {
    height: 100%;
    background-color: #fff;
    border-top-right-radius: 15rpx;
    border-top-left-radius: 15rpx;
    .login-tips {
      color: #3c8af8;
      font-size: 24rpx;
      margin-top: 30rpx;
    }
  }
}
</style>
