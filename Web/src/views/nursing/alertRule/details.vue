<template>
  <div class="detail-base ruleDetail">
    <div ref="mainHeight" class="bg-wt">
      <el-card header="报警规则">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          :label-width="120"
        >
          <el-card title="报警规则">
            <el-form-item label="所属产品：" prop="productKey">
              <el-select
                v-model="formData.productKey"
                clearable
                filterable
                placeholder="请选择"
                class="wt-300"
                @change="handleProduct"
              >
                <el-option
                  v-for="(item, index) in productData"
                  :key="index"
                  :value="item.productId"
                  :label="item.name"
                  title=""
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="服务：" prop="moduleId">
              <el-select
                v-model="formData.moduleId"
                clearable
                filterable
                placeholder="请选择"
                class="wt-300"
                :disabled="formData.productKey !== undefined ? false : true"
                @change="handleModele"
              >
                <el-option
                  v-for="(item, index) in itemData"
                  :key="index"
                  :value="item.service_id"
                  :label="item.service_id"
                  title=""
                >
                  {{ item.service_id }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="功能名称：" prop="functionId">
              <el-select
                v-model="formData.functionId"
                clearable
                filterable
                placeholder="请选择"
                class="wt-300"
                :disabled="formData.moduleId !== undefined ? false : true"
                @change="handleIdentifier"
              >
                <el-option
                  v-for="(item, index) in publishedData"
                  :key="index"
                  :value="item.property_name"
                  :label="item.property_name"
                  title=""
                >
                  {{ item.property_name }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关联设备：" prop="iotId">
              <el-select
                v-model="formData.iotId"
                clearable
                filterable
                placeholder="请选择"
                class="wt-300"
                :disabled="formData.productKey !== undefined ? false : true"
                @change="handleDevice"
              >
                <el-option
                  v-for="(item, index) in listData"
                  :key="index"
                  :value="item.iotId"
                  :label="item.deviceName"
                  title=""
                >
                  {{ item.deviceName }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="报警数据类型：" prop="alertDataType">
              <el-radio-group v-model="formData.alertDataType">
                <el-radio
                  v-for="(item, index) in dataType"
                  :key="index"
                  :label="item.id"
                  >{{ item.value }}</el-radio
                >
              </el-radio-group>
            </el-form-item>
            <el-form-item label="报警规则名称：" prop="alertRuleName">
              <el-input
                v-model="formData.alertRuleName"
                placeholder="请输入"
                class="wt-300"
                show-limit-number
                show-word-limit
                :maxlength="20"
              >
              </el-input>
            </el-form-item>
            <el-form-item label="持续周期：" prop="duration">
              <el-select
                v-model="formData.duration"
                clearable
                filterable
                placeholder="请选择"
                class="wt-300"
              >
                <el-option
                  v-for="(item, index) in continuePeriodData"
                  :key="index"
                  :value="item.value"
                  :label="item.label"
                  title=""
                >
                  {{ item.label }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="运算符：" prop="operator">
              <el-select
                v-model="formData.operator"
                clearable
                filterable
                placeholder="请选择"
                class="wt-300"
              >
                <el-option
                  v-for="(item, index) in operatorData"
                  :key="index"
                  :value="item.value"
                  :label="item.value"
                  title=""
                >
                  {{ item.value }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="阈值：" prop="value">
              <el-input-number
                v-model="formData.value"
                :step="1"
                large-number
                :max="99999999"
                placeholder="0.0"
                :decimal-places="1"
                @blur="textBlur"
                @change="textBlur"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="报警生效时段：" prop="alertEffectivePeriod">
              <el-time-picker
                v-model="formData.alertEffectivePeriod"
                class="wt-300"
                clearable
                is-range
                range-separator="-"
                value-format="HH:mm:ss"
                @pick="handleRangePick"
              />
            </el-form-item>
            <el-form-item label="报警沉默周期：" prop="alertSilentPeriod">
              <el-select
                v-model="formData.alertSilentPeriod"
                clearable
                filterable
                placeholder="请选择"
                class="wt-300"
              >
                <el-option
                  v-for="(item, index) in silenceCycleData"
                  :key="index"
                  :value="item.value"
                  :label="
                    item.id > 4 ? item.value + '小时' : item.value + '分钟'
                  "
                  title=""
                  @click="handlePeriod(item)"
                >
                  {{ item.id > 4 ? item.value + '小时' : item.value + '分钟' }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-card>
        </el-form>
      </el-card>
    </div>
    <div class="boxBottom fx fx-ct bg-wt">
      <el-button @click="handleReturn">返回</el-button>
      <el-button type="primary" @click="handleSub">提交</el-button>
    </div>
  </div>
</template>
<script setup name="DetailsRule">
import { ref, onMounted, nextTick } from 'vue';
import { getNum } from '@/utils/validate';
import dayjs from 'dayjs';
const { proxy } = getCurrentInstance();
// 接口
import { allProduct, getPublishedList, getDeviceList } from '@/api/nursing/device';
import {
  getAlertRule,
  addAlertRule,
  updateAlertRule,
} from '@/api/nursing/alertRule';
// 基础数据
import {
  operatorData,
  continuePeriodData,
  dataType,
  silenceCycleData,
} from '@/utils/commonData';
// ------定义变量------
const route = useRoute(); // 获取局部
const router = useRouter();
const ruleId = ref(null); // 规则id
const formData = ref({
  alertEffectivePeriod: ['00:00:00', '23:59:59'],
  status: 1,
});
const productData = ref([]); // 产品数据
const form = ref(null); // 定义表单ref
const publishedData = ref([]); // 功能
const itemData = ref([]); // 模块
const listData = ref(); // 关联设备
const published = ref({});
const formRef = ref(null);
// 表单校验
const rules = {
  productKey: [
    {
      required: true,
      message: '所属产品为空，请选择所属产品',
      trigger: 'change',
    },
  ],
  moduleId: [
    {
      required: true,
      message: '模块为空，请选择模块',
      trigger: 'change',
    },
  ],
  functionId: [
    {
      required: true,
      message: '功能名称为空，请选择功能名称',
      trigger: 'change',
    },
  ],
  iotId: [
    {
      required: true,
      message: '关联设备为空，请选择关联设备',
      trigger: 'change',
    },
  ],
  alertRuleName: [
    {
      required: true,
      message: '报警规则名称为空，请输入报警规则名称',
      trigger: 'blur',
    },
  ],
  operator: [
    {
      required: true,
      message: '运算符为空，请选择运算符',
      trigger: 'change',
    },
  ],
  value: [
    {
      required: true,
      message: '阈值为空，请输入阈值',
      trigger: 'change',
    },
  ],
  duration: [
    {
      required: true,
      message: '持续周期为空，请选择持续周期',
      trigger: 'change',
    },
  ],
  alertDataType: [
    {
      required: true,
      message: '报警数据类型为空，请选择报警数据类型',
      trigger: 'change',
    },
  ],
  alertEffectivePeriod: [
    {
      required: true,
      message: '报警生效时段为空，请选择报警生效时段',
      trigger: 'change',
    },
  ],
  alertSilentPeriod: [
    {
      required: true,
      message: '报警沉默周期为空，请选择报警沉默周期',
      trigger: 'change',
    },
  ],
};
// 生命周期
onMounted(() => {
  if (route.query.id) {
    ruleId.value = route.query.id;
    getFromData();
  }
  getProduct();
});
// ------定义方法------
// 获取详情
const getFromData = async () => {
  const res = await getAlertRule(ruleId.value);
  if (res.code === 200) {
    formData.value = res.data;
    formData.value.duration = String(formData.value.duration);
    formData.value.alertEffectivePeriod =
      formData.value.alertEffectivePeriod.split('~'); // 需要把日期转为数组
    getPublished(formData.value.productKey);
    getList(formData.value.productKey);
  }
};
// 提交表单
const handleSub = () => {
  proxy.$refs['formRef'].validate(async (valid) => {
    if (valid === true) {
      const form = formData.value;
      const num = form.alertSilentPeriod;
      if (typeof num === 'number') {
        form.alertSilentPeriod = num;
      } else {
        form.alertSilentPeriod = Number(getNum(num));
      }
      const obj = {
        ...form,
        alertSilentPeriod: form.alertSilentPeriod,
        alertDataType: Number(form.alertDataType),
        alertEffectivePeriod: form.alertEffectivePeriod.join('~'),
        duration:
          typeof num === 'number' ? form.duration : Number(form.duration),
        value: Number(form.value),
      };
      if (ruleId.value) {
        const res = await updateAlertRule(obj);
        if (res.code === 200) {
          proxy.$modal.msgSuccess('编辑成功');
          router.push({
            path: `/intelligence/alertRule`,
          });
        }
      } else {
        const res = await addAlertRule(obj);
        if (res.code === 200) {
          proxy.$modal.msgSuccess('添加成功');
          router.push({
            path: '/intelligence/alertRule',
          });
        }
      }
    }
  });
};
// 获取产品列表
const getProduct = async () => {
  const res = await allProduct();
  if (res.code === 200) {
    productData.value = res.data;
  }
};
// 获取运行状态
const getPublished = async (val) => {
  const params = {
    // ...published.value,
    productKey: val, // 产品key
  };
  const res = await getPublishedList(params); // 获取列表数据
  if (res.code === 200) {
    if (res.data !== undefined) {
      const data = res.data;
      console.log(data,111)
      itemData.value = [];
      // const obj = {
      //   functionBlockId: '-1',
      //   functionBlockName: '默认模块',
      // };
      if (data !== undefined) {
        itemData.value = data;
      }
      // publishedData.value = data.properties;
      // itemData.value.unshift(obj);
    }
  }
};
// 设备列表
// 获取列表数据
const getList = async (val) => {
  const pages = {
    pageSize: 100,
    pageNum: 1,
    productKey: val, // 产品key
  };
  const res = await getDeviceList(val); // 获取列表数据
  if (res.rows) {
    listData.value = res.rows;

    const obj = {
      id: '-1',
      iotId: '-1',
      deviceName: '全部设备',
    };
    listData.value.unshift(obj);
  }
};
// 触发产品
const handleProduct = (val, e) => {
  console.log(e);
  nextTick(() => {
    delete formData.value.moduleId;
    delete formData.value.iotId;
    delete formData.value.functionId;
    // form.value.clearValidate(['moduleId', 'iotId', 'functionId'])
  });
  const arr = productData.value.filter((item) => item.productId === val);
  formData.value.productName = arr[0].name;
  formData.value.productKey = val;
  getPublished(val);
  getList(val);
};
// 触发模块
const handleModele = (val, e) => {
  const data = itemData.value.filter(item=>item.service_id===val)
  publishedData.value = data[0].properties
  // formData.value.moduleName = '默认模块';
  // // 如果选择的是自定义模块需要传functionBlockId
  // if (val !== '-1') {
  //   published.value.functionBlockId = e.selectedOptions[0].value;
  // } else {
  //   // 否则删除functionBlockId，显示默认模块的数据
  //   delete published.value.functionBlockId;
  // }
  // getPublished(formData.value.productKey);
};
// 触发功能
const handleIdentifier = (val, e) => {
  const arr = publishedData.value.filter((item) => item.property_name === val);
  formData.value.functionName = arr[0].property_name;
};

// 触发设备
const handleDevice = (val) => {
  const arr = listData.value.filter((item) => item.iotId === val);
  formData.value.deviceName = arr[0].deviceName;
};
// 触发沉默周期
const handlePeriod = (val) => {
  if (val.id > 4) {
    formData.value.remark = val.value * 60;
  } else {
    formData.value.remark = val.value;
  }
};
// 时间段选择
const handleRangePick = (v) => {
  formData.value.alertEffectivePeriod = v;
};
// 阈值
const textBlur = () => {
  const data = Number(formData.value.value);
  // 最大值设置
  if (data > 99999999) {
    formData.value.value = '99999999';
  }
};
// 返回
const handleReturn = () => {
  router.go(-1);
};
</script>
