/* eslint-disable no-param-reassign */
import {
  observable, action, computed, toJS,
} from 'mobx';
import { find } from 'lodash';
import { getIssueTree } from '@/api/IssueManageApi';

class IssueTreeStore {
  @observable treeData = {
    rootIds: [],
    treeFolder: [],
  }

  @observable selectedKeys = [];

  @observable currentCycle = {};

  @observable loading = false;

  treeRef = null;

  @action clearStore = () => {
    this.currentCycle = {};
    this.treeData = {
      rootIds: [],
      treeFolder: [],
    };
    this.treeRef = null;
  }

  @computed get getTreeData() {
    return toJS(this.treeData);
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

  @action setSelectedKeys(selectedKeys) {
    this.selectedKeys = selectedKeys;
  }

  async loadIssueTree(defaultSelectId) {
    this.setLoading(true);
    const treeData = await getIssueTree();   
    this.setTreeData(treeData, defaultSelectId);
    this.setLoading(false);
  }

  @action setTreeData(treeData, defaultSelectId) {
    const { rootIds, treeFolder } = treeData;
    // 选中之前选中的
    let selectedId = this.currentCycle ? this.currentCycle.id : undefined;
    if (!this.currentCycle.id && rootIds.length > 0) {
      selectedId = defaultSelectId ? Number(defaultSelectId) : rootIds[0];      
    }
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
          selected: folder.id === selectedId,
          ...other,
        };
      }),
    };
    if (selectedId) {
      this.setCurrentCycle(find(this.treeData.treeFolder, { id: selectedId }) || {});
    }
  }

  @action setCurrentCycle(currentCycle) {
    this.currentCycle = currentCycle;
  }

  @action setCurrentCycleById(id) {
    const data = find(this.treeData.treeFolder, { id });
    if (data) {
      this.setCurrentCycle(data);
    }
  }

  @action setLoading = (loading) => {
    this.loading = loading;
  }

  @action setTreeRef = (treeRef) => {
    this.treeRef = treeRef;
  }
}

export default new IssueTreeStore();
