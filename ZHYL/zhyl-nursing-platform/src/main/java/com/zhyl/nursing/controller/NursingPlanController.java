package com.zhyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zhyl.common.core.domain.R;
import com.zhyl.nursing.dto.NursingPlanDto;
import com.zhyl.nursing.vo.NursingPlanVo;
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
import com.zhyl.nursing.domain.NursingPlan;
import com.zhyl.nursing.service.INursingPlanService;
import com.zhyl.common.utils.poi.ExcelUtil;
import com.zhyl.common.core.page.TableDataInfo;

/**
 * 护理计划Controller
 * 
 * @author alexis
 * @date 2025-06-02
 */
@Api("护理计划管理")
@RestController
@RequestMapping("/nursing/plan")
public class NursingPlanController extends BaseController
{
    @Autowired
    private INursingPlanService nursingPlanService;

    /**
     * 查询护理计划列表
     */
    @ApiOperation("查询护理计划列表")
    @PreAuthorize("@ss.hasPermi('nursing:plan:list')")
    @GetMapping("/list")
    public TableDataInfo<List<NursingPlan>> list(@ApiParam("查询条件对象") NursingPlan nursingPlan)
    {
        startPage();
        List<NursingPlan> list = nursingPlanService.selectNursingPlanList(nursingPlan);
        return getDataTable(list);
    }

    /**
     * 导出护理计划列表
     */
    @ApiOperation("导出护理计划列表")
    @PreAuthorize("@ss.hasPermi('nursing:plan:export')")
    @Log(title = "护理计划", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@ApiParam("导出的查询条件") HttpServletResponse response, NursingPlan nursingPlan)
    {
        List<NursingPlan> list = nursingPlanService.selectNursingPlanList(nursingPlan);
        ExcelUtil<NursingPlan> util = new ExcelUtil<NursingPlan>(NursingPlan.class);
        util.exportExcel(response, list, "护理计划数据");
    }

    /**
     * 获取护理计划详细信息
     */
    @ApiOperation("获取护理计划详细信息")
    @PreAuthorize("@ss.hasPermi('nursing:plan:query')")
    @GetMapping(value = "/{id}")
    public R<NursingPlanVo> getInfo(@PathVariable("id") @ApiParam("护理计划ID") Long id)
    {
        return R.ok(nursingPlanService.selectNursingPlanById(id));
    }

    /**
     * 新增护理计划
     */
    @ApiOperation("新增护理计划")
    @PreAuthorize("@ss.hasPermi('nursing:plan:add')")
    @Log(title = "护理计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @ApiParam("新增的护理计划对象") NursingPlanDto dto)
    {
        return toAjax(nursingPlanService.insertNursingPlan(dto));
    }

    /**
     * 修改护理计划
     */
    @ApiOperation("修改护理计划")
    @PreAuthorize("@ss.hasPermi('nursing:plan:edit')")
    @Log(title = "护理计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @ApiParam("修改的护理计划对象") NursingPlanDto dto)
    {
        return toAjax(nursingPlanService.updateNursingPlan(dto));
    }

    /**
     * 删除护理计划
     */
    @ApiOperation("删除护理计划")
    @PreAuthorize("@ss.hasPermi('nursing:plan:remove')")
    @Log(title = "护理计划", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable @ApiParam("要删除的护理计划ID") Long id)
    {
        return toAjax(nursingPlanService.deleteNursingPlanById(id));
    }

    /**
     * 查询所有护理计划
     */
    @GetMapping("/all")
    @ApiOperation(value = "获取所有护理计划")
    public R<List<NursingPlan>> listAll()
    {
        return R.ok(nursingPlanService.getAllNursingPlans());
    }
}
