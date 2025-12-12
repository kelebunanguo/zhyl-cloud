<!-- tab栏切换组件 -->
<template>
  <div class="headBox">
    <div class="sw-box">
      <span
        v-for="(item, index) in data"
        :key="index"
        :class="{ title: true, line: currentId == index }"
        @click.stop="changeId(item.id, index)"
        >{{ item.name }}</span
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 父组件传值
defineProps({
  data: {
    type: Array,
    default: []
  }
})
// 触发父组件事件
const emit: Function = defineEmits(['changeId', 'handleFloorEdit'])
// 当前选中id
const currentId = ref(0)
// 点击切换
const changeId = (id, i) => {
  currentId.value = i
  emit('changeId', id, i)
}
// 传给父级
defineExpose({
  currentId
})
</script>
<style lang="scss" scoped>
@import '@/assets/styles/variables.module.scss';
.headBox {
  padding-bottom: 14px;
  border-bottom: 1px solid #e8e8e8;
  line-height: 22px;
  position: relative;
  padding-top: 20px;
  padding-right: 80px;
  .sw-box {
    font-family: PingFangSC-SNaNpxibold;
    font-weight: 400;
    font-size: 16px;
    color: $--color-black;
    .title {
      color: $--color-black;
      font-size: 14px;
      text-align: center;
      display: inline-block;
      //  文字左右居中
      text-align: center;
      cursor: pointer;
      position: relative;
      &:hover {
        color: $--color-main;
        i {
          position: absolute;
          display: inline-block;
          width: 16px;
          height: 16px;
          // margin-left: 0px;
          top: 2px;
          z-index: 9;
        }
        .delect {
          right: -2px;
        }
        .edit {
          background: url(../../assets/images/icon-bj.png) no-repeat;
          background-size: contain;
          right: -2px;
        }
      }
    }
    .line {
      color: $--color-main;
      font-weight: 500;
      position: relative;
      cursor: pointer;
      &::before {
        position: absolute;
        left: 50%;
        transform: translate(-50%);
        content: '';
        width: 100%;
        height: 2px;
        border-radius: 4px;
        bottom: -15px;
        background: $--color-main;
      }
    }
  }
  .rText {
    position: absolute;
    right: 0;
    top: 20px;
    color: $--color-link;
    font-size: 14px;
    cursor: pointer;
  }
}
.value {
  margin-left: 5px;
}
</style>
