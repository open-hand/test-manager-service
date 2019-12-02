
import {
  observable, action, computed, toJS,
} from 'mobx';
import { Choerodon } from '@choerodon/boot';
import _ from 'lodash';
import { getParams } from '../../../common/utils';
import {
  getCycle, geDetailsData, getCycleHistiorys,
} from '../../../api/ExecuteDetailApi';
import { getStatusList } from '../../../api/TestStatusApi';
import { getUsers } from '../../../api/IamApi';
import { getIssueList, getIssuesForDefects } from '../../../api/agileApi';

class ExecuteDetailStore {
  @observable id = null;

  @observable loading = false;

  @observable selectLoading = false;

  @observable ExecuteDetailSideVisible = true;

  @observable statusList = [];

  // 状态列表

  @observable detailData = false;

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
    this.enterloading();
    this.setId(id);
    Promise.all([
      // getCycle(id, cycleId),
      getStatusList('CYCLE_CASE'),
      geDetailsData(id),
      getIssuesForDefects(),
    ])
      .then(([statusList, detailData, issueData]) => {
        this.setDetailData(detailData);
        this.setStatusList(statusList);

        this.unloading();
      }).catch((error) => {
        Choerodon.prompt('网络异常');
        this.unloading();
      });
  }

  loadDetailData(id = this.id) {
    geDetailsData(id).then((res) => {
      this.setDetailData(res);
    });
  }


  @computed get getLoading() {
    return this.loading;
  }


  @computed get getDetailData() {
    return toJS(this.detailData);
  }

  @computed get getStatusList() {
    return toJS(this.statusList);
  }


  @computed get getUserList() {
    return toJS(this.userList);
  }


  @computed get getDefectIssueIds() {
    return [];
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

  @action removeLocalDefect = (defectId) => {
    _.remove(this.cycleData.defects, { id: defectId });
  }

  @action setStatusList = (statusList) => {
    this.statusList = statusList;
  }


  @action setDetailData = (data) => {
    this.detailData = data;
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

export default ExecuteDetailStore;
