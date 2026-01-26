import axios from "axios";

axios.defaults.baseURL = 'http://localhost:1024/';


// 设置请求拦截器
axios.interceptors.request.use(
    config => {
        const userInfo: any = localStorage.getItem("userInfo");
        const token = userInfo ? JSON.parse(userInfo).data.token : null;
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 设置响应拦截器
axios.interceptors.response.use(
    response => {
        // 检查响应数据中是否有未登录标识
        const data = response.data;
        if (data && data.code === 30007) {
            // 清除本地存储的用户信息
            localStorage.removeItem("userInfo");
            // 跳转到登录页面
            uni.navigateTo({
                url: '/pages/login/login'
            });
            return Promise.reject(response); // 拒绝这个响应
        }
        return response;
    },
    error => {
        // 检查错误响应中是否有未登录标识
        if (error.response && error.response.data && error.response.data.code === 30007) {
            // 清除本地存储的用户信息
            localStorage.removeItem("userInfo");
            // 跳转到登录页面
            uni.navigateTo({
                url: '/pages/login/login'
            });
        }
        return Promise.reject(error);
    }
);

export const homeData
    = (activeActivityPage: number, pageNum: number, pageSize:number)=>{
    return axios.get("portal/homeData", {
        params: {
            activeActivityPage,
            pageNum,
            pageSize
        }
    })
}

export const getCaptcha = ()=>{
    return axios.get("portal/login/getCaptcha")
}

export const usernamePasswordLogin =
    (username: string, password: string,
     loginDevice: number, emailCode: string,
     captchaCode: string, captchaUuid: string)=>{
    return axios.post("portal/login", {
        username,
        password,
        loginDevice,
        emailCode,
        captchaCode,
        captchaUuid
    })
}

export const initActivityPublishPage= ()=> {
    return axios.get("/portal/activity/publish/init")
}


export const presignUploadUrl = (originalFileName: string, folderType: number)=>{
    return axios.get("/portal/file/uploadUrl/presign", {
        params: {
            originalFileName,
            folderType
        }
    })
}

export const querySimpleTribeList = (keyword: string)=>{
    return axios.get("/portal/tribe/query/simple", {
        params: {
            keyword
        }
    })
}