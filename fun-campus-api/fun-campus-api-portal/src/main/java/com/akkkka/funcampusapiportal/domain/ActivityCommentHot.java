package com.akkkka.funcampusnotice.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.akkkka.common.core.annotation.Excel;
import com.akkkka.common.core.web.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 activity_comment_hot
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public class ActivityCommentHot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String commentUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long commentHot;

    public void setCommentUid(String commentUid) 
    {
        this.commentUid = commentUid;
    }

    public String getCommentUid() 
    {
        return commentUid;
    }

    public void setCommentHot(Long commentHot) 
    {
        this.commentHot = commentHot;
    }

    public Long getCommentHot() 
    {
        return commentHot;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("commentUid", getCommentUid())
            .append("commentHot", getCommentHot())
            .toString();
    }
}
