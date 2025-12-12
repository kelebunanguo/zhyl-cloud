<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="98px">
      <el-form-item label="告警规则名称" prop="alertRuleName">
        <el-input v-model="queryParams.alertRuleName" placeholder="请输入告警规则名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="所属产品" prop="productKey">
        <el-select
          v-model="queryParams.productKey"
          placeholder="请选择"
          style="width: 200px;"
          clearable
        >
          <el-option
            v-for="item in options"
            :key="item.productId"
            :label="item.name"
            :value="item.productId"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['elder:alertRule:add']">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="alertRuleList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" type="index" width="55" />
      <el-table-column label="告警规则名称" align="center" prop="alertRuleName"  width="150"/>
      <el-table-column label="所属产品" align="center" prop="productName"  width="150"/>
      <el-table-column label="关联设备" align="center" prop="deviceName" width="150"/>
      <el-table-column label="报警规则" align="center" prop="operator"  width="260" >
        <template #default="scope">
          {{ scope.row.functionName }} {{ scope.row.operator }}{{ scope.row.value }},持续{{ scope.row.duration }}个周期就报警
        </template>
      </el-table-column>
      <el-table-column label="报警生效时段" align="center" prop="alertEffectivePeriod"  width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" >
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'danger' : 'success'">{{
            scope.row.status === 0 ? '禁用' : '启用'
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" align="center" class-name="small-padding fixed-width" width="200">
        <template #default="scope">
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
            >删除</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            >修改</el-button>
            <el-button
            link
            type="primary"
            :icon="scope.row.status == 0 ? 'Unlock' : 'lock'"
            @click="handleEnable(scope.row)"
            >{{ scope.row.status === 1 ? '禁用' : '启用' }}</el-button
          >
          
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script setup name="alertRule">
import { listAlertRule, getAlertRule, delAlertRule, addAlertRule, updateAlertRule } from "@/api/nursing/alertRule";

import {
  syncProductList,
  allProduct,
} from '@/api/nursing/device';

const { proxy } = getCurrentInstance();
const router = useRouter();
const alertRuleList = ref([]);
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
    functionId: null,
    alertDataType: null,
    alertRuleName: null,
  },
  rules: {
    createTime: [
      { required: true, message: "创建时间不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);
//产品列表
const options = ref([]);

//组件挂载完成，自动同步产品列表
onMounted(() => {
  refreshProduct();
  getList();
});

//同步产品列表
const refreshProduct = async () => {
  await syncProductList().then((response) => {
    if (response.code === 200) {
      allProduct().then((res) => {
        if (res.code === 200) {
          options.value = res.data;
        }
      });
    }
  });
};

/** 查询报警规则列表 */
function getList() {
  loading.value = true;
  listAlertRule(queryParams.value).then(response => {
    alertRuleList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}


// 表单重置
function reset() {
  form.value = {
    id: null,
    productKey: null,
    productName: null,
    moduleId: null,
    moduleName: null,
    functionName: null,
    functionId: null,
    iotId: null,
    deviceName: null,
    alertDataType: null,
    alertRuleName: null,
    operator: null,
    value: null,
    duration: null,
    alertEffectivePeriod: null,
    alertSilentPeriod: null,
    status: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
    remark: null
  };
  proxy.resetForm("alertRuleRef");
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
function handleAdd(row) {
  router.push({
    path: '/intelligence/ruleDetails',
    query: { iotId: row.iotId, productKey: row.productKey },
  });
}

/** 修改按钮操作 */
function handleUpdate(row) {
  // reset();
  // const _id = row.id || ids.value
  // getAlertRule(_id).then(response => {
  //   form.value = response.data;
  //   open.value = true;
  //   title.value = "修改报警规则";
  // });
  router.push({
    path: '/intelligence/ruleDetails',
    query: { id:row.id },
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["alertRuleRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateAlertRule(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addAlertRule(form.value).then(response => {
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
    const confirmMessage = `是否确认${info}报警规则的数据项？`;

    // 确认操作
    if (await proxy.$modal.confirm(confirmMessage)) {
      // 更新参数
      const param = {
        id: row.id,
        status: status === 0 ? 1 : 0,
      };

      // 执行更新操作
      await updateAlertRule(param);
      // 刷新列表
      getList();
      // 成功消息
      proxy.$modal.msgSuccess(`${info}成功`);
    }
  } catch (error) {
    // 异常处理：这里可以根据实际需求进行调整，例如打印错误日志或显示用户友好的错误信息
    console.error('操作失败，请重试或联系管理员。');
  }
};

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除报警规则编号为"' + _ids + '"的数据项？').then(function () {
    return delAlertRule(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => { });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('elder/alertRule/export', {
    ...queryParams.value
  }, `alertRule_${new Date().getTime()}.xlsx`)
}


</script>
