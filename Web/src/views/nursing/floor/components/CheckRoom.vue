<!-- 查看房间弹窗 -->
<template>
  <div class="dialogBox dialogTable roomDialog">
    <el-dialog v-model="visible" title="房间详情" :footer="false" @close="handleClose">
      <div>
        <div class="roomDetails">
          <ul>
            <li>
              房间号：<label>{{ data.code }}</label>
            </li>
            <li>
              房间类型：<label>{{ data.typeName }}</label>
            </li>
            <li>
              总床位数：<label>{{ data.totalBeds }}</label>
            </li>
            <li>
              入住床位数：<label>{{ data.occupiedBeds }}</label>
            </li>
            <li>
              入住率：<label class="font-bt5">{{
                data.occupancyRate === 0
                  ? 0
                  : (data.occupancyRate * 100).toFixed(2)
              }}%</label>
            </li>
          </ul>
        </div>
        <!-- table 数据 -->
        <div class="scrollTable" :class="showPageTip ? 'heighta' : 'dialogHeight'">
          <el-table id="app" ref="tableRef" row-key="id" :data="bedData ? bedData : []"
            :scroll="{ type: 'virtual', rowHeight: 48, bufferSize: 10 }" :height="400"
            :v-loading="bedData > 0 ? loadingNode : ''" @scroll="handleScroll($event)">
            <el-table-column label="床位号" align="center" prop="老人姓名" width="180" />
            <el-table-column label="老人姓名" align="center" prop="deviceName" width="180" />
            <el-table-column label="护理等级" align="center" prop="deviceName" width="180" />
            <!-- 入住期限 -->
            <el-table-column label="入住期限" align="center" prop="locationType" width="180">
              <template #default="scope">
                {{ getDateInfo(scope.row.checkInConfigVo.checkInStartTime) }}~{{
                  getDateInfo(scope.row.checkInConfigVo.checkInEndTime)
                }}
              </template>
            </el-table-column>

            <!-- end -->
            <!-- 暂无数据 -->
            <!-- <template #empty>
              <NoData></NoData>
            </template> -->
            <!-- end -->
          </el-table>
        </div>
        <div v-if="showPageTip" class="footInfo">已经到底了~</div>
        <!-- end -->
      </div>
    </el-dialog>
  </div>
</template>
<script setup>
// import NoData from '@/components/noData/index.vue'
import { getDateInfo } from '@/utils/date';
import NoData from '@/components/noData/index.vue';
// 获取父组件值、方法
const props = defineProps({
  // 获取房间列表数据
  data: {
    type: Object,
    default: () => ({}),
  },
  //   获取床位列表
  bedData: {
    type: Array,
    default: () => [],
  },
  // 弹层隐藏显示
  dialogVisible: {
    type: Boolean,
    default: false,
  },
});
// ------定义变量------
const emit = defineEmits(['handleClose']); // 子组件获取父组件事件传值
const tableRef = ref(null); // 定义表格ref
const visible = ref(false); // 用来控制弹层显示隐藏
const fixedTopAndBottomRows = ref(false);
const showPageTip = ref(false); // 数据到底了提示
const asyncLoading = ref('loading-custom');
const customLoadingNode = () => {
  return `已经到底了`;
};
const loadingNode = computed(() =>
  asyncLoading.value === 'loading-custom'
    ? customLoadingNode
    : asyncLoading.value
);
// 监听弹层是显示还是隐藏
watch(
  () => props.dialogVisible,
  (newVal) => {
    visible.value = newVal;
  }
);
// ------生命周期------
const handleScroll = (e) => {
  const { scrollTop, clientHeight, scrollHeight } = e.e.target;
  if (scrollTop + clientHeight === scrollHeight) {
    // 判断是否全部加载完成
    asyncLoading.value = 'loading-custom';
    showPageTip.value = true;
    fixedTopAndBottomRows.value = true;
    // }
  } else {
    showPageTip.value = false;
    fixedTopAndBottomRows.value = false;
  }
};
// ------定义方法------
// 关闭弹层
const handleClose = () => {
  visible.value = false;
  emit('handleClose');
};
</script>
