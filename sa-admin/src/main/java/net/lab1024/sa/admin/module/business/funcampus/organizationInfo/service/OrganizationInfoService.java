package net.lab1024.sa.admin.module.business.funcampus.organizationInfo.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.dao.OrganizationInfoDao;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.entity.OrganizationInfoEntity;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoAddForm;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.form.OrganizationInfoUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.vo.OrganizationInfoVO;
import net.lab1024.sa.admin.module.business.funcampus.organizationInfo.domain.vo.SimpleOrganizationInfoVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 各学校组织信息 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:24:05
 * @Copyright akkkka114514
 */

@Service
public class OrganizationInfoService {

    @Resource
    private OrganizationInfoDao organizationInfoDao;

    /**
     * 分页查询
     */
    public PageResult<OrganizationInfoVO> queryPage(OrganizationInfoQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<OrganizationInfoVO> list = organizationInfoDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(OrganizationInfoAddForm addForm) {
        OrganizationInfoEntity organizationInfoEntity = SmartBeanUtil.copy(addForm, OrganizationInfoEntity.class);
        organizationInfoDao.insert(organizationInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(OrganizationInfoUpdateForm updateForm) {
        OrganizationInfoEntity organizationInfoEntity = SmartBeanUtil.copy(updateForm, OrganizationInfoEntity.class);
        organizationInfoDao.updateById(organizationInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        organizationInfoDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        organizationInfoDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }

    public List<SimpleOrganizationInfoVO> getOrganizationInfoByUserId() {
        Long userId =SmartRequestUtil.getRequestUserId();

        return organizationInfoDao.getOrganizationInfoByUserId(userId);
    }
}
