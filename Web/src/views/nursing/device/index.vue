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
      <el-form-item label="产品名称" prop="productKey">
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
      <el-form-item label="设备类型" prop="locationType">
        <el-select
          v-model="queryParams.locationType"
          placeholder="请选择"
          style="width: 200px;"
          clearable
        >
          <el-option
            v-for="item in device_location_type"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="Refresh" @click="resetQuery(queryRef)">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="newBtn mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="RefreshRight"
          @click="refreshProduct"
          >同步数据</el-button
        >
      </el-col>

      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          >新增</el-button
        >
      </el-col>
    </el-row>

    <el-table
      v-loading="loading"
      :data="deviceList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column label="序号" type="index" align="center" width="55" />
      <el-table-column
        label="设备名称"
        align="center"
        prop="deviceName"
        width="180"
      />
      <el-table-column
        label="设备标识码"
        align="center"
        prop="nodeId"
        width="180"
      />
      <el-table-column
        label="产品名称"
        align="center"
        prop="productName"
        width="150"
      />
      <el-table-column
        label="接入位置"
        align="center"
        prop="remark"
        width="180"
      />
      <el-table-column
        label="设备类型"
        align="center"
        prop="locationType"
        width="180"
      >
        <template #default="scope">
          {{ scope.row.locationType === 1 ? '固定设备' : '随身设备' }}
        </template>
      </el-table-column>

      <el-table-column label="创建时间" align="center" prop="createTime">
        <template #default="scope">
          <span>{{
            parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}')
          }}</span>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
        width="200"
      >
        <template #default="scope">
          <el-button
            link
            type="danger"
            icon="Delete"
            @click="handleDelete(scope.row)"
            >删除</el-button
          >
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            >编辑</el-button
          >
          <el-button
            link
            type="primary"
            icon="ZoomIn"
            @click="handleDetails(scope.row)"
            >查看</el-button
          >
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

    <!-- 添加或修改设备对话框 -->
    <el-dialog :title="title" v-model="open" class="elDialogBox" append-to-body>
      <el-form
        ref="deviceRef"
        :model="formData"
        :rules="rules"
        label-width="106px"
      >
        <el-form-item label="设备标识码：" name="nodeId">
          <el-input
            v-model="formData.nodeId"
            type="textarea"
            class="wt-400"
            placeholder="请输入"
            show-word-limit
            clearable
            resize="none"
            :maxlength="64"
            :disabled="
              formData.id && formData.id !== undefined && formData.id !== ''
            "
          >
          </el-input>
        </el-form-item>
        <el-form-item label="设备名称：" prop="deviceName">
          <el-input
            v-model="formData.deviceName"
            placeholder="请输入"
            :maxlength="15"
            show-word-limit
            clearable
            class="wt-400"
          ></el-input>
        </el-form-item>
        <!-- <el-form-item label="备注名称：" prop="nickname">
          <el-input
            v-model="formData.nickname"
            placeholder="请输入"
            :maxlength="15"
            show-word-limit
            clearable
            class="wt-400"
          ></el-input>
        </el-form-item> -->
        <el-form-item label="所属产品：" prop="productKey">
          <el-select
            v-model="formData.productKey"
            placeholder="请选择"
            clearable
            class="wt-400"
            :disabled="
              formData.id && formData.id !== undefined && formData.id !== ''
            "
            @change="handleProduct"
          >
            <el-option
              v-for="item in options"
              :key="item.productId"
              :label="item.name"
              :value="item.productId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备类型：" prop="locationType">
          <el-radio-group
            v-model="formData.locationType"
            size="medium"
            @change="handleRadio"
          >
            <el-radio
              v-for="(item, index) in locationTypeOptions"
              :key="index"
              :label="item.id"
              >{{ item.value }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="formData.locationType === 0"
          label="接入位置："
          prop="name"
        >
          <el-input
            v-model="formData.name"
            placeholder="请选择"
            :readonly="readonly"
            @focus="selectFlavor"
            @blur="outSelect"
            class="wt-400"
          >
            <template #suffix>
              <el-icon v-if="mak"><ArrowUp /></el-icon>
              <el-icon v-else><ArrowDown /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item v-else label="接入位置：" prop="localName">
          <el-cascader
            v-model="bedValue"
            :options="floorData"
            :props="{ checkStrictly: true }"
            @change="handleFloor"
            class="wt-400"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 选择老人弹窗 -->
    <!-- 老人选择弹层 -->
    <OldManSelect
      :dialog-visible="dialogOldVisible"
      :dialog-data="listOldManData"
      :pagination="paginationOld"
      :formData="formData"
      @handle-search="handleOldSearch"
      @handle-reset="handleOldReset"
      @handle-close-dialog="handleCloseDialog"
      @get-current="getOldCurrent"
      @handle-select-old="handleSelectOld"
      @get-old-list="getOldList"
    ></OldManSelect>
    <!-- end -->
  </div>
</template>

<script setup name="Device">
import {
  listDevice,
  getDevice,
  delDevice,
  addDevice,
  updateDevice,
  syncProductList,
  allProduct,
} from '@/api/nursing/device';
import { listElderPageQuery } from '@/api/nursing/checkIn';
import { getFloorBed } from '@/api/nursing/floor'
import { onMounted, ref } from 'vue';
import { locationTypeOptions } from '@/utils/commonData';
// 选择老人列表弹层
import OldManSelect from './components/LookData.vue';
const { proxy } = getCurrentInstance();
const router = useRouter();
const deviceList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const queryRef = ref(null)
//设备类型
const { device_location_type } = proxy.useDict('device_location_type');
const formData = ref({
  locationType: 0,
  resource: '',
});
const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    physicalLocationType: null,
    // nickname: null,
    productKey: null,
    name: null,
  },
});
const mak = ref(false); // 控制input上下箭头
const formVisible = ref(false); // 弹窗
const readonly = ref(false); // 是否只读
const dialogOldVisible = ref(false); // 老人选择弹窗
const listOldManData = ref([]); // 获取所有老人
const oldManInfo = ref({}); // 选择的老人数据数据
const floorData = ref([]); // 楼层信息
const productData = ref([]); // 产品数据
const bedValue = ref([]);
const formOldData = ref({});
// 老人弹层分页
const paginationOld = ref({
  pageSize: 5,
  pageNum: 1, // 默认当前页
  status: 1,
});
const rules = {
  nodeId:[
    {
      required: true,
      message: '请输入',
      trigger: 'blur',
    },
  ],
  deviceName: [
    {
      required: true,
      message: '请输入',
      trigger: 'blur',
    }
    // {
    //   pattern: /^[^\u4e00-\u9fa5]{4,15}$/g,
    //   message:
    //     '支持英文字母、数字、下划线（_）、中划线（-）、点号（.）、半角冒号（:）和特殊字符@，长度限制为4~32个字符',
    //   trigger: 'blur',
    // },
  ],
  // nickname: [
  //   {
  //     required: true,
  //     message: '请输入',
  //     trigger: 'blur',
  //   },
  // ],
  productId: [
    {
      required: true,
      message: '请选择',
      trigger: 'change',
    },
  ],
  locationType: [
    {
      required: true,
      message: '设备类型不能为空',
      trigger: 'change',
    },
  ],
  name: [
    {
      required: true,
      message: '请选择',
      trigger: 'change',
    },
  ],
};
const { queryParams } = toRefs(data);
//产品列表
const options = ref([]);

//组件挂载完成，自动同步产品列表
onMounted(() => {
  refreshProduct();
});

/** 查询设备列表 */
function getList() {
  loading.value = true;
  listDevice(queryParams.value).then((response) => {
    deviceList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}
// 获取老人列表数据
const getOldList = async (val) => {
  if (val !== undefined) {
    paginationOld.value = {
      ...paginationOld.value,
      pageSize: val.limit,
      pageNum: val.page,
    };
  }

  const res = await listElderPageQuery(paginationOld.value); // 获取列表数据
  // if (res.code === 200) {
  listOldManData.value = res.rows;
  listOldManData.value.forEach((ele) => {
    ele.elderId = ele.id;
  });
  paginationOld.value.total = Number(res.total);
  // }
};
// 搜索功能
const handleOldSearch = () => {
  paginationOld.value.pageNum = 1;
  getOldList();
};
// 重置，清空搜索框
const handleOldReset = () => {
  // 重置页码
  paginationOld.value = {
    pageSize: 5,
    pageNum: 1,
    status: 1,
  };
  getOldList();
};
// 翻页设置当前页
const getOldCurrent = (val) => {
  paginationOld.value.pageNum = val.current;
  paginationOld.value.pageSize = val.pageSize;
  getOldList();
};
// 打开老人弹层
const handleOpenDialog = (val) => {

  // 打开弹层前判断是否有选择得老人，如果是undefined，老人不提示回显
  if (val.name === undefined) {
    formData.value.elderId = undefined;
  } else {
    formData.value.elderId = val.elderId;
  }
  dialogOldVisible.value = true;
  getOldList();
};
// 关闭普通列表弹层
const handleCloseDialog = () => {
  dialogOldVisible.value = false;
  paginationOld.value = {
    pageSize: 5,
    pageNum: 1,
    status: 1,
  };
};
// 提交普通列表弹层
const handleSelectOld = (val) => {
  formData.value.elderId = val.elderId;
  formData.value.name = val.name;
};
// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  bedValue.value = [];
  formOldData.value = {}
  formData.value = {
    id: null,
    iotId: null,
    bindingLocation: null,
    locationType: 0,
    physicalLocationType: null,
    deviceName: null,
    // nickname: null,
    productKey: null,
    productName: null,
    deviceDescription: null,
    haveEntranceGuard: null,
    createTime: null,
    updateTime: null,
    createBy: null,
    updateBy: null,
    remark: null,
  };
  proxy.resetForm('deviceRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  queryRef.value.resetFields()
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

//同步产品列表
const refreshProduct = async () => {
  await syncProductList().then((response) => {
    if (response.code === 200) {
      allProduct().then((res) => {
        if (res.code === 200) {
          options.value = res.data;
          proxy.$modal.msgSuccess('同步产品列表成功');
        } else {
          proxy.$modal.msgError('同步产品列表失败');
        }
      });
    }
  });
};

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加设备';
  getAllFloorList();
}

/** 修改按钮操作 */
function handleUpdate(row) {
  getAllFloorList();
  reset();
  const params = {
    iotId: row.iotId,
    productKey: row.productKey,
  };
  getDevice(params).then((response) => {
    const data = response.data;
    formData.value = data;

    if (data.locationType === 0) {
      data.elderId = data.bindingLocation;
      data.name = data.remark;
    } else if (data.deviceDescription) {
      bedValue.value = data.deviceDescription.split(',');
      data.localName = bedValue.value;
    }
    formOldData.value = Object.assign({}, formData.value);
    open.value = true;
    title.value = '修改设备';
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs['deviceRef'].validate((valid) => {
    if (valid) {
      const data = formData.value;
      let params = {
        deviceName: data.deviceName,
        locationType: data.locationType,
        productName: data.productName,
        productKey: data.productKey,
        nodeId:data.nodeId
        // registerDeviceRequest: {
        //   deviceName: data.deviceName,
        //   nickname: data.nickname,
        //   productKey: data.productKey,
        // },
      };
      // 如果选择的接入类别是1，需要传楼层房间id
      if (data.locationType === 1) {
        params = {
          ...params,
          physicalLocationType: bedValue.value.length - 1,
          bindingLocation: bedValue.value[bedValue.value.length - 1],
          deviceDescription: bedValue.value.join(),
          remark: data.remark,
        };
      } else {
        // 否则传老人的id
        params.deviceDescription = null;
        params = {
          ...params,
          bindingLocation: data.elderId,
          remark: data.name,
        };
      }
      if (data.id != null) {
        params = {
          ...params,
          id: formData.value.id,
          iotId: formData.value.iotId,
        };
        updateDevice(params).then((response) => {
          proxy.$modal.msgSuccess('编辑成功');
          open.value = false;
          setTime();
        });
      } else {
        addDevice(params).then((response) => {
          proxy.$modal.msgSuccess('添加成功');
          open.value = false;
          setTime();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal
    .confirm('是否确认删除设备编号为"' + _ids + '"的数据项？')
    .then(function async() {
      // const params = {
      //   iotId: row.iotId,
      //   productKey: row.productKey,
      // };
      delDevice(row.iotId)
    })
    .then(() => {
      setTime()
      proxy.$modal.msgSuccess('删除成功');
    })
    .catch(() => {});
}
// 增删改查1秒钟后刷新列表，因为阿里云创建设备有延时
const setTime = () => {
  const time = setTimeout(() => {
    getList()
    clearTimeout(time)
  }, 1000)
  
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'elder/device/export',
    {
      ...queryParams.value,
    },
    `device_${new Date().getTime()}.xlsx`
  );
}

getList();
// 获取楼层，房间床位
const getAllFloorList = async () => {
  const res = await getFloorBed(1);
  if (res.code === 200) {
    floorData.value = res.data;
  }
};
// 选择楼层、房间、床位
const handleFloor = (e) => {
  formData.value.physicalLocationType=e.length-1
  const valueArr = [];
  bedValue.value.forEach((eleObj) => {
    valueArr.push(eleObj.substring(1));
  });
  formData.value.localName = valueArr;
  const data = [];
  floorData.value.forEach((ele) => {
    if (ele.value === bedValue.value[0]) {
      data.push(ele.label);
      ele.children.forEach((obj) => {
        if (obj.value === bedValue.value[1]) {
          data.push(obj.label);
          obj.children.forEach((val) => {
            if (val.value === bedValue.value[2]) {
              data.push(val.label);
            }
          });
        }
      });
    }
  });
  formData.value.remark = data.join();
};
// 选择老人获取相关联的信息
const selectFlavor = () => {
  mak.value = true;
  readonly.value = true;
  handleOpenDialog(formData.value);
};
const outSelect = () => {
  setTimeout(function () {
    mak.value = false;
    readonly.value = false;
  }, 200);
};
const handleProduct = (val)=>{

   const data= options.value.filter(obj => obj.productId ===val)
   formData.value.productName=data[0].name
   
// formData.val.productName
}
// 选择接送类别
const handleRadio = (value) => {
  const data = formData.value;
  const oldData = formOldData.value;
  console.log(oldData,1);
  console.log(data,2);
  // // 如果接入类型选择的是位置，需要把老人的选项内容清掉
  if (data.locationType === 1) {
    // 获取详情要回显的数据
    console.log(oldData.deviceDescription)
    if (oldData.deviceDescription) {
      bedValue.value = oldData.deviceDescription.split(',');
      delete data.name;
    }
  }

  // 反之，接入类型选择的是老人，需要把位置的选项内容清掉
  if (data.locationType === 0) {
    bedValue.value = [];
    data.localName = [];

    // 获取详情要回显的数据
    if (!oldData.deviceDescription) {
      data.name = oldData.remark;
      data.elderId = oldData.elderId;
    }
  }
};
// 去详情页
const handleDetails = (row) => {
  router.push({
    path: '/intelligence/details',
    query: { iotId: row.iotId, productKey: row.productKey },
  });
};

</script>
<style rel="stylesheet/scss">
    .el-cascader-panel .el-radio{
        width: 100%;
        height: 100%;
        z-index: 10;
        position: absolute;
        top: 10px;
        right: 10px;
    }
    .el-cascader-panel .el-radio__input{
        visibility: hidden;
    }
    .el-cascader-panel .el-cascader-node__postfix {
        top: 10px;
    }
</style>