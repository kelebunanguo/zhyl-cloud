import request from '@/utils/request';

// 查询护理任务列表
export function listNursingTask(query) {
  return request({
    url: '/nursing/task/list',
    method: 'get',
    params: query,
  });
}


// 查询护理任务详细信息
export function getNursingTask(id) {
  return request({
    url: '/nursing/task/'+id,
    method: 'get'
  });
}

// 取消护理任务
export function cancelPlan(params) {
  return request({
    url: `/nursing/task/cancel`,
    method: 'put',
    data:params
  });
}

// 执行任务
export function executePlan(params) {
  return request({
    url: `/nursing/task/do`,
    method: 'put',
    data:params
  });
}

// 修改护理任务的执行日期
export function changePlanTime(params) {
  return request({
    url: `/nursing/task/updateTime`,
    method: 'put',
    data:params
  });
}