<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="老人姓名" prop="elderName">
        <el-input v-model="queryParams.elderName" placeholder="请输入老人姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="toDetails"
          v-hasPermi="['nursing:checkIn:add']">发起入住申请</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="checkInList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="老人姓名" align="center" prop="elderName" />
      <el-table-column label="身份证号" align="center" prop="idCardNo" />
      <el-table-column label="入住床位" align="center" prop="bedNumber" />
      <el-table-column label="护理等级名称" align="center" prop="nursingLevelName" />
      <el-table-column label="入住期限" align="center" prop="startDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }} ~ {{ parseTime(scope.row.endDate, '{y}-{m}-{d}')
            }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="success">已入住</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="danger">已退住</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="startDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" @click="getDetails(scope.row, 'read')"
            v-hasPermi="['elder:checkIn:edit']">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script setup name="CheckIn">
import { listCheckIn } from "@/api/nursing/checkIn";

const { proxy } = getCurrentInstance();

const checkInList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

const route = useRoute();

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    elderName: null,
    elderId: null,
    idCardNo: null,
    startDate: null,
    endDate: null,
    nursingLevelName: null,
    bedNumber: null,
    status: null,
    sortOrder: null,
  },
  rules: {
    elderName: [
      { required: true, message: "老人姓名不能为空", trigger: "blur" }
    ],
    elderId: [
      { required: true, message: "老人ID不能为空", trigger: "blur" }
    ],
    idCardNo: [
      { required: true, message: "身份证号不能为空", trigger: "blur" }
    ],
    startDate: [
      { required: true, message: "入住开始时间不能为空", trigger: "blur" }
    ],
    nursingLevelName: [
      { required: true, message: "护理等级名称不能为空", trigger: "blur" }
    ],
    bedNumber: [
      { required: true, message: "入住床位不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态 (0: 已入住, 1: 已退住)不能为空", trigger: "change" }
    ],
    sortOrder: [
      { required: true, message: "排序编号不能为空", trigger: "blur" }
    ],
    createTime: [
      { required: true, message: "创建时间不能为空", trigger: "blur" }
    ],
    updateTime: [
      { required: true, message: "修改时间不能为空", trigger: "blur" }
    ],
    createBy: [
      { required: true, message: "创建人不能为空", trigger: "blur" }
    ],
    updateBy: [
      { required: true, message: "修改人不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

const toDetails = () => {
  proxy.$router.push({ path: "/enterQuit/checkInInfo" });
}

/** 查询入住列表 */
function getList() {
  loading.value = true;
  listCheckIn(queryParams.value).then(response => {
    checkInList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

//页面跳转到详情页
const getDetails = (row, type) => {
  proxy.$router.push({ path: "/enterQuit/checkInInfo", query: { id: row.id, type: type } });
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
    elderName: null,
    elderId: null,
    idCardNo: null,
    startDate: null,
    endDate: null,
    nursingLevelName: null,
    bedNumber: null,
    status: null,
    sortOrder: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
    remark: null
  };
  proxy.resetForm("checkInRef");
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

//解决页面不刷新的问题
watch(route, (to, from) => {
  proxy.$router.go(0);
})
getList();
</script>
