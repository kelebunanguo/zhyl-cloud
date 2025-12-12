<template>
    <div class="info">
        <h3>基本信息</h3>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="老人姓名" prop="name">
                    <el-input v-model="formData.name" type="text" :readonly="type === 'read'" placeholder="请输入老人姓名"
                        clearable :style="{ width: '100%' }"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="身份证号" prop="idCardNo">
                    <el-input v-model="formData.idCardNo" type="text" :readonly="type === 'read'" placeholder="请输入身份证号"
                        clearable :style="{ width: '100%' }"></el-input>
                </el-form-item>
            </el-col>

        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="出生日期" prop="birthday">
                    <el-date-picker v-model="formData.birthday" :readonly="type == 'read'" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" :style="{ width: '100%' }" placeholder="请选择出生日期"
                        clearable></el-date-picker>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="年龄" prop="age">
                    <el-input v-model="formData.age" type="text" :readonly="type == 'read'" placeholder="请输入年龄"
                        clearable :style="{ width: '100%' }"></el-input>
                </el-form-item>
            </el-col>

        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="性别" prop="sex">
                    <el-radio-group v-model="formData.sex" :readonly="type == 'read'" size="default">
                        <el-radio v-for="(item, index) in sexOptions" :key="index" :label="item.value"
                            :disabled="item.disabled">{{ item.label }}</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="联系方式" prop="phone">
                    <el-input v-model="formData.phone" type="text" :readonly="type == 'read'" placeholder="请输入联系方式"
                        clearable :style="{ width: '100%' }"></el-input>
                </el-form-item>
            </el-col>

        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="家庭住址" prop="address">
                    <el-input v-model="formData.address" type="textarea" :readonly="type == 'read'"
                        placeholder="请输入家庭住址" :autosize="{ minRows: 4, maxRows: 4 }"
                        :style="{ width: '100%' }"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="一寸照片" prop="image">
                    <el-image v-if="type === 'read'" style="width: 100px; height: 100px" :src="formData.image" />
                    <el-upload v-else ref="image" :file-list="imagefileList" :action="imageAction" :headers="headers"
                        :on-success="(res) => handleSuccess(res, 'image')" :on-remove="() => handleRemove('image')"
                        list-type="picture" :before-upload="imageBeforeUpload">
                        <el-button size="small" type="primary" icon="el-icon-upload">点击上传</el-button>
                    </el-upload>
                </el-form-item>
            </el-col>

        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="身份证人像面" prop="idCardPortraitImg">
                    <el-image v-if="type === 'read'" style="width: 100px; height: 100px"
                        :src="formData.idCardPortraitImg" />
                    <el-upload v-else ref="idCardPortraitImg" :file-list="idCardPortraitImgfileList"
                        :action="idCardPortraitImgAction" :headers="headers"
                        :on-success="(res) => handleSuccess(res, 'idCardPortraitImg')"
                        :on-remove="() => handleRemove('idCardPortraitImg')" list-type="picture"
                        :before-upload="idCardPortraitImgBeforeUpload">
                        <el-button size="small" type="primary" icon="el-icon-upload">点击上传</el-button>
                    </el-upload>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="身份证国徽面" prop="idCardNationalEmblemImg">
                    <el-image v-if="type === 'read'" style="width: 100px; height: 100px"
                        :src="formData.idCardNationalEmblemImg" />
                    <el-upload v-else ref="idCardNationalEmblemImg" :file-list="idCardNationalEmblemImgfileList"
                        :action="idCardNationalEmblemImgAction" :headers="headers"
                        :on-success="(res) => handleSuccess(res, 'idCardNationalEmblemImg')"
                        :on-remove="() => handleRemove('idCardNationalEmblemImg')" list-type="picture"
                        :before-upload="idCardNationalEmblemImgBeforeUpload">
                        <el-button size="small" type="primary" icon="el-icon-upload">点击上传</el-button>
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
import {
    getToken
} from '@/utils/auth'

const headers = {
    Authorization: 'Bearer ' + getToken()
}
const data = reactive({
    formData: {
        name: undefined,
        idCardNo: undefined,
        birthday: null,
        age: undefined,
        sex: 0,
        phone: undefined,
        address: undefined,
        image: null,
        idCardPortraitImg: null,
        idCardNationalEmblemImg: null,
    },
    rules: {
        name: [{
            required: true,
            message: '请输入老人姓名',
            trigger: 'blur'
        }],
        idCardNo: [{
            required: true,
            message: '请输入身份证号',
            trigger: 'blur'
        }],
        birthday: [{
            required: true,
            message: '请选择出生日期',
            trigger: 'change'
        }],
        age: [{
            required: true,
            message: '请输入年龄',
            trigger: 'blur'
        }],
        sex: [{
            required: true,
            message: '性别不能为空',
            trigger: 'change'
        }],
        phone: [{
            required: true,
            message: '请输入联系方式',
            trigger: 'blur'
        }],
        address: [{
            required: true,
            message: '请输入家庭住址',
            trigger: 'blur'
        }],
    }
})
const {
    formData,
    rules
} = toRefs(data)
const sexOptions = ref([{
    "label": "男",
    "value": 1
}, {
    "label": "女",
    "value": 0
}])

const imageAction = ref('/dev-api/common/upload')
// 上传文件列表
const imagefileList = ref([])
// 上传请求路径
const idCardPortraitImgAction = ref('/dev-api/common/upload')
// 上传文件列表
const idCardPortraitImgfileList = ref([])
// 上传请求路径
const idCardNationalEmblemImgAction = ref('/dev-api/common/upload')
// 上传文件列表
const idCardNationalEmblemImgfileList = ref([])


//接收父组件的数据
const props = defineProps({
    applyFormInfo: {
        type: Object,
        default: () => {
            return {}
        }
    },
    type: {
        type: String,
        default: () => {
            return {}
        }
    }
})

watch(props, () => {
    formData.value = props.applyFormInfo;
}, { immediate: true })

/**
 * @name: 上传之前的文件判断
 * @description: 上传之前的文件判断，判断文件大小文件类型等
 * @param {*} file
 * @return {*}
 */
function imageBeforeUpload(file) {
    let isRightSize = file.size / 1024 / 1024 < 2
    if (!isRightSize) {
        ElMessage.error('文件大小超过 2MB')
    }
    return isRightSize
}
/**
 * @name: 上传之前的文件判断
 * @description: 上传之前的文件判断，判断文件大小文件类型等
 * @param {*} file
 * @return {*}
 */
function idCardPortraitImgBeforeUpload(file) {
    let isRightSize = file.size / 1024 / 1024 < 2
    if (!isRightSize) {
        ElMessage.error('文件大小超过 2MB')
    }
    return isRightSize
}
/**
 * @name: 上传之前的文件判断
 * @description: 上传之前的文件判断，判断文件大小文件类型等
 * @param {*} file
 * @return {*}
 */
function idCardNationalEmblemImgBeforeUpload(file) {
    let isRightSize = file.size / 1024 / 1024 < 2
    if (!isRightSize) {
        ElMessage.error('文件大小超过 2MB')
    }
    return isRightSize
}
/**
 * @name: 表单提交
 * @description: 表单提交方法
 * @return {*}
 */
function submitForm() {
    formRef.value.validate((valid) => {
        if (!valid) return
        // TODO 提交表单
    })
}
/**
 * @name: 表单重置
 * @description: 表单重置方法
 * @return {*}
 */
function resetForm() {
    formRef.value.resetFields()
}

//上传文件成功
const handleSuccess = (res, key) => {
    formData.value[key] = res.url;
}
//移除文件
const handleRemove = (key) => {
    formData.value[key] = '';
}

//声明父组件的方法
const emit = defineEmits(['getFormData'])
//监听数据，如果发生变化，则发送数据给父组件
watch(formData.value, (val) => {
    emit('getFormData', val)
}, { immediate: true })

//把数据暴露出去，引用该组件的可以获取里面的数据
defineExpose({
    rules
})

</script>
<style>
.el-upload__tip {
    line-height: 1.2;
}
</style>
<style src="../index.scss"></style>