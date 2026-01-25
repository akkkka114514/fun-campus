package net.lab1024.sa.admin.module.business.funcampus.organizationInfo.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.entity.OrganizationInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.vo.OrganizationInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.vo.SimpleOrganizationInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 各学校组织信息 Dao
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:24:05
 * @Copyright akkkka114514
 */

@Mapper
public interface OrganizationInfoDao extends BaseMapper<OrganizationInfoEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<OrganizationInfoVO> queryPage(Page page, @Param("queryForm") OrganizationInfoQueryForm queryForm);

    /**
     * 更新删除状态
     */
    long updateDeleted(@Param("id")Long id,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 批量更新删除状态
     */
    void batchUpdateDeleted(@Param("idList")List<Long> idList,@Param("deletedFlag")boolean deletedFlag);

    /**
     * 根据userId查询用户所在学校的所有组织
     */
    List<SimpleOrganizationInfoVO> getOrganizationInfoByUserId(@Param("userId")Long userId);

}
