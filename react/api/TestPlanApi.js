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
