package com.akkkka.funcampusnotice.controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.akkkka.common.log.annotation.Log;
import com.akkkka.common.log.enums.BusinessType;
import com.akkkka.common.security.annotation.RequiresPermissions;
import com.akkkka.funcampusnotice.domain.ActivityComment;
import com.akkkka.funcampusnotice.service.IActivityCommentService;
import com.akkkka.common.core.web.controller.BaseController;
import com.akkkka.common.core.web.domain.AjaxResult;
import com.akkkka.common.core.utils.poi.ExcelUtil;
import com.akkkka.common.core.web.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 * 
 * @author akkkka
 * @date 2025-07-11
 */
@RestController
@RequestMapping("/comment")
public class ActivityCommentController extends BaseController
{
    @Autowired
    private IActivityCommentService activityCommentService;

    /**
     * 查询【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:comment:list")
    @GetMapping("/list")
    public TableDataInfo list(ActivityComment activityComment)
    {
        startPage();
        List<ActivityComment> list = activityCommentService.selectActivityCommentList(activityComment);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:comment:export")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ActivityComment activityComment)
    {
        List<ActivityComment> list = activityCommentService.selectActivityCommentList(activityComment);
        ExcelUtil<ActivityComment> util = new ExcelUtil<ActivityComment>(ActivityComment.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @RequiresPermissions("funcampusnotice:comment:query")
    @GetMapping(value = "/{uid}")
    public AjaxResult getInfo(@PathVariable("uid") String uid)
    {
        return success(activityCommentService.selectActivityCommentByUid(uid));
    }

    /**
     * 新增【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:comment:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivityComment activityComment)
    {
        return toAjax(activityCommentService.insertActivityComment(activityComment));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:comment:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivityComment activityComment)
    {
        return toAjax(activityCommentService.updateActivityComment(activityComment));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:comment:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{uids}")
    public AjaxResult remove(@PathVariable String[] uids)
    {
        return toAjax(activityCommentService.deleteActivityCommentByUids(uids));
    }
}
