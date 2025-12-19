package com.zhyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import cn.hutool.cache.Cache;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.zhyl.common.constant.CacheConstants;
import com.zhyl.common.utils.DateUtils;
import com.zhyl.nursing.vo.NursingLevelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zhyl.nursing.mapper.NursingLevelMapper;
import com.zhyl.nursing.domain.NursingLevel;
import com.zhyl.nursing.service.INursingLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 护理等级Service业务层处理
 * 
 * @author alexis
 * @date 2025-06-02
 */
@Service
public class NursingLevelServiceImpl extends ServiceImpl<NursingLevelMapper, NursingLevel> implements INursingLevelService
{
    @Autowired
    private NursingLevelMapper nursingLevelMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    /**
     * 查询护理等级
     * 
     * @param id 护理等级主键
     * @return 护理等级
     */
    @Override
    public NursingLevel selectNursingLevelById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询护理等级列表
     * 
     * @param nursingLevel 护理等级
     * @return 护理等级
     */
    @Override
    public List<NursingLevel> selectNursingLevelList(NursingLevel nursingLevel)
    {
        return nursingLevelMapper.selectNursingLevelList(nursingLevel);
    }

    /**
     * 新增护理等级
     * 
     * @param nursingLevel 护理等级
     * @return 结果
     */
    @Override
    public int insertNursingLevel(NursingLevel nursingLevel)
    {

        boolean flag = save(nursingLevel);
        //删除缓存数据
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 删除缓存数据
     */
    private void deleteCache() {
        redisTemplate.delete(CacheConstants.NURSING_LEVEL_VO_LIST_KEY);
    }

    /**
     * 修改护理等级
     * 
     * @param nursingLevel 护理等级
     * @return 结果
     */
    @Override
    public int updateNursingLevel(NursingLevel nursingLevel)
    {

        boolean flag = updateById(nursingLevel);
        //删除缓存数据
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 批量删除护理等级
     * 
     * @param ids 需要删除的护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelByIds(Long[] ids)
    {

        boolean flag = removeByIds(Arrays.asList(ids));
        //删除缓存数据
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 删除护理等级信息
     * 
     * @param id 护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelById(Long id)
    {

        boolean flag = removeById(id);
        //删除缓存数据
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 查询护理等级Vo列表
     *
     * @param nursingLevel 条件
     * @return 结果
     */
    @Override
    public List<NursingLevelVo> selectNursingLevelVoList(NursingLevel nursingLevel) {
        // 查询缓存
        List<NursingLevelVo> cacheList = (List<NursingLevelVo>) redisTemplate.opsForValue().get(CacheConstants.NURSING_LEVEL_VO_LIST_KEY);

        // 缓存中有数据，直接返回
        if (ObjectUtils.isNotEmpty(cacheList)) {
            return cacheList;
        }

        // 缓存中没有数据，查询数据库
        List<NursingLevelVo> list = nursingLevelMapper.selectNursingLevelVoList(nursingLevel);
        // 更新缓存
        redisTemplate.opsForValue().set(CacheConstants.NURSING_LEVEL_VO_LIST_KEY, list);
        return list;
    }
}
