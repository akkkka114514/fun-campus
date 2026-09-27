package com.akkkka.admin.module.system.notice.domain.form;

import com.akkkka.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通知公告 门户查询表单
 * <p>
 * 门户（学生/组织者）仅可查询全员可见且已发布的公告，查询条件收敛为分类与关键词
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */
@Data
public class PortalNoticeQueryForm extends PageParam {

    @Schema(description = "分类")
    private Long noticeTypeId;

    @Schema(description = "关键词（标题、作者、来源）")
    private String keywords;

}
