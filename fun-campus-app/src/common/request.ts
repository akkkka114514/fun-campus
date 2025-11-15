class Request {
	interceptor = {
		//请求之前拦截
		request: (cb: any) => {
			if (typeof cb == 'function') {
				this.requestBeforeFun = cb;
			}
		},
		//响应拦截
		response: (cb: any) => {
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
	send(action: string, functionName: string, data = {}): Promise<{ data: any; code: number; message: string }> {
		let options = {
			name: functionName,
			data: {
				action: action,
				data,
			},
		};
		//请求之前拦截
		let beforeData = this.requestBeforeFun(options);
		if (beforeData === false) {
			return new Promise(() => {});
		}		
		return uniCloud
			.callFunction(options)
			.then(({ result }) => {				
				//响应拦截
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
export default new Request();
