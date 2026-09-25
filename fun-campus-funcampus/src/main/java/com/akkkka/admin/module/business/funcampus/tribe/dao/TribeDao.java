package com.akkkka.admin.module.business.funcampus.tribe.dao;

import java.util.List;
import com.akkkka.admin.module.business.funcampus.tribe.domain.entity.TribeEntity;
import com.akkkka.admin.module.business.funcampus.tribe.domain.form.TribeQueryForm;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.SimpleTribeVO;
import com.akkkka.admin.module.business.funcampus.tribe.domain.vo.TribeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 部落 Dao
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@Mapper
public interface TribeDao extends BaseMapper<TribeEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<TribeVO> queryPage(Page page, @Param("queryForm") TribeQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

    // 查询所属学校的满足关键字的部落
    List<SimpleTribeVO> querySimpleList(Long schoolId, String keyword);
}
