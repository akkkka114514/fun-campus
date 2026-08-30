package com.akkkka.admin.module.business.funcampus.activityComment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.entity.ActivityCommentEntity;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.vo.ActivityCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动评论 Dao
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityCommentDao extends BaseMapper<ActivityCommentEntity> {

    /**
     * 分页查询活动的根评论
     */
    List<ActivityCommentVO> queryPageByActivityId(Page<?> page, @Param("activityId") Long activityId);

    /**
     * 根据根评论id查询子评论
     */
    List<ActivityCommentVO> queryByRootId(@Param("rootId") Long rootId);

}
