<template>
  <tm-app color="white">
    <view class="login-container">
      <view class="login-box">
        <view class="title text-center text-size-xl text-weight-b mb-50">欢迎登录</view>
        <tm-form class="login-form" @submit="usernamePasswordLogin" ref="form" v-model="loginForm" :label-width="0" :transprent=true>
          <tm-form-item required field="username" :rules="[{ required: true, message: '请输入账号' }]">
            <tm-input 
              :inputPadding="[30, 0]" 
              :round="26" 
              prefix="tmicon-account" 
              v-model.lazy="loginForm.username" 
              placeholder="请输入账号" 
              :showBottomBotder="false"
              clearable
              class="custom-input"
              :font-size="32"
              :height="80"
            >
            </tm-input>
          </tm-form-item>
          <tm-form-item required field="password" :rules="[{ required: true, message: '请输入密码' }]">
            <tm-input 
              :inputPadding="[30, 0]" 
              type="password" 
              :round="26" 
              prefix="tmicon-lock" 
              v-model.lazy="loginForm.password" 
              placeholder="请输入密码" 
              :showBottomBotder="false"
              clearable
              class="custom-input"
              :font-size="32"
              :height="80"
            >
            </tm-input>
          </tm-form-item>
        <tm-form-item required field="captchaCode" :border="false" :rules="[{ required: true, message: '请输入验证码' }]">
          <view class="captcha-container">
            <tm-input
                :inputPadding="[30, 0]"
                :round="26"
                prefix="tmicon-picture"
                v-model.lazy="loginForm.captchaCode"
                placeholder="请输入验证码"
                :showBottomBotder="false"
                class="captcha-input custom-input"
                :font-size="32"
                :height="80"
            >
            </tm-input>
            <image
                :src="captchaBase64Image"
                class="captcha-image"
                @click="captcha"
            >
            </image>
          </view>
        </tm-form-item>

          <tm-form-item :border="false">
            <tm-button 
              :margin="[20]" 
              :shadow="0" 
              :round="26" 
              size="normal" 
              form-type="submit" 
              block 
              label="立即登录"
              type="primary"
              class="login-button"
              :font-size="34"
              :height="90"
            ></tm-button>
            <view class="login-tips flex flex-between px-30 mt-40">
              <text class="text-size-n tip-link" @click="openLink('pages/login/forgot')">忘记密码</text>
              <text class="text-size-n tip-link" @click="openLink('pages/login/register')">没有账号?立即注册</text>
            </view>
          </tm-form-item>
        </tm-form>
      </view>
      <view class="footer">
        <tm-divider align="center" label="其他登录方式" color="#999"></tm-divider>
        <view class="flex-center pt-30">
          <view>
            <!-- 短信验证码 -->
            <tm-avatar 
              :round="25" 
              :size="60" 
              icon="tmicon-bell" 
              @click="openLink('pages/login/mobile')"
              class="social-icon"
            ></tm-avatar>
          </view>

          <!-- #ifdef H5 -->
          <view class="ml-50" v-if="isWeixin">
            <!-- 公众号 -->
            <tm-avatar 
              :round="25" 
              :size="60" 
              color="#38b434" 
              icon="tmicon-weixin" 
              @click="wechatLogin"
              class="social-icon"
            ></tm-avatar>
          </view>
          <!-- #endif -->

          <!-- #ifdef MP-WEIXIN -->
          <view class="ml-50">
            <!-- 小程序 -->
            <tm-avatar 
              :round="25" 
              :size="60" 
              color="#38b434" 
              icon="tmicon-weixin" 
              @click="showWin = true"
              class="social-icon"
            ></tm-avatar>
          </view>
          <!-- #endif -->

          <!-- #ifdef APP-PLUS -->
          <view class="ml-50" v-if="isShowMobileLogin">
            <!-- 手机一键登录 -->
            <tm-avatar 
              :round="25" 
              :size="60" 
              color="#f08d49" 
              icon="tmicon-md-phone-portrait" 
              @click="mobileLogin"
              class="social-icon"
            ></tm-avatar>
          </view>
          <!-- #endif -->
        </view>
      </view>
    </view>
    <!-- #ifdef MP-WEIXIN -->
    <tm-drawer ref="calendarView" :hideHeader="true" :height="800" placement="bottom" v-model:show="showWin">
      <view class="pa-30">
        <view class="pt-10">
          <text class="text-size-g">授权获取您的头像、昵称</text>
        </view>
        <view class="tips mt-15 pb-50">
          <text>获取用户头像、昵称，主要用于完善用户个人资料，向用户提供更好的使用体验～</text>
        </view>
        <view class="flex-row-center-start pt-10" @click="uploadAvatar">
          <text class="mr-30">头像</text>
          <tm-avatar :round="25" :size="70" :img="avatarUrl"></tm-avatar>
        </view>
        <view class="flex mt-30">
          <view class="mr-30">昵称</view>
          <view>
            <input v-model="nickname" placeholder="请输入昵称" type="nickname" @blur="inputBlur" />
          </view>
        </view>
        <view class="mt-50 pt-30 px-30">
          <tm-button :margin="[10]" :shadow="0" :round="20" size="small" block label="立即登录" @click="usernamePasswordLogin"></tm-button>
        </view>
      </view>
    </tm-drawer>
    <!-- #endif -->
  </tm-app>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { login, getCaptcha } from '@/common';
import { useAppStore } from '@/stores/app';
import { useUserStore } from '@/stores/user';
import { openLink } from '@/common/tools';
import { onLoad } from '@dcloudio/uni-app';
const userStore = useUserStore();
let captchaBase64Image = ref('static/captcha-placeholder.png'); // 默认占位图
const loginForm = ref({
  username: '',
  password: '',
  loginDevice: -1,
  emailCode:'',
  captchaCode: '',
  captchaUuid: '',
});
// #ifdef MP-WEIXIN
const showWin = ref(false);
const nickname = ref('');
const avatarUrl = ref('');
async function uploadAvatar() {
  const res = await uni.chooseImage({ count: 1 });
  const { tempFilePath }: any = await uni.cropImage({ src: res.tempFilePaths[0], cropScale: '1:1' });
  let y, m, d, day, rand, cloudPath;
  d = new Date();
  y = d.getFullYear().toString();
  m = (d.getMonth() + 1).toString().padStart(2, '0');
  day = d.getDate().toString().padStart(2, '0');
  rand = Math.floor(Math.random() * 900) + 100;
  cloudPath = y + '/' + m + '/' + day + '/' + Date.now() + rand + '.' + tempFilePath.split('.').pop();
  const result = await uniCloud.uploadFile({
    filePath: tempFilePath,
    cloudPath,
    onUploadProgress(e) {
      console.log(e);
    },
  });
  let url = '';
  if (/^cloud:\/\//.test(result.fileID)) {
    //腾讯云处理
    const { fileList } = await uniCloud.getTempFileURL({
      fileList: [result.fileID],
    });
    if (fileList.length) {
      url = fileList[0].tempFileURL || fileList[0].download_url;
    }
  } else {
    url = result.fileID;
  }
  avatarUrl.value = url;
}
function inputBlur(e: any) {
  nickname.value = e.detail.value;
}
function toAuthLogin() {
  uni.login({
    provider: 'weixin', //使用微信登录
    success: function (res) {
      if (res.code) {
        login({
          code: res.code,
          nickname: nickname.value,
          avatar: avatarUrl.value,
          loginType: 'mpWechat',
        }).then(res => {
          if (res.code === 1000) {
            userStore.setUserInfo(res.data);
            uni.reLaunch({
              url: '/pages/index/index',
            });
          } else {
            uni.$tm.u.toast(res.message);
          }
        });
      }
    }
  });
}
// #endif
// #ifdef H5
const isWeixin = computed(() => {
  // H5 判断 是否是微信环境
  return window.navigator.userAgent.toLowerCase().includes('micromessenger');
});

function wechatLogin() {
  // 微信登录
  console.log('微信登录');
  login({
    url: window.location.href,
    loginType: 'wechat',
  }).then(res => {
    console.log(res);
    if (res.code === 1000) {
      window.location.href = res.data;
    } else {
      uni.$tm.u.toast(res.message);
    }
  });
}
// #endif
// #ifdef APP-PLUS
const isShowMobileLogin = ref(false);
function mobileLogin() {
  uni.login({
	provider: 'univerify',
	univerifyStyle: { // 自定义登录框样式
    //参考`univerifyStyle 数据结构`
  },
	success(res:any){ // 登录成功
		console.log(res.authResult);  // {openid:'登录授权唯一标识',access_token:'接口返回的 token'}
    login({
      openid: res.authResult.openid,
      access_token: res.authResult.access_token,
      loginType: 'mobile',
    }).then(res => {
      if (res.code === 1000) {
        uni.closeAuthView();
        userStore.setUserInfo(res.data);
        uni.reLaunch({
          url: '/pages/index/index',
        });
      } else {
        uni.$tm.u.toast(res.message);
      }
    });
	},
	fail(res){  // 登录失败
		console.log(res)
	}
})

}
// #endif

function captcha() {
  getCaptcha().then(res => {
    // 设置验证码图片URL
    if (res.code === 0 && res.data) {
      captchaBase64Image.value = res.data.captchaBase64Image;
      loginForm.value.captchaUuid = res.data.captchaUuid;
    } else {
      console.log('获取验证码失败:', res.message || res);
      uni.$tm.u.toast(res.message || '获取验证码失败');
    }
  }).catch(err => {
    console.error('获取验证码异常:', err);
    uni.$tm.u.toast('获取验证码失败');
  });
}

function usernamePasswordLogin(){
  console.log(loginForm)
  login({
    username: loginForm.value.username,
    password: loginForm.value.password,
    emailCode: loginForm.value.emailCode,
    loginDevice: loginForm.value.loginDevice,
    captchaCode: loginForm.value.captchaCode,
    captchaUuid: loginForm.value.captchaUuid
  }).then(res => {
    console.log(res)
    if (res.code === 0) {
      userStore.setUserInfo(res.data);
      uni.reLaunch({
        url: '/pages/index/index',
      });
    } else {
      uni.$tm.u.toast(res.message);
      captcha();
    }
  }).catch(err => {
    console.error('登录请求出错:', err);
    uni.$tm.u.toast('登录请求失败');
  });
}
function getDeviceInfo() : void {
  try {
    const info = uni.getSystemInfoSync();
    if (info.platform === 'ios') loginForm.value.loginDevice = 3
    if (info.platform === 'android') loginForm.value.loginDevice = 2
    if (info.platform === 'h5') loginForm.value.loginDevice = 4
    if (info.platform === 'mp-weixin') loginForm.value.loginDevice = 5
    if (info.platform === 'windows') loginForm.value.loginDevice = 1
  } catch (e) {
    console.error('获取设备信息失败:', e);
  }
}

onMounted(() => {
  // 页面加载时获取验证码
  captcha();
  getDeviceInfo();
});

onLoad((e: any) => {
  // #ifdef H5
  if (e.code && e.state) {
    // 微信登录
    console.log(e);
    login({
      code: e.code,
      state: e.state,
      loginType: 'wechat',
    }).then(res => {
      console.log(res);
      if (res.code === 1000) {
        userStore.setUserInfo(res.data);
        uni.reLaunch({
          url: '/pages/index/index',
        });
      } else {
        uni.$tm.u.toast(res.message);
      }
    });
  }
  // #endif
  // #ifdef APP-PLUS
  uni.preLogin({
    provider: 'univerify',
    success() {  //预登录成功
      isShowMobileLogin.value = true;
    },
    fail(err) {  // 预登录失败
      isShowMobileLogin.value = false;
      console.log(err);
    }
  })
  // #endif
});
</script>

<style lang="scss" scoped>
.top {
  background: linear-gradient(135deg, #3c8af8 0%, #2962ff 100%);
  height: 350rpx;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle at 30% 20%, rgba(255,255,255,0.15) 0%, rgba(255,255,255,0) 60%);
  }
}

.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 40rpx;
  background: linear-gradient(135deg, #3c8af8 0%, #2962ff 100%);
  
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle at 30% 20%, rgba(255,255,255,0.15) 0%, rgba(255,255,255,0) 60%);
  }

  .login-box {
    width: 100%;
    max-width: 750rpx;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 30rpx;
    box-shadow: 0 20rpx 50rpx rgba(0,0,0,0.15), 
                inset 0 1px 0 rgba(255,255,255,0.5);
    padding: 70rpx 60rpx;
    margin-bottom: 50rpx;
    border: 1px solid rgba(230, 230, 230, 0.8);
    backdrop-filter: blur(10rpx);
    position: relative;
    z-index: 2;
      
      .title {
        margin-bottom: 60rpx;
        color: #222;
        font-weight: 600;
        letter-spacing: 1px;
        position: relative;
        font-size: 36rpx;
        
        &::after {
          content: "";
          position: absolute;
          bottom: -15rpx;
          left: 50%;
          transform: translateX(-50%);
          width: 60rpx;
          height: 6rpx;
          border-radius: 3rpx;
        }
      }
      
      .login-tips {
        color: #3c8af8;
        font-size: 26rpx;
        margin-top: 40rpx;
        display: flex;
        justify-content: space-between;
        
        .tip-link {
          transition: all 0.3s ease;
          position: relative;
          padding: 8rpx 0;
          
          &:hover {
            color: #2962ff;
            transform: translateY(-3rpx);
          }
          
          &::after {
            content: "";
            position: absolute;
            bottom: 0;
            left: 0;
            width: 0;
            height: 2rpx;
            background: #2962ff;
            transition: width 0.3s ease;
          }
          
          &:hover::after {
            width: 100%;
          }
        }
      }
    }
  
  .footer {
    width: 100%;
    max-width: 750rpx;
    text-align: center;
    
    .tm-divider {
      margin-bottom: 30rpx;
    }
    
    .flex-center {
      display: flex;
      justify-content: center;
      gap: 40rpx;
    }
  }
}

// 添加动画效果
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}

// 按钮样式优化
.tm-button {
  transition: all 0.3s ease;
  &:active {
    transform: scale(0.98);
  }
}

.login-button {
  background: linear-gradient(135deg, #3c8af8, #2962ff);
  color: white;
  font-weight: 500;
  letter-spacing: 1rpx;
  box-shadow: 0 10rpx 20rpx rgba(60, 138, 248, 0.3);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 5rpx 15rpx rgba(60, 138, 248, 0.2);
  }
  
  &:hover {
    box-shadow: 0 15rpx 25rpx rgba(60, 138, 248, 0.4);
    transform: translateY(-3rpx);
  }
}

// 输入框聚焦效果
.tm-input {
  transition: all 0.3s ease;
  
  &:focus-within {
    box-shadow: 0 0 0 2rpx #3c8af8;
  }
}

.custom-input {
  background-color: rgba(248, 249, 250, 0.8);
  border: 2rpx solid #e9ecef;
  transition: all 0.3s ease;
  font-size: 32rpx;
  
  &:focus-within {
    border-color: #3c8af8;
    box-shadow: 0 0 0 2rpx rgba(60, 138, 248, 0.2);
    background-color: #fff;
  }
  
  ::v-deep .tm-input__field {
    background-color: transparent;
  }
}

// 验证码容器样式
.captcha-container {
  display: flex;
  align-items: center;
  gap: 20rpx;
  
  .captcha-input {
    flex: 1;
  }
  
  .captcha-image {
    width: 80rpx;
    height: 80rpx;
    border-radius: 12rpx;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4rpx 10rpx rgba(0,0,0,0.05);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
      transition: all 0.3s ease;
    }
    
    &:hover {
      transform: scale(1.05);
      box-shadow: 0 6rpx 15rpx rgba(0,0,0,0.1);
      
      &::before {
        left: 100%;
      }
    }
    
    &:active {
      transform: scale(0.98);
    }
  }
  
}

// 社交登录图标样式
.social-icon {
  transition: all 0.3s ease;
  cursor: pointer;
  box-shadow: 0 4rpx 10rpx rgba(0,0,0,0.05);
  
  &:hover {
    transform: translateY(-5rpx);
    box-shadow: 0 8rpx 20rpx rgba(0,0,0,0.1);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

// 表单间距优化
.tm-form-item {
  margin-bottom: 30rpx;
}

// 浮动动画
@keyframes float {
  0% {
    transform: translate(-50%, -50%) translateY(0px);
  }
  50% {
    transform: translate(-50%, -50%) translateY(-15px);
  }
  100% {
    transform: translate(-50%, -50%) translateY(0px);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    padding: 20rpx;
  }
  
  .login-box {
    padding: 40rpx 30rpx;
    max-width: none;
  }
  
  .title {
    font-size: 32rpx;
    margin-bottom: 40rpx;
  }
  
  .custom-input {
    font-size: 28rpx;
  }
  
  .captcha-container {
    flex-direction: column;
    align-items: stretch;
    
    .captcha-image {
      width: 100%;
      height: 80rpx;
      margin-top: 10rpx;
    }
  }
  
  .footer {
    max-width: none;
  }
}
</style>