package com.akkkka.admin.module.business.funcampus.activityReviewLog.domain.dto;


import lombok.Data;
import com.akkkka.admin.module.business.funcampus.activityEnrollment.domain.entity.ActivityEnrollmentEntity;

import java.util.LinkedList;

/**
 * author:akkkka114514
 * create at 2026-03-19 14:39
 * 用于把enroll review中的人员更改转换成添加列表和删除列表
 */
@Data
public class EnrollersChangeDTO {
    private final LinkedList<ActivityEnrollmentEntity> delList = new LinkedList<>();
    private final LinkedList<ActivityEnrollmentEntity> addList = new LinkedList<>();

}
