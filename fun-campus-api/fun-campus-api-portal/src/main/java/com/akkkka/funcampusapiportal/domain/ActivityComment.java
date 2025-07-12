package com.akkkka.funcampusnotice.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.akkkka.common.core.annotation.Excel;
import com.akkkka.common.core.web.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 activity_comment
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public class ActivityComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String uid;

    /** 评论用户id */
    @Excel(name = "评论用户id")
    private String userUid;

    /** 被评论的活动id */
    @Excel(name = "被评论的活动id")
    private String activityUid;

    /** 被回复的帖子id，null即为最顶层评论 */
    @Excel(name = "被回复的帖子id，null即为最顶层评论")
    private String toCommentUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String content;

    /** 为null即为顶层 */
    @Excel(name = "为null即为顶层")
    private String rootUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer deleted;

    public void setUid(String uid) 
    {
        this.uid = uid;
    }

    public String getUid() 
    {
        return uid;
    }

    public void setUserUid(String userUid) 
    {
        this.userUid = userUid;
    }

    public String getUserUid() 
    {
        return userUid;
    }

    public void setActivityUid(String activityUid) 
    {
        this.activityUid = activityUid;
    }

    public String getActivityUid() 
    {
        return activityUid;
    }

    public void setToCommentUid(String toCommentUid) 
    {
        this.toCommentUid = toCommentUid;
    }

    public String getToCommentUid() 
    {
        return toCommentUid;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setRootUid(String rootUid) 
    {
        this.rootUid = rootUid;
    }

    public String getRootUid() 
    {
        return rootUid;
    }

    public void setDeleted(Integer deleted) 
    {
        this.deleted = deleted;
    }

    public Integer getDeleted() 
    {
        return deleted;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uid", getUid())
            .append("userUid", getUserUid())
            .append("activityUid", getActivityUid())
            .append("toCommentUid", getToCommentUid())
            .append("content", getContent())
            .append("createTime", getCreateTime())
            .append("rootUid", getRootUid())
            .append("deleted", getDeleted())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
