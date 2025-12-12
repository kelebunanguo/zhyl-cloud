<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="预约人姓名" prop="name" style="width: 300px;">
        <el-input v-model="queryParams.name" placeholder="请输入" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="预约人手机号" prop="mobile" label-width="110px">
        <el-input v-model="queryParams.mobile" placeholder="请输入预约人手机号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="预约时间" prop="time">
        <el-date-picker clearable v-model="queryParams.time" type="date" value-format="YYYY-MM-DD"
          placeholder="请选择预约时间">
        </el-date-picker>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>



    <el-table v-loading="loading" :data="reservationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="预约人姓名" align="center" prop="name" />
      <el-table-column label="预约人手机号" align="center" prop="mobile" />
      <el-table-column label="预约时间" align="center" prop="time" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.time, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="探访人" align="center" prop="visitor" />
      <el-table-column label="预约类型，0：参观预约，1：探访预约" align="center" prop="type" />
      <el-table-column label="预约状态，0：待报道，1：已完成，2：取消，3：过期" align="center" prop="status" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改预约信息对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="reservationRef" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Reservation">
import { listReservation, getReservation, delReservation, addReservation, updateReservation } from "@/api/nursing/reservation";

const { proxy } = getCurrentInstance();

const reservationList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    mobile: null,
    time: null,
    visitor: null,
    type: null,
    status: null,
  },
  rules: {
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询预约信息列表 */
function getList() {
  loading.value = true;
  listReservation(queryParams.value).then(response => {
    reservationList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    name: null,
    mobile: null,
    time: null,
    visitor: null,
    type: null,
    status: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
    remark: null
  };
  proxy.resetForm("reservationRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加预约信息";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value
  getReservation(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改预约信息";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["reservationRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateReservation(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addReservation(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除预约信息编号为"' + _ids + '"的数据项？').then(function () {
    return delReservation(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => { });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('elder/reservation/export', {
    ...queryParams.value
  }, `reservation_${new Date().getTime()}.xlsx`)
}

getList();
</script>
