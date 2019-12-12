import { getProjectId, request } from '../common/utils';
/**
 *获取
 *
 * @export
 * @param {*} summary
 * @returns
 */
export function getIssues(search, { page = 1, size = 20 } = {}) {
  return request.post(`agile/v1/projects/${getProjectId()}/issues/test_component/no_sub?page=${page}&size=${size}`, search);
}
/**
 *获取当前项目的issue类型列表
 *
 * @export
 * @returns
 */
export function getIssueTypes(applyType) {
  return request.get(`/agile/v1/projects/${getProjectId()}/schemes/query_issue_types_with_sm_id?apply_type=${applyType || 'test'}`);
}
/**
 *获取缺陷列表（排除test类型）
 *
 * @export
 * @param {*} summary
 * @returns
 */
export function getIssuesForDefects(summary, { page = 1, size = 20 } = {}) {
  return new Promise(((resolve) => {
    getIssueTypes('agile').then((types) => {
      const advancedSearchArgs = {
        issueTypeId: types.filter(type => type.typeCode === 'bug').map(type => type.id),
      };
      const searchArgs = {};
      if (summary) {
        searchArgs.summary = summary;
      }
      resolve(getIssues({ advancedSearchArgs, searchArgs }, { page, size }));
    });
  }));
}
