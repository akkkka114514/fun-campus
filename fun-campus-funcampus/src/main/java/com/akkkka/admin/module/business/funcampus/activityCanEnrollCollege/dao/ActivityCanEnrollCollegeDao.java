package com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.dao;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.vo.ActivityCanEnrollCollegeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 活动能报名的学院 Dao
 *
 * @Author akkkka114514
 * @Date 2026-01-01 20:24:18
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityCanEnrollCollegeDao extends BaseMapper<ActivityCanEnrollCollegeEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ActivityCanEnrollCollegeVO> queryPage(Page page, @Param("queryForm") ActivityCanEnrollCollegeQueryForm queryForm);

    /**
     * 更新删除状态
     */
    int updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    int batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

}
