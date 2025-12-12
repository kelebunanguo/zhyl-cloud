package com.zhyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zhyl.common.core.domain.R;
import com.zhyl.nursing.dto.CheckInApplyDto;
import com.zhyl.nursing.vo.CheckInDetailVo;
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
import com.zhyl.nursing.domain.CheckIn;
import com.zhyl.nursing.service.ICheckInService;
import com.zhyl.common.utils.poi.ExcelUtil;
import com.zhyl.common.core.page.TableDataInfo;

/**
 * 入住Controller
 * 
 * @author alexis
 * @date 2025-12-10
 */
@Api("入住管理")
@RestController
@RequestMapping("/nursing/checkIn")
public class CheckInController extends BaseController
{
    @Autowired
    private ICheckInService checkInService;

    /**
     * 查询入住列表
     */
    @ApiOperation("查询入住列表")
    @PreAuthorize("@ss.hasPermi('nursing:checkIn:list')")
    @GetMapping("/list")
    public TableDataInfo<List<CheckIn>> list(@ApiParam("查询条件对象") CheckIn checkIn)
    {
        startPage();
        List<CheckIn> list = checkInService.selectCheckInList(checkIn);
        return getDataTable(list);
    }

    /**
     * 导出入住列表
     */
    @ApiOperation("导出入住列表")
    @PreAuthorize("@ss.hasPermi('nursing:checkIn:export')")
    @Log(title = "入住", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@ApiParam("导出的查询条件") HttpServletResponse response, CheckIn checkIn)
    {
        List<CheckIn> list = checkInService.selectCheckInList(checkIn);
        ExcelUtil<CheckIn> util = new ExcelUtil<CheckIn>(CheckIn.class);
        util.exportExcel(response, list, "入住数据");
    }

    /**
     * 获取入住详细信息
     */
    @ApiOperation("获取入住详细信息")
    @PreAuthorize("@ss.hasPermi('nursing:checkIn:query')")
    @GetMapping(value = "/{id}")
    public R<CheckIn> getInfo(@PathVariable("id") @ApiParam("入住ID") Long id)
    {
        return R.ok(checkInService.selectCheckInById(id));
    }

    /**
     * 新增入住
     */
    @ApiOperation("新增入住")
    @PreAuthorize("@ss.hasPermi('nursing:checkIn:add')")
    @Log(title = "入住", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @ApiParam("新增的入住对象") CheckIn checkIn)
    {
        return toAjax(checkInService.insertCheckIn(checkIn));
    }

    /**
     * 修改入住
     */
    @ApiOperation("修改入住")
    @PreAuthorize("@ss.hasPermi('nursing:checkIn:edit')")
    @Log(title = "入住", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @ApiParam("修改的入住对象") CheckIn checkIn)
    {
        return toAjax(checkInService.updateCheckIn(checkIn));
    }

    /**
     * 删除入住
     */
    @ApiOperation("删除入住")
    @PreAuthorize("@ss.hasPermi('nursing:checkIn:remove')")
    @Log(title = "入住", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable @ApiParam("要删除的入住ID") Long[] ids)
    {
        return toAjax(checkInService.deleteCheckInByIds(ids));
    }

    @ApiOperation("申请入住")
    @PostMapping("/apply")
    public AjaxResult apply(@RequestBody CheckInApplyDto dto)
    {
        checkInService.apply(dto);
        return AjaxResult.success();
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("查询入住详情")
    public AjaxResult detail(@PathVariable("id") Long id){
        CheckInDetailVo checkInDetailVo = checkInService.detail(id);
        return success(checkInDetailVo);
    }
}
