
import { getProjectId, getOrganizationId, request } from '../common/utils';


export function getTestHistoryByApp(appId, pagination, filter) {
  const { current, pageSize } = pagination;
  const search = { appId, ...filter };
  if (filter.version) {
    search.filter = {
      searchParam: {
        version: filter.version,
      },
    };
  }
  return request.post(`/test/v1/projects/${getProjectId()}/test/automation/queryWithHistroy?page=${current}&size=${pageSize}`, search);
}
export function getYaml(appId, appVersionId, envId) {
  // return axios.get('/getYaml');
  return request.get(`/test/v1/projects/${getProjectId()}/app_service_instances/value?appId=${appId}&envId=${envId}&versionId=${appVersionId}`);
}
export function checkYaml(value) {
  return request.post(`/devops/v1/projects/${getProjectId()}/app_service_instances/value_format`, { yaml: value });
}

export function loadPodParam(id, type) {
  return request.get(`devops/v1/projects/${getProjectId()}/app_pod/${id}/containers/logs`);
}
export function getApps({
  page, size, sort, postData,
}) {
  return request.post(`/devops/v1/projects/${getProjectId()}/app_service/page_by_options?type=test&active=true&page=${page}&size=${size}&sort=${sort.field},${sort.order}`, JSON.stringify(postData));
}
export function getAppVersions(appId, pagination, filter) {
  const { page, size } = pagination;
  return request.post(`/devops/v1/projects/${getProjectId()}/app_service_versions/page_by_options?app_service_id=${appId}&page=${page}&size=${size}&sort=id,desc`, { searchParam: filter , params:[]});
}
export function getEnvs() {
  return request.post(
    `/devops/v1/organizations/${getOrganizationId()}/clusters/page_cluster?page=0&size=12&sort=id,desc`,
    {
      param: '',
      searchParam: {},
    },
  );
}
export function getAllEnvs() {
  return request.get(`/devops/v1/projects/${getProjectId()}/envs/list_clusters`);
}
export function runTestInstant(scheduleTaskDTO) {
  return request.post(`/test/v1/projects/${getProjectId()}/app_service_instances`, scheduleTaskDTO);
}
export function reRunTest(scheduleTaskDTO) {
  return request.post(`/test/v1/projects/${getProjectId()}/app_service_instances`, scheduleTaskDTO);
}
export function getLog(logId) {
  return request.get(`/test/v1/projects/${getProjectId()}/test/automation/queryLog/${logId}`);
}
export function runTestTiming(scheduleTaskDTO) {
  return request.post(`/test/v1/projects/${getProjectId()}/app_service_instances/schedule`, scheduleTaskDTO);
}
export function getTestReport(id) {
  return request.get(`/test/v1/projects/${getProjectId()}/automation/result/query/${id}`);
}
