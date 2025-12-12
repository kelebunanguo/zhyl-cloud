package com.zhyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zhyl.common.core.domain.R;
import com.zhyl.nursing.vo.NursingLevelVo;
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
import com.zhyl.nursing.domain.NursingLevel;
import com.zhyl.nursing.service.INursingLevelService;
import com.zhyl.common.utils.poi.ExcelUtil;
import com.zhyl.common.core.page.TableDataInfo;

/**
 * 护理等级Controller
 * 
 * @author alexis
 * @date 2025-06-02
 */
@Api("护理等级管理")
@RestController
@RequestMapping("/nursing/level")
public class NursingLevelController extends BaseController
{
    @Autowired
    private INursingLevelService nursingLevelService;

    /**
     * 查询护理等级列表
     */
    @ApiOperation("查询护理等级列表")
    @PreAuthorize("@ss.hasPermi('nursing:level:list')")
    @GetMapping("/list")
    public TableDataInfo<List<NursingLevelVo>> list(@ApiParam("护理等级查询条件") NursingLevel nursingLevel)
    {
        startPage();
        List<NursingLevelVo> list = nursingLevelService.selectNursingLevelVoList(nursingLevel);
        return getDataTable(list);
    }

    /**
     * 导出护理等级列表
     */
    @ApiOperation("导出护理等级列表")
    @PreAuthorize("@ss.hasPermi('nursing:level:export')")
    @Log(title = "护理等级", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@ApiParam("导出的查询条件") HttpServletResponse response, NursingLevel nursingLevel)
    {
        List<NursingLevel> list = nursingLevelService.selectNursingLevelList(nursingLevel);
        ExcelUtil<NursingLevel> util = new ExcelUtil<NursingLevel>(NursingLevel.class);
        util.exportExcel(response, list, "护理等级数据");
    }

    /**
     * 获取护理等级详细信息
     */
    @ApiOperation("获取护理等级详细信息")
    @PreAuthorize("@ss.hasPermi('nursing:level:query')")
    @GetMapping(value = "/{id}")
    public R<NursingLevel> getInfo(@PathVariable("id") @ApiParam("护理等级ID") Long id)
    {
        return R.ok(nursingLevelService.selectNursingLevelById(id));
    }

    /**
     * 新增护理等级
     */
    @ApiOperation("新增护理等级")
    @PreAuthorize("@ss.hasPermi('nursing:level:add')")
    @Log(title = "护理等级", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @ApiParam("新增的护理等级对象") NursingLevel nursingLevel)
    {
        return toAjax(nursingLevelService.insertNursingLevel(nursingLevel));
    }

    /**
     * 修改护理等级
     */
    @ApiOperation("修改护理等级")
    @PreAuthorize("@ss.hasPermi('nursing:level:edit')")
    @Log(title = "护理等级", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @ApiParam("修改的护理等级对象") NursingLevel nursingLevel)
    {
        return toAjax(nursingLevelService.updateNursingLevel(nursingLevel));
    }

    /**
     * 删除护理等级
     */
    @ApiOperation("删除护理等级")
    @PreAuthorize("@ss.hasPermi('nursing:level:remove')")
    @Log(title = "护理等级", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable @ApiParam("要删除的护理等级ID") Long[] ids)
    {
        return toAjax(nursingLevelService.deleteNursingLevelByIds(ids));
    }

    @ApiOperation("获取启用的护理等级列表")
    @GetMapping("/all")
    public R<List<NursingLevel>> getAllNursingLevel()
    {
    	NursingLevel nursingLevel = new NursingLevel();
    	nursingLevel.setStatus(1);
    	return R.ok(nursingLevelService.selectNursingLevelList(nursingLevel));
    }
}
