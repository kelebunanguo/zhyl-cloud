import request from '@/utils/request'

export function getFloorBed(status) {
  return request({
    url: '/elder/floor/getRoomAndBedByBedStatus/'+status,
    method: 'get'
  })
}

export function getRootById(id) {
  return request({
    url: '/elder/room/one/'+id,
    method: 'get'
  })
}

// 查询楼层列表
export function listFloor(query) {
  return request({
    url: '/elder/floor/list',
    method: 'get',
    params: query
  })
}

// 查询楼层详细
export function getFloorDetails(id) {
  return request({
    url: '/elder/floor/' + id,
    method: 'get'
  })
}

// 新增楼层
export function addFloor(data) {
  return request({
    url: '/elder/floor',
    method: 'post',
    data: data
  })
}

// 修改楼层
export function updateFloor(data) {
  return request({
    url: '/elder/floor',
    method: 'put',
    data: data
  })
}

// 删除楼层
export function delFloor(id) {
  return request({
    url: '/elder/floor/' + id,
    method: 'delete'
  })
}
// 获取房间数据
export function getRoomBedList(id) {
  return request({
    url: '/elder/room/getRoomsByFloorId/'+id,
    method: 'get'
  })
}
// 查询房间详细
export function getRoomDetails(id) {
  return request({
    url: '/elder/room/' + id,
    method: 'get'
  })
}

// 新增房间
export function addRoom(data) {
  return request({
    url: '/elder/room',
    method: 'post',
    data: data
  })
}

// 修改房间
export function updateRoom(data) {
  return request({
    url: '/elder/room',
    method: 'put',
    data: data
  })
}
// 删除房间
export function delRoom(id) {
  return request({
    url: '/elder/room/' + id,
    method: 'delete'
  })
}
// 查询房间详细
export function getBedDetails(id) {
  return request({
    url: '/elder/bed/' + id,
    method: 'get'
  })
}
// 新增床位
export function addBed(data) {
  return request({
    url: '/elder/bed',
    method: 'post',
    data: data
  })
}

// 修改床位
export function updateBed(data) {
  return request({
    url: '/elder/bed',
    method: 'put',
    data: data
  })
}
// 删除床位
export function delBed(id) {
  return request({
    url: '/elder/bed/' + id,
    method: 'delete'
  })
}

// 智能床位
// 查询所有楼层（智能设备）
export function listAllDeviceFloor(query) {
  return request({
    url: '/elder/floor/getAllFloorsWithDevice',
    method: 'get',
    params: query
  })
}
// 获取房间数据（智能床位）
export function getRoomBedDeviceList(id) {
  return request({
    url: '/elder/room/getRoomsWithDeviceByFloorId/'+id,
    method: 'get'
  })
}
// 护理任务
// 查询所有楼层（负责老人）
export function listAllWithNurFloor(query) {
  return request({
    url: '/elder/floor/getAllFloorsWithNur',
    method: 'get',
    params: query
  })
}
// 获取房间数据（负责老人）
export function getRoomBedWithNurList(id) {
  return request({
    url: '/elder/room/getRoomsWithNurByFloorId/'+id,
    method: 'get'
  })
}
// 设置护理员
export function setNursing(data) {
  return request({
    url: '/elder/nursingElder/setNursing',
    method: 'post',
    data: data
  })
}