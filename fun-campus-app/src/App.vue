<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app';
import { init } from '@/common/index'
import { useAppStore } from '@/stores/app'
onLaunch((e) => {
	console.log('App Launch',e);
	// #ifdef H5
	const search = window.location.search;
	const params = new URLSearchParams(search);
	const code = params.get('code');
	const state = params.get('state');
	// 携带了 # 号的参数
	if(state?.includes('dxadmin__') && code){
		let stateArr = state.split('__');
		window.location.href = window.location.origin + '/#'+stateArr[1]+'?code=' + code + '&state=' + stateArr[0];
	}
	// #endif
});
onShow(() => {
	console.log('App Show');
	const appStore = useAppStore();
	init().then(res => {
		if (res.code === 1000) {
			appStore.setAppInfo(res.data);
		}
	})
});
onHide(() => {
	console.log('App Hide');
});
</script>
<style>
/* #ifdef APP-NVUE */
@import './tmui/scss/nvue.css';
/* #endif */
/* #ifndef APP-NVUE */
@import './tmui/scss/noNvue.css';

/* #endif */
view {
	box-sizing: border-box;
}

/* 全局美化样式 */
.bg-white {
	background-color: #fff;
	border-radius: 16rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
	overflow: hidden;
}

.align-center {
	align-items: center;
}

.tips {
	font-size: 24rpx;
	color: #888;
}

.price {
	color: #ff4757;
	font-weight: 500;
}

.approve {
	color: #999999;
	font-size: 24rpx;
}

.success {
	color: #2ed573;
	font-size: 24rpx;
}

.input-select{
   padding: 16rpx 24rpx;
   border: 2rpx solid #e6e6e6;
   background-color: #f8f9fa;
   border-radius: 12rpx;
   transition: all 0.3s ease;
}

.input-select:focus {
	border-color: #3c8af8;
	box-shadow: 0 0 0 2rpx rgba(60, 138, 248, 0.2);
}

.no-select{
  color: #808080;
}

.load-more{
  padding: 30rpx 30vw;
  text-align: center;
  color: #888;
  font-size: 26rpx;
}

/* 卡片样式优化 */
.card {
	background-color: #fff;
	border-radius: 16rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
	overflow: hidden;
	transition: all 0.3s ease;
}

.card:hover {
	box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
	transform: translateY(-4rpx);
}

/* 按钮样式优化 */
.btn-primary {
	background: linear-gradient(120deg, #3c8af8, #2962ff);
	color: white;
	border: none;
	border-radius: 50rpx;
	padding: 20rpx 30rpx;
	font-weight: 500;
	transition: all 0.3s ease;
}

.btn-primary:active {
	transform: scale(0.98);
	opacity: 0.9;
}

/* 文本样式增强 */
.text-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.text-subtitle {
	font-size: 28rpx;
	color: #666;
}

.text-content {
	font-size: 26rpx;
	color: #555;
	line-height: 1.6;
}

/* 分割线优化 */
.divider {
	height: 1rpx;
	background: linear-gradient(to right, transparent, #eee, transparent);
	margin: 20rpx 0;
}

/* 动画效果 */
.fade-in {
	animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
	from {
		opacity: 0;
		transform: translateY(20rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

/* 加载动画 */
.loading-dot {
	animation: loading 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) { animation-delay: -0.32s; }
.loading-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes loading {
	0%, 80%, 100% {
		transform: scale(0);
		opacity: 0.6;
	}
	40% {
		transform: scale(1);
		opacity: 1;
	}
}

/* 组件美化样式 */

/* 输入框美化 */
.tm-input {
	transition: all 0.3s ease;
	border-radius: 20rpx !important;
	background-color: rgba(248, 249, 250, 0.8) !important;
	border: 2rpx solid #e9ecef !important;
}

.tm-input:focus-within {
	border-color: #3c8af8 !important;
	box-shadow: 0 0 0 2rpx rgba(60, 138, 248, 0.2) !important;
	background-color: #fff !important;
}

/* 按钮美化 */
.tm-button {
	transition: all 0.3s ease;
	border-radius: 20rpx !important;
	font-weight: 500;
	letter-spacing: 1rpx;
	box-shadow: 0 10rpx 20rpx rgba(60, 138, 248, 0.3) !important;
}

.tm-button:active {
	transform: scale(0.98);
	box-shadow: 0 5rpx 15rpx rgba(60, 138, 248, 0.2) !important;
}

.tm-button:hover {
	box-shadow: 0 15rpx 25rpx rgba(60, 138, 248, 0.4) !important;
	transform: translateY(-3rpx);
}

/* 表单项美化 */
.tm-form-item {
	margin-bottom: 30rpx;
}

/* 头像美化 */
.tm-avatar {
	transition: all 0.3s ease;
	box-shadow: 0 4rpx 10rpx rgba(0,0,0,0.05);
}

.tm-avatar:hover {
	transform: translateY(-5rpx);
	box-shadow: 0 8rpx 20rpx rgba(0,0,0,0.1);
}

.tm-avatar:active {
	transform: scale(0.95);
}

/* 图标美化 */
.tm-icon {
	transition: all 0.3s ease;
}

.tm-icon:hover {
	transform: scale(1.1);
}

/* 分割线美化 */
.tm-divider {
	margin: 30rpx 0;
}

/* 卡片美化 */
.tm-sheet {
	border-radius: 16rpx;
	transition: all 0.3s ease;
}

.tm-sheet:hover {
	transform: translateY(-4rpx);
	box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
}
</style>