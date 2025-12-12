import request from '@/utils/request'

// 查询指定产品下的设备列表
export function getDeviceList(val) {
  return request({
    url: '/nursing/device/list?productKey=' + val,
    method: 'get'
  })
}

// 查询设备列表
export function listDevice(query) {
  return request({
    url: '/nursing/device/list',
    method: 'get',
    params: query
  })
}

// 查询设备详细
export function getDevice(data) {
  return request({
    url: '/nursing/device/'+data.iotId,
    method: 'get'
  })
}
// 详情运行状态事件管理-查看指定产品的已发布物模型中的功能定义详情
export function getPublishedList(params) {
  return request({
    url: '/nursing/device/queryProduct/'+params.productKey,
    method: 'get'
  })
}
// 详情运行状态状态的卡片-列表
export function getPropertyStatusList(params) {
  return request({
    url: '/nursing/device/queryDevicePropertyStatus',
    method: 'post',
    data:params
  })
}
// 查看设备上报的数据-列表
export function getPropertyServiceList(iotId) {
  return request({
    url: '/nursing/device/queryServiceProperties/'+iotId,
    method: 'get'
  })
}
// 获取设备数据分页结果-查看数据
export function getDataList(params) {
  return request({
    url: '/nursing/data/list',
    method: 'get',
    params
  })
}
// 新增设备
export function addDevice(data) {
  return request({
    url: '/nursing/device/register',
    method: 'post',
    data: data
  })
}

// 修改设备
export function updateDevice(data) {
  return request({
    url: '/nursing/device',
    method: 'put',
    data: data
  })
}

// 删除设备
export function delDevice(iotId) {
  return request({
    url: '/nursing/device/' + iotId,
    method: 'delete'
  })
}

// 同步产品列表
export function syncProductList(id) {
  return request({
    url: '/nursing/device/syncProductList',
    method: 'post'
  })
}

// 查询产品列表
export function allProduct(id) {
  return request({
    url: '/nursing/device/allProduct',
    method: 'get'
  })
}
