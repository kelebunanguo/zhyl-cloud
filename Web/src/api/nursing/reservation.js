import request from '@/utils/request'

// 查询预约信息列表
export function listReservation(query) {
  return request({
    url: '/nursing/reservation/list',
    method: 'get',
    params: query
  })
}

// 查询预约信息详细
export function getReservation(id) {
  return request({
    url: '/nursing/reservation/' + id,
    method: 'get'
  })
}

// 新增预约信息
export function addReservation(data) {
  return request({
    url: '/nursing/reservation',
    method: 'post',
    data: data
  })
}

// 修改预约信息
export function updateReservation(data) {
  return request({
    url: '/nursing/reservation',
    method: 'put',
    data: data
  })
}

// 删除预约信息
export function delReservation(id) {
  return request({
    url: '/nursing/reservation/' + id,
    method: 'delete'
  })
}
