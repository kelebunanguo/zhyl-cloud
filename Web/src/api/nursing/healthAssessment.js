import request from '@/utils/request'

// 查询健康评估列表
export function listHealthAssessment(query) {
  return request({
    url: '/nursing/healthAssessment/list',
    method: 'get',
    params: query
  })
}

// 查询健康评估详细
export function getHealthAssessment(id) {
  return request({
    url: '/nursing/healthAssessment/' + id,
    method: 'get'
  })
}

// 新增健康评估
export function addHealthAssessment(data) {
  return request({
    url: '/nursing/healthAssessment',
    method: 'post',
    data: data
  })
}

// 修改健康评估
export function updateHealthAssessment(data) {
  return request({
    url: '/nursing/healthAssessment',
    method: 'put',
    data: data
  })
}

// 删除健康评估
export function delHealthAssessment(id) {
  return request({
    url: '/nursing/healthAssessment/' + id,
    method: 'delete'
  })
}
