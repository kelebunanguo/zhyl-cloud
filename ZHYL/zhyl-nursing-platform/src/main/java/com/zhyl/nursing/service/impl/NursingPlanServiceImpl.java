package com.zhyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhyl.common.utils.DateUtils;
import com.zhyl.common.utils.bean.BeanUtils;
import com.zhyl.nursing.domain.NursingProjectPlan;
import com.zhyl.nursing.dto.NursingPlanDto;
import com.zhyl.nursing.mapper.NursingProjectPlanMapper;
import com.zhyl.nursing.vo.NursingLevelVo;
import com.zhyl.nursing.vo.NursingPlanVo;
import com.zhyl.nursing.vo.NursingProjectPlanVo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zhyl.nursing.mapper.NursingPlanMapper;
import com.zhyl.nursing.domain.NursingPlan;
import com.zhyl.nursing.service.INursingPlanService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 护理计划Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-05
 */
@Transactional
@Service
public class NursingPlanServiceImpl extends ServiceImpl<NursingPlanMapper, NursingPlan> implements INursingPlanService
{
    @Autowired
    private NursingPlanMapper nursingPlanMapper;
    @Autowired
    private NursingProjectPlanMapper nursingProjectPlanMapper;


    /**
     * 查询护理计划
     *
     * @param id 护理计划主键
     * @return 护理计划
     */
    @Override
    public NursingPlanVo selectNursingPlanById(Long id)
    {
        // 1.根据id查询护理计划
        NursingPlan nursingPlan = nursingPlanMapper.selectNursingPlanById(Math.toIntExact(id));

        // 2.根据护理计划id查询关联的所有护理项目
        List<NursingProjectPlanVo> list = nursingProjectPlanMapper.selectByPlanId(id);

        // 3.封装结果并返回
        NursingPlanVo nursingPlanVo = new NursingPlanVo();
        BeanUtils.copyProperties(nursingPlan, nursingPlanVo);
        nursingPlanVo.setProjectPlans(list);
        return nursingPlanVo;
    }



    /**
     * 查询护理计划列表
     * 
     * @param nursingPlan 护理计划
     * @return 护理计划
     */
    @Override
    public List<NursingPlan> selectNursingPlanList(NursingPlan nursingPlan)
    {
        return nursingPlanMapper.selectNursingPlanList(nursingPlan);
    }

    /**
     * 新增护理计划
     * @param dto 护理计划
     * @return 结果
     */
    @Transactional
    @Override
    public int insertNursingPlan(NursingPlanDto dto)
    {
        // 保存护理计划
        // 属性拷贝
        NursingPlan nursingPlan = new NursingPlan();
        BeanUtils.copyProperties(dto, nursingPlan);
        nursingPlan.setCreateTime(DateUtils.getNowDate());
        nursingPlanMapper.insert(nursingPlan);

        // 批量保存护理项目计划关系
        int count = nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(), nursingPlan.getId().longValue());
        return count == 0 ? 0 : 1;
    }

    /**
     * 修改护理计划
     *
     * @param dto 护理计划
     * @return 结果
     */
    @Override
    public int updateNursingPlan(NursingPlanDto dto) {
        try {
            // 处理项目关联关系
            if (dto.getProjectPlans() != null && !dto.getProjectPlans().isEmpty()) {
                nursingProjectPlanMapper.deleteByPlanId(dto.getId());

                // 设置项目计划关联的planId
                dto.getProjectPlans().forEach(projectPlan ->
                        projectPlan.setPlanId(dto.getId()));

                nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(), dto.getId());
            }

            // ====== 修复点：手动转换并更新 ======
            // 先查询原有数据
            NursingPlan existing = getById(dto.getId());
            if (existing == null) {
                throw new RuntimeException("护理计划不存在，ID: " + dto.getId());
            }

            // 只更新必要的字段，避免null覆盖
            if (dto.getPlanName() != null) {
                existing.setPlanName(dto.getPlanName());
            }
            if (dto.getSortNo() != null) {
                existing.setSortNo(dto.getSortNo());
            }
            if (dto.getStatus() != null) {
                // 类型转换：Integer -> Long
                existing.setStatus(dto.getStatus().longValue());
            }

            // 设置更新时间（如果BaseEntity中有更新时间字段）
            // existing.setUpdateTime(new Date());

            // 使用updateById更新
            return updateById(existing) ? 1 : 0;

        } catch (BeansException e) {
            log.error("更新护理计划失败", e);
            throw new RuntimeException("更新护理计划失败", e);
        }
    }

    /**
     * 批量删除护理计划
     * 
     * @param ids 需要删除的护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanByIds(Integer[] ids)
    {
                return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除护理计划信息
     *
     * @param id 护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanById(Long id)
    {
        // 删除护理计划与护理项目的关系
        nursingProjectPlanMapper.deleteByPlanId(id);
        // 删除护理计划
        return removeById(id) ? 1 : 0;
    }

    /**
     * 查询所有护理计划
     *
     * @return
     */
    @Override
    public List<NursingPlanVo> selectAll() {
        return nursingPlanMapper.selectAll();
    }


}
