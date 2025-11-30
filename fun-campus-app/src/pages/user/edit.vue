<template>
  <tm-app color="white">
    <view class="edit-profile-container">
      <tm-form @submit="confirm" :margin="[0, 0]" ref="form" v-model="editForm" :label-width="90">
        <view class="section-title">基本信息</view>
        <tm-form-item label="头像" field="cale">
          <dx-upload v-model="editForm.avatar"></dx-upload>
        </tm-form-item>
        <tm-form-item label="昵称" field="nickname">
          <tm-input 
            :inputPadding="[20, 0]" 
            :round="20" 
            v-model.lazy="editForm.nickname" 
            placeholder="修改昵称,留空不修改" 
            :showBottomBotder="false"
            :border="1"
            :color="inputBorderColor">
          </tm-input>
        </tm-form-item>
        
        <view class="info-item">
          <text class="info-label">真实姓名</text>
          <text class="info-value">{{ userStore.userInfo.realname || '未设置' }}</text>
        </view>
        <tm-form-item label="性别" field="gender">
          <tm-radio-group v-model="editForm.gender">
            <tm-radio :value="1" label="男" :color="radioColor"></tm-radio>
            <tm-radio :value="2" label="女" :color="radioColor"></tm-radio>
          </tm-radio-group>
        </tm-form-item>
        <view class="info-item">
          <text class="info-label">学号</text>
          <text class="info-value">{{ userStore.userInfo.studentId || '未设置' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">政治面貌</text>
          <text class="info-value">{{ userStore.userInfo.politicalStatus || '未设置' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">年制</text>
          <text class="info-value">{{ userStore.userInfo.educationLength || '未设置' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">院系</text>
          <text class="info-value">{{ userStore.userInfo.department || '未设置' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">班级</text>
          <text class="info-value">{{ userStore.userInfo.class || '未设置' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">专业</text>
          <text class="info-value">{{ userStore.userInfo.major || '未设置' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">入学年份</text>
          <text class="info-value">{{ userStore.userInfo.enrollmentYear || '未设置' }}</text>
        </view>

        <tm-form-item :border="false">
          <tm-button 
            :margin="[10]" 
            :shadow="3" 
            :round="20" 
            size="small" 
            form-type="submit" 
            block 
            label="保存信息"
            color="orange">
          </tm-button>
        </tm-form-item>
      </tm-form>
    </view>
  </tm-app>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { editUserInfo } from '@/common/index';
import { onMounted } from 'vue';

// 使用 tmicon 中的一个图标名称作为字符串
const editIcon = "tmicon-edit";

const userStore = useUserStore();
const editForm = ref({
  nickname: userStore.userInfo.nickname,
  avatar: userStore.userInfo.avatar,
  // mobile: userStore.userInfo.mobile,
  email:  userStore.userInfo.email,
  gender:  userStore.userInfo.gender,
  password: '',
});

// 检查用户是否已登录
onMounted(() => {
  if (!userStore.isLogin) {
    // 用户未登录，跳转到登录页面
    uni.redirectTo({
      url: '/pages/login/login'
    });
  }
});

function confirm(e:any) {
  editUserInfo(e.data).then((res) => {
    if(res.code === 1000){
      userStore.setUserInfo(res.data);
      uni.$tm.u.toast('修改成功');
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }else{
      uni.$tm.u.toast(res.message);
    }
  });
}
// 主题色
const primaryColor = "#ff8c42";
const inputBorderColor = "#ffccbc";
const radioColor = "#ff8c42";

</script>

<style scoped lang="scss">
.edit-profile-container {
  padding: 30rpx;
  background: linear-gradient(180deg, #fff9f2 0%, #ffffff 100%);
  min-height: 100vh;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #ff8c42;
  padding: 20rpx 0;
  margin-top: 20rpx;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0;
    width: 60rpx;
    height: 6rpx;
    background: linear-gradient(90deg, #ff8c42 0%, #ff6b35 100%);
    border-radius: 3rpx;
  }
}

:deep(.tm-form-item) {
  margin-bottom: 30rpx;
  
  .tm-form-item__label {
    color: #333333;
    font-weight: 500;
    font-size: 28rpx;
  }
}

:deep(.tm-button) {
  background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 100%);
  border: none;
  box-shadow: 0 10rpx 20rpx rgba(255, 140, 66, 0.3);
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 4rpx 10rpx rgba(255, 140, 66, 0.3);
  }
}

:deep(.tm-input) {
  background-color: #ffffff;
  border: 1rpx solid #ffccbc;
  
  &.tm-input--focus {
    border-color: #ff8c42;
    box-shadow: 0 0 10rpx rgba(255, 140, 66, 0.3);
  }
}

:deep(.tm-radio) {
  margin-right: 30rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
  
  .info-label {
    font-size: 28rpx;
    color: #333333;
    font-weight: 500;
  }
  
  .info-value {
    font-size: 26rpx;
    color: #666666;
  }
}
</style>