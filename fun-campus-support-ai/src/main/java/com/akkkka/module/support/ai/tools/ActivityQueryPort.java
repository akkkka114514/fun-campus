package com.akkkka.module.support.ai.tools;

import java.util.List;

/**
 * 活动查询端口
 * <p>
 * 依赖反转：接口定义在 AI 支撑模块，实现由业务模块（funcampus）提供，
 * 使 AI 工具层不反向依赖业务模块实体与查询逻辑，也便于单测中替换桩实现。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
public interface ActivityQueryPort {

    /**
     * 查询当前可报名的活动（关键词模糊匹配活动标题）
     *
     * @param keyword 搜索关键词，可为空
     * @param limit   返回条数上限
     */
    List<ActivityBriefVO> queryEnrollableActivities(String keyword, int limit);

    /**
     * 查询用户的报名记录（含活动信息与签到状态）
     *
     * @param userId 门户用户id
     * @param limit  返回条数上限
     */
    List<EnrollmentBriefVO> queryUserEnrollments(Long userId, int limit);
}