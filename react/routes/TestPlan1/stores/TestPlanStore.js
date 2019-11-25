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

    @action clearStore = () => {
      this.treeData = [];  
      this.expandedKeys = ['0-0'];  
      this.selectedKeys = [];  
      this.addingParent = null;  
      this.currentCycle = {};  
      this.preCycle = {};
      this.dataList = [];
    }
}

export default TestPlanStore;
