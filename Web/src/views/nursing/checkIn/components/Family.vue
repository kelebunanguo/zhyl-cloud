<template>
  <div class="info family">
    <h3>家属信息</h3>
    <div class="tableHead">
      <div>家属姓名</div>
      <div>家属联系方式</div>
      <div>与老人关系</div>
      <div>操作</div>
    </div>
    <div class="tableBody">
      <div class="tableColumn" v-for="(item, index) in relationData" :key="index">
        <div class="column">
          <el-input :readonly="type === 'read'" v-model="item.name" placeholder="请输入家属姓名"></el-input>
        </div>
        <div class="column">
          <el-input :readonly="type === 'read'" v-model="item.phone" placeholder="请输入家属联系方式"></el-input>
        </div>
        <div class="column" style="width: 200px;">
          <el-select :disabled="type === 'read'" v-model="item.kinship" placeholder="请选择">
            <el-option v-for="item in elderRelation" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </div>
        <div class="column">
          <div v-if="type === 'read'">
            <el-button icon="plus"></el-button>
            <el-button icon="minus"></el-button>
          </div>
          <div v-else>
            <el-button icon="plus" @click="handleRowAdd"></el-button>
            <el-button icon="minus" @click="handleRowDel(item, index)"></el-button>
          </div>
        </div>

      </div>
    </div>

  </div>
</template>
<script setup>
import { watch } from "vue";

const relationData = ref([
  {
    name: '',
    phone: '',
    kinship: '',
  },
]);

const elderRelation = ref([
  { label: '子女', value: '0' },
  { label: '配偶', value: '1' },
  { label: '亲属', value: '2' },
  {
    value: '3',
    label: '朋友',
  },
  {
    value: '4',
    label: '社工',
  },
  {
    value: '5',
    label: '其他',
  },
]);

//接收父组件的数据
const props = defineProps({
  familyInfo: {
    type: Object,
    default: () => {
      return {}
    }
  },
  type: {
    type: String,
    default: () => {
      return {}
    }
  }
})

watch(
  props,
  () => {
    if (props.familyInfo.length) {
      relationData.value = props.familyInfo;
    }
  },
  { immediate: true }
);

const handleRowAdd = () => {
  const obj = {
    name: '',
    phone: '',
    kinship: '',
  }
  relationData.value.push(obj);
}
//删除行数据
const handleRowDel = (item, index) => {

  if (relationData.value.length === 1) return;
  relationData.value.splice(index, 1)
}

//声明父组件的方法
const emits = defineEmits(['setFamily'])
//监听数据变化，有变化，则将数据传递给父组件
watch(relationData.value, (val) => {
  emits('setFamily', val)
}, { deep: true })

</script>
