import { useLocalStore } from 'mobx-react-lite';
import { axios, Choerodon } from '@choerodon/boot';
import queryString from 'query-string';
import { getProjectId, getOrganizationId } from '@/common/utils';
import to from '@choerodon/agile/lib/utils/to';

function TestLinkStore(issueId) {
  return useLocalStore(() => ({
    data: [],
    issueId,
    loadData() {
      return axios.get(`test/v1/projects/${getProjectId()}/case_link/list_link_case_info?issue_id=${issueId}`).then((res) => {
        this.data = res;
      });
    },
    loadCaseList({ page, filter }) {
      const queryStr = queryString({ page, size: 20, content: filter });
      return axios.get(`/test/v1/projects/${getProjectId()}/case/case/summary?${queryStr}`);
    },
    delete(linkId) {
      const queryStr = queryString({ linkId, organizationId: getOrganizationId() });
      return axios.delete(`test/v1/projects/${getProjectId()}/case_link?${queryStr}`).then(() => this.loadData());
    },
    createLink(caseIds = []) {
      return axios({
        url: `test/v1/projects/${getProjectId()}/case_link/create_by_issue`,
        method: 'post',
        params: {
          issue_id: this.issueId,
        },
        data: caseIds,
      }).then(() => {
        this.loadData();
      }).catch(() => Choerodon.prompt('关联错误，请重试', 'error'));
    },
    toLink(linkId, name, folderId) {
      to('/testManager/IssueManage', {
        type: 'project',
        params: {
          paramIssueId: linkId,
          paramName: name,
          folderId,
        },
      });
    },
  }));
}
export default TestLinkStore;
