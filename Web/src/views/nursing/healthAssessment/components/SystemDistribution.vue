<template>
  <div id="main1" ref="EcharRef" class="monitorContainer"></div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue';
import * as echarts from 'echarts';
import { number } from 'echarts/core';

const props = defineProps({
  systemScore: {
    type: Array,
    default: () => { },
  },
});
// 创建一个erchart线图
const EcharRef = ref(null);
const baseData = ref([]);
const baseTime = ref([]);
// 监听器，监听父级传递的visible值，控制弹窗显示隐藏
watch(props, (val) => {
  //   baseData.value = val.allDateArr;
  //   baseTime.value = val.allTimeArr;
  setOption();
});
onMounted(() => {
  //   baseData.value = props.allDateArr;
  //   baseTime.value = props.allTimeArr;
  setOption();
});
const setOption = () => {
  const chartDom = document.getElementById('main1');
  const myChart = echarts.init(chartDom);
  let option = null;

  // nextTick(() => {
  // setTimeout(() => {
  const data = props.systemScore;
  const arr = [
    data.digestiveSystem,
    data.breathingSystem,
    data.senseSystem,
    data.motionSystem,
    data.urinarySystem,
    data.circulatorySystem,
    data.immuneSystem,
    data.endocrineSystem,
  ];
  let i = -1;
  option = {
    title: {
      text: '不同系统健康指数分布',
      left: 'center',
    },
    radar: {
      shape: 'circle',
      splitNumber: 4, // 雷达图圈数设置
      // 设置雷达图中间射线的颜色
      axisLine: {
        lineStyle: {
          color: 'rgba(131,141,158,.1)',
        },
      },
      indicator: [
        { name: '消化系统', max: 100, value: arr[0] },
        { name: '呼吸系统', max: 100, value: arr[1] },
        { name: '感官系统', max: 100, value: arr[2] },
        { name: '运动系统', max: 100, value: arr[3] },
        { name: '泌尿系统', max: 100, value: arr[4] },
        { name: '循环系统', max: 100, value: arr[5] },
        { name: '免疫系统', max: 100, value: arr[6] },
        { name: '内分泌系统', max: 100, value: arr[7] },
      ],
      radius: 120,
      axisName: {
        color: '#333',
        lineHeight: 18,
        textAlign: 'center',
        padding: [-5, 0],
        formatter(value, o) {
          return `{a|${o.value}分}\n{b|${value}}`
        },
        rich: {
          a: {
            textAlign: 'center',
          },
          b: {
            fontSize: 14,
            lineHeight: 18,
            textAlign: 'center',
          },
        },
      },

      //雷达图背景的颜色，在这儿随便设置了一个颜色，完全不透明度为0，就实现了透明背景
      splitArea: {
        show: false,
        areaStyle: {
          color: 'rgba(255,0,0,0)', // 图表背景的颜色
        },
      },
      splitLine: {
        show: true,
        lineStyle: {
          width: 1,
          type: 'dashed',
          color: '#d9d9d9', // 设置网格的颜色
        },
      },
    },
    series: [
      {
        type: 'radar',
        symbol: 'rect', // 拐点的样式，还可以取值'rect','angle'等
        symbolSize: 0, // 拐点的大小
        data: [
          {
            value: arr,
            name: 'Actual Spending',
            // 设置区域边框和区域的颜色
            itemStyle: {
              normal: {
                color: 'rgba(255,225,0,0)',
                lineStyle: {
                  color: 'rgba(255,225,0,0)',
                },
              },
            },
            areaStyle: {
              color: 'rgba(86,194,39,0.5)', //默认存在透明度
            },
          },
        ],
      },
    ],
    graphic: {
      type: 'text',
      left: 'center',
      top: 'center',
      z: 100,
      style: {
        text: '0', // 这里的数字需要计算出数据的平均值然后替换
        textAlign: 'center',
        fill: '#000',
        fontSize: 30,
      },
    },
  };

  myChart.setOption(option);
  // 计算平均值的函数
  function calculateAverage(data) {
    var sum = data.reduce((prev, curr) => prev + curr, 0);
    return (sum / data.length).toFixed(2); // 保留两位小数
  }
  option.graphic.style.text = calculateAverage(arr) + '分';
  myChart.setOption(option);
  // }, 500);
  // });
};
</script>
