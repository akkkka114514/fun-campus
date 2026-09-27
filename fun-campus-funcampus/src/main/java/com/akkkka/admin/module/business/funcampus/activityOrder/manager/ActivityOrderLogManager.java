package com.akkkka.admin.module.business.funcampus.activityOrder.manager;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityOrder.dao.ActivityOrderLogDao;
import com.akkkka.admin.module.business.funcampus.activityOrder.domain.entity.ActivityOrderLogEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 活动订单操作记录 Manager
 * <p>
 * 记录只增不改，插入与查询均走本 Manager
 *
 * @Author akkkka114514
 * @Date 2026-09-27
 * @Copyright akkkka114514
 */
@Service
public class ActivityOrderLogManager extends ServiceImpl<ActivityOrderLogDao, ActivityOrderLogEntity> {

    /**
     * 查询订单全部操作记录（按发生时间正序）
     */
    public List<ActivityOrderLogEntity> listByOrderNo(String orderNo) {
        return this.list(
                new LambdaQueryWrapper<ActivityOrderLogEntity>()
                        .eq(ActivityOrderLogEntity::getOrderNo, orderNo)
                        .eq(ActivityOrderLogEntity::getDeletedFlag, false)
                        .orderByAsc(ActivityOrderLogEntity::getId)
        );
    }
}
