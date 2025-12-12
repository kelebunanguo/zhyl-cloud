package com.zhyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zhyl.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zhyl.common.annotation.Log;
import com.zhyl.common.core.controller.BaseController;
import com.zhyl.common.core.domain.AjaxResult;
import com.zhyl.common.enums.BusinessType;
import com.zhyl.nursing.domain.Elder;
import com.zhyl.nursing.service.IElderService;
import com.zhyl.common.utils.poi.ExcelUtil;
import com.zhyl.common.core.page.TableDataInfo;

/**
 * 老人Controller
 * 
 * @author alexis
 * @date 2025-12-10
 */
@Api("老人管理")
@RestController
@RequestMapping("/nursing/elder")
public class ElderController extends BaseController
{
    @Autowired
    private IElderService elderService;

    /**
     * 查询老人列表
     */
    @ApiOperation("查询老人列表")
    @PreAuthorize("@ss.hasPermi('nursing:elder:list')")
    @GetMapping("/list")
    public TableDataInfo<List<Elder>> list(@ApiParam("查询条件对象") Elder elder)
    {
        startPage();
        List<Elder> list = elderService.selectElderList(elder);
        return getDataTable(list);
    }

    /**
     * 导出老人列表
     */
    @ApiOperation("导出老人列表")
    @PreAuthorize("@ss.hasPermi('nursing:elder:export')")
    @Log(title = "老人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@ApiParam("导出的查询条件") HttpServletResponse response, Elder elder)
    {
        List<Elder> list = elderService.selectElderList(elder);
        ExcelUtil<Elder> util = new ExcelUtil<Elder>(Elder.class);
        util.exportExcel(response, list, "老人数据");
    }

    /**
     * 获取老人详细信息
     */
    @ApiOperation("获取老人详细信息")
    @PreAuthorize("@ss.hasPermi('nursing:elder:query')")
    @GetMapping(value = "/{id}")
    public R<Elder> getInfo(@PathVariable("id") @ApiParam("老人ID") Long id)
    {
        return R.ok(elderService.selectElderById(id));
    }

    /**
     * 新增老人
     */
    @ApiOperation("新增老人")
    @PreAuthorize("@ss.hasPermi('nursing:elder:add')")
    @Log(title = "老人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @ApiParam("新增的老人对象") Elder elder)
    {
        return toAjax(elderService.insertElder(elder));
    }

    /**
     * 修改老人
     */
    @ApiOperation("修改老人")
    @PreAuthorize("@ss.hasPermi('nursing:elder:edit')")
    @Log(title = "老人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @ApiParam("修改的老人对象") Elder elder)
    {
        return toAjax(elderService.updateElder(elder));
    }

    /**
     * 删除老人
     */
    @ApiOperation("删除老人")
    @PreAuthorize("@ss.hasPermi('nursing:elder:remove')")
    @Log(title = "老人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable @ApiParam("要删除的老人ID") Long[] ids)
    {
        return toAjax(elderService.deleteElderByIds(ids));
    }
}
