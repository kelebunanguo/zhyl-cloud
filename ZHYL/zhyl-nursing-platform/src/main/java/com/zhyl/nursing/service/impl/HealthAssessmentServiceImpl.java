package com.zhyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;
import com.zhyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zhyl.nursing.mapper.HealthAssessmentMapper;
import com.zhyl.nursing.domain.HealthAssessment;
import com.zhyl.nursing.service.IHealthAssessmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 健康评估Service业务层处理
 * 
 * @author alexis
 * @date 2025-12-19
 */
@Service
public class HealthAssessmentServiceImpl extends ServiceImpl<HealthAssessmentMapper, HealthAssessment> implements IHealthAssessmentService
{
    @Autowired
    private HealthAssessmentMapper healthAssessmentMapper;

    /**
     * 查询健康评估
     * 
     * @param id 健康评估主键
     * @return 健康评估
     */
    @Override
    public HealthAssessment selectHealthAssessmentById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询健康评估列表
     * 
     * @param healthAssessment 健康评估
     * @return 健康评估
     */
    @Override
    public List<HealthAssessment> selectHealthAssessmentList(HealthAssessment healthAssessment)
    {
        return healthAssessmentMapper.selectHealthAssessmentList(healthAssessment);
    }

    /**
     * 新增健康评估
     * 
     * @param healthAssessment 健康评估
     * @return 结果
     */
    @Override
    public int insertHealthAssessment(HealthAssessment healthAssessment)
    {
        return save(healthAssessment) ? 1 : 0;
    }

    /**
     * 修改健康评估
     * 
     * @param healthAssessment 健康评估
     * @return 结果
     */
    @Override
    public int updateHealthAssessment(HealthAssessment healthAssessment)
    {
        return updateById(healthAssessment) ? 1 : 0;
    }

    /**
     * 批量删除健康评估
     * 
     * @param ids 需要删除的健康评估主键
     * @return 结果
     */
    @Override
    public int deleteHealthAssessmentByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除健康评估信息
     * 
     * @param id 健康评估主键
     * @return 结果
     */
    @Override
    public int deleteHealthAssessmentById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }
}
