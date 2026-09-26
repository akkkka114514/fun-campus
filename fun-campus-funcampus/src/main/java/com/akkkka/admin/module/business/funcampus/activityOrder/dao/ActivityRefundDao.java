package com.akkkka.admin.module.business.funcampus.activityOrder.dao;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityRefundEntity;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.form.ActivityRefundQueryForm;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.vo.ActivityRefundVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 活动报名退款记录 Dao
 *
 * @Author akkkka114514
 * @Date 2026-09-24
 * @Copyright akkkka114514
 */

@Mapper
public interface ActivityRefundDao extends BaseMapper<ActivityRefundEntity> {

    /**
     * 管理端分页查询退款单（联表带出订单号/活动标题/用户名）
     *
     * @param page      分页参数
     * @param queryForm 查询条件（状态/活动/用户/关键词/时间范围）
     * @return 退款单列表
     */
    List<ActivityRefundVO> queryPageForAdmin(Page page, @Param("queryForm") ActivityRefundQueryForm queryForm);

}
