<template>
  <view class="detail-page">
    <!-- 封面 -->
    <view class="cover">
      <image
        v-if="activity.coverImg"
        class="cover-img"
        :src="resolveFileUrl(activity.coverImg)"
        mode="aspectFill"
      />
      <view v-else class="cover-placeholder">
        <text class="cover-placeholder-text">{{ activity.title || '活动详情' }}</text>
      </view>
    </view>

    <template v-if="detail">
      <!-- 基本信息卡 -->
      <view class="card info-card">
        <view class="title-row">
          <text class="title">{{ activity.title || '' }}</text>
          <view class="status-tag" :class="statusClass">{{ statusText }}</view>
        </view>

        <view class="tag-row">
          <view v-if="activity.paidFlag" class="tag tag--price">¥{{ fenToYuan(activity.priceFen) }}</view>
          <view v-else class="tag tag--free">免费</view>
          <view v-if="scoreText" class="tag tag--score">{{ scoreText }}</view>
          <view v-if="activity.enrollNeedReview" class="tag">需审核</view>
          <view v-if="activity.needSignOut" class="tag">需签退</view>
          <view v-if="activity.categoryName" class="tag">{{ activity.categoryName }}</view>
        </view>

        <view class="info-rows">
          <view class="info-row">
            <text class="info-label">活动时间</text>
            <text class="info-value">{{ formatTimeRange(schedule.activityStartTime, schedule.activityEndTime) || '待定' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">报名时间</text>
            <text class="info-value">{{ formatTimeRange(schedule.enrollStartTime, schedule.enrollEndTime) || '待定' }}</text>
          </view>
          <view v-if="activity.position" class="info-row">
            <text class="info-label">活动地点</text>
            <text class="info-value">{{ activity.position }}</text>
          </view>
          <view v-if="orgText" class="info-row">
            <text class="info-label">主办方</text>
            <text class="info-value">{{ orgText }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">报名范围</text>
            <text class="info-value">{{ enrollScopeText }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">报名人数</text>
            <text class="info-value">
              {{ enrollNumText }} 人
              <text v-if="signInNum > 0" class="info-sub">（已签到 {{ signInNum }} 人）</text>
            </text>
          </view>
        </view>
      </view>

      <!-- 报名同学 -->
      <view v-if="enrollUsers.length" class="card">
        <view class="card-title">报名同学（{{ enrollNum }}）</view>
        <view class="avatar-wall">
          <view v-for="u in enrollUsers.slice(0, 12)" :key="u.id" class="avatar-item">
            <image v-if="u.avatarKey" class="avatar-img" :src="resolveFileUrl(u.avatarKey)" mode="aspectFill" />
            <view v-else class="avatar-fallback" :style="{ background: avatarColor(u.id) }">
              {{ (u.name || '?').slice(0, 1) }}
            </view>
            <text class="avatar-name">{{ u.name }}</text>
          </view>
        </view>
        <text v-if="enrollNum > 12" class="avatar-more">等 {{ enrollNum }} 人已报名</text>
      </view>

      <!-- 活动介绍 -->
      <view class="card">
        <view class="card-title">活动介绍</view>
        <text class="desc">{{ activity.description || '暂无介绍' }}</text>
        <view v-if="activity.attachment" class="attachment" @click="copyAttachment">
          <u-icon name="attach" color="#3c7cff" size="16" />
          <text class="attachment-name">{{ attachmentName }}</text>
          <text class="attachment-copy">复制链接</text>
        </view>
      </view>

      <!-- 评论区 -->
      <view class="card">
        <view class="card-title">评论（{{ commentTotal }}）</view>

        <view class="comment-form">
          <input
            v-model="commentContent"
            class="comment-input"
            :placeholder="replyTo ? '回复 用户' + replyTo.userId : '说点什么...'"
            :maxlength="500"
            confirm-type="send"
            @confirm="submitComment"
          />
          <view v-if="replyTo" class="reply-cancel" @click="replyTo = null">取消</view>
          <view class="send-btn" :class="{ 'send-btn--disabled': sending }" @click="submitComment">
            {{ sending ? '发送中' : '发送' }}
          </view>
        </view>

        <view v-if="commentLoaded && !comments.length" class="comment-empty">暂无评论，快来抢沙发</view>

        <view v-for="c in comments" :key="c.id" class="comment-item">
          <view class="comment-avatar">
            <u-icon name="account" color="#ffffff" size="18" />
          </view>
          <view class="comment-main">
            <view class="comment-head">
              <text class="comment-name">用户{{ c.userId }}</text>
              <text class="comment-time">{{ commentTime(c.createTime) }}</text>
            </view>
            <text class="comment-content">{{ c.content }}</text>
            <view class="comment-ops">
              <view class="comment-op" :class="{ 'comment-op--active': likedIds[c.id] }" @click="toggleLike(c)">
                <u-icon :name="likedIds[c.id] ? 'thumb-up-fill' : 'thumb-up'" :color="likedIds[c.id] ? '#3c7cff' : '#909399'" size="16" />
                <text class="comment-op-text">{{ c.commentHot || 0 }}</text>
              </view>
              <view class="comment-op" @click="startReply(c)"><text class="comment-op-text">回复</text></view>
              <view v-if="myUserId && c.userId === myUserId" class="comment-op" @click="removeComment(c)">
                <text class="comment-op-text">删除</text>
              </view>
            </view>

            <view v-if="c.children && c.children.length" class="children">
              <view v-for="child in c.children" :key="child.id" class="comment-item comment-item--child">
                <view class="comment-main">
                  <view class="comment-head">
                    <text class="comment-name">用户{{ child.userId }}</text>
                    <text class="comment-time">{{ commentTime(child.createTime) }}</text>
                  </view>
                  <text class="comment-content">{{ child.content }}</text>
                  <view class="comment-ops">
                    <view class="comment-op" :class="{ 'comment-op--active': likedIds[child.id] }" @click="toggleLike(child)">
                      <u-icon :name="likedIds[child.id] ? 'thumb-up-fill' : 'thumb-up'" :color="likedIds[child.id] ? '#3c7cff' : '#909399'" size="16" />
                      <text class="comment-op-text">{{ child.commentHot || 0 }}</text>
                    </view>
                    <view class="comment-op" @click="startReply(child)"><text class="comment-op-text">回复</text></view>
                    <view v-if="myUserId && child.userId === myUserId" class="comment-op" @click="removeComment(child)">
                      <text class="comment-op-text">删除</text>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view v-if="comments.length < commentTotal" class="load-more" @click="loadComments(false)">
          {{ commentLoading ? '加载中...' : '加载更多' }}
        </view>
      </view>
    </template>

    <!-- 底部操作栏 -->
    <view class="action-bar">
      <view class="action-icon" @click="toggleFavorite">
        <u-icon :name="favorited ? 'star-fill' : 'star'" :color="favorited ? '#ff6a3d' : '#909399'" size="24" />
        <text class="action-icon-text" :class="{ 'action-icon-text--active': favorited }">{{ favorited ? '已收藏' : '收藏' }}</text>
      </view>
      <view class="action-icon" @click="shareActivity">
        <u-icon name="share" color="#909399" size="24" />
        <text class="action-icon-text">分享</text>
      </view>
      <view
        class="main-btn"
        :class="{
          'main-btn--disabled': mainAction.disabled || acting,
          'main-btn--cancel': mainAction.action === 'cancel',
        }"
        @click="onMainAction"
      >
        <text>{{ acting ? '处理中...' : mainAction.text }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue';
import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { activityApi } from '@/api/activity-api';
import { orderApi } from '@/api/order-api';
import { useUserStore } from '@/store/user';
import { resolveFileUrl, formatDateTime, formatTimeRange, fenToYuan } from '@/utils/format';

const userStore = useUserStore();

const activityId = ref('');
const detail = ref(null);

// ===== 派生数据 =====
const activity = computed(() => detail.value?.activity || {});
const schedule = computed(() => detail.value?.schedule || {});
const enrollUsers = computed(() => detail.value?.enrollUsers || []);
const enrollNum = computed(() => detail.value?.enrollNum || 0);
const signInNum = computed(() => detail.value?.signInNum || 0);
const myEnroll = computed(() => detail.value?.currentUserEnrollment || null);
const myUserId = computed(() => userStore.userInfo?.id || null);

const STATUS_TEXT = { 0: '等待报名', 1: '报名中', 2: '报名结束', 3: '进行中', 4: '已结束', 9: '待审核' };
const statusText = computed(() => STATUS_TEXT[activity.value.status] || '未知');
const statusClass = computed(() => 'status-tag--' + (STATUS_TEXT[activity.value.status] ? activity.value.status : 'x'));

const scoreText = computed(() => {
  const s = activity.value.scoreCanGet;
  return s && Number(s) > 0 ? `学分 +${s}` : '';
});

const orgText = computed(() => {
  const a = activity.value;
  return [a.activityBelongToOrganizationName, a.activityBelongToCollegeName].filter(Boolean).join(' · ');
});

const enrollNumText = computed(() => {
  const limit = activity.value.enrollNumLimit;
  return limit ? `${enrollNum.value} / ${limit}` : `${enrollNum.value}`;
});

// 报名范围：后端空数组 = 不限
const enrollScopeText = computed(() => {
  const d = detail.value || {};
  const college = (d.canEnrollCollege || []).map((e) => e.name).filter(Boolean);
  const grade = (d.canEnrollGrade || []).map((e) => e.name).filter(Boolean);
  const tribe = (d.canEnrollTribe || []).map((e) => e.name).filter(Boolean);
  if (!college.length && !grade.length && !tribe.length) {
    return '不限';
  }
  return [college.join('、'), grade.join('、'), tribe.join('、')].filter(Boolean).join(' / ');
});

const attachmentName = computed(() => {
  const a = activity.value.attachment || '';
  const idx = a.lastIndexOf('/');
  return idx >= 0 ? a.slice(idx + 1) : a;
});

// ===== 报名窗口 & 主按钮状态机 =====
function nowIsoStr() {
  const d = new Date();
  const p = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`;
}

// 报名截止时间是否未到（免费活动取消报名的后端规则：now < enrollEndTime）
const beforeEnrollEnd = computed(() => {
  const s = schedule.value;
  if (!s || !s.enrollEndTime) return true;
  return nowIsoStr() < s.enrollEndTime;
});

const mainAction = computed(() => {
  const a = activity.value;
  if (!detail.value || !a.id) return { text: '加载中...', disabled: true, action: '' };
  const enr = myEnroll.value;
  if (enr && enr.enrolled) {
    if (enr.signOutStatus) return { text: '已完成', disabled: true, action: '' };
    if (enr.signInStatus) return { text: '已签到', disabled: true, action: '' };
    if (a.paidFlag) return { text: '已报名', disabled: true, action: '' };
    if (beforeEnrollEnd.value) return { text: '取消报名', disabled: false, action: 'cancel' };
    return { text: '已报名', disabled: true, action: '' };
  }
  // 未报名：以后端活动状态为准（后端报名仅校验 status=报名中）
  const st = a.status;
  if (st === 4) return { text: '活动已结束', disabled: true, action: '' };
  if (st === 0) return { text: '报名未开始', disabled: true, action: '' };
  if (st === 2 || st === 3) return { text: '报名已结束', disabled: true, action: '' };
  if (st === 9) return { text: '待审核', disabled: true, action: '' };
  if (a.paidFlag) {
    const order = detail.value?.currentUserOrder;
    if (order && order.status === 0) {
      // 已有待支付订单：继续支付
      return { text: `待支付 ¥${fenToYuan(a.priceFen)}`, disabled: false, action: 'pay' };
    }
    if (order && order.status === 3) return { text: '退款中', disabled: true, action: '' };
    return { text: `立即报名 ¥${fenToYuan(a.priceFen)}`, disabled: false, action: 'pay' };
  }
  return { text: '立即报名', disabled: false, action: 'enroll' };
});

// ===== 详情 / 收藏 =====
const favorited = ref(false);
const acting = ref(false);
const favoriting = ref(false);
const shareLoading = ref(false);

async function loadDetail() {
  if (!activityId.value) return;
  try {
    const { data } = await activityApi.detail(activityId.value);
    detail.value = data;
  } catch (e) {
    // 错误提示由 request 层统一处理
  }
}

async function loadFavorite() {
  if (!userStore.isLogin || !activityId.value) return;
  try {
    const { data } = await activityApi.checkFavorite(activityId.value);
    favorited.value = !!data;
  } catch (e) {
    // 忽略：未登录或接口失败不阻塞页面
  }
}

function requireLogin() {
  if (userStore.isLogin) return true;
  uni.showToast({ title: '请先登录', icon: 'none' });
  setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 300);
  return false;
}

// ===== 主按钮：报名 / 取消 / 付费 =====
function onMainAction() {
  const { action, disabled } = mainAction.value;
  if (disabled || acting.value) return;
  if (!requireLogin()) return;
  if (action === 'enroll') doEnroll();
  else if (action === 'cancel') doCancel();
  else if (action === 'pay') doPay();
}

// 付费报名：有未支付的订单直接进支付页，否则先下单（裸参 activityId）
async function doPay() {
  const order = detail.value?.currentUserOrder;
  if (order && order.status === 0) {
    uni.navigateTo({
      url: `/pages/order/pay?orderNo=${order.orderNo}&activityId=${activityId.value}`,
    });
    return;
  }
  acting.value = true;
  try {
    const { data } = await orderApi.create(Number(activityId.value));
    uni.navigateTo({
      url: `/pages/order/pay?orderNo=${data.orderNo}&activityId=${activityId.value}`,
    });
  } catch (e) {
    // 失败原因由 request 层 toast
  } finally {
    acting.value = false;
  }
}

function doEnroll() {
  const a = activity.value;
  uni.showModal({
    title: '确认报名',
    content: `确认报名《${a.title}》吗？`,
    success: async (res) => {
      if (!res.confirm) return;
      acting.value = true;
      try {
        await activityApi.enroll(Number(activityId.value));
        uni.showToast({ title: '报名成功', icon: 'none' });
        await loadDetail();
      } catch (e) {
        // 失败原因由 request 层 toast
      } finally {
        acting.value = false;
      }
    },
  });
}

function doCancel() {
  uni.showModal({
    title: '取消报名',
    content: '确定要取消报名吗？',
    success: async (res) => {
      if (!res.confirm) return;
      acting.value = true;
      try {
        await activityApi.cancelEnroll(Number(activityId.value));
        uni.showToast({ title: '已取消报名', icon: 'none' });
        await loadDetail();
      } catch (e) {
        // 失败原因由 request 层 toast
      } finally {
        acting.value = false;
      }
    },
  });
}

// ===== 收藏 / 分享 / 附件 =====
async function toggleFavorite() {
  if (!requireLogin() || favoriting.value) return;
  favoriting.value = true;
  try {
    if (favorited.value) {
      await activityApi.removeFavorite(Number(activityId.value));
      favorited.value = false;
      uni.showToast({ title: '已取消收藏', icon: 'none' });
    } else {
      await activityApi.addFavorite(Number(activityId.value));
      favorited.value = true;
      uni.showToast({ title: '收藏成功', icon: 'none' });
    }
  } catch (e) {
    // 失败原因由 request 层 toast
  } finally {
    favoriting.value = false;
  }
}

async function shareActivity() {
  if (shareLoading.value || !requireLogin()) return;
  shareLoading.value = true;
  try {
    const { data } = await activityApi.generateShare(Number(activityId.value));
    if (data?.shareUrl) {
      uni.setClipboardData({
        data: data.shareUrl,
        success: () => uni.showToast({ title: '分享链接已复制', icon: 'none' }),
      });
    }
  } catch (e) {
    // 失败原因由 request 层 toast
  } finally {
    shareLoading.value = false;
  }
}

function copyAttachment() {
  uni.setClipboardData({
    data: resolveFileUrl(activity.value.attachment),
    success: () => uni.showToast({ title: '附件链接已复制', icon: 'none' }),
  });
}

// ===== 评论 =====
const PAGE_SIZE = 10;
const comments = ref([]);
const commentTotal = ref(0);
const commentPage = ref(1);
const commentLoading = ref(false);
const commentLoaded = ref(false);
const commentContent = ref('');
const sending = ref(false);
const replyTo = ref(null);
// 后端热度无用户维度，用本地 map 做会话内点赞切换
const likedIds = ref({});

async function loadComments(reset = false) {
  if (commentLoading.value || !activityId.value) return;
  commentLoading.value = true;
  const pageNum = reset ? 1 : commentPage.value;
  try {
    const { data } = await activityApi.queryComments({
      activityId: Number(activityId.value),
      pageNum,
      pageSize: PAGE_SIZE,
    });
    const list = data?.list || [];
    comments.value = reset ? list : comments.value.concat(list);
    commentTotal.value = Number(data?.total || 0);
    commentPage.value = pageNum + 1;
    commentLoaded.value = true;
  } catch (e) {
    // 失败原因由 request 层 toast
  } finally {
    commentLoading.value = false;
  }
}

async function submitComment() {
  if (sending.value || !requireLogin()) return;
  const content = commentContent.value.trim();
  if (!content) {
    uni.showToast({ title: '请输入评论内容', icon: 'none' });
    return;
  }
  sending.value = true;
  try {
    await activityApi.addComment({
      activityId: Number(activityId.value),
      content,
      toCommentId: replyTo.value?.id,
    });
    commentContent.value = '';
    replyTo.value = null;
    uni.showToast({ title: '评论成功', icon: 'none' });
    await loadComments(true);
  } catch (e) {
    // 失败原因由 request 层 toast
  } finally {
    sending.value = false;
  }
}

function startReply(comment) {
  replyTo.value = comment;
}

async function toggleLike(comment) {
  if (!requireLogin()) return;
  const id = comment.id;
  try {
    if (likedIds.value[id]) {
      await activityApi.unlikeComment(id);
      likedIds.value[id] = false;
      comment.commentHot = Math.max(0, Number(comment.commentHot || 0) - 1);
    } else {
      await activityApi.likeComment(id);
      likedIds.value[id] = true;
      comment.commentHot = Number(comment.commentHot || 0) + 1;
    }
  } catch (e) {
    // 失败原因由 request 层 toast
  }
}

function removeComment(comment) {
  if (!requireLogin()) return;
  uni.showModal({
    title: '删除评论',
    content: '确定删除这条评论吗？',
    success: async (res) => {
      if (!res.confirm) return;
      try {
        await activityApi.deleteComment(comment.id);
        uni.showToast({ title: '已删除', icon: 'none' });
        await loadComments(true);
      } catch (e) {
        // 失败原因由 request 层 toast
      }
    },
  });
}

// ===== 展示辅助 =====
function commentTime(value) {
  return value ? formatDateTime(value).slice(5) : '';
}

const AVATAR_COLORS = ['#5b8ff9', '#61ddaa', '#f6bd16', '#ff9d4d', '#7262fd', '#78d3f8', '#9661bc', '#f6903d'];
function avatarColor(seed) {
  return AVATAR_COLORS[Number(seed || 0) % AVATAR_COLORS.length];
}

// ===== 生命周期 =====
onLoad((options) => {
  activityId.value = options.id || '';
  if (!activityId.value) {
    uni.showToast({ title: '缺少活动ID', icon: 'none' });
    return;
  }
  loadDetail();
  loadFavorite();
  loadComments(true);
});

// 从支付页等页面返回时刷新（订单与报名状态可能已变化；首次加载由 onLoad 负责）
onShow(() => {
  if (detail.value) {
    loadDetail();
  }
});

onPullDownRefresh(async () => {
  await Promise.all([loadDetail(), loadFavorite(), loadComments(true)]);
  uni.stopPullDownRefresh();
});
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background: $fc-bg;
  padding-bottom: 160rpx;
}

/* 封面 */
.cover {
  width: 100%;
  height: 360rpx;
  background: #e8ecf3;
}

.cover-img {
  width: 100%;
  height: 100%;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #3c7cff 0%, #6ea8ff 100%);
  padding: 0 48rpx;
  box-sizing: border-box;
}

.cover-placeholder-text {
  font-size: 40rpx;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
}

/* 卡片通用 */
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
  margin-bottom: 20rpx;
}

/* 信息卡 */
.title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.title {
  flex: 1;
  font-size: 36rpx;
  font-weight: 700;
  color: $fc-text-main;
  line-height: 1.4;
  margin-right: 16rpx;
}

.status-tag {
  flex-shrink: 0;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  background: #f0f2f5;
  color: $fc-text-sub;
}

.status-tag--1 {
  background: rgba(60, 124, 255, 0.1);
  color: $fc-primary;
}

.status-tag--0,
.status-tag--9 {
  background: rgba(255, 154, 46, 0.12);
  color: #ff9a2e;
}

.status-tag--3 {
  background: rgba(103, 194, 58, 0.12);
  color: #67c23a;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  margin-top: 20rpx;
}

.tag {
  padding: 6rpx 16rpx;
  margin: 0 12rpx 12rpx 0;
  border-radius: 8rpx;
  font-size: 22rpx;
  background: #f0f2f5;
  color: $fc-text-sub;
}

.tag--free {
  background: rgba(103, 194, 58, 0.12);
  color: #67c23a;
}

.tag--price {
  background: rgba(255, 106, 61, 0.12);
  color: $fc-price;
  font-weight: 600;
}

.tag--score {
  background: rgba(60, 124, 255, 0.1);
  color: $fc-primary;
}

.info-rows {
  margin-top: 12rpx;
  border-top: 1rpx solid #f0f1f3;
  padding-top: 12rpx;
}

.info-row {
  display: flex;
  align-items: flex-start;
  margin-top: 16rpx;
}

.info-label {
  flex-shrink: 0;
  width: 140rpx;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.info-value {
  flex: 1;
  font-size: 26rpx;
  color: $fc-text-main;
  line-height: 1.5;
}

.info-sub {
  font-size: 24rpx;
  color: $fc-text-sub;
}

/* 报名同学 */
.avatar-wall {
  display: flex;
  flex-wrap: wrap;
}

.avatar-item {
  width: 108rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 16rpx;
}

.avatar-img,
.avatar-fallback {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
}

.avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 600;
}

.avatar-name {
  margin-top: 8rpx;
  max-width: 100rpx;
  font-size: 20rpx;
  color: $fc-text-sub;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.avatar-more {
  font-size: 24rpx;
  color: $fc-text-sub;
}

/* 活动介绍 */
.desc {
  display: block;
  font-size: 28rpx;
  color: $fc-text-main;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-all;
}

.attachment {
  display: flex;
  align-items: center;
  margin-top: 24rpx;
  padding: 20rpx 24rpx;
  background: #f7f9fc;
  border-radius: 12rpx;
}

.attachment-name {
  flex: 1;
  margin: 0 16rpx;
  font-size: 26rpx;
  color: $fc-text-main;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attachment-copy {
  flex-shrink: 0;
  font-size: 24rpx;
  color: $fc-primary;
}

/* 评论 */
.comment-form {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.comment-input {
  flex: 1;
  height: 72rpx;
  padding: 0 24rpx;
  background: #f5f6f8;
  border-radius: 36rpx;
  font-size: 26rpx;
  color: $fc-text-main;
}

.reply-cancel {
  flex-shrink: 0;
  margin-left: 16rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.send-btn {
  flex-shrink: 0;
  margin-left: 16rpx;
  padding: 0 32rpx;
  height: 72rpx;
  line-height: 72rpx;
  border-radius: 36rpx;
  background: $fc-primary;
  color: #ffffff;
  font-size: 26rpx;
}

.send-btn--disabled {
  background: #a8c3ff;
}

.comment-empty {
  padding: 32rpx 0;
  text-align: center;
  font-size: 26rpx;
  color: $fc-text-sub;
}

.comment-item {
  display: flex;
  margin-top: 24rpx;
}

.comment-avatar {
  flex-shrink: 0;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #c3cbd8;
  display: flex;
  align-items: center;
  justify-content: center;
}

.comment-main {
  flex: 1;
  margin-left: 16rpx;
  min-width: 0;
}

.comment-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.comment-name {
  font-size: 26rpx;
  font-weight: 600;
  color: #5b6b8c;
}

.comment-time {
  font-size: 22rpx;
  color: $fc-text-sub;
}

.comment-content {
  display: block;
  margin-top: 8rpx;
  font-size: 28rpx;
  color: $fc-text-main;
  line-height: 1.6;
  word-break: break-all;
}

.comment-ops {
  display: flex;
  align-items: center;
  margin-top: 8rpx;
}

.comment-op {
  display: flex;
  align-items: center;
  margin-right: 32rpx;
}

.comment-op-text {
  margin-left: 6rpx;
  font-size: 24rpx;
  color: $fc-text-sub;
}

.comment-op--active .comment-op-text {
  color: $fc-primary;
}

.children {
  margin-top: 16rpx;
  padding-left: 20rpx;
  border-left: 4rpx solid #eef1f6;
}

.comment-item--child {
  margin-top: 16rpx;
}

.load-more {
  margin-top: 24rpx;
  padding: 16rpx 0;
  text-align: center;
  font-size: 26rpx;
  color: $fc-primary;
}

/* 底部操作栏 */
.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  height: 110rpx;
  padding: 0 24rpx;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  background: $fc-card-bg;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.action-icon {
  width: 110rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.action-icon-text {
  margin-top: 2rpx;
  font-size: 20rpx;
  color: $fc-text-sub;
}

.action-icon-text--active {
  color: $fc-price;
}

.main-btn {
  flex: 1;
  margin-left: 16rpx;
  height: 80rpx;
  border-radius: 40rpx;
  background: $fc-primary;
  color: #ffffff;
  font-size: 30rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-btn--disabled {
  background: #c8c9cc;
}

.main-btn--cancel {
  background: #ffffff;
  border: 2rpx solid #dcdfe6;
  color: #606266;
}
</style>
