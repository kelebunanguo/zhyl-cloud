package com.zhyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.zhyl.common.constant.CacheConstants;
import com.zhyl.common.utils.DateUtils;
import com.zhyl.nursing.vo.NursingProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zhyl.nursing.mapper.NursingProjectMapper;
import com.zhyl.nursing.domain.NursingProject;
import com.zhyl.nursing.service.INursingProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 护理项目Service业务层处理
 * 
 * @author alexis
 * @date 2025-06-02
 */
@Service
public class NursingProjectServiceImpl extends ServiceImpl<NursingProjectMapper, NursingProject> implements INursingProjectService
{
    @Autowired
    private NursingProjectMapper nursingProjectMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 查询护理项目
     * 
     * @param id 护理项目主键
     * @return 护理项目
     */
    @Override
    public NursingProject selectNursingProjectById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询护理项目列表
     * 
     * @param nursingProject 护理项目
     * @return 护理项目
     */
    @Override
    public List<NursingProject> selectNursingProjectList(NursingProject nursingProject)
    {
        return nursingProjectMapper.selectNursingProjectList(nursingProject);
    }

    /**
     * 新增护理项目
     * 
     * @param nursingProject 护理项目
     * @return 结果
     */
    @Override
    public int insertNursingProject(NursingProject nursingProject)
    {
        boolean flag = save(nursingProject);
        //删除缓存
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 删除缓存
     */
    private void deleteCache() {
        redisTemplate.delete(CacheConstants.NURSING_PROJECT_VO_LIST_KEY);
    }

    /**
     * 修改护理项目
     * 
     * @param nursingProject 护理项目
     * @return 结果
     */
    @Override
    public int updateNursingProject(NursingProject nursingProject)
    {
        boolean flag = updateById(nursingProject);
        //删除缓存
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 批量删除护理项目
     * 
     * @param ids 需要删除的护理项目主键
     * @return 结果
     */
    @Override
    public int deleteNursingProjectByIds(Long[] ids)
    {

        boolean flag = removeByIds(Arrays.asList(ids));
        //删除缓存
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 删除护理项目信息
     * 
     * @param id 护理项目主键
     * @return 结果
     */
    @Override
    public int deleteNursingProjectById(Long id)
    {

        boolean flag = removeById(id);
        //删除缓存
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 查询所有护理项目
     *
     * @return 护理项目列表
     */
    @Override
    public List<NursingProjectVo> getAll() {
        // 从缓存中获取
        List<NursingProjectVo> NursingProjectVo = (List<NursingProjectVo>)redisTemplate.opsForValue().get(CacheConstants.NURSING_PROJECT_VO_LIST_KEY);
        // 如果缓存中有数据则直接返回
        if (ObjectUtils.isNotEmpty(NursingProjectVo)){
            return NursingProjectVo;
        }

        // 如果缓存中没有数据则从数据库中查询
        List<NursingProjectVo> list = nursingProjectMapper.getAll();
        // 返回结果添加到缓存
        redisTemplate.opsForValue().set(CacheConstants.NURSING_PROJECT_VO_LIST_KEY, list);
        return list;
    }
}
