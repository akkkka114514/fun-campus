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
import org.apache.ibatis.annotations.Update;

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
     * 抢占式插入报名记录（报名幂等核心之一）：INSERT IGNORE
     * <p>
     * activity_enrollment 以 (activity_id, user_id) 为复合主键；
     * 主键冲突（并发重复报名 / 已有记录含软删）时不报错、返回 0，由调用方决定走复活还是判重复。
     *
     * @return 1=本次调用插入成功（抢到报名权）；0=记录已存在（含软删）
     */
    @Insert("INSERT IGNORE INTO activity_enrollment (activity_id, user_id, sign_in_status, sign_out_status, create_time, update_time, deleted_flag) "
            + "VALUES (#{activityId}, #{userId}, false, false, NOW(), NOW(), false)")
    int insertIgnoreEnrollment(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 复活已取消（逻辑删除）的报名记录（报名幂等核心之二）：条件更新
     * <p>
     * 仅 deleted_flag = true 时生效（CAS 风格）：并发重复报名时只有一个请求能复活成功，
     * 失败方据此判定「请勿重复报名」，并重置签到/签退状态。
     *
     * @return 1=本次调用完成复活（抢到报名权）；0=无软删记录（有效报名已存在或从未报名）
     */
    @Update("UPDATE activity_enrollment SET deleted_flag = false, sign_in_status = false, sign_out_status = false, update_time = NOW() "
            + "WHERE activity_id = #{activityId} AND user_id = #{userId} AND deleted_flag = true")
    int reviveEnrollment(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("activityId")Long activityId,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

}