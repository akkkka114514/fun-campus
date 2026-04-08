package com.akkkka.admin.util;

import com.akkkka.admin.module.system.login.domain.RequestBackendUser;
import com.akkkka.common.domain.RequestUser;
import com.akkkka.common.util.SmartRequestUtil;

/**
 * admin 端的请求工具类
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/7/28 19:39:21
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */
public final class AdminRequestUtil {


    public static RequestBackendUser getRequestUser() {
        return (RequestBackendUser) SmartRequestUtil.getRequestUser();
    }

    public static Long getRequestUserId() {
        RequestUser requestUser = getRequestUser();
        return null == requestUser ? null : requestUser.getUserId();
    }


}
