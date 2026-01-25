package net.lab1024.sa.admin.module.business.funcampus.tribe.service;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.domain.entity.PortalUserEntity;
import net.lab1024.sa.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import net.lab1024.sa.admin.module.business.funcampus.tribe.dao.TribeDao;
import net.lab1024.sa.admin.module.business.funcampus.tribe.domain.entity.TribeEntity;
import net.lab1024.sa.admin.module.business.funcampus.tribe.domain.form.TribeAddForm;
import net.lab1024.sa.admin.module.business.funcampus.tribe.domain.form.TribeQueryForm;
import net.lab1024.sa.admin.module.business.funcampus.tribe.domain.form.TribeUpdateForm;
import net.lab1024.sa.admin.module.business.funcampus.tribe.domain.vo.SimpleTribeVO;
import net.lab1024.sa.admin.module.business.funcampus.tribe.domain.vo.TribeVO;
import net.lab1024.sa.base.common.domain.RequestUser;
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
 * 部落 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:27:15
 * @Copyright akkkka114514
 */

@Service
@Slf4j
public class TribeService {

    @Resource
    private TribeDao tribeDao;
    @Resource
    private PortalUserManager portalUserManager;

    /**
     * 分页查询
     */
    public PageResult<TribeVO> queryPage(TribeQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<TribeVO> list = tribeDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(TribeAddForm addForm) {
        TribeEntity tribeEntity = SmartBeanUtil.copy(addForm, TribeEntity.class);
        tribeDao.insert(tribeEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(TribeUpdateForm updateForm) {
        TribeEntity tribeEntity = SmartBeanUtil.copy(updateForm, TribeEntity.class);
        tribeDao.updateById(tribeEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        tribeDao.batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        tribeDao.updateDeleted(id, true);
        return ResponseDTO.ok();
    }

    /**
    * <p>
    * description: 供选择奇选择的部落列表，只提供部落的id和名称
    * </p>
    *
    * @param keyword
    * @return:
    * @author: akkkka114514
    * @date: 13:04:26 2026-01-25
    */
    public List<SimpleTribeVO> querySimpleList(String keyword){
        Long userId=SmartRequestUtil.getRequestUserId();
        PortalUserEntity portalUserEntity=portalUserManager.getById(userId);
        if(portalUserEntity==null||portalUserEntity.getDeletedFlag()||portalUserEntity.getDisableFlag()){
            log.error("由smartRequestUtil获得的用户不存在或者被禁用");
            return new ArrayList<>();
        }
        Long schoolId=portalUserEntity.getSchoolId();
        return tribeDao.querySimpleList(schoolId, keyword);
    }
}
