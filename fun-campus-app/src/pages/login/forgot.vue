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
          <tm-form-item required field="mobile" :rules="[{ required: true, message: '请输入手机号码' }]">
            <tm-input :inputPadding="[20, 0]" :round="20" prefix="tmicon-mobile-alt" v-model.lazy="loginForm.mobile" placeholder="请输入手机号码" :showBottomBotder="false">
            </tm-input>
          </tm-form-item>
          <tm-form-item required field="code" :rules="[{ required: true, message: '请输入验证码' }]">
            <tm-input :inputPadding="[20, 0]" :round="20" prefix="tmicon-safety-certificate" v-model.lazy="loginForm.code" placeholder="请输入验证码" :showBottomBotder="false">
              <template #right>
                <tm-button :margin="[0, 0]" :width="150" :padding="[10, 0]" :height="50" :shadow="0" :round="20" size="small" :label="codeMsg" @click="sendCode"></tm-button>
              </template>
            </tm-input>
          </tm-form-item>
          <tm-form-item required field="password" :border="false" :rules="[{ required: true, message: '请输入新密码' }]">
            <tm-input :inputPadding="[20, 0]" type="password" :round="20" prefix="tmicon-lock" v-model.lazy="loginForm.password" placeholder="请输入新密码" :showBottomBotder="false"> </tm-input>
          </tm-form-item>
          <tm-form-item :border="false">
            <tm-button :margin="[10]" :shadow="0" :round="20" size="small" form-type="submit" block label="确定"></tm-button>
            <view class="login-tips flex flex-between px-30">
              <text class="" @click="openLink('pages/login/login', 1)">已有账号,立即登录</text>
              <text class="" @click="openLink('pages/login/register', 1)">没有账号?立即注册</text>
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
import { sendSmsCode, forget } from '@/common/index';
import { useAppStore } from '@/stores/app';
const appStore = useAppStore();
const codeMsg = ref('发送验证码');
const loginForm = ref({
  mobile: '',
  code: '',
  password: '',
});
const time = ref(60);
// 倒计时
function countDown() {
  if (time.value <= 0) {
    codeMsg.value = '发送验证码';
    time.value = 60;
    return;
  } else {
    codeMsg.value = `剩余${time.value}s`;
    time.value--;
  }
  setTimeout(countDown, 1000);
}

function sendCode() {
  if (time.value !== 60) {
    return;
  }
  if (!uni.$tm.u.isPhone(loginForm.value.mobile)) {
    uni.$tm.u.toast('请输入正确的手机号码');
    return;
  }
  sendSmsCode({
    mobile: loginForm.value.mobile,
    scene: 'forget',
  }).then(res => {
    if (res.code === 1000) {
      uni.$tm.u.toast('发送成功');
      countDown();
    } else {
      uni.$tm.u.toast(res.message);
    }
  });
}
function confirm(e: any) {
  if (e.validate) {
    forget(e.data).then(res => {
      if (res.code === 1000) {
        uni.$tm.u.toast('修改成功');
        setTimeout(() => {
          openLink('pages/login/login', 1);
        }, 1500);
      } else {
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
