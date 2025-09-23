package net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.entity.PortalOrganizerUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.vo.PortalOrganizerUserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 组织账号运营者 Dao
 *
 * @Author akkkka114514
 * @Date 2025-09-19 10:12:39
 * @Copyright akkkka114514
 */

@Mapper
public interface PortalOrganizerUserDao extends BaseMapper<PortalOrganizerUserEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<PortalOrganizerUserVO> queryPage(Page<?> page, @Param("queryForm") PortalOrganizerUserQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

    long updateDisableFlag(@Param("id")Long id,@Param("disabledFlag")boolean disabledFlag);

    void batchUpdateDisableFlag(@Param("idList")List<Long> idList,@Param("disabledFlag")boolean disabledFlag);
}
