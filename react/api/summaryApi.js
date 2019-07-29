import { getProjectId, request } from '../common/utils';

export function getCaseNotPlain() {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/case/countCaseNotPlain`);
}

export function getCaseNotRun() {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/case/countCaseNotRun`);
}
export function getCaseNum() {
  return request.get(`/test/v1/projects/${getProjectId()}/cycle/case/countCaseSum`);
}

export function getCycleRange(day, range) {
  return request.post(`/test/v1/projects/${getProjectId()}/cycle/case/range/${day}/${range}`);
}
export function getCreateRange(range) {
  return request.get(`/agile/v1/projects/${getProjectId()}/issues/type/issue_test?timeSlot=${range}`);
}
export function getIssueStatistic(type) {
  return request.post(`/agile/v1/projects/${getProjectId()}/issues/test_component/statistic?type=${type}`,
    ['sub_task', 'story', 'task', 'issue_epic', 'bug']);
}
