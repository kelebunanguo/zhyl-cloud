package com.zhyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.zhyl.common.constant.CacheConstants;
import com.zhyl.common.utils.DateUtils;
import com.zhyl.common.utils.bean.BeanUtils;
import com.zhyl.nursing.domain.NursingProjectPlan;
import com.zhyl.nursing.dto.NursingPlanDto;
import com.zhyl.nursing.mapper.NursingProjectPlanMapper;
import com.zhyl.nursing.vo.NursingPlanVo;
import com.zhyl.nursing.vo.NursingProjectPlanVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zhyl.nursing.mapper.NursingPlanMapper;
import com.zhyl.nursing.domain.NursingPlan;
import com.zhyl.nursing.service.INursingPlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 护理计划Service业务层处理
 * 
 * @author alexis
 * @date 2025-06-02
 */
@Slf4j
@Service
public class NursingPlanServiceImpl extends ServiceImpl<NursingPlanMapper, NursingPlan> implements INursingPlanService
{
    @Autowired
    private NursingPlanMapper nursingPlanMapper;

    @Autowired
    private NursingProjectPlanMapper nursingProjectPlanMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 查询护理计划
     * 
     * @param id 护理计划主键
     * @return 护理计划
     */
    @Override
    public NursingPlanVo selectNursingPlanById(Long id)
    {
        // 查询护理计划基本信息
        NursingPlan nursingPlan = nursingPlanMapper.selectById(id);

        // 查询护理计划关联的护理项目集合
        List<NursingProjectPlanVo> projectPlans = nursingProjectPlanMapper.selectByNursingPlanId(id);

        // 将两部分信息合并到一个对象中返回
        NursingPlanVo nursingPlanVo = new NursingPlanVo();

        BeanUtils.copyProperties(nursingPlan, nursingPlanVo);
        nursingPlanVo.setProjectPlans(projectPlans);

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
     * 
     * @param dto 护理计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNursingPlan(NursingPlanDto dto)
    {

        // 1.保存护理计划
        NursingPlan nursingPlan = new NursingPlan();
        BeanUtils.copyProperties(dto, nursingPlan);
        nursingPlan.setCreateTime(DateUtils.getNowDate());

        nursingPlanMapper.insert(nursingPlan);

        // 2.批量保存护理计划和护理项目的对应关系
        int count = nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(), nursingPlan.getId());
        boolean falg = count == 0;
        //删除缓存
        deleteCache();
        return falg ? 0 : 1;
    }

    /**
     * 删除缓存
     */
    private void deleteCache() {
        redisTemplate.delete(CacheConstants.NURSING_PLAN_LIST_KEY);
    }

    /**
     * 修改护理计划
     * 
     * @param dto 护理计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateNursingPlan(NursingPlanDto dto)
    {
        // 判断dto中的护理项目是否为空，如果不为空，先删除护理计划关联的所有护理项目，再重新批量保存最新的关联
        if (dto.getProjectPlans() != null && !dto.getProjectPlans().isEmpty()) {
            // 删除护理计划对应的护理项目列表
            nursingProjectPlanMapper.deleteByNursingPlanId(dto.getId());

            // 批量保存护理计划关联的护理项目
            nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(), dto.getId());
        }

        NursingPlan nursingPlan = new NursingPlan();
        BeanUtils.copyProperties(dto, nursingPlan);

        // 修改护理计划
        int flag = nursingPlanMapper.updateById(nursingPlan);
        //删除缓存数据
        deleteCache();
        return flag;
    }

    /**
     * 批量删除护理计划
     * 
     * @param ids 需要删除的护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanByIds(Long[] ids)
    {
        boolean flag = removeByIds(Arrays.asList(ids));
        //删除缓存数据
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 删除护理计划信息
     * 
     * @param id 护理计划主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNursingPlanById(Long id)
    {
        // 删除护理计划关联的护理项目
        nursingProjectPlanMapper.deleteByNursingPlanId(id);
        // 删除护理计划
        boolean flag = removeById(id);
        //删除缓存数据
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 查询所有护理计划
     *
     * @return 护理计划列表
     */
    @Override
    public List<NursingPlan> getAllNursingPlans() {
        log.info("开始查询所有启用的护理计划...");
        // 从缓存中获取所有护理计划
        List<NursingPlan> list = (List<NursingPlan>)redisTemplate.opsForValue().get(CacheConstants.NURSING_PLAN_LIST_KEY);
        log.info("缓存结果: {}", list);
        //如果缓存中有数据，直接返回
        if (ObjectUtils.isNotEmpty(list)){
            log.info("缓存命中，返回缓存数据");
            return list;
        }

        log.info("缓存未命中，查询数据库");

        // 如果没有数据，则从数据库中查询
        LambdaQueryWrapper<NursingPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NursingPlan::getStatus, 1);
        List<NursingPlan> planList = list(queryWrapper);
        log.info("数据库查询结果: {}", list);
        // 将查询结果保存到缓存中
        redisTemplate.opsForValue().set(CacheConstants.NURSING_PLAN_LIST_KEY, planList);
        log.info("已存入缓存，key: {}", CacheConstants.NURSING_PLAN_LIST_KEY);
        return planList;
    }
}
