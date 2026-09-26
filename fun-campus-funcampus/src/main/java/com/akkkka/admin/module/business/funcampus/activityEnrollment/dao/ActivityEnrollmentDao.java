package com.akkkka.admin.module.business.funcampus.activityEnrollment.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.ActivityEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.form.MyEnrollmentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.ActivityEnrollmentVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.MyEnrollmentVO;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.vo.PendingSignVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Insert;
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
public interface ActivityEnrollmentDao extends BaseMapper<ActivityEnrollmentEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<ActivityEnrollmentVO> queryPage(Page<?> page, @Param("queryForm") ActivityEnrollmentQueryForm queryForm);

    /**
     * 分页 查询我的报名（联表带出活动与时间表信息）
     *
     * @param page
     * @param userId
     * @param queryForm
     * @return
     */
    List<MyEnrollmentVO> queryMyEnrollment(Page<?> page, @Param("userId") Long userId, @Param("queryForm") MyEnrollmentQueryForm queryForm);

    /**
     * 查询待签到活动列表（已报名 + 未签到 + 当前处于签到时间窗口内）
     *
     * @param userId
     * @return
     */
    List<PendingSignVO> queryPendingSignInList(@Param("userId") Long userId);

    /**
     * 查询待签退活动列表（已报名 + 已签到 + 需签退 + 未签退 + 当前处于签退时间窗口内）
     *
     * @param userId
     * @return
     */
    List<PendingSignVO> queryPendingSignOutList(@Param("userId") Long userId);

    /**
     * 报名记录 upsert：不存在则插入；已存在软删记录（如取消报名后重新报名）则复活并重置签到/签退状态
     * <p>
     * activity_enrollment 以 (activity_id, user_id) 为复合主键，取消报名为逻辑删除，
     * 直接 insert 会主键冲突，因此统一走 ON DUPLICATE KEY UPDATE。
     */
    @Insert("INSERT INTO activity_enrollment (activity_id, user_id, sign_in_status, sign_out_status, create_time, update_time, deleted_flag) "
            + "VALUES (#{activityId}, #{userId}, false, false, NOW(), NOW(), false) "
            + "ON DUPLICATE KEY UPDATE deleted_flag = false, sign_in_status = false, sign_out_status = false, update_time = NOW()")
    int upsertEnrollment(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("activityId")Long activityId,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

}