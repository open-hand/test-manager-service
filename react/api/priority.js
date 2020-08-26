import { getProjectId, request, getOrganizationId } from '../common/utils';

class PriorityApi {
  get getPrefix() {
    return `/test/v1/organizations/${getOrganizationId()}`;
  }

  create(data) {
    request.post(`${this.getPrefix}/test_priority`, data);
  }

  update(data) {
    request.put(`${this.getPrefix}/test_priority`, data);
  }

  delete(data) {
    request.delete(`${this.getPrefix}/test_priority`, data);
  }

  /**
   * 删除前检查优先级
   * @param priorityId 
   */
  checkBeforeDel(priorityId) {
    return request.get(`${this.getPrefix}/priority/check_delete/${priorityId}`);
  }

  /**
     * 检查优先级名称是否重复
     * @param name 
     */
  checkName(name) {
    return request.get(`${this.getPrefix}/priority/check_name`, name);
  }
  
  /**
   * 优先级排序
   * @param sequences 
   */
  sort(sequences) {
    return request.put(`${this.getPrefix}/priority/sequence`, sequences);
  }
}
export default new PriorityApi();
