import { getProjectId, request } from '../common/utils';

class TestReportApi {
  get prefix() {
    return `/test/v1/projects/${getProjectId()}`;
  }

  load(planId) {
    return request.get(`${this.prefix}/plan/${planId}/reporter/info`);
  }
}
export default new TestReportApi();
