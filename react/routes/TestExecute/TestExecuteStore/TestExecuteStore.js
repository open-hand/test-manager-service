import {
  observable, action, computed, toJS,
} from 'mobx';
import BaseTreeProto from '../prototype';


class TestExecuteStore extends BaseTreeProto {
  @observable leftVisible = true;

  @observable executePagination = {
    current: 1,
    total: 0,
    pageSize: 50,
  };

  @observable treeAssignedTo = 0;

  @action clearStore = () => {
    this.treeAssignedTo = 0;
    this.leftVisible = true;    
    this.executePagination = {
      current: 1,
      total: 0,
      pageSize: 50,
    };
    this.treeData = [];  
    this.expandedKeys = ['0-0'];  
    this.selectedKeys = [];  
    this.addingParent = null;  
    this.currentCycle = {};  
    this.preCycle = {};
  }

  @action setLeftVisible(leftVisible) {
    this.leftVisible = leftVisible;
  }

  @action setExecutePagination(executePagination) {
    this.executePagination = { ...this.executePagination, ...executePagination };
  }

  @action setTreeAssignedTo(treeAssignedTo) {
    this.treeAssignedTo = treeAssignedTo;
  }


  @computed get getExecutePagination() {
    return toJS(this.executePagination);
  }
}

export default new TestExecuteStore();
