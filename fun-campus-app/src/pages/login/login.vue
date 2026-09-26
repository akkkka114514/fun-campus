<template>
  <view class="login-page">
    <view class="login-header">
      <text class="login-title">Fun Campus</text>
      <text class="login-subtitle">校园二课活动平台</text>
    </view>

    <view class="login-card">
      <view class="form-item">
        <text class="form-label">账号</text>
        <u-input
          v-model="form.username"
          border="none"
          placeholder="请输入学号 / 账号"
          customStyle="flex: 1;"
        />
      </view>

      <view class="form-item">
        <text class="form-label">密码</text>
        <u-input
          v-model="form.password"
          type="password"
          border="none"
          placeholder="请输入密码"
          customStyle="flex: 1;"
        />
      </view>

      <view class="form-item form-item--last">
        <text class="form-label">验证码</text>
        <u-input
          v-model="form.captchaCode"
          border="none"
          placeholder="请输入验证码"
          customStyle="flex: 1;"
        />
        <image
          v-if="captchaImage"
          class="captcha-img"
          :src="captchaImage"
          mode="aspectFit"
          @click="loadCaptcha"
        />
        <view v-else class="captcha-placeholder" @click="loadCaptcha">点击加载</view>
      </view>

      <u-button
        type="primary"
        :loading="logging"
        text="登 录"
        customStyle="margin-top: 56rpx;"
        @click="handleLogin"
      />

      <text class="login-tip">账号或密码有问题请联系学校管理员</text>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { loginApi } from '@/api/login-api';
import { useUserStore } from '@/store/user';

const userStore = useUserStore();

const form = reactive({
  username: '',
  password: '',
  captchaCode: '',
  captchaUuid: '',
});

const captchaImage = ref('');
const logging = ref(false);

// 登录设备码（后端 loginDevice 字典）：H5=4、安卓 App=2、iOS App=3、小程序=5
let loginDevice = 4;
// #ifdef APP-PLUS
const appPlatform = uni.getSystemInfoSync().platform;
loginDevice = appPlatform === 'ios' ? 3 : 2;
// #endif
// #ifdef MP
loginDevice = 5;
// #endif

// 加载图形验证码（captchaBase64Image 自带 data:image/png;base64, 前缀）
async function loadCaptcha() {
  try {
    const { data } = await loginApi.getCaptcha();
    captchaImage.value = data.captchaBase64Image;
    form.captchaUuid = data.captchaUuid;
    form.captchaCode = '';
  } catch (e) {
    captchaImage.value = '';
    form.captchaUuid = '';
  }
}

async function handleLogin() {
  if (!form.username) {
    uni.showToast({ title: '请输入账号', icon: 'none' });
    return;
  }
  if (!form.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' });
    return;
  }
  if (!form.captchaCode) {
    uni.showToast({ title: '请输入验证码', icon: 'none' });
    return;
  }
  if (!form.captchaUuid) {
    uni.showToast({ title: '验证码已失效，请点击图片刷新', icon: 'none' });
    loadCaptcha();
    return;
  }
  logging.value = true;
  try {
    await userStore.login({ ...form, loginDevice });
    uni.showToast({ title: '登录成功', icon: 'success' });
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' });
    }, 400);
  } catch (e) {
    // 登录失败（含验证码错误）刷新验证码
    loadCaptcha();
  } finally {
    logging.value = false;
  }
}

onShow(() => {
  // 已登录直接进首页
  if (userStore.isLogin) {
    uni.reLaunch({ url: '/pages/index/index' });
    return;
  }
  loadCaptcha();
});
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 0 48rpx;
  box-sizing: border-box;
}

.login-header {
  display: flex;
  flex-direction: column;
  padding-top: 160rpx;
  margin-bottom: 72rpx;
}

.login-title {
  font-size: 64rpx;
  font-weight: 700;
  color: $fc-primary;
}

.login-subtitle {
  margin-top: 16rpx;
  font-size: 28rpx;
  color: $fc-text-sub;
}

.login-card {
  background: $fc-card-bg;
  border-radius: 24rpx;
  padding: 24rpx 40rpx 48rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.04);
}

.form-item {
  display: flex;
  align-items: center;
  height: 108rpx;
  border-bottom: 1rpx solid #f0f1f3;
}

.form-item--last {
  border-bottom: none;
}

.form-label {
  width: 140rpx;
  font-size: 30rpx;
  color: $fc-text-main;
}

.captcha-img {
  width: 200rpx;
  height: 72rpx;
  margin-left: 16rpx;
  border-radius: 8rpx;
  background: #f5f6f8;
}

.captcha-placeholder {
  width: 200rpx;
  height: 72rpx;
  margin-left: 16rpx;
  border-radius: 8rpx;
  background: #f5f6f8;
  font-size: 24rpx;
  color: $fc-text-sub;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-tip {
  display: block;
  margin-top: 32rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
  text-align: center;
}
</style>
