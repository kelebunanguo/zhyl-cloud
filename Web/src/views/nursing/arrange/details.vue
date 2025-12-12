<template>
  <div class="detail-base intelligenc">
    <!-- 基本信息 -->
    <div ref="mainHeight" class="bg-wt">
      <el-card header="基本信息">
        <div class="info-block">
          <div class="info-item">
            <h1 class="label-wt-long">老人姓名：</h1>
            <span>{{ baseData.elderName || '--' }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">护理等级：</h1>
            <span>{{ baseData.nursingLevelName || '--' }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">性别：</h1>
            <span>{{ baseData.sex || '--' }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">年龄：</h1>
            <span>{{ baseData.age || '--' }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">床位号：</h1>
            <span>{{ baseData.bedNumber }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">护理员姓名：</h1>
            <span>{{
              baseData.nursingName ? baseData.nursingName.join(',') : '--'
            }}</span>
          </div>
        </div>
      </el-card>
      <!-- 护理项目 -->
      <el-card header="护理项目" :class="baseData.status === 1 ? 'noBottomLine' : ''">
        <div class="info-block">
          <div class="info-item">
            <h1 class="label-wt-long">护理项目名称：</h1>
            <span>{{ baseData.projectName }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">执行状态：</h1>
            <span>{{
              baseData.status === 1
                ? '待执行'
                : baseData.status === 2
                  ? '已执行'
                  : '已取消'
            }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">创建时间：</h1>
            <span>{{ baseData.createTime }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">期望服务时间：</h1>
            <span>{{ baseData.estimatedServerTime }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt-long">备注信息：</h1>
            <span>{{ baseData.remark || '--' }}</span>
          </div>
        </div>
      </el-card>
      <!-- end -->
      <!-- 执行记录 -->
      <div v-if="baseData.status === 2">
        <el-card header="执行记录">
          <div class="info-block">
            <div class="info-item">
              <h1 class="label-wt-long">执行人：</h1>
              <span>{{ baseData.updater }}</span>
            </div>
            <div class="info-item">
              <h1 class="label-wt-long">执行时间：</h1>
              <span>{{ baseData.realServerTime || '--' }}</span>
            </div>
            <div class="info-item">
              <h1 class="label-wt-long">执行图片：</h1>
              <span class="img">
                <img alt="test" :src="baseData.taskImage" class="tdesign-demo-image-viewer__ui-image--img" />
              </span>
            </div>
            <div class="info-item">
              <h1 class="label-wt-long">执行记录：</h1>
              <span>{{ baseData.mark }}</span>
            </div>
          </div>
        </el-card>
      </div>
      <!-- end -->
      <!-- 取消记录 -->
      <div v-if="baseData.status === 3">
        <el-card header="取消记录">
          <div class="info-block">
            <div class="info-item">
              <h1 class="label-wt-long">取消人：</h1>
              <span>{{ baseData.updater }}</span>
            </div>
            <div class="info-item">
              <h1 class="label-wt-long">取消时间：</h1>
              <span>{{ baseData.updateTime }}</span>
            </div>
            <div class="info-item">
              <h1 class="label-wt-long">取消原因：</h1>
              <span>{{ baseData.cancelReason }}</span>
            </div>
          </div>
        </el-card>
        <!-- end -->
      </div>
    </div>
    <!-- end -->
  </div>
</template>
<script setup>
// 接口
import {
  getNursingTask,
} from '@/api/nursing/task';

// ------定义变量------
const route = useRoute(); // 获取局部
const router = useRouter(); // 获取全局
const baseData = ref({});
const taskId = ref(''); // 账单id
// 生命周期
onMounted(() => {
  taskId.value = route.query.id;
  getBaseData()
});
// ------定义方法------
// // 获取列表数据
const getBaseData = async () => {
  const res = await getNursingTask(taskId.value); // 获取列表数据
  if (res.code === 200) {
    baseData.value = res.data;
    // 年龄需要前端算
    baseData.value.age = getBirthday(baseData.value.idCardNo).age;
  }
};
// 返回
const handleReturn = () => {
  router.go(-1);
};
</script>
