import { getProjectId, request } from '../common/utils';

export function getStatusList(statusType) {
  return request.post(`/test/v1/projects/${getProjectId()}/status/query`, { statusType, projectId: getProjectId() });
}
export function editStatus(data) {
  return request.put(`/test/v1/projects/${getProjectId()}/status/update`, { ...data, ...{ projectId: getProjectId() } });
}
export function createStatus(data) {
  return request.post(`/test/v1/projects/${getProjectId()}/status`, { ...data, ...{ projectId: getProjectId() } });
}
export function deleteStatus(statusId) {
  return request.delete(`/test/v1/projects/${getProjectId()}/status/${statusId}`);
}
