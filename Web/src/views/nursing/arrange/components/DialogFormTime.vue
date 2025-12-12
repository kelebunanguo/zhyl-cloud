<!-- 改期弹窗 -->
<template>
  <div class="dialog-form">
    <el-dialog title="修改执行时间" v-model="formVisible" class="elDialogBox" @close="onClickCloseBtn">
      <!-- 表单内容 -->
      <div class="dialogCenter">
        <el-form ref="queryRef" :model="formData" :rules="rules" label-width="120px">
          <el-form-item label="期望服务时间：" name="estimatedServerTime"><el-date-picker
              v-model="formData.estimatedServerTime" type="datetime" placeholder="" />
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
import { ref, watch } from 'vue'
const { proxy } = getCurrentInstance();
import dayjs from 'dayjs'
// 获取父组件值、方法
const props = defineProps({
  // 弹层隐藏显示
  visible: {
    type: Boolean,
    default: false
  },
  timeData: {
    type: String
  }
})
// ------定义变量------
// 触发父级事件
const emit: Function = defineEmits(['handleClose', 'handleSubmit'])
const resetType = ref('empty') // 重置表单
const form = ref() // 表单
const formVisible = ref(false) // 弹窗
// 表单数据
const formData = ref({
  estimatedServerTime: ''
})
// 表单校验
const rules = {
  estimatedServerTime: [
    // 来访时间校验
    {
      required: true,
      message: '期望服务时间为空，请输入期望服务时间',
      type: 'error',
      trigger: 'change'
    }
  ]
}
// 监听器，监听父级传递的visible值，控制弹窗显示隐藏
watch(
  () => props.visible,
  (val) => {
    formVisible.value = props.visible
    if (val) {
      formData.value.estimatedServerTime = props.timeData
    }
  }
)
// -----定义方法------
// 提交表单
const submitForm = () => {
  proxy.$refs['queryRef'].validate((valid) => {
    let time = null
    if (formData.value.estimatedServerTime !== undefined) {
      // tdesign的日期选择有bug，选择的时间和实际组件抛出的时间相差8个小时
      time = dayjs(formData.value.estimatedServerTime).format(
        'YYYY-MM-DD HH:mm:ss'
      )
    }
    emit('handleSubmit', { estimatedServerTime: time })
    onClickCloseBtn()
  })
}
// 点击取消关闭
const onClickCloseBtn = () => {
  // 重置表单
  proxy.resetForm('queryRef');
  emit('handleClose')
}
</script>
<style lang="scss" scoped>
.t-popup {
  width: auto;
}
</style>
