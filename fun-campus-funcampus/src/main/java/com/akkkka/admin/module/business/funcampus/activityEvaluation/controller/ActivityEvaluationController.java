package com.akkkka.admin.module.business.funcampus.activityEvaluation.controller;

import java.util.List;

import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.form.ActivityEvaluationQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.form.ActivityEvaluationSubmitForm;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.form.MyEvaluationQueryForm;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.vo.ActivityEvaluationVO;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.domain.vo.PendingEvaluationVO;
import com.akkkka.admin.module.business.funcampus.activityEvaluation.service.ActivityEvaluationService;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 活动评价 Controller（门户端）
 *
 * @Author akkkka114514
 * @Date 2026-09-25
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动评价（门户）")
@RequestMapping("portal")
public class ActivityEvaluationController {

    @Resource
    private ActivityEvaluationService activityEvaluationService;

    @Operation(summary = "提交活动评价（活动已结束且已签到） @author akkkka114514")
    @PostMapping("/activityEvaluation/submit")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<String> submit(@RequestBody @Valid ActivityEvaluationSubmitForm submitForm) {
        activityEvaluationService.submit(submitForm);
        return ResponseDTO.ok();
    }

    @Operation(summary = "某活动的评价分页列表 @author akkkka114514")
    @PostMapping("/activityEvaluation/queryByActivity")
    public ResponseDTO<PageResult<ActivityEvaluationVO>> queryByActivity(
            @RequestBody @Valid ActivityEvaluationQueryForm queryForm) {
        return ResponseDTO.ok(activityEvaluationService.queryByActivity(queryForm));
    }

    @Operation(summary = "我的评价分页列表 @author akkkka114514")
    @PostMapping("/activityEvaluation/queryMy")
    public ResponseDTO<PageResult<ActivityEvaluationVO>> queryMy(@RequestBody @Valid MyEvaluationQueryForm queryForm) {
        return ResponseDTO.ok(activityEvaluationService.queryMy(queryForm));
    }

    @Operation(summary = "我对某活动的评价（未评价返回空） @author akkkka114514")
    @GetMapping("/activityEvaluation/mine")
    public ResponseDTO<ActivityEvaluationVO> mine(@RequestParam Long activityId) {
        return ResponseDTO.ok(activityEvaluationService.myEvaluation(activityId));
    }

    @Operation(summary = "待评价活动列表（已报名+已签到+已结束） @author akkkka114514")
    @GetMapping("/activityEvaluation/pending/list")
    public ResponseDTO<List<PendingEvaluationVO>> pendingList() {
        return ResponseDTO.ok(activityEvaluationService.queryPendingList());
    }

    @Operation(summary = "待评价数量 @author akkkka114514")
    @GetMapping("/activityEvaluation/pending/count")
    public ResponseDTO<Long> pendingCount() {
        return ResponseDTO.ok(activityEvaluationService.countPending());
    }
}
