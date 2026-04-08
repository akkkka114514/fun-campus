package com.akkkka.admin.module.business.funcampus.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * author:akkkka114514
 * create at 2026-02-28 15:26
 */
public class UpdateFormUtil {
    /*
        mbp处理全null更新会返回false或0
        这样子就分不清事务的回滚时机
        检测一个实体是不是所有字段为null,如果是返回true，反之返回false
     */

    public static boolean isEntityPropertiesAllNull(Object obj){
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields).noneMatch(field -> {
            try {
                return field.get(obj)!=null;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /*
        比上面的功能多出了忽略某些字段，比如主键
     */
    public static boolean isEntityPropertiesAllNull(Object obj, String ignoreField){
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> !field.getName().equals(ignoreField))
                .noneMatch(field -> {
            try {
                return field.get(obj)!=null;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /*
        比上面的功能多出了可以忽略更多字段
    */
    public static boolean isEntityPropertiesAllNull(Object obj, List<String> ignoreField){
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> !ignoreField.contains(field.getName()))
                .noneMatch(field -> {
            try {
                return field.get(obj)!=null;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
