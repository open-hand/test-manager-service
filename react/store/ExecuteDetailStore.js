
import {
  observable, action, computed, toJS,
} from 'mobx';
import { store } from '@choerodon/master';
import _ from 'lodash';
import { getParams } from '../common/utils';
import {
  getCycle, getCycleDetails, getCycleHistiorys,
} from '../api/ExecuteDetailApi';
import { getStatusList } from '../api/TestStatusApi';
import { getUsers } from '../api/IamApi';
import { getIssueList, getIssuesForDefects } from '../api/agileApi';

@store('ExecuteDetailStore')
class ExecuteDetailStore {
  @observable id = null;

  @observable issueList = [];

  @observable loading = false;

  @observable selectLoading = false;

  @observable ExecuteDetailSideVisible = true;

  @observable userList = [];

  // 用户列表
  @observable statusList = [];

  // 状态列表
  @observable stepStatusList = [];

  @observable detailList = [];

  @observable historyList = [];

  @observable historyPagination = {
    current: 1,
    total: 0,
    pageSize: 5,
  };

  @observable cycleData = {
    caseAttachment: [], //
    defects: [], // 缺陷
  };
  // constructor() {

  // }
  @observable createBugShow = false;

  @computed get getCreateBugShow() {
    return this.createBugShow;
  }

  @action setCreateBugShow(data) {
    this.createBugShow = data;
  }

  @observable defectType = '';

  @computed get getDefectType() {
    return this.defectType;
  }

  @action setDefectType(data) {
    this.defectType = data;
  }

  @observable createDectTypeId = 0;

  @computed get getCreateDectTypeId() {
    return this.createDectTypeId;
  }

  @action setCreateDectTypeId(data) {
    this.createDectTypeId = data;
  }

  getInfo = (id = this.id) => {
    const { cycleId } = getParams(window.location.href);
    this.enterloading();
    this.setId(id);
    const { historyPagination } = this;
    Promise.all([
      getCycle(id, cycleId),
      getStatusList('CYCLE_CASE'),
      getCycleDetails(id),
      getStatusList('CASE_STEP'),
      getCycleHistiorys({
        page: historyPagination.current,
        size: historyPagination.pageSize,
      }, id),
      getIssuesForDefects(),
    ])
      .then(([cycleData, statusList, detailList, stepStatusList, historyData, issueData]) => {
        this.setCycleData(cycleData);
        this.setStatusList(statusList);
        this.setDetailList(detailList);
        this.setStepStatusList(stepStatusList);
        this.setHistoryPagination({
          current: historyPagination.current,
          pageSize: historyPagination.pageSize,
          total: historyData.total,
        });
        this.setHistoryList(historyData.list);
        this.setIssueList(issueData.list);
        this.unloading();
      }).catch((error) => {
        Choerodon.prompt('网络异常');
        this.unloading();
      });
  }

  loadHistoryList = (pagination = this.historyPagination) => {
    const { id } = this;
    this.enterloading();
    getCycleHistiorys({
      page: pagination.current,
      size: pagination.pageSize,
    }, id).then((history) => {
      this.setHistoryPagination({
        current: pagination.current,
        pageSize: pagination.pageSize,
        total: history.total,
      });
      this.setHistoryList(history.list);
      this.unloading();
    });
  }

  loadDetailList = () => {
    const { id } = this;
    this.enterloading();
    getCycleDetails(id).then((detail) => {
      this.setDetailList(detail);
      this.unloading();
    });
  }

  loadIssueList = (value) => {
    this.selectEnterLoading();
    // 加载不含测试类型的issue
    getIssuesForDefects(value).then((issueData) => {
      this.setIssueList(issueData.list);
      this.selectUnLoading();
    });
  }

  loadUserList = (value) => {
    this.selectEnterLoading();
    getUsers(value).then((userData) => {
      this.setUserList(userData.list);
      this.selectUnLoading();
    });
    getIssueList(value).then((issueData) => {
      this.setIssueList(issueData.list);
      this.selectUnLoading();
    });
  }

  @computed get getLoading() {
    return this.loading;
  }

  @computed get getHistoryPagination() {
    return toJS(this.historyPagination);
  }

  @computed get getCycleData() {
    return this.cycleData;
  }

  @computed get getHistoryList() {
    return toJS(this.historyList);
  }

  @computed get getDetailList() {
    return toJS(this.detailList);
  }

  @computed get getStatusList() {
    return toJS(this.statusList);
  }

  @computed get getStepStatusList() {
    return toJS(this.stepStatusList);
  }

  @computed get getIssueList() {
    return toJS(this.issueList);
  }

  @computed get getUserList() {
    return toJS(this.userList);
  }

  @computed get getFileList() {
    return this.cycleData.caseAttachment.map((attachment) => {
      const { url, attachmentName } = attachment;
      return {
        uid: attachment.id,
        name: attachmentName,
        status: 'done',
        url,
      };
    });
  }

  @computed get getDefectIssueIds() {
    return this.cycleData.defects.map(defect => defect.issueId.toString());
  }

  getStatusById = (status) => {
    const statusId = Number(status);
    return {
      statusName: _.find(this.statusList, { statusId })
        && _.find(this.statusList, { statusId }).statusName,
      statusColor:
        _.find(this.statusList, { statusId })
        && _.find(this.statusList, { statusId }).statusColor,
    };
  }

  @computed get getStepStatusById() {
    return this.cycleData.defects.map(defect => defect.issueId.toString());
  }

  // set
  @action setId = (id) => {
    this.id = id;
  }

  @action setCycleData = (cycleData) => {
    // window.console.log(cycleData, 'set');
    this.cycleData = cycleData;
  }

  @action removeLocalDefect = (defectId) => {
    _.remove(this.cycleData.defects, { id: defectId });
  }
  
  @action setStatusList = (statusList) => {
    this.statusList = statusList;
  }

  @action setStepStatusList = (stepStatusList) => {
    this.stepStatusList = stepStatusList;
  }

  @action setIssueList = (issueList) => {
    this.issueList = issueList;
  }

  @action setUserList = (userList) => {
    this.userList = userList;
  }

  @action clearPagination = () => {
    this.historyPagination = {
      current: 1,
      total: 0,
      pageSize: 5,
    };
  }

  @action setHistoryPagination = (historyPagination) => {
    this.historyPagination = historyPagination;
  }

  @action setHistoryList = (historyList) => {
    this.historyList = historyList;
  }

  @action setDetailList = (detailList) => {
    this.detailList = detailList;
  }

  @action selectEnterLoading = () => {
    this.selectLoading = true;
  }

  @action selectUnLoading = () => {
    this.selectLoading = false;
  }

  @action enterloading = () => {
    this.loading = true;
  }

  @action unloading = () => {
    this.loading = false;
  }

  @action setExecuteDetailSideVisible = (ExecuteDetailSideVisible) => {
    this.ExecuteDetailSideVisible = ExecuteDetailSideVisible;
  }
}

const executeDetailStore = new ExecuteDetailStore();
export default executeDetailStore;
