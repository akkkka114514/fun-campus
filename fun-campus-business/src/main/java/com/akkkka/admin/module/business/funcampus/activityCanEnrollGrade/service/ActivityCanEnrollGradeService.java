package com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.admin.module.business.funcampus.gradeInfo.service.GradeInfoService;
import com.akkkka.admin.module.business.funcampus.gradeInfo.service.GradeInfoValidator;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.domain.IdNameVO;
import com.akkkka.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.dao.ActivityCanEnrollGradeDao;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeAddForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.form.ActivityCanEnrollGradeUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.vo.ActivityCanEnrollGradeVO;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.manager.ActivityCanEnrollGradeManager;
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
 * 活动能报名的年级 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-13 15:42:17
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class ActivityCanEnrollGradeService {
    private final ActivityCanEnrollGradeManager canEnrollGradeManager;
    private final TransactionTemplate transactionTemplate;
    private final GradeInfoValidator gradeValidator;
    private final GradeInfoService gradeInfoService;

    public List<IdNameVO> getIdNameByActivityId(Long activityId){
        List<ActivityCanEnrollGradeEntity> list = canEnrollGradeManager.list(
                Wrappers.lambdaQuery(ActivityCanEnrollGradeEntity.class)
                        .eq(ActivityCanEnrollGradeEntity::getActivityId,activityId)
                        .eq(ActivityCanEnrollGradeEntity::getDeletedFlag,false)
                        .select(ActivityCanEnrollGradeEntity::getCanEnrollGrade)
        );
        if(list.isEmpty()){
            return new ArrayList<>();
        }
        List<IdNameVO> result = new LinkedList<>();
        for(ActivityCanEnrollGradeEntity e:list){
            IdNameVO vo =  new IdNameVO();
            vo.setId(e.getId());
            vo.setName(gradeInfoService.getNameById(e.getId()));

            result.add(vo);
        }
        return result;
    }

    public List<Long> getGradeIdByActivityId(Long activityId){
        LambdaQueryWrapper<ActivityCanEnrollGradeEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ActivityCanEnrollGradeEntity::getActivityId,activityId)
            .eq(ActivityCanEnrollGradeEntity::getDeletedFlag,false)
            .select(ActivityCanEnrollGradeEntity::getCanEnrollGrade);
        List<ActivityCanEnrollGradeEntity> list = canEnrollGradeManager.list(qw);
        if(list==null || list.isEmpty()){
            return null;
        }
        return list
                .stream()
                .map(ActivityCanEnrollGradeEntity::getCanEnrollGrade)
                .toList();
    }

    public void doSaveBatchTransaction(List<Long> ids,Long activityId){
        if(ids.isEmpty()){
            return;
        }
        gradeValidator.validateGradeIds(ids);
        List<ActivityCanEnrollGradeEntity> list = buildCanEnrollGrade(ids,activityId);
        transactionTemplate.executeWithoutResult(status -> {
            if(!canEnrollGradeManager.saveBatch(list)){
                log.warn("事务失败：插入能报名的年级失败：List<ActivityCanEnrollGradeEntity>={}",list);
                status.setRollbackOnly();
            }
        });
    }
    public List<ActivityCanEnrollGradeEntity> buildCanEnrollGrade(List<Long> ids,Long activityId){
        List<ActivityCanEnrollGradeEntity> gradeList=new ArrayList<>();
        ids.forEach((id)-> {
            ActivityCanEnrollGradeEntity grade = new ActivityCanEnrollGradeEntity();
            grade.setId(null);
            grade.setActivityId(activityId);
            grade.setCanEnrollGrade(id);
            grade.setDeletedFlag(false);
            grade.setCreateTime(LocalDateTime.now());
            grade.setUpdateTime(LocalDateTime.now());
            gradeList.add(grade);
        });
        return gradeList;
    }
    //量不大，直接删掉原来的再添加现在的
    public void doUpdateBatchTransaction(List<Long> gradeIds,Long activityId){
        if(Objects.isNull(gradeIds)||gradeIds.isEmpty()){
            return;
        }
        gradeValidator.validateGradeIds(gradeIds);
        doDeleteBatchTransaction(activityId);
        doSaveBatchTransaction(gradeIds,activityId);
    }
    public void doDeleteBatchTransaction(Long activityId){
        transactionTemplate.executeWithoutResult(status -> {
            try {
                LambdaQueryWrapper<ActivityCanEnrollGradeEntity> qw=
                        new LambdaQueryWrapper<>();
                qw.select(ActivityCanEnrollGradeEntity::getId)
                        .eq(ActivityCanEnrollGradeEntity::getActivityId, activityId)
                        .eq(ActivityCanEnrollGradeEntity::getDeletedFlag,false);
                List<ActivityCanEnrollGradeEntity> list = canEnrollGradeManager.list(qw);
                for(ActivityCanEnrollGradeEntity e:list){
                    e.setDeletedFlag(true);
                }
                if(!canEnrollGradeManager.updateBatchById(list)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.error("doDeleteBatchTransaction 事务失败回滚：activityId={}",activityId,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR,e);
            }
        });

    }
}
