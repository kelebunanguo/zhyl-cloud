package com.zhyl.nursing.service.impl;

import cn.hutool.core.util.IdcardUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhyl.common.exception.base.BaseException;
import com.zhyl.common.utils.CodeGenerator;
import com.zhyl.common.utils.StringUtils;
import com.zhyl.common.utils.bean.BeanUtils;
import com.zhyl.nursing.domain.*;
import com.zhyl.nursing.dto.CheckInApplyDto;
import com.zhyl.nursing.dto.CheckInElderDto;
import com.zhyl.nursing.dto.ElderFamilyDto;
import com.zhyl.nursing.mapper.*;
import com.zhyl.nursing.service.ICheckInService;
import com.zhyl.nursing.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 入住Service业务层处理
 * 
 * @author alexis
 * @date 2025-12-10
 */
@Slf4j
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements ICheckInService
{
    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private CheckInConfigMapper checkInConfigMapper;

    @Autowired
    private ContractMapper contractMapper;

    /**
     * 查询入住
     * 
     * @param id 入住主键
     * @return 入住
     */
    @Override
    public CheckIn selectCheckInById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询入住列表
     * 
     * @param checkIn 入住
     * @return 入住
     */
    @Override
    public List<CheckIn> selectCheckInList(CheckIn checkIn)
    {
        return checkInMapper.selectCheckInList(checkIn);
    }

    /**
     * 新增入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int insertCheckIn(CheckIn checkIn)
    {
        return save(checkIn) ? 1 : 0;
    }

    /**
     * 修改入住
     * 
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int updateCheckIn(CheckIn checkIn)
    {
        return updateById(checkIn) ? 1 : 0;
    }

    /**
     * 批量删除入住
     * 
     * @param ids 需要删除的入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除入住信息
     * 
     * @param id 入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }

    /**
     * 申请入住
     *
     * @param checkInApplyDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(CheckInApplyDto checkInApplyDto) {
        // 判断老人是否已经入住
        // 通过身份证号查询老人
        LambdaQueryWrapper<Elder> elderQueryWrapper = new LambdaQueryWrapper<>();
        elderQueryWrapper.eq(Elder::getIdCardNo, checkInApplyDto.getCheckInElderDto().getIdCardNo());
        elderQueryWrapper.eq(Elder::getStatus, 1);
        Elder elder =  elderMapper.selectOne(elderQueryWrapper);
        if(ObjectUtils.isNotEmpty(elder)) {
            throw new BaseException("老人已入住");
        }

        // 更新床位的状态  已入住
        Bed bed = bedMapper.selectById(checkInApplyDto.getCheckInConfigDto().getBedId());
        bed.setBedStatus(1);
        bedMapper.updateById(bed);

        // 保存或更新老人数据
        elder = insertOrUpdate(bed, checkInApplyDto.getCheckInElderDto());

        // 生成合同编号
        String contractNo = "HT" + CodeGenerator.generateContractNumber();

        // 新增签约办理
        insertContract(contractNo, elder, checkInApplyDto);

        // 新增入住信息
        CheckIn checkIn = insertCheckIn(elder, checkInApplyDto);

        // 新增入住配置信息
        insertCheckInConfig(checkIn.getId(), checkInApplyDto);
    }

    /**
     * 新增入住配置
     * @param checkInApplyDto
     */
    private void insertCheckInConfig(Long checkInId, CheckInApplyDto checkInApplyDto) {
        CheckInConfig checkInConfig = new CheckInConfig();
        BeanUtils.copyProperties(checkInApplyDto.getCheckInConfigDto(), checkInConfig);
        checkInConfig.setCheckInId(checkInId);
        checkInConfigMapper.insert(checkInConfig);
    }

    /**
     * 新增入住信息
     * @param elder
     * @param checkInApplyDto
     */
    private CheckIn insertCheckIn(Elder elder, CheckInApplyDto checkInApplyDto) {
        CheckIn checkIn = new CheckIn();
        checkIn.setElderId(elder.getId());
        checkIn.setElderName(elder.getName());
        checkIn.setIdCardNo(elder.getIdCardNo());
        checkIn.setNursingLevelName(checkInApplyDto.getCheckInConfigDto().getNursingLevelName());
        checkIn.setStartDate(checkInApplyDto.getCheckInConfigDto().getStartDate());
        checkIn.setEndDate(checkInApplyDto.getCheckInConfigDto().getEndDate());
        checkIn.setBedNumber(elder.getBedNumber());
        checkIn.setRemark(JSON.toJSONString(checkInApplyDto.getElderFamilyDtoList()));
        checkIn.setStatus(0);
        checkInMapper.insert(checkIn);
        return checkIn;
    }

    /**
     * 新增合同
     * @param contractNo
     * @param elder
     * @param checkInApplyDto
     */
    private void insertContract(String contractNo, Elder elder, CheckInApplyDto checkInApplyDto) {

        Contract contract = new Contract();
        // 属性拷贝
        BeanUtils.copyProperties(checkInApplyDto.getCheckInContractDto(), contract);
        contract.setContractNumber(contractNo);
        contract.setElderId(elder.getId());
        contract.setElderName(elder.getName());
        // 状态、开始时间、结束时间
        // 签约时间小于等于当前时间，合同生效中
        LocalDateTime checkInStartTime = checkInApplyDto.getCheckInConfigDto().getStartDate();
        LocalDateTime checkInEndTime = checkInApplyDto.getCheckInConfigDto().getEndDate();
        Integer status = checkInStartTime.isAfter(LocalDateTime.now()) ? 0 : 1;
        contract.setStatus(status);
        contract.setStartDate(checkInStartTime);
        contract.setEndDate(checkInEndTime);
        contractMapper.insert(contract);
    }

    /**
     * 新增或更新老人
     * @param bed
     * @param checkInElderDto
     * @return
     */
    private Elder insertOrUpdate(Bed bed, CheckInElderDto checkInElderDto) {

        // 准备老人数据
        Elder elder = new Elder();
        // 属性拷贝
        BeanUtils.copyProperties(checkInElderDto, elder);
        elder.setBedNumber(bed.getBedNumber());
        elder.setBedId(bed.getId());
        elder.setStatus(1);
        // 查询老人信息，（身份证号、状态不为1）
        LambdaQueryWrapper<Elder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Elder::getIdCardNo, checkInElderDto.getIdCardNo()).ne(Elder::getStatus, 1);
        Elder elderDb =  elderMapper.selectOne(lambdaQueryWrapper);
        if(ObjectUtils.isNotEmpty(elderDb)) {
            // 修改
            elderMapper.updateById(elder);
        }else {
            // 新增
            elderMapper.insert(elder);
        }
        return elder;
    }

    /**
     * 入住详情
     *
     * @param id
     * @return
     */
    @Override
    public CheckInDetailVo detail(Long id) {
        //创建一个空的详情对象
        CheckInDetailVo checkInDetailVo = new CheckInDetailVo();
        //1.设置入住配置响应信息
        CheckInConfigVo checkInConfigVo = new CheckInConfigVo();
        CheckIn checkIn = checkInMapper.selectCheckInById(id);
        BeanUtils.copyProperties(checkIn, checkInConfigVo);

        CheckInConfig checkInConfig = checkInConfigMapper.selectOne(new LambdaQueryWrapper<CheckInConfig>().eq(CheckInConfig::getCheckInId, id));
        BeanUtils.copyProperties(checkInConfig, checkInConfigVo);

        checkInDetailVo.setCheckInConfigVo(checkInConfigVo);

        //2.设置老人响应信息
        CheckInElderVo checkInElderVo = new CheckInElderVo();
        //获取老人ID
        Long elderId = checkIn.getElderId();
        Elder elder = elderMapper.selectById(elderId);
        BeanUtils.copyProperties(elder, checkInElderVo);
        //从身份证号中获取老人的年龄
        checkInElderVo.setAge(IdcardUtil.getAgeByIdCard(elder.getIdCardNo()));
        checkInDetailVo.setCheckInElderVo(checkInElderVo);

        //3.设置家属响应信息
        String remark = checkIn.getRemark();
        List<ElderFamilyVo> elderFamilyVos = JSON.parseArray(remark, ElderFamilyVo.class);
        checkInDetailVo.setElderFamilyVoList(elderFamilyVos);

        //4.设置签约办理响应信息
        Contract contract = contractMapper.selectOne(new LambdaQueryWrapper<Contract>().eq(Contract::getElderId, elderId));
        checkInDetailVo.setContract(contract);

        //5.返回结果
        return checkInDetailVo;

    }
}
