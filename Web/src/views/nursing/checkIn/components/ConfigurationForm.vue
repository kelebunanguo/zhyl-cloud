<template>
    <div class="info">
        <h3>入住配置</h3>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="入住期限" prop="checkInTime">
                    <el-date-picker type="datetimerange" :readonly="type === 'read'" v-model="checkInTime"
                        format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" :style="{ width: '100%' }"
                        start-placeholder="开始日期" end-placeholder="结束日期" range-separator="至" clearable
                        @change="hendleCheckInTime"></el-date-picker>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="费用期限" prop="feeTime">
                    <el-date-picker type="datetimerange" :readonly="type === 'read'" v-model="feeTime"
                        format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" :style="{ width: '100%' }"
                        start-placeholder="开始日期" end-placeholder="结束日期" range-separator="至" clearable
                        @change="hendleFeeTime"></el-date-picker>
                </el-form-item>
            </el-col>

        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="护理等级" prop="nursingLevelId">
                    <el-select v-model="formData.nursingLevelId" :disabled="type === 'read'" placeholder="请选择护理等级"
                        clearable :style="{ width: '100%' }" @change="handleNursing">
                        <el-option v-for="(item, index) in nursingLevelIdOptions" :key="index" :label="item.name"
                            :value="item.id" :disabled="item.disabled"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="入住床位" prop="bed">
                    <div v-if="type === 'read'">
                        {{ formData.bedNumber }}
                    </div>
                    <el-cascader v-else v-model="formData.bed" :options="bedIdOptions" :props="bedProps"
                        @change="handleFloor" :style="{ width: '100%' }" placeholder="请选择入住床位" clearable></el-cascader>
                </el-form-item>
            </el-col>

        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="押金" prop="deposit">
                    <el-input v-model="formData.deposit" :disabled="type === 'read'" type="text" placeholder="请输入押金"
                        clearable :style="{ width: '100%' }"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="护理费用" prop="nursingFee">
                    <el-input-number v-model="formData.nursingFee" placeholder="护理费用"
                        :disabled='true'></el-input-number>
                </el-form-item>
            </el-col>

        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="床位费用" prop="bedFee">
                    <el-input-number v-model="formData.bedFee" placeholder="床位费用" :disabled='true'></el-input-number>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="其他费用" prop="otherFees">
                    <el-input-number v-model="formData.otherFees" :disabled="type === 'read'"
                        placeholder="其他费用"></el-input-number>
                </el-form-item>
            </el-col>

        </el-row>
        <el-row gutter="15">
            <el-col :span="12">
                <el-form-item label="医保支付" prop="insurancePayment">
                    <el-input-number :disabled="type === 'read'" v-model="formData.insurancePayment"
                        placeholder="医保支付"></el-input-number>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="政府补贴" prop="governmentSubsidy">
                    <el-input-number :disabled="type === 'read'" v-model="formData.governmentSubsidy"
                        placeholder="政府补贴"></el-input-number>
                </el-form-item>
            </el-col>

        </el-row>
    </div>
</template>
<script setup>
import { getAllLevels } from '@/api/nursing/level'
import { getFloorBed, getRootById } from '@/api/nursing/floor'
import {
    ElMessage
}
    from 'element-plus'
import { onMounted } from 'vue'
const formRef = ref()
const data = reactive({
    formData: {
        checkInTime: '',
        feeTime: '',
        nursingLevelId: undefined,
        bed: [],
        deposit: 3000,
        nursingFee: undefined,
        bedFee: undefined,
        otherFees: 0,
        insurancePayment: 0,
        governmentSubsidy: 0,
    },
    rules: {
        checkInTime: [{
            required: true,
            message: '入住期限不能为空',
            trigger: 'change'
        }],
        feeTime: [{
            required: true,
            message: '费用期限不能为空',
            trigger: 'change'
        }],
        nursingLevelId: [{
            required: true,
            message: '请选择护理等级',
            trigger: 'change'
        }],
        bed: [{
            required: true,
            type: 'array',
            message: '请至少选择一个bed',
            trigger: 'change'
        }],
        deposit: [{
            required: true,
            message: '请输入押金',
            trigger: 'blur'
        }],
        nursingFee: [{
            required: true,
            message: '护理费用',
            trigger: 'blur'
        }],
        bedFee: [{
            required: true,
            message: '床位费用',
            trigger: 'blur'
        }],
        otherFees: [{
            required: true,
            message: '其他费用',
            trigger: 'blur'
        }],
        insurancePayment: [{
            required: true,
            message: '医保支付',
            trigger: 'blur'
        }],
        governmentSubsidy: [{
            required: true,
            message: '政府补贴',
            trigger: 'blur'
        }],
    }
})
const {
    formData,
    rules
} = toRefs(data)
const nursingLevelIdOptions = ref([])

const bedIdOptions = ref([])

onMounted(() => {
    //初始化护理等级
    getAllLevels().then((res) => {
        nursingLevelIdOptions.value = res.data;
    })

    //初始化楼层房间床位
    getFloorBed(0).then((res) => {
        if (res.code === 200) {
            bedIdOptions.value = res.data;
        }
    })
})


//选择床位之后，给属性赋值
//val===>['6','39','58']  第一个值为楼层id, 第二个为房间id,第三个为床位id
const handleFloor = (val) => {

    console.log(val)
    formData.value.bedId = val[2];
    getRootById(val[1]).then((res) => {
        if (res.code === 200) {
            formData.value.floorName = res.data.floorName;
            formData.value.floorId = res.data.floorId;
            formData.value.bedFee = res.data.price;
            formData.value.price = res.data.price;
            formData.value.code = res.data.code;
            formData.value.roomId = res.data.roomId;
        }
    })
}
//处理入住时间
const hendleCheckInTime = (val) => {
    if (val.length > 0) {
        formData.value.startDate = val[0];
        formData.value.endDate = val[1];
        formData.value.checkInTime = '1'
    }
}

//处理费用时间
const hendleFeeTime = (val) => {
    if (val.length > 0) {
        formData.value.feeStartDate = val[0];
        formData.value.feeEndDate = val[1];
        formData.value.feeTime = '1'
    }
}

//选择了护理等级之后，触发，给护理费用和护理名称赋值
const handleNursing = () => {
    if (formData.value.nursingLevelId) {
        //根据id从集合中找对应的护理等级对象
        const obj = nursingLevelIdOptions.value.find((item) => item.id === formData.value.nursingLevelId);
        console.log(obj)
        //给属性赋值
        formData.value.nursingFee = obj.fee;
        formData.value.nursingLevelName = obj.name;
    } else {
        formData.value.nursingCost = null;
    }
}

const bedOptions = ref([])
// props设置
const bedProps = ref({
    "multiple": false
})


const props = defineProps({
    configInfo: {
        type: Object,
        default: {},
    },
    type: {
        type: String,
        default: '',
    },
});

const checkInTime = ref([]);
const feeTime = ref([]);

watch(
    props,
    () => {
        formData.value = props.configInfo;
        formData.value.deposit = 3000;
        checkInTime.value = [props.configInfo.startDate, props.configInfo.endDate];
        feeTime.value = [props.configInfo.feeStartDate, props.configInfo.feeEndDate];
    },
    { immediate: true }
);


//声明父组件的方法
const emits = defineEmits(['getFormData']);
//监听数据变化，传递到父组件
watch(formData.value, (val) => {
    //调用父组件的方法
    emits('getFormData', val)
}, { immediate: true })

//暴露表单校验的数据给外面，给父组件
defineExpose({
    rules
})

</script>
<style></style>