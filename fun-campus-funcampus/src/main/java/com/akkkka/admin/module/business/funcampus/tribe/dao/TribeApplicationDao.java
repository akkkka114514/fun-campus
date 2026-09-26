package com.akkkka.admin.module.business.funcampus.tribe.dao;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.tribe.domain.entity.TribeApplicationEntity;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeApplicationQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeApplicationVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 部落加入申请 Dao
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@Mapper
public interface TribeApplicationDao extends BaseMapper<TribeApplicationEntity> {

    /**
     * 分页 查询申请（管理端，联表带出部落名）
     */
    List<TribeApplicationVO> queryPage(Page<?> page, @Param("queryForm") TribeApplicationQueryForm queryForm);

    /**
     * 查询我的申请列表（门户，联表带出部落名）
     */
    List<TribeApplicationVO> queryMyList(@Param("portalUserId") Long portalUserId);

}
