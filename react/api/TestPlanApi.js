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
export function getPlanTreeById(planId) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/tree?plan_id=${planId}`);
}
export function createPlan(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/plan`, data);
}
export function clonePlan(planId) {
  return request.post(`/test/v1/projects/${getProjectId()}/plan/${planId}/clone`);
}
export function getPlan(planId) {
  return request.get(`/test/v1/projects/${getProjectId()}/plan/${planId}/query`);
}
export function editPlan(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/plan`, data);
}

export function deletePlan(planId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/plan/${planId}/delete`);
}
export function addFolder(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle`, data);
}
export function editFolder(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/cycle`, data);
}
export function moveFolder(folderId, targetFolderId, lastRank, nextRank) {
  return request.put(`/test/v1/projects/${getProjectId()}/cycle/move/${folderId}?target_cycle_id=${targetFolderId}&lastRank=${lastRank}&nextRank=${nextRank}`);
}
export function deleteFolder(folderId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/cycle/delete/${folderId}`);
}
export function importIssueToFolder(planId, folderId, data) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/case/import?plan_id=${planId}&cycle_id=${folderId}`, data);
}
/**
 *  根据文件夹id和计划id获取执行
 */
export function getExecutesByFolder({ 
  planId, folderId, search, orderField, orderType, current, pageSize, 
}) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/case/query/caseList?cycle_id=${planId !== folderId ? folderId : ''}&plan_id=${planId}&page=${current}&size=${pageSize}`, search, {
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
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/case/query/status?cycle_id=${planId !== folderId ? folderId : ''}&plan_id=${planId}`);
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

/**
 * 删除状态
 *
 * @export
 * @param {*} executeId
 * @returns
 */
export function deleteExecute(executeId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/cycle/case?cycleCaseId=${executeId}`);
}

export function updatePlanStatus(updateData) {
  return request.post(`/test/v1/projects/${getProjectId()}/plan/update_status`, updateData);
}

/**
 * 快速通过或快速失败或者给执行排序
 *
 * @export
 * @param {*} data
 * @returns
 */
export function updateExecute(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/cycle/case/cycle_case`, data);
}

/**
 * 
 *
 * @export
 * @param {*} executeId
 * @returns
 */
export function getUpdateCompared(executeId) {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/case/${executeId}/compared`);
}

/**
 * 确认更新，在变更提醒弹框点击确认更新
 *
 * @export
 * @param {*} data
 * @returns
 */
export function comfirmUpdate(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/case/compared`, data);
}

export function ignoreUpdate(executed) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/case/${executed}/ignore/update`, {});
}

export function getPlanList() {
  return request.get(`/test/v1/projects/${getProjectId()}/plan/project_plan`);
}
