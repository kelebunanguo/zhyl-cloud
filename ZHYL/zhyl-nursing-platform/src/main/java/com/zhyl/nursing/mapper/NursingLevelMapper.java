package com.zhyl.nursing.mapper;

import java.util.List;
import com.zhyl.nursing.domain.NursingLevel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhyl.nursing.vo.NursingLevelVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 护理等级Mapper接口
 * 
 * @author alexis
 * @date 2025-06-02
 */
@Mapper
public interface NursingLevelMapper extends BaseMapper<NursingLevel>
{
    /**
     * 查询护理等级
     * 
     * @param id 护理等级主键
     * @return 护理等级
     */
    public NursingLevel selectNursingLevelById(Long id);

    /**
     * 查询护理等级列表
     * 
     * @param nursingLevel 护理等级
     * @return 护理等级集合
     */
    public List<NursingLevel> selectNursingLevelList(NursingLevel nursingLevel);

    /**
     * 新增护理等级
     * 
     * @param nursingLevel 护理等级
     * @return 结果
     */
    public int insertNursingLevel(NursingLevel nursingLevel);

    /**
     * 修改护理等级
     * 
     * @param nursingLevel 护理等级
     * @return 结果
     */
    public int updateNursingLevel(NursingLevel nursingLevel);

    /**
     * 删除护理等级
     * 
     * @param id 护理等级主键
     * @return 结果
     */
    public int deleteNursingLevelById(Long id);

    /**
     * 批量删除护理等级
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNursingLevelByIds(Long[] ids);

    /**
     * 查询护理等级Vo列表
     * @param nursingLevel  参数
     * @return  结果
     */
    List<NursingLevelVo> selectNursingLevelVoList(NursingLevel nursingLevel);
}
