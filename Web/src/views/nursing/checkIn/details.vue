<template>
    <el-form 
        ref="formRef" 
        :model="formData" 
        :rules="rules" 
        size="default" 
        label-width="100px">

        <!-- 基本信息 -->
        <ApplyForm ref="applyForm" :type="type" :applyFormInfo="applyFormInfo"  @getFormData="getFormData"></ApplyForm>
        
        <!-- 家属信息 -->
        <Family :type="type" :familyInfo="familyInfo" @setFamily="setFamily"></Family>

        <!-- 入住配置 -->
        <ConfigurationForm :type="type" :configInfo="configInfo" ref="configForm" @getFormData="getFormData"></ConfigurationForm>

        <!-- 签约办理 -->
        <TransactForm ref="transactForm" @getFormData="getFormData" :type="type" :transactFormInfo="transactFormInfo"></TransactForm>

        <!-- 提交按钮 -->
        <div class="info center">
            <el-button type="primary" @click="submitForm">提交</el-button>
        </div>
    </el-form>
</template>

<script setup>
//引入组件
import ApplyForm from './components/ApplyForm.vue'
import Family from './components/Family.vue'
import ConfigurationForm from './components/ConfigurationForm.vue'
import TransactForm from './components/TransactForm.vue'

import { applyCheckIn ,getCheckInInfo } from '@/api/nursing/checkIn';

import { ElMessage } from 'element-plus'
//表单数据
const formData = ref({})
//表单验证
const rules = ref({})
//表单对象
const formRef = ref({})

//基本信息组件
const applyForm = ref({})

const configForm = ref({})

//家属信息列表
const familyUserData = ref([]);

//路由对象
const router = useRouter();

const transactForm = ref({}); //签约办理数据
const route = useRoute();
const type = ref('');

const applyFormInfo = ref({});
const familyInfo = ref([]);
const configInfo = ref({});
const transactFormInfo = ref({});

onMounted(()=>{
    type.value = route.query.type;
    if(type.value === 'read'){
        getCheckIn(route.query.id)
    }
})

//查看入住详情
const getCheckIn = (id) => {
    getCheckInInfo(id).then((res) => {
    applyFormInfo.value = res.data.checkInElderVo;
    familyInfo.value = res.data.elderFamilyVoList;
    configInfo.value = res.data.checkInConfigVo;
    transactFormInfo.value = res.data.contract;
  });
};

//提交表单
const submitForm = () => {
    console.log(formData.value,"---------------")
    //获取所有表单中的rules(校验规则)，全部合并到rules中
    rules.value = {
        ...applyForm.value.rules,
        ...configForm.value.rules,
        ...transactForm.value.rules
    }
    //校验表单
    formRef.value.validate((valid) => {
        //校验不成功，则终止提交
        if (!valid) return;
        


        //组装请求参数
        const params = {
            //基本信息
            checkInElderDto: {
                name: formData.value.name, // 姓名
                idCardNo: formData.value.idCardNo, // 身份证号码
                birthday: formData.value.birthday, // 出生日期
                age: formData.value.age, // 年龄
                sex: formData.value.sex, // 性别
                phone: formData.value.phone, // 电话号码
                address: formData.value.address, // 地址
                image: formData.value.image, // 一寸照片
                idCardPortraitImg: formData.value.idCardPortraitImg, // 身份证人像面图片
                idCardNationalEmblemImg: formData.value.idCardNationalEmblemImg, // 身份证国徽面图片
            },
            //家属信息
            elderFamilyDtoList: familyUserData.value,
            //入住配置
            checkInConfigDto: {
                startDate: formData.value.startDate,
                endDate: formData.value.endDate,
                feeStartDate: formData.value.feeStartDate,
                feeEndDate: formData.value.feeEndDate,
                nursingLevelId: formData.value.nursingLevelId,
                nursingLevelName: formData.value.nursingLevelName,
                bedFee: formData.value.bedFee,
                bedId: formData.value.bedId,
                code: formData.value.code,
                floorId: formData.value.floorId,
                floorName: formData.value.floorName,
                governmentSubsidy: formData.value.governmentSubsidy,
                insurancePayment: formData.value.insurancePayment,
                nursingFee: formData.value.nursingFee,
                otherFees: formData.value.otherFees,
                roomId: formData.value.roomId,
                deposit: 3000
            },
            //签约办理
            checkInContractDto: {
                thirdPartyName: formData.value.thirdPartyName,
                thirdPartyPhone: formData.value.thirdPartyPhone,
                contractName: formData.value.contractName,
                agreementPath: formData.value.agreementPath,
                signDate: formData.value.signDate
            }
        }
        console.log(params)

        //发起请求-申请入住
        applyCheckIn(params).then(res => {
            if (res.code == 200) {
                ElMessage({ type: 'success', message: '提交成功' })
                router.push({path: '/enterQuit/checkIn'});//跳转到列表查询
            } else {
                ElMessage({
                    type: 'error',
                    message: '提交失败'
                })
            }
        })
    });
}

//获取子组件的表单数据
const getFormData = (val) => {
    //合并数据到formData
    formData.value = {...formData.value, ...val};
}

//接收家属信息
const setFamily = (val) => {
    familyUserData.value =  val
}

</script>