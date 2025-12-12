<!--房型列表-->
<template>
  <div v-if="floorData.length">
    <!-- 房间列表 -->
    <div v-if="roomVoList && roomVoList.length" class="houseList">
      <div v-for="(roomItem, roomindex) in roomVoList" :key="roomindex" class="item">
        <!-- 房间标题 -->
        <div class="title">
          <div class="lText">
            <span class="name">{{ roomItem.code }}</span><span class="num">{{ roomItem.typeName }}</span>
            <div class="icon">
              <span class="edit" @click="handleRoomEdit(roomItem.id)"></span><span class="delect" :class="roomItem.bedVoList && roomItem.bedVoList.length > 0
                  ? 'forbidDelect'
                  : ''
                " @click="handleDelete(2, roomItem)"></span>
              <!-- <span class="check" @click="handleRoomCheck(roomItem.id)"></span> -->
            </div>
          </div>
          <span class="add" @click="handleBedAdd(roomItem.id)"></span>
        </div>
        <!--  -->
        <!-- 床位列表 -->
        <div class="bedList">
          <div v-if="roomItem.bedVoList && roomItem.bedVoList.length">
            <ul>
              <li v-for="(item, index) in roomItem.bedVoList" :key="index" class="leisure">
                <div class="bedIcon">
                  <span :class="item.ename ? 'checkInIcon' : 'leisureIcon'"></span>
                </div>
                <div class="bedText">
                  <p>床位号：{{ item.bedNumber }}</p>
                  <p v-if="
                    item.bedStatus === 2 ||
                    (item.bedStatus === 1 && item.ename)
                  ">
                    {{ item.ename ? item.ename : '空闲' }}
                  </p>
                  <p v-else>空闲</p>
                </div>
                <div class="bedHover">
                  <span class="edit" :class="item.bedStatus ? 'forbidDelect' : ''" @click="handleBedEdit(item)"></span>
                  <!-- -->
                  <span class="delect" :class="item.bedStatus ? 'forbidDelect' : ''"
                    @click="handleDelete(3, item)"></span>
                </div>
              </li>
            </ul>
          </div>
          <div v-else class="noData">当前房间没有安排床位</div>
        </div>
        <!-- end -->
      </div>
    </div>
    <!-- end -->
    <div v-else>
      <NoData></NoData>
    </div>
  </div>
  <div v-else>
    <NoData></NoData>
  </div>
</template>
<script setup>
import { ref } from 'vue'
// 床位信息新增房间
import FloorInfo from './FloorInfo.vue'
import NoData from '@/components/noData/index.vue' // 无数据提示组件
// ------定义变量------

// 获取父组件值、方法
const props = defineProps({
  //  房间数据
  roomVoList: {
    type: Array
  },
  // 楼层数据
  floorData: {
    type: Array
  },
  bedSelectData: {
    type: Object,
    default: () => {
      return {}
    }
  }
})
// 触发父级事件
const emit = defineEmits([
  'handleEdit',
  'handleDelete',
  'handleAdd',
  'handleBedEdit',
  'handleCheck',
  'getBedInfo'
])
const isCheck = ref('')
const isRoom = ref('')
// ------定义方法------
// 删除
const handleDelete = (type, item) => {
  // 如果房间里有床位或者床位上有住人，禁止删除此信息
  if ((item.bedVoList && item.bedVoList.length === 0) || (type === 3 && !item.bedStatus)) {
    emit('handleDelete', type, item)
  }
}
// 房间编辑
const handleRoomEdit = (id) => {
  emit('handleEdit', id)
}
// 房间查看
const handleRoomCheck = (id) => {
  emit('handleCheck', id)
}
// 床位添加
const handleBedAdd = (id) => {
  emit('handleAdd', id)
}
// 床位编辑
const handleBedEdit = (item) => {
  if (!item.bedStatus) {
    emit('handleBedEdit', item.id)
  }
}
// 床位选择
const handleRadio = (room, item) => {
  isCheck.value = item.id
  isRoom.value = item.roomId
  const obj = {
    ...room,
    ...item,
    roomId: room.id,
    bedId: item.id
  }
  emit('getBedInfo', obj)
}
</script>
