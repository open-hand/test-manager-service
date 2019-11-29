import { getProjectId, request } from '../common/utils';

/**
 * 根据状态获取测试计划树
 *
 * @export
 * @param {*} testPlanStatus
 * @returns
 */
export function getPlanTree(testPlanStatus) {
  return request.post(`/test/v1/project/${getProjectId()}/plan/tree?status_code=${testPlanStatus}`);
}
export function createPlan(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/plan`, data);
}
export function editPlan(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/plan`, data);
}
export function clonePlan(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/plan`, data);
}
