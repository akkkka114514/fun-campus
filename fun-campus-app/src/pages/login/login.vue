<template>
  <tm-app color="white">
    <view class="top">
      <view class="logo">
        <tm-avatar :round="26" :size="150" :img="appStore.app_logo"></tm-avatar>
      </view>
    </view>
    <view class="login">
      <view class="box">
        <view class="title text-center text-size-xl text-weight-b mb-50">欢迎登录</view>
        <tm-form @submit="confirm" ref="form" v-model="loginForm" :label-width="0">
          <tm-form-item required field="username" :rules="[{ required: true, message: '请输入账号' }]">
            <tm-input 
              :inputPadding="[20, 0]" 
              :round="20" 
              prefix="tmicon-account" 
              v-model.lazy="loginForm.username" 
              placeholder="请输入账号" 
              :showBottomBotder="false"
              clearable
            >
            </tm-input>
          </tm-form-item>
          <tm-form-item required field="password" :rules="[{ required: true, message: '请输入密码' }]">
            <tm-input 
              :inputPadding="[20, 0]" 
              type="password" 
              :round="20" 
              prefix="tmicon-lock" 
              v-model.lazy="loginForm.password" 
              placeholder="请输入密码" 
              :showBottomBotder="false"
              clearable
            >
            </tm-input>
          </tm-form-item>
          <tm-form-item required field="captcha" :border="false" :rules="[{ required: true, message: '请输入验证码' }]">
            <view class="captcha-container">
              <tm-input 
                :inputPadding="[20, 0]" 
                :round="20" 
                prefix="tmicon-picture" 
                v-model.lazy="loginForm.captcha" 
                placeholder="请输入验证码" 
                :showBottomBotder="false"
                class="captcha-input"
              >
              </tm-input>
              <image 
                :src="captchaUrl" 
                class="captcha-image" 
                @click="captcha"
              >
              </image>
            </view>
          </tm-form-item>
          <tm-form-item :border="false">
            <tm-button 
              :margin="[10]" 
              :shadow="0" 
              :round="20" 
              size="small" 
              form-type="submit" 
              block 
              label="立即登录"
              type="primary"
            ></tm-button>
            <view class="login-tips flex flex-between px-30 mt-30">
              <text class="text-size-n" @click="openLink('pages/login/forgot')">忘记密码</text>
              <text class="text-size-n" @click="openLink('pages/login/register')">没有账号?立即注册</text>
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
          <tm-button :margin="[10]" :shadow="0" :round="20" size="small" block label="立即登录" @click="toAuthLogin"></tm-button>
        </view>
      </view>
    </tm-drawer>
    <!-- #endif -->
  </tm-app>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { login, getCaptcha } from '@/common/index';
import { useAppStore } from '@/stores/app';
import { useUserStore } from '@/stores/user';
import { openLink } from '@/common/tools';
import { onLoad } from '@dcloudio/uni-app';
const appStore = useAppStore();
const userStore = useUserStore();
const captchaUrl = ref('');
const loginForm = ref({
  username: '',
  password: '',
  captcha: '',
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
  getCaptcha({ scene: 'login' }).then(res => {
    if (res.code == 1000) {
      captchaUrl.value = res.data;
    }
  });
}
captcha();
function confirm(e: any) {
  if (e.validate) {
    login({
      ...e.data,
      loginType: 'account',
    }).then(res => {
      console.log(res);
      if (res.code === 1000) {
        userStore.setUserInfo(res.data);
        uni.reLaunch({
          url: '/pages/index/index',
        });
      } else {
        captcha();
        uni.$tm.u.toast(res.message);
      }
    });
  }
}
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

  .logo {
    position: absolute;
    left: 50%;
    top: 170rpx;
    transform: translate(-50%, -50%);
    z-index: 100;
    box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.15);
    background-color: white;
    padding: 20rpx;
    border-radius: 50%;
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
      border-radius: 30rpx;
      box-shadow: 0 20rpx 50rpx rgba(0,0,0,0.15), 
                  inset 0 1px 0 rgba(255,255,255,0.5);
      padding: 50rpx 40rpx;
      margin-bottom: 50rpx;
      border: 1px solid rgba(230, 230, 230, 0.8);
      
      .title {
        margin-bottom: 60rpx;
        color: #222;
        font-weight: 600;
        letter-spacing: 1px;
      }
      
      .login-tips {
        color: #3c8af8;
        font-size: 26rpx;
        margin-top: 40rpx;
        display: flex;
        justify-content: space-between;
        
        text {
          transition: all 0.3s ease;
          &:hover {
            color: #2962ff;
            transform: translateY(-3rpx);
            text-decoration: underline;
          }
        }
      }
    }
}

.footer {
  padding-bottom: 20vh;
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

// 输入框聚焦效果
.tm-input {
  transition: all 0.3s ease;
  &:focus-within {
    box-shadow: 0 0 0 2rpx #3c8af8;
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
    width: 160rpx;
    height: 80rpx;
    border-radius: 10rpx;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      transform: scale(1.05);
    }
  }
}

// 社交登录图标样式
.social-icon {
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-5rpx);
    box-shadow: 0 5rpx 15rpx rgba(0,0,0,0.1);
  }
}

// 表单间距优化
.tm-form-item {
  margin-bottom: 30rpx;
}

// 响应式设计
@media (max-width: 768px) {
  .login {
    padding: 0 20rpx;
  }
  
  .box {
    padding: 30rpx 20rpx;
  }
  
  .title {
    font-size: 32rpx;
    margin-bottom: 40rpx;
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
}
</style>
