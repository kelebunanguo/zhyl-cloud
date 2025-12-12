<template>
  <div class="min-h content-layout">
    <div class="bg-wt">
    <!-- 楼层tab导航 -->
    <SwitchBar
      ref="tabBar"
      :data="tabData"
      :route-type="routeType"
      @change-id="getFloorId"
    ></SwitchBar>
    <!-- end -->
    <!-- 房型列表 -->
    <HouseList
      :key="floorId"
      :floorId="floorId"
      :base-data="roomVoList"
      :floor-data="tabData"
      :route-type="routeType"
    ></HouseList>
    <!-- end -->
  </div>
  </div>
</template>
<script setup>
import useUserStore from '@/store/modules/user'
// 接口
import { listAllDeviceFloor,getRoomBedDeviceList } from '@/api/nursing/floor'
// 组件
// tab切换
import SwitchBar from '@/components/switchBar/switchBartop.vue'

// 房型列表
import HouseList from './components/List.vue'
// ------定义变量------
const route = useRoute() // 获取局部
const userStore = useUserStore()
// 楼层变量
const tabBar = ref(null) // 楼层的ref
const tabData = ref([]) // 定义tab切换数据
const roomVoList = ref([])
const floorId = ref('') // 楼层id
const floorIndex = ref(null)
// 房间变量
const routeType = ref(null)
// 获取全部数据
const allData = ref([])
const timerId = ref(null)
onUnmounted(() => {
  clearInterval(timerId.value)
})
// 默认加载数据
onMounted(() => {
  if (route.query.type !== undefined) {
    routeType.value = route.query.type
  }
  getAllFloorList()
})
// ------定义方法------
// 获取楼层数据
const getAllFloorList = async () => {
  const res = await listAllDeviceFloor()
  allData.value = res.data
  if (res.code === 200) {
    tabData.value = res.data
    // 首次进来需要拿楼层第一条数据的id来获取当前数据
    if (floorId.value === '' && tabData.value.length) {
      floorId.value = tabData.value[0].id
      userStore.floorId = tabData.value[0].id
    }
    getList()
  }
}
// 根据不同的楼层id楼层信息
const getList = async () => {
  const res = await getRoomBedDeviceList(floorId.value)
  if (res.code === 200) {
    roomVoList.value = res.data
  }
}
// 获取楼层id
const getFloorId = (id, i) => {
  // 点击异常楼层后消除对应的红点异常

  userStore.floorId = floorId.value
  floorIndex.value = i
  floorId.value = id
  getList()
}
</script>
<style lang="less" src="./../floor/index.scss"></style>
<style lang="scss" scoped>
.smartBed {
  min-height: calc(100vh - 130px);
}
</style>
