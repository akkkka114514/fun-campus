package com.akkkka.funcampusnotice.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.akkkka.common.core.annotation.Excel;
import com.akkkka.common.core.web.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 organizer_activity
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public class OrganizerActivity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String uid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String activityUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String organizerUid;

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

    public void setActivityUid(String activityUid) 
    {
        this.activityUid = activityUid;
    }

    public String getActivityUid() 
    {
        return activityUid;
    }

    public void setOrganizerUid(String organizerUid) 
    {
        this.organizerUid = organizerUid;
    }

    public String getOrganizerUid() 
    {
        return organizerUid;
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
            .append("activityUid", getActivityUid())
            .append("organizerUid", getOrganizerUid())
            .append("deleted", getDeleted())
            .append("updateTime", getUpdateTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
