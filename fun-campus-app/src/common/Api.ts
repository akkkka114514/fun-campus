import axios from "axios";

axios.defaults.baseURL = 'http://localhost:1024/';

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