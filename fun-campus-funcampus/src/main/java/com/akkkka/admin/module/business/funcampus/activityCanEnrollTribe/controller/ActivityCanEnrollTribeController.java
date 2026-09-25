package com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.controller;

import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeAddForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.form.ActivityCanEnrollTribeUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.domain.vo.ActivityCanEnrollTribeVO;
import com.akkkka.admin.module.business.funcampus.activityCanEnrollTribe.service.ActivityCanEnrollTribeService;
import com.akkkka.common.domain.ValidateList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.common.domain.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 活动能报名的部落 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-15 13:18:01
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动能报名的部落")
public class ActivityCanEnrollTribeController {

    @Resource
    private ActivityCanEnrollTribeService activityCanEnrollTribeService;

}
