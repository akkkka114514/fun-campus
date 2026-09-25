package com.akkkka.admin.module.system;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

/**
 * MyBatis-Plus 单元测试辅助：手动注册实体 TableInfo
 * <p>
 * 纯 Mockito 单测环境没有 SqlSessionFactory 启动流程，TableInfoHelper 缓存为空，
 * 而 LambdaQueryWrapper.select / LambdaUpdateWrapper.set 等操作会立即解析实体列
 * （AbstractLambdaWrapper.tryInitCache → LambdaUtils.getColumnMap），缺少缓存时抛出
 * "can not find lambda cache for this entity"。测试类加载时注册被测实体即可消除该问题。
 * <p>
 * 与 funcampus 模块测试目录中的同名工具类各自独立（跨模块测试类不可直接复用）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 */
public final class MybatisPlusTestRegistry {

    private MybatisPlusTestRegistry() {
    }

    /**
     * 注册实体对应的 TableInfo（幂等：已注册的实体跳过）
     */
    public static synchronized void register(Class<?>... entityClasses) {
        for (Class<?> entityClass : entityClasses) {
            if (TableInfoHelper.getTableInfo(entityClass) == null) {
                TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new Configuration(), ""), entityClass);
            }
        }
    }
}
