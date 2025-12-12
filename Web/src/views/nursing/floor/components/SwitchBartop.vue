<!-- tab栏切换组件 -->
<template>
  <div class="headBox">
    <div class="sw-box" :class="routeType === 'config' ? 'configTpye' : ''">
      <span v-for="(item, index) in data" :key="index" :class="{ title: true, line: currentId == index }"
        @click.stop="changeId(item.id, index)">{{ item.name
        }}<i class="edit" @click.stop="handleEdit(item.id, index)"></i><span v-if="item.value" class="value">{{
          item.value }}</span></span>
      <div v-if="routeType !== 'config'" class="rText" @click="handleAdd">
        新增楼层
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
// 父组件传值
const props = defineProps({
  data: {
    type: Array<any>,
    default: []
  },
  routeType: {
    type: String,
    default: null
  },
  floorIndex: {
    type: Number,
    default: 0
  }
})
// 触发父组件事件
const emit: Function = defineEmits(['changeId', 'handleFloorEdit', 'handleAdd'])
// 当前选中id
const currentId = ref(0)
const isTabCheck = ref(false)
watch(
  () => props.floorIndex,
  (val) => {
    // 判断是不是从入住配置页面进来的
    if (!isTabCheck.value) {
      if (props.routeType === 'config') {
        currentId.value = val
      }
    }
  }
)
// 点击切换
const changeId = (id, i) => {
  isTabCheck.value = true
  currentId.value = i
  emit('changeId', id, i, true)
}
// 编辑
const handleEdit = (id, i) => {
  emit('handleFloorEdit', id, i)
}
// 新增
const handleAdd = () => {
  emit('handleAdd')
}
// 传给父级
defineExpose({
  currentId
})
</script>
<style lang="scss" scoped>
.value {
  margin-left: 5px;
}
</style>
