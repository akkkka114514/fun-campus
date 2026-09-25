package com.akkkka.admin.module.business.funcampus.activityComment.controller;

import com.akkkka.admin.module.business.funcampus.activityComment.domain.form.ActivityCommentAddForm;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.form.ActivityCommentQueryForm;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.vo.ActivityCommentHotVO;
import com.akkkka.admin.module.business.funcampus.activityComment.domain.vo.ActivityCommentVO;
import com.akkkka.admin.module.business.funcampus.activityComment.service.ActivityCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akkkka.common.domain.PageResult;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 活动评论 Controller
 *
 * @Author akkkka114514
 * @Date 2026-08-30
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动评论")
@RequestMapping("portal")
public class ActivityCommentController {

    @Resource
    private ActivityCommentService activityCommentService;

    @Operation(summary = "发表评论 @author akkkka114514")
    @PostMapping("/activity/comment/add")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<Void> addComment(@RequestBody @Valid ActivityCommentAddForm form) {
        activityCommentService.addComment(form);
        return ResponseDTO.ok();
    }

    @Operation(summary = "删除评论 @author akkkka114514")
    @PostMapping("/activity/comment/delete/{commentId}")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<Void> deleteComment(@PathVariable Long commentId) {
        activityCommentService.deleteComment(commentId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "点赞评论 @author akkkka114514")
    @PostMapping("/activity/comment/like/{commentId}")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<Void> likeComment(@PathVariable Long commentId) {
        activityCommentService.likeComment(commentId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "撤销点赞评论 @author akkkka114514")
    @PostMapping("/activity/comment/unlike/{commentId}")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<Void> unlikeComment(@PathVariable Long commentId) {
        activityCommentService.unlikeComment(commentId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "热门评论排行 @author akkkka114514")
    @GetMapping("/activity/comment/hot")
    public ResponseDTO<PageResult<ActivityCommentHotVO>> hotComments(
            @RequestParam Long activityId,
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize) {
        Page<?> page = new Page<>(pageNum, pageSize);
        return ResponseDTO.ok(activityCommentService.queryHotComments(activityId, page));
    }

    @Operation(summary = "评论分页查询 @author akkkka114514")
    @PostMapping("/activity/comment/query")
    public ResponseDTO<PageResult<ActivityCommentVO>> queryComments(@RequestBody @Valid ActivityCommentQueryForm queryForm) {
        return ResponseDTO.ok(activityCommentService.queryComments(queryForm));
    }
}
