package com.akkkka.funcampusnotice.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.akkkka.common.core.annotation.Excel;
import com.akkkka.common.core.web.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 activity_schedule
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public class ActivitySchedule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String activityUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date enrollStartTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date enrollEndTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date activityStartTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date activityEndTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date signinStartTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date signinEndTime;

    public void setActivityUid(String activityUid) 
    {
        this.activityUid = activityUid;
    }

    public String getActivityUid() 
    {
        return activityUid;
    }

    public void setEnrollStartTime(Date enrollStartTime) 
    {
        this.enrollStartTime = enrollStartTime;
    }

    public Date getEnrollStartTime() 
    {
        return enrollStartTime;
    }

    public void setEnrollEndTime(Date enrollEndTime) 
    {
        this.enrollEndTime = enrollEndTime;
    }

    public Date getEnrollEndTime() 
    {
        return enrollEndTime;
    }

    public void setActivityStartTime(Date activityStartTime) 
    {
        this.activityStartTime = activityStartTime;
    }

    public Date getActivityStartTime() 
    {
        return activityStartTime;
    }

    public void setActivityEndTime(Date activityEndTime) 
    {
        this.activityEndTime = activityEndTime;
    }

    public Date getActivityEndTime() 
    {
        return activityEndTime;
    }

    public void setSigninStartTime(Date signinStartTime) 
    {
        this.signinStartTime = signinStartTime;
    }

    public Date getSigninStartTime() 
    {
        return signinStartTime;
    }

    public void setSigninEndTime(Date signinEndTime) 
    {
        this.signinEndTime = signinEndTime;
    }

    public Date getSigninEndTime() 
    {
        return signinEndTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("activityUid", getActivityUid())
            .append("enrollStartTime", getEnrollStartTime())
            .append("enrollEndTime", getEnrollEndTime())
            .append("activityStartTime", getActivityStartTime())
            .append("activityEndTime", getActivityEndTime())
            .append("signinStartTime", getSigninStartTime())
            .append("signinEndTime", getSigninEndTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
