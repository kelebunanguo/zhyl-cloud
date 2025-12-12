<!--负责老人-->
<template>
  <div class="min-h content-layout">
    <div class="bg-wt">
      <!-- 楼层tab导航 -->
      <SwitchBar ref="tabBar" :data="tabData" :route-type="routeType" @change-id="getFloorId"></SwitchBar>
      <!-- end -->
      <!-- 房型列表 -->
      <HouseList :base-data="roomVoList" :floor-data="tabData" :route-type="routeType" @handleCheck="handleRoomCheck"
        @handleSelectOlder="handleSelectOlder" @handleSingleOlder="handleSingleOlder"></HouseList>
      <!-- end -->
      <!--设置 -->
      <Setting ref="formRef" :visible="checkVisible" :data="nurseList" :singleNurse="singleNurse"
        @handle-close="handleCheckRoomClose" @handle-submit="handleSubmit"></Setting>
      <!-- end -->
    </div>
  </div>
</template>
<script setup>
import useUserStore from '@/store/modules/user';
import { getAllUserList } from '@/api/system/user';
const { proxy } = getCurrentInstance();
// 接口
import {
  listAllWithNurFloor,
  getRoomBedWithNurList,
  setNursing,
} from '@/api/nursing/floor';
// 组件
// tab切换
import SwitchBar from '@/components/switchBar/switchBartop.vue';
// 房型列表
import HouseList from './components/List.vue';
// 设置
import Setting from './components/Setting.vue';
// 床位添加编辑弹层
// ------定义变量------
const userStore = useUserStore();
const route = useRoute(); // 获取局部
// 楼层变量
const tabBar = ref(null); // 楼层的ref
const tabData = ref([]); // 定义tab切换数据
const roomVoList = ref([]);
const floorId = ref(''); // 楼层id
const floorIndex = ref(null);
// 房间变量
const checkVisible = ref(false); // 查看房间弹层显示/隐藏

const routeType = ref(null);

// 设置护理员的老人
const selectOlder = ref([]);
const singleNurse = ref([]);
const formRef = ref(null);

// 护理员下拉数据
const nurseList = ref([]);
// 默认加载数据
onMounted(() => {
  if (route.query.type !== undefined) {
    routeType.value = route.query.type;
  }
  getAllFloorList();
  getAllUserListFunc();
});
// ------定义方法------
// 获取单个设置老人的护理人数据
const handleSingleOlder = (val) => {
  console.log(val, 1)
  singleNurse.value = val;
};
// 获取要设置的老人数据
const handleSelectOlder = (val) => {
  console.log(val)
  selectOlder.value = val;
};
// 给老人设置护理员
const handleSubmit = async (val) => {
  const params = selectOlder.value.map((item) => {
    return { elderId: item.elderId, nursingIds: val }
  })
  const res = await setNursing(params);
  if (res.code === 200) {
    proxy.$modal.msgSuccess('设置成功');
    getAllFloorList();
    handleCheckRoomClose();
    formRef.value.handleClear();
  } else {
    MessagePlugin.error(res.msg);
  }
};
// 获取护理员下拉数据
const getAllUserListFunc = async () => {
  const res = await getAllUserList({ deptId: '201' });
  nurseList.value = res.data;
  //   nurseList.value = res.data.map((item) => {
  //     return { value: item.id, label: item.nickName }
  //   })
};
// 获取楼层数据
const getAllFloorList = async () => {
  const res = await listAllWithNurFloor();
  if (res.code === 200) {
    tabData.value = res.data;
    // 首次进来需要拿楼层第一条数据的id来获取当前数据
    if (floorId.value === '') {
      floorId.value = tabData.value[0].id;
    }
    getList();
  }
};
// 根据不同的楼层id房间信息
const getList = async () => {
  const res = await getRoomBedWithNurList(floorId.value);
  if (res.code === 200) {
    roomVoList.value = res.data;
  }
};
// 获取楼层id
const getFloorId = (id, i) => {
  floorIndex.value = i;
  floorId.value = id;

  getAllFloorList();
  userStore.setFloorInfo({});
};
// 查看房间
const handleRoomCheck = () => {
  // handleRoomData(id)
  checkVisible.value = true;
};
// 关闭查看房间
const handleCheckRoomClose = () => {
  checkVisible.value = false;
  selectOlder.value = [];
  singleNurse.value = [];
};
</script>
<style lang="less" src="./../floor/index.scss"></style>
