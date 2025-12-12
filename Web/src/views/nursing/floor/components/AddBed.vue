<!-- 楼层新增编辑弹窗 -->
<template>
  <div class="dialog-form">
    <el-dialog :title="pageTitle + '床位'" v-model="formVisible" @close="onClickCloseBtn">
      <!-- 表单内容 -->
      <div class="dialogCenter">
        <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
          <el-form-item label="床位名称：" prop="bedNumber">
            <el-input v-model="formData.bedNumber" class="wt-400" placeholder="请输入" clearable show-limit-number
              :maxlength="10">
            </el-input>
          </el-form-item>
          <el-form-item label="排序：" prop="sort">
            <el-input-number v-model="formData.sort" :min="minNumber" large-number :max="maxNumber" :decimal-places="0"
              @blur="textBlurNo" @change="textBlurNo"></el-input-number>
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
const { proxy } = getCurrentInstance();
import useUserStore from '@/store/modules/user';
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
  // 最小值
  minNumber: {
    type: Number,
    default: 1,
  },
  // 最大值
  maxNumber: {
    type: Number,
    default: 1,
  },
  title: {
    type: String,
    default: '',
  },
});
// ------定义变量------
// 触发父级事件
const emit = defineEmits([
  'handleClose',
  'fetchData',
  'handleAdd',
  'handleEdit',
]);
const resetType = ref('empty'); // 重置表单
const formRef = ref(null); // 表单
const formVisible = ref(false); // 弹窗
const isChick = ref(false); // 防止连续点击提交按钮

// 表单数据
const formData = ref({
  bedNumber: '',
  sort: 1,
});
// 表单校验
const rules = {
  bedNumber: [
    {
      required: true,
      message: '床位号为空，请输入床位号',
      trigger: 'blur',
    },
  ],
};
// 弹窗标题
const pageTitle = ref();
// 监听器，监听父级传递的visible值，控制弹窗显示隐藏
watch(
  () => props.visible,
  () => {
    formVisible.value = props.visible;
    pageTitle.value = props.title;
  }
);
// 监听器，监听父级传递的data值，控制表单数据
watch(
  () => props.data,
  (val) => {
    if (val.id) {
      formData.value.id = val.id;
      formData.value.bedNumber = val.bedNumber;
      formData.value.sort = val.sort;
    }
  }
);
// -----定义方法------
// 提交表单
const onSubmit = () => {
  proxy.$refs['formRef'].validate((valid) => {
    if (valid) {
      if (!isChick.value) {
        isChick.value = true;
        if (props.title === '新增') {
          // 调用新增接口
          emit('handleAdd', formData.value);
        } else {
          // 调用编辑接口
          emit('handleEdit', formData.value);
        }
        const time = setTimeout(() => {
          isChick.value = false;
          clearTimeout(time);
        }, 3000);
      }
    }
  });
};
// 清除表单数据
const handleClear = () => {
  // 重置表单
  formRef.value.resetFields();
  formData.value.sort = 1;
};
// 点击取消关闭
const onClickCloseBtn = () => {
  handleClear();
  emit('handleClose');
};
// 监听排序
const textBlurNo = () => {
  const data = formData.value.sort;
  if (data <= 1) {
    formData.value.sort = 1;
  }
  // 最大值设置
  if (data > 99999999) {
    formData.value.sort = 99999999;
  }
};
// 传递给父组件的方法
defineExpose({
  handleClear,
});
</script>
