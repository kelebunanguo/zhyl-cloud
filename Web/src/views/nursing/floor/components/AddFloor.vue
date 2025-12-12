<!-- 楼层新增编辑弹窗 -->
<template>
  <div class="dialog-form">
    <el-dialog :title="pageTitle + '楼层'" v-model="formVisible" @close="onClickCloseBtn">
      <!-- 表单内容 -->
      <div class="dialogCenter">
        <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
          <el-form-item label="楼层名称：" prop="name">
            <el-input v-model="formData.name" class="wt-400" placeholder="请输入" clearable show-limit-number
              :maxlength="5">
            </el-input>
          </el-form-item>
          <el-form-item label="排序：" prop="code">
            <el-input-number v-model="formData.code" :min="minNumber" large-number :max="maxNumber" :decimal-places="0"
              @blur="textBlurNo" @change="textBlurNo"></el-input-number>
          </el-form-item>

          <!-- <el-form-item class="dialog-footer">
            <button class="bt bt-grey wt-60" type="reset">取消</button>
            <button
              v-if="formData.id"
              class="bt-red wt-60"
              :class="roomVoList.length > 0 ? 'bt-forbid' : ''"
              type="button"
              @click="handleDelete(formData.id)"
            >
              <span>删除</span>
            </button>
            <button theme="primary" type="submit" class="bt wt-60">
              <span>确定</span>
            </button>
          </el-form-item> -->
        </el-form>
        <div class="dialog-footer">
          <el-button @click="onClickCloseBtn">取 消</el-button>
          <el-button v-if="formData.id" class="bt-red wt-60" :class="roomVoList.length > 0 ? 'bt-forbid' : ''"
            type="button" @click="handleDelete(formData.id)">
            <span>删除</span>
          </el-button>
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
  // 房间数据
  roomVoList: {
    type: Array,
    default: () => {
      return [];
    },
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
  // 标题
  title: {
    type: String,
    default: '新增',
  },
});
// ------定义变量------
// 触发父级事件
const emit = defineEmits([
  'handleClose',
  'fetchData',
  'handleAdd',
  'handleEdit',
  'handleDelete',
]);
const userStore = useUserStore();
const resetType = ref('empty'); // 重置表单
const form = ref(); // 表单
const formVisible = ref(false); // 弹窗
// 表单数据
const formData = ref({
  code: 1,
});
// 表单校验
const rules = {
  name: [
    {
      required: true,
      message: '楼层名称为空，请输入楼层名称',
      trigger: 'blur',
    },
  ],
  code: [
    // 排序校验
    {
      required: true,
      message: '排序为空，请输入排序',
      trigger: 'blur',
    },
  ],
};
// 弹窗标题
const pageTitle = ref();
// 监听器，监听父级传递的visible值，控制弹窗显示隐藏
watch(
  () => props.visible,
  (val) => {
    formVisible.value = val;
    pageTitle.value = props.title;
  }
);
// 监听器，监听父级传递的data值，控制表单数据
watch(
  () => props.data,
  (val) => {
    formData.value.id = val.id;
    formData.value.name = val.name;
    formData.value.code = val.code;
  }
);
// -----定义方法------
// 提交表单
const onSubmit = () => {
  proxy.$refs['formRef'].validate((valid) => {
    if (valid) {
      if (props.title === '新增') {
        // 调用新增接口
        formData.value.id = '';
        emit('handleAdd', formData.value);
      } else {
        // 调用编辑接口
        emit('handleEdit', formData.value);
      }
      userStore.setFloorInfo(JSON.parse(JSON.stringify(formData.value)));
    }
  });
};
// 清除表单数据
const handleClear = () => {
  // 重置表单
  proxy.resetForm('formRef');
  //   fromRef.value.reset();
  formData.value.code = 1;
};
// 点击取消关闭
const onClickCloseBtn = () => {
  handleClear();
  emit('handleClose');
};
// 监听排序
const textBlurNo = () => {
  const data = Number(formData.value.code);
  if (data <= 1) {
    formData.value.code = 1;
  }
  // 最大值设置
  if (data > 99999999) {
    formData.value.code = 99999999;
  }
};
// 删除
const handleDelete = (id) => {
  // 楼层下有房间，禁止删除
  if (props.roomVoList.length === 0) {
    emit('handleDelete', id);
  }
};
// 传递给父组件的方法
defineExpose({
  handleClear,
});
</script>
