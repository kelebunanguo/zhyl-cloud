<!-- 执行弹窗 -->
<template>
  <div class="dialog-form">
    <el-dialog title="新增执行记录" v-model="formVisible" class="elDialogBox" @close="onClickCloseBtn">
      <!-- 表单内容 -->
      <div class="dialogCenter">
        <el-form ref="queryRef" :model="formData" :rules="rules" label-width="108px">
          <el-form-item label="执行时间：" name="estimatedServerTime">
            <el-date-picker v-model="formData.estimatedServerTime" type="datetime" placeholder=""
              :default-time="defaultTime" />
          </el-form-item>
          <el-form-item label="执行图片：" name="taskImage">
            <image-upload v-model="formData.taskImage" />
          </el-form-item>
          <el-form-item label="执行记录：" name="mark">
            <el-input v-model="formData.mark" type="textarea" class="wt-400" placeholder="请输入" show-word-limit
              :maxlength="100" resize="none">
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
import dayjs from 'dayjs';
// 基础数据
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
const emit: Function = defineEmits(['handleClose', 'handleSubmit']);
const resetType = ref('empty'); // 重置表单
const form = ref(); // 表单
const formVisible = ref(false); // 弹窗
// 表单数据
const formData = ref({
  estimatedServerTime: '',
  mark: '',
});
const autoUpload = ref(true); // 是否在选择文件后自动发起请求上传文件
const photoFile = ref([]); // 绑定上传的文件
const sizeLimit = ref({
  size: 2,
  unit: 'MB',
  message: '图片大小超过2m，请重新上传',
}); // 图片的大小限制
const defaultTime = new Date(2000, 1, 1, 12, 0, 0);
// 表单校验
const rules = {
  estimatedServerTime: [
    // 房型类型校验
    {
      required: true,
      message: '执行时间为空，请选择执行时间',
      type: 'error',
      trigger: 'blur',
    },
  ],
  //   图片
  taskImage: [
    {
      required: true,
      message: '执行图片为空，请上传执行图片',
      type: 'error',
      trigger: 'change',
    },
  ],
  //   介绍
  mark: [
    {
      required: true,
      message: '执行记录为空，请输入执行记录',
      type: 'error',
      trigger: 'blur',
    },
  ],
};
// 监听器，监听父级传递的visible值，控制弹窗显示隐藏
watch(
  () => props.visible,
  (val) => {
    formVisible.value = props.visible;
    if (val) {
      formData.value.estimatedServerTime = dayjs().format(
        'YYYY-MM-DD HH:mm:ss'
      );
    }
  }
);
// -----定义方法------
// 提交表单
const submitForm = () => {
  proxy.$refs['queryRef'].validate((valid) => {
    if (valid) {
      emit('handleSubmit', formData.value);
      handleClear();
    }
  });
};
// 获取时间
const handleChange = (date) => {
  formData.value.estimatedServerTime = dayjs(date).valueOf();
};
// 清除表单数据
const handleClear = () => {
  // 重置表单
  proxy.resetForm('queryRef');
  formData.value.mark = '';
  formData.value.estimatedServerTime = '';
  photoFile.value = [];
  formData.value.taskImage = '';
};
// 点击取消关闭
const onClickCloseBtn = () => {
  handleClear();
  emit('handleClose');
};
// 移除图片时将图片设置为默认图片
const remove = () => {
  photoFile.value = [];
  formData.value.taskImage = '';
};
// 上传图片失败
const handleFail = () => {
  MessagePlugin.error(`文件上传失败`);
};
// 上传成功后触发。
const handleSuccess = (params) => {
  const photo = params.response.data;
  formData.value.taskImage = photo;
  photoFile.value[0].response.url = photo;
  photoFile.value[0].url = photo;
};
// 限制图片的大小
const beforeUpload = (file) => {
  if (file.size > 2 * 1024 * 1024) {
    MessagePlugin.error('图片大小超过2M，请重新上传');
    return false;
  }
  return true;
};
defineExpose({
  handleClear,
});
</script>
<style lang="scss" scoped>
.expectTime {
  width: 400px;
}
</style>
