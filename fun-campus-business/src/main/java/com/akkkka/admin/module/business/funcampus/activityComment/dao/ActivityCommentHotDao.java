package com.akkkka.admin.module.business.funcampus.activityComment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.entity.ActivityCommentHotEntity;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.vo.ActivityCommentHotVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动评论热度 Dao
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityCommentHotDao extends BaseMapper<ActivityCommentHotEntity> {

    /**
     * 点赞评论（插入或更新热度）
     */
    void likeComment(@Param("commentId") Long commentId);

    /**
     * 撤销点赞评论（热度-1，热度归零时删除记录）
     */
    void unlikeComment(@Param("commentId") Long commentId);

    /**
     * 分页查询热门评论
     */
    List<ActivityCommentHotVO> queryHotCommentsByActivityId(Page<?> page, @Param("activityId") Long activityId);

}
