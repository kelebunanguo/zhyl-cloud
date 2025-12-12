<!--设置护理人员-->

<template>
  <div class="dialog-form">
    <el-dialog v-model="formVisible" title="设置护理员" @close="onClickCloseBtn">
      <!-- 表单内容 -->
      <div class="dialogCenter">
        <el-form ref="formRef" :model="formData" :rules="rules" :label-width="120">
          <el-form-item label="护理员姓名：" prop="bedNumber">
            <el-select v-model="formData.bedNumber" multiple placeholder="请选择" class="wt-400">
              <el-option v-for="item in data" :key="item.userId" :label="item.nickName" :value="item.userId">
                {{ item.nickName }}
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="dialog-footer">
          <el-button @click="onClickCloseBtn">取 消</el-button>
          <el-button type="primary" @click="onSubmit">确 定</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { watch } from 'vue';
const { proxy } = getCurrentInstance();
// 获取父组件值、方法
const props = defineProps({
  // 弹层隐藏显示
  visible: {
    type: Boolean,
    default: false,
  },
  //   详情数据
  data: {
    type: Object,
    default: () => {
      return {};
    },
  },
  //   设置单个老人护理员的数据
  singleNurse: {
    type: Object,
    default: () => {
      return [];
    },
  },
});
// ------定义变量------
// 触发父级事件
const emit = defineEmits(['handleClose', 'handleSubmit']);
const resetType = ref('empty'); // 重置表单
const formRef = ref(null); // 表单
const formVisible = ref(false); // 弹窗
// 表单数据
const formData = ref({});
// 表单校验
const rules = {
  bedNumber: [
    {
      required: true,
      message: '护理员姓名为空，请选择护理员姓名',
      type: 'error',
      trigger: 'blur',
    },
    {
      validator: (val) => {
        if (val && val.length < 0) {
          return {
            result: false,
            message: '请选择护理员姓名',
            type: 'error',
          };
        }
        return true;
      },
    },
  ],
};
// 监听器，监听父级传递的visible值，控制弹窗显示隐藏
watch(
  () => props.visible,
  () => {
    formVisible.value = props.visible;
    formData.value.bedNumber = props.singleNurse;
  }
);
// 最多先择4个护理员
watch(
  () => formData.value.bedNumber,
  (val) => {
    if (val.length > 4) {
      formData.value.bedNumber = val.slice(0, 4);
    }
  }
);
// 定义方法
// 提交表单
const onSubmit = () => {
  proxy.$refs['formRef'].validate((valid) => {
    if (valid) {
      emit('handleSubmit', formData.value.bedNumber);
      // form.value.clearValidate()
    }
  });
};
const handleChange = (val) => {
  formData.value.bedNumber = val;
};
// 清除表单数据
const handleClear = () => {
  // 重置表单
  formRef.value.resetFields();
};
// 点击取消关闭
const onClickCloseBtn = () => {
  handleClear();
  emit('handleClose');
};
// 传递给父组件的方法
defineExpose({
  handleClear,
});
</script>
