import Axios from 'axios';
import queryString from 'query-string';
import { getProjectId, request } from '../common/utils';

/**
 * 更新执行用例详情（用于状态更改）
 * @param {*} data 
 */
export function updateDetail(data) {
  return request.put(`test/v1/projects/${getProjectId()}/cycle/case/cycle_case`, data);
}
/**
 * 更新执行用例详情（用于弹窗修改内容）
 * @param {*} data 
 */
export function updateSidebarDetail(data) {
  const { isAsync = false } = data;
  return request.put(`test/v1/projects/${getProjectId()}/cycle/case/case_step?isAsync=${isAsync}`, data);
}

/**
 * 获取问题链接信息
 * @param {*} data 
 */
export function getIssueInfos(caseId) {
  return request.get(`test/v1/projects/${getProjectId()}/case_link/list_issue_info?case_id=${caseId}`);
}
/**
 *增加缺陷
 *
 * @export
 * @param {*} defects
 * @returns
 */
export function addDefects(defects) {
  return request.post(`/test/v1/projects/${getProjectId()}/defect`, defects);
}
/**
 *移除缺陷
 *
 * @export
 * @param {*} defectId
 * @returns
 */
export function removeDefect(defectId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/defect/delete/${defectId}`);
}
export function editCycleSide(data) {
  const axiosConfig = {
    headers: { 'content-type': 'multipart/form-datal' },
  };

  return request.post(`/zuul/test/v1/projects/${getProjectId()}/cycle/case/step/updateWithAttach`, data, axiosConfig);
}
export function editCycleStep(data) {
  // /v1/projects/28/cycle/case/case_step/25 /v1/projects/28/cycle/case/step
  return Axios.put(`/test/v1/projects/${getProjectId()}/cycle/case/step`, data);
}
export function geDetailsData(cycleCaseId, param) {
  return request.post(`test/v1/projects/${getProjectId()}/cycle/case/${cycleCaseId}/info?${queryString.stringify(param)}`,
    {
      searchArgs: {
        assignUser: param.assignerId,
      },
      contents: [],
    });
}
export function getCycleHistiorys(pagination, cycleCaseId) {
  const { size, page } = pagination;

  return request.get(`test/v1/projects/${getProjectId()}/cycle/case/history/${cycleCaseId}?size=${size}&page=${page}`);
}

/**
 * 在执行详情中为执行或步骤增加缺陷
 * 
 */
export function addBugForExecuteOrStep(defectType, id, data) {
  return request.post(`test/v1/projects/${getProjectId()}/defect/createIssueAndDefect/${defectType}/${id}?applyType=agile`, data);
}
export function getIssueLinkTypes() {
  return request.post(`/agile/v1/projects/${getProjectId()}/issue_link_types/query_all`, {
    contents: [],
    linkName: '',
  });
}
