import { getProjectId, getOrganizationId, request } from '../common/utils';


/**
 *获取当前用户
 *
 * @export
 * @returns
 */
export function getSelf() {
  return request.get('/base/v1/users/self');
}
/**
 *获取指定用户
 *
 * @export
 * @param {*} userId
 * @returns
 */
export function getUser(userId) {
  return request.get(`base/v1/projects/${getProjectId()}/users?id=${userId}`);
}
/**
 *获取用户列表
 *
 * @export
 * @param {*} param
 * @returns
 */
export function getUsers(param, size, page) {
  return request.get(`/base/v1/projects/${getProjectId()}/users?size=20${page ? `&page=${page}` : ''}${param ? `&param=${param}` : ''}`);
}

export function getUpdateProjectInfoPermission() {
  return request.post('/base/v1/permissions/checkPermission', [{
    code: 'agile-service.project-info.updateProjectInfo',
    organizationId: getOrganizationId(),
    projectId: getProjectId(),
    resourceType: 'project',
  }]);
}
