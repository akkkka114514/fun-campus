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
import com.akkkka.funcampusnotice.domain.ActivitySchedule;
import com.akkkka.funcampusnotice.service.IActivityScheduleService;
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
@RequestMapping("/schedule")
public class ActivityScheduleController extends BaseController
{
    @Autowired
    private IActivityScheduleService activityScheduleService;

    /**
     * 查询【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:schedule:list")
    @GetMapping("/list")
    public TableDataInfo list(ActivitySchedule activitySchedule)
    {
        startPage();
        List<ActivitySchedule> list = activityScheduleService.selectActivityScheduleList(activitySchedule);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:schedule:export")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ActivitySchedule activitySchedule)
    {
        List<ActivitySchedule> list = activityScheduleService.selectActivityScheduleList(activitySchedule);
        ExcelUtil<ActivitySchedule> util = new ExcelUtil<ActivitySchedule>(ActivitySchedule.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @RequiresPermissions("funcampusnotice:schedule:query")
    @GetMapping(value = "/{activityUid}")
    public AjaxResult getInfo(@PathVariable("activityUid") String activityUid)
    {
        return success(activityScheduleService.selectActivityScheduleByActivityUid(activityUid));
    }

    /**
     * 新增【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:schedule:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivitySchedule activitySchedule)
    {
        return toAjax(activityScheduleService.insertActivitySchedule(activitySchedule));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:schedule:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivitySchedule activitySchedule)
    {
        return toAjax(activityScheduleService.updateActivitySchedule(activitySchedule));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:schedule:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{activityUids}")
    public AjaxResult remove(@PathVariable String[] activityUids)
    {
        return toAjax(activityScheduleService.deleteActivityScheduleByActivityUids(activityUids));
    }
}
