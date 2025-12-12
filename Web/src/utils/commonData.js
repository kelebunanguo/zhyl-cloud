export const locationTypeOptions = [
  {
    id: 0,
    value: '随身设备',
  },
  {
    id: 1,
    value: '固定设备',
  },
];
export const facilityTabData = [
  {
    id: 0,
    name: '设备详情',
  },
  {
    id: 1,
    name: '物模型数据',
  },
];
export const modelTabData = [
  {
    id: 0,
    name: '运行状态',
  }
];
export const timeData = [
  {
    id: 0,
    value: '1小时',
  },
  {
    id: 1,
    value: '24小时',
  },
  {
    id: 2,
    value: '7天',
  }
];
// 报警管理
export const tabStatusData = [
  {
    id: null,
    value: '全部'
  },
  {
    id: 0,
    value: '待处理'
  },
  {
    id: 1,
    value: '已处理'
  }
]
// 运算符
export const operatorData = [
  {
    id: 0,
    value: '>='
  },
  // {
  //   id: 1,
  //   value: '>'
  // },
  // {
  //   id: 2,
  //   value: '<='
  // },
  {
    id: 3,
    value: '<'
  }
  // {
  //   id: 4,
  //   value: '!='
  // }
]
// 持续周期
export const continuePeriodData = [
  {
    id: 0,
    value: '1',
    label: '持续1个周期（1周期=1分钟）'
  },
  {
    id: 1,
    value: '3',
    label: '持续3个周期（1周期=1分钟）'
  },
  {
    id: 2,
    value: '5',
    label: '持续5个周期（1周期=1分钟）'
  },
  {
    id: 3,
    value: '10',
    label: '持续10个周期（1周期=1分钟）'
  },
  {
    id: 4,
    value: '20',
    label: '持续20个周期（1周期=1分钟）'
  },
  {
    id: 5,
    value: '30',
    label: '持续30个周期（1周期=1分钟）'
  }
]
// 数据聚合周期
export const aggregationPeriodData = [
  {
    id: 0,
    value: 1
  },
  {
    id: 1,
    value: 5
  }
]
// 报警沉默周期
export const silenceCycleData = [
  {
    id: 0,
    value: 5
  },
  {
    id: 1,
    value: 10
  },
  {
    id: 2,
    value: 15
  },
  {
    id: 3,
    value: 30
  },
  {
    id: 4,
    value: 60
  },
  {
    id: 5,
    value: 3
  },
  {
    id: 6,
    value: 6
  },
  {
    id: 7,
    value: 12
  },
  {
    id: 8,
    value: 24
  }
]
// 数据类型
export const dataType = [
  {
    id: 0,
    value: '老人异常数据'
  },
  {
    id: 1,
    value: '设备异常数据'
  }
]
// 任务安排
// 报警管理
export const arrangeTabData = [
  {
    id: 1,
    value: '待执行'
  },
  {
    id: 2,
    value: '已执行'
  },
  {
    id: 3,
    value: '已取消'
  }
]