<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="老人姓名" prop="elderName">
        <el-input v-model="queryParams.elderName" placeholder="请输入老人姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCard">
        <el-input v-model="queryParams.idCard" placeholder="请输入身份证号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="入住情况" prop="admissionStatus" style="width: 200px;">
        <el-select v-model="queryParams.admissionStatus" placeholder="请选择入住情况" clearable>
          <el-option v-for="dict in admission_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="newBtn mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">上传体检报告</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="healthAssessmentList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" align="center" type="index" width="55" />
      <el-table-column label="老人姓名" align="center" prop="elderName" width="180" />
      <el-table-column label="身份证号" align="center" prop="idCard" width="180" />
      <el-table-column label="健康评分" align="center" prop="healthScore" />
      <el-table-column label="建议入住" align="center" prop="suggestionForAdmission">
        <template #default="scope">
          {{ scope.row.suggestionForAdmission === 0 ? '建议' : '不建议' }}
        </template>
      </el-table-column>
      <el-table-column label="推荐护理等级" align="center" prop="nursingLevelName" width="180" />
      <el-table-column label="入住情况" align="center" prop="admissionStatus">
        <template #default="scope">
          {{ scope.row.admissionStatus === 0 ? '已入住' : '未入住' }}
        </template>
      </el-table-column>
      <el-table-column label="总检日期" align="center" prop="totalCheckDate" width="180" />
      <el-table-column label="评估时间" align="center" prop="assessmentTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.assessmentTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="ZoomIn" @click="handleUpdate(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改健康评估对话框 -->
    <el-dialog title="上传体检报告" v-model="open" @close="cancel" width="600px" append-to-body>
      <div>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
          <el-form-item label="老人姓名：" prop="elderName">
            <el-input v-model="form.elderName" maxlength="10" show-word-limit placeholder="请输入老人姓名" />
          </el-form-item>
          <el-form-item label="老人身份证号：" prop="idCard">
            <el-input v-model="form.idCard" maxlength="18" show-word-limit placeholder="请输入身份证号" />
          </el-form-item>
          <el-form-item label="体检单位：" prop="physicalExamInstitution">
            <el-input v-model="form.physicalExamInstitution" maxlength="10" show-word-limit placeholder="请输入体检机构" />
          </el-form-item>
          <el-form-item label="体检报告：" prop="physicalReportUrl">
            <el-upload ref="pdfUrl" v-model:file-list="pdfUrlfileList" :action="pdfUrlAction" :headers="headers"
              :data="{ idCardNo: form.idCard }" accept=".pdf" :limit="1" :auto-upload="true" :multiple="false"
              :on-success="handleSuccess" :on-remove="() => handleRemove()" :before-upload="pdfUrlBeforeUpload"
              :on-exceed="handleExceed">
              <el-button size="small" type="primary">点击上传</el-button>
              <template #tip>
                <span class="fontCol">请上传pdf文件，大小在60M以内</span>
              </template>
            </el-upload>
          </el-form-item>
          <div v-if="updateLoading" class="update">
            <div class="updateLoad">
              <div class="bigBg"></div>
              <div class="smallBg"></div>
              <div class="textInfo">
                <p class="text">AI</p>
                <p>分析中...</p>
              </div>
            </div>
          </div>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- end -->
  </div>
</template>

<script setup name="HealthAssessment">
import { genFileId } from 'element-plus';
import {
  listHealthAssessment,
  getHealthAssessment,
  delHealthAssessment,
  addHealthAssessment,
  updateHealthAssessment,
} from '@/api/nursing/healthAssessment';
import { getToken } from '@/utils/auth';
import { isCardID } from '@/utils/validate';
const { proxy } = getCurrentInstance();
const { admission_status } = proxy.useDict('admission_status');

const healthAssessmentList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
// 上传请求路径
const pdfUrlAction = ref('/dev-api/nursing/healthAssessment/upload');
const headers = {
  Authorization: 'Bearer ' + getToken(),
};
const updateLoading = ref(false); //图片上传中
// 上传文件列表
const formRef = ref();
const pdfUrlfileList = ref([]);
const pdfUrl = ref(null);
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    elderName: null,
    idCard: null,
    admissionStatus: null,
  },
  rules: {
    elderName: [
      {
        required: true,
        message: '请输入老人姓名',
        trigger: 'blur',
      },
    ],
    idCard: [
      {
        required: true,
        message: '请输入老人身份证号',
        trigger: 'blur',
      },
      {
        validator: isCardID,
        message: '身份证格式错误，请重新输入',
        trigger: 'blur',
      },
    ],
    physicalExamInstitution: [
      {
        required: true,
        message: '请输入体检单位',
        trigger: 'blur',
      },
    ],
    physicalReportUrl: [
      { required: true, message: '请上传体检报告', trigger: 'change' },
    ],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询健康评估列表 */
function getList() {
  loading.value = true;
  listHealthAssessment(queryParams.value).then((response) => {
    healthAssessmentList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  updateLoading.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    elderName: null,
    idCard: null,
    physicalExamInstitution: null,
    physicalReportUrl: null,
  };
  pdfUrlfileList.value = [];
  proxy.resetForm('healthAssessmentRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加健康评估';
}

/** 修改按钮操作 */
function handleUpdate(row) {
  proxy.$router.push({
    path: '/enterQuit/healthDetails',
    query: { id: row.id },
  });
}

// 上传之前的文件判断，判断文件大小文件类型等
const pdfUrlBeforeUpload = (file) => {
  // 校检文件大小
  const isLt = file.size / 1024 / 1024 < 60;
  if (!isLt) {
    proxy.$modal.msgError(`文件大小超过60m，请重新上传!`);
    return false;
  }
};
const handleExceed = (files) => {
  pdfUrl.value.clearFiles();
  const file = files[0];
  file.uid = genFileId();
  pdfUrl.value.handleStart(file);
};
//上传文件成功的处理
const handleSuccess = (res) => {
  form.value.physicalReportUrl = res.url;
  formRef.value.validateField('physicalReportUrl');
};

//删除文件的处理
const handleRemove = () => {
  form.value.physicalReportUrl = '';
};
/** 提交按钮 */
function submitForm() {
  formRef.value.validate(async (valid) => {
    if (!valid) return;
    updateLoading.value = true;
    const res = await addHealthAssessment(form.value);
    if (res.code === 200) {
      proxy.$modal.msgSuccess(`操作成功`);
      cancel();
      proxy.$router.push({
        path: '/enterQuit/healthDetails',
        query: { id: res.data },
      });
    }
  });
}

getList();
</script>
<style lang="scss" scoped>
@import '@/assets/styles/variables.module.scss';

.update {
  position: absolute;
  width: 100%;
  height: 80%;
  top: 0;
  left: 0;
  background: rgba(#fff, 0.8);

  .updateLoad {
    width: 300px;
    height: 300px;
    position: absolute;
    top: 55%;
    left: 50%;
    transform: translate(-50%, -50%);

    .bigBg {
      background: url(@/assets/images/bigbg.png) no-repeat;
      background-size: contain;
      width: 300px;
      height: 300px;
      position: absolute;
      animation: animationRight 5s infinite linear;
      top: 0;
    }

    .smallBg {
      background: url(@/assets/images/smallbg.png) no-repeat;
      background-size: contain;
      width: 300px;
      height: 300px;
      position: absolute;
      animation: animationLeft 3s infinite linear;
      top: 0;
    }

    .textInfo {
      position: absolute;
      left: 51%;
      top: 52%;
      transform: translate(-50%, -50%);
      color: $--color-link;
      line-height: 28px;

      .text {
        font-size: 46px;
        font-weight: 600;
      }
    }
  }
}

@keyframes animationRight {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

@keyframes animationLeft {
  from {
    transform: rotate(360deg);
  }

  to {
    transform: rotate(0deg);
  }
}
</style>
