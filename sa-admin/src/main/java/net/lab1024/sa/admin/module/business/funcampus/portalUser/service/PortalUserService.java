package net.lab1024.sa.admin.module.business.funcampus.portalUser.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.dao.PortalUserDao;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserAddForm;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.form.PortalUserUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.vo.PortalUserVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 前端用户 Service
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */

@Service
public class PortalUserService {

    @Resource
    private PortalUserDao portalUserDao;

    /**
     * 分页查询
     */
    public PageResult<PortalUserVO> queryPage(PortalUserQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<PortalUserVO> list = portalUserDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(PortalUserAddForm addForm) {
        PortalUserEntity portalUserEntity = SmartBeanUtil.copy(addForm, PortalUserEntity.class);
        portalUserDao.insert(portalUserEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(PortalUserUpdateForm updateForm) {
        PortalUserEntity portalUserEntity = SmartBeanUtil.copy(updateForm, PortalUserEntity.class);
        portalUserDao.updateById(portalUserEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        portalUserDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        portalUserDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
