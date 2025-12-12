import request from '@/utils/request'

// 查询护理计划和项目关联列表
export function listProjectplan(query) {
  return request({
    url: '/nursing/projectplan/list',
    method: 'get',
    params: query
  })
}

// 查询护理计划和项目关联详细
export function getProjectplan(id) {
  return request({
    url: '/nursing/projectplan/' + id,
    method: 'get'
  })
}

// 新增护理计划和项目关联
export function addProjectplan(data) {
  return request({
    url: '/nursing/projectplan',
    method: 'post',
    data: data
  })
}

// 修改护理计划和项目关联
export function updateProjectplan(data) {
  return request({
    url: '/nursing/projectplan',
    method: 'put',
    data: data
  })
}

// 删除护理计划和项目关联
export function delProjectplan(id) {
  return request({
    url: '/nursing/projectplan/' + id,
    method: 'delete'
  })
}
