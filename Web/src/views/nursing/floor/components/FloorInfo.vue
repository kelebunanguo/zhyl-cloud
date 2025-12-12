<!--新增房间-->
<template>
  <div class="floorInfo">
    <ul>
      <li class="bedIcon"><span class="leisureIcon"></span>空闲中</li>
      <li class="bedIcon"><span class="checkInIcon"></span>已入住</li>
      <li class="bedIcon"><span class="goOutIcon"></span>请假中</li>
    </ul>
    <el-button type="primary" @click="handleAdd">新增房间</el-button>
  </div>
</template>
<script setup>
// ------定义变量------
const emit = defineEmits(['handleAdd'])
// 获取父组件值、方法
// ------定义方法------
const handleAdd = () => {
  emit('handleAdd')
}
</script>
