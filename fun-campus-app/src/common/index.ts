import request from './request';
import { useUserStore } from '../stores/user';

// 创建拦截器对象来兼容原有代码
const interceptor = {
  request: (callback: Function) => {
    // 保存请求拦截器
    (request as any)._requestInterceptor = callback;
  },
  response: (callback: Function) => {
    // 保存响应拦截器
    (request as any)._responseInterceptor = callback;
  }
};

// 为request对象添加拦截器属性，以兼容原有代码
(request as any).interceptor = interceptor;

// type:request,upload
interceptor.request(function (config : any) {
	console.log('config', config);
	const userStore = useUserStore();
	//判断登录是否过期
	if (userStore.checkLogin()) {
		// 避免重复请求
		uni.$tm.u.throttle(() => {
			request.post('user/getUserInfo', 'dx-func-user').then((res : any) => {
				if (res.code === 1000) {
					userStore.setUserInfo(res);
				}
			});
		}, 15000);
	}
	return config;
});

//响应拦截，判断状态码是否通过
interceptor.response(function (res : any) {
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
export const init = (params = {}) => request.post('api/index/init', 'dx-func-activity', params);

// 获取分类
export const getCategory = (params = {}) => request.post('api/index/category', 'dx-func-activity', params);

export const indexHome = (params = {}) => request.post('api/index/home', 'dx-func-activity', params);
export const indexHelp = (params = {}) => request.post('api/index/help', 'dx-func-activity', params);

// 登陆
export const login = (params: any = {}) => request.post('portal/login', 'dx-func-user', params);

// 注册
export const register = (params = {}) => request.post('index/register', 'dx-func-user', params);

// 获取图形验证码
export const getCaptcha = () => request.get('portal/login/getCaptcha', 'dx-func-user', {});

// 获取短信验证码
export const postSmsCode = (params = {}) => request.post('index/postSmsCode', 'dx-func-user', params);

// 获取用户信息
export const getUserInfo = (params = {}) => request.post('user/getUserInfo', 'dx-func-user', params);

// 编辑用户信息
export const editUserInfo = (params = {}) => request.post('user/edit', 'dx-func-user', params);

// 找回密码
export const forget = (params = {}) => request.post('index/forget', 'dx-func-user', params);

// 创建/更新组织
export const saveTeam = (params = {}) => request.post('api/team/save', 'dx-func-activity', params);

// 组织首页
export const homeTeam = (params = {}) => request.post('api/team/home', 'dx-func-activity', params);

// 我的组织
export const myTeamList = (params = {}) => request.post('api/team/myList', 'dx-func-activity', params);

// 删除组织
export const delTeam = (params = {}) => request.post('api/team/del', 'dx-func-activity', params);

// 认证信息
export const getApproveInfo = (params = {}) => request.post('api/team/getApproveInfo', 'dx-func-activity', params);

// 认证
export const approve = (params = {}) => request.post('api/team/approve', 'dx-func-activity', params);

// 组织详情
export const teamDetail = (params = {}) => request.post('api/team/detail', 'dx-func-activity', params);
export const teamList = (params = {}) => request.post('api/team/list', 'dx-func-activity', params);
export const teamApply = (params = {}) => request.post('api/team/apply', 'dx-func-activity', params);
export const teamQuit = (params = {}) => request.post('api/team/quit', 'dx-func-activity', params);
export const teamJoinInfo = (params = {}) => request.post('api/team/joinInfo', 'dx-func-activity', params);

// 我的组织详情
export const myTeamDetail = (params = {}) => request.post('api/team/myDetail', 'dx-func-activity', params);

// 组织相片
export const photoIndex = (params = {}) => request.post('api/photo/index', 'dx-func-activity', params);
export const photoList = (params = {}) => request.post('api/photo/list', 'dx-func-activity', params);
export const photoSave = (params = {}) => request.post('api/photo/save', 'dx-func-activity', params);
export const photoUpdate = (params = {}) => request.post('api/photo/update', 'dx-func-activity', params);
export const photoInfo = (params = {}) => request.post('api/photo/info', 'dx-func-activity', params);
export const photoDel = (params = {}) => request.post('api/photo/del', 'dx-func-activity', params);

// 活动
export const activitySave = (params = {}) => request.post('api/activity/save', 'dx-func-activity', params);
export const myActivityInfo = (params = {}) => request.post('api/activity/myInfo', 'dx-func-activity', params);
export const homeActivity = (params = {}) => request.post('api/activity/home', 'dx-func-activity', params);
export const delActivity = (params = {}) => request.post('api/activity/del', 'dx-func-activity', params);
export const myActivityList = (params = {}) => request.post('api/activity/myList', 'dx-func-activity', params);
export const activitySetting = (params = {}) => request.post('api/activity/setting', 'dx-func-activity', params);
export const activityHome = (params = {}) => request.post('api/activity/home', 'dx-func-activity', params);
export const activityList = (params = {}) => request.post('api/activity/list', 'dx-func-activity', params);
export const activityDetail = (params = {}) => request.post('api/activity/detail', 'dx-func-activity', params);
export const formAndCost = (params = {}) => request.post('api/activity/formAndCost', 'dx-func-activity', params);

// 关注
export const followTeam = (params = {}) => request.post('api/follow/team', 'dx-func-activity', params);
export const followActivity = (params = {}) => request.post('api/follow/activity', 'dx-func-activity', params);
export const followList = (params = {}) => request.post('api/follow/list', 'dx-func-activity', params);

// 报名
export const applySave = (params = {}) => request.post('api/apply/save', 'dx-func-activity', params);
export const applyInfo = (params = {}) => request.post('api/apply/info', 'dx-func-activity', params);
export const applyCancel = (params = {}) => request.post('api/apply/cancel', 'dx-func-activity', params);
export const applyList = (params = {}) => request.post('api/apply/list', 'dx-func-activity', params);

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