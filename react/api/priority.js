import { getProjectId, request, getOrganizationId } from '../common/utils';

class PriorityApi {
  get getPrefix() {
    return `/test/v1/organizations/${getOrganizationId()}`;
  }

  load() {
    return request.get(`${this.getPrefix}/test_priority`);
  }

  create(data) {
    return request.post(`${this.getPrefix}/test_priority`, { ...data, organizationId: getOrganizationId() });
  }

  update(data) {
    return request.put(`${this.getPrefix}/test_priority`, { ...data, organizationId: getOrganizationId() });
  }

  updateStatus(priorityId, enable) {
    return request.post(`${this.getPrefix}/test_priority/${priorityId}/${enable ? 'enabled' : 'disabled'}`);
  }

  delete(data) {
    return request.delete(`${this.getPrefix}/test_priority`, data);
  }

  /**
   * 删除前检查优先级
   * @param priorityId 
   */
  checkBeforeDel(priorityId) {
    return request.get(`${this.getPrefix}/test_priority/check_delete/${priorityId}`);
  }

  /**
     * 检查优先级名称是否重复
     * @param name 
     */
  checkName(name) {
    return request.get(`${this.getPrefix}/test_priority/check_name?name=${name}`);
  }

  /**
   * 优先级排序
   * @param sequences 
   */
  sort(sequences) {
    return request.put(`${this.getPrefix}/test_priority/sequence`, sequences);
  }
}
export default new PriorityApi();
