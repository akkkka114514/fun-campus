package com.akkkka.admin.module.business.funcampus.activitySigninManager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerAddForm;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerQueryForm;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerUpdateForm;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.vo.ActivitySigninManagerVO;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.manager.ActivitySigninManagerManager;
import com.akkkka.admin.module.business.funcampus.portalUser.manager.PortalUserManager;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.ValidateList;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.dao.ActivitySigninManagerDao;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.entity.ActivitySigninManagerEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import org.springframework.transaction.support.TransactionTemplate;

import static cn.dev33.satoken.SaManager.log;

/**
 * 活动签到管理员 Service
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */

@Service
@AllArgsConstructor
public class ActivitySigninManagerService {
    private PortalUserManager portalUserManager;
    private TransactionTemplate transactionTemplate;
    private ActivitySigninManagerManager signinManagerManager;
    private SignInManagerValidator signInManagerValidator;

    /**
     * 获取活动的签到员ID列表
     */
    public List<Long> getSignInManagerIds(Long activityId) {
        return signinManagerManager.list(
                        Wrappers.lambdaQuery(ActivitySigninManagerEntity.class)
                                .eq(ActivitySigninManagerEntity::getActivityId, activityId)
                                .eq(ActivitySigninManagerEntity::getDeletedFlag, false)
                                .select(ActivitySigninManagerEntity::getPortalUserId)
                ).stream()
                .map(ActivitySigninManagerEntity::getPortalUserId)
                .toList();
    }

    public void doSaveBatchTransaction(List<Long> ids,Long belongToSchoolId,Long activityId){
        signInManagerValidator.validateSignInManagerIds(ids,belongToSchoolId);
        List<ActivitySigninManagerEntity> list = buildSigninManagerList(ids,activityId);
        transactionTemplate.executeWithoutResult(status -> {
            try {
                if(!signinManagerManager.saveBatch(list)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.error("批量插入signinManager，字段校验通过，数据库事务回滚" +
                                ",exception message={}" +
                                ",activity id={}" +
                                ",signin manager ids={}",
                        e.getMessage(),activityId, ids,e);
                status.setRollbackOnly();
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }
        });

    }

    public List<ActivitySigninManagerEntity> buildSigninManagerList(List<Long> ids,Long activityId){
        List<ActivitySigninManagerEntity> result = new ArrayList<>();

        ids.forEach(id -> {
            ActivitySigninManagerEntity signinManager=new ActivitySigninManagerEntity();
            signinManager.setId(null);
            signinManager.setActivityId(activityId);
            signinManager.setPortalUserId(id);
            signinManager.setDeletedFlag(false);
            signinManager.setCreateTime(LocalDateTime.now());
            signinManager.setUpdateTime(LocalDateTime.now());
            signinManager.setUsername(portalUserManager.getById(id).getUsername());

            result.add(signinManager);
        });
        return result;
    }

    //量不大，直接删掉原来的再添加现在的
    public void doUpdateBatchTransaction(List<Long> signInManagerIds,Long schoolId,Long activityId){
        if(Objects.isNull(signInManagerIds)||signInManagerIds.isEmpty()){
            return;
        }
        signInManagerValidator.validateSignInManagerIds(signInManagerIds,schoolId);
        doDeleteBatchTransaction(activityId);
        doSaveBatchTransaction(signInManagerIds,schoolId,activityId);
    }
    public void doDeleteBatchTransaction(Long activityId){
        transactionTemplate.executeWithoutResult(status -> {
            try {
                LambdaQueryWrapper<ActivitySigninManagerEntity> qw=
                        new LambdaQueryWrapper<>();
                qw.select(ActivitySigninManagerEntity::getId)
                        .eq(ActivitySigninManagerEntity::getActivityId, activityId)
                        .eq(ActivitySigninManagerEntity::getDeletedFlag,false);
                List<ActivitySigninManagerEntity> list = signinManagerManager.list(qw);
                for(ActivitySigninManagerEntity e:list){
                    e.setDeletedFlag(true);
                }
                if(!signinManagerManager.updateBatchById(list)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.error("doDeleteBatchTransaction signInManager 事务失败回滚：activityId={}",activityId,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR,e);
            }
        });

    }

    /**
     * 分页查询
     */
    public PageResult<ActivitySigninManagerVO> queryPage(ActivitySigninManagerQueryForm queryForm) {
        Page<ActivitySigninManagerVO> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<ActivitySigninManagerVO> list = signinManagerManager.getBaseMapper().queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ActivitySigninManagerAddForm addForm) {
        ActivitySigninManagerEntity entity = SmartBeanUtil.copy(addForm, ActivitySigninManagerEntity.class);
        entity.setId(null);
        entity.setDeletedFlag(false);
        signinManagerManager.save(entity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     */
    public ResponseDTO<String> update(ActivitySigninManagerUpdateForm updateForm) {
        ActivitySigninManagerEntity entity = SmartBeanUtil.copy(updateForm, ActivitySigninManagerEntity.class);
        signinManagerManager.updateById(entity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(ValidateList<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        signinManagerManager.getBaseMapper().batchUpdateDeleted(idList, true);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (id == null) {
            return ResponseDTO.ok();
        }
        signinManagerManager.getBaseMapper().updateDeleted(id, true);
        return ResponseDTO.ok();
    }
}
