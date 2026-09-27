package com.akkkka.admin.module.system.notice.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知公告 门户详情 VO
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */
@Data
public class PortalNoticeDetailVO {

    @Schema(description = "id")
    private Long noticeId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "分类")
    private Long noticeTypeId;

    @Schema(description = "分类名称")
    private String noticeTypeName;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "文号")
    private String documentNumber;

    @Schema(description = "页面浏览量")
    private Integer pageViewCount;

    @Schema(description = "纯文本内容")
    private String contentText;

    @Schema(description = "html内容")
    private String contentHtml;

    @Schema(description = "附件（多个英文逗号分隔）")
    private String attachment;

}
