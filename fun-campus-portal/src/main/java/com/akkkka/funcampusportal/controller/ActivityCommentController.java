package com.akkkka.funcampusportal.controller;

import com.akkkka.funcampusportal.domain.ActivityComment;
import com.akkkka.funcampusportal.service.IActivityCommentService;
import com.akkkka.funcampusportal.util.ParamCheckUtil;
import com.akkkka.funcampusportal.vo.ActivityCommentRootVO;
import io.swagger.models.auth.In;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author: akkkka114514
 * @create: 2025-04-25 14:20
 * @description:
 */
@RestController
@RequestMapping("/activity/comment")
@Tag(name = "activity comment")
public class ActivityCommentController {

    @Resource
    private IActivityCommentService activityCommentService;

    @PostMapping("/add")
    @Operation(description = "add")
    public void add(@Valid @RequestBody ActivityComment activityComment){
        activityCommentService.save(activityComment);
    }

    @GetMapping("/get")
    @Operation(description = "get")
    public List<ActivityCommentRootVO> getByActivityId(@RequestParam Integer activityId){
        ParamCheckUtil.checkPositiveInteger(activityId);
        return activityCommentService.getByActivityId(activityId);
    }

    @PostMapping("/update")
    @Operation(description = "update")
    public void update(@Valid @RequestBody ActivityComment activityComment){
        activityCommentService.updateById(activityComment);
    }

    @GetMapping("/delete")
    @Operation(description = "delete")
    public void delete(@RequestParam("commentId")Integer id){
        activityCommentService.deleteComment(id);
    }

}
