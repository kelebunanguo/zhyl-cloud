<!-- 处理结果弹窗 -->
<template>
  <div class="dialog-form">
    <el-dialog
      title="填写处理结果"
      v-model="formVisible"
      class="elDialogBox"
      @close="cancel"
    >
      <!-- 表单内容 -->
      <div class="dialogCenter">
        <el-form
          ref="queryRef"
          :model="formData"
          :rules="rules"
          label-width="108px"
        >
          <el-form-item label="处理时间：" prop="processingTime">
            <el-date-picker
              v-model="formData.processingTime"
              placeholder="请选择"
              enable-time-picker
              allow-input
              clearable
              class="wt-400"
            />
          </el-form-item>
          <el-form-item label="处理结果：" prop="processingResult"
            ><el-input
              v-model="formData.processingResult"
              type="textarea"
              class="wt-400"
              placeholder="请输入"
              show-word-limit
              :maxlength="100"
              resize="none"
            >
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
const { proxy } = getCurrentInstance();
// 获取父组件值、方法
const props = defineProps({
  // 弹层隐藏显示
  visible: {
    type: Boolean,
    default: false,
  },
});
// ------定义变量------
// 触发父级事件
const emit = defineEmits(['handleClose', 'handleSub']);
const resetType = ref('empty'); // 重置表单
const form = ref(); // 表单
const formVisible = ref(false); // 弹窗
// 表单数据
const formData = ref({});

// 表单校验
const rules = {
  processingTime: [
    {
      required: true,
      message: '处理时间为空，请选择处理时间',
      trigger: 'change',
    },
  ],
  processingResult: [
    {
      required: true,
      message: '处理结果为空，请输入处理结果',
      trigger: 'blur',
    },
  ],
};
// 监听器，监听父级传递的visible值，控制弹窗显示隐藏
watch(
  () => props.visible,
  (val) => {
    formVisible.value = val;
  }
);
// -----定义方法------
// 提交表单
const submitForm = () => {
  proxy.$refs['queryRef'].validate((valid) => {
    if (valid) {
        emit('handleSub', formData.value);
        handleClear()
    }
  });
};
// 清除表单数据
const handleClear = () => {
  // 重置表单
  proxy.resetForm('queryRef');
};
// 点击取消关闭
const cancel = () => {
  handleClear();
  emit('handleClose');
};
// 向父组件暴露数据与方法
defineExpose({
  handleClear,
});
</script>
