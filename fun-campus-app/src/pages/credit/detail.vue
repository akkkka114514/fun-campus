<template>
  <view class="detail-page">
    <!-- 状态横幅 -->
    <view class="status-banner" :class="bannerClass">
      <text class="status-name">{{ detail.statusName || statusText(detail.status) }}</text>
      <text v-if="detail.status === 0" class="status-desc">申请已提交，等待审核人处理</text>
      <text v-else-if="detail.status === 1" class="status-desc">审核已通过，实践分请以学分明细为准</text>
      <text v-else-if="detail.status === 2" class="status-desc">很遗憾，本次申请未通过</text>
    </view>

    <!-- 基本信息 -->
    <view class="info-card">
      <view class="info-row">
        <text class="info-label">标题</text>
        <text class="info-value">{{ detail.title }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">学期</text>
        <text class="info-value">{{ detail.semester }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">审核人院系/组织</text>
        <text class="info-value">{{ detail.reviewOrganizationName || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">审核人</text>
        <text class="info-value">{{ detail.reviewUserName || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">申请时间</text>
        <text class="info-value">{{ formatDateTime(detail.createTime) }}</text>
      </view>
      <view v-if="detail.status !== 0" class="info-row">
        <text class="info-label">审核时间</text>
        <text class="info-value">{{ formatDateTime(detail.reviewTime) }}</text>
      </view>
    </view>

    <!-- 驳回原因 -->
    <view v-if="detail.status === 2 && detail.reviewRemark" class="remark-card">
      <text class="remark-title">驳回原因</text>
      <text class="remark-content">{{ detail.reviewRemark }}</text>
    </view>

    <!-- 申请内容 -->
    <view class="section-card">
      <text class="section-title">申请内容</text>
      <text class="section-content">{{ detail.content || '（未填写）' }}</text>
    </view>

    <!-- 证明材料 -->
    <view class="section-card">
      <text class="section-title">证明材料</text>
      <view v-if="imageList.length" class="image-grid">
        <image
          v-for="(img, index) in imageList"
          :key="index"
          class="proof-img"
          :src="resolveFileUrl(img)"
          mode="aspectFill"
          @click="previewImage(index)"
        />
      </view>
      <text v-else class="section-content">（未上传证明材料）</text>
    </view>

    <!-- 待审核操作 -->
    <view v-if="detail.status === 0" class="action-bar">
      <view class="action-btn action-btn--edit" @click="goEdit">编辑</view>
      <view class="action-btn action-btn--delete" :class="{ 'action-btn--disabled': deleting }" @click="handleDelete">
        {{ deleting ? '删除中...' : '删除' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { creditApi } from '@/api/credit-api';
import { getToken } from '@/utils/auth';
import { resolveFileUrl, formatDateTime } from '@/utils/format';

const id = ref(null);
const detail = ref({});
const deleting = ref(false);
const inited = ref(false);

const imageList = computed(() => detail.value.imageList || []);

const bannerClass = computed(() => {
  if (detail.value.status === 1) {
    return 'status-banner--pass';
  }
  if (detail.value.status === 2) {
    return 'status-banner--reject';
  }
  return 'status-banner--wait';
});

onLoad((options) => {
  id.value = options && options.id ? Number(options.id) : null;
});

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  if (!id.value) {
    uni.showToast({ title: '参数错误', icon: 'none' });
    return;
  }
  // 每次回到本页（含编辑返回）都拉一次，保证状态最新
  loadDetail();
  inited.value = true;
});

async function loadDetail() {
  try {
    const { data } = await creditApi.detail(id.value);
    detail.value = data || {};
  } catch (e) {
    // 请求层已统一 toast
  }
}

function goEdit() {
  uni.navigateTo({ url: `/pages/credit/form?id=${id.value}` });
}

function handleDelete() {
  if (deleting.value) {
    return;
  }
  uni.showModal({
    title: '提示',
    content: '确定删除这条申请吗？删除后不可恢复',
    success: async (res) => {
      if (!res.confirm) {
        return;
      }
      deleting.value = true;
      try {
        await creditApi.remove(id.value);
        uni.showToast({ title: '已删除', icon: 'success' });
        setTimeout(() => {
          uni.navigateBack();
        }, 600);
      } catch (e) {
        // 请求层已统一 toast
      } finally {
        deleting.value = false;
      }
    },
  });
}

function previewImage(index) {
  uni.previewImage({
    urls: imageList.value.map((img) => resolveFileUrl(img)),
    current: index,
  });
}

function statusText(status) {
  if (status === 0) {
    return '待审核';
  }
  if (status === 1) {
    return '已通过';
  }
  if (status === 2) {
    return '已驳回';
  }
  return '未知';
}
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 24rpx 24rpx 160rpx;
  box-sizing: border-box;
}

.status-banner {
  border-radius: 20rpx;
  padding: 32rpx 28rpx;
  margin-bottom: 24rpx;
}

.status-banner--wait {
  background: #fff4e6;
}

.status-banner--pass {
  background: #e9f8ef;
}

.status-banner--reject {
  background: #fdeceb;
}

.status-name {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: $fc-text-main;
}

.status-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.info-card,
.section-card,
.remark-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 8rpx 28rpx;
  margin-bottom: 24rpx;
}

.info-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f6f8;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  flex-shrink: 0;
  font-size: 28rpx;
  color: $fc-text-sub;
  margin-right: 32rpx;
}

.info-value {
  flex: 1;
  text-align: right;
  font-size: 28rpx;
  color: $fc-text-main;
  word-break: break-all;
}

.remark-card {
  padding: 28rpx;
  background: #fdeceb;
}

.remark-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #fa3534;
}

.remark-content {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  color: $fc-text-main;
  line-height: 1.7;
}

.section-card {
  padding: 28rpx;
}

.section-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
  margin-bottom: 20rpx;
}

.section-content {
  display: block;
  font-size: 28rpx;
  color: $fc-text-main;
  line-height: 1.7;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
}

.proof-img {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  margin: 0 16rpx 16rpx 0;
  background: #eceef2;
}

.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
  background: #ffffff;
  box-sizing: border-box;
}

.action-btn {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  border-radius: 44rpx;
  font-size: 30rpx;
}

.action-btn--edit {
  background: #f5f6f8;
  color: $fc-text-main;
  margin-right: 20rpx;
}

.action-btn--delete {
  background: $fc-primary;
  color: #ffffff;
}

.action-btn--disabled {
  opacity: 0.6;
}
</style>
