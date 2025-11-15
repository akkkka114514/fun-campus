import request from './request';
import { useUserStore } from '../stores/user';

// type:request,upload
request.interceptor.request(function (config : any) {
	console.log('config', config);
	const userStore = useUserStore();
	//判断登录是否过期
	if (userStore.checkLogin()) {
		// 避免重复请求
		uni.$tm.u.throttle(() => {
			request.send('user/getUserInfo', 'dx-func-user').then((res : any) => {
				if (res.code === 1000) {
					userStore.setUserInfo(res);
				}
			});
		}, 15000);
	}
	return config;
});

//响应拦截，判断状态码是否通过
request.interceptor.response(function (res : any) {
	if (res.code === 4001) {
		uni.$tm.u.throttle(() => {
			uni.showToast({
				title: res.message,
				icon: 'none'
			});
			uni.reLaunch({
				url: '/pages/login/login'
			});
		}, 5000);
	}
	return res;
});
// 导出 request
export const axios = request;
// 应用初始化
export const init = (params = {}) => request.send('api/index/init', 'dx-func-activity', params);
// 获取分类
export const getCategory = (params = {}) => request.send('api/index/category', 'dx-func-activity', params);
export const indexHome = (params = {}) => request.send('api/index/home', 'dx-func-activity', params);
export const indexHelp = (params = {}) => request.send('api/index/help', 'dx-func-activity', params);
// 登陆
export const login = (params = {}) => request.send('portal/login', 'dx-func-user', params, 'http://localhost:1024');
// 注册
export const register = (params = {}) => request.send('index/register', 'dx-func-user', params);
// 获取图形验证码
export const getCaptcha = (params = {}) => request.send('index/getCaptcha', 'dx-func-user', params);
// 获取短信验证码
export const sendSmsCode = (params = {}) => request.send('index/sendSmsCode', 'dx-func-user', params);
// 获取用户信息
export const getUserInfo = (params = {}) => request.send('user/getUserInfo', 'dx-func-user', params);
// 编辑用户信息
export const editUserInfo = (params = {}) => request.send('user/edit', 'dx-func-user', params);
// 找回密码
export const forget = (params = {}) => request.send('index/forget', 'dx-func-user', params);
// 创建/更新组织
export const saveTeam = (params = {}) => request.send('api/team/save', 'dx-func-activity', params);
// 组织首页
export const homeTeam = (params = {}) => request.send('api/team/home', 'dx-func-activity', params);
// 我的组织
export const myTeamList = (params = {}) => request.send('api/team/myList', 'dx-func-activity', params);
// 删除组织
export const delTeam = (params = {}) => request.send('api/team/del', 'dx-func-activity', params);
// 认证信息
export const getApproveInfo = (params = {}) => request.send('api/team/getApproveInfo', 'dx-func-activity', params);
// 认证
export const approve = (params = {}) => request.send('api/team/approve', 'dx-func-activity', params);
// 组织详情
export const teamDetail = (params = {}) => request.send('api/team/detail', 'dx-func-activity', params);
export const teamList = (params = {}) => request.send('api/team/list', 'dx-func-activity', params);
export const teamApply = (params = {}) => request.send('api/team/apply', 'dx-func-activity', params);
export const teamQuit = (params = {}) => request.send('api/team/quit', 'dx-func-activity', params);
export const teamJoinInfo = (params = {}) => request.send('api/team/joinInfo', 'dx-func-activity', params);
// 我的组织详情
export const myTeamDetail = (params = {}) => request.send('api/team/myDetail', 'dx-func-activity', params);
// 组织相片
export const photoIndex = (params = {}) => request.send('api/photo/index', 'dx-func-activity', params);
export const photoList = (params = {}) => request.send('api/photo/list', 'dx-func-activity', params);
export const photoSave = (params = {}) => request.send('api/photo/save', 'dx-func-activity', params);
export const photoUpdate = (params = {}) => request.send('api/photo/update', 'dx-func-activity', params);
export const photoInfo = (params = {}) => request.send('api/photo/info', 'dx-func-activity', params);
export const photoDel = (params = {}) => request.send('api/photo/del', 'dx-func-activity', params);
// 活动
export const activitySave = (params = {}) => request.send('api/activity/save', 'dx-func-activity', params);
export const myActivityInfo = (params = {}) => request.send('api/activity/myInfo', 'dx-func-activity', params);
export const homeActivity = (params = {}) => request.send('api/activity/home', 'dx-func-activity', params);
export const delActivity = (params = {}) => request.send('api/activity/del', 'dx-func-activity', params);
export const myActivityList = (params = {}) => request.send('api/activity/myList', 'dx-func-activity', params);
export const activitySetting = (params = {}) => request.send('api/activity/setting', 'dx-func-activity', params);
export const activityHome = (params = {}) => request.send('api/activity/home', 'dx-func-activity', params);
export const activityList = (params = {}) => request.send('api/activity/list', 'dx-func-activity', params);
export const activityDetail = (params = {}) => request.send('api/activity/detail', 'dx-func-activity', params);
export const formAndCost = (params = {}) => request.send('api/activity/formAndCost', 'dx-func-activity', params);

// 关注
export const followTeam = (params = {}) => request.send('api/follow/team', 'dx-func-activity', params);
export const followActivity = (params = {}) => request.send('api/follow/activity', 'dx-func-activity', params);
export const followList = (params = {}) => request.send('api/follow/list', 'dx-func-activity', params);
// 报名
export const applySave = (params = {}) => request.send('api/apply/save', 'dx-func-activity', params);
export const applyInfo = (params = {}) => request.send('api/apply/info', 'dx-func-activity', params);
export const applyCancel = (params = {}) => request.send('api/apply/cancel', 'dx-func-activity', params);
export const applyList = (params = {}) => request.send('api/apply/list', 'dx-func-activity', params);


// 单文件上传请求
export const upload = ({ name, size, path, type } : any, query = {}, index ?: number) => {
	return new Promise(async (resolve, reject) => {
		const userStore = useUserStore();
		if (!userStore.checkLogin() && !userStore.isLogin) {
			uni.$tm.u.throttle(() => {
				uni.showToast({
					title: '登录已过期，请重新登录',
					icon: 'none'
				});
				uni.reLaunch({
					url: '/pages/index/index',
				});
			}, 5000);
			return reject();
		}
		let y, m, d, day, rand, cloudPath;
		d = new Date();
		y = d.getFullYear().toString();
		m = (d.getMonth() + 1).toString().padStart(2, '0');
		day = d.getDate().toString().padStart(2, '0');
		rand = (index || 0).toString().padStart(4, '0');
		cloudPath = y + '/' + m + '/' + day + '/' + Date.now() + rand + '.' + (name || path).split('.').pop();
		const result = await uniCloud.uploadFile({
			filePath: path,
			cloudPath,
			onUploadProgress(e) {
				console.log(e);
			},
		});
		console.log('上传结果：', result);
		let url = '';
		if (/^cloud:\/\//.test(result.fileID)) {
			//腾讯云处理
			const { fileList } = await uniCloud.getTempFileURL({
				fileList: [result.fileID],
			});
			if (fileList.length) {
				url = fileList[0].tempFileURL || fileList[0].download_url;
			}
		} else {
			url = result.fileID;
		}
		//上传云储存成功处理【保存数据库】
		if (url) {
			const db = uniCloud.database();
			const collection = db.collection('dx-resource');
			const res = await collection.add({
				file_id: result.fileID,
				url,
				file_size: size,
				file_name: name,
				type: type || (name || path).split('.').pop(),
				group_id: '',
				user_id: userStore.userInfo.user_id,
				...query,
			});
			if (res.errCode !== 0) {
				reject(res.message);
			} else {
				resolve(index === void 0 ? url : { url, index });
			}
		} else {
			reject();
		}
	});
};
// 多文件上传
export const multiUpload = (files : any[], query = {}, max = 6) => {
	return new Promise(async resolve => {
		let pool : any = []; //并发池
		let result : any = [];
		for (let i = 0; i < files.length; i++) {
			let url = files[i];
			let task = upload(url, query, i);
			task.then(data => {
				//每当并发池跑完一个任务,从并发池删除个任务
				pool.splice(pool.indexOf(task), 1);
				result.push(data);
				if (pool.length === 0) {
					resolve(result.sort((a : any, b : any) => a.index - b.index).map((item : any) => item.url));
				}
			});
			pool.push(task);
			if (pool.length === max) {
				await Promise.race(pool);
			}
		}
	});
};