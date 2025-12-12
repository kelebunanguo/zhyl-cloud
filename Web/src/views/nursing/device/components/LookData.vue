<template>
  <el-dialog
    title="查看数据"
    v-model="visible"
    class="oldSelectDialog"
    @close="handleClose"
  >
    <div class="dialogCenter">
      <!-- 搜索 -->
      <el-form ref="ruleForm" :data="pagination" :label-width="116">
        <el-row>
          <el-col :span="24">
            <el-row>
              <el-col :span="9">
                <el-form-item label="老人姓名：" name="name">
                  <el-input
                    v-model="pagination.name"
                    class="form-item-content"
                    clearable
                    type="search"
                    placeholder="请输入"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="9">
                <el-form-item
                  label="老人身份证号："
                  name="idCardNo"
                  class="label-wt"
                >
                  <el-input
                    v-model="pagination.idCardNo"
                    class="form-item-content"
                    clearable
                    type="search"
                    placeholder="请输入"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 按钮区域 -->
                <div class="searchBtn">
                  <el-button @click="handleReset">重 置</el-button>
                  <el-button type="primary" @click="handleSearch"
                    >搜 索</el-button
                  >
                </div>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-form>
      <!-- end -->
      <el-table
        ref="multipleTableRef"
        :data="dialogData"
        style="width: 100%"
        @row-click="chooseone"
        @current-change="handleCurrentChange"
      >
        <el-table-column width="55">
          <template #default="scope">
            <el-radio v-model="selectedRowKeys" :label="scope.row.id"
              ><span class="el-radio__label"></span
            ></el-radio>
          </template>
        </el-table-column>
        <el-table-column label="序号" type="index" width="50" />
        <el-table-column prop="name" label="老人姓名" width="180" />
        <el-table-column prop="idCardNo" label="老人身份证号" />
        <el-table-column prop="bedNumber" label="床位号" width="180" />
      </el-table>
      <Pagination
        v-show="pagination.total > 0"
        :total="pagination.total"
        v-model:page="pagination.pageNum"
        v-model:limit="pagination.pageSize"
        @pagination="getList"
      />
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSub">确定</el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script setup>
import { nextTick, onMounted, ref, watch } from 'vue';
import { getTimestamp } from '@/utils/date';
import { timeData } from '@/utils/commonData';
import { getNum } from '@/utils/validate';
import { ElMessage } from 'element-plus';
// 获取父组件值、方法
const props = defineProps({
  // 获取列表数据
  dialogData: {
    type: Array,
    default: () => [],
  },
  // 弹层隐藏显示
  dialogVisible: {
    type: Boolean,
    default: false,
  },
  // 分页
  pagination: {
    type: Object,
    default: () => ({}),
  },
  // 加载状态
  dataLoading: {
    type: Boolean,
    default: false,
  },
  // 基本信息
  formData: {
    type: Object,
    default: () => ({}),
  },
});
// ------定义变量------
const emit = defineEmits([
  'handleCloseDialog',
  'getCurrent',
  'getOldList',
  'handleSelectOld',
  'handleSearch',
  'handleReset',
]); // 子组件获取父组件事件传值
const regTable = ref(); // 定义表格ref
const ruleForm = ref(null); // 定义表单ref
const visible = ref(false); // 用来控制弹层显示隐藏
const selectedRowKeys = ref(''); // 选中的数据
const checkTabData = reactive({}); // 选中的list
const currentSelectItem = ref({});
// 监听弹层是显示还是隐藏
watch(props, (val) => {
  visible.value = val.dialogVisible;
  // console.log(visible.value)
  if (visible.value) {
    // 回显选中的老人
    if (val.formData.elderId !== undefined) {
      selectedRowKeys.value = val.formData.elderId;
    } else {
      selectedRowKeys.value = '';
    }
  }
});
// ------定义方法------
// 确认提交
const handleSub = async () => {
  // checkTabData.value是选择的内容传给父组件
  if (selectedRowKeys.value.length === 0) {
    ElMessage.error('未选择老人，请选择老人信息');
  } else {
    emit('handleSelectOld', checkTabData.value);
    handleClose();
  }
};
// 关闭弹层
const handleClose = () => {
  ruleForm.value.resetFields(); // 清除表单
  // selectedRowKeys.value = [];
  // visible.value = false
  emit('handleCloseDialog');
};
// 触发复选框
const handleChangeSelection = (index, { selectedRowData }) => {
  selectedRowKeys.value = index;
  checkTabData.value = selectedRowData;
};
// 点击翻页
const onPageChange = (pageInfo) => {
  emit('getCurrent', pageInfo);
};
// 搜索
const handleSearch = () => {
  emit('handleSearch');
};
// 清空搜索数据
const handleReset = () => {
  console.log(ruleForm.value);
  ruleForm.value.resetFields(); // 清除表单
  emit('handleReset');
};
const getList = (val) => {
  emit('getOldList', val);
};
// 触发单选框
const handleCurrentChange = (row) => {
  checkTabData.value = row;
};
const chooseone = (row) => {
  selectedRowKeys.value = row.elderId;
};
</script>
