<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="设备名称" prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="timeData">
        <el-date-picker
          clearable
          v-model="timeData"
          type="daterange"
          range-separator="-"
          value-format="YYYY-MM-DD "
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- tab导航 -->
    <div class="marg-main newBox">
      <SwitchBar
        ref="tabBar"
        :data="tabStatusData"
        @change-id="changeId"
      ></SwitchBar>
    </div>
    <!-- 表格 -->
    <el-table v-loading="loading" :data="alertDataList">
      <el-table-column label="序号" type="index" align="center" width="55" />
      <el-table-column
        label="设备名称"
        align="center"
        prop="deviceName"
        width="180"
      />
      <el-table-column
        label="所属产品"
        align="center"
        prop="productName"
        width="200"
      />
      <el-table-column
        label="报警数据类型"
        align="center"
        prop="type"
        width="180"
      >
        <template #default="scope">{{
          scope.row.type === 0 ? '老人异常数据' : '设备异常数据'
        }}</template>
      </el-table-column>
      <el-table-column
        label="接入位置"
        align="center"
        prop="remark"
        width="180"
      />
      <el-table-column
        label="数据值"
        align="center"
        prop="dataValue"
        width="180"
      />
      <el-table-column
        label="报警时间"
        align="center"
        prop="createTime"
        width="200"
      />
      <el-table-column
        label="报警规则"
        align="center"
        prop="alertReason"
        width="200"
      />
      <el-table-column
        label="处理结果"
        align="center"
        prop="processingResult"
        width="320"
      >
        <template #default="scope">{{
          scope.row.processingResult ? scope.row.processingResult : '--'
        }}</template>
      </el-table-column>
      <el-table-column
        label="处理人"
        align="center"
        prop="processorName"
        width="180"
      >
        <template #default="scope">{{
          scope.row.processorName ? scope.row.processorName : '--'
        }}</template>
      </el-table-column>
      <el-table-column
        label="处理时间"
        align="center"
        prop="processingTime"
        width="300"
      >
        <template #default="scope">
          <span>{{
            scope.row.processingTime
              ? parseTime(scope.row.processingTime, '{y}-{m}-{d}')
              : '--'
          }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="120">
        <template #default="scope">
          <span class="status-dot" :class="'status-dot-' + scope.row.status">{{
            scope.row.status === 0 ? '待处理' : '已处理'
          }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" width="84">
        <template #default="scope">
          <div class="operateCon">
            <a
              class="font-bt"
              :class="scope.row.status === 1 ? 'disabled' : ''"
              @click="handleOpen(scope.row)"
              >处理</a
            >
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 处理结果弹窗 -->
    <DialogFrom
      ref="formRef"
      :visible="visible"
      @handle-close="handleClose"
      @handle-sub="handleSub"
    >
    </DialogFrom>
    <!-- end -->
  </div>
</template>

<script setup name="AlertData">
import { listAlertData, handleAlertRule } from '@/api/nursing/alertData';
import { getStartTime, getEndTime } from '@/utils/date'
// 组件
import SwitchBar from '@/components/switchBar/switchBar.vue';
import DialogFrom from './components/DialogFrom.vue';
import { tabStatusData } from '@/utils/commonData';
const { proxy } = getCurrentInstance();

const alertDataList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const visible = ref(false); // 处理弹层
const disposeId = ref('');
const timeData = ref([]) // 时间选择
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deviceName: null,
    createTime: null,
  },
  rules: {
    deviceName: [
      { required: true, message: '设备名称不能为空', trigger: 'blur' },
    ],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询报警数据列表 */
function getList() {
  loading.value = true;
  const time = timeData.value
  if (time.length > 0) {
    queryParams.value.startTime = getStartTime(time[0])
    queryParams.value.endTime = getEndTime(time[1])
  }
  listAlertData(queryParams.value).then((response) => {
    alertDataList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}
// 处理结果提交
const handleSub = async (val) => {
  const data = {
    ...val,
    id: disposeId.value,
  };
  const res = await handleAlertRule(data);
  debugger
  if (res.code === 200) {
    proxy.$modal.msgSuccess('操作成功');
    handleClose();
    getList();
    formRef.value.handleClear();
    
  }
};

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value = {};
  queryParams.value.pageNum = 1;
  queryParams.value.pageSize = 10;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  timeData.value = []
  // proxy.resetForm('queryRef');
  handleQuery();
}

// 获取tab索引值
const changeId = (val) => {
  queryParams.value.pageNum = 1;
  if (val === 0 || val === 1) {
    queryParams.value.status = val;
  } else {
    delete queryParams.value.status;
  }
  delete queryParams.value.id;
  getList();
};
// 打开处理结果弹层
const handleOpen = (val) => {
  console.log(val);
  if (val.status === 1) {
    return;
  }
  delete queryParams.value.id;
  disposeId.value = val.id;
  visible.value = true;
};
// 关闭处理结果弹层
const handleClose = () => {
  delete queryParams.value.id;
  visible.value = false;
};
getList();
</script>
