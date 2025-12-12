<template>
  <div class="app-container">

    <!-- 搜索表单  start -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" style="width: 200px;" clearable>
          <el-option label="请选择状态" :value="null" disabled />
          <el-option v-for="item in nursing_project_status" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 搜索表单 end -->

    <!-- 按钮区域 start -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['nursing:project:add']">新增</el-button>
      </el-col>
    </el-row>
    <!-- 按钮区域 end -->

    <!-- 数据展示区域 start -->
    <el-table v-loading="loading" :data="projectList">
      <el-table-column label="序号" align="center" type="index" width="120" />
      <el-table-column label="名称" align="center" prop="name" width="150" />
      <el-table-column label="排序号" align="center" prop="orderNo" width="100" />
      <el-table-column label="单位" align="center" prop="unit" width="100" />
      <el-table-column label="价格" align="center" prop="price" width="150" />
      <el-table-column label="图片" align="center" prop="image" width="150">
        <template #default="scope">
          <image-preview :src="scope.row.image" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="护理要求" align="center" prop="nursingRequirement" />
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">{{ scope.row.status === 1 ? '启用' : '禁用'
            }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['nursing:project:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['nursing:project:remove']">删除</el-button>
          <el-button link type="primary" :icon="scope.row.status == 0 ? 'Lock' : 'Unlock'"
            @click="handleEnable(scope.row)">{{ scope.row.status == 0 ? '启用' : '禁用' }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 数据展示区域 end -->


    <!-- 分页组件 start -->
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
      @pagination="getList" />
    <!-- 分页组件 end -->

    <!-- 新增或修改弹窗  start -->
    <!-- 添加或修改护理项目对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="projectRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="排序号" prop="orderNo">
          <el-input-number v-model="form.orderNo" placeholder="请输入" :min="1" :max="20" controls-position="right"  />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" placeholder="请输入" :min="1" :max="200" :step="10" controls-position="right"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in nursing_project_status" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <image-upload v-model="form.image" />
        </el-form-item>
        <el-form-item label="护理要求" prop="nursingRequirement">
          <el-input v-model="form.nursingRequirement" placeholder="请输入护理要求" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 新增或修改弹窗  end -->
  </div>
</template>

<script setup name="Project">

/* 启用禁用按钮操作 */
function handleEnable(row) {
  // 获取当前状态
  const status = row.status;
  // 提示信息
  const msg = status === 1 ? '禁用' : '启用';

  // 构建参数
  const params = {
    id: row.id,
    status: status === 1 ? 0 : 1
  };
  
  proxy.$modal.confirm(`是否确认${msg}该护理项目的数据项？`).then(function() {
    return updateProject(params);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess(`${msg}成功`);
  }).catch(() => {});
}

//下拉选择框的选择项
// 下拉/单选项由数据字典提供（见下面通过 proxy.useDict 获取）
// 引入后端接口
import { listProject, getProject, delProject, addProject, updateProject } from "@/api/nursing/project";
// 获取当前实例代理对象，用于访问组件的数据、方法等属性。
const { proxy } = getCurrentInstance();

// 使用全局字典：返回值是一个响应式数组，例如 [{ value: 1, label: '启用' }, ...]
const { nursing_project_status } = proxy.useDict("nursing_project_status");

// 列表数据
const projectList = ref([]);
// 是否显示弹窗
const open = ref(false);
// 加载状态
const loading = ref(true);
// 是否显示搜索栏
const showSearch = ref(true);
//记录被选中的id集合
const ids = ref([]);
//是否是单选，用于是否高亮修改按钮
const single = ref(true);
//是否是多选，用于是否高亮删除按钮
const multiple = ref(true);
//总条数
const total = ref(0);
// 标题
const title = ref("");

const data = reactive({
  // 新增或修改表单数据
  form: {},
  // 查询参数
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    status: null,
  },
  // 表单校验
  rules: {
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
});
//toRefs将传入的数据对象的特定属性转换为响应式对象
const { queryParams, form, rules } = toRefs(data);

/** 查询护理项目列表 */
function getList() {
  loading.value = true;
  listProject(queryParams.value).then(response => {
    projectList.value = response.rows;
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
    orderNo: null,
    unit: null,
    price: null,
    image: null,
    nursingRequirement: null,
    status: null,
    createBy: null,
    updateBy: null,
    remark: null,
    createTime: null,
    updateTime: null
  };
  proxy.resetForm("projectRef");
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
  title.value = "添加护理项目";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value
  getProject(_id).then(response => {
    form.value = response.data;
    form.value.status = String(form.value.status)
    open.value = true;
    title.value = "修改护理项目";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["projectRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateProject(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addProject(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除护理项目编号为"' + _ids + '"的数据项？').then(function() {
    return delProject(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}



getList();
</script>
