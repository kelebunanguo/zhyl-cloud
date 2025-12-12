<template>
    <div class="info">
        <h3>签约办理</h3>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="合同名称" prop="contractName">
                    <el-input v-model="formData.contractName" :readonly="type === 'read'" type="text"
                        placeholder="请输入合同名称" clearable :style="{ width: '100%' }"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="签约日期" prop="signDate">
                    <el-date-picker type="datetime" :readonly="type === 'read'" v-model="formData.signDate"
                        format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" :style="{ width: '100%' }"
                        placeholder="请选择签约日期" clearable></el-date-picker>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="丙方姓名" prop="thirdPartyName">
                    <el-input v-model="formData.thirdPartyName" :readonly="type === 'read'" type="text"
                        placeholder="请输入丙方姓名" clearable :style="{ width: '100%' }"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="丙方联系方式" prop="thirdPartyPhone">
                    <el-input v-model="formData.thirdPartyPhone" :readonly="type === 'read'" type="text"
                        placeholder="请输入丙方联系方式" clearable :style="{ width: '100%' }"></el-input>
                </el-form-item>
            </el-col>

        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="入住合同" prop="agreementPath">
                    <div v-if="type === 'read'">
                        {{ formData.elderName }}的入住合同.pdf
                        <a :href="formData.agreementPath" class="searchPdf">查看</a>
                    </div>
                    <el-upload v-else ref="agreementPath" :file-list="agreementPathfileList"
                        :action="agreementPathAction" :before-upload="agreementPathBeforeUpload" accept=".pdf"
                        :headers="headers" :on-success="(res) => handleSuccess(res)" :on-remove="handleRemove">
                        <el-button size="small" type="primary" icon="el-icon-upload">点击上传</el-button>
                        <div slot="tip" class="el-upload__tip">只能上传不超过 2MB 的文件</div>
                    </el-upload>
                </el-form-item>
            </el-col>

        </el-row>
    </div>
</template>
<script setup>
import {
    ElMessage
}
    from 'element-plus'
const formRef = ref()

import { getToken } from '@/utils/auth';

const headers = {
    Authorization: 'Bearer ' + getToken(),
};
const data = reactive({
    formData: {
        contractName: undefined,
        signDate: null,
        thirdPartyName: undefined,
        thirdPartyPhone: undefined,
        agreementPath: null,
    },
    rules: {
        contractName: [{
            required: true,
            message: '请输入合同名称',
            trigger: 'blur'
        }],
        signDate: [{
            required: true,
            message: '请选择签约日期',
            trigger: 'change'
        }],
        memberName: [],
        memberPhone: [],
    }
})
const {
    formData,
    rules
} = toRefs(data)

// 上传请求路径
const agreementPathAction = ref('/dev-api/common/upload')
// 上传文件列表
const agreementPathfileList = ref([])
/**
 * @name: 上传之前的文件判断
 * @description: 上传之前的文件判断，判断文件大小文件类型等
 * @param {*} file
 * @return {*}
 */
function agreementPathBeforeUpload(file) {
    let isRightSize = file.size / 1024 / 1024 < 2
    if (!isRightSize) {
        ElMessage.error('文件大小超过 2MB')
    }
    return isRightSize
}

const props = defineProps({
    transactFormInfo: {
        type: Object,
        default: () => { }
    },
    type: {
        type: String,
        default: '',
    }
})

watch(
    props,
    () => {
        formData.value = props.transactFormInfo;
    },
    { immediate: true }
);


//上传成功之后给属性pdfUrl赋值
const handleSuccess = (val) => {
    formData.value['agreementPath'] = val.url
}
//删除文件给属性pdfUrl赋值为空
const handleRemove = () => {
    formData.value['agreementPath'] = ''
}

//定义emit函数，用于向父组件发送自定义事件
const emit = defineEmits(['getFormData']);
// 监听formData的变化，并在变化时触发getFormData自定义事件，将新值传递给父组件
watch((formData.value), (val) => {
    emit('getFormData', val)
}, { immediate: true })
// 将rules对象暴露给父组件使用
defineExpose({
    rules
})

</script>
<style>
.el-upload__tip {
    line-height: 1.2;
}
</style>