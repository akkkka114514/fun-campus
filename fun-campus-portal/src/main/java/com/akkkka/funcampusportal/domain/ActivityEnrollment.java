package com.akkkka.funcampusnotice.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.akkkka.common.core.annotation.Excel;
import com.akkkka.common.core.web.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 activity_enrollment
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public class ActivityEnrollment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String activityUid;

    /** $column.columnComment */
    private String userUid;

    /** 1-》是，0-》否 */
    @Excel(name = "1-》是，0-》否")
    private Integer signInStatus;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer deleted;

    public void setActivityUid(String activityUid) 
    {
        this.activityUid = activityUid;
    }

    public String getActivityUid() 
    {
        return activityUid;
    }

    public void setUserUid(String userUid) 
    {
        this.userUid = userUid;
    }

    public String getUserUid() 
    {
        return userUid;
    }

    public void setSignInStatus(Integer signInStatus) 
    {
        this.signInStatus = signInStatus;
    }

    public Integer getSignInStatus() 
    {
        return signInStatus;
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
            .append("activityUid", getActivityUid())
            .append("userUid", getUserUid())
            .append("signInStatus", getSignInStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deleted", getDeleted())
            .toString();
    }
}
