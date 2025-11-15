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

.bg-white {
	background-color: #fff;
}

.align-center {
	align-items: center;
}

.tips {
	font-size: 26rpx;
	color: #6d6868;
}

.price {
	color: #ff0405;
}

.approve {
	color: #999999;
	font-size: 26rpx;
}

.success {
	color: #0bbb08;
	font-size: 26rpx;
}
.input-select{
   padding: 12rpx 0.75rem;
   border: 0px solid rgb(230, 230, 230);
   background-color: rgb(245, 245, 245);
   transition: border 0.24s ease 0s;
}
.no-select{
  color: #808080;
}
.load-more{
  padding: 30rpx 30vw;
}
</style>
