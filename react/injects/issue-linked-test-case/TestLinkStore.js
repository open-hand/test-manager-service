import { useLocalStore } from 'mobx-react-lite';
import { axios, stores, Choerodon } from '@choerodon/boot';
import queryString from 'query-string';
import { useHistory } from 'react-router-dom';
import to from '@choerodon/agile/lib/utils/to';
import { getProjectId, getOrganizationId } from '@/common/utils';

const { AppState } = stores;

function TestLinkStore(projectId, issueId) {
  const history = useHistory();
  return useLocalStore(() => ({
    data: [],
    issueId,
    loadData() {
      return axios.get(`test/v1/projects/${projectId || getProjectId()}/case_link/list_link_case_info?issue_id=${issueId}`).then((res) => {
        this.data = res;
      });
    },
    loadCaseList({ page, filter }) {
      const queryStr = queryString.stringify({
        page, size: 20, content: filter, issueId,
      });
      return axios.get(`/test/v1/projects/${projectId || getProjectId()}/case/case/summary?${queryStr}`);
    },
    delete(linkId) {
      const queryStr = queryString.stringify({ linkId, organizationId: getOrganizationId() });
      return axios.delete(`test/v1/projects/${projectId || getProjectId()}/case_link?${queryStr}`).then(() => this.loadData());
    },
    createLink(caseIds = []) {
      return axios({
        url: `test/v1/projects/${projectId || getProjectId()}/case_link/create_by_issue`,
        method: 'post',
        params: {
          issue_id: this.issueId,
        },
        data: caseIds,
      }).then(() => {
        this.loadData();
      }).catch(() => Choerodon.prompt('关联错误，请重试', 'error'));
    },
    createCaseAndLink(data) {
      return axios({
        url: `test/v1/projects/${projectId || getProjectId()}/case_link/create_and_link`,
        params: {
          issue_id: this.issueId,
        },
        method: 'post',
        data,
      }).then((res) => {
        const { failed } = res || {};
        if (failed) {
          throw new Error();
        }
        this.loadData();
        return Array.isArray(res) ? { caseId: res[0].linkCaseId } : {};
      }).catch(() => Choerodon.prompt('关联错误，请重试', 'error'));
    },
    toLink(linkId, paramName, folderId) {
      to('/testManager/IssueManage', {
        type: 'project',
        id: projectId,
        params: {
          paramIssueId: linkId,
          paramName,
          folderId,
        },
      });
    },
  }));
}
export default TestLinkStore;
