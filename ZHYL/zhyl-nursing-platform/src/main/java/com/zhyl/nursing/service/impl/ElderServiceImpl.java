package com.zhyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;
import com.zhyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zhyl.nursing.mapper.ElderMapper;
import com.zhyl.nursing.domain.Elder;
import com.zhyl.nursing.service.IElderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 老人Service业务层处理
 * 
 * @author alexis
 * @date 2025-12-10
 */
@Service
public class ElderServiceImpl extends ServiceImpl<ElderMapper, Elder> implements IElderService
{
    @Autowired
    private ElderMapper elderMapper;

    /**
     * 查询老人
     * 
     * @param id 老人主键
     * @return 老人
     */
    @Override
    public Elder selectElderById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询老人列表
     * 
     * @param elder 老人
     * @return 老人
     */
    @Override
    public List<Elder> selectElderList(Elder elder)
    {
        return elderMapper.selectElderList(elder);
    }

    /**
     * 新增老人
     * 
     * @param elder 老人
     * @return 结果
     */
    @Override
    public int insertElder(Elder elder)
    {
        return save(elder) ? 1 : 0;
    }

    /**
     * 修改老人
     * 
     * @param elder 老人
     * @return 结果
     */
    @Override
    public int updateElder(Elder elder)
    {
        return updateById(elder) ? 1 : 0;
    }

    /**
     * 批量删除老人
     * 
     * @param ids 需要删除的老人主键
     * @return 结果
     */
    @Override
    public int deleteElderByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除老人信息
     * 
     * @param id 老人主键
     * @return 结果
     */
    @Override
    public int deleteElderById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }
}
