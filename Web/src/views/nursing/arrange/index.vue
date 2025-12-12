<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="110px">
      <el-form-item label="老人姓名：" prop="elderName">
        <el-input v-model="queryParams.elderName" placeholder="请输入" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="护理员姓名：" prop="nurseId">
        <el-select v-model="queryParams.nurseId" placeholder="请选择" clearable>
          <el-option v-for="item in nurseList" :key="item.userId" :label="item.nickName" :value="item.userId">
            {{ item.nickName }}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="护理项目：" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择" clearable>
          <el-option v-for="item in nursingProjectOptions" :key="item.value" :label="item.label" :value="item.value">
            {{ item.label }}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="期望服务时间：" prop="timeData">
        <el-date-picker v-model="queryParams.timeData" type="datetimerange" start-placeholder="开始时间"
          end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"
          date-format="YYYY/MM/DD ddd" time-format="A hh:mm:ss" @change="handleTimeData" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- tab导航 -->
    <div class="marg-main newBox">
      <SwitchBar ref="tabBar" :data="arrangeTabData" @change-id="changeId"></SwitchBar>
    </div>
    <!-- 表格 -->
    <el-table v-loading="loading" :data="nursingTaskDataList">
      <el-table-column label="序号" type="index" align="center" width="55" />
      <el-table-column label="老人姓名" align="center" prop="elderName" width="150" />
      <el-table-column label="床位号" align="center" prop="bedNumber" width="150" />
      <el-table-column label="护理项目名称" align="center" prop="projectName" width="200" />
      <el-table-column label="项目类型" align="center" prop="taskType" width="180">
        <template #default="scope">{{
          scope.row.taskType === 2 ? '护理计划内' : '护理计划外'
        }}</template>
      </el-table-column>
      <el-table-column label="护理员姓名" align="center" prop="nursingName" width="220" />
      <el-table-column label="期望服务时间" align="center" prop="estimatedServerTime" width="200" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="300">
        <template #default="scope">
          <el-button v-if="scope.row.status === 1" link type="primary" icon="CircleClose"
            @click="handleCancel(scope.row)">取消</el-button>
          <el-button link type="primary" icon="View" @click="handleCheck(scope.row)">查看</el-button>
          <el-button v-if="scope.row.status === 1" link type="primary" icon="Edit"
            :class="scope.row.count ? 'disabled' : ''" @click="handleExecute(scope.row)">执行</el-button>
          <el-button v-if="scope.row.status === 1" link type="primary" icon="Tickets"
            :class="scope.row.count ? 'disabled' : ''" @click="handleChangeDate(scope.row)">改期</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />
    <!-- 取消弹窗 -->
    <CancelDialogFrom ref="formRef" :visible="visible" :title="title" :data="formBaseData" @handle-close="handleClose"
      @handle-edit-form="handleEditForm">
    </CancelDialogFrom>
    <!-- end -->
    <!-- 执行弹层 -->
    <DialogFormExecute ref="formRef" :visible="dialogVisibleExecute" @handle-close="handleClose"
      @handle-submit="handleSubmit"></DialogFormExecute>
    <!-- end -->
    <!-- 改期弹层 -->
    <DialogFormTime :visible="dialogVisible" :timeData="timeData" @handle-close="handleClose"
      @handle-submit="handleSubmitTime"></DialogFormTime>
    <!-- end -->
  </div>
</template>

<script setup name="AlertData">
// 接口
import { listAlertData, handleAlertRule } from '@/api/nursing/alertData';
import {
  listNursingTask,
  cancelPlan,
  executePlan,
  changePlanTime
} from '@/api/nursing/task';
import { getAllUserList } from '@/api/system/user';
import { getAllProjects } from '@/api/nursing/project';
import dayjs from 'dayjs';
// 组件
import SwitchBar from '@/components/switchBar/switchBar.vue';
// 取消 弹窗
import CancelDialogFrom from './components/CancelDialogFrom.vue';
// 执行弹窗
import DialogFormExecute from './components/DialogFormExecute.vue';
// 改期弹窗
import DialogFormTime from './components/DialogFormTime.vue'
import { arrangeTabData } from '@/utils/commonData';
const { proxy } = getCurrentInstance();
const router = useRouter();
const nursingTaskDataList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const visible = ref(false); // 处理弹层
const dialogVisible = ref(false); // 改期弹窗
const dialogVisibleExecute = ref(false); // 执行弹窗
const disposeId = ref('');
const formRef = ref(null);
const taskId = ref(null); // 护理任务ID
const timeData = ref('')
// 护理员下拉数据
const nurseList = ref([]);
const nursingProjectOptions = ref([]); //护理项目列表
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
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
  if (queryParams.value.status == '' || queryParams.value.status == null) {
    queryParams.value.status = 1;
  }
  listNursingTask(queryParams.value).then((response) => {
    nursingTaskDataList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

//处理时间
const handleTimeData = (val) => {
  console.log(val);
  if (val) {
    // queryParams.value.startTime = new Date(queryParams.value.timeData[0]).getTime();
    queryParams.value.startTime = val[0];
    queryParams.value.endTime = val[1];
  } else {
    queryParams.value.startTime = null;
    queryParams.value.endTime = null;
  }
};

const handleCancel = (row) => {
  //打开弹窗
  visible.value = true;
  taskId.value = row.id;
};
// 关闭弹窗
const handleClose = () => {
  visible.value = false; // 关闭取消弹窗
  dialogVisible.value = false; // 关闭改期弹窗
  dialogVisibleExecute.value = false; // 关闭执行记录弹窗
};
// 取消任务
const handleEditForm = async (val) => {
  const res = await cancelPlan({ ...val, ...{ taskId: taskId.value } });
  if (res.code === 200) {
    proxy.$modal.msgSuccess('编辑成功');
    getList();
    handleClose();
    formRef.value.handleClear();
  }
};
// 打开执行弹窗
const handleExecute = (row) => {
  dialogVisibleExecute.value = true;
  taskId.value = row.id;
};
// 执行
const handleSubmit = async (val) => {
  const res = await executePlan({
    ...val,
    ...{ taskId: taskId.value },
    ...{
      estimatedServerTime: dayjs(val.estimatedServerTime).format(
        'YYYY-MM-DD HH:mm:ss'
      ),
    },
  });
  if (res.code === 200) {
    proxy.$modal.msgSuccess('执行成功');
    getList();
    handleClose();
    formRef.value.handleClear();
  } else {
    proxy.$modal.msgError(res.msg);
  }
};
// 打开改期弹窗
const handleChangeDate = (row) => {
  dialogVisible.value = true
  taskId.value = row.id
  timeData.value = row.estimatedServerTime
}
//改期
const handleSubmitTime = async (val) => {
  const res = await changePlanTime({ ...val, ...{ taskId: taskId.value } })
  if (res.code === 200) {
    proxy.$modal.msgSuccess('修改成功')
    getList()
    handleClose()
  } else {
    proxy.$modal.msgError(res.msg)
  }
}
// 获取护理员列表
const getAllUserListFunc = async () => {
  const res = await getAllUserList({ deptId: '201' });
  nurseList.value = res.data;
};
//查询所有护理项目
const getAllProjectList = () => {
  getAllProjects().then((res) => {
    nursingProjectOptions.value = res.data;
  });
};
/** 搜索按钮操作 */
function handleQuery() {

  queryParams.value.pageNum = 1;
  queryParams.value.pageSize = 10;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  queryParams.value = {};
  handleQuery();
}

// 获取tab索引值
const changeId = (val) => {
  console.log(val);
  queryParams.value.pageNum = 1;
  queryParams.value.status = val;
  /*  if (val === 0 || val === 1) {
    queryParams.value.status = val;
  } else {
    delete queryParams.value.status;
  }
  delete queryParams.value.status; */
  getList();
};
// 查看
const handleCheck = (val) => {
  router.push({
    path: `/nursing/arrangeDetails`,
    query: {
      id: val.id,
    },
  });
};
getList();
getAllUserListFunc();
getAllProjectList();
</script>
