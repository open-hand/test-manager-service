/* eslint-disable no-param-reassign */
import { Choerodon } from '@choerodon/boot';
import {
  observable, action, computed, toJS,
} from 'mobx';
import {
  loadDatalogs, loadLinkIssues, loadIssue, getIssueSteps, 
} from '@/api/IssueManageApi';

class EditIssueStore {
  @observable loading = false;

  @observable issueInfo = {};

  @observable linkIssues = [];

  @observable dataLogs = [];

  @observable issueSteps = [];

  @action
  setIssueInfo(issueInfo) {
    this.issueInfo = issueInfo;
  }

  @action
  setIssueSteps(issueSteps) {
    this.issueSteps = issueSteps;
  }

  @action
  setLoading(loading) {
    this.loading = loading;
  }

  @action
  setData(data) {
    const [issueInfo, linkIssues, dataLogs, issueSteps] = data;
    this.issueInfo = issueInfo;
    this.linkIssues = linkIssues;
    this.dataLogs = dataLogs;
    this.issueSteps = issueSteps;
  }

  loadIssueData = async (caseId = this.issueInfo.caseId) => {
    this.setLoading(true);
    const data = await Promise.all([
      loadIssue(caseId),
      loadLinkIssues(caseId),
      loadDatalogs(caseId),
      getIssueSteps(caseId),
    ]);
    this.setData(data);
    this.setLoading(false);
  }
  

  async loadWithLoading(promise, callback) {
    this.setLoading(true);
    try {
      const result = await promise;
      if (callback && typeof callback === 'function') {
        callback();
      }
      this.setLoading(false);
      return result;
    } catch (error) {
      Choerodon.prompt(error.message, 'error');
      this.setLoading(false);
    }
    return null;
  }
}

export default EditIssueStore;
