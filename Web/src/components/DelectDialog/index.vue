<template>
  <div class="deleteDialog baseDialog">
    <el-dialog
      v-model="dialogVisible"
      :title="title ? title : '确认删除'"
      :footer="false"
      @close="handleClose"
    >
      <div>
        <div v-if="text">此操作将{{ text }}，是否继续？</div>
        <div v-else>此操作将删除该{{ deleteText }}，是否继续？</div>
      </div>

      <!-- 此操作将永久删除这条信息，是否继续？ -->
      <div class="dialog-footer">
        <el-button @click="handleClose">
          <span>取消</span>
        </el-button>
        <el-button type="primary" @click="handleSubmit">
          <span>确定</span>
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script setup>
// 获取父组件值、方法
const props = defineProps({
  // 弹层隐藏显示
  visible: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '',
  },
  text: {
    type: String,
    default: '',
  },
  deleteText: {
    type: String,
    default: '',
  },
});
// ------定义变量------
const emit = defineEmits(['handleClose', 'handleDelete']); // 子组件获取父组件事件传值
const dialogVisible = ref(false);
watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal;
  }
);
// ------定义方法------
// 关闭弹层
const handleClose = () => {
  emit('handleClose');
};
// 提交确定删除
const handleSubmit = () => {
  emit('handleDelete');
};
</script>
