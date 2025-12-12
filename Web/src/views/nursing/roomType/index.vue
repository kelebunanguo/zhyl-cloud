<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roomTypeList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" align="center" type="index" width="50" />
      <el-table-column label="房间图片" align="center" prop="photo" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.photo" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="房型类型" align="center" prop="name" />
      <el-table-column label="床位费用" align="center" prop="price" />
      <!-- 房型介绍，只展示10个字符 -->
      <el-table-column label="房型介绍" align="center" prop="introduction" width="180">
        <template #default="scope">
          <span>{{ computeLength(scope.row.introduction) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status == 1 ? 'success' : 'danger'">{{ scope.row.status == 1 ? '启用' : '禁用'
            }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          <el-button link type="primary" :icon="scope.row.status == 0 ? 'Unlock' : 'lock'"
            @click="handleEnable(scope.row)">{{ scope.row.status == 1 ? '禁用' : '启用' }}</el-button>

        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改房型对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="roomTypeRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="房型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入房型名称" />
        </el-form-item>
        <el-form-item label="床位数量" prop="bedCount">
          <el-input v-model="form.bedCount" placeholder="请输入床位数量" />
        </el-form-item>
        <el-form-item label="床位费用" prop="price">
          <el-input-number v-model="form.price" placeholder="请输入床位费用"></el-input-number>
        </el-form-item>
        <el-form-item label="介绍" prop="introduction">
          <el-input v-model="form.introduction" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="照片" prop="photo">
          <!-- <el-input v-model="form.photo" placeholder="请输入照片" /> -->
          <image-upload v-model="form.photo" />
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

<script setup name="RoomType">
import { listRoomType, getRoomType, delRoomType, addRoomType, updateRoomType } from "@/api/nursing/roomType";
import { computed } from "vue";

const { proxy } = getCurrentInstance();

const roomTypeList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {
    status: 0
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    bedCount: null,
    price: null,
    introduction: null,
    photo: null,
    status: null,
  },
  rules: {
    name: [
      { required: true, message: "房型名称不能为空", trigger: "blur" }
    ],
    bedCount: [
      { required: true, message: "床位数量不能为空", trigger: "blur" }
    ],
    price: [
      { required: true, message: "床位费用不能为空", trigger: "change" }
    ],
    status: [
      { required: true, message: "状态，0：禁用，1：启用不能为空", trigger: "change" }
    ],
    createTime: [
      { required: true, message: "创建时间不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);


const computeLength = (val) => {
  return val.slice(0, 10) + (val.length > 10 ? '...' : '');
}

/** 查询房型列表 */
function getList() {
  loading.value = true;
  listRoomType(queryParams.value).then(response => {
    roomTypeList.value = response.rows;
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
    bedCount: null,
    price: null,
    introduction: null,
    photo: null,
    status: 0,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
    remark: null
  };
  proxy.resetForm("roomTypeRef");
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
  title.value = "添加房型";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value
  getRoomType(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改房型";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["roomTypeRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateRoomType(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addRoomType(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}


// 使用 async/await 语法优化异步操作
const handleEnable = async (row) => {
  try {
    // 获取状态
    const status = row.status;
    const info = status === 0 ? '启用' : '禁用';

    // 使用模板字符串
    const confirmMessage = `是否确认${info}护理项目的数据项？`;

    // 确认操作
    if (await proxy.$modal.confirm(confirmMessage)) {
      // 更新参数
      const param = {
        id: row.id,
        status: status === 0 ? 1 : 0
      };

      // 执行更新操作
      await updateRoomType(param);
      // 刷新列表
      getList();
      // 成功消息
      proxy.$modal.msgSuccess(`${info}成功`);
    }
  } catch (error) {
    // 异常处理：这里可以根据实际需求进行调整，例如打印错误日志或显示用户友好的错误信息
    console.error("操作失败，请重试或联系管理员。");
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除房型名称为"' + row.name + '"的数据项？').then(function () {
    return delRoomType(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => { });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('elder/roomType/export', {
    ...queryParams.value
  }, `roomType_${new Date().getTime()}.xlsx`)
}

getList();
</script>
