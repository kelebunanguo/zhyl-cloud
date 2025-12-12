<!-- 取消原因弹窗 -->
<template>
  <div class="dialog-form">
    <el-dialog title="填写取消原因" v-model="formVisible" class="elDialogBox" @close="onClickCloseBtn">
      <!-- 表单内容 -->
      <div class="dialogCenter">
        <el-form ref="queryRef" :model="formData" :rules="rules" label-width="108px">
          <el-form-item label="取消原因：" name="reason"><el-input v-model="formData.reason" type="textarea" class="wt-400"
              placeholder="请输入" show-word-limit :maxlength="100" resize="none">
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="onClickCloseBtn">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
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
const emit: Function = defineEmits(['handleClose', 'handleEditForm']);
const resetType = ref('empty'); // 重置表单
const form = ref(); // 表单
const formVisible = ref(false); // 弹窗
// 表单数据
const formData = ref<Object | any>({
  reason: '',
});

// 表单校验
const rules = {
  //   取消说明校验
  reason: [
    {
      required: true,
      message: '取消原因为空，请输入取消原因',
      type: 'error',
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
// -----定义方法------
// 提交表单
const submitForm = () => {
  proxy.$refs['queryRef'].validate((valid) => {
    if (valid) {
      emit('handleEditForm', formData.value);
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
const onClickCloseBtn = () => {
  handleClear();
  emit('handleClose');
};
defineExpose({
  handleClear,
});
</script>
