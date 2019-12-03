import { getProjectId, request } from '../common/utils';

/**
 * 根据状态获取测试计划树
 *
 * @export
 * @param {*} testPlanStatus
 * @returns
 */
export function getPlanTree(testPlanStatus) {
  return request.get(`/test/v1/projects/${getProjectId()}/plan/tree?status_code=${testPlanStatus}`);
}
export function createPlan(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/plan`, data);
}
export function getPlan(planId) {
  return request.get(`/test/v1/projects/${getProjectId()}/plan/${planId}/query`);
}
export function editPlan(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/plan`, data);
}
/**
 *  根据文件夹id和计划id获取执行
 */
export function getExecutesByFolder({ 
  planId, folderId, filter, orderField, orderType, current, pageSize, 
}) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/case/query/caseList?folder_id=${planId !== folderId ? folderId : ''}&plan_id=${planId}&page=${current}&size=${pageSize}`, filter, {
    params: {
      sort: `${orderField && orderType ? `${orderField},${orderType}` : ''}`,
    },
  });
}

/**
 * 通过测试计划获取统计状态
 *
 * @export
 * @param {*} planId
 * @returns
 */
export function getStatusByFolder({ planId, folderId }) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/case/query/status?folder_id=${planId !== folderId ? folderId : ''}&plan_id=${planId}`);
}

/**
 * 获取计划详情
 *
 * @export
 * @param {*} planId
 * @returns
 */
export function getPlanDetail(planId) {
  return request.get(`/test/v1/projects/${getProjectId()}/plan/${planId}/info`);
}


/**
 * 批量给执行指定执行人
 *
 * @export
 * @param {*} executeIds
 * @param {*} assignUserId
 * @returns
 */
export function executesAssignTo(executeIds, assignUserId) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/case/batchAssign/cycleCase?assign_user_id=${assignUserId}`, executeIds);
}

export function deleteExecute(executeId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/cycle/case?cycleCaseId=${executeId}`);
}
