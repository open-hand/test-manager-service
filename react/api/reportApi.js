import { getProjectId, request } from '../common/utils';

export function getReportsFromStory(pagination, search) {
  const { size, page } = pagination;
  return request.post(`/test/v1/projects/${getProjectId()}/case/get/reporter/from/issue?size=${size}&page=${page}`, search);
}

export function getReportsFromDefect(pagination, search) {
  const { size, page } = pagination;
  return request.post(`/test/v1/projects/${getProjectId()}/case/get/reporter/from/defect?size=${size}&page=${page}`, search);
}
export function getReportsFromDefectByIssueIds(issueIds) {
  return request.post(`/test/v1/projects/${getProjectId()}/case/get/reporter/from/defect/by/issueId`, issueIds);
}
