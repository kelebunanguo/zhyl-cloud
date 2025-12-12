package com.zhyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zhyl.common.core.domain.R;
import com.zhyl.nursing.vo.NursingProjectVo;
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
import com.zhyl.nursing.domain.NursingProject;
import com.zhyl.nursing.service.INursingProjectService;
import com.zhyl.common.utils.poi.ExcelUtil;
import com.zhyl.common.core.page.TableDataInfo;

/**
 * 护理项目Controller
 * 
 * @author alexis
 * @date 2025-06-02
 */
@Api("护理项目管理")
@RestController
@RequestMapping("/nursing/project")
public class NursingProjectController extends BaseController
{
    @Autowired
    private INursingProjectService nursingProjectService;

    /**
     * 查询护理项目列表
     */
    @ApiOperation("查询护理项目列表")
    @PreAuthorize("@ss.hasPermi('nursing:project:list')")
    @GetMapping("/list")
    public TableDataInfo<List<NursingProject>> list(@ApiParam("查询条件对象") NursingProject nursingProject)
    {
        startPage();
        List<NursingProject> list = nursingProjectService.selectNursingProjectList(nursingProject);
        return getDataTable(list);
    }

    /**
     * 导出护理项目列表
     */
    @ApiOperation("导出护理项目列表")
    @PreAuthorize("@ss.hasPermi('nursing:project:export')")
    @Log(title = "护理项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@ApiParam("导出的查询条件") HttpServletResponse response, NursingProject nursingProject)
    {
        List<NursingProject> list = nursingProjectService.selectNursingProjectList(nursingProject);
        ExcelUtil<NursingProject> util = new ExcelUtil<NursingProject>(NursingProject.class);
        util.exportExcel(response, list, "护理项目数据");
    }

    /**
     * 获取护理项目详细信息
     */
    @ApiOperation("获取护理项目详细信息")
    @PreAuthorize("@ss.hasPermi('nursing:project:query')")
    @GetMapping(value = "/{id}")
    public R<NursingProject> getInfo(@PathVariable("id") @ApiParam("护理项目ID") Long id)
    {
        return R.ok(nursingProjectService.selectNursingProjectById(id));
    }

    /**
     * 新增护理项目
     */
    @ApiOperation("新增护理项目")
    @PreAuthorize("@ss.hasPermi('nursing:project:add')")
    @Log(title = "护理项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @ApiParam("新增的护理项目对象") NursingProject nursingProject)
    {
        return toAjax(nursingProjectService.insertNursingProject(nursingProject));
    }

    /**
     * 修改护理项目
     */
    @ApiOperation("修改护理项目")
    @PreAuthorize("@ss.hasPermi('nursing:project:edit')")
    @Log(title = "护理项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @ApiParam("修改的护理项目对象") NursingProject nursingProject)
    {
        return toAjax(nursingProjectService.updateNursingProject(nursingProject));
    }

    /**
     * 删除护理项目
     */
    @ApiOperation("删除护理项目")
    @PreAuthorize("@ss.hasPermi('nursing:project:remove')")
    @Log(title = "护理项目", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable @ApiParam("要删除的护理项目ID") Long[] ids)
    {
        return toAjax(nursingProjectService.deleteNursingProjectByIds(ids));
    }

    @GetMapping("/all")
    public AjaxResult getAll() {
        List<NursingProjectVo> list = nursingProjectService.getAll();
        return AjaxResult.success(list);
    }
}
