<template>
  <view class="notice-page">
    <template v-if="detail.title">
      <!-- 标题与元信息 -->
      <view class="head-card">
        <text class="detail-title">{{ detail.title }}</text>
        <view class="meta-row">
          <text v-if="detail.noticeTypeName" class="meta-tag">{{ detail.noticeTypeName }}</text>
          <text class="meta-text">{{ formatDateTime(detail.publishTime) }}</text>
          <text class="meta-text">{{ detail.pageViewCount || 0 }} 浏览</text>
        </view>
        <view v-if="detail.source || detail.author || detail.documentNumber" class="meta-row">
          <text v-if="detail.source" class="meta-text">来源：{{ detail.source }}</text>
          <text v-if="detail.author" class="meta-text">作者：{{ detail.author }}</text>
          <text v-if="detail.documentNumber" class="meta-text">文号：{{ detail.documentNumber }}</text>
        </view>
      </view>

      <!-- 正文（优先 html 渲染，兼容纯文本） -->
      <view class="body-card">
        <rich-text v-if="detail.contentHtml" class="content-html" :nodes="detail.contentHtml" />
        <text v-else class="content-text">{{ detail.contentText || '（暂无内容）' }}</text>
      </view>

      <!-- 附件（多个英文逗号分隔） -->
      <view v-if="attachmentList.length" class="attach-card">
        <text class="attach-title">附件</text>
        <view
          v-for="(file, index) in attachmentList"
          :key="index"
          class="attach-item"
          @click="copyAttachment(file)"
        >
          <u-icon name="attach" color="#3c7cff" size="16" />
          <text class="attach-name">{{ fileName(file) }}</text>
          <text class="attach-copy">复制链接</text>
        </view>
      </view>
    </template>

    <!-- 空态/加载态 -->
    <view v-else class="empty">
      <text class="empty-text">{{ loading ? '加载中...' : '公告不存在' }}</text>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { noticeApi } from '@/api/notice-api';
import { getToken } from '@/utils/auth';
import { resolveFileUrl, formatDateTime } from '@/utils/format';

const noticeId = ref(null);
const detail = ref({});
const loading = ref(true);

// 只加载一次：详情接口会累加页面浏览量，避免 onShow 重复进入重复计数
let loaded = false;

const attachmentList = computed(() => {
  const raw = detail.value.attachment || '';
  return raw
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean);
});

onLoad((options) => {
  noticeId.value = options && options.noticeId ? Number(options.noticeId) : null;
});

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  if (!noticeId.value) {
    loading.value = false;
    uni.showToast({ title: '参数错误', icon: 'none' });
    return;
  }
  if (!loaded) {
    loaded = true;
    loadDetail();
  }
});

async function loadDetail() {
  try {
    const { data } = await noticeApi.detail(noticeId.value);
    detail.value = data || {};
  } catch (e) {
    // 请求层已统一 toast（公告不存在/已下架等）
  } finally {
    loading.value = false;
  }
}

function fileName(fileKey) {
  const idx = fileKey.lastIndexOf('/');
  return idx >= 0 ? fileKey.slice(idx + 1) : fileKey;
}

function copyAttachment(fileKey) {
  uni.setClipboardData({
    data: resolveFileUrl(fileKey),
    success: () => uni.showToast({ title: '附件链接已复制', icon: 'none' }),
  });
}
</script>

<style lang="scss" scoped>
.notice-page {
  min-height: 100vh;
  background: $fc-bg;
  padding: 24rpx;
  box-sizing: border-box;
}

.head-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.detail-title {
  font-size: 38rpx;
  font-weight: 600;
  line-height: 54rpx;
  color: $fc-text-main;
  word-break: break-all;
}

.meta-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-top: 16rpx;
}

.meta-tag {
  margin-right: 16rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  color: $fc-primary;
  background: rgba(60, 124, 255, 0.1);
}

.meta-text {
  margin-right: 24rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.body-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.content-html {
  font-size: 28rpx;
  line-height: 46rpx;
  color: $fc-text-main;
  word-break: break-all;
}

.content-text {
  font-size: 28rpx;
  line-height: 46rpx;
  color: $fc-text-main;
  white-space: pre-wrap;
  word-break: break-all;
}

.attach-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.attach-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
  margin-bottom: 16rpx;
}

.attach-item {
  display: flex;
  align-items: center;
  margin-top: 16rpx;
  padding: 20rpx 24rpx;
  background: #f7f9fc;
  border-radius: 12rpx;
}

.attach-name {
  flex: 1;
  margin: 0 16rpx;
  font-size: 26rpx;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attach-copy {
  flex-shrink: 0;
  font-size: 24rpx;
  color: $fc-primary;
}

.empty {
  padding-top: 240rpx;
  display: flex;
  justify-content: center;
}

.empty-text {
  font-size: 28rpx;
  color: $fc-text-sub;
}
</style>
