package com.akkkka.admin.module.business.funcampus.activityOrder.dao;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityOrderQueryForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityOrderVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 活动报名订单 Dao
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityOrderDao extends BaseMapper<ActivityOrderEntity> {

    /**
     * 分页查询我的订单（联表带出活动标题）
     *
     * @param page     分页参数
     * @param queryForm 查询条件（状态/活动id）
     * @param userId   当前登录用户id（只查自己的订单）
     * @return 订单列表
     */
    List<ActivityOrderVO> queryPage(Page page, @Param("queryForm") ActivityOrderQueryForm queryForm, @Param("userId") Long userId);

    /**
     * 管理端分页查询订单（不限定用户；支持状态/活动/用户/关键词/时间范围筛选，联表带出活动标题与下单用户名）
     *
     * @param page      分页参数
     * @param queryForm 查询条件
     * @return 订单列表
     */
    List<ActivityOrderVO> queryPageForAdmin(Page page, @Param("queryForm") ActivityOrderQueryForm queryForm);

}
