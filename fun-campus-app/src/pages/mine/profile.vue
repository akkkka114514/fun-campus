<template>
  <view class="profile-page">
    <view class="form-card">
      <!-- 头像 -->
      <view class="form-item" @click="chooseAvatar">
        <text class="form-label">头像</text>
        <view class="avatar-right">
          <image v-if="avatarPreview" class="avatar-img" :src="avatarPreview" mode="aspectFill" />
          <view v-else class="avatar-fallback">
            <text class="avatar-char">{{ firstChar }}</text>
          </view>
          <text class="form-arrow">></text>
        </view>
      </view>

      <!-- 昵称 -->
      <view class="form-item">
        <text class="form-label">昵称</text>
        <input
          v-model="form.nickname"
          class="form-input"
          placeholder="请输入昵称"
          :maxlength="50"
          placeholder-class="input-placeholder"
        />
      </view>

      <!-- 手机号 -->
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input
          v-model="form.phone"
          class="form-input"
          type="number"
          placeholder="请输入手机号"
          :maxlength="11"
          placeholder-class="input-placeholder"
        />
      </view>

      <!-- 性别 -->
      <view class="form-item">
        <text class="form-label">性别</text>
        <view class="gender-row">
          <view
            class="gender-chip"
            :class="{ 'gender-active': form.gender === true }"
            @click="form.gender = true"
          >
            男
          </view>
          <view
            class="gender-chip"
            :class="{ 'gender-active': form.gender === false }"
            @click="form.gender = false"
          >
            女
          </view>
        </view>
      </view>
    </view>

    <view class="save-btn" :class="{ 'save-disabled': saving }" @click="handleSave">
      {{ saving ? '保存中...' : '保存' }}
    </view>

    <view class="page-tip">昵称与头像将展示在「我的」页与活动评价等场景</view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useUserStore } from '@/store/user';
import { userApi } from '@/api/user-api';
import { uploadFile } from '@/utils/request';
import { getToken } from '@/utils/auth';
import { resolveFileUrl } from '@/utils/format';

const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo || {});

const form = reactive({
  nickname: '',
  avatar: null,
  phone: '',
  gender: null,
});

const uploading = ref(false);
const saving = ref(false);
const inited = ref(false);

const avatarPreview = computed(() => (form.avatar ? resolveFileUrl(form.avatar) : ''));

const firstChar = computed(() => {
  const name = form.nickname || userInfo.value.username || '';
  return name ? name.slice(0, 1).toUpperCase() : '?';
});

function fillForm() {
  const info = userInfo.value;
  form.nickname = info.nickname || '';
  form.avatar = info.avatar || null;
  form.phone = info.phone || '';
  form.gender = info.gender == null ? null : info.gender;
}

// 头像：选图 → 上传 → 记录 fileKey（保存时才提交资料）
function chooseAvatar() {
  if (uploading.value) {
    return;
  }
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const filePath = res.tempFilePaths && res.tempFilePaths[0];
      if (!filePath) {
        return;
      }
      uploading.value = true;
      uni.showLoading({ title: '上传中...' });
      try {
        const { data } = await uploadFile(filePath, {
          url: '/portal/file/upload',
          formData: { folder: 5 }, // FileFolderTypeEnum.AVATAR
        });
        form.avatar = data.fileKey;
        uni.showToast({ title: '头像已上传', icon: 'none' });
      } catch (e) {
        // 请求层已统一提示
      } finally {
        uploading.value = false;
        uni.hideLoading();
      }
    },
  });
}

async function handleSave() {
  if (saving.value || uploading.value) {
    return;
  }
  const nickname = (form.nickname || '').trim();
  if (!nickname) {
    uni.showToast({ title: '昵称不能为空', icon: 'none' });
    return;
  }
  const phone = (form.phone || '').trim();
  if (phone && !/^1\d{10}$/.test(phone)) {
    uni.showToast({ title: '手机号格式不正确', icon: 'none' });
    return;
  }

  saving.value = true;
  try {
    await userApi.updateProfile({
      nickname,
      avatar: form.avatar || undefined,
      phone: phone || undefined,
      gender: form.gender == null ? undefined : form.gender,
    });
    await userStore.fetchUserInfo(true);
    uni.showToast({ title: '保存成功', icon: 'success' });
    setTimeout(() => {
      uni.navigateBack();
    }, 600);
  } catch (e) {
    // 请求层已统一提示
  } finally {
    saving.value = false;
  }
}

onShow(async () => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  if (inited.value) {
    return;
  }
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo(true);
    } catch (e) {
      // 请求层已统一提示
    }
  }
  fillForm();
  inited.value = true;
});
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 24rpx;
  box-sizing: border-box;
}

.form-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 0 32rpx;
}

.form-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #f5f6f8;
}

.form-item:last-child {
  border-bottom: none;
}

.form-label {
  flex-shrink: 0;
  font-size: 30rpx;
  color: $fc-text-main;
  margin-right: 24rpx;
}

.form-input {
  flex: 1;
  font-size: 30rpx;
  color: $fc-text-main;
  text-align: right;
}

.input-placeholder {
  color: #c0c4cc;
}

.avatar-right {
  display: flex;
  align-items: center;
}

.avatar-img {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
}

.avatar-fallback {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: #e8eefc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-char {
  font-size: 40rpx;
  font-weight: 600;
  color: $fc-primary;
}

.form-arrow {
  margin-left: 16rpx;
  font-size: 28rpx;
  color: #c0c4cc;
}

.gender-row {
  display: flex;
}

.gender-chip {
  min-width: 120rpx;
  padding: 14rpx 0;
  text-align: center;
  border-radius: 32rpx;
  background: #f5f6f8;
  color: $fc-text-sub;
  font-size: 28rpx;
  margin-left: 16rpx;
}

.gender-active {
  background: #e8eefc;
  color: $fc-primary;
  font-weight: 600;
}

.save-btn {
  margin-top: 48rpx;
  background: $fc-primary;
  border-radius: 44rpx;
  text-align: center;
  padding: 26rpx 0;
  font-size: 32rpx;
  color: #ffffff;
}

.save-disabled {
  opacity: 0.6;
}

.page-tip {
  text-align: center;
  font-size: 24rpx;
  color: $fc-text-sub;
  padding: 24rpx 16rpx;
  line-height: 1.6;
}
</style>
