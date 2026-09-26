package com.akkkka.admin.module.business.funcampus.activityEvaluation.dao;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.entity.ActivityEvaluationEntity;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.vo.ActivityEvaluationVO;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.vo.PendingEvaluationVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 活动评价 Dao
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityEvaluationDao extends BaseMapper<ActivityEvaluationEntity> {

    /**
     * 分页 查询某活动的评价列表（门户活动详情页）
     */
    List<ActivityEvaluationVO> queryByActivity(Page<?> page, @Param("activityId") Long activityId);

    /**
     * 分页 查询我的评价列表（联表带出活动标题/封面）
     */
    List<ActivityEvaluationVO> queryMyPage(Page<?> page, @Param("userId") Long userId);

    /**
     * 查询我的待评价活动列表（已报名+已签到+活动已结束+未评价）
     */
    List<PendingEvaluationVO> queryPendingList(@Param("userId") Long userId);

    /**
     * 统计我的待评价数量
     */
    Long countPending(@Param("userId") Long userId);

}
