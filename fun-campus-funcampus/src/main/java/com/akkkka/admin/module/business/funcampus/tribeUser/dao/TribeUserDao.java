package com.akkkka.admin.module.business.funcampus.tribeUser.dao;

import java.util.List;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.entity.TribeUserEntity;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.form.TribeUserQueryForm;
import com.akkkka.admin.module.business.funcampus.tribeUser.domain.vo.TribeUserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 参与部落的用户 Dao
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:42:50
 * @Copyright akkkka114514
 */

@Mapper
public interface TribeUserDao extends BaseMapper<TribeUserEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<TribeUserVO> queryPage(Page page, @Param("queryForm") TribeUserQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);


}
