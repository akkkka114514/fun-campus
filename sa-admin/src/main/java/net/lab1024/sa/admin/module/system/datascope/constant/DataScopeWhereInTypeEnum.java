package net.lab1024.sa.admin.module.system.datascope.constant;


import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 数据范围 sql where
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
public enum DataScopeWhereInTypeEnum implements BaseEnum {

    /**
     * 以后台用户IN
     */
    BACKEND_USER(0, "以后台用户IN"),


    /**
     * 自定义策略
     */
    CUSTOM_STRATEGY(2, "自定义策略");

    private final Integer value;
    private final String desc;

    DataScopeWhereInTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }


}
