package com.akkkka.funcampusnotice.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.akkkka.common.core.annotation.Excel;
import com.akkkka.common.core.web.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 activity
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public class Activity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String uid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String title;

    /** 1-》等待报名，2->报名结束，3-》等待签到，4-》活动结束 */
    @Excel(name = "1-》等待报名，2->报名结束，3-》等待签到，4-》活动结束")
    private Long status;

    /** 活动地点 */
    @Excel(name = "活动地点")
    private String position;

    /** 能得到的学分 */
    @Excel(name = "能得到的学分")
    private Long scoreCanGet;

    /** 报名人数限制 */
    @Excel(name = "报名人数限制")
    private Long enrollNumLimit;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String activitySchoolId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String activityOrganizerId;

    /** 0-》未删除，1-》已删除 */
    @Excel(name = "0-》未删除，1-》已删除")
    private Integer deleted;

    public void setUid(String uid) 
    {
        this.uid = uid;
    }

    public String getUid() 
    {
        return uid;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setPosition(String position) 
    {
        this.position = position;
    }

    public String getPosition() 
    {
        return position;
    }

    public void setScoreCanGet(Long scoreCanGet) 
    {
        this.scoreCanGet = scoreCanGet;
    }

    public Long getScoreCanGet() 
    {
        return scoreCanGet;
    }

    public void setEnrollNumLimit(Long enrollNumLimit) 
    {
        this.enrollNumLimit = enrollNumLimit;
    }

    public Long getEnrollNumLimit() 
    {
        return enrollNumLimit;
    }

    public void setActivitySchoolId(String activitySchoolId) 
    {
        this.activitySchoolId = activitySchoolId;
    }

    public String getActivitySchoolId() 
    {
        return activitySchoolId;
    }

    public void setActivityOrganizerId(String activityOrganizerId) 
    {
        this.activityOrganizerId = activityOrganizerId;
    }

    public String getActivityOrganizerId() 
    {
        return activityOrganizerId;
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
            .append("title", getTitle())
            .append("status", getStatus())
            .append("position", getPosition())
            .append("scoreCanGet", getScoreCanGet())
            .append("enrollNumLimit", getEnrollNumLimit())
            .append("activitySchoolId", getActivitySchoolId())
            .append("activityOrganizerId", getActivityOrganizerId())
            .append("deleted", getDeleted())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
