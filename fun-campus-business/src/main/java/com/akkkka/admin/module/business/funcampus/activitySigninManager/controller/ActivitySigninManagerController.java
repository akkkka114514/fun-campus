package com.akkkka.admin.module.business.funcampus.activitySigninManager.controller;

import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerAddForm;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerQueryForm;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.form.ActivitySigninManagerUpdateForm;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.domain.vo.ActivitySigninManagerVO;
import com.akkkka.admin.module.business.funcampus.activitySigninManager.service.ActivitySigninManagerService;
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
 * 活动签到管理员 Controller
 *
 * @Author akkkka114514
 * @Date 2026-02-18 15:10:19
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动签到管理员")
public class ActivitySigninManagerController {

    @Resource
    private ActivitySigninManagerService activitySigninManagerService;


}
