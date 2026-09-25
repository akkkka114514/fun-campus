package com.akkkka.admin.module.business.funcampus.activityFavorite.controller;

import com.akkkka.admin.module.business.funcampus.activityFavorite.service.ActivityFavoriteService;
import com.akkkka.common.domain.ResponseDTO;
import com.akkkka.module.support.repeatsubmit.annoation.RepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 活动收藏 Controller
 *
 * @Author akkkka114514
 * @Date 2026-08-16
 * @Copyright akkkka114514
 */

@RestController
@Tag(name = "活动收藏")
@RequestMapping("portal")
public class ActivityFavoriteController {

    @Resource
    private ActivityFavoriteService activityFavoriteService;

    @Operation(summary = "收藏活动 @author akkkka114514")
    @PostMapping("/activity/favorite/add")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<Void> addFavorite(@RequestBody Long activityId) {
        activityFavoriteService.addFavorite(activityId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "取消收藏活动 @author akkkka114514")
    @PostMapping("/activity/favorite/remove")
    @RepeatSubmit(intervalMilliSecond = 3 * 1000)
    public ResponseDTO<Void> removeFavorite(@RequestBody Long activityId) {
        activityFavoriteService.removeFavorite(activityId);
        return ResponseDTO.ok();
    }

    @Operation(summary = "查询当前用户是否已收藏该活动 @author akkkka114514")
    @GetMapping("/activity/favorite/check")
    public ResponseDTO<Boolean> isFavorited(@RequestParam Long activityId) {
        return ResponseDTO.ok(activityFavoriteService.isFavorited(activityId));
    }

    @Operation(summary = "查询当前用户收藏的活动id列表 @author akkkka114514")
    @GetMapping("/activity/favorite/list")
    public ResponseDTO<List<Long>> listFavoriteActivityIds() {
        return ResponseDTO.ok(activityFavoriteService.listFavoriteActivityIds());
    }
}
