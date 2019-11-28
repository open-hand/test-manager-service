import {
  observable, action, computed, toJS,
} from 'mobx';

import BaseTreeProto from '../../../store/BaseTreeProto'; 
import { getExecutesByCycleId } from '../../../api/cycleApi';

class TestPlanStore extends BaseTreeProto {
    @observable loading = false;

    @action setLoading = (loading) => {
      this.loading = loading;
    }

    @computed get getLoading() {
      return this.loading;
    }

    @observable dataList = [];

    @action setDataList = (dataList) => {
      this.dataList = dataList;
    }

    @computed get getDataList() {
      return this.dataList;
    }

    @observable rightLoading = false;

    @action setRightLoading = (rightLoading) => {
      this.rightLoading = rightLoading;
    }

    @computed get getRightLoading() {
      return this.rightLoading;
    }

    @observable tableLoading = false;

    @action setTableLoading = (tableLoading) => {
      this.tableLoading = tableLoading;
    }

    @computed get getTableLoading() {
      return this.tableLoading;
    }

    @observable executePagination = {
      current: 1,
      total: 0,
      pageSize: 50,
    };
     
    @action setExecutePagination(executePagination) {
      this.executePagination = { ...this.executePagination, ...executePagination };
    }
  
    @computed get getExecutePagination() {
      return toJS(this.executePagination);
    }

    @observable statusList = [];

    @action setStatusList(statusList) {
      this.statusList = statusList;
    }

    @computed get getStatusList() {
      return this.statusList;
    }

    @observable testList = [];

    @action setTestList = (testList) => {
      this.testList = testList;
    }

    @computed get getTestList() {
      return toJS(this.testList);
    }

    @observable rightLoading = false;

    @action rightEnterLoading() {
      this.rightLoading = true;
    }
  
    @action rightLeaveLoading() {
      this.rightLoading = false;
    }

    @computed get getRightLoading() {
      return this.rightLoading;
    }

    @observable filters = {};

    @action setFilters = (filters) => {
      this.filters = filters;
    }

    @computed get getFilters() {
      return this.filters;
    }

    checkIdMap = observable.map();

    @observable testPlanStatus = 'notStart';

    @action setTestPlanStatus = (testPlanStatus) => {
      this.testPlanStatus = testPlanStatus;
    }

    @action clearStore = () => {
      this.treeData = [];  
      this.expandedKeys = ['0-0'];  
      this.selectedKeys = [];  
      this.addingParent = null;  
      this.currentCycle = {};  
      this.preCycle = {};
      this.dataList = [];
      this.rightLoading = false;
      this.filters = {};
      this.executePagination = {
        current: 1,
        total: 0,
        pageSize: 50,
      };
      this.checkIdMap = observable.map();
    }

    loadExecutes = () => {
      const data = this.getCurrentCycle;
      const { executePagination, filters } = this;
      if (data.type === 'folder') {
        this.setRightLoading(true);
        getExecutesByCycleId({
          page: executePagination.current,
          size: executePagination.pageSize,
        }, data.cycleId,
        {
          ...filters,
        }).then((cycle) => {
          this.setRightLoading(false);
          this.setTestList(cycle.list);
          this.setExecutePagination({
            current: executePagination.current,
            pageSize: executePagination.pageSize,
            total: cycle.total,
          });
        });
      }
    }
}

export default TestPlanStore;
