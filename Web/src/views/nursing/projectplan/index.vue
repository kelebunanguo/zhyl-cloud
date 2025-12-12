<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['nursing:projectplan:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['nursing:projectplan:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['nursing:projectplan:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['nursing:projectplan:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="projectplanList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="计划id" align="center" prop="planId" />
      <el-table-column label="项目id" align="center" prop="projectId" />
      <el-table-column label="计划执行时间" align="center" prop="executeTime" />
      <el-table-column label="执行周期 0 天 1 周 2月" align="center" prop="executeCycle" />
      <el-table-column label="执行频次" align="center" prop="executeFrequency" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['nursing:projectplan:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['nursing:projectplan:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改护理计划和项目关联对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="projectplanRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="计划id" prop="planId">
          <el-input v-model="form.planId" placeholder="请输入计划id" />
        </el-form-item>
        <el-form-item label="项目id" prop="projectId">
          <el-input v-model="form.projectId" placeholder="请输入项目id" />
        </el-form-item>
        <el-form-item label="计划执行时间" prop="executeTime">
          <el-input v-model="form.executeTime" placeholder="请输入计划执行时间" />
        </el-form-item>
        <el-form-item label="执行周期 0 天 1 周 2月" prop="executeCycle">
          <el-input v-model="form.executeCycle" placeholder="请输入执行周期 0 天 1 周 2月" />
        </el-form-item>
        <el-form-item label="执行频次" prop="executeFrequency">
          <el-input v-model="form.executeFrequency" placeholder="请输入执行频次" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
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

<script setup name="Projectplan">
import { listProjectplan, getProjectplan, delProjectplan, addProjectplan, updateProjectplan } from "@/api/nursing/projectplan"

const { proxy } = getCurrentInstance()

const projectplanList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询护理计划和项目关联列表 */
function getList() {
  loading.value = true
  listProjectplan(queryParams.value).then(response => {
    projectplanList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    planId: null,
    projectId: null,
    executeTime: null,
    executeCycle: null,
    executeFrequency: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
    remark: null
  }
  proxy.resetForm("projectplanRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加护理计划和项目关联"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getProjectplan(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改护理计划和项目关联"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["projectplanRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateProjectplan(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addProjectplan(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除护理计划和项目关联编号为"' + _ids + '"的数据项？').then(function() {
    return delProjectplan(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('nursing/projectplan/export', {
    ...queryParams.value
  }, `projectplan_${new Date().getTime()}.xlsx`)
}

getList()
</script>
