import request from '@/utils/request'

// 查询房型列表
export function listRoomType(query) {
  return request({
    url: '/elder/roomType/list',
    method: 'get',
    params: query
  })
}
// 获取所有房型列表
export function getRoomTypesList() {
  return request({
    url: '/elder/roomType/listAll',
    method: 'get'
  })
}
// 查询房型详细
export function getRoomType(id) {
  return request({
    url: '/elder/roomType/' + id,
    method: 'get'
  })
}

// 新增房型
export function addRoomType(data) {
  return request({
    url: '/elder/roomType',
    method: 'post',
    data: data
  })
}

// 修改房型
export function updateRoomType(data) {
  return request({
    url: '/elder/roomType',
    method: 'put',
    data: data
  })
}

// 删除房型
export function delRoomType(id) {
  return request({
    url: '/elder/roomType/' + id,
    method: 'delete'
  })
}
