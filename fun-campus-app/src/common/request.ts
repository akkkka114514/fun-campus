import { request } from './config';

class Request {
	interceptor = {
		//请求之前拦截
		request: (cb: (options: any) => any) => {
			if (typeof cb == 'function') {
				this.requestBeforeFun = cb;
			}
		},
		//响应拦截
		response: (cb: (res: any) => any) => {
			if (typeof cb == 'function') {
				this.responseAfterFun = cb;
			}
		},
	};
	//请求前处理
	requestBeforeFun(options: any) {
		return options;
	}
	//响应拦截
	responseAfterFun(res: any) {
		return res;
	}
	//错误处理
	requestErrorFun(error: any) {
		return error;
	}
	get(action: string, functionName: string, data = {}): Promise<{ data: any; code: number; message: string }> {
		let options = {
			name: functionName,
			data: {
				action: action,
				data,
			},
		};
		let beforeData = this.requestBeforeFun(options);
		if (beforeData === false) {
			return new Promise(() => {});
		}

		// 对于验证码等不需要参数的GET请求，直接使用action路径
		const requestData = Object.keys(data).length > 0 ? options.data : {};
		return request.get('/' + action, requestData)
			.then((result: any) => {
				result = this.responseAfterFun(result);
				return Promise.resolve(result);
			})
			.catch((err) => {
				if (this.requestErrorFun(err) === false) {
					return;
				}
				return Promise.reject(err);
			});
	}
	post(action: string, functionName: string, data = {}): Promise<{ data: any; code: number; message: string }> {
		let options = {
			name: functionName,
			data: {
				action: action,
				// 直接发送data而不是包装在action/data结构中
				...data,
			},
		};
		let beforeData = this.requestBeforeFun(options);
		if (beforeData === false) {
			return new Promise(() => {});
		}

		// 确保使用JSON格式发送数据
		const requestOptions = {
			header: {
				'Content-Type': 'application/json'
			}
		};

		// 确保发送的数据是JSON字符串格式
		const requestData = typeof options.data === 'object' ? options.data : {};

		return request.post('/' + action, requestData, requestOptions)
			.then((result: any) => {
				result = this.responseAfterFun(result);
				return Promise.resolve(result);
			})
			.catch((err) => {
				if (this.requestErrorFun(err) === false) {
					return;
				}
				return Promise.reject(err);
			});
	}
}

// 保持与原文件相同的接口，但使用配置好的request方法
const requestInstance = new Request();

// 添加拦截器支持
(requestInstance as any).interceptor = {
	request: (callback: (options: any) => any) => {
		requestInstance.requestBeforeFun = callback;
	},
	response: (callback: (res: any) => any) => {
		requestInstance.responseAfterFun = callback;
	}
};

export default requestInstance;