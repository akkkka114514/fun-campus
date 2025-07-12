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
import com.akkkka.funcampusnotice.domain.ActivityCommentHot;
import com.akkkka.funcampusnotice.service.IActivityCommentHotService;
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
@RequestMapping("/hot")
public class ActivityCommentHotController extends BaseController
{
    @Autowired
    private IActivityCommentHotService activityCommentHotService;

    /**
     * 查询【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:hot:list")
    @GetMapping("/list")
    public TableDataInfo list(ActivityCommentHot activityCommentHot)
    {
        startPage();
        List<ActivityCommentHot> list = activityCommentHotService.selectActivityCommentHotList(activityCommentHot);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:hot:export")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ActivityCommentHot activityCommentHot)
    {
        List<ActivityCommentHot> list = activityCommentHotService.selectActivityCommentHotList(activityCommentHot);
        ExcelUtil<ActivityCommentHot> util = new ExcelUtil<ActivityCommentHot>(ActivityCommentHot.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @RequiresPermissions("funcampusnotice:hot:query")
    @GetMapping(value = "/{commentUid}")
    public AjaxResult getInfo(@PathVariable("commentUid") String commentUid)
    {
        return success(activityCommentHotService.selectActivityCommentHotByCommentUid(commentUid));
    }

    /**
     * 新增【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:hot:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivityCommentHot activityCommentHot)
    {
        return toAjax(activityCommentHotService.insertActivityCommentHot(activityCommentHot));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:hot:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivityCommentHot activityCommentHot)
    {
        return toAjax(activityCommentHotService.updateActivityCommentHot(activityCommentHot));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:hot:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{commentUids}")
    public AjaxResult remove(@PathVariable String[] commentUids)
    {
        return toAjax(activityCommentHotService.deleteActivityCommentHotByCommentUids(commentUids));
    }
}
