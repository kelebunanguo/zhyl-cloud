<template>
  <div id="main" ref="EcharRef" class="monitorContainer"></div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue';
import * as echarts from 'echarts';

const props = defineProps({
  allDateArr: {
    type: Array,
    default: () => [],
  },
  allTimeArr: {
    type: Array,
    default: () => [],
  },
  tabActive: {
    type: Number,
    default: 0,
  },
  baseData: {
    type: Object,
    default: () => { },
  },
});
// 创建一个erchart线图
const EcharRef = ref(null);
const objData = ref([]);
const baseTime = ref([]);
// 监听器，监听父级传递的visible值，控制弹窗显示隐藏
watch(props, (val) => {
  objData.value = val.baseData;
  console.log(objData.value);
  setOption();
});
onMounted(() => {
  //   baseData.value = props.allDateArr;
  //   baseTime.value = props.allTimeArr;
  setOption();
});
const setOption = () => {
  const chartDom = document.getElementById('main');
  const myChart = echarts.init(chartDom);
  let option = null;
  nextTick(() => {
    // // There should not be negative values in rawData
    // const rawData = [
    //   // [50, 20, 15, 10, 5],
    //   // [45, 25, 15, 10, 5],
    //   // [40, 25, 20, 10, 5],
    //   // [35, 25, 20, 15, 5],
    //   // [30, 20, 20, 20, 10],
    //   [50, 45, 40, 35, 30],
    //   [20, 25, 25, 25, 20],
    //   [15, 15, 20, 20, 20],
    //   [10, 10, 10, 15, 20],
    //   [5, 5, 5, 5, 10],
    // ];
    // const totalData = [];
    // for (let i = 0; i < rawData[0].length; ++i) {
    //   let sum = 0;
    //   for (let j = 0; j < rawData.length; ++j) {
    //     sum += rawData[j][i];
    //   }
    //   totalData.push(sum);
    // }
    // const grid = {
    //   left: 100,
    //   right: 100,
    //   top: 50,
    //   bottom: 50,
    // };
    // const series = ['健康', '提示', '风险', '危险', '严重危险'].map(
    //   (name, sid) => {
    //     return {
    //       name,
    //       type: 'bar',
    //       stack: 'total',
    //       barWidth: '20%',
    //       emphasis: {
    //         focus: 'series',
    //       },
    //       data: rawData[sid].map((d, did) => (totalData[sid] <= 0 ? 0 : d)),
    //     };
    //   }
    // );
    // option = {
    //   title: {
    //     text: '不同年龄人群健康指数分布',
    //     left:'center'
    //   },
    //   color: ['#57c227', '#fbcf00', '#fea102', '#fe464d', '#537cfe'],
    //   tooltip: {
    //     trigger: 'axis',
    //     axisPointer: {
    //       // Use axis to trigger tooltip
    //       type: 'shadow', // 'shadow' as default; can also be 'line' or 'shadow'
    //     },
    //   },
    //   // 标签显示在下方
    //   legend: {
    //     x: 'center',
    //     y: 'bottom',
    //     selectedMode: false,
    //   },
    //   grid,
    //   yAxis: {
    //     type: 'value',
    //     min: 10, // 设置Y轴最小值为10%
    //     max: 100, // 设置Y轴最大值为100%
    //     interval: 10, // 设置刻度的间隔为10%
    //     axisLabel: {
    //       // 使用 formatter 函数格式化标签
    //       formatter: '{value} %',
    //     },
    //   },
    //   xAxis: {
    //     type: 'category',
    //     data: ['50岁-', '60岁', '70岁', '80岁', '90岁+'],
    //   },
    //   series,
    // };
    // 原始数据
    const rawData = [
      [50, 45, 40, 35, 30],
      [20, 25, 25, 25, 20],
      [15, 15, 20, 20, 20],
      [10, 10, 10, 15, 20],
      [5, 5, 5, 5, 10],
    ];

    // 添加一个系列，确保 "Tue" 的数据为 0.5
    // rawData.push([0, 50, 0, 0, 0]); // 新增一行数据，仅 "Tue" 有值

    // 计算总数
    const totalData = [];
    for (let i = 0; i < rawData[0].length; ++i) {
      let sum = 0;
      for (let j = 0; j < rawData.length; ++j) {
        sum += rawData[j][i];
      }
      totalData.push(sum);
    }

    const grid = {
      left: 100,
      right: 100,
      top: 50,
      bottom: 50,
    };
    const ageData = ['50岁-', '60岁', '70岁', '80岁', '90岁+'];
    let age = objData.value.age;
    let newAge = '';
    if (age > 50 && age <= 55) {
      newAge = '50岁-';
    } else if (age > 55 && age <= 65) {
      newAge = '60岁';
    } else if (age > 65 && age <= 75) {
      newAge = '70岁';
    } else if (age > 75 && age <= 85) {
      newAge = '80岁';
    } else {
      newAge = '90岁';
    }
    let ageIndex = ageData.findIndex(function (element) {
      return element === newAge;
    });

    const healthScore = Number(objData.value.healthScore) / 100;
    console.log(healthScore);
    const series = ['健康', '提示', '风险', '危险', '严重危险'].map(
      (name, sid) => {
        return {
          name,
          type: 'bar',
          stack: 'total',
          barWidth: '20%',
          label: {
            show: false,
            formatter: (params) => Math.round(params.value * 1000) / 10 + '%',
          },
          data: rawData[sid].map((d, did) =>
            totalData[did] <= 0 ? 0 : d / totalData[did]
          ),
          markPoint:
            sid === ageIndex
              ? {
                data: [
                  {
                    type: 'max',
                    name: 'Max',
                    symbol: 'circle',
                    symbolSize: 10,
                    // symbol: 'image://url/to/your/image.png', // 自定义图标，使用图片
                    //   symbolSize: [30, 30] // 图标大小，可以是单独的宽度或高度
                    value: '当前位置',
                    coord: [newAge, healthScore],
                    label: {
                      show: true,
                      position: 'top',
                    },
                    itemStyle: { color: '#8e0606' },
                  },
                ],
              }
              : {},
        };
      }
    );

    const option = {
      title: {
        text: '不同年龄人群健康指数分布',
        left: 'center',
      },
      color: ['#57c227', '#fbcf00', '#fea102', '#fe464d', '#537cfe'],
      // tooltip: {
      //   trigger: 'axis',
      //   axisPointer: {
      //     // Use axis to trigger tooltip
      //     type: 'shadow', // 'shadow' as default; can also be 'line' or 'shadow'
      //   },
      // },
      legend: {
        x: 'center',
        y: 'bottom',
        selectedMode: false,
      },
      grid,
      yAxis: {
        type: 'value',
        // min: 10, // 设置Y轴最小值为10%
        // max: 100, // 设置Y轴最大值为100%
        // interval: 10, // 设置刻度的间隔为10%
      },
      xAxis: {
        type: 'category',
        data: ageData,
      },
      series,
    };

    myChart.setOption(option);
  });
};
</script>
