import {
  observable, action, computed, toJS,
} from 'mobx';

import BaseTreeProto from '../../../store/BaseTreeProto';

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

    @observable tableLoading = true;

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


    @action clearStore = () => {
      this.treeData = [];  
      this.expandedKeys = ['0-0'];  
      this.selectedKeys = [];  
      this.addingParent = null;  
      this.currentCycle = {};  
      this.preCycle = {};
      this.dataList = [];
      this.executePagination = {
        current: 1,
        total: 0,
        pageSize: 50,
      };
    }
}

export default TestPlanStore;
