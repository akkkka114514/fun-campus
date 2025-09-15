package net.lab1024.sa.admin.module.system.notice.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 公告、通知 可见范围类型
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-12 21:40:39
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@AllArgsConstructor
public enum NoticeVisibleRangeDataTypeEnum implements BaseEnum {

    /**
     * 后台用户
     */
    BACKEND_USER(1, "后台用户"),

    ORGANIZER(2,"学校组织账号运营者"),
    ;

    private final Integer value;

    private final String desc;
}
