import { defineStore } from 'pinia';
import { ref } from 'vue';
import { UserInfoState } from './interface';
// 用户store
export const useUserStore = defineStore('userStore', () => {
	const initInfo = {
		role: [],
		permission: [],
		user_id: '',
		avatar: '',
		mobile: '',
		email: '',
		nickname: '',
		username: '',
		gender: 0,
		token: '',
		tokenExpired: 0,
	};
	//用户信息
	const userInfo = ref<UserInfoState>(
		uni.getStorageSync('dx_activity_user_info') || initInfo
	);
	// 是否登录
	const isLogin = ref(false);
	// 设置用户信息
	const setUserInfo = (info : any) => {
		userInfo.value = info;
		uni.setStorageSync('dx_activity_user_info', info);
		uni.setStorageSync('uni_id_token', info.token);
		uni.setStorageSync('uni_id_token_expired', info.tokenExpired);
		isLogin.value = true;
	};
	
	// 退出登录
	const logout = () => {
		if (!isLogin.value) return;
		isLogin.value = false;
		userInfo.value = initInfo;
		uni.clearStorageSync();
		console.log('退出登录');
		uni.reLaunch({
			url: '/pages/login/login',
		});
	};
	// 判断是否登录
	const checkLogin = () => {
		if (userInfo.value.tokenExpired - Date.now() <= 0) {
			logout();
		} else {
			isLogin.value = true;
		}
		return userInfo.value.tokenExpired - Date.now() <= 60000 && userInfo.value.tokenExpired - Date.now() > 0;
	};
	// 检查登录
	checkLogin();
	
	return {
		isLogin,
		userInfo,
		setUserInfo,
		checkLogin,
		logout,
	};
});