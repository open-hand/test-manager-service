
import {
  observable, action, computed, toJS,
} from 'mobx';
import { Choerodon } from '@choerodon/boot';
import _ from 'lodash';
import { geDetailsData } from '@/api/ExecuteDetailApi';
import { getStatusList } from '@/api/TestStatusApi';

class ExecuteDetailStore {
  @observable id = null;

  @observable loading = false;

  @observable selectLoading = false;

  @observable ExecuteDetailSideVisible = false;

  @observable statusList = [];

  @observable detailParams = {};// 查询接口所需参数
  // 状态列表

  @observable detailData = false;

  @observable createBugShow = false;

  @computed get getDetailParams() {
    return this.detailParams;
  }


  @action setDetailParams(data) {
    this.detailParams = {
      cycle_id: data.cycle_id,
      page: data.page,
      plan_id: data.plan_id,
      size: data.size,
    };
  }


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
      geDetailsData(id, this.detailParams),
    ])
      .then(([statusList, detailData]) => {
        // console.log('statusList', statusList);
        const { failed } = detailData;
        if (!failed) {
          this.setDetailData(detailData);
        } else {
          throw new Error(detailData.message);
        }
        this.setStatusList(statusList);

        this.unloading();
      }).catch((error) => {
        Choerodon.prompt(`${error || '网络异常'}`);
        this.unloading();
      });
  }

  loadDetailData(id = this.id) {
    geDetailsData(id, this.detailParams).then((res) => {
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
