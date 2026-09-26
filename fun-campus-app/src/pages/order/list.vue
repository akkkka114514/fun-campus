<template>
  <view class="order-page">
    <!-- 状态筛选 Tab -->
    <view class="order-tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="order-tab"
        :class="{ 'order-tab--active': activeTab.key === tab.key }"
        @click="switchTab(tab)"
      >
        {{ tab.text }}
      </view>
    </view>

    <!-- 订单列表 -->
    <view v-if="orderList.length" class="order-list">
      <view
        v-for="order in orderList"
        :key="order.orderNo"
        class="order-card"
        @click="goActivity(order)"
      >
        <view class="order-head">
          <text class="order-title">{{ order.activityTitle || '活动报名' }}</text>
          <text class="order-status" :class="'status--' + order.status">
            {{ statusText(order.status) }}
          </text>
        </view>

        <view class="order-row">
          <text class="order-label">订单号</text>
          <text class="order-value">{{ order.orderNo }}</text>
        </view>
        <view class="order-row">
          <text class="order-label">下单时间</text>
          <text class="order-value">{{ formatDateTime(order.createTime) }}</text>
        </view>
        <view class="order-row" v-if="order.status === 0 && order.expireTime">
          <text class="order-label">支付截止</text>
          <text class="order-value">{{ formatDateTime(order.expireTime) }}</text>
        </view>
        <view class="order-row" v-if="order.payTime">
          <text class="order-label">支付时间</text>
          <text class="order-value">{{ formatDateTime(order.payTime) }}</text>
        </view>

        <view class="order-foot">
          <view class="order-amount">
            <text class="amount-symbol">¥</text>
            <text class="amount">{{ fenToYuan(order.amountFen) }}</text>
          </view>
          <view class="order-actions">
            <button
              v-if="order.status === 0"
              class="mini-btn mini-btn--ghost"
              @click.stop="onCancel(order)"
            >
              取消订单
            </button>
            <button
              v-if="order.status === 0"
              class="mini-btn mini-btn--primary"
              @click.stop="goPay(order)"
            >
              去支付
            </button>
            <button
              v-if="order.status === 1"
              class="mini-btn mini-btn--danger"
              @click.stop="onRefund(order)"
            >
              申请退款
            </button>
            <button
              v-if="order.status === 5"
              class="mini-btn mini-btn--danger"
              @click.stop="onRefund(order)"
            >
              重新申请退款
            </button>
          </view>
        </view>

        <view class="order-tip" v-if="order.status === 3">
          退款处理中，到账后将自动更新为「已退款」
        </view>
        <view class="order-tip order-tip--muted" v-if="order.status === 4">
          退款已原路退回，如有疑问请联系活动负责人
        </view>
        <view class="order-tip order-tip--danger" v-if="order.status === 5">
          退款未成功，可重新申请退款
        </view>
      </view>

      <view class="load-status">
        <text v-if="loadStatus === 'loading'">加载中...</text>
        <text v-else-if="loadStatus === 'nomore'">没有更多了</text>
      </view>
    </view>

    <!-- 空态 -->
    <view v-else class="empty">
      <text class="empty-text">{{ loading ? '加载中...' : '暂无订单' }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { orderApi } from '@/api/order-api';
import { getToken } from '@/utils/auth';
import { formatDateTime, fenToYuan } from '@/utils/format';

const PAGE_SIZE = 10;

// 订单状态：0-待支付 1-已支付 2-已关闭 3-退款中 4-已退款 5-退款失败
const ORDER_STATUS_TEXT = {
  0: '待支付',
  1: '已支付',
  2: '已关闭',
  3: '退款中',
  4: '已退款',
  5: '退款失败',
};

const tabs = [
  { key: 'all', text: '全部', status: null },
  { key: 'waitPay', text: '待支付', status: 0 },
  { key: 'paid', text: '已支付', status: 1 },
  { key: 'refunded', text: '已退款', status: 4 },
];

const activeTab = ref(tabs[0]);
const orderList = ref([]);
const pageNum = ref(1);
const total = ref(0);
const loading = ref(false);
// loadmore-可继续加载 loading-加载中 nomore-没有更多
const loadStatus = ref('loadmore');

// 请求序号：并发/快速切 Tab 时丢弃过期响应
let requestSeq = 0;

function statusText(status) {
  return ORDER_STATUS_TEXT[status] ?? '--';
}

onShow(() => {
  if (!getToken()) {
    uni.reLaunch({ url: '/pages/login/login' });
    return;
  }
  // 每次进入刷新（支付/退款后状态可能已变化）
  loadData(true);
});

onPullDownRefresh(async () => {
  await loadData(true);
  uni.stopPullDownRefresh();
});

onReachBottom(() => {
  if (loadStatus.value === 'loadmore' && !loading.value) {
    loadData(false);
  }
});

async function loadData(reset) {
  if (loading.value && !reset) {
    return;
  }
  const seq = ++requestSeq;
  loading.value = true;
  if (reset) {
    pageNum.value = 1;
  }
  loadStatus.value = 'loading';
  try {
    const form = {
      pageNum: pageNum.value,
      pageSize: PAGE_SIZE,
      searchCount: true,
    };
    if (activeTab.value.status != null) {
      form.status = activeTab.value.status;
    }
    const { data } = await orderApi.queryMyOrders(form);
    if (seq !== requestSeq) {
      return;
    }
    const records = (data && data.list) || [];
    total.value = (data && data.total) || 0;
    orderList.value = reset ? records : orderList.value.concat(records);
    if (records.length < PAGE_SIZE || orderList.value.length >= total.value) {
      loadStatus.value = 'nomore';
    } else {
      loadStatus.value = 'loadmore';
      pageNum.value += 1;
    }
  } catch (e) {
    if (seq === requestSeq) {
      // 请求层已统一 toast
      loadStatus.value = 'loadmore';
    }
  } finally {
    if (seq === requestSeq) {
      loading.value = false;
    }
  }
}

function switchTab(tab) {
  if (activeTab.value.key === tab.key) {
    return;
  }
  activeTab.value = tab;
  orderList.value = [];
  total.value = 0;
  loadStatus.value = 'loadmore';
  loadData(true);
}

// 点击卡片：跳活动详情
function goActivity(order) {
  if (!order.activityId) {
    return;
  }
  uni.navigateTo({ url: '/pages/activity/detail?id=' + order.activityId });
}

// 待支付 → 继续支付（pay 页自动预取收银台链接）
function goPay(order) {
  uni.navigateTo({
    url: '/pages/order/pay?orderNo=' + encodeURIComponent(order.orderNo) + '&activityId=' + (order.activityId || ''),
  });
}

// 取消待支付订单（名额释放，后端 CAS 幂等）
function onCancel(order) {
  uni.showModal({
    title: '取消订单',
    content: '取消后报名名额将释放，确定取消这笔订单吗？',
    success: async (res) => {
      if (!res.confirm) return;
      try {
        await orderApi.cancel(order.orderNo);
        uni.showToast({ title: '订单已取消', icon: 'none' });
        loadData(true);
      } catch (e) {
        // 失败原因由 request 层 toast
      }
    },
  });
}

// 申请退款：可填原因（不填则默认"用户申请退款"），到账以渠道回调为准
function onRefund(order) {
  const amountText = fenToYuan(order.amountFen);
  uni.showModal({
    title: '申请退款',
    editable: true,
    placeholderText: '退款原因（可选）',
    content: `将按活动退款政策原路退回 ¥${amountText}，到账后自动释放报名名额。`,
    success: async (res) => {
      if (!res.confirm) return;
      try {
        await orderApi.refundApply(order.orderNo, res.content);
        uni.showToast({ title: '退款申请已提交', icon: 'none' });
        loadData(true);
      } catch (e) {
        // 不可退（政策限制/状态已变化）由 request 层 toast 后端消息
      }
    },
  });
}
</script>

<style lang="scss" scoped>
.order-page {
  min-height: 100vh;
  background: $fc-bg;
}

.order-tabs {
  display: flex;
  background: #ffffff;
  padding: 0 24rpx;
  position: sticky;
  top: 0;
  z-index: 10;
}

.order-tab {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 30rpx;
  color: $fc-text-sub;
  position: relative;
}

.order-tab--active {
  color: $fc-text-main;
  font-weight: 600;
}

.order-tab--active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 8rpx;
  transform: translateX(-50%);
  width: 48rpx;
  height: 6rpx;
  border-radius: 6rpx;
  background: $fc-primary;
}

.order-list {
  padding: 24rpx;
}

.order-card {
  background: $fc-card-bg;
  border-radius: 20rpx;
  padding: 28rpx 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.order-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.order-title {
  flex: 1;
  margin-right: 16rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-status {
  flex-shrink: 0;
  font-size: 26rpx;
  font-weight: 600;
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

.status--3 {
  color: $fc-primary;
}

.status--4 {
  color: #8a5cf6;
}

.status--5 {
  color: #e64545;
}

.order-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-top: 14rpx;
  font-size: 24rpx;
}

.order-label {
  flex-shrink: 0;
  margin-right: 24rpx;
  color: $fc-text-sub;
}

.order-value {
  text-align: right;
  color: #606266;
  word-break: break-all;
}

.order-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #f0f1f3;
}

.order-amount {
  display: flex;
  align-items: baseline;
  color: $fc-price;
}

.amount-symbol {
  font-size: 26rpx;
  font-weight: 600;
}

.amount {
  margin-left: 4rpx;
  font-size: 40rpx;
  font-weight: 700;
}

.order-actions {
  display: flex;
  align-items: center;
}

.mini-btn {
  margin: 0 0 0 16rpx;
  padding: 0 32rpx;
  height: 60rpx;
  line-height: 60rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  font-weight: 500;
}

.mini-btn::after {
  border: none;
}

.mini-btn--ghost {
  background: #ffffff;
  border: 2rpx solid #dcdfe6;
  color: #606266;
}

.mini-btn--primary {
  background: $fc-primary;
  color: #ffffff;
}

.mini-btn--danger {
  background: #ffffff;
  border: 2rpx solid #f0a0a0;
  color: #e64545;
}

.order-tip {
  margin-top: 20rpx;
  padding: 16rpx 20rpx;
  border-radius: 12rpx;
  background: rgba(60, 124, 255, 0.06);
  font-size: 22rpx;
  color: $fc-primary;
}

.order-tip--muted {
  background: #f5f6f8;
  color: $fc-text-sub;
}

.order-tip--danger {
  background: rgba(230, 69, 69, 0.06);
  color: #e64545;
}

.load-status {
  text-align: center;
  padding: 24rpx 0;
  font-size: 24rpx;
  color: $fc-text-sub;
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
