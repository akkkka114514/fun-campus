package com.akkkka.admin.constant;

import com.akkkka.constant.CacheKeyConst;

/**
 * 缓存 key
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2022-01-07 18:59:22
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
public class AdminCacheConst extends CacheKeyConst {


    /**
     * 分类相关缓存
     */
    public static class Category {

        public static final String CATEGORY_ENTITY = "category_cache";

        public static final String CATEGORY_SUB = "category_sub_cache";

        public static final String CATEGORY_TREE = "category_tree_cache";
    }

    /**
     * 登录相关
     */
    public static class Login {

        /**
         * 请求用户信息
         */
        public static final String REQUEST_USER = "login_request_user";

        /**
         * 请求用户信息权限
         */
        public static final String USER_PERMISSION = "login_user_permission";
    }

}
