<template>
  <div class="deleteDialog baseDialog warnDialog">
    <el-dialog
      title="报警提醒"
      v-model="dialogVisible"
      width="560"
      @close="handleClose()"
    >
      <div class="warnBox">
        <ul>
          <li><label>接入位置：</label>{{ data.accessLocation }}</li>
          <li><label>所属产品：</label>{{ data.productName }}</li>
          <li><label>功能名称：</label>{{ data.functionName }}</li>
          <li><label>数据值：</label>{{ data.dataValue }}</li>
        </ul>
        <div class="time">{{ time }} 秒后，弹窗自动关闭</div>
      </div>

      <!-- 此操作将永久删除这条信息，是否继续？ -->
      <div class="dialog-footer">
        <el-button
          class="bt-grey wt-60"
          @click="handleClose"
        >
          <span>取消</span>
        </el-button>
        <el-button
          type="primary"
          class="bt wt-60"
          @click="handleSubmit"
        >
          <span>查看</span>
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
// 获取父组件值、方法
const props = defineProps({
  // 弹层隐藏显示
  visible: {
    type: Boolean,
    default: false,
  },
  data: {
    type: Object,
    default: () => {
      return {};
    },
  },
});
// ------定义变量------
const emit = defineEmits(['handleClose', 'handleSubmit']); // 子组件获取父组件事件传值
const dialogVisible = ref(false);
const times = ref(null);
const time = ref(10);
watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal;
    if (newVal) {
      // 弹层10秒后关闭
      times.value = setInterval(() => {
        if (time.value > 0) {
          time.value--;
        } else {
          time.value = 10;
          clearInterval(times.value);
          emit('handleClose');
        }
      }, 1000);
    }
  }
);
// ------定义方法------
onMounted(()=>{
    dialogVisible.value = props.visible;
})
// 关闭弹层
const handleClose = () => {
  emit('handleClose');
};
// 提交确定删除
const handleSubmit = () => {
  emit('handleSubmit');
};
</script>
