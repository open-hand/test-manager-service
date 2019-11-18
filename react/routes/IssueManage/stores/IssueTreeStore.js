/* eslint-disable no-param-reassign */
import {
  observable, action, computed, toJS,
} from 'mobx';

class IssueTreeStore {
  @observable treeData = {
    rootIds: [],
    treeFolder: [],
  }

  @observable expandedKeys = ['0-0'];

  @observable selectedKeys = [];

  @observable currentCycle = {};

  @observable preCycle = {};

  @observable loading = false;

  treeRef = null;

  @computed get getTreeData() {
    return toJS(this.treeData);
  }

  @computed get getExpandedKeys() {
    return toJS(this.expandedKeys);
  }

  @computed get getSelectedKeys() {
    return toJS(this.selectedKeys);
  }

  @computed get getCurrentCycle() {
    return toJS(this.currentCycle);
  }

  @computed get getPreCycle() {
    return toJS(this.preCycle);
  }

  @action setExpandedKeys(expandedKeys) {
    // window.console.log(expandedKeys);
    this.expandedKeys = expandedKeys;
  }

  @action setSelectedKeys(selectedKeys) {
    this.selectedKeys = selectedKeys;
  }

  @action setTreeData(treeData) {
    const { rootIds, treeFolder } = treeData;
    this.treeData = {
      rootIds,
      treeFolder: treeFolder.map((folder) => {
        const {
          issueFolderVO, expanded, children, ...other 
        } = folder;
        return {
          children: children || [],
          data: issueFolderVO,
          isExpanded: expanded,
          ...other,
        };
      }),
    };
  }

  @action setCurrentCycle(currentCycle) {
    this.setPreCycle({ ...this.currentCycle });
    this.currentCycle = currentCycle;
  }

  @action setPreCycle(preCycle) {
    this.preCycle = preCycle;
  }

  @action setLoading = (loading) => {
    this.loading = loading;
  }

  @action setTreeRef = (treeRef) => {
    this.treeRef = treeRef;
  }
}

export default new IssueTreeStore();
