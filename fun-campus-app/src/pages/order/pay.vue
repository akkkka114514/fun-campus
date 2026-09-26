<template>
  <view class="pay-page">
    <!-- 订单信息 -->
    <view class="card">
      <view class="card-title">订单信息</view>
      <view class="amount-box">
        <text class="amount-label">应付金额</text>
        <view class="amount-line">
          <text class="amount-symbol">¥</text>
          <text class="amount">{{ amountText }}</text>
        </view>
      </view>
      <view class="info-row">
        <text class="info-label">活动</text>
        <text class="info-value">{{ order.activityTitle || '--' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">订单号</text>
        <text class="info-value">{{ order.orderNo || orderNo }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">订单状态</text>
        <text class="info-value" :class="'status--' + order.status">{{ statusText }}</text>
      </view>
      <view class="info-row" v-if="isWaitPay">
        <text class="info-label">支付剩余时间</text>
        <text class="info-value" :class="{ 'countdown--over': remainSeconds <= 0 }">
          {{ remainSeconds > 0 ? countdownText : '已超时' }}
        </text>
      </view>
    </view>

    <!-- 提示 -->
    <view class="tips">
      支付结果由渠道异步通知，完成支付后请回到本页等待自动刷新；长时间未支付订单将自动关闭。
      <text class="refresh-link" v-if="!polling && isWaitPay" @click="onRefresh">刷新订单状态</text>
    </view>

    <!-- 轮询状态条 -->
    <view class="poll-bar" v-if="polling">
      <text>正在等待支付结果 ...</text>
    </view>

    <!-- 底部操作区 -->
    <view class="action-bar">
      <button class="btn btn--ghost" v-if="isWaitPay" :disabled="acting" @click="onCancelOrder">
        取消订单
      </button>
      <button class="btn btn--primary" :disabled="btnDisabled" @click="onMainBtn">
        {{ mainBtnText }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onLoad, onUnload } from '@dcloudio/uni-app';
import { orderApi } from '@/api/order-api';
import { API_BASE_URL } from '@/utils/request';
import { fenToYuan } from '@/utils/format';

const orderNo = ref('');
const activityId = ref('');
const order = ref({});
const acting = ref(false);
const polling = ref(false);
// 预取的收银台链接：点击「去支付」时同步打开，规避弹窗拦截
const cashierUrl = ref('');

// ===== 展示计算 =====
const ORDER_STATUS_TEXT = { 0: '待支付', 1: '已支付', 2: '已关闭', 3: '退款中', 4: '已退款', 5: '退款失败' };
const statusText = computed(() => ORDER_STATUS_TEXT[order.value.status] ?? '--');
const amountText = computed(() => fenToYuan(order.value.amountFen));
const isWaitPay = computed(() => order.value.status === 0);

const mainBtnText = computed(() => {
  if (isWaitPay.value) return '去支付';
  if (order.value.status === 1) return '已支付，返回活动';
  if (order.value.status === 2) return '订单已关闭，返回';
  return '返回';
});
const btnDisabled = computed(() => acting.value || !order.value.orderNo);

// ===== 支付倒计时（本地每秒递减） =====
const nowTs = ref(Date.now());
const expireTs = computed(() => {
  const t = order.value.expireTime;
  if (!t) return 0;
  const d = new Date(String(t).replace(' ', 'T'));
  return isNaN(d.getTime()) ? 0 : d.getTime();
});
const remainSeconds = computed(() => {
  if (!expireTs.value) return 0;
  return Math.max(0, Math.floor((expireTs.value - nowTs.value) / 1000));
});
const countdownText = computed(() => {
  const s = remainSeconds.value;
  const m = Math.floor(s / 60);
  const sec = s % 60;
  return `${String(m).padStart(2, '0')}:${String(sec).padStart(2, '0')}`;
});
let clockTimer = null;

// ===== 轮询回查（支付成功是渠道异步回调，收银台同步返回不可信） =====
const POLL_INTERVAL = 2500;
const POLL_MAX_COUNT = 24; // 约 60 秒
let pollTimer = null;
let pollCount = 0;

function startPolling() {
  stopPolling();
  polling.value = true;
  pollCount = 0;
  pollTimer = setInterval(async () => {
    pollCount += 1;
    if (pollCount > POLL_MAX_COUNT) {
      stopPolling();
      uni.showToast({ title: '暂未检测到支付结果，可稍后刷新', icon: 'none' });
      return;
    }
    await pollOnce();
  }, POLL_INTERVAL);
}

async function pollOnce() {
  try {
    const { data } = await orderApi.detail(orderNo.value);
    if (!data) return;
    order.value = data;
    if (data.status === 1) {
      stopPolling();
      uni.showToast({ title: '支付成功', icon: 'success' });
      setTimeout(goBack, 1200);
    } else if (data.status === 2 || data.status === 3 || data.status === 4) {
      stopPolling();
    }
  } catch (e) {
    // 轮询失败静默，等待下一次
  }
}

function stopPolling() {
  polling.value = false;
  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }
}

// ===== 订单加载 / 收银台拉起 =====
async function loadOrder() {
  try {
    const { data } = await orderApi.detail(orderNo.value);
    if (data) order.value = data;
  } catch (e) {
    // 错误提示由 request 层统一处理
  }
}

// 预取收银台链接：进入页面时静默获取，点击「去支付」时同步 window.open
async function prefetchCashier() {
  if (!isWaitPay.value) return;
  try {
    const { data } = await orderApi.prepay(orderNo.value);
    if (data?.cashierUrl) {
      cashierUrl.value = API_BASE_URL + data.cashierUrl;
    }
  } catch (e) {
    // 预取失败不打扰用户，点击「去支付」时兜底重新获取
  }
}

function openCashier(url) {
  // #ifdef H5
  window.open(url, '_blank');
  // #endif
  // #ifndef H5
  uni.navigateTo({ url: '/pages/order/cashier?url=' + encodeURIComponent(url) });
  // #endif
}

function doPay() {
  if (acting.value) return;
  if (cashierUrl.value) {
    // 已预取：同步打开，避免被浏览器当作弹窗拦截
    openCashier(cashierUrl.value);
    startPolling();
    return;
  }
  doPayFresh();
}

// 无缓存链接时：先同步开空白页（保住用户手势），拿到链接后再导航
async function doPayFresh() {
  acting.value = true;
  let win = null;
  try {
    // #ifdef H5
    win = window.open('', '_blank');
    // #endif
    const { data } = await orderApi.prepay(orderNo.value);
    if (data?.cashierUrl) {
      const fullUrl = API_BASE_URL + data.cashierUrl;
      // #ifdef H5
      if (win) {
        win.location.href = fullUrl;
      } else {
        window.open(fullUrl, '_blank');
      }
      // #endif
      // #ifndef H5
      uni.navigateTo({ url: '/pages/order/cashier?url=' + encodeURIComponent(fullUrl) });
      // #endif
      startPolling();
    }
  } catch (e) {
    // #ifdef H5
    if (win) win.close();
    // #endif
    // 订单超时被关闭等场景：刷新订单状态展示
    await loadOrder();
  } finally {
    acting.value = false;
  }
}

// ===== 按钮动作 =====
function onMainBtn() {
  if (isWaitPay.value) {
    doPay();
    return;
  }
  goBack();
}

function onCancelOrder() {
  if (acting.value) return;
  uni.showModal({
    title: '取消订单',
    content: '取消后名额将释放，确定取消这笔订单吗？',
    success: async (res) => {
      if (!res.confirm) return;
      acting.value = true;
      try {
        await orderApi.cancel(orderNo.value);
        stopPolling();
        uni.showToast({ title: '订单已取消', icon: 'none' });
        setTimeout(goBack, 800);
      } catch (e) {
        // 失败原因由 request 层 toast
      } finally {
        acting.value = false;
      }
    },
  });
}

async function onRefresh() {
  await loadOrder();
  uni.showToast({ title: '订单状态：' + statusText.value, icon: 'none' });
}

function goBack() {
  const pages = getCurrentPages();
  if (pages.length > 1) {
    uni.navigateBack();
  } else if (activityId.value) {
    uni.redirectTo({ url: '/pages/activity/detail?id=' + activityId.value });
  } else {
    uni.reLaunch({ url: '/pages/index/index' });
  }
}

// H5：用户从收银台切回本标签时立即查询一次
function onWindowFocus() {
  if (polling.value || isWaitPay.value) {
    loadOrder();
  }
}

onLoad((options) => {
  orderNo.value = options.orderNo || '';
  activityId.value = options.activityId || '';
  if (!orderNo.value) {
    uni.showToast({ title: '缺少订单号', icon: 'none' });
    setTimeout(goBack, 800);
    return;
  }
  loadOrder().then(prefetchCashier);
  clockTimer = setInterval(() => {
    nowTs.value = Date.now();
  }, 1000);
  // #ifdef H5
  window.addEventListener('focus', onWindowFocus);
  // #endif
});

onUnload(() => {
  stopPolling();
  if (clockTimer) {
    clearInterval(clockTimer);
    clockTimer = null;
  }
  // #ifdef H5
  window.removeEventListener('focus', onWindowFocus);
  // #endif
});
</script>

<style lang="scss" scoped>
.pay-page {
  min-height: 100vh;
  background: $fc-bg;
  padding-bottom: 200rpx;
  box-sizing: border-box;
}

.card {
  margin: 20rpx 24rpx 0;
  padding: 28rpx;
  background: $fc-card-bg;
  border-radius: 20rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
}

.amount-box {
  padding: 32rpx 0 20rpx;
  text-align: center;
}

.amount-label {
  font-size: 24rpx;
  color: $fc-text-sub;
}

.amount-line {
  margin-top: 8rpx;
  display: flex;
  align-items: baseline;
  justify-content: center;
  color: $fc-price;
}

.amount-symbol {
  font-size: 32rpx;
  font-weight: 600;
}

.amount {
  margin-left: 4rpx;
  font-size: 64rpx;
  font-weight: 700;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 18rpx 0;
  font-size: 26rpx;
}

.info-row + .info-row {
  border-top: 1rpx solid #f0f1f3;
}

.info-label {
  flex-shrink: 0;
  margin-right: 24rpx;
  color: $fc-text-sub;
}

.info-value {
  text-align: right;
  color: $fc-text-main;
  word-break: break-all;
}

.status--0 {
  color: $fc-price;
}

.status--1 {
  color: #07c160;
}

.status--2 {
  color: $fc-text-sub;
}

.countdown--over {
  color: #e64545;
}

.tips {
  margin: 20rpx 24rpx 0;
  padding: 20rpx 24rpx;
  background: rgba(60, 124, 255, 0.06);
  border-radius: 12rpx;
  font-size: 22rpx;
  color: $fc-text-sub;
  line-height: 1.7;
}

.refresh-link {
  margin-left: 12rpx;
  color: $fc-primary;
}

.poll-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 150rpx;
  text-align: center;
  font-size: 24rpx;
  color: $fc-primary;
}

.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: $fc-card-bg;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.btn {
  flex: 1;
  height: 84rpx;
  line-height: 84rpx;
  border-radius: 42rpx;
  font-size: 30rpx;
  font-weight: 600;
  padding: 0;
}

.btn::after {
  border: none;
}

.btn--ghost {
  margin-right: 16rpx;
  background: #ffffff;
  border: 2rpx solid #dcdfe6;
  color: #606266;
}

.btn--primary {
  background: $fc-primary;
  color: #ffffff;
}

.btn--primary[disabled] {
  background: #c8c9cc;
  color: #ffffff;
}
</style>
