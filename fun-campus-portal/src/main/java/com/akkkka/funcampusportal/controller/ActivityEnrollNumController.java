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
import com.akkkka.funcampusnotice.domain.ActivityEnrollNum;
import com.akkkka.funcampusnotice.service.IActivityEnrollNumService;
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
@RequestMapping("/num")
public class ActivityEnrollNumController extends BaseController
{
    @Autowired
    private IActivityEnrollNumService activityEnrollNumService;

    /**
     * 查询【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:num:list")
    @GetMapping("/list")
    public TableDataInfo list(ActivityEnrollNum activityEnrollNum)
    {
        startPage();
        List<ActivityEnrollNum> list = activityEnrollNumService.selectActivityEnrollNumList(activityEnrollNum);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:num:export")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ActivityEnrollNum activityEnrollNum)
    {
        List<ActivityEnrollNum> list = activityEnrollNumService.selectActivityEnrollNumList(activityEnrollNum);
        ExcelUtil<ActivityEnrollNum> util = new ExcelUtil<ActivityEnrollNum>(ActivityEnrollNum.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @RequiresPermissions("funcampusnotice:num:query")
    @GetMapping(value = "/{activityUid}")
    public AjaxResult getInfo(@PathVariable("activityUid") String activityUid)
    {
        return success(activityEnrollNumService.selectActivityEnrollNumByActivityUid(activityUid));
    }

    /**
     * 新增【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:num:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivityEnrollNum activityEnrollNum)
    {
        return toAjax(activityEnrollNumService.insertActivityEnrollNum(activityEnrollNum));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:num:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivityEnrollNum activityEnrollNum)
    {
        return toAjax(activityEnrollNumService.updateActivityEnrollNum(activityEnrollNum));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:num:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{activityUids}")
    public AjaxResult remove(@PathVariable String[] activityUids)
    {
        return toAjax(activityEnrollNumService.deleteActivityEnrollNumByActivityUids(activityUids));
    }
}
