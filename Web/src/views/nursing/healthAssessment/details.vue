<template>
  <div class="detail-base">
    <!-- 基本信息 -->
    <div ref="mainHeight" class="bg-wt">
      <el-card header="基本信息">
        <div class="info-block">
          <div class="info-item">
            <h1 class="label-wt">老人姓名：</h1>
            <span>{{ baseData.elderName }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">老人身份证号：</h1>
            <span>{{ baseData.idCard }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">出生日期：</h1>
            <span>{{ baseData.birthDate }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">年龄：</h1>
            <span>{{ baseData.age }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">性别：</h1>
            <span>{{ baseData.gender === 0 ? '男' : '女' }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">体检机构：</h1>
            <span>{{ baseData.physicalExamInstitution }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">总检日期：</h1>
            <span>{{ baseData.totalCheckDate }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">体检报告：</h1>
            <span>{{ baseData.elderName }}的体检报告.pdf
              <i><a class="font-bt" :href="baseData.physicalReportUrl" target="black">查看</a></i>
            </span>
          </div>
        </div>
      </el-card>
    </div>
    <!-- end -->
    <!-- 体检总结 -->
    <div ref="mainHeight" class="bg-wt mt-24">
      <el-card header="体检总结">
        <div class="info-block">
          <div class="info-item">
            <h1 class="label-wt">健康评分：</h1>
            <span>{{ baseData.healthScore }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">风险等级：</h1>
            <span>{{ baseData.riskLevel }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">建议入住：</h1>
            <span>{{
              baseData.suggestionForAdmission === 0 ? '建议' : '不建议'
            }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">推荐等级：</h1>
            <span>{{ baseData.nursingLevelName }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">入住情况：</h1>
            <span>{{
              baseData.admissionStatus === 0 ? '已入住' : '未入住'
            }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">评估时间：</h1>
            <span>{{ baseData.assessmentTime }}</span>
          </div>
          <div class="info-item startTop">
            <h1 class="label-wt-long">分析报告：</h1>
            <span>{{ baseData.elderName }}的分析报告.pdf
              <i><a class="font-bt" :href="baseData.analysisReportUrl" target="black">查看</a></i>
            </span>
          </div>
          <div class="info-item startTop">
            <h1 class="label-wt">报告总结：</h1>
            <span>{{ baseData.reportSummary }}</span>
          </div>
        </div>
      </el-card>
    </div>
    <!-- end -->
    <!-- 疾病风险 -->
    <div ref="mainHeight" class="bg-wt mt-24">
      <el-card header="疾病风险">
        <div class="echartsBox">
          <!-- 不同年龄人群健康指数分布 -->

          <AgeDistribution :baseData="baseData"></AgeDistribution>
          <!-- end -->
          <!-- 不同系统健康指数分布 -->
          <SystemDistribution :systemScore="systemScore"></SystemDistribution>
          <!-- end -->
        </div>
      </el-card>
    </div>
    <!-- end -->
    <!-- 异常分析 -->
    <div ref="mainHeight" class="bg-wt mt-24">
      <el-card header="异常分析">
        <el-table :data="abnormalAnalysis" style="width: 100%">
          <el-table-column type="expand">
            <template #default="props">
              <div class="tableCon">
                <div class="item">
                  <span>异常解读：</span>{{ props.row.interpret }}
                </div>
                <div class="item">
                  <span>AI建议：</span>{{ props.row.advice }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="结论" prop="conclusion" />
          <el-table-column label="项目名称" prop="examinationItem" width="200" />
          <el-table-column label="检查结果" prop="result" />
          <el-table-column label="参考值" prop="referenceValue" />
          <el-table-column label="单位" prop="unit" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>
<script setup>
// 接口
import { getHealthAssessment } from '@/api/nursing/healthAssessment';
// 组件
// 不同年龄人群健康指数分布
import AgeDistribution from './components/AgeDistribution.vue';
// 不同系统健康指数分布
import SystemDistribution from './components/SystemDistribution.vue';
// ------ 定义变量 ------
const route = useRoute();
const baseData = ref({}); //详情数据
const abnormalAnalysis = ref([]); //异常分析数据
const diseaseRisk = ref({}); //疾病风险
const systemScore = ref({}); //健康指数分布
onMounted(() => {
  //获取路由参数,某一条入住的id
  const id = route.query.id;
  if (id) {
    getDetailsData(id);
  }
});
// 获取详情数据
const getDetailsData = (id) => {
  getHealthAssessment(id).then((res) => {
    baseData.value = res.data;
    abnormalAnalysis.value = JSON.parse(baseData.value.abnormalAnalysis);
    diseaseRisk.value = JSON.parse(baseData.value.diseaseRisk);
    systemScore.value = JSON.parse(baseData.value.systemScore);
  });
};
const getDataBeforeSpecialChar = (str, specialChar) => {
  const index = str.indexOf(specialChar);
  if (index !== -1) {
    return str.substring(0, index);
  }
  return str; // 如果特殊字符不存在，返回原字符串
};
</script>
<style lang="scss" scoped>
:deep(.monitorContainer) {
  width: 100%;
  height: 400px;

  &>div {
    &:first-child {
      width: 100% !important;

      &>canvas {
        width: 100% !important;
      }
    }
  }
}

.echartsBox {
  display: flex;
}
</style>
