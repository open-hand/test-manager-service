import { getProjectId, request, getOrganizationId } from '../common/utils';

/**
 *获取当前项目的所有版本
 *
 * @export
 * @returns
 */
export function getProjectVersion() {
  return request.get(`agile/v1/projects/${getProjectId()}/product_version/versions`);
}
/**
 * 根据冲刺状态获取冲刺，["started", "sprint_planning", "closed"]
 * @param {*} arr
 */
export function getSprints(arr = []) {
  return request.post(`/agile/v1/projects/${getProjectId()}/sprint/names`, arr);
}
/**
 *获取当前项目的指定类型的版本
 *
 * @export
 * @returns
 */
export function getProjectVersionByStatus(statusList = ['version_planning', 'released']) {
  return request.post(`agile/v1/projects/${getProjectId()}/product_version/names`, statusList);
}
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
        issueTypeId: types.filter((type) => type.typeCode === 'bug').map((type) => type.id),
      };
      const searchArgs = {};
      if (summary) {
        searchArgs.summary = summary;
      }
      resolve(getIssues({ advancedSearchArgs, searchArgs }, { page, size }));
    });
  }));
}
/**
 *获取根据筛选条件获取issues
 *
 * @export
 * @param {*} summary
 * @param {*} type
 * @returns
 */
export function getIssueList(summary, type) {
  const advancedSearchArgs = {};
  const searchArgs = {};
  if (type) {
    // advancedSearchArgs.typeCode = ['issue_test'];
  }
  if (summary) {
    searchArgs.summary = summary;
  }
  return getIssues({ advancedSearchArgs, searchArgs });
}
/**
 *获取测试类型issue数量
 *
 * @export
 * @param {*} search
 * @returns
 */
export function getIssueCount(search) {
  return new Promise(((resolve) => {
    getIssueTypes('test').then((types) => {
      const advancedSearchArgs = {
        issueTypeId: types.map((type) => type.id),
      };
      const searchArgs = {};
      resolve(getIssues({ advancedSearchArgs, searchArgs }));
    });
  }));
}
/**
 *获取当前项目的模块
 *
 * @export
 * @returns
 */
export function getModules() {
  return request.get(`agile/v1/projects/${getProjectId()}/component`);
}

/**
 *获取当前组织的issue优先级
 *
 * @export
 * @returns
 */
export function getPrioritys() {
  return request.get(`/agile/v1/projects/${getProjectId()}/priority/list_by_org`);
}
/**
 *获取当前项目的issue状态列表
 *
 * @export
 * @returns
 */
export function getIssueStatus(applyType) {
  return request.get(`/agile/v1/projects/${getProjectId()}/schemes/query_status_by_project_id?apply_type=${applyType || 'test'}`);
}

/**
 *获取当前项目的史诗列表
 *
 * @export
 * @returns
 */
export function getEpics() {
  return request.get(`/agile/v1/projects/${getProjectId()}/issues/epics?apply_type=agile`);
}

/**
 *获取当前项目的未关闭的冲刺列表
 *
 * @export
 * @returns
 */
export function getSprintsUnClosed() {
  return request.get(`/agile/v1/projects/${getProjectId()}/sprint/unclosed`);
}

export function createIssue(issueObj, projectId = getProjectId()) {
  const issue = {
    projectId,
    ...issueObj,
  };
  return request.post(`/agile/v1/projects/${projectId}/issues?applyType=agile`, issue);
}
/**
 * 新增Issue字段值
 * @returns {V|*}
 */
export function createFieldValue(id, code, dto) {
  return request.post(`/foundation/v1/projects/${getProjectId()}/field_value/${id}?organizationId=${getOrganizationId()}&schemeCode=${code}`, dto);
}
/**
 * 加载字段配置
 * @returns {V|*}
 */
export function getFields(dto) {
  return request.post(`/agile/v1/projects/${getProjectId()}/field_value/list`, dto);
}
