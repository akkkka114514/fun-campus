package com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollGrade.domain.entity.ActivityCanEnrollGradeEntity;
import com.akkkka.admin.module.business.funcampus.activityWithSchedule.domain.form.ActivityWithScheduleAddForm;
import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoService;
import com.akkkka.admin.module.business.funcampus.collegeInfo.service.CollegeInfoValidator;
import com.akkkka.common.code.SystemErrorCode;
import com.akkkka.common.domain.IdNameVO;
import com.akkkka.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.dao.ActivityCanEnrollCollegeDao;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.entity.ActivityCanEnrollCollegeEntity;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeAddForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.form.ActivityCanEnrollCollegeUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.domain.vo.ActivityCanEnrollCollegeVO;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollCollege.manager.ActivityCanEnrollCollegeManager;
import com.akkkka.admin.module.business.funcampus.collegeInfo.manager.CollegeInfoManager;
import com.akkkka.common.util.SmartBeanUtil;
import com.akkkka.common.util.SmartPageUtil;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 活动能报名的学院 Service
 *
 * @Author akkkka114514
 * @Date 2026-01-01 20:24:18
 * @Copyright akkkka114514
 */

@Slf4j
@Service
@AllArgsConstructor
public class ActivityCanEnrollCollegeService {
    private final ActivityCanEnrollCollegeManager canEnrollCollegeManager;
    private final TransactionTemplate transactionTemplate;
    private final CollegeInfoValidator collegeValidator;
    private final CollegeInfoService collegeInfoService;

    public List<IdNameVO> listIdNameByActivityId(Long activityId){
        List<ActivityCanEnrollCollegeEntity> list = canEnrollCollegeManager.list(
                Wrappers.lambdaQuery(ActivityCanEnrollCollegeEntity.class)
                        .eq(ActivityCanEnrollCollegeEntity::getActivityId,activityId)
                        .eq(ActivityCanEnrollCollegeEntity::getDeletedFlag,false)
                        .select(ActivityCanEnrollCollegeEntity::getCanEnrollCollege)
        );
        List<IdNameVO> result=new ArrayList<>();
        if(list.isEmpty()){
            return result;
        }
        for(ActivityCanEnrollCollegeEntity e:list){
            IdNameVO vo = new IdNameVO();
            vo.setId(e.getId());
            vo.setName(collegeInfoService.getNameById(e.getCanEnrollCollege()));
            result.add(vo);
        }
        return result;
    }

    //取出所有canEnrollCollege表的collegeId，然后获取所有college的name
    public List<Long> getCollegeIdsByActivityId(Long activityId){
        LambdaQueryWrapper<ActivityCanEnrollCollegeEntity> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(ActivityCanEnrollCollegeEntity::getActivityId,activityId)
                .eq(ActivityCanEnrollCollegeEntity::getDeletedFlag,false)
                .select(ActivityCanEnrollCollegeEntity::getCanEnrollCollege);
        List<ActivityCanEnrollCollegeEntity> collegeEntities = canEnrollCollegeManager.list(lqw1);
        List<Long> collegeIds = null;
        if(collegeEntities!=null&&!collegeEntities.isEmpty()){
            collegeIds = collegeEntities
                    .stream()
                    .map(ActivityCanEnrollCollegeEntity::getCanEnrollCollege)
                    .toList();
        }
        return collegeIds;
    }
    public void doSaveBatchTransaction(List<Long> ids,Long activityId){
        if(ids.isEmpty()){
            return;
        }
        collegeValidator.validateCollegeIds(ids);
        List<ActivityCanEnrollCollegeEntity> list=buildCanEnrollCollege(ids,activityId);
        transactionTemplate.executeWithoutResult(status -> {
            try{
                if(!canEnrollCollegeManager.saveBatch(list)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.warn("事务失败：插入能报名的学院失败：List<ActivityCanEnrollCollegeEntity>={}",list,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
            }

        });
    }


    public List<ActivityCanEnrollCollegeEntity> buildCanEnrollCollege(List<Long> ids,Long activityId){
        List<ActivityCanEnrollCollegeEntity> collegeList=new ArrayList<>();
        ids.forEach(id-> {
            ActivityCanEnrollCollegeEntity college = new ActivityCanEnrollCollegeEntity();
            college.setId(null);
            college.setActivityId(activityId);
            college.setCanEnrollCollege(id);
            college.setDeletedFlag(false);
            college.setCreateTime(LocalDateTime.now());
            college.setUpdateTime(LocalDateTime.now());
            collegeList.add(college);
        });
        return collegeList;
    }

    //量不大，直接删掉原来的再添加现在的
    public void doUpdateBatchTransaction(List<Long> collegeIds,Long activityId){
        if(Objects.isNull(collegeIds)||collegeIds.isEmpty()){
            return;
        }
        collegeValidator.validateCollegeIds(collegeIds);
        doDeleteBatchTransaction(activityId);
        doSaveBatchTransaction(collegeIds,activityId);
    }
    public void doDeleteBatchTransaction(Long activityId){
        transactionTemplate.executeWithoutResult(status -> {
            try {
                LambdaQueryWrapper<ActivityCanEnrollCollegeEntity> qw=
                        new LambdaQueryWrapper<>();
                qw.select(ActivityCanEnrollCollegeEntity::getId)
                        .eq(ActivityCanEnrollCollegeEntity::getActivityId, activityId)
                        .eq(ActivityCanEnrollCollegeEntity::getDeletedFlag,false);
                List<ActivityCanEnrollCollegeEntity> list = canEnrollCollegeManager.list(qw);
                for(ActivityCanEnrollCollegeEntity e:list){
                    e.setDeletedFlag(true);
                }
                if(!canEnrollCollegeManager.updateBatchById(list)){
                    throw new BusinessException(SystemErrorCode.SYSTEM_ERROR);
                }
            }catch (Exception e){
                log.error("doDeleteBatchTransaction 事务失败回滚：activityId={}",activityId,e);
                throw new BusinessException(SystemErrorCode.SYSTEM_ERROR,e);
            }
        });
    }

}
