package com.akkkka.funcampusnotice.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.akkkka.common.core.annotation.Excel;
import com.akkkka.common.core.web.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 activity_enroll_num
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public class ActivityEnrollNum extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String activityUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long enrollNum;

    public void setActivityUid(String activityUid) 
    {
        this.activityUid = activityUid;
    }

    public String getActivityUid() 
    {
        return activityUid;
    }

    public void setEnrollNum(Long enrollNum) 
    {
        this.enrollNum = enrollNum;
    }

    public Long getEnrollNum() 
    {
        return enrollNum;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("activityUid", getActivityUid())
            .append("enrollNum", getEnrollNum())
            .toString();
    }
}
