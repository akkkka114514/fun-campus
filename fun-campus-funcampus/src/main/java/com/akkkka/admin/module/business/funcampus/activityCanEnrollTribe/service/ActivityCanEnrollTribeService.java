package com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.admin.module.business.funcampus.tribe.service.TribeService;
import com.akkkka.admin.module.business.funcampus.tribe.service.TribeValidator;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.domain.IdNameVO;
import com.akkkka.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.dao.ActivityCanEnrollTribeDao;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.entity.ActivityCanEnrollTribeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeAddForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.vo.ActivityCanEnrollTribeVO;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.manager.ActivityCanEnrollTribeManager;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 活动能报名的部落 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:18:01
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class ActivityCanEnrollTribeService {
    private final ActivityCanEnrollTribeManager canEnrollTribeManager;
    private final TransactionTemplate transactionTemplate;
    private final TribeValidator tribeValidator;
    private final TribeService tribeService;

    public List<IdNameVO> getIdNameByActivityId(Long activityId){
        List<ActivityCanEnrollTribeEntity> list = canEnrollTribeManager.list(
                Wrappers.lambdaQuery(ActivityCanEnrollTribeEntity.class)
                        .eq(ActivityCanEnrollTribeEntity::getActivityId,activityId)
                        .eq(ActivityCanEnrollTribeEntity::getDeletedFlag,false)
                        .select(ActivityCanEnrollTribeEntity::getId)
                        .select(ActivityCanEnrollTribeEntity::getCanEnrollTribe)
        );
        if(list.isEmpty()){
            return new ArrayList<>();
        }
        List<IdNameVO> result = new LinkedList<>();
        for(ActivityCanEnrollTribeEntity e:list){
            IdNameVO vo = new IdNameVO();
            vo.setId(e.getId());
            vo.setName(tribeService.getNameById(e.getId()));
            result.add(vo);
        }
        return result;
    }

    public List<Long> getTribeIdsByActivityId(Long activityId){
        LambdaQueryWrapper<ActivityCanEnrollTribeEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ActivityCanEnrollTribeEntity::getActivityId,activityId)
            .eq(ActivityCanEnrollTribeEntity::getDeletedFlag,false)
            .select(ActivityCanEnrollTribeEntity::getCanEnrollTribe);
        List<ActivityCanEnrollTribeEntity> list = canEnrollTribeManager.list(qw);
        if(list==null||list.isEmpty()){
            return null;
        }
        return list
                .stream()
                .map(ActivityCanEnrollTribeEntity::getCanEnrollTribe)
                .toList();
    }
    public void doSaveBatchTransaction(List<Long> ids,Long activityId){
        if(ids.isEmpty()){
            return;
        }
        tribeValidator.validateTribeIds(ids);
        List<ActivityCanEnrollTribeEntity> list= buildCanEnrollTribe(ids,activityId);
        transactionTemplate.executeWithoutResult(status -> {
            if(!canEnrollTribeManager.saveBatch(list)){
                log.warn("事务失败：插入能报名的部落失败：List<ActivityCanEnrollTribeEntity>={}",list);
                status.setRollbackOnly();
            }
        });
    }
    public List<ActivityCanEnrollTribeEntity> buildCanEnrollTribe(List<Long> ids,Long activityId){
        List<ActivityCanEnrollTribeEntity> tribeList=new ArrayList<>();
        ids.forEach((id)->{
            ActivityCanEnrollTribeEntity tribe = new ActivityCanEnrollTribeEntity();
            tribe.setId(null);
            tribe.setActivityId(activityId);
            tribe.setCanEnrollTribe(id);
            tribe.setCreateTime(LocalDateTime.now());
            tribe.setUpdateTime(LocalDateTime.now());
            tribe.setDeletedFlag(false);

            tribeList.add(tribe);
        });
        return tribeList;
    }

    //量不大，直接删掉原来的再添加现在的
    public void doUpdateBatchTransaction(List<Long> tribeIds,Long activityId){
        if(Objects.isNull(tribeIds)||tribeIds.isEmpty()){
            return;
        }
        tribeValidator.validateTribeIds(tribeIds);
        doDeleteBatchTransaction(activityId);
        doSaveBatchTransaction(tribeIds,activityId);
    }
    public void doDeleteBatchTransaction(Long activityId){
        transactionTemplate.executeWithoutResult(status -> {
            try {
                LambdaQueryWrapper<ActivityCanEnrollTribeEntity> qw=
                        new LambdaQueryWrapper<>();
                qw.select(ActivityCanEnrollTribeEntity::getId)
                        .eq(ActivityCanEnrollTribeEntity::getActivityId, activityId)
                        .eq(ActivityCanEnrollTribeEntity::getDeletedFlag,false);
                List<ActivityCanEnrollTribeEntity> list = canEnrollTribeManager.list(qw);
                for(ActivityCanEnrollTribeEntity e:list){
                    e.setDeletedFlag(true);
                }
                if(!canEnrollTribeManager.updateBatchById(list)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.error("doDeleteBatchTransaction 事务失败回滚：activityId={}",activityId,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR,e);
            }
        });

    }
}
