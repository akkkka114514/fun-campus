package com.akkkka.admin.module.business.funcampus.activityEnrollment.dao;

import java.util.List;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 活动报名关系 Dao
 *
 * @Author akkkka114514
 * @Date 2025-10-02 13:54:42
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityEnrollmentDao extends MppBaseMapper<ActivityEnrollmentEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ActivityEnrollmentVO> queryPage(Page<?> page, @Param("queryForm") ActivityEnrollmentQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("activityId")Long activityId,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

}