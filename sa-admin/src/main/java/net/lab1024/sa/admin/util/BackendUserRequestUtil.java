package net.lab1024.sa.admin.util;

import net.lab1024.sa.admin.module.system.login.domain.RequestBackendUser;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.util.SmartRequestUtil;

/**
 * 后台用户端的请求工具类
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-09-08 20:47:06
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */
public final class BackendUserRequestUtil {

    public static RequestBackendUser getRequestUser() {
        return (RequestBackendUser) SmartRequestUtil.getRequestUser();
    }

    public static Long getRequestUserId() {
        RequestUser requestUser = getRequestUser();
        return null == requestUser ? null : requestUser.getUserId();
    }
}