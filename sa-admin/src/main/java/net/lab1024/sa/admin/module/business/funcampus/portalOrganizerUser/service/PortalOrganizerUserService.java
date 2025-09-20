package net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.dao.PortalOrganizerUserDao;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.entity.PortalOrganizerUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserAddForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.form.PortalOrganizerUserUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.portalOrganizerUser.domain.vo.PortalOrganizerUserVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 组织账号运营者 Service
 *
 * @Author akkkka114514
 * @Date 2025-09-19 10:12:39
 * @Copyright akkkka114514
 */

@Service
public class PortalOrganizerUserService {

    @Resource
    private PortalOrganizerUserDao portalOrganizerUserDao;

    /**
     * 分页查询
     */
    public PageResult<PortalOrganizerUserVO> queryPage(PortalOrganizerUserQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<PortalOrganizerUserVO> list = portalOrganizerUserDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(PortalOrganizerUserAddForm addForm) {
        PortalOrganizerUserEntity portalOrganizerUserEntity = SmartBeanUtil.copy(addForm, PortalOrganizerUserEntity.class);
        portalOrganizerUserDao.insert(portalOrganizerUserEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(PortalOrganizerUserUpdateForm updateForm) {
        PortalOrganizerUserEntity portalOrganizerUserEntity = SmartBeanUtil.copy(updateForm, PortalOrganizerUserEntity.class);
        portalOrganizerUserDao.updateById(portalOrganizerUserEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        portalOrganizerUserDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        portalOrganizerUserDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
