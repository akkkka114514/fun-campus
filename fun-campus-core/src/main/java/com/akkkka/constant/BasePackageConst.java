package com.akkkka.constant;

/**
 * 应用扫描根包常量
 * <p>
 * 供 @ComponentScan / @MapperScan / 反射扫描统一引用，避免各模块硬编码包名字符串
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 */
public class BasePackageConst {

    /**
     * 应用根包（组件扫描 / Mapper 扫描 / 反射扫描的统一入口包）
     */
    public static final String BASE_PACKAGE = "com.akkkka";

}
