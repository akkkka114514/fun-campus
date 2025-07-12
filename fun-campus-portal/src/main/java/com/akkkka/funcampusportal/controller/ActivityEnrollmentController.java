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
import com.akkkka.funcampusnotice.domain.ActivityEnrollment;
import com.akkkka.funcampusnotice.service.IActivityEnrollmentService;
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
@RequestMapping("/enrollment")
public class ActivityEnrollmentController extends BaseController
{
    @Autowired
    private IActivityEnrollmentService activityEnrollmentService;

    /**
     * 查询【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:enrollment:list")
    @GetMapping("/list")
    public TableDataInfo list(ActivityEnrollment activityEnrollment)
    {
        startPage();
        List<ActivityEnrollment> list = activityEnrollmentService.selectActivityEnrollmentList(activityEnrollment);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:enrollment:export")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ActivityEnrollment activityEnrollment)
    {
        List<ActivityEnrollment> list = activityEnrollmentService.selectActivityEnrollmentList(activityEnrollment);
        ExcelUtil<ActivityEnrollment> util = new ExcelUtil<ActivityEnrollment>(ActivityEnrollment.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @RequiresPermissions("funcampusnotice:enrollment:query")
    @GetMapping(value = "/{activityUid}")
    public AjaxResult getInfo(@PathVariable("activityUid") String activityUid)
    {
        return success(activityEnrollmentService.selectActivityEnrollmentByActivityUid(activityUid));
    }

    /**
     * 新增【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:enrollment:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivityEnrollment activityEnrollment)
    {
        return toAjax(activityEnrollmentService.insertActivityEnrollment(activityEnrollment));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:enrollment:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivityEnrollment activityEnrollment)
    {
        return toAjax(activityEnrollmentService.updateActivityEnrollment(activityEnrollment));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:enrollment:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{activityUids}")
    public AjaxResult remove(@PathVariable String[] activityUids)
    {
        return toAjax(activityEnrollmentService.deleteActivityEnrollmentByActivityUids(activityUids));
    }
}
