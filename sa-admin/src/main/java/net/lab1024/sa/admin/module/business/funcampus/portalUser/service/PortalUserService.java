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
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

/**
 * 前端用户 Service
 *
 * @Author akkkka114514
 * @Date 2025-10-09 16:29:35
 * @Copyright akkkka114514
 */
@Slf4j
@Service
public class PortalUserService {

    @Resource
    private PortalUserDao portalUserDao;

    /**
     * 分页查询
     */
    public PageResult<PortalUserVO> queryPage(PortalUserQueryForm queryForm) {
        log.info("PortalUserService.queryPage param: {}", queryForm);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<PortalUserVO> list = portalUserDao.queryPage(page, queryForm);
        log.info("PortalUserService.queryPage result: count={}", list.size());
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(PortalUserAddForm addForm) {
        log.info("PortalUserService.add param: {}", addForm);
        PortalUserEntity portalUserEntity = SmartBeanUtil.copy(addForm, PortalUserEntity.class);
        int result = portalUserDao.insert(portalUserEntity);
        log.info("PortalUserService.add result: inserted={} records", result);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(PortalUserUpdateForm updateForm) {
        log.info("PortalUserService.update param: {}", updateForm);
        PortalUserEntity portalUserEntity = SmartBeanUtil.copy(updateForm, PortalUserEntity.class);
        int result = portalUserDao.updateById(portalUserEntity);
        log.info("PortalUserService.update result: updated={} records", result);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        log.info("PortalUserService.batchDelete param: idListSize={}", idList != null ? idList.size() : 0);
        if (CollectionUtils.isEmpty(idList)){
            log.info("PortalUserService.batchDelete skipped: empty idList");
            return ResponseDTO.ok();
        }

        int result = portalUserDao.batchUpdateDeleted(idList, true);
        log.info("PortalUserService.batchDelete result: updated={} records", result);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        log.info("PortalUserService.delete param: id={}", id);
        if (null == id){
            log.info("PortalUserService.delete skipped: null id");
            return ResponseDTO.ok();
        }

        Long result = portalUserDao.updateDeleted(id, true);
        log.info("PortalUserService.delete result: updated id={} ", result);
        return ResponseDTO.ok();
    }
}