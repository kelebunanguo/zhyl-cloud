import request from '@/utils/request'

//查询老人列表
export function listElderPageQuery (query) {
  return request({
    url: '/nursing/elder/list',
    method: 'get',
    params: query
  })
}

//申请入住
export function applyCheckIn (data) {
  return request({
    url: '/nursing/checkIn/apply',
    method: 'post',
    data: data
  })
}

//入住详情
export function getCheckInInfo (id) {
  return request({
    url: '/nursing/checkIn/detail/'+id,
    method: 'get'
  })
}

// 查询入住列表
export function listCheckIn(query) {
  return request({
    url: '/nursing/checkIn/list',
    method: 'get',
    params: query
  })
}

// 查询入住详细
export function getCheckIn(id) {
  return request({
    url: '/nursing/checkIn/' + id,
    method: 'get'
  })
}

// 新增入住
export function addCheckIn(data) {
  return request({
    url: '/nursing/checkIn',
    method: 'post',
    data: data
  })
}

// 修改入住
export function updateCheckIn(data) {
  return request({
    url: '/nursing/checkIn',
    method: 'put',
    data: data
  })
}

// 删除入住
export function delCheckIn(id) {
  return request({
    url: '/nursing/checkIn/' + id,
    method: 'delete'
  })
}
