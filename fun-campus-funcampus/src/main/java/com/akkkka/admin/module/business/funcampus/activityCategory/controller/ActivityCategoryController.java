package com.akkkka.admin.module.business.funcampus.activityCategory.controller;

import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryAddForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryQueryForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.form.ActivityCategoryUpdateForm;
import com.akkkka.admin.module.business.funcampus.activityCategory.domain.vo.ActivityCategoryVO;
import com.akkkka.admin.module.business.funcampus.activityCategory.service.ActivityCategoryService;
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
 * 活动分类 Controller
 *
 * @Author akkkka114514
 * @Date 2026-01-10 19:46:44
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动分类")
public class ActivityCategoryController {

    @Resource
    private ActivityCategoryService activityCategoryService;

}
