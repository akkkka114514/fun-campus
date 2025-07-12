package com.akkkka.funcampusnotice.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.akkkka.common.core.annotation.Excel;
import com.akkkka.common.core.web.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 organizer_user
 * 
 * @author akkkka
 * @date 2025-07-11
 */
public class OrganizerUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String uid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String name;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String schoolUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String collegeUid;

    /** 0-》否，1-》是 */
    @Excel(name = "0-》否，1-》是")
    private Integer deleted;

    public void setUid(String uid) 
    {
        this.uid = uid;
    }

    public String getUid() 
    {
        return uid;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setSchoolUid(String schoolUid) 
    {
        this.schoolUid = schoolUid;
    }

    public String getSchoolUid() 
    {
        return schoolUid;
    }

    public void setCollegeUid(String collegeUid) 
    {
        this.collegeUid = collegeUid;
    }

    public String getCollegeUid() 
    {
        return collegeUid;
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
            .append("name", getName())
            .append("schoolUid", getSchoolUid())
            .append("collegeUid", getCollegeUid())
            .append("deleted", getDeleted())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
