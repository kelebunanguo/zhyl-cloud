package com.zhyl.nursing.controller;

import com.zhyl.common.core.domain.R;
import com.zhyl.nursing.dto.NursingPlanDto;
import com.zhyl.nursing.service.INursingLevelService;
import com.zhyl.nursing.vo.NursingLevelVo;
import com.zhyl.nursing.vo.NursingPlanVo;
import com.zhyl.nursing.vo.NursingProjectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
 * @author ruoyi
 * @date 2025-12-05
 */
@RestController
@RequestMapping("/nursing/plan")
@Api(tags = "护理计划相关接口")
public class NursingPlanController extends BaseController
{
    @Autowired
    private INursingPlanService nursingPlanService;
    @Autowired
    private INursingLevelService iNursingLevelService;


    /**
     * 新增护理计划
     */
    @PreAuthorize("@ss.hasPermi('nursing:nursingPlan:add')")
    @Log(title = "护理计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NursingPlanDto dto)
    {
        return toAjax(nursingPlanService.insertNursingPlan(dto));
    }

/**
 * 查询护理计划列表
 */
@PreAuthorize("@ss.hasPermi('nursing:plan:list')")
@GetMapping("/list")
@ApiOperation("查询护理计划列表")
    public TableDataInfo<List<NursingPlan>> list(@ApiParam(value = "护理计划查询条件") NursingPlan nursingPlan)
    {
        startPage();
        List<NursingPlan> list = nursingPlanService.selectNursingPlanList(nursingPlan);
        return getDataTable(list);
    }


    /**
     * 获取护理计划详细信息
     */
    @PreAuthorize("@ss.hasPermi('nursing:plan:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取护理计划详细信息")
    public R<NursingPlanVo> getInfo(@ApiParam(value = "护理计划ID", required = true)
                                    @PathVariable("id") Long id)
    {
        return R.ok(nursingPlanService.selectNursingPlanById(id));
    }

    /**
     * 修改护理计划
     */
    @PreAuthorize("@ss.hasPermi('nursing:plan:edit')")
    @Log(title = "护理计划", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改护理计划")
    public AjaxResult edit(@ApiParam(value = "护理计划实体", required = true) @RequestBody NursingPlanDto dto)
    {
        return toAjax(nursingPlanService.updateNursingPlan(dto));
    }
    /**
     * 删除护理计划
     */
    @PreAuthorize("@ss.hasPermi('nursing:plan:remove')")
    @Log(title = "护理计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    @ApiOperation("删除护理计划")
    public AjaxResult remove(@ApiParam(value = "要删除的数据id") @PathVariable Long id)
    {
        return toAjax(nursingPlanService.deleteNursingPlanById(id));
    }

    /**
     * 查询护理项目列表
     */
    @GetMapping("/all")
    @ApiOperation(value = "查询所有护理计划")
    public AjaxResult listAll()
    {
        List<NursingPlanVo> list = nursingPlanService.selectAll();
        return success(list);
    }
}