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
import com.akkkka.funcampusnotice.domain.MessageGroup;
import com.akkkka.funcampusnotice.service.IMessageGroupService;
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
@RequestMapping("/group")
public class MessageGroupController extends BaseController
{
    @Autowired
    private IMessageGroupService messageGroupService;

    /**
     * 查询【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:group:list")
    @GetMapping("/list")
    public TableDataInfo list(MessageGroup messageGroup)
    {
        startPage();
        List<MessageGroup> list = messageGroupService.selectMessageGroupList(messageGroup);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @RequiresPermissions("funcampusnotice:group:export")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MessageGroup messageGroup)
    {
        List<MessageGroup> list = messageGroupService.selectMessageGroupList(messageGroup);
        ExcelUtil<MessageGroup> util = new ExcelUtil<MessageGroup>(MessageGroup.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @RequiresPermissions("funcampusnotice:group:query")
    @GetMapping(value = "/{uid}")
    public AjaxResult getInfo(@PathVariable("uid") String uid)
    {
        return success(messageGroupService.selectMessageGroupByUid(uid));
    }

    /**
     * 新增【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:group:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MessageGroup messageGroup)
    {
        return toAjax(messageGroupService.insertMessageGroup(messageGroup));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:group:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MessageGroup messageGroup)
    {
        return toAjax(messageGroupService.updateMessageGroup(messageGroup));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("funcampusnotice:group:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{uids}")
    public AjaxResult remove(@PathVariable String[] uids)
    {
        return toAjax(messageGroupService.deleteMessageGroupByUids(uids));
    }
}
