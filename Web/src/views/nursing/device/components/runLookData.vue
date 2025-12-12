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
                <el-form-item label="时间范围：" name="name">
                  <el-select
                    v-model="timeValue"
                    filterable
                    placeholder="请选择"
                    @change="handleTime"
                  >
                    <el-option
                      v-for="(item, index) in timeData"
                      :key="index"
                      :value="item.id"
                      :label="item.value"
                      title=""
                    ></el-option>
                  </el-select>
                  <el-date-range-picker
                    v-if="typeValue === 3"
                    v-model="time"
                    :placeholder="['开始日期', '结束日期']"
                    @change="handleDate"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-form>
      <!-- end -->
      <el-table ref="multipleTableRef" :data="dialogData" style="width: 100%">
        <el-table-column label="序号" type="index" width="120" />
        <el-table-column prop="dataValue" label="原始值" />
        <el-table-column prop="alarmTime" label="时间" />
      </el-table>
      <Pagination
        v-show="pagination.total > 0"
        :total="pagination.total"
        v-model:page="pagination.pageNum"
        v-model:limit="pagination.pageSize"
        @pagination="getLists"
      />
    </div>
  </el-dialog>
</template>
<script setup>
import { ref, watch } from 'vue'
import { getTimestamp } from '@/utils/date';
import { timeData } from '@/utils/commonData';
import { getNum } from '@/utils/validate';
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
});
// ------定义变量------
const emit = defineEmits([
  'handleCloseDialog',
  'handleSearch',
  'getList',
  'onPage'
]); // 子组件获取父组件事件传值
const regTable = ref(); // 定义表格ref
const ruleForm = ref(null); // 定义表单ref
const visible = ref(false); // 用来控制弹层显示隐藏
const time = ref([]);
const timeValue = ref(0);
const typeValue = ref(0); // 时间筛选索引
// 监听弹层是显示还是隐藏
watch(props, (val) => {
  visible.value = val.dialogVisible;
});
// ------定义方法------
// 关闭弹层
const handleClose = () => {
  timeValue.value = 0;
  typeValue.value = 0;
  time.value = [];
  emit('handleCloseDialog');
};
const getLists = (val) => {
  emit('getList',val);
};
// 时间筛选、自定义除外
const handleTime = (val) => {
  typeValue.value = val;

  if (val < 3) {
    const data = timeData.filter((user) => user.id === val);
    emit('handleSearch', getNum(data[0].value));
    emit('onPage')
    time.value = [];
  }
};
// 日期筛选数据
const handleDate = (val) => {
  emit('handleSearch', val);
};
</script>
