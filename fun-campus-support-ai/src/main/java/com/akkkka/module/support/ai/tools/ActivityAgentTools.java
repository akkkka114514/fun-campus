package com.akkkka.module.support.ai.tools;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI 活动工具集
 * <p>
 * 以 @Tool 暴露给对话模型的函数调用入口：查可报名活动、查我的报名。
 * 业务查询通过 {@link ActivityQueryPort} 依赖反转，由业务模块提供实现；
 * 端口缺失（如单模块启动未装配）或查询异常时返回说明性文案，不影响对话主流程。
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */
@Slf4j
@Component
@AllArgsConstructor
public class ActivityAgentTools {

    /**
     * 工具上下文 key：当前登录用户id（由对话层写入 ToolContext）
     */
    public static final String CONTEXT_USER_ID = "userId";

    /**
     * 单次查询返回条数上限
     */
    private static final int QUERY_LIMIT = 5;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ObjectProvider<ActivityQueryPort> activityQueryPortProvider;

    @Tool(description = "查询当前可报名的活动列表。当用户想找活动、问有哪些活动可以报名时调用。返回活动名称、地点、报名截止时间、活动时间、付费信息等。")
    public String queryEnrollableActivities(
            @ToolParam(description = "搜索关键词，用于按活动名称筛选，没有明确关键词时传空字符串", required = false) String keyword) {
        ActivityQueryPort queryPort = activityQueryPortProvider.getIfAvailable();
        if (queryPort == null) {
            log.warn("ActivityQueryPort 未装配，活动查询工具暂不可用");
            return "活动查询功能暂时不可用。";
        }
        try {
            String normalizedKeyword = keyword == null ? "" : keyword.trim();
            List<ActivityBriefVO> activities = queryPort.queryEnrollableActivities(normalizedKeyword, QUERY_LIMIT);
            if (activities.isEmpty()) {
                return StringUtils.hasText(normalizedKeyword)
                        ? "没有找到与「" + normalizedKeyword + "」相关的可报名活动。"
                        : "当前没有正在报名的活动。";
            }
            StringBuilder text = new StringBuilder("当前可报名的活动：\n");
            for (ActivityBriefVO activity : activities) {
                text.append("- ").append(activity.getTitle());
                if (StringUtils.hasText(activity.getPosition())) {
                    text.append("｜地点：").append(activity.getPosition());
                }
                if (activity.getEnrollEndTime() != null) {
                    text.append("｜报名截止：").append(activity.getEnrollEndTime().format(DATE_TIME_FORMATTER));
                }
                if (activity.getActivityStartTime() != null) {
                    text.append("｜活动开始：").append(activity.getActivityStartTime().format(DATE_TIME_FORMATTER));
                }
                if (Boolean.TRUE.equals(activity.getPaidFlag()) && activity.getPriceFen() != null) {
                    text.append("｜付费：").append(formatPrice(activity.getPriceFen())).append("元");
                }
                text.append('\n');
            }
            return text.toString();
        } catch (Exception e) {
            log.warn("AI 工具查询可报名活动失败：{}", e.getMessage());
            return "活动查询暂时失败，请稍后再试。";
        }
    }

    @Tool(description = "查询当前用户已报名的活动及其状态（活动名称、活动时间、是否已签到等）。当用户问「我报了哪些活动」「我的报名情况」「我的活动」时调用。")
    public String queryMyEnrollments(ToolContext toolContext) {
        Long userId = resolveUserId(toolContext);
        if (userId == null) {
            return "当前未获取到登录用户，无法查询报名记录。";
        }
        ActivityQueryPort queryPort = activityQueryPortProvider.getIfAvailable();
        if (queryPort == null) {
            log.warn("ActivityQueryPort 未装配，活动查询工具暂不可用");
            return "活动查询功能暂时不可用。";
        }
        try {
            List<EnrollmentBriefVO> enrollments = queryPort.queryUserEnrollments(userId, QUERY_LIMIT);
            if (enrollments.isEmpty()) {
                return "当前没有查询到你已报名的活动。";
            }
            StringBuilder text = new StringBuilder("已报名的活动：\n");
            for (EnrollmentBriefVO enrollment : enrollments) {
                text.append("- ").append(enrollment.getActivityTitle());
                if (StringUtils.hasText(enrollment.getActivityStatusLabel())) {
                    text.append("｜状态：").append(enrollment.getActivityStatusLabel());
                }
                if (enrollment.getActivityStartTime() != null) {
                    text.append("｜活动开始：").append(enrollment.getActivityStartTime().format(DATE_TIME_FORMATTER));
                }
                text.append("｜签到：").append(Boolean.TRUE.equals(enrollment.getSignInStatus()) ? "已签到" : "未签到");
                if (Boolean.TRUE.equals(enrollment.getSignOutStatus())) {
                    text.append("｜已签退");
                }
                text.append('\n');
            }
            return text.toString();
        } catch (Exception e) {
            log.warn("AI 工具查询我的报名失败：{}", e.getMessage());
            return "报名记录查询暂时失败，请稍后再试。";
        }
    }

    /**
     * 从工具上下文取当前用户id（由对话层在发起请求时写入）
     */
    private Long resolveUserId(ToolContext toolContext) {
        if (toolContext == null) {
            return null;
        }
        Object value = toolContext.getContext().get(CONTEXT_USER_ID);
        return value instanceof Long userId ? userId : null;
    }

    /**
     * 分转元展示
     */
    private String formatPrice(Integer priceFen) {
        return BigDecimal.valueOf(priceFen, 2).stripTrailingZeros().toPlainString();
    }
}