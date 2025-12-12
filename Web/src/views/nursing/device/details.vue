<template>
  <div class="detail-base intelligenc">
    <!-- 设备信息 -->
    <div ref="mainHeight" class="bg-wt">
      <el-card header="设备信息">
        <div class="info-block">
          <div class="info-item">
            <h1 class="label-wt">设备名称：</h1>
            <span>{{ baseData.deviceName }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">设备标识码：</h1>
            <span>{{ baseData.nodeId }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">设备ID：</h1>
            <span>{{ baseData.iotId }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">所属产品：</h1>
            <span>{{ baseData.productName }} </span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">密钥：</h1>
            <span>{{ baseData.secret }} </span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">设备状态：</h1>
            <span
              ><span v-if="baseData.deviceStatus === 'ONLINE'" class="bt-small"
                >在线</span
              >
              <span
                v-else-if="baseData.deviceStatus === 'OFFLINE'"
                class="undefinedBtn"
                >离线</span
              >
              <span
                v-else-if="baseData.deviceStatus === 'ABNORMAL'"
                class="undefinedBtn"
                >异常</span
              >
              <span
                v-else-if="baseData.deviceStatus === 'INACTIVE'"
                class="undefinedBtn"
                >未激活</span
              >
              <span v-else class="forbidBtn">已冻结</span></span
            >
          </div>
          <div class="info-item">
            <h1 class="label-wt">接入位置：</h1>
            <span>
              <span v-if="locationData.length === 1">{{
                locationData[0]
              }}</span>
              <span v-if="locationData.length === 2"
                >{{ locationData[0] }}-{{ locationData[1] }}</span
              >
              <span v-if="locationData.length === 3"
                >{{ locationData[0] }}-{{ locationData[1] }}-{{
                  locationData[2]
                }}</span
              >
            </span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">设备类型：</h1>
            <span>{{ baseData.locationType === 0 ? '随身设备' : '固定设备' }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">节点类型：</h1>
            <span>直连设备</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">认证类型：</h1>
            <span>密钥</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">创建时间：</h1>
            <span>{{ baseData.createTime }}</span>
          </div>
          <div class="info-item">
            <h1 class="label-wt">激活时间：</h1>
            <span>{{ baseData.activeTime ? baseData.activeTime : '--' }}</span>
          </div>
        </div>
      </el-card>
    </div>
    <!-- end -->
    <div ref="mainHeight" class="bg-wt min-steph height mt-24">
      <SwitchBar
        ref="tabBar"
        :data="facilityTabData"
        @change-id="changeId"
      ></SwitchBar>
      <!-- 物模型数据 -->
      <div v-if="activeIndex === 0">
        <div class="modelBox">
          <div class="modelL">
            <SwitchTabBar
              ref="tabBar"
              :data="modelTabData"
              @change-id="changeModelId"
            ></SwitchTabBar>
            <div class="modelList">
              <ul>
                <li
                  :class="isActive === 0 ? 'active' : ''"
                  @click="handleActive(0)"
                >
                  默认模块
                </li>
                <li
                  v-for="(item, index) in itemData"
                  :key="index"
                  :class="isActive === index + 1 ? 'active' : ''"
                  @click="handleActive(index + 1, item)"
                >
                  <p>{{ item.functionBlockName }}</p>
                  <p>标识符：{{ item.functionBlockId }}</p>
                </li>
              </ul>
            </div>
          </div>
          <div class="modelR">
            <el-table
              v-loading="loading"
              :data="publishedData"
              @selection-change="onPageChange"
            >
              <el-table-column
                label="序号"
                type="index"
                align="center"
                width="55"
              />
              <el-table-column
                label="标识符"
                align="center"
                prop="functionId"
                width="180"
              />
              <el-table-column label="更新时间" align="center" prop="eventTime">
                <template #default="scope">
                  <span>{{
                    scope.row.eventTime?parseTime(scope.row.eventTime, '{y}-{m}-{d} {h}:{i}:{s}'):'--'
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                label="数据值"
                align="center"
                prop="value"
                width="150"
              >
                <template #default="scope">
                  {{ scope.row.value}}
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
                    type="primary"
                    icon="ZoomIn"
                    @click="handleDetails(scope.row)"
                    >查看数据</el-button
                  >
                </template>
              </el-table-column>
            </el-table>

            <pagination
              v-show="pagination.total > 0"
              :total="pagination.total"
              v-model:page="pagination.pageNum"
              v-model:limit="pagination.pageSize"
              @pagination="getPropertyStatus"
            />
          </div>
        </div>
      </div>
      <!-- end -->
    </div>
    <LookData
      :dialogVisible="dialogVisible"
      :dialog-data="listLookData"
      :pagination="paginationLook"
      :nextValid="nextValid"
      :preValid="preValid"
      :total="total"
      @handle-search="handleLookSearch"
      @handle-close-dialog="handleCloseDialog"
      @get-list="getLookList"
      @onPage="onPage"
    ></LookData>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import dayjs from 'dayjs';
import { facilityTabData, modelTabData, timeData } from '@/utils/commonData';
import { getTimestamp, getStartTimeStr, getEndTimeStr } from '@/utils/date';
import { getNum } from '@/utils/validate';
// 接口
import {
  getDevice,
  // getPublishedList,
  getPropertyServiceList,
  getDataList,
} from '@/api/nursing/device';
// tab切换
import SwitchBar from '@/components/switchBar/switchBartop.vue';
import SwitchTabBar from '@/components/switchBar/switchBar.vue';
// 查看数据
import LookData from './components/runLookData.vue';
// ------定义变量------
const route = useRoute(); // 获取局部
const router = useRouter(); // 获取全局
const iotId = ref(''); // 设备id
const productKey = ref(''); // 产品key
const activeIndex = ref(0); // tabindex
const activeModelIndex = ref(0); //
const isActive = ref(0); // 默认数据索引值
const dialogVisible = ref(false); // 查看数据弹层
const listLookData = ref([]); // 查看数据
const eventListData = ref([]);
const dataLoading = ref(false); // 加载中
const baseData = ref({}); // 基础信息
const publishedData = ref([]); // 运行状态 服务调用
const statusObj = ref({}); // 获取运行管理所需要的参数
const eventObj = ref({
  startTime: dayjs().subtract(1, 'hours').format('YYYY-MM-DD HH:mm:ss'),
  endTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
}); // 获取服务调用所需要的参数
const locationData = ref([]); // 获取接入的位置名称分老人和楼层
const nextValid = ref(null); // 是否还有下一页
const preValid = ref(null); // 是否还有上一页
const dateData = ref(null); // 时间戳
const isNext = ref(false);
const nextEndTime = ref(null); // 下一个时间点
const timeValue = ref(0);
const typeValue = ref(0); // 时间筛选索引
const time = ref([]);
const isUpdate = ref(false); // 是否触发了数据更新
// 查看数据分页
const paginationLook = ref({
  pageSize: 5,
  pageNum: 1, // 默认当前页
  startTime: dayjs().subtract(1, 'hours').format('YYYY-MM-DD HH:mm:ss'),
  endTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
});
// 行的key
const rowKey = 'index';
const itemData = ref([]); // 默认模块数据
//
// 分页
const pagination = ref({
  pageSize: 10,
  pageNum: 1, // 默认当前页
});
const total = ref(0); // 总条数
// 生命周期
onMounted(() => {
  if (route.query.iotId !== undefined) {
    iotId.value = route.query.iotId;
    productKey.value = route.query.productKey;
    getDetails();
  }
});
// ------定义方法------
// 获取详情数据
const getDetails = async () => {
  const params = {
    iotId: iotId.value,
    productKey: productKey.value,
  };
  const res = await getDevice(params); // 获取列表数据
  if (res.code === 200) {
    baseData.value = res.data;
    paginationLook.value.deviceId = baseData.value.iotId;
    paginationLook.value.deviceName = baseData.value.deviceName;
    if (baseData.value.remark) {
      locationData.value = baseData.value.remark.split(',');
    }
    getPropertyStatus(baseData.value); // 获取详情的同时获取运行状态，需要把详情数据传给运行状态
  }
};
// // 获取运行状态
// const getPublished = async (val) => {
//   const params = {
//     productKey: productKey.value, // 产品key
//   };
//   const res = await getPublishedList(params); // 获取列表数据
//   if (res.code === 200) {
//     const data = JSON.parse(res.data.thingModelJson);
//     itemData.value = data.functionBlocks;
//     // 获取运行状态的接口
//     getPropertyStatus(val);
//   }
// };
// 详情运行状态状态的卡片
const getPropertyStatus = async (basedata) => {
  statusObj.value = {
    ...statusObj.value,
    deviceName: basedata.deviceName,
    productKey: productKey.value,
  };
  
  const res = await getPropertyServiceList(basedata.iotId); // 获取列表数据
  if (res.code === 200) {
    const data = res.data;
    publishedData.value = data;
  }
};
// 获取运行状态列表的查看数据
const getLookList = async (val) => {
  const res = await getDataList(paginationLook.value);
  if (res.code === 200) {
    const data = res.rows;
    listLookData.value = data;
    total.value = res.total;
    paginationLook.value.total = res.total;
  }
};
// 时间筛选分页回到第一页
const onPage = ()=>{
  paginationLook.value.pageNum = 1
}
// tab切换
const changeId = (val) => {
  activeIndex.value = val;
};
// 模型数据
const changeModelId = (val) => {
  activeModelIndex.value = val;
  getPublished(baseData.value);
};
// 触发默认模块
const handleActive = (i, item) => {
  isActive.value = i;
  isNext.value = false;
  isUpdate.value = true;
  eventListData.value = [];
  // 如果选择的是自定义模块需要传functionBlockId
  if (i > 0 && item !== undefined) {
    statusObj.value.functionBlockId = item.functionBlockId;
  } else {
    // 否则删除functionBlockId，显示默认模块的数据
    delete statusObj.value.functionBlockId;
  }
  getPublished(baseData.value);
};
const onPageChange = (val) => {
  pagination.value.pageNum = val.current;
  pagination.value.pageSize = val.pageSize;
};
// 打开查看数据弹层
const handleDetails = (row) => {
  paginationLook.value.functionId = row.functionId;
  dialogVisible.value = true;
  getLookList();
};
// 关闭查看数据弹层
const handleCloseDialog = () => {
  dialogVisible.value = false;
  paginationLook.value.startTime = dayjs().subtract(1, 'hours').format('YYYY-MM-DD HH:mm:ss');
  paginationLook.value.endTime = dayjs().format('YYYY-MM-DD HH:mm:ss');
  getLookList();
};
// 查看数据时间范围筛选
const handleLookSearch = (val) => {
  preValid.value = true;
  const pageLook = paginationLook.value;
  delete pageLook.startTime;
  delete pageLook.endTime;
  pageLook.endTime = dayjs().format('YYYY-MM-DD HH:mm:ss'); // 结束时间，现在的时间
  pageLook.startTime = dayjs().subtract(val, 'hours').format('YYYY-MM-DD HH:mm:ss');
  switch (val) {
    case '1':
      // 1小时
      dateData.value = 60 * 60 * 1000;
      break;
    case '24':
      // 24小时
      dateData.value = 24 * 60 * 60 * 1000;
      break;
    case '7':
      // 7天
      pageLook.startTime = dayjs().subtract(val, 'days').format('YYYY-MM-DD HH:mm:ss');
      dateData.value = 7 * 24 * 60 * 60 * 1000;
      break;
    default:
      dateData.value = null;
      pageLook.startTime = getStartTimeStr(val[0]);
      pageLook.endTime = getEndTimeStr(val[1]);
  }
  getLookList();
};
// 重置，清空搜索框
const handleLookReset = () => {
  // 重置页码
  paginationLook.value = {
    pageSize: 5,
    pageNum: 1,
  };
  getLookList();
};
// 翻页设置当前页
const getCurrent = (val) => {
  paginationLook.value.pageNum = val.current;
  paginationLook.value.pageSize = val.pageSize;
  getLookList();
};
// 返回
// const handleReturn = () => {
//   router.go(-1);
// };
</script>
<style lang="scss" scoped src="./index.scss"></style>
