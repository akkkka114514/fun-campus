<template>
    <tm-app color="white">
      <view class="top">
        <view class="logo">
          <tm-avatar :round="26" :size="150" img="https://picsum.photos/200/300"></tm-avatar>
        </view>
      </view>
      <view class="login">
        <view class="box pt-50">
          <tm-form @submit="confirm" ref="form" v-model="loginForm" :label-width="190">
            <tm-form-item required field="username" :rules="[{ required: true, message: '请输入账号' }]">
              <tm-input :inputPadding="[20, 0]" :round="20" prefix="tmicon-account" v-model.lazy="loginForm.username" placeholder="请输入账号" :showBottomBotder="false"> </tm-input>
            </tm-form-item>
            <tm-form-item required field="username" :border="false" :rules="[{ required: true, message: '请输入密码' }]">
              <tm-input :inputPadding="[20, 0]" :round="20" prefix="tmicon-lock" v-model.lazy="loginForm.username" placeholder="请输入密码" :showBottomBotder="false"> </tm-input>
            </tm-form-item>
            <tm-form-item :border="false">
              <tm-button :margin="[10]" :shadow="0" round="20" size="small" block label="立即登录"></tm-button>
              <view class="login-tips flex flex-between px-30">
                  <text class="">忘记密码</text> 
                  <text class="">没有账号?立即注册</text>
              </view>
            </tm-form-item>
          </tm-form>
        </view>
        <view class="footer">
          <tm-divider align="center" label="更多登录方式"></tm-divider>
          <view class="flex-center pt-30"> 
              <view>
                  <tm-avatar :round="25" :size="60" color="#38b434" icon="tmicon-weixin"></tm-avatar>
              </view>
              <view class="ml-30">
                  <tm-avatar :round="25" :size="60"  icon="tmicon-mobile-alt"></tm-avatar>
              </view>
          </view>
        </view>
      </view>
    </tm-app>
  </template>
  
  <script lang="ts" setup>
  import { ref } from 'vue';
  const loginForm = ref({
    username: '',
  });
  function confirm() {
    console.log(loginForm.value);
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
  .footer{
      padding-bottom: 20vh;
  }
  </style>
  